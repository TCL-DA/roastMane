package com.lmax.disruptor;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

class SequenceGroups {
   static <T> void addSequences(T holder, AtomicReferenceFieldUpdater<T, Sequence[]> updater, Cursored cursor, Sequence... sequencesToAdd) {
      Sequence[] updatedSequences;
      Sequence[] currentSequences;
      do {
         currentSequences = updater.get(holder);
         updatedSequences = Arrays.copyOf(currentSequences, currentSequences.length + sequencesToAdd.length);
         long cursorSequence = cursor.getCursor();
         int index = currentSequences.length;

         for (Sequence sequence : sequencesToAdd) {
            sequence.set(cursorSequence);
            updatedSequences[index++] = sequence;
         }
      } while (!updater.compareAndSet(holder, currentSequences, updatedSequences));

      long var13 = cursor.getCursor();

      for (Sequence sequence : sequencesToAdd) {
         sequence.set(var13);
      }
   }

   static <T> boolean removeSequence(T holder, AtomicReferenceFieldUpdater<T, Sequence[]> sequenceUpdater, Sequence sequence) {
      while (true) {
         Sequence[] oldSequences = sequenceUpdater.get(holder);
         int numToRemove = countMatching(oldSequences, sequence);
         if (0 != numToRemove) {
            int oldSize = oldSequences.length;
            Sequence[] newSequences = new Sequence[oldSize - numToRemove];
            int i = 0;

            for (int pos = 0; i < oldSize; i++) {
               Sequence testSequence = oldSequences[i];
               if (sequence != testSequence) {
                  newSequences[pos++] = testSequence;
               }
            }

            if (!sequenceUpdater.compareAndSet(holder, oldSequences, newSequences)) {
               continue;
            }
         }

         return numToRemove != 0;
      }
   }

   private static <T> int countMatching(T[] values, T toMatch) {
      int numToRemove = 0;

      for (T value : values) {
         if (value == toMatch) {
            numToRemove++;
         }
      }

      return numToRemove;
   }
}
