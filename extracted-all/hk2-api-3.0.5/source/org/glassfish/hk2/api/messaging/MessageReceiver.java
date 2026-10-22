package org.glassfish.hk2.api.messaging;

import jakarta.inject.Qualifier;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.glassfish.hk2.api.Metadata;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface MessageReceiver {
   String EVENT_RECEIVER_TYPES = "org.glassfish.hk2.messaging.messageReceiverTypes";

   @Metadata("org.glassfish.hk2.messaging.messageReceiverTypes")
   Class<?>[] value() default {};
}
