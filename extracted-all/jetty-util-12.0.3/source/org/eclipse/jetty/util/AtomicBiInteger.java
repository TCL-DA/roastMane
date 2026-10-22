package org.eclipse.jetty.util;

import java.util.concurrent.atomic.AtomicLong;

public class AtomicBiInteger extends AtomicLong {
   public AtomicBiInteger() {
   }

   public AtomicBiInteger(long encoded) {
      super(encoded);
   }

   public AtomicBiInteger(int hi, int lo) {
      super(encode(hi, lo));
   }

   public int getHi() {
      return getHi(this.get());
   }

   public static int getHi(long encoded) {
      return (int)(encoded >> 32 & 4294967295L);
   }

   public int getLo() {
      return getLo(this.get());
   }

   public static int getLo(long encoded) {
      return (int)(encoded & 4294967295L);
   }

   public int getAndSetHi(int hi) {
      long encoded;
      long update;
      do {
         encoded = this.get();
         update = encodeHi(encoded, hi);
      } while (!this.compareAndSet(encoded, update));

      return getHi(encoded);
   }

   public int getAndSetLo(int lo) {
      long encoded;
      long update;
      do {
         encoded = this.get();
         update = encodeLo(encoded, lo);
      } while (!this.compareAndSet(encoded, update));

      return getLo(encoded);
   }

   public void set(int hi, int lo) {
      this.set(encode(hi, lo));
   }

   public boolean compareAndSetHi(int expectHi, int hi) {
      long encoded;
      long update;
      do {
         encoded = this.get();
         if (getHi(encoded) != expectHi) {
            return false;
         }

         update = encodeHi(encoded, hi);
      } while (!this.compareAndSet(encoded, update));

      return true;
   }

   public boolean compareAndSetLo(int expectLo, int lo) {
      long encoded;
      long update;
      do {
         encoded = this.get();
         if (getLo(encoded) != expectLo) {
            return false;
         }

         update = encodeLo(encoded, lo);
      } while (!this.compareAndSet(encoded, update));

      return true;
   }

   public boolean compareAndSet(long encoded, int hi, int lo) {
      long update = encode(hi, lo);
      return this.compareAndSet(encoded, update);
   }

   public boolean compareAndSet(int expectHi, int hi, int expectLo, int lo) {
      long encoded = encode(expectHi, expectLo);
      long update = encode(hi, lo);
      return this.compareAndSet(encoded, update);
   }

   public int addAndGetHi(int delta) {
      long encoded;
      int hi;
      long update;
      do {
         encoded = this.get();
         hi = getHi(encoded) + delta;
         update = encodeHi(encoded, hi);
      } while (!this.compareAndSet(encoded, update));

      return hi;
   }

   public int addAndGetLo(int delta) {
      long encoded;
      int lo;
      long update;
      do {
         encoded = this.get();
         lo = getLo(encoded) + delta;
         update = encodeLo(encoded, lo);
      } while (!this.compareAndSet(encoded, update));

      return lo;
   }

   public void add(int deltaHi, int deltaLo) {
      long encoded;
      long update;
      do {
         encoded = this.get();
         update = encode(getHi(encoded) + deltaHi, getLo(encoded) + deltaLo);
      } while (!this.compareAndSet(encoded, update));
   }

   @Override
   public String toString() {
      long encoded = this.get();
      return getHi(encoded) + "|" + getLo(encoded);
   }

   public static long encode(int hi, int lo) {
      long h = (long)hi & 4294967295L;
      long l = (long)lo & 4294967295L;
      return (h << 32) + l;
   }

   public static long encodeHi(long encoded, int hi) {
      long h = (long)hi & 4294967295L;
      long l = encoded & 4294967295L;
      return (h << 32) + l;
   }

   public static long encodeLo(long encoded, int lo) {
      long h = encoded >> 32 & 4294967295L;
      long l = (long)lo & 4294967295L;
      return (h << 32) + l;
   }
}
