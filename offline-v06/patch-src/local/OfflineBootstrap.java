package local;
import com.cropster.roastingintelligence.Data;
import com.cropster.util.model.Unit;
import org.cropster.roastingintelligence.*;
import org.cropster.roastingintelligence.remote.Version;
import org.cropster.roastingintelligence.model.*;
import org.cropster.roastingintelligence.measurement.*;
import org.cropster.roastingintelligence.measurement.impl.GenericModbusRtuSourceFactory;
import org.cropster.roastingintelligence.service.impl.JsonReader;
import org.cropster.roastingintelligence.service.impl.JsonWriter;
import com.cropster.roastingintelligence.ui.JavaFxController;
import java.nio.file.*;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import javafx.application.Platform;
import javafx.stage.WindowEvent;
public final class OfflineBootstrap {
 private static Data catalog;
 public static Path root(){ return Paths.get(System.getProperty("offline.data",System.getProperty("user.home")+"/roastMane/otl-offline")); }
 public static synchronized Data data(){
  if(catalog!=null)return catalog;
  try {
   Files.createDirectories(root()); Path file=root().resolve("data.json");
   if(Files.exists(file)){try(var r=Files.newBufferedReader(file)){catalog=new JsonReader().readData(r);}return catalog;}
   Data d=new Data(); Group g=new Group("LOCAL","Local offline workspace");
   Machine m=new Machine(1L,"roasting","DRUM","OTL - local Modbus RTU",g);
   m.setLocation(new Location(1L,"Local"));m.setChannels(new String[]{"beanTemperature","exhaustTemperature"});m.setComments(new String[]{"CHARGE","DRY_END","FIRST_CRACK","DROP"});
   d.setMachines(Map.of(1L,m));d.setWorkers(List.of("Local operator"));
   Lot l=new Lot(1L,"Local coffee - describe the actual coffee in batch notes");l.setIdTag("LOCAL-GREEN");l.setProject(new Project(1L,"Local coffee"));l.setLocation(m.getLocation());
   // Không gán tồn kho giả; null biểu thị không quản lý tồn kho.
   d.setLots(Map.of(1L,l));
   Profile p=new Profile(1L,"Local roast");BlendComponent c=new BlendComponent();c.setId(1L);c.setName("Coffee");c.setPercentage(new BigDecimal("100"));c.setLotIds(List.of(1L));p.setComponents(new LinkedHashSet<>(List.of(c)));d.setProfiles(Map.of(1L,p));
   d.setGroupMembers(Map.of("LOCAL",new GroupMember("LOCAL","local",List.of(GroupMember.MODULE_RI),List.of(GroupMember.PERMISSION_RI))));
   catalog=d;
   try(var out=Files.newBufferedWriter(file,StandardOpenOption.CREATE_NEW)){new JsonWriter(new Version("5.13")).write(out,d);}
   return d;
  }catch(Exception e){throw new IllegalStateException("Unable to load/save local catalog",e);}
 }
 public static void configure(AppModel model){
  model.setRefreshToken(null);model.setRememberMe(false);model.setUser("Local operator");model.setUserName("local");model.setData(data());
  var prefs=model.getPreferences();if(model.getSelectedMachine()==null)model.setSelectedMachine(data().getMachines().get(1L));
  if(model.getSelectedProfile()==null)model.setSelectedProfile(data().getProfiles().get(1L));
  prefs.setCurvePredictionEnabled(false);prefs.setFirstCrackPredictionEnabled(false);prefs.setFlickPredictionEnabled(false);
  if(prefs.getChannels().stream().noneMatch(c->c.getSourceFactory()!=null)){
   List<Channel> channels=new ArrayList<>();for(String name:List.of("beanTemperature","exhaustTemperature")){Channel c=new Channel(name);c.setSourceFactory(null);c.setProbeType("Default");c.setRorEnabled(true);c.setColor(name.equals("beanTemperature")?java.awt.Color.ORANGE:java.awt.Color.CYAN);channels.add(c);}prefs.setChannels(channels);
   Set<SourceParameter> ps=new LinkedHashSet<>();
   for(String[] p:new String[][]{{"encoding","rtu"},{"baudRate","9600"},{"dataBits","8"},{"parity","None"},{"stopBits","1"},{"modbusFunction","3"},{"temperatureUnit","CELSIUS"},{"storageFormatTemperature","DECIMAL1_16"},{"beanTemperatureSlave","1"},{"beanTemperatureRegister","0"},{"exhaustTemperatureSlave","1"},{"exhaustTemperatureRegister","1"}})ps.add(new SourceParameter(p[0],String.class,p[1]));
   // Chưa chọn cổng: không tự mở kết nối phần cứng.
   prefs.setSourceConfigurations(Map.of("GenericModbusRtuSourceFactory",ps));
  }
 }
 public static com.cropster.roastingintelligence.config.FileSystemConfiguration.Directories directories(){try{Files.createDirectories(root());return new com.cropster.roastingintelligence.config.FileSystemConfiguration.Directories(root().toFile());}catch(IOException e){throw new IllegalStateException(e);}}
 public static boolean locallySaved(Roast roast){return roast.isSynced()||(roast.getLot()!=null&&roast.getLot().getId()!=null&&Files.isRegularFile(root().resolve("roasts").resolve(roast.getLot().getId()+".json")));}
 public static double availableForBatch(Object panel,Lot lot,Unit unit){if(lot==null)return 0;if(lot.getWeight()==null)return Double.POSITIVE_INFINITY;return unit==null?lot.getWeight().getAmount():lot.getWeight().toUnit(unit).getAmount();}
 public static boolean any(java.util.Map<?,Boolean> map){return map.values().stream().anyMatch(Boolean.TRUE::equals);}
 public static Measure inventoryWeight(Lot lot){return lot.getWeight()==null?new Measure(0,Unit.KG):lot.getWeight();}
 public static void setInventoryWeight(Lot lot,Measure weight){if(lot.getWeight()!=null)lot.setWeight(weight);}
 public static void start(JavaFxController controller){
  try{
   var af=JavaFxController.class.getDeclaredField("application");af.setAccessible(true);RoastingIntelligenceApp app=(RoastingIntelligenceApp)af.get(controller);
   app.getStage().setOnCloseRequest(e->{e.consume();try{var close=JavaFxController.class.getDeclaredMethod("doClose");close.setAccessible(true);close.invoke(controller);}catch(Exception ex){throw new RuntimeException(ex);}});
   controller.proceedLogin();
   var mp=JavaFxController.class.getDeclaredField("mainPanelController");mp.setAccessible(true);var panel=(com.cropster.roastingintelligence.ui.layout.MainPanelController)mp.get(controller);
   var modelField=JavaFxController.class.getDeclaredField("appModel");modelField.setAccessible(true);
   panel.populateProductionPanel(((AppModel)modelField.get(controller)).getRecentRoasts());
   panel.getSyncButton().setDisable(true);panel.getSyncButton().setTooltip(new javafx.scene.control.Tooltip("Offline: batches are saved locally; cloud synchronization is disabled."));panel.getSupportButton().setDisable(true);
   app.getStage().setTitle("roastMane — Local Offline");
   if(Boolean.getBoolean("offline.smoke"))Platform.runLater(()->SmokeProbe.run(controller));
  }catch(Exception e){throw new IllegalStateException("Offline startup failed",e);}
 }
}
