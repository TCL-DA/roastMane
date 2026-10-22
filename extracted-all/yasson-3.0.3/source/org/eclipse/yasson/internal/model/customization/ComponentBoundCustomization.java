package org.eclipse.yasson.internal.model.customization;

import org.eclipse.yasson.internal.components.AdapterBinding;
import org.eclipse.yasson.internal.components.DeserializerBinding;
import org.eclipse.yasson.internal.components.SerializerBinding;

public interface ComponentBoundCustomization {
   AdapterBinding getSerializeAdapterBinding();

   AdapterBinding getDeserializeAdapterBinding();

   SerializerBinding<?> getSerializerBinding();

   DeserializerBinding<?> getDeserializerBinding();
}
