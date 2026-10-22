package org.eclipse.yasson.internal.serializer;

import jakarta.json.stream.JsonGenerator;
import org.eclipse.yasson.internal.SerializationContextImpl;

public interface ModelSerializer {
   void serialize(Object var1, JsonGenerator var2, SerializationContextImpl var3);
}
