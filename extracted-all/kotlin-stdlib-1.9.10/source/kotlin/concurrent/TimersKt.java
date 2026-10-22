@file:JvmName(name = "TimersKt")

package kotlin.concurrent

import java.util.Date
import java.util.Timer
import java.util.TimerTask
import kotlin.internal.InlineOnly
import kotlin.jvm.functions.Function1

@InlineOnly
public inline fun Timer.schedule(delay: Long, crossinline action: (TimerTask) -> Unit): TimerTask {
   var task: TimerTask = new TimerTask(action) {
      {
         this.$action = `$action`;
      }

      @Override
      public void run() {
         this.$action.invoke(this);
      }
   };
   `$this$schedule`.schedule(task, delay);
   return task;
}

@InlineOnly
public inline fun Timer.schedule(time: Date, crossinline action: (TimerTask) -> Unit): TimerTask {
   var task: TimerTask = new TimerTask(action) {
      {
         this.$action = `$action`;
      }

      @Override
      public void run() {
         this.$action.invoke(this);
      }
   };
   `$this$schedule`.schedule(task, time);
   return task;
}

@InlineOnly
public inline fun Timer.schedule(delay: Long, period: Long, crossinline action: (TimerTask) -> Unit): TimerTask {
   var task: TimerTask = new TimerTask(action) {
      {
         this.$action = `$action`;
      }

      @Override
      public void run() {
         this.$action.invoke(this);
      }
   };
   `$this$schedule`.schedule(task, delay, period);
   return task;
}

@InlineOnly
public inline fun Timer.schedule(time: Date, period: Long, crossinline action: (TimerTask) -> Unit): TimerTask {
   var task: TimerTask = new TimerTask(action) {
      {
         this.$action = `$action`;
      }

      @Override
      public void run() {
         this.$action.invoke(this);
      }
   };
   `$this$schedule`.schedule(task, time, period);
   return task;
}

@InlineOnly
public inline fun Timer.scheduleAtFixedRate(delay: Long, period: Long, crossinline action: (TimerTask) -> Unit): TimerTask {
   var task: TimerTask = new TimerTask(action) {
      {
         this.$action = `$action`;
      }

      @Override
      public void run() {
         this.$action.invoke(this);
      }
   };
   `$this$scheduleAtFixedRate`.scheduleAtFixedRate(task, delay, period);
   return task;
}

@InlineOnly
public inline fun Timer.scheduleAtFixedRate(time: Date, period: Long, crossinline action: (TimerTask) -> Unit): TimerTask {
   var task: TimerTask = new TimerTask(action) {
      {
         this.$action = `$action`;
      }

      @Override
      public void run() {
         this.$action.invoke(this);
      }
   };
   `$this$scheduleAtFixedRate`.scheduleAtFixedRate(task, time, period);
   return task;
}

@PublishedApi
internal fun timer(name: String?, daemon: Boolean): Timer {
   return if (name == null) new Timer(daemon) else new Timer(name, daemon);
}

@InlineOnly
public inline fun timer(name: String?, daemon: Boolean, initialDelay: Long, period: Long, crossinline action: (TimerTask) -> Unit): Timer {
   var timer: Timer = timer(name, daemon);
   timer.schedule(new TimerTask(action) {
      {
         this.$action = `$action`;
      }

      @Override
      public void run() {
         this.$action.invoke(this);
      }
   }, initialDelay, period);
   return timer;
}

@JvmSynthetic
fun `timer$default`(name: java.lang.String, daemon: Boolean, initialDelay: Long, period: Long, action: Function1, timer: Int, var8: Any): Timer {
   if ((timer and 1) != 0) {
      name = null;
   }

   if ((timer and 2) != 0) {
      daemon = false;
   }

   if ((timer and 4) != 0) {
      initialDelay = 0L;
   }

   var var10: Timer = timer(name, daemon);
   var10.schedule(new TimerTask(action) {
      {
         this.$action = `$action`;
      }

      @Override
      public void run() {
         this.$action.invoke(this);
      }
   }, initialDelay, period);
   return var10;
}

@InlineOnly
public inline fun timer(name: String?, daemon: Boolean, startAt: Date, period: Long, crossinline action: (TimerTask) -> Unit): Timer {
   var timer: Timer = timer(name, daemon);
   timer.schedule(new TimerTask(action) {
      {
         this.$action = `$action`;
      }

      @Override
      public void run() {
         this.$action.invoke(this);
      }
   }, startAt, period);
   return timer;
}

@JvmSynthetic
fun `timer$default`(name: java.lang.String, daemon: Boolean, startAt: Date, period: Long, action: Function1, timer: Int, var7: Any): Timer {
   if ((timer and 1) != 0) {
      name = null;
   }

   if ((timer and 2) != 0) {
      daemon = false;
   }

   var var9: Timer = timer(name, daemon);
   var9.schedule(new TimerTask(action) {
      {
         this.$action = `$action`;
      }

      @Override
      public void run() {
         this.$action.invoke(this);
      }
   }, startAt, period);
   return var9;
}

@InlineOnly
public inline fun fixedRateTimer(name: String?, daemon: Boolean, initialDelay: Long, period: Long, crossinline action: (TimerTask) -> Unit): Timer {
   var timer: Timer = timer(name, daemon);
   timer.scheduleAtFixedRate(new TimerTask(action) {
      {
         this.$action = `$action`;
      }

      @Override
      public void run() {
         this.$action.invoke(this);
      }
   }, initialDelay, period);
   return timer;
}

@JvmSynthetic
fun `fixedRateTimer$default`(name: java.lang.String, daemon: Boolean, initialDelay: Long, period: Long, action: Function1, timer: Int, var8: Any): Timer {
   if ((timer and 1) != 0) {
      name = null;
   }

   if ((timer and 2) != 0) {
      daemon = false;
   }

   if ((timer and 4) != 0) {
      initialDelay = 0L;
   }

   var var10: Timer = timer(name, daemon);
   var10.scheduleAtFixedRate(new TimerTask(action) {
      {
         this.$action = `$action`;
      }

      @Override
      public void run() {
         this.$action.invoke(this);
      }
   }, initialDelay, period);
   return var10;
}

@InlineOnly
public inline fun fixedRateTimer(name: String?, daemon: Boolean, startAt: Date, period: Long, crossinline action: (TimerTask) -> Unit): Timer {
   var timer: Timer = timer(name, daemon);
   timer.scheduleAtFixedRate(new TimerTask(action) {
      {
         this.$action = `$action`;
      }

      @Override
      public void run() {
         this.$action.invoke(this);
      }
   }, startAt, period);
   return timer;
}

@JvmSynthetic
fun `fixedRateTimer$default`(name: java.lang.String, daemon: Boolean, startAt: Date, period: Long, action: Function1, timer: Int, var7: Any): Timer {
   if ((timer and 1) != 0) {
      name = null;
   }

   if ((timer and 2) != 0) {
      daemon = false;
   }

   var var9: Timer = timer(name, daemon);
   var9.scheduleAtFixedRate(new TimerTask(action) {
      {
         this.$action = `$action`;
      }

      @Override
      public void run() {
         this.$action.invoke(this);
      }
   }, startAt, period);
   return var9;
}

@InlineOnly
public inline fun timerTask(crossinline action: (TimerTask) -> Unit): TimerTask {
   return new TimerTask(action) {
      {
         this.$action = `$action`;
      }

      @Override
      public void run() {
         this.$action.invoke(this);
      }
   };
}

