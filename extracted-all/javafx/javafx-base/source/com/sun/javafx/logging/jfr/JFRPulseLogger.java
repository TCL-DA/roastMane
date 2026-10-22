package com.sun.javafx.logging.jfr;

import com.sun.javafx.logging.Logger;
import com.sun.javafx.logging.PulseLogger;
import jdk.jfr.FlightRecorder;

public final class JFRPulseLogger extends Logger {
   private final ThreadLocal<JFRPulsePhaseEvent> currentPulsePhaseEvent;
   private final ThreadLocal<JFRInputEvent> currentInputEvent;
   private int pulseNumber;
   private int fxPulseNumber;
   private int renderPulseNumber;
   private Thread fxThread;

   public static Logger createInstance() {
      return !FlightRecorder.isInitialized() && !PulseLogger.isPulseLoggingRequested() ? null : new JFRPulseLogger();
   }

   private JFRPulseLogger() {
      FlightRecorder.register(JFRInputEvent.class);
      FlightRecorder.register(JFRPulsePhaseEvent.class);
      this.currentPulsePhaseEvent = new ThreadLocal<JFRPulsePhaseEvent>() {
         public JFRPulsePhaseEvent initialValue() {
            return new JFRPulsePhaseEvent();
         }
      };
      this.currentInputEvent = new ThreadLocal<JFRInputEvent>() {
         public JFRInputEvent initialValue() {
            return new JFRInputEvent();
         }
      };
   }

   @Override
   public void pulseStart() {
      this.pulseNumber++;
      this.fxPulseNumber = this.pulseNumber;
      if (this.fxThread == null) {
         this.fxThread = Thread.currentThread();
      }

      this.newPhase("Pulse start");
   }

   @Override
   public void pulseEnd() {
      this.newPhase(null);
      this.fxPulseNumber = 0;
   }

   @Override
   public void renderStart() {
      this.renderPulseNumber = this.fxPulseNumber;
   }

   @Override
   public void renderEnd() {
      this.newPhase(null);
      this.renderPulseNumber = 0;
   }

   @Override
   public void newPhase(String var1) {
      JFRPulsePhaseEvent var2 = this.currentPulsePhaseEvent.get();
      if (!var2.isEnabled()) {
         var2.setPhaseName(null);
      } else {
         if (var2.getPhaseName() != null) {
            var2.commit();
         }

         if (var1 == null) {
            var2.setPhaseName(null);
         } else {
            var2 = new JFRPulsePhaseEvent();
            var2.begin();
            var2.setPhaseName(var1);
            var2.setPulseId(Thread.currentThread() == this.fxThread ? this.fxPulseNumber : this.renderPulseNumber);
            this.currentPulsePhaseEvent.set(var2);
         }
      }
   }

   @Override
   public void newInput(String var1) {
      JFRInputEvent var2 = this.currentInputEvent.get();
      if (!var2.isEnabled()) {
         var2.setInput(null);
      } else {
         if (var2.getInput() != null) {
            var2.commit();
         }

         if (var1 == null) {
            var2.setInput(null);
         } else {
            var2 = new JFRInputEvent();
            var2.begin();
            var2.setInput(var1);
            this.currentInputEvent.set(var2);
         }
      }
   }
}
