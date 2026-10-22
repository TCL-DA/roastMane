package com.nimbusds.jose;

import com.nimbusds.jose.util.JSONStringUtils;
import java.io.Serializable;
import net.jcip.annotations.Immutable;

@Immutable
public final class CompressionAlgorithm implements Serializable {
   private static final long serialVersionUID = 1L;
   public static final CompressionAlgorithm DEF = new CompressionAlgorithm("DEF");
   private final String name;

   public CompressionAlgorithm(String name) {
      if (name == null) {
         throw new IllegalArgumentException("The compression algorithm name must not be null");
      } else {
         this.name = name;
      }
   }

   public String getName() {
      return this.name;
   }

   @Override
   public int hashCode() {
      return this.name.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      return object instanceof CompressionAlgorithm && this.toString().equals(object.toString());
   }

   @Override
   public String toString() {
      return this.name;
   }

   public String toJSONString() {
      return JSONStringUtils.toJSONString(this.name);
   }
}
