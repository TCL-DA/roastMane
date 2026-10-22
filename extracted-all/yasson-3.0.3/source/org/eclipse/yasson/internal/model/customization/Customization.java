package org.eclipse.yasson.internal.model.customization;

import org.eclipse.yasson.internal.JsonbDateFormatter;
import org.eclipse.yasson.internal.JsonbNumberFormatter;

public interface Customization {
   JsonbNumberFormatter getSerializeNumberFormatter();

   JsonbNumberFormatter getDeserializeNumberFormatter();

   JsonbDateFormatter getSerializeDateFormatter();

   JsonbDateFormatter getDeserializeDateFormatter();

   boolean isNillable();
}
