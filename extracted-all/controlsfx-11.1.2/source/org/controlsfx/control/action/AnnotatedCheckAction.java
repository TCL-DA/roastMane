package org.controlsfx.control.action;

import java.lang.reflect.Method;

@ActionCheck
public class AnnotatedCheckAction extends AnnotatedAction {
   public AnnotatedCheckAction(String text, Method method, Object target) {
      super(text, method, target);
   }
}
