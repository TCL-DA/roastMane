package local;

import com.cropster.roastingintelligence.Data;
import com.cropster.roastingintelligence.exception.BetaAccessDeniedException;
import com.cropster.roastingintelligence.remote.Authenticator;
import com.cropster.util.model.Unit;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
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
import java.util.Map;
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

/**
 * Kết nối cục bộ cho RI: không thực hiện bất kỳ yêu cầu mạng nào.
 *
 * Dữ liệu nền do OfflineBootstrap sở hữu. Lớp này chỉ cập nhật singleton đó và
 * ghi các thay đổi xuống thư mục cục bộ một cách nguyên tử.
 */
public final class OfflineConnection extends CsarConnectionImpl {
   private static final Object FILE_LOCK = new Object();
   private static final String OFFLINE_USER = "offline";
   private static final Collection<String> OFFLINE_MODULES = Collections.singletonList(GroupMember.MODULE_RI);
   private static final Collection<String> OFFLINE_PERMISSIONS = Collections.singletonList(GroupMember.PERMISSION_RI);

   private final JsonReader jsonReader = new JsonReader();
   private final JsonWriter jsonWriter;
   private final ObjectMapper objectMapper;

   public OfflineConnection(Version clientVersion, URI baseUri) {
      super(clientVersion, baseUri);
      this.jsonWriter = new JsonWriter(clientVersion);
      this.objectMapper = new ObjectMapper();
      this.objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
      this.objectMapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));
   }

   @Override
   public void init(Authenticator authenticator) {
      // Chế độ cục bộ không cần trình xác thực.
   }

   @Override
   public void open() {
      ensureRoot();
   }

   @Override
   public void invalidate() {
      // Không có phiên đăng nhập cục bộ để vô hiệu hóa.
   }

   @Override
   public void log() throws BetaAccessDeniedException {
      ensureRoot();
   }

   @Override
   public URL createSupportTicket(String subject, String description, File attachment) {
      throw unsupported("support tickets");
   }

   @Override
   public User getUser(String username) {
      String effectiveUsername = isBlank(username) ? OFFLINE_USER : username.trim();
      return new User(effectiveUsername, effectiveUsername, null, effectiveUsername);
   }

   @Override
   public User getCurrentUser() {
      return getUser(OFFLINE_USER);
   }

   @Override
   public GroupMember getCurrentUserGroupMember(String groupCode) {
      GroupMember configured = data().getGroupMembers().get(groupCode);
      if (configured != null) {
         return configured;
      }
      return new GroupMember(groupCode, OFFLINE_USER, OFFLINE_MODULES, OFFLINE_PERMISSIONS);
   }

   @Override
   public Collection<Group> getGroups() {
      Map<String, Group> groups = new LinkedHashMap<>();
      for (Machine machine : data().getMachines().values()) {
         Group group = machine.getGroup();
         if (group != null && group.getCode() != null) {
            groups.put(group.getCode(), group);
         }
      }
      return new ArrayList<>(groups.values());
   }

   @Override
   public String getGroupAccountManager(String groupCode) {
      return null;
   }

   @Override
   public boolean isGroupOnHold(String groupCode) {
      return false;
   }

   @Override
   public Machine getMachine(Long machineId) {
      return data().getMachines().get(machineId);
   }

   @Override
   public Collection<Machine> getMachines() {
      return new ArrayList<>(data().getMachines().values());
   }

   @Override
   public Collection<String> getRoastPersons(Machine machine) {
      Collection<String> workers = data().getWorkers();
      return workers.isEmpty() ? Collections.singletonList(OFFLINE_USER) : new ArrayList<>(workers);
   }

   @Override
   public Collection<Profile> getProfiles(Long machineId) {
      return new ArrayList<>(data().getProfiles().values());
   }

   @Override
   public Lot getLot(Long lotId) {
      Lot inventoryLot = data().getLots().get(lotId);
      if (inventoryLot != null) {
         return inventoryLot;
      }
      Roast archivedRoast = getProcessingRecord(lotId, Unit.CELSIUS);
      return archivedRoast != null ? archivedRoast.getLot() : null;
   }

   @Override
   public Collection<Lot> getLots(Machine machine, boolean samplesOnly) {
      return new ArrayList<>(data().getLots().values());
   }

   @Override
   public synchronized void updateLot(Lot lot) {
      requireLotId(lot);
      Map<Long, Lot> lots = new LinkedHashMap<>(data().getLots());
      lots.put(lot.getId(), lot);
      data().setLots(lots);
      persistData();
   }

   @Override
   public Roast getProcessingRecord(Long lotId, Unit temperatureUnit) {
      if (lotId == null) {
         throw new IllegalArgumentException("lotId must not be null");
      }
      for (Roast reference : data().getReferences().values()) {
         if (reference != null && reference.getLot() != null && lotId.equals(reference.getLot().getId())) {
            return reference;
         }
      }
      Path file = roastFile(lotId);
      if (!Files.isRegularFile(file)) {
         return null;
      }
      try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
         return this.jsonReader.read(reader);
      } catch (IOException ex) {
         throw storageFailure("read roast " + lotId, ex);
      }
   }

   @Override
   public Collection<BetweenBatchLog> getBetweenBatchLogs(Collection<Long> nextBatchIds) {
      if (nextBatchIds == null || nextBatchIds.isEmpty()) {
         return Collections.emptyList();
      }
      List<BetweenBatchLog> result = new ArrayList<>();
      for (Long id : nextBatchIds) {
         BetweenBatchLog log = data().getBetweenBatchLogs().get(id);
         if (log != null) {
            result.add(log);
         }
      }
      return result;
   }

   @Override
   public Analysis getLatestCuppingAnalysis(Long lotId) {
      return data().getAnalyses().get(lotId);
   }

   @Override
   public Analysis getLatestPhysicalAnalysis(Long lotId) {
      return data().getAnalyses().get(lotId);
   }

   @Override
   public Collection<Scheduled> getSchedules(Long machineId) {
      List<Scheduled> schedules = readList(stateFile("schedules.json"), Scheduled.class);
      List<Scheduled> result = new ArrayList<>();
      for (Scheduled scheduled : schedules) {
         if (machineId == null || machineId.equals(scheduled.getMachineId())) {
            result.add(scheduled);
         }
      }
      return result;
   }

   @Override
   public Collection<Goal> getGoals(String groupCode) {
      LinkedHashSet<Goal> goals = new LinkedHashSet<>();
      for (Collection<Goal> profileGoals : data().getGoals().values()) {
         if (profileGoals != null) {
            goals.addAll(profileGoals);
         }
      }
      return new ArrayList<>(goals);
   }

   @Override
   public Collection<LotConsistencyCheck> getLotConsistencyChecks(String groupCode, Collection<Long> lotIds) {
      if (lotIds == null || lotIds.isEmpty()) {
         return Collections.emptyList();
      }
      List<LotConsistencyCheck> result = new ArrayList<>();
      for (Long lotId : lotIds) {
         LotConsistencyCheck check = data().getChecks().get(lotId);
         if (check != null) {
            result.add(check);
         }
      }
      return result;
   }

   @Override
   public synchronized void markReference(Long profileId, Long lotId) {
      Profile profile = data().getProfiles().get(profileId);
      if (profile == null) {
         throw new IllegalArgumentException("Unknown profile: " + profileId);
      }
      if (getProcessingRecord(lotId, Unit.CELSIUS) == null) {
         throw new IllegalArgumentException("Unknown local roast: " + lotId);
      }
      Date now = new Date();
      profile.setReference(new Reference(now, lotId));
      profile.setReferenceLastModified(now);
      persistData();
   }

   @Override
   public synchronized boolean updateScheduled(Scheduled scheduled, Lot roastedLot) {
      if (scheduled == null || scheduled.getId() == null) {
         throw new IllegalArgumentException("scheduled and scheduled.id must not be null");
      }
      if (roastedLot != null) {
         requireLotId(roastedLot);
         scheduled.setRoastedLot(roastedLot.getId());
      }
      List<Scheduled> schedules = readList(stateFile("schedules.json"), Scheduled.class);
      boolean replaced = false;
      for (int i = 0; i < schedules.size(); i++) {
         if (scheduled.getId().equals(schedules.get(i).getId())) {
            schedules.set(i, scheduled);
            replaced = true;
            break;
         }
      }
      if (!replaced) {
         schedules.add(scheduled);
      }
      writeJson(stateFile("schedules.json"), schedules);
      return true;
   }

   @Override
   public synchronized boolean updateScheduled(Scheduled scheduled) {
      return updateScheduled(scheduled, null);
   }

   @Override
   public synchronized Lot startRoast(Roast roast) {
      if (roast == null || roast.getLot() == null) {
         throw new IllegalArgumentException("roast and roast.lot must not be null");
      }
      Lot roastedLot = roast.getLot();
      if (roastedLot.getId() == null) {
         long id = reserveLotId();
         roastedLot.setId(id);
         roastedLot.setIdTag(String.format("OTL-%06d", id));
         if (isBlank(roastedLot.getName())) {
            roastedLot.setName("Roast #" + id);
         }
         if (roastedLot.getCreationDate() == null) {
            roastedLot.setCreationDate(roast.getStartDate() != null ? roast.getStartDate() : new Date());
         }
      }
      if (roast.getEndWeight() != null) {
         roastedLot.setWeight(roast.getEndWeight());
      }
      persistRoast(roast);
      return roastedLot;
   }

   @Override
   public synchronized void finishRoast(Roast roast) {
      requireRoastId(roast);
      if (roast.getEndDate() == null) {
         throw new IllegalArgumentException("roast.endDate must not be null");
      }
      if (roast.getEndWeight() != null) {
         roast.getLot().setWeight(roast.getEndWeight());
      }
      persistRoast(roast);
   }

   @Override
   public synchronized void update(Roast roast) {
      requireRoastId(roast);
      persistRoast(roast);
   }

   @Override
   public synchronized void update(WorkerTask task) {
      if (task == null || task.getId() == null) {
         throw new IllegalArgumentException("task and task.id must not be null");
      }
      List<WorkerTask> tasks = readList(stateFile("tasks.json"), WorkerTask.class);
      boolean replaced = false;
      for (int i = 0; i < tasks.size(); i++) {
         if (task.getId().equals(tasks.get(i).getId())) {
            tasks.set(i, task);
            replaced = true;
            break;
         }
      }
      if (!replaced) {
         tasks.add(task);
      }
      writeJson(stateFile("tasks.json"), tasks);
   }

   @Override
   public synchronized void storeBetweenBatchLog(UnsavedBetweenBatchLog betweenBatchLog) {
      if (betweenBatchLog == null || betweenBatchLog.getNextBatch() == null) {
         throw new IllegalArgumentException("betweenBatchLog and nextBatch must not be null");
      }
      Map<Long, BetweenBatchLog> logs = new LinkedHashMap<>(data().getBetweenBatchLogs());
      logs.put(betweenBatchLog.getNextBatch(), betweenBatchLog);
      data().setBetweenBatchLogs(logs);
      persistData();
   }

   @Override
   public Collection<WorkerTask> getTasks(Long machineId) {
      List<WorkerTask> tasks = readList(stateFile("tasks.json"), WorkerTask.class);
      List<WorkerTask> result = new ArrayList<>();
      for (WorkerTask task : tasks) {
         if (machineId == null || machineId.equals(task.getMachineId())) {
            result.add(task);
         }
      }
      return result;
   }

   @Override
   public int getCurvePredictionInterval() {
      return -1;
   }

   @Override
   public double[] getCurvePrediction(
      Machine machine,
      Measure startWeight,
      Unit temperatureUnit,
      SortedMap<Long, Double> beanCurve,
      SortedMap<Long, Double> gasCurve
   ) {
      throw unsupported("curve prediction");
   }

   @Override
   public long[] getFirstCrackPrediction(Machine machine, Measure startWeight, Unit temperatureUnit, SortedMap<Long, Double> beanCurve) {
      throw unsupported("first-crack prediction");
   }

   @Override
   public Double getFlickPrediction(
      Machine machine,
      Measure startWeight,
      Unit temperatureUnit,
      SortedMap<Long, Double> beanCurve,
      SortedMap<Long, Double> gasCurve
   ) {
      throw unsupported("flick prediction");
   }

   @Override
   public String loadPreferences(long machineId) {
      return readText(preferencesFile("machine-" + machineId + ".json"));
   }

   @Override
   public String loadAlarmPreferences() {
      return readText(preferencesFile("alarms.json"));
   }

   @Override
   public String loadLiveGoalPreferences() {
      return readText(preferencesFile("live-goals.json"));
   }

   @Override
   public void storeUserPreferences(String json) {
      storeJsonText(preferencesFile("user.json"), json);
   }

   @Override
   public void storeMachinePreferences(long machineId, String json) {
      storeJsonText(preferencesFile("machine-" + machineId + ".json"), json);
   }

   @Override
   public void storeAlarmPreferences(String json) {
      storeJsonText(preferencesFile("alarms.json"), json);
   }

   @Override
   public void storeLiveGoalPreferences(String json) {
      storeJsonText(preferencesFile("live-goals.json"), json);
   }

   @Override
   public Lot createManualBlend(String name, String notes, List<LotTransaction> components) {
      throw unsupported("manual blend creation");
   }

   @Override
   public Lot createBlendWithProfile(long profileId, String notes, List<LotTransaction> components) {
      throw unsupported("profile blend creation");
   }

   @Override
   public Collection<Lot> getBlends(String groupCode, String user) {
      return Collections.emptyList();
   }

   @Override
   public Collection<BlendProfile> getBlendProfiles(String groupCode) {
      return Collections.emptyList();
   }

   @Override
   public synchronized void close() {
      // Không có kết nối mạng hoặc tài nguyên nền cần đóng.
   }

   private Data data() {
      Data current = OfflineBootstrap.data();
      if (current == null) {
         throw new IllegalStateException("OfflineBootstrap has not initialized local data");
      }
      return current;
   }

   private Path root() {
      Path root = OfflineBootstrap.root();
      if (root == null) {
         throw new IllegalStateException("OfflineBootstrap returned a null root path");
      }
      return root.toAbsolutePath().normalize();
   }

   private void ensureRoot() {
      try {
         Files.createDirectories(root());
      } catch (IOException ex) {
         throw storageFailure("create offline data directory", ex);
      }
   }

   private Path roastFile(Long lotId) {
      return root().resolve("roasts").resolve(String.format("%d.json", lotId));
   }

   private Path stateFile(String name) {
      return root().resolve("state").resolve(name);
   }

   private Path preferencesFile(String name) {
      return root().resolve("preferences").resolve(name);
   }

   private void persistRoast(final Roast roast) {
      atomicWrite(roastFile(roast.getLot().getId()), new FileWriterAction() {
         @Override
         public void write(BufferedWriter writer) throws IOException {
            jsonWriter.write(writer, roast);
         }
      });
   }

   private void persistData() {
      atomicWrite(root().resolve("data.json"), new FileWriterAction() {
         @Override
         public void write(BufferedWriter writer) throws IOException {
            jsonWriter.write(writer, data());
         }
      });
   }

   private long reserveLotId() {
      synchronized (FILE_LOCK) {
         Path sequenceFile = stateFile("next-lot-id.txt");
         long nextId = maximumKnownLotId() + 1L;
         if (Files.isRegularFile(sequenceFile)) {
            String value = readRequiredText(sequenceFile).trim();
            try {
               nextId = Math.max(nextId, Long.parseLong(value));
            } catch (NumberFormatException ex) {
               throw new IllegalStateException("Invalid local lot ID sequence in " + sequenceFile, ex);
            }
         }
         if (nextId == Long.MAX_VALUE) {
            throw new IllegalStateException("Local lot ID space exhausted");
         }
         writeText(sequenceFile, Long.toString(nextId + 1L));
         return nextId;
      }
   }

   private long maximumKnownLotId() {
      long maximum = 0L;
      for (Long id : data().getLots().keySet()) {
         if (id != null && id > maximum) {
            maximum = id;
         }
      }
      for (Roast roast : data().getReferences().values()) {
         if (roast != null && roast.getLot() != null && roast.getLot().getId() != null && roast.getLot().getId() > maximum) {
            maximum = roast.getLot().getId();
         }
      }
      Path roastDirectory = root().resolve("roasts");
      if (Files.isDirectory(roastDirectory)) {
         try (java.nio.file.DirectoryStream<Path> files = Files.newDirectoryStream(roastDirectory, "*.json")) {
            for (Path file : files) {
               String fileName = file.getFileName().toString();
               String idText = fileName.substring(0, fileName.length() - ".json".length());
               try {
                  maximum = Math.max(maximum, Long.parseLong(idText));
               } catch (NumberFormatException ignored) {
                  // Bỏ qua file không thuộc quy ước archive theo ID.
               }
            }
         } catch (IOException ex) {
            throw storageFailure("scan local roast archive", ex);
         }
      }
      return maximum;
   }

   private void storeJsonText(Path file, String json) {
      if (json == null) {
         throw new IllegalArgumentException("json must not be null");
      }
      try {
         this.objectMapper.readTree(json);
      } catch (IOException ex) {
         throw new IllegalArgumentException("Invalid JSON for " + file.getFileName(), ex);
      }
      writeText(file, json);
   }

   private void writeText(final Path file, final String value) {
      atomicWrite(file, new FileWriterAction() {
         @Override
         public void write(BufferedWriter writer) throws IOException {
            writer.write(value);
            writer.newLine();
         }
      });
   }

   private String readText(Path file) {
      return Files.isRegularFile(file) ? readRequiredText(file) : null;
   }

   private String readRequiredText(Path file) {
      try {
         return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
      } catch (IOException ex) {
         throw storageFailure("read " + file, ex);
      }
   }

   private <T> List<T> readList(Path file, Class<T> elementType) {
      if (!Files.isRegularFile(file)) {
         return new ArrayList<>();
      }
      JavaType listType = this.objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, elementType);
      try {
         return this.objectMapper.readValue(file.toFile(), listType);
      } catch (IOException ex) {
         throw storageFailure("read " + file, ex);
      }
   }

   private void writeJson(final Path file, final Object value) {
      atomicWrite(file, new FileWriterAction() {
         @Override
         public void write(BufferedWriter writer) throws IOException {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, value);
         }
      });
   }

   private void atomicWrite(Path target, FileWriterAction action) {
      synchronized (FILE_LOCK) {
         Path parent = target.getParent();
         Path temporary = null;
         boolean moved = false;
         try {
            Files.createDirectories(parent);
            temporary = Files.createTempFile(parent, target.getFileName().toString(), ".tmp");
            try (BufferedWriter writer = Files.newBufferedWriter(
               temporary,
               StandardCharsets.UTF_8,
               StandardOpenOption.TRUNCATE_EXISTING,
               StandardOpenOption.WRITE
            )) {
               action.write(writer);
            }
            try {
               Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException ex) {
               Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
            }
            moved = true;
         } catch (IOException ex) {
            throw storageFailure("write " + target, ex);
         } finally {
            if (!moved && temporary != null) {
               try {
                  Files.deleteIfExists(temporary);
               } catch (IOException cleanupError) {
                  throw storageFailure("remove incomplete file " + temporary, cleanupError);
               }
            }
         }
      }
   }

   private void requireRoastId(Roast roast) {
      if (roast == null || roast.getLot() == null || roast.getLot().getId() == null) {
         throw new IllegalArgumentException("roast, roast.lot and roast.lot.id must not be null");
      }
   }

   private void requireLotId(Lot lot) {
      if (lot == null || lot.getId() == null) {
         throw new IllegalArgumentException("lot and lot.id must not be null");
      }
   }

   private boolean isBlank(String value) {
      return value == null || value.trim().isEmpty();
   }

   private UnsupportedOperationException unsupported(String feature) {
      return new UnsupportedOperationException(feature + " is unavailable in offline mode");
   }

   private IllegalStateException storageFailure(String operation, IOException cause) {
      return new IllegalStateException("Unable to " + operation, cause);
   }

   private interface FileWriterAction {
      void write(BufferedWriter writer) throws IOException;
   }
}
