package org.apache.commons.math3.stat;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.MathUtils;

public class Frequency implements Serializable {
   private static final long serialVersionUID = -3845586908418844111L;
   private final SortedMap<Comparable<?>, Long> freqTable;

   public Frequency() {
      this.freqTable = new TreeMap<>();
   }

   public Frequency(Comparator<?> comparator) {
      this.freqTable = new TreeMap<>((Comparator<? super Comparable<?>>)comparator);
   }

   @Override
   public String toString() {
      NumberFormat nf = NumberFormat.getPercentInstance();
      StringBuilder outBuffer = new StringBuilder();
      outBuffer.append("Value \t Freq. \t Pct. \t Cum Pct. \n");

      for (Comparable<?> value : this.freqTable.keySet()) {
         outBuffer.append(value);
         outBuffer.append('\t');
         outBuffer.append(this.getCount(value));
         outBuffer.append('\t');
         outBuffer.append(nf.format(this.getPct(value)));
         outBuffer.append('\t');
         outBuffer.append(nf.format(this.getCumPct(value)));
         outBuffer.append('\n');
      }

      return outBuffer.toString();
   }

   public void addValue(Comparable<?> v) throws MathIllegalArgumentException {
      this.incrementValue(v, 1L);
   }

   public void addValue(int v) throws MathIllegalArgumentException {
      this.addValue(Long.valueOf((long)v));
   }

   public void addValue(long v) throws MathIllegalArgumentException {
      this.addValue(Long.valueOf(v));
   }

   public void addValue(char v) throws MathIllegalArgumentException {
      this.addValue(Character.valueOf(v));
   }

   public void incrementValue(Comparable<?> v, long increment) throws MathIllegalArgumentException {
      Comparable<?> obj = v;
      if (v instanceof Integer) {
         obj = ((Integer)v).longValue();
      }

      try {
         Long count = this.freqTable.get(obj);
         if (count == null) {
            this.freqTable.put(obj, Long.valueOf(increment));
         } else {
            this.freqTable.put(obj, Long.valueOf(count + increment));
         }
      } catch (ClassCastException var6) {
         throw new MathIllegalArgumentException(LocalizedFormats.INSTANCES_NOT_COMPARABLE_TO_EXISTING_VALUES, v.getClass().getName());
      }
   }

   public void incrementValue(int v, long increment) throws MathIllegalArgumentException {
      this.incrementValue(Long.valueOf((long)v), increment);
   }

   public void incrementValue(long v, long increment) throws MathIllegalArgumentException {
      this.incrementValue(Long.valueOf(v), increment);
   }

   public void incrementValue(char v, long increment) throws MathIllegalArgumentException {
      this.incrementValue(Character.valueOf(v), increment);
   }

   public void clear() {
      this.freqTable.clear();
   }

   public Iterator<Comparable<?>> valuesIterator() {
      return this.freqTable.keySet().iterator();
   }

   public Iterator<Entry<Comparable<?>, Long>> entrySetIterator() {
      return this.freqTable.entrySet().iterator();
   }

   public long getSumFreq() {
      long result = 0L;
      Iterator<Long> iterator = this.freqTable.values().iterator();

      while (iterator.hasNext()) {
         result += iterator.next();
      }

      return result;
   }

   public long getCount(Comparable<?> v) {
      if (v instanceof Integer) {
         return this.getCount(((Integer)v).longValue());
      } else {
         long result = 0L;

         try {
            Long count = this.freqTable.get(v);
            if (count != null) {
               result = count;
            }
         } catch (ClassCastException var5) {
         }

         return result;
      }
   }

   public long getCount(int v) {
      return this.getCount(Long.valueOf((long)v));
   }

   public long getCount(long v) {
      return this.getCount(Long.valueOf(v));
   }

   public long getCount(char v) {
      return this.getCount(Character.valueOf(v));
   }

   public int getUniqueCount() {
      return this.freqTable.keySet().size();
   }

   public double getPct(Comparable<?> v) {
      long sumFreq = this.getSumFreq();
      return sumFreq == 0L ? Double.NaN : (double)this.getCount(v) / (double)sumFreq;
   }

   public double getPct(int v) {
      return this.getPct(Long.valueOf((long)v));
   }

   public double getPct(long v) {
      return this.getPct(Long.valueOf(v));
   }

   public double getPct(char v) {
      return this.getPct(Character.valueOf(v));
   }

   public long getCumFreq(Comparable<?> v) {
      if (this.getSumFreq() == 0L) {
         return 0L;
      } else if (v instanceof Integer) {
         return this.getCumFreq(((Integer)v).longValue());
      } else {
         Comparator<Comparable<?>> c = (Comparator<Comparable<?>>)this.freqTable.comparator();
         if (c == null) {
            c = new Frequency.NaturalComparator();
         }

         long result = 0L;

         try {
            Long value = this.freqTable.get(v);
            if (value != null) {
               result = value;
            }
         } catch (ClassCastException var7) {
            return result;
         }

         if (c.compare(v, this.freqTable.firstKey()) < 0) {
            return 0L;
         } else if (c.compare(v, this.freqTable.lastKey()) >= 0) {
            return this.getSumFreq();
         } else {
            Iterator<Comparable<?>> values = this.valuesIterator();

            while (values.hasNext()) {
               Comparable<?> nextValue = values.next();
               if (c.compare(v, nextValue) <= 0) {
                  return result;
               }

               result += this.getCount(nextValue);
            }

            return result;
         }
      }
   }

   public long getCumFreq(int v) {
      return this.getCumFreq(Long.valueOf((long)v));
   }

   public long getCumFreq(long v) {
      return this.getCumFreq(Long.valueOf(v));
   }

   public long getCumFreq(char v) {
      return this.getCumFreq(Character.valueOf(v));
   }

   public double getCumPct(Comparable<?> v) {
      long sumFreq = this.getSumFreq();
      return sumFreq == 0L ? Double.NaN : (double)this.getCumFreq(v) / (double)sumFreq;
   }

   public double getCumPct(int v) {
      return this.getCumPct(Long.valueOf((long)v));
   }

   public double getCumPct(long v) {
      return this.getCumPct(Long.valueOf(v));
   }

   public double getCumPct(char v) {
      return this.getCumPct(Character.valueOf(v));
   }

   public List<Comparable<?>> getMode() {
      long mostPopular = 0L;

      for (Long l : this.freqTable.values()) {
         long frequency = l;
         if (frequency > mostPopular) {
            mostPopular = frequency;
         }
      }

      List<Comparable<?>> modeList = new ArrayList<>();

      for (Entry<Comparable<?>, Long> ent : this.freqTable.entrySet()) {
         long frequency = ent.getValue();
         if (frequency == mostPopular) {
            modeList.add(ent.getKey());
         }
      }

      return modeList;
   }

   public void merge(Frequency other) throws NullArgumentException {
      MathUtils.checkNotNull(other, LocalizedFormats.NULL_NOT_ALLOWED);
      Iterator<Entry<Comparable<?>, Long>> iter = other.entrySetIterator();

      while (iter.hasNext()) {
         Entry<Comparable<?>, Long> entry = iter.next();
         this.incrementValue(entry.getKey(), entry.getValue());
      }
   }

   public void merge(Collection<Frequency> others) throws NullArgumentException {
      MathUtils.checkNotNull(others, LocalizedFormats.NULL_NOT_ALLOWED);

      for (Frequency freq : others) {
         this.merge(freq);
      }
   }

   @Override
   public int hashCode() {
      int prime = 31;
      int result = 1;
      return 31 * result + (this.freqTable == null ? 0 : this.freqTable.hashCode());
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof Frequency)) {
         return false;
      } else {
         Frequency other = (Frequency)obj;
         if (this.freqTable == null) {
            if (other.freqTable != null) {
               return false;
            }
         } else if (!this.freqTable.equals(other.freqTable)) {
            return false;
         }

         return true;
      }
   }

   private static class NaturalComparator<T extends Comparable<T>> implements Comparator<Comparable<T>>, Serializable {
      private static final long serialVersionUID = -3852193713161395148L;

      private NaturalComparator() {
      }

      public int compare(Comparable<T> o1, Comparable<T> o2) {
         return o1.compareTo((T)o2);
      }
   }
}
