package local;

import com.cropster.util.model.Unit;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.cropster.roastingintelligence.model.Curve;
import org.cropster.roastingintelligence.model.LotTransaction;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.remote.Version;
import org.cropster.roastingintelligence.service.impl.FilePersister;
import org.cropster.roastingintelligence.service.impl.JsonWriter;

public final class PatchedFilePersisterIntegrationTest {
   public static void main(String[] args) throws Exception {
      Path testRoot = Files.createTempDirectory("patched-file-persister-").toAbsolutePath();
      Path offlineRoot = testRoot.resolve("offline-data");
      Path outbox = Files.createDirectories(testRoot.resolve("outbox"));
      Path sent = Files.createDirectories(testRoot.resolve("sent"));
      Path logs = Files.createDirectories(testRoot.resolve("logs"));
      System.setProperty("offline.data", offlineRoot.toString());
      System.setProperty("org.cropster.roastingintelligence.logging.path", logs.toString().replace('\\', '/') + "/");

      String implementation = FilePersister.class.getProtectionDomain().getCodeSource().getLocation().toString();
      require(implementation.endsWith("/app/roastingintelligence-5.13.jar"), "Test did not load the packaged roastMane application jar: " + implementation);

      Version version = new Version("5.13");
      FilePersister firstPersister = new FilePersister(version, outbox.toFile(), sent.toFile());
      Roast original = createRoast(1700000200000L, "first save");
      File firstFile = firstPersister.saveNew(original);
      Long assignedId = original.getLot().getId();
      require(assignedId != null, "saveNew did not assign a durable lot ID");
      require(firstFile.isFile() && firstFile.length() > 0L, "saveNew did not create a non-empty outbox file");
      require(firstFile.getName().matches("\\d{8}_\\d{6}_\\d{3}\\.json"), "Saved filename is incompatible with RoastFilters: " + firstFile.getName());
      OfflinePersistence.verifySaved(original);

      FilePersister restartedPersister = new FilePersister(version, outbox.toFile(), sent.toFile());
      List<Roast> afterRestart = restartedPersister.loadRecentRoasts(10);
      require(afterRestart.size() == 1, "Restart did not find exactly one saved roast");
      Roast reloaded = afterRestart.get(0);
      require(assignedId.equals(reloaded.getLot().getId()), "Roast ID changed after persister restart");
      assertInitialCurves(reloaded);

      reloaded.setNotes("edited after restart");
      reloaded.getCurves().get("beanTemperature").put(3000L, 191.75);
      reloaded.getCurves().get("gas").put(3000L, 37.0);
      File editedFile = restartedPersister.save(reloaded);
      require(editedFile.equals(firstFile), "save changed the file associated with the roast");
      require(assignedId.equals(reloaded.getLot().getId()), "save changed the durable roast ID");
      OfflinePersistence.verifySaved(reloaded);

      FilePersister thirdPersister = new FilePersister(version, outbox.toFile(), sent.toFile());
      Roast editedReloaded = thirdPersister.loadRecentRoasts(10).get(0);
      require("edited after restart".equals(editedReloaded.getNotes()), "Edited notes did not survive another restart");
      require(editedReloaded.getCurves().get("beanTemperature").size() == 4, "Edited bean curve was truncated");
      require(Double.valueOf(191.75).equals(editedReloaded.getCurves().get("beanTemperature").get(3000L)), "Edited bean curve value changed");
      require(Double.valueOf(37.0).equals(editedReloaded.getCurves().get("gas").get(3000L)), "Edited gas curve value changed");

      OfflineConnection connection = new OfflineConnection(version, URI.create("https://offline.invalid"));
      Roast archived = connection.getProcessingRecord(assignedId, Unit.CELSIUS);
      require(archived != null, "Connection archive is missing");
      require(archived.getCurves().get("beanTemperature").equals(editedReloaded.getCurves().get("beanTemperature")), "Archive bean curve differs from outbox curve");
      require(archived.getCurves().get("gas").equals(editedReloaded.getCurves().get("gas")), "Archive gas curve differs from outbox curve");
      require(connection.getLots(null, false).size() == 1, "Inventory API includes a produced roast lot");
      require(connection.getLot(assignedId) != null && assignedId.equals(connection.getLot(assignedId).getId()), "getLot did not resolve the separate roast archive");

      byte[] bytesBeforeFailedEdit = Files.readAllBytes(editedFile.toPath());
      java.lang.reflect.Field jsonWriterField = FilePersister.class.getDeclaredField("_jsonWriter");
      jsonWriterField.setAccessible(true);
      JsonWriter originalJsonWriter = (JsonWriter)jsonWriterField.get(restartedPersister);
      reloaded.setNotes("edit that must fail");
      boolean editSaveFailed = false;
      try {
         jsonWriterField.set(
            restartedPersister,
            new JsonWriter(version) {
               @Override
               public void write(Writer writer, Roast roast) throws IOException {
                  throw new IOException("Injected edit failure");
               }
            }
         );
         restartedPersister.save(reloaded);
      } catch (IOException expected) {
         editSaveFailed = true;
      } finally {
         jsonWriterField.set(restartedPersister, originalJsonWriter);
      }
      require(editSaveFailed, "Injected JsonWriter did not make the edit fail");
      require(Arrays.equals(bytesBeforeFailedEdit, Files.readAllBytes(editedFile.toPath())), "Failed edit changed the existing outbox file");
      boolean failedEditVerificationRejected = false;
      try {
         OfflinePersistence.verifySaved(reloaded);
      } catch (IllegalStateException expected) {
         failedEditVerificationRejected = true;
      }
      require(failedEditVerificationRejected, "verifySaved accepted an old file after the current edit failed");

      int rosterSizeBeforeCollision = OfflineBootstrap.data().getLots().size();
      long archiveCountBeforeCollision = countFiles(offlineRoot.resolve("roasts"));
      Path sequenceFile = offlineRoot.resolve("state").resolve("next-lot-id.txt");
      String sequenceBeforeCollision = Files.readString(sequenceFile, StandardCharsets.UTF_8);
      Roast colliding = createRoast(original.getStartDate().getTime(), "duplicate timestamp");
      boolean collisionFailed = false;
      try {
         firstPersister.saveNew(colliding);
      } catch (IOException expected) {
         collisionFailed = true;
      }
      require(collisionFailed, "Duplicate timestamp did not make saveNew fail");
      require(colliding.getLot().getId() == null, "Duplicate timestamp consumed a durable lot ID");
      require(OfflineBootstrap.data().getLots().size() == rosterSizeBeforeCollision, "Duplicate timestamp changed the inventory roster");
      require(countFiles(offlineRoot.resolve("roasts")) == archiveCountBeforeCollision, "Duplicate timestamp created an archive");
      require(sequenceBeforeCollision.equals(Files.readString(sequenceFile, StandardCharsets.UTF_8)), "Duplicate timestamp advanced the lot ID sequence");

      Roast retryAfterCollision = createRoast(original.getStartDate().getTime() + 1L, "retry after duplicate timestamp");
      File retryFile = firstPersister.saveNew(retryAfterCollision);
      require(retryFile.isFile() && retryFile.length() > 0L, "Retry after duplicate timestamp did not create an outbox file");
      require(Long.valueOf(assignedId + 1L).equals(retryAfterCollision.getLot().getId()), "Duplicate timestamp left a gap in the lot ID sequence");
      OfflinePersistence.verifySaved(retryAfterCollision);
      long archiveCountAfterRetry = countFiles(offlineRoot.resolve("roasts"));
      require(archiveCountAfterRetry == archiveCountBeforeCollision + 1L, "Successful retry did not create exactly one archive");

      Path badOutbox = Files.createDirectories(testRoot.resolve("bad-outbox"));
      Path badSent = Files.createDirectories(testRoot.resolve("bad-sent"));
      FilePersister failingPersister = new FilePersister(version, badOutbox.toFile(), badSent.toFile());
      Files.delete(badOutbox);
      Files.write(badOutbox, "not a directory".getBytes(StandardCharsets.UTF_8));
      Roast failed = createRoast(1700000300000L, "must fail");
      boolean saveFailed = false;
      try {
         failingPersister.saveNew(failed);
      } catch (IOException expected) {
         saveFailed = true;
      }
      require(saveFailed, "Invalid outbox path did not make saveNew fail");
      require(failed.getLot().getId() == null, "Failed outbox preflight consumed a durable lot ID");
      boolean verificationRejected = false;
      try {
         OfflinePersistence.verifySaved(failed);
      } catch (IllegalStateException expected) {
         verificationRejected = true;
      }
      require(verificationRejected, "verifySaved accepted a roast whose outbox save failed");

      int rosterSize = OfflineBootstrap.data().getLots().size();
      boolean outputLotInRoster = OfflineBootstrap.data().getLots().containsKey(assignedId);
      boolean retryOutputLotInRoster = OfflineBootstrap.data().getLots().containsKey(retryAfterCollision.getLot().getId());
      boolean failedOutputLotInRoster = failed.getLot().getId() != null && OfflineBootstrap.data().getLots().containsKey(failed.getLot().getId());
      long archiveCount = countFiles(offlineRoot.resolve("roasts"));
      require(rosterSize == 1, "Produced roast polluted the green-coffee roster");
      require(!outputLotInRoster && !retryOutputLotInRoster && !failedOutputLotInRoster, "Output lot was stored in the inventory catalog");
      require(archiveCount == archiveCountAfterRetry, "Failed outbox preflight created an unexpected archive");

      System.out.println("PASS implementation=" + implementation);
      System.out.println("PASS root=" + testRoot);
      System.out.println("PASS stableId=" + assignedId + " outboxBytes=" + firstFile.length());
      System.out.println("PASS restartCurvePoints=4 archiveCurvePoints=" + archived.getCurves().get("beanTemperature").size());
      System.out.println("PASS failedEdit=true oldBytesPreserved=true verifyRejected=true");
      System.out.println("PASS duplicateTimestampRejected=true duplicateId=null retryId=" + retryAfterCollision.getLot().getId());
      System.out.println("PASS saveFailure=true verifyRejected=true");
      System.out.println("PASS rosterSize=" + rosterSize + " savedOutputLotInRoster=" + outputLotInRoster + " retryOutputLotInRoster=" + retryOutputLotInRoster + " failedOutputLotInRoster=" + failedOutputLotInRoster);
      System.out.println("PASS failedId=null archiveCount=" + archiveCount);
   }

   private static Roast createRoast(long startMillis, String notes) {
      Roast roast = new Roast();
      roast.setMachine(1L);
      roast.setProfile(1L);
      roast.setWorker("Local operator");
      roast.setNotes(notes);
      roast.setStartDate(new Date(startMillis));
      roast.setEndDate(new Date(startMillis + 3000L));
      roast.setStartWeight(new Measure(5.0, Unit.KG));
      roast.setEndWeight(new Measure(4.2, Unit.KG));
      roast.getLot().getSources().add(new LotTransaction(1L, new Measure(5.0, Unit.KG)));

      Curve bean = new Curve(Unit.CELSIUS);
      bean.put(0L, 180.0);
      bean.put(1000L, 183.25);
      bean.put(2000L, 187.5);
      roast.getCurves().put("beanTemperature", bean);

      Curve gas = new Curve(Unit.PERCENT);
      gas.put(0L, 62.0);
      gas.put(1000L, 54.0);
      gas.put(2000L, 46.0);
      roast.getCurves().put("gas", gas);
      return roast;
   }

   private static void assertInitialCurves(Roast roast) {
      require(roast.getCurves().get("beanTemperature").size() == 3, "Bean curve point count changed after restart");
      require(Double.valueOf(183.25).equals(roast.getCurves().get("beanTemperature").get(1000L)), "Bean curve value changed after restart");
      require(roast.getCurves().get("gas").size() == 3, "Gas curve point count changed after restart");
      require(Double.valueOf(46.0).equals(roast.getCurves().get("gas").get(2000L)), "Gas curve value changed after restart");
   }

   private static long countFiles(Path directory) throws IOException {
      try (java.util.stream.Stream<Path> files = Files.list(directory)) {
         return files.filter(Files::isRegularFile).count();
      }
   }

   private static void require(boolean condition, String message) {
      if (!condition) {
         throw new AssertionError(message);
      }
   }
}
