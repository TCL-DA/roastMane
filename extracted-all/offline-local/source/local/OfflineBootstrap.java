package local;

import com.cropster.roastingintelligence.Data;
import com.cropster.roastingintelligence.Preferences;
import com.cropster.roastingintelligence.config.FileSystemConfiguration.Directories;
import com.cropster.roastingintelligence.ui.JavaFxController;
import com.cropster.roastingintelligence.ui.layout.MainPanelController;
import com.cropster.util.model.Unit;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.scene.control.Tooltip;
import org.cropster.roastingintelligence.AppModel;
import org.cropster.roastingintelligence.RoastingIntelligenceApp;
import org.cropster.roastingintelligence.measurement.SourceParameter;
import org.cropster.roastingintelligence.model.BlendComponent;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.Group;
import org.cropster.roastingintelligence.model.GroupMember;
import org.cropster.roastingintelligence.model.Location;
import org.cropster.roastingintelligence.model.Lot;
import org.cropster.roastingintelligence.model.Machine;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.Profile;
import org.cropster.roastingintelligence.model.Project;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.remote.Version;
import org.cropster.roastingintelligence.service.impl.JsonReader;
import org.cropster.roastingintelligence.service.impl.JsonWriter;

public final class OfflineBootstrap {
   private static Data catalog;

   public static Path root() {
      return Paths.get(System.getProperty("offline.data", System.getProperty("user.home") + "/roastMane/otl-offline"));
   }

   public static synchronized Data data() {
      if (catalog != null) {
         return catalog;
      } else {
         try {
            Files.createDirectories(root());
            Path var0 = root().resolve("data.json");
            if (Files.exists(var0)) {
               try (BufferedReader var15 = Files.newBufferedReader(var0)) {
                  catalog = new JsonReader().readData(var15);
               }

               return catalog;
            } else {
               Data var1 = new Data();
               Group var2 = new Group("LOCAL", "Local offline workspace");
               Machine var3 = new Machine(1L, "roasting", "DRUM", "OTL - local Modbus RTU", var2);
               var3.setLocation(new Location(1L, "Local"));
               var3.setChannels(new String[]{"beanTemperature", "exhaustTemperature"});
               var3.setComments(new String[]{"CHARGE", "DRY_END", "FIRST_CRACK", "DROP"});
               var1.setMachines(Map.of(1L, var3));
               var1.setWorkers(List.of("Local operator"));
               Lot var4 = new Lot(1L, "Local coffee - describe the actual coffee in batch notes");
               var4.setIdTag("LOCAL-GREEN");
               var4.setProject(new Project(1L, "Local coffee"));
               var4.setLocation(var3.getLocation());
               var1.setLots(Map.of(1L, var4));
               Profile var5 = new Profile(1L, "Local roast");
               BlendComponent var6 = new BlendComponent();
               var6.setId(1L);
               var6.setName("Coffee");
               var6.setPercentage(new BigDecimal("100"));
               var6.setLotIds(List.of(1L));
               var5.setComponents(new LinkedHashSet<>(List.of(var6)));
               var1.setProfiles(Map.of(1L, var5));
               var1.setGroupMembers(
                  Map.of("LOCAL", new GroupMember("LOCAL", "local", List.of("roast.roastingintelligence"), List.of("roastRoastingIntelligence")))
               );
               catalog = var1;

               try (BufferedWriter var7 = Files.newBufferedWriter(var0, StandardOpenOption.CREATE_NEW)) {
                  new JsonWriter(new Version("5.13")).write(var7, var1);
               }

               return var1;
            }
         } catch (Exception var14) {
            throw new IllegalStateException("Unable to load/save local catalog", var14);
         }
      }
   }

   public static void configure(AppModel var0) {
      var0.setRefreshToken(null);
      var0.setRememberMe(false);
      var0.setUser("Local operator");
      var0.setUserName("local");
      var0.setData(data());
      Preferences var1 = var0.getPreferences();
      if (var0.getSelectedMachine() == null) {
         var0.setSelectedMachine((Machine)data().getMachines().get(1L));
      }

      if (var0.getSelectedProfile() == null) {
         var0.setSelectedProfile((Profile)data().getProfiles().get(1L));
      }

      var1.setCurvePredictionEnabled(false);
      var1.setFirstCrackPredictionEnabled(false);
      var1.setFlickPredictionEnabled(false);
      if (var1.getChannels().stream().noneMatch(var0x -> var0x.getSourceFactory() != null)) {
         ArrayList var2 = new ArrayList();

         for (String var4 : List.of("beanTemperature", "exhaustTemperature")) {
            Channel var5 = new Channel(var4);
            var5.setSourceFactory(null);
            var5.setProbeType("Default");
            var5.setRorEnabled(true);
            var5.setColor(var4.equals("beanTemperature") ? Color.ORANGE : Color.CYAN);
            var2.add(var5);
         }

         var1.setChannels(var2);
         LinkedHashSet var8 = new LinkedHashSet();

         for (String[] var7 : new String[][]{
            {"encoding", "rtu"},
            {"baudRate", "9600"},
            {"dataBits", "8"},
            {"parity", "None"},
            {"stopBits", "1"},
            {"modbusFunction", "3"},
            {"temperatureUnit", "CELSIUS"},
            {"storageFormatTemperature", "DECIMAL1_16"},
            {"beanTemperatureSlave", "1"},
            {"beanTemperatureRegister", "0"},
            {"exhaustTemperatureSlave", "1"},
            {"exhaustTemperatureRegister", "1"}
         }) {
            var8.add(new SourceParameter(var7[0], String.class, var7[1]));
         }

         var1.setSourceConfigurations(Map.of("GenericModbusRtuSourceFactory", var8));
      }
   }

   public static Directories directories() {
      try {
         Files.createDirectories(root());
         return new Directories(root().toFile());
      } catch (IOException var1) {
         throw new IllegalStateException(var1);
      }
   }

   public static boolean locallySaved(Roast var0) {
      return var0.isSynced()
         || var0.getLot() != null && var0.getLot().getId() != null && Files.isRegularFile(root().resolve("roasts").resolve(var0.getLot().getId() + ".json"));
   }

   public static double availableForBatch(Object var0, Lot var1, Unit var2) {
      if (var1 == null) {
         return 0.0;
      } else if (var1.getWeight() == null) {
         return Double.POSITIVE_INFINITY;
      } else {
         return var2 == null ? var1.getWeight().getAmount() : var1.getWeight().toUnit(var2).getAmount();
      }
   }

   public static boolean any(Map<?, Boolean> var0) {
      return var0.values().stream().anyMatch(Boolean.TRUE::equals);
   }

   public static Measure inventoryWeight(Lot var0) {
      return var0.getWeight() == null ? new Measure(0.0, Unit.KG) : var0.getWeight();
   }

   public static void setInventoryWeight(Lot var0, Measure var1) {
      if (var0.getWeight() != null) {
         var0.setWeight(var1);
      }
   }

   public static void start(JavaFxController var0) {
      try {
         Field var1 = JavaFxController.class.getDeclaredField("application");
         var1.setAccessible(true);
         RoastingIntelligenceApp var2 = (RoastingIntelligenceApp)var1.get(var0);
         var2.getStage().setOnCloseRequest(var1x -> {
            var1x.consume();

            try {
               Method var2x = JavaFxController.class.getDeclaredMethod("doClose");
               var2x.setAccessible(true);
               var2x.invoke(var0);
            } catch (Exception var3x) {
               throw new RuntimeException(var3x);
            }
         });
         var0.proceedLogin();
         Field var3 = JavaFxController.class.getDeclaredField("mainPanelController");
         var3.setAccessible(true);
         MainPanelController var4 = (MainPanelController)var3.get(var0);
         Field var5 = JavaFxController.class.getDeclaredField("appModel");
         var5.setAccessible(true);
         var4.populateProductionPanel(((AppModel)var5.get(var0)).getRecentRoasts());
         var4.getSyncButton().setDisable(true);
         var4.getSyncButton().setTooltip(new Tooltip("Offline: batches are saved locally; cloud synchronization is disabled."));
         var4.getSupportButton().setDisable(true);
         var2.getStage().setTitle("roastMane — Local Offline");
         if (Boolean.getBoolean("offline.smoke")) {
            Platform.runLater(() -> SmokeProbe.run(var0));
         }
      } catch (Exception var6) {
         throw new IllegalStateException("Offline startup failed", var6);
      }
   }
}
