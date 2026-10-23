package local;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.components.TaskLabel;
import com.cropster.util.model.Unit;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.WorkerTask;

public final class RecoveredUiTest {
   private RecoveredUiTest() {
   }

   public static void main(String[] args) throws Exception {
      require(args.length == 1, "Expected rebuilt application JAR path");
      Path expectedJar = Path.of(args[0]).toRealPath();
      Path loadedJar = Path.of(TaskLabel.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toRealPath();
      require(loadedJar.equals(expectedJar), "TaskLabel loaded from " + loadedJar + ", expected " + expectedJar);
      require(loadedJar.toString().replace('\\', '/').endsWith("/app/roastingintelligence-5.13.jar"), "Unexpected CodeSource layout");

      CountDownLatch finished = new CountDownLatch(1);
      AtomicReference<Throwable> failure = new AtomicReference<>();
      Platform.startup(() -> {
         try {
            testTaskLabel();
            testLengthFormatter(() -> finished.countDown(), failure);
         } catch (Throwable t) {
            failure.set(t);
            finished.countDown();
         }
      });
      require(finished.await(30, TimeUnit.SECONDS), "JavaFX test timed out");
      Platform.exit();
      if (failure.get() != null) {
         throw new AssertionError("Recovered UI regression failed", failure.get());
      }
      System.out.println("PASS RecoveredUiTest CodeSource=" + loadedJar);
   }

   private static void testTaskLabel() throws Exception {
      TaskLabel label = new TaskLabel(event -> {}, event -> {});

      WorkerTask volume = task(WorkerTask.TaskFrequency.ROAST_VOLUME, new Measure(10, Unit.KG), new Measure(-2, Unit.KG));
      label.init(volume);
      require(text(label, "frequencyLabel").contains("10"), "ROAST_VOLUME frequency missing threshold");
      require(text(label, "overdueCountLabel").startsWith("+"), "ROAST_VOLUME overdue value must be absolute and prefixed");

      volume.setDueProductionValue(new Measure(0, Unit.KG));
      label.init(volume);
      require(text(label, "overdueCountLabel") == null, "Zero production debt must clear a previous overdue label");
      volume.setDueProductionValue(null);
      label.init(volume);
      require(text(label, "overdueCountLabel") == null, "Null production debt must remain unlabeled");

      WorkerTask duration = task(WorkerTask.TaskFrequency.ROAST_DURATION, new Measure(300, Unit.SECOND), new Measure(65, Unit.SECOND));
      label.init(duration);
      require(text(label, "frequencyLabel").contains("300"), "ROAST_DURATION frequency missing threshold");
      require(text(label, "overdueCountLabel").startsWith("+"), "ROAST_DURATION overdue label missing");

      WorkerTask number = task(WorkerTask.TaskFrequency.ROAST_NUMBER, new Measure(3, Unit.KG), new Measure(-2, Unit.KG));
      label.init(number);
      require(text(label, "frequencyLabel").contains("3"), "ROAST_NUMBER frequency missing count");
      require(text(label, "overdueCountLabel").startsWith("+2"), "ROAST_NUMBER overdue count incorrect");

      WorkerTask oneTime = new WorkerTask(2L, "One time", new Date(0), false);
      oneTime.setFrequency(WorkerTask.TaskFrequency.ONE_TIME);
      setField(oneTime, "overdueDays", 2);
      label.init(oneTime);
      require(text(label, "dateLabel") != null, "ONE_TIME date missing");
      require(text(label, "frequencyLabel") == null, "ONE_TIME must not show a recurring frequency");
      require(text(label, "overdueCountLabel").startsWith("+2"), "ONE_TIME overdue days incorrect");
   }

   private static void testLengthFormatter(Runnable done, AtomicReference<Throwable> failure) {
      TextField input = new TextField();
      TextFormatter<String> formatter = JavaFxUtil.createLengthLimitingTextFormatter(4);
      input.setTextFormatter(formatter);
      Stage stage = new Stage(StageStyle.UTILITY);
      stage.setScene(new Scene(new StackPane(input), 120, 40));
      stage.setX(-10000);
      stage.setY(-10000);
      stage.show();
      input.requestFocus();
      Platform.runLater(() -> {
         try {
            require(input.isFocused(), "Formatter focus fixture did not acquire focus");
            input.replaceText(0, 0, "abcdef");
            require("abcd".equals(input.getText()), "Focused input was not limited to four characters");
         } catch (Throwable t) {
            failure.set(t);
         } finally {
            stage.close();
            done.run();
         }
      });
   }

   private static WorkerTask task(WorkerTask.TaskFrequency frequency, Measure threshold, Measure due) {
      WorkerTask task = new WorkerTask(1L, frequency.name(), null, false);
      task.setFrequency(frequency);
      task.setProductionThreshold(threshold);
      task.setDueProductionValue(due);
      return task;
   }

   private static String text(TaskLabel label, String name) throws Exception {
      return ((Label)getField(label, name)).getText();
   }

   private static Object getField(Object target, String name) throws Exception {
      Field field = target.getClass().getDeclaredField(name);
      field.setAccessible(true);
      return field.get(target);
   }

   private static void setField(Object target, String name, Object value) throws Exception {
      Field field = target.getClass().getDeclaredField(name);
      field.setAccessible(true);
      field.set(target, value);
   }

   private static void require(boolean condition, String message) {
      if (!condition) {
         throw new AssertionError(message);
      }
   }
}
