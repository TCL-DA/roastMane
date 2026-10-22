package local;

import com.cropster.roastingintelligence.Data;
import com.cropster.roastingintelligence.exception.BetaAccessDeniedException;
import com.cropster.roastingintelligence.remote.Authenticator;
import com.cropster.util.model.Unit;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.type.CollectionType;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SortedMap;
import org.cropster.roastingintelligence.model.Analysis;
import org.cropster.roastingintelligence.model.BetweenBatchLog;
import org.cropster.roastingintelligence.model.BlendProfile;
import org.cropster.roastingintelligence.model.Goal;
import org.cropster.roastingintelligence.model.Group;
import org.cropster.roastingintelligence.model.GroupMember;
import org.cropster.roastingintelligence.model.Lot;
import org.cropster.roastingintelligence.model.LotConsistencyCheck;
import org.cropster.roastingintelligence.model.LotTransaction;
import org.cropster.roastingintelligence.model.Machine;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.Profile;
import org.cropster.roastingintelligence.model.Reference;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.model.Scheduled;
import org.cropster.roastingintelligence.model.UnsavedBetweenBatchLog;
import org.cropster.roastingintelligence.model.User;
import org.cropster.roastingintelligence.model.WorkerTask;
import org.cropster.roastingintelligence.remote.CsarConnectionImpl;
import org.cropster.roastingintelligence.remote.Version;
import org.cropster.roastingintelligence.service.impl.JsonReader;
import org.cropster.roastingintelligence.service.impl.JsonWriter;

public final class OfflineConnection extends CsarConnectionImpl {
   private static final Object FILE_LOCK = new Object();
   private static final String OFFLINE_USER = "offline";
   private static final Collection<String> OFFLINE_MODULES = Collections.singletonList("roast.roastingintelligence");
   private static final Collection<String> OFFLINE_PERMISSIONS = Collections.singletonList("roastRoastingIntelligence");
   private final JsonReader jsonReader = new JsonReader();
   private final JsonWriter jsonWriter;
   private final ObjectMapper objectMapper;

   public OfflineConnection(Version var1, URI var2) {
      super(var1, var2);
      this.jsonWriter = new JsonWriter(var1);
      this.objectMapper = new ObjectMapper();
      this.objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
      this.objectMapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));
   }

   public void init(Authenticator var1) {
   }

   public void open() {
      this.ensureRoot();
   }

   public void invalidate() {
   }

   public void log() throws BetaAccessDeniedException {
      this.ensureRoot();
   }

   public URL createSupportTicket(String var1, String var2, File var3) {
      throw this.unsupported("support tickets");
   }

   public User getUser(String var1) {
      String var2 = this.isBlank(var1) ? "offline" : var1.trim();
      return new User(var2, var2, null, var2);
   }

   public User getCurrentUser() {
      return this.getUser("offline");
   }

   public GroupMember getCurrentUserGroupMember(String var1) {
      GroupMember var2 = (GroupMember)this.data().getGroupMembers().get(var1);
      return var2 != null ? var2 : new GroupMember(var1, "offline", OFFLINE_MODULES, OFFLINE_PERMISSIONS);
   }

   public Collection<Group> getGroups() {
      LinkedHashMap var1 = new LinkedHashMap();

      for (Machine var3 : this.data().getMachines().values()) {
         Group var4 = var3.getGroup();
         if (var4 != null && var4.getCode() != null) {
            var1.put(var4.getCode(), var4);
         }
      }

      return new ArrayList<>(var1.values());
   }

   public String getGroupAccountManager(String var1) {
      return null;
   }

   public boolean isGroupOnHold(String var1) {
      return false;
   }

   public Machine getMachine(Long var1) {
      return (Machine)this.data().getMachines().get(var1);
   }

   public Collection<Machine> getMachines() {
      return new ArrayList<>(this.data().getMachines().values());
   }

   public Collection<String> getRoastPersons(Machine var1) {
      Collection var2 = this.data().getWorkers();
      return (Collection<String>)(var2.isEmpty() ? Collections.singletonList("offline") : new ArrayList<>(var2));
   }

   public Collection<Profile> getProfiles(Long var1) {
      return new ArrayList<>(this.data().getProfiles().values());
   }

   public Lot getLot(Long var1) {
      Lot var2 = (Lot)this.data().getLots().get(var1);
      if (var2 != null) {
         return var2;
      } else {
         Roast var3 = this.getProcessingRecord(var1, Unit.CELSIUS);
         return var3 != null ? var3.getLot() : null;
      }
   }

   public Collection<Lot> getLots(Machine var1, boolean var2) {
      return new ArrayList<>(this.data().getLots().values());
   }

   public synchronized void updateLot(Lot var1) {
      this.requireLotId(var1);
      LinkedHashMap var2 = new LinkedHashMap(this.data().getLots());
      var2.put(var1.getId(), var1);
      this.data().setLots(var2);
      this.persistData();
   }

   public Roast getProcessingRecord(Long var1, Unit var2) {
      if (var1 == null) {
         throw new IllegalArgumentException("lotId must not be null");
      } else {
         for (Roast var4 : this.data().getReferences().values()) {
            if (var4 != null && var4.getLot() != null && var1.equals(var4.getLot().getId())) {
               return var4;
            }
         }

         Path var10 = this.roastFile(var1);
         if (!Files.isRegularFile(var10)) {
            return null;
         } else {
            try {
               Roast var5;
               try (BufferedReader var11 = Files.newBufferedReader(var10, StandardCharsets.UTF_8)) {
                  var5 = this.jsonReader.read(var11);
               }

               return var5;
            } catch (IOException var9) {
               throw this.storageFailure("read roast " + var1, var9);
            }
         }
      }
   }

   public Collection<BetweenBatchLog> getBetweenBatchLogs(Collection<Long> var1) {
      if (var1 != null && !var1.isEmpty()) {
         ArrayList var2 = new ArrayList();

         for (Long var4 : var1) {
            BetweenBatchLog var5 = (BetweenBatchLog)this.data().getBetweenBatchLogs().get(var4);
            if (var5 != null) {
               var2.add(var5);
            }
         }

         return var2;
      } else {
         return Collections.emptyList();
      }
   }

   public Analysis getLatestCuppingAnalysis(Long var1) {
      return (Analysis)this.data().getAnalyses().get(var1);
   }

   public Analysis getLatestPhysicalAnalysis(Long var1) {
      return (Analysis)this.data().getAnalyses().get(var1);
   }

   public Collection<Scheduled> getSchedules(Long var1) {
      List var2 = this.readList(this.stateFile("schedules.json"), Scheduled.class);
      ArrayList var3 = new ArrayList();

      for (Scheduled var5 : var2) {
         if (var1 == null || var1.equals(var5.getMachineId())) {
            var3.add(var5);
         }
      }

      return var3;
   }

   public Collection<Goal> getGoals(String var1) {
      LinkedHashSet var2 = new LinkedHashSet();

      for (Collection var4 : this.data().getGoals().values()) {
         if (var4 != null) {
            var2.addAll(var4);
         }
      }

      return new ArrayList<>(var2);
   }

   public Collection<LotConsistencyCheck> getLotConsistencyChecks(String var1, Collection<Long> var2) {
      if (var2 != null && !var2.isEmpty()) {
         ArrayList var3 = new ArrayList();

         for (Long var5 : var2) {
            LotConsistencyCheck var6 = (LotConsistencyCheck)this.data().getChecks().get(var5);
            if (var6 != null) {
               var3.add(var6);
            }
         }

         return var3;
      } else {
         return Collections.emptyList();
      }
   }

   public synchronized void markReference(Long var1, Long var2) {
      Profile var3 = (Profile)this.data().getProfiles().get(var1);
      if (var3 == null) {
         throw new IllegalArgumentException("Unknown profile: " + var1);
      } else if (this.getProcessingRecord(var2, Unit.CELSIUS) == null) {
         throw new IllegalArgumentException("Unknown local roast: " + var2);
      } else {
         Date var4 = new Date();
         var3.setReference(new Reference(var4, var2));
         var3.setReferenceLastModified(var4);
         this.persistData();
      }
   }

   public synchronized boolean updateScheduled(Scheduled var1, Lot var2) {
      if (var1 != null && var1.getId() != null) {
         if (var2 != null) {
            this.requireLotId(var2);
            var1.setRoastedLot(var2.getId());
         }

         List var3 = this.readList(this.stateFile("schedules.json"), Scheduled.class);
         boolean var4 = false;

         for (int var5 = 0; var5 < var3.size(); var5++) {
            if (var1.getId().equals(((Scheduled)var3.get(var5)).getId())) {
               var3.set(var5, var1);
               var4 = true;
               break;
            }
         }

         if (!var4) {
            var3.add(var1);
         }

         this.writeJson(this.stateFile("schedules.json"), var3);
         return true;
      } else {
         throw new IllegalArgumentException("scheduled and scheduled.id must not be null");
      }
   }

   public synchronized boolean updateScheduled(Scheduled var1) {
      return this.updateScheduled(var1, null);
   }

   public synchronized Lot startRoast(Roast var1) {
      if (var1 != null && var1.getLot() != null) {
         Lot var2 = var1.getLot();
         if (var2.getId() == null) {
            long var3 = this.reserveLotId();
            var2.setId(var3);
            var2.setIdTag(String.format("OTL-%06d", var3));
            if (this.isBlank(var2.getName())) {
               var2.setName("Roast #" + var3);
            }

            if (var2.getCreationDate() == null) {
               var2.setCreationDate(var1.getStartDate() != null ? var1.getStartDate() : new Date());
            }
         }

         if (var1.getEndWeight() != null) {
            var2.setWeight(var1.getEndWeight());
         }

         this.persistRoast(var1);
         return var2;
      } else {
         throw new IllegalArgumentException("roast and roast.lot must not be null");
      }
   }

   public synchronized void finishRoast(Roast var1) {
      this.requireRoastId(var1);
      if (var1.getEndDate() == null) {
         throw new IllegalArgumentException("roast.endDate must not be null");
      } else {
         if (var1.getEndWeight() != null) {
            var1.getLot().setWeight(var1.getEndWeight());
         }

         this.persistRoast(var1);
      }
   }

   public synchronized void update(Roast var1) {
      this.requireRoastId(var1);
      this.persistRoast(var1);
   }

   public synchronized void update(WorkerTask var1) {
      if (var1 != null && var1.getId() != null) {
         List var2 = this.readList(this.stateFile("tasks.json"), WorkerTask.class);
         boolean var3 = false;

         for (int var4 = 0; var4 < var2.size(); var4++) {
            if (var1.getId().equals(((WorkerTask)var2.get(var4)).getId())) {
               var2.set(var4, var1);
               var3 = true;
               break;
            }
         }

         if (!var3) {
            var2.add(var1);
         }

         this.writeJson(this.stateFile("tasks.json"), var2);
      } else {
         throw new IllegalArgumentException("task and task.id must not be null");
      }
   }

   public synchronized void storeBetweenBatchLog(UnsavedBetweenBatchLog var1) {
      if (var1 != null && var1.getNextBatch() != null) {
         LinkedHashMap var2 = new LinkedHashMap(this.data().getBetweenBatchLogs());
         var2.put(var1.getNextBatch(), var1);
         this.data().setBetweenBatchLogs(var2);
         this.persistData();
      } else {
         throw new IllegalArgumentException("betweenBatchLog and nextBatch must not be null");
      }
   }

   public Collection<WorkerTask> getTasks(Long var1) {
      List var2 = this.readList(this.stateFile("tasks.json"), WorkerTask.class);
      ArrayList var3 = new ArrayList();

      for (WorkerTask var5 : var2) {
         if (var1 == null || var1.equals(var5.getMachineId())) {
            var3.add(var5);
         }
      }

      return var3;
   }

   public int getCurvePredictionInterval() {
      return -1;
   }

   public double[] getCurvePrediction(Machine var1, Measure var2, Unit var3, SortedMap<Long, Double> var4, SortedMap<Long, Double> var5) {
      throw this.unsupported("curve prediction");
   }

   public long[] getFirstCrackPrediction(Machine var1, Measure var2, Unit var3, SortedMap<Long, Double> var4) {
      throw this.unsupported("first-crack prediction");
   }

   public Double getFlickPrediction(Machine var1, Measure var2, Unit var3, SortedMap<Long, Double> var4, SortedMap<Long, Double> var5) {
      throw this.unsupported("flick prediction");
   }

   public String loadPreferences(long var1) {
      return this.readText(this.preferencesFile("machine-" + var1 + ".json"));
   }

   public String loadAlarmPreferences() {
      return this.readText(this.preferencesFile("alarms.json"));
   }

   public String loadLiveGoalPreferences() {
      return this.readText(this.preferencesFile("live-goals.json"));
   }

   public void storeUserPreferences(String var1) {
      this.storeJsonText(this.preferencesFile("user.json"), var1);
   }

   public void storeMachinePreferences(long var1, String var3) {
      this.storeJsonText(this.preferencesFile("machine-" + var1 + ".json"), var3);
   }

   public void storeAlarmPreferences(String var1) {
      this.storeJsonText(this.preferencesFile("alarms.json"), var1);
   }

   public void storeLiveGoalPreferences(String var1) {
      this.storeJsonText(this.preferencesFile("live-goals.json"), var1);
   }

   public Lot createManualBlend(String var1, String var2, List<LotTransaction> var3) {
      throw this.unsupported("manual blend creation");
   }

   public Lot createBlendWithProfile(long var1, String var3, List<LotTransaction> var4) {
      throw this.unsupported("profile blend creation");
   }

   public Collection<Lot> getBlends(String var1, String var2) {
      return Collections.emptyList();
   }

   public Collection<BlendProfile> getBlendProfiles(String var1) {
      return Collections.emptyList();
   }

   public synchronized void close() {
   }

   private Data data() {
      Data var1 = OfflineBootstrap.data();
      if (var1 == null) {
         throw new IllegalStateException("OfflineBootstrap has not initialized local data");
      } else {
         return var1;
      }
   }

   private Path root() {
      Path var1 = OfflineBootstrap.root();
      if (var1 == null) {
         throw new IllegalStateException("OfflineBootstrap returned a null root path");
      } else {
         return var1.toAbsolutePath().normalize();
      }
   }

   private void ensureRoot() {
      try {
         Files.createDirectories(this.root());
      } catch (IOException var2) {
         throw this.storageFailure("create offline data directory", var2);
      }
   }

   private Path roastFile(Long var1) {
      return this.root().resolve("roasts").resolve(String.format("%d.json", var1));
   }

   private Path stateFile(String var1) {
      return this.root().resolve("state").resolve(var1);
   }

   private Path preferencesFile(String var1) {
      return this.root().resolve("preferences").resolve(var1);
   }

   private void persistRoast(final Roast var1) {
      this.atomicWrite(this.roastFile(var1.getLot().getId()), new OfflineConnection.FileWriterAction() {
         @Override
         public void write(BufferedWriter var1x) throws IOException {
            OfflineConnection.this.jsonWriter.write(var1x, var1);
         }
      });
   }

   private void persistData() {
      this.atomicWrite(this.root().resolve("data.json"), new OfflineConnection.FileWriterAction() {
         @Override
         public void write(BufferedWriter var1) throws IOException {
            OfflineConnection.this.jsonWriter.write(var1, OfflineConnection.this.data());
         }
      });
   }

   private long reserveLotId() {
      synchronized (FILE_LOCK) {
         Path var2 = this.stateFile("next-lot-id.txt");
         long var3 = this.maximumKnownLotId() + 1L;
         if (Files.isRegularFile(var2)) {
            String var5 = this.readRequiredText(var2).trim();

            try {
               var3 = Math.max(var3, Long.parseLong(var5));
            } catch (NumberFormatException var8) {
               throw new IllegalStateException("Invalid local lot ID sequence in " + var2, var8);
            }
         }

         if (var3 == Long.MAX_VALUE) {
            throw new IllegalStateException("Local lot ID space exhausted");
         } else {
            this.writeText(var2, Long.toString(var3 + 1L));
            return var3;
         }
      }
   }

   private long maximumKnownLotId() {
      long var1 = 0L;

      for (Long var4 : this.data().getLots().keySet()) {
         if (var4 != null && var4 > var1) {
            var1 = var4;
         }
      }

      for (Roast var16 : this.data().getReferences().values()) {
         if (var16 != null && var16.getLot() != null && var16.getLot().getId() != null && var16.getLot().getId() > var1) {
            var1 = var16.getLot().getId();
         }
      }

      Path var15 = this.root().resolve("roasts");
      if (Files.isDirectory(var15)) {
         try (DirectoryStream var17 = Files.newDirectoryStream(var15, "*.json")) {
            for (Path var6 : var17) {
               String var7 = var6.getFileName().toString();
               String var8 = var7.substring(0, var7.length() - ".json".length());

               try {
                  var1 = Math.max(var1, Long.parseLong(var8));
               } catch (NumberFormatException var11) {
               }
            }
         } catch (IOException var13) {
            throw this.storageFailure("scan local roast archive", var13);
         }
      }

      return var1;
   }

   private void storeJsonText(Path var1, String var2) {
      if (var2 == null) {
         throw new IllegalArgumentException("json must not be null");
      } else {
         try {
            this.objectMapper.readTree(var2);
         } catch (IOException var4) {
            throw new IllegalArgumentException("Invalid JSON for " + var1.getFileName(), var4);
         }

         this.writeText(var1, var2);
      }
   }

   private void writeText(Path var1, final String var2) {
      this.atomicWrite(var1, new OfflineConnection.FileWriterAction() {
         @Override
         public void write(BufferedWriter var1) throws IOException {
            var1.write(var2);
            var1.newLine();
         }
      });
   }

   private String readText(Path var1) {
      return Files.isRegularFile(var1) ? this.readRequiredText(var1) : null;
   }

   private String readRequiredText(Path var1) {
      try {
         return new String(Files.readAllBytes(var1), StandardCharsets.UTF_8);
      } catch (IOException var3) {
         throw this.storageFailure("read " + var1, var3);
      }
   }

   private <T> List<T> readList(Path var1, Class<T> var2) {
      if (!Files.isRegularFile(var1)) {
         return new ArrayList<>();
      } else {
         CollectionType var3 = this.objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, var2);

         try {
            return (List<T>)this.objectMapper.readValue(var1.toFile(), var3);
         } catch (IOException var5) {
            throw this.storageFailure("read " + var1, var5);
         }
      }
   }

   private void writeJson(Path var1, final Object var2) {
      this.atomicWrite(var1, new OfflineConnection.FileWriterAction() {
         @Override
         public void write(BufferedWriter var1) throws IOException {
            OfflineConnection.this.objectMapper.writerWithDefaultPrettyPrinter().writeValue(var1, var2);
         }
      });
   }

   private void atomicWrite(Path var1, OfflineConnection.FileWriterAction var2) {
      synchronized (FILE_LOCK) {
         Path var4 = var1.getParent();
         Path var5 = null;
         boolean var6 = false;

         try {
            Files.createDirectories(var4);
            var5 = Files.createTempFile(var4, var1.getFileName().toString(), ".tmp");

            try (BufferedWriter var7 = Files.newBufferedWriter(var5, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
               var2.write(var7);
            }

            try {
               Files.move(var5, var1, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException var23) {
               Files.move(var5, var1, StandardCopyOption.REPLACE_EXISTING);
            }

            var6 = true;
         } catch (IOException var25) {
            throw this.storageFailure("write " + var1, var25);
         } finally {
            if (!var6 && var5 != null) {
               try {
                  Files.deleteIfExists(var5);
               } catch (IOException var21) {
                  throw this.storageFailure("remove incomplete file " + var5, var21);
               }
            }
         }
      }
   }

   private void requireRoastId(Roast var1) {
      if (var1 == null || var1.getLot() == null || var1.getLot().getId() == null) {
         throw new IllegalArgumentException("roast, roast.lot and roast.lot.id must not be null");
      }
   }

   private void requireLotId(Lot var1) {
      if (var1 == null || var1.getId() == null) {
         throw new IllegalArgumentException("lot and lot.id must not be null");
      }
   }

   private boolean isBlank(String var1) {
      return var1 == null || var1.trim().isEmpty();
   }

   private UnsupportedOperationException unsupported(String var1) {
      return new UnsupportedOperationException(var1 + " is unavailable in offline mode");
   }

   private IllegalStateException storageFailure(String var1, IOException var2) {
      return new IllegalStateException("Unable to " + var1, var2);
   }

   private interface FileWriterAction {
      void write(BufferedWriter var1) throws IOException;
   }
}
