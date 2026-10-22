package org.controlsfx.control.action;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import javafx.event.ActionEvent;

public class AnnotatedAction extends Action {
   private final Method method;
   private final WeakReference<Object> target;

   public AnnotatedAction(String text, Method method, Object target) {
      super(text);
      this.setEventHandler(this::handleAction);
      this.method = method;
      this.method.setAccessible(true);
      this.target = new WeakReference<>(target);
   }

   public Object getTarget() {
      return this.target.get();
   }

   protected void handleAction(ActionEvent ae) {
      try {
         Object actionTarget = this.getTarget();
         if (actionTarget == null) {
            throw new IllegalStateException("Action target object is no longer reachable");
         }

         int paramCount = this.method.getParameterCount();
         if (paramCount == 0) {
            this.method.invoke(actionTarget);
         } else if (paramCount == 1) {
            this.method.invoke(actionTarget, ae);
         } else if (paramCount == 2) {
            this.method.invoke(actionTarget, ae, this);
         }
      } catch (Throwable var4) {
         this.handleActionException(ae, var4);
      }
   }

   protected void handleActionException(ActionEvent ae, Throwable ex) {
      ex.printStackTrace();
   }

   @Override
   public String toString() {
      return this.getText();
   }
}
