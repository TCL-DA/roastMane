package local;

import com.cropster.roastingintelligence.ui.JavaFxController;
import com.cropster.roastingintelligence.ui.layout.MainPanelController;
import com.cropster.roastingintelligence.ui.layout.PreparationPanel;
import com.cropster.util.model.Unit;
import java.io.File;
import java.lang.reflect.Field;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Labeled;
import javafx.scene.control.TextInputControl;
import javafx.scene.text.Text;
import org.cropster.roastingintelligence.AppModel;
import org.cropster.roastingintelligence.RoastingIntelligenceApp;
import org.cropster.roastingintelligence.model.Curve;
import org.cropster.roastingintelligence.model.LotTransaction;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.Roast;

public final class SmokeProbe {
   public static void run(JavaFxController var0) {
      try {
         if (!OfflineBootstrap.root().toString().contains("Temp")) {
            throw new IllegalStateException("Smoke test requires a temporary data root");
         } else {
            Field var29 = JavaFxController.class.getDeclaredField("appModel");
            var29.setAccessible(true);
            AppModel var2 = (AppModel)var29.get(var0);
            Field var3 = JavaFxController.class.getDeclaredField("application");
            var3.setAccessible(true);
            RoastingIntelligenceApp var4 = (RoastingIntelligenceApp)var3.get(var0);
            String var5 = var4.getStage().getTitle();
            if (!"roastMane — Local Offline".equals(var5)) {
               throw new AssertionError("Wrong window title: " + var5);
            } else {
               ArrayList var6 = new ArrayList();
               collectVisibleText(var4.getStage().getScene().getRoot(), var6);
               long var7 = var6.stream()
                  .map(var0x -> var0x.toLowerCase(Locale.ROOT))
                  .filter(var0x -> var0x.contains("cropster") || var0x.contains("roasting intelligence"))
                  .count();
               if (var7 != 0L) {
                  throw new AssertionError("Legacy branding remains in visible text: " + var6);
               } else if (var2.getData() != OfflineBootstrap.data()) {
                  throw new AssertionError("Two catalogs");
               } else if (!var2.getDirectories().dataDirectory().toPath().equals(OfflineBootstrap.root())) {
                  throw new AssertionError("Data root mismatch");
               } else {
                  Field var9 = JavaFxController.class.getDeclaredField("connection");
                  var9.setAccessible(true);
                  if (!(var9.get(var0) instanceof OfflineConnection)) {
                     throw new AssertionError("Not a local connection");
                  } else {
                     Field var10 = JavaFxController.class.getDeclaredField("mainPanelController");
                     var10.setAccessible(true);
                     MainPanelController var11 = (MainPanelController)var10.get(var0);
                     PreparationPanel var12 = var11.getPreparationPanel();
                     Field var13 = var12.getClass().getDeclaredField("lotCombos");
                     var13.setAccessible(true);

                     for (Object var15 : (List)var13.get(var12)) {
                        ((ComboBox)var15).getSelectionModel().select(0);
                     }

                     var12.setStartWeight(6.0);
                     Map var30 = var12.getLotComponents();
                     double var31 = var30.values().stream().mapToDouble(var0x -> var0x.toUnit(Unit.KG).getAmount()).sum();
                     if (Math.abs(var31 - 6.0) > 0.001) {
                        throw new AssertionError("Wrong source weight: " + var31);
                     } else {
                        boolean var17 = false;

                        try {
                           Socket var18 = new Socket("example.invalid", 443);
                           var18.close();
                        } catch (SecurityException var27) {
                           var17 = true;
                        }

                        if (!var17) {
                           throw new AssertionError("Network not blocked");
                        } else {
                           int var32 = var2.getRoastPersister().loadRecentRoasts(100).size();
                           Roast var19 = new Roast();
                           var19.setMachine(1L);
                           var19.setProfile(1L);
                           var19.setNotes("SIMULATED QA batch, not hardware data");
                           long var20 = System.currentTimeMillis();
                           var19.setStartDate(new Date(var20));
                           var19.setEndDate(new Date(var20 + 2000L));
                           var19.setStartWeight(new Measure(6.0, Unit.KG));
                           var19.setEndWeight(new Measure(5.1, Unit.KG));
                           var19.getLot().getSources().add(new LotTransaction(1L, new Measure(6.0, Unit.KG)));
                           Curve var22 = new Curve(Unit.CELSIUS);
                           var22.put(0L, 180.0);
                           var22.put(1000L, 185.0);
                           var22.put(2000L, 190.0);
                           var19.getCurves().put("beanTemperature", var22);
                           File var23 = var2.getRoastPersister().saveNew(var19);
                           OfflinePersistence.verifySaved(var19);
                           Roast var24 = var2.getRoastPersister().load(var23);
                           if (((Curve)var24.getCurves().get("beanTemperature")).size() != 3) {
                              throw new AssertionError("Lost curve");
                           } else {
                              String var25 = "PASS title=roastMane visibleLegacyBranding="
                                 + var7
                                 + " catalogIdentity=true rootUnified=true batchInputKg="
                                 + var31
                                 + " networkBlocked="
                                 + var17
                                 + " previousBatches="
                                 + var32
                                 + " savedId="
                                 + var19.getLot().getId()
                                 + " points=3\n";
                              Files.writeString(OfflineBootstrap.root().resolve("smoke-result.txt"), var25);
                              System.out.println(var25);
                           }
                        }
                     }
                  }
               }
            }
         }
      } catch (Throwable var28) {
         Throwable var1 = var28;

         try {
            Files.writeString(OfflineBootstrap.root().resolve("smoke-failure.txt"), var1.toString());
         } catch (Exception var26) {
         }

         throw new RuntimeException(var28);
      }
   }

   private static void collectVisibleText(Node var0, List<String> var1) {
      if (var0.isVisible()) {
         String var2 = null;
         if (var0 instanceof Labeled var3) {
            var2 = var3.getText();
         } else if (var0 instanceof Text var4) {
            var2 = var4.getText();
         } else if (var0 instanceof TextInputControl var5) {
            var2 = var5.getText();
         }

         if (var2 != null && !var2.isBlank()) {
            var1.add(var2);
         }

         if (var0 instanceof Parent var6) {
            for (Node var8 : var6.getChildrenUnmodifiable()) {
               collectVisibleText(var8, var1);
            }
         }
      }
   }
}
