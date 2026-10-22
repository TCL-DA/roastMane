package org.eclipse.yasson.internal.jsonstructure;

import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import java.math.BigDecimal;
import java.math.BigInteger;

abstract class JsonStructureBuilder {
   abstract JsonStructure build();

   abstract void put(JsonStructure var1);

   abstract void write(JsonValue var1);

   abstract void write(String var1);

   abstract void write(BigDecimal var1);

   abstract void write(BigInteger var1);

   abstract void write(int var1);

   abstract void write(long var1);

   abstract void write(double var1);

   abstract void write(boolean var1);

   abstract void writeNull();
}
