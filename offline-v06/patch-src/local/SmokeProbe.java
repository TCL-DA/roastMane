package local;
import java.nio.file.*;import java.util.*;import java.net.*;
import org.cropster.roastingintelligence.*;import org.cropster.roastingintelligence.model.*;import com.cropster.util.model.Unit;import com.cropster.roastingintelligence.ui.JavaFxController;import javafx.scene.Node;import javafx.scene.Parent;import javafx.scene.control.ComboBox;import javafx.scene.control.Labeled;import javafx.scene.control.TextInputControl;import javafx.scene.text.Text;
public final class SmokeProbe {
 public static void run(JavaFxController controller){try{
  if(!OfflineBootstrap.root().toString().contains("Temp"))throw new IllegalStateException("Smoke test requires a temporary data root");
  var mf=JavaFxController.class.getDeclaredField("appModel");mf.setAccessible(true);AppModel model=(AppModel)mf.get(controller);
  var af=JavaFxController.class.getDeclaredField("application");af.setAccessible(true);RoastingIntelligenceApp app=(RoastingIntelligenceApp)af.get(controller);
  String title=app.getStage().getTitle();if(!"roastMane — Local Offline".equals(title))throw new AssertionError("Wrong window title: "+title);
  List<String> visibleText=new ArrayList<>();collectVisibleText(app.getStage().getScene().getRoot(),visibleText);
  long legacyVisible=visibleText.stream().map(s->s.toLowerCase(Locale.ROOT)).filter(s->s.contains("cropster")||s.contains("roasting intelligence")).count();
  if(legacyVisible!=0)throw new AssertionError("Legacy branding remains in visible text: "+visibleText);
  if(model.getData()!=OfflineBootstrap.data())throw new AssertionError("Two catalogs");
  if(!model.getDirectories().dataDirectory().toPath().equals(OfflineBootstrap.root()))throw new AssertionError("Data root mismatch");
  var cf=JavaFxController.class.getDeclaredField("connection");cf.setAccessible(true);if(!(cf.get(controller) instanceof OfflineConnection))throw new AssertionError("Not a local connection");
  var pf=JavaFxController.class.getDeclaredField("mainPanelController");pf.setAccessible(true);var main=(com.cropster.roastingintelligence.ui.layout.MainPanelController)pf.get(controller);var prep=main.getPreparationPanel();
  var lf=prep.getClass().getDeclaredField("lotCombos");lf.setAccessible(true);for(Object obj:(List<?>)lf.get(prep)){((ComboBox<?>)obj).getSelectionModel().select(0);}prep.setStartWeight(6.0);
  var components=prep.getLotComponents();double input=components.values().stream().mapToDouble(m->m.toUnit(Unit.KG).getAmount()).sum();if(Math.abs(input-6)>0.001)throw new AssertionError("Wrong source weight: "+input);
  boolean blocked=false;try(var socket=new Socket("example.invalid",443)){}catch(SecurityException expected){blocked=true;}if(!blocked)throw new AssertionError("Network not blocked");
  int previous=model.getRoastPersister().loadRecentRoasts(100).size();Roast r=new Roast();r.setMachine(1L);r.setProfile(1L);r.setNotes("SIMULATED QA batch, not hardware data");long now=System.currentTimeMillis();r.setStartDate(new Date(now));r.setEndDate(new Date(now+2000));r.setStartWeight(new Measure(6,Unit.KG));r.setEndWeight(new Measure(5.1,Unit.KG));r.getLot().getSources().add(new LotTransaction(1L,new Measure(6,Unit.KG)));Curve curve=new Curve(Unit.CELSIUS);curve.put(0L,180.0);curve.put(1000L,185.0);curve.put(2000L,190.0);r.getCurves().put("beanTemperature",curve);
  var file=model.getRoastPersister().saveNew(r);OfflinePersistence.verifySaved(r);var loaded=model.getRoastPersister().load(file);if(loaded.getCurves().get("beanTemperature").size()!=3)throw new AssertionError("Lost curve");
  String report="PASS title=roastMane visibleLegacyBranding="+legacyVisible+" catalogIdentity=true rootUnified=true batchInputKg="+input+" networkBlocked="+blocked+" previousBatches="+previous+" savedId="+r.getLot().getId()+" points=3\n";
  Files.writeString(OfflineBootstrap.root().resolve("smoke-result.txt"),report);System.out.println(report);
 }catch(Throwable e){try{Files.writeString(OfflineBootstrap.root().resolve("smoke-failure.txt"),e.toString());}catch(Exception ignored){}throw new RuntimeException(e);}}
 private static void collectVisibleText(Node node,List<String> values){
  if(!node.isVisible())return;
  String value=null;if(node instanceof Labeled labeled)value=labeled.getText();else if(node instanceof Text text)value=text.getText();else if(node instanceof TextInputControl input)value=input.getText();
  if(value!=null&&!value.isBlank())values.add(value);
  if(node instanceof Parent parent)for(Node child:parent.getChildrenUnmodifiable())collectVisibleText(child,values);
 }
}
