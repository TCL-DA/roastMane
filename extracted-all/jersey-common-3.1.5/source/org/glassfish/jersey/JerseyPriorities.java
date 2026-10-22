package org.glassfish.jersey;

import jakarta.annotation.Priority;

public class JerseyPriorities {
   public static final int POST_ENTITY_CODER = 4100;

   private JerseyPriorities() {
   }

   public static int getPriorityValue(Class<?> prioritized, int defaultValue) {
      Priority priority = prioritized.getAnnotation(Priority.class);
      return priority != null ? priority.value() : defaultValue;
   }
}
