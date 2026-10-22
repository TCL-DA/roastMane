package org.cropster.roastingintelligence;

import com.cropster.roastingintelligence.Data;
import com.cropster.roastingintelligence.util.RenamingThreadFactory;
import com.cropster.util.Collect;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.cropster.roastingintelligence.listener.MessageListener;
import org.cropster.roastingintelligence.model.BlendProfile;
import org.cropster.roastingintelligence.model.Lot;
import org.cropster.roastingintelligence.model.LotConsistencyCheck;
import org.cropster.roastingintelligence.model.Machine;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.model.WorkerTask;
import org.cropster.roastingintelligence.remote.CsarConnection;
import org.cropster.roastingintelligence.tasks.AlarmUploader;
import org.cropster.roastingintelligence.tasks.DataLoader;
import org.cropster.roastingintelligence.tasks.GroupMemberLoader;
import org.cropster.roastingintelligence.tasks.LiveGoalUploader;
import org.cropster.roastingintelligence.tasks.MachineLoader;
import org.cropster.roastingintelligence.util.TaskCompletionLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyncService {
   private final Logger logger = LoggerFactory.getLogger(SyncService.class);
   private final CsarConnection _connection;
   private final AppModel _appModel;
   private final ExecutorService _executorService;
   private final CompletionService<?> _completionService;
   private final Map<Class<?>, Future<?>> _executingTasks;
   private final CopyOnWriteArraySet<MessageListener> _messageListeners;

   public SyncService(CsarConnection connection, AppModel appModel) {
      this._connection = connection;
      this._appModel = appModel;
      this._executorService = Executors.newFixedThreadPool(1, new RenamingThreadFactory(this.getClass().getSimpleName()));
      this._completionService = new ExecutorCompletionService(this._executorService);
      this._executingTasks = new ConcurrentHashMap<>();
      new TaskCompletionLogger(this._completionService).start();
      this._messageListeners = new CopyOnWriteArraySet<>();
   }

   public CsarConnection getConnection() {
      return this._connection;
   }

   public void addMessageListener(MessageListener listener) {
      this._messageListeners.add(listener);
   }

   public void removeMessageListener(MessageListener listener) {
      this._messageListeners.remove(listener);
   }

   public synchronized Future<?> execute(Runnable task) {
      Future<?> future = this._executingTasks.get(task.getClass());
      if (future == null || future.isDone()) {
         future = this._completionService.submit(task, null);
         this._executingTasks.put(task.getClass(), future);
      } else if (future != null) {
         this.logger.warn("Ignoring execution request for {}", task.getClass().getSimpleName());
      }

      return future;
   }

   public void loadMachines() throws Exception {
      MachineLoader machineLoader = new MachineLoader(this._connection, this._appModel);
      machineLoader.listeners().addAll(this._messageListeners);
      this.execute(machineLoader);
   }

   public void loadChecks(Roast roast) {
      Machine machine = this._appModel.getData().getMachines().get(roast.getMachine());
      Collection<LotConsistencyCheck> checks = this._connection.getLotConsistencyChecks(machine.getGroup().getCode(), Arrays.asList(roast.getLot().getId()));
      if (checks != null) {
         Map<Long, LotConsistencyCheck> lotsChecks = new HashMap<>(this._appModel.getData().getChecks());
         lotsChecks.put(roast.getLot().getId(), (LotConsistencyCheck)Collect.first(checks));
         this._appModel.getData().setChecks(lotsChecks);
      }
   }

   public CompletableFuture<Collection<Lot>> loadBlendsAsync(String groupCode, String user) {
      return CompletableFuture.supplyAsync(() -> this.loadBlends(groupCode, user), this._executorService);
   }

   private Collection<Lot> loadBlends(String groupCode, String user) {
      return this._connection.getBlends(groupCode, user);
   }

   private Collection<BlendProfile> loadBlendProfiles(String groupCode) {
      return this._connection.getBlendProfiles(groupCode);
   }

   public void syncDataNonBlocking() {
      if (this._appModel.getSelectedMachine() != null) {
         DataLoader dataLoader = new DataLoader(this._connection, this._appModel.getSelectedMachine().getId(), this._appModel);
         dataLoader.listeners().addAll(this._messageListeners);
         this.execute(dataLoader);
      } else {
         this._appModel.setData(new Data(this._appModel.getData()));
         this._appModel.fireCsarDataReceived();
      }
   }

   public void syncDataBlocking() throws Exception {
      if (this._appModel.getSelectedMachine() != null) {
         DataLoader dataLoader = new DataLoader(this._connection, this._appModel.getSelectedMachine().getId(), this._appModel);
         dataLoader.listeners().addAll(this._messageListeners);
         Future<?> future = this.execute(dataLoader);
         future.get();
      } else {
         this._appModel.setData(new Data(this._appModel.getData()));
         this._appModel.fireCsarDataReceived();
      }
   }

   public void syncGroupMembers() {
      GroupMemberLoader loader = new GroupMemberLoader(this._appModel, this._connection);
      loader.listeners().addAll(this._messageListeners);
      this.execute(loader);
   }

   private Collection<WorkerTask> syncTasks(Long machineId, WorkerTask... tasks) {
      for (WorkerTask task : tasks) {
         if (!task.isSynced()) {
            this._connection.update(task);
         }
      }

      return this._connection.getTasks(machineId);
   }

   public CompletableFuture<Collection<WorkerTask>> syncTasksAsync(Long machineId, WorkerTask... tasks) {
      return CompletableFuture.supplyAsync(() -> this.syncTasks(machineId, tasks), this._executorService);
   }

   public void uploadAlarms() {
      AlarmUploader alarmUploader = new AlarmUploader(this._appModel, this._connection);
      alarmUploader.listeners().addAll(this._messageListeners);
      this.execute(alarmUploader);
   }

   public void uploadRoasts() {
   }

   public void uploadRoastsBlocking() throws Exception {
   }

   public void uploadLiveGoals() {
      LiveGoalUploader liveGoalUploader = new LiveGoalUploader(this._appModel, this._connection);
      liveGoalUploader.listeners().addAll(this._messageListeners);
      this.execute(liveGoalUploader);
   }

   public void shutdown() {
      try {
         this.logger.info("Shutting down ExecutorService");
         this._executorService.shutdownNow();
         this._executorService.awaitTermination(30L, TimeUnit.SECONDS);
      } catch (InterruptedException var2) {
         this.logger.warn("Error shutting down ExecutorService", var2);
      }
   }
}
