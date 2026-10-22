package kotlin.collections.builders

import java.io.Externalizable
import java.io.InvalidObjectException
import java.io.ObjectInput
import java.io.ObjectOutput
import java.util.Map.Entry

private class SerializedMap(map: Map<*, *>) : Externalizable {
   @JvmStatic
   public SerializedMap.Companion Companion = new SerializedMap.Companion(null);
   @JvmStatic
   private long serialVersionUID = 0L;

   private final var map: Map<*, *>

   init {
      this.map = map;
   }

   public constructor() : this(MapsKt.emptyMap())
   public override fun writeExternal(output: ObjectOutput) {
      output.writeByte(0);
      output.writeInt(this.map.size());

      for (Entry entry : this.map.entrySet()) {
         output.writeObject(entry.getKey());
         output.writeObject(entry.getValue());
      }
   }

   public override fun readExternal(input: ObjectInput) {
      var flags: Int = input.readByte();
      if (flags != 0) {
         throw new InvalidObjectException("Unsupported flags value: " + flags);
      } else {
         var size: Int = input.readInt();
         if (size < 0) {
            throw new InvalidObjectException("Illegal size value: " + size + '.');
         } else {
            var var4: java.util.Map = MapsKt.createMapBuilder(size);
            var `$this$readExternal_u24lambda_u241`: java.util.Map = var4;

            for (int var7 = 0; var7 < size; var7++) {
               `$this$readExternal_u24lambda_u241`.put(input.readObject(), input.readObject());
            }

            this.map = MapsKt.build(var4);
         }
      }
   }

   private fun readResolve(): Any {
      return this.map;
   }

   companion object Companion private constructor() {
      private const val serialVersionUID: Long

   }
}
