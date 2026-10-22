package local;

import com.cropster.util.model.Unit;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import org.cropster.roastingintelligence.model.Curve;
import org.cropster.roastingintelligence.model.LotTransaction;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.remote.Version;
import org.cropster.roastingintelligence.service.impl.FilePersister;

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
      require(implementation.contains("build/lib/roastingintelligence-5.13.jar"), "Test did not load the patched application jar: " + implementation);

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
      boolean failedOutputLotInRoster = failed.getLot().getId() != null && OfflineBootstrap.data().getLots().containsKey(failed.getLot().getId());
      long archiveCount;
      try (java.util.stream.Stream<Path> files = Files.list(offlineRoot.resolve("roasts"))) {
         archiveCount = files.filter(Files::isRegularFile).count();
      }
      require(rosterSize == 1, "Produced roast polluted the green-coffee roster");
      require(!outputLotInRoster && !failedOutputLotInRoster, "Output lot was stored in the inventory catalog");
      require(archiveCount == 1L, "Failed outbox preflight created an unexpected archive");

      System.out.println("PASS implementation=" + implementation);
      System.out.println("PASS root=" + testRoot);
      System.out.println("PASS stableId=" + assignedId + " outboxBytes=" + firstFile.length());
      System.out.println("PASS restartCurvePoints=4 archiveCurvePoints=" + archived.getCurves().get("beanTemperature").size());
      System.out.println("PASS saveFailure=true verifyRejected=true");
      System.out.println("PASS rosterSize=" + rosterSize + " savedOutputLotInRoster=" + outputLotInRoster + " failedOutputLotInRoster=" + failedOutputLotInRoster);
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

   private static void require(boolean condition, String message) {
      if (!condition) {
         throw new AssertionError(message);
      }
   }
}
