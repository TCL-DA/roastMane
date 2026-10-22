package javafx.animation;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import javafx.beans.NamedArg;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

public final class KeyFrame {
   private static final EventHandler<ActionEvent> DEFAULT_ON_FINISHED = null;
   private static final String DEFAULT_NAME = null;
   private final Duration time;
   private final Set<KeyValue> values;
   private final EventHandler<ActionEvent> onFinished;
   private final String name;

   public Duration getTime() {
      return this.time;
   }

   public Set<KeyValue> getValues() {
      return this.values;
   }

   public EventHandler<ActionEvent> getOnFinished() {
      return this.onFinished;
   }

   public String getName() {
      return this.name;
   }

   public KeyFrame(
      @NamedArg("time") Duration var1,
      @NamedArg("name") String var2,
      @NamedArg("onFinished") EventHandler<ActionEvent> var3,
      @NamedArg("values") Collection<KeyValue> var4
   ) {
      if (var1 == null) {
         throw new NullPointerException("The time has to be specified");
      } else if (!var1.lessThan(Duration.ZERO) && !var1.equals(Duration.UNKNOWN)) {
         this.time = var1;
         this.name = var2;
         if (var4 != null) {
            CopyOnWriteArraySet var5 = new CopyOnWriteArraySet(var4);
            var5.remove(null);
            this.values = var5.size() == 0
               ? Collections.emptySet()
               : (var5.size() == 1 ? Collections.singleton((KeyValue)var5.iterator().next()) : Collections.unmodifiableSet(var5));
         } else {
            this.values = Collections.emptySet();
         }

         this.onFinished = var3;
      } else {
         throw new IllegalArgumentException("The time is invalid.");
      }
   }

   public KeyFrame(
      @NamedArg("time") Duration var1,
      @NamedArg("name") String var2,
      @NamedArg("onFinished") EventHandler<ActionEvent> var3,
      @NamedArg("values") KeyValue... var4
   ) {
      if (var1 == null) {
         throw new NullPointerException("The time has to be specified");
      } else if (!var1.lessThan(Duration.ZERO) && !var1.equals(Duration.UNKNOWN)) {
         this.time = var1;
         this.name = var2;
         if (var4 != null) {
            CopyOnWriteArraySet var5 = new CopyOnWriteArraySet();

            for (KeyValue var9 : var4) {
               if (var9 != null) {
                  var5.add(var9);
               }
            }

            this.values = var5.size() == 0
               ? Collections.emptySet()
               : (var5.size() == 1 ? Collections.singleton((KeyValue)var5.iterator().next()) : Collections.unmodifiableSet(var5));
         } else {
            this.values = Collections.emptySet();
         }

         this.onFinished = var3;
      } else {
         throw new IllegalArgumentException("The time is invalid.");
      }
   }

   public KeyFrame(@NamedArg("time") Duration var1, @NamedArg("onFinished") EventHandler<ActionEvent> var2, @NamedArg("values") KeyValue... var3) {
      this(var1, DEFAULT_NAME, var2, var3);
   }

   public KeyFrame(@NamedArg("time") Duration var1, @NamedArg("name") String var2, @NamedArg("values") KeyValue... var3) {
      this(var1, var2, DEFAULT_ON_FINISHED, var3);
   }

   public KeyFrame(@NamedArg("time") Duration var1, @NamedArg("values") KeyValue... var2) {
      this(var1, DEFAULT_NAME, DEFAULT_ON_FINISHED, var2);
   }

   @Override
   public String toString() {
      return "KeyFrame [time=" + this.time + ", values=" + this.values + ", onFinished=" + this.onFinished + ", name=" + this.name + "]";
   }

   @Override
   public int hashCode() {
      assert this.time != null && this.values != null;

      int var1 = 1;
      var1 = 31 * var1 + this.time.hashCode();
      var1 = 31 * var1 + (this.name == null ? 0 : this.name.hashCode());
      var1 = 31 * var1 + (this.onFinished == null ? 0 : this.onFinished.hashCode());
      return 31 * var1 + this.values.hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof KeyFrame)) {
         return false;
      } else {
         KeyFrame var2 = (KeyFrame)var1;

         assert this.time != null && this.values != null && var2.time != null && var2.values != null;

         return this.time.equals(var2.time)
            && (this.name == null ? var2.name == null : this.name.equals(var2.name))
            && (this.onFinished == null ? var2.onFinished == null : this.onFinished.equals(var2.onFinished))
            && this.values.equals(var2.values);
      }
   }
}
