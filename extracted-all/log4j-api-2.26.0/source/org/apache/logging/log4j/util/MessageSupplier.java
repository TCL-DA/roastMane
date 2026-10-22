package org.apache.logging.log4j.util;

import org.apache.logging.log4j.message.Message;

@FunctionalInterface
public interface MessageSupplier extends Supplier<Message> {
   Message get();
}
