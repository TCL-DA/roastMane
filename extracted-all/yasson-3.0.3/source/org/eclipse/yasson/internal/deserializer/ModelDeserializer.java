package org.eclipse.yasson.internal.deserializer;

import org.eclipse.yasson.internal.DeserializationContextImpl;

public interface ModelDeserializer<T> {
   Object deserialize(T var1, DeserializationContextImpl var2);
}
