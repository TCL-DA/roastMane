package com.lmax.disruptor;

public class EventPoller<T> {
   private final DataProvider<T> dataProvider;
   private final Sequencer sequencer;
   private final Sequence sequence;
   private final Sequence gatingSequence;

   public EventPoller(DataProvider<T> dataProvider, Sequencer sequencer, Sequence sequence, Sequence gatingSequence) {
      this.dataProvider = dataProvider;
      this.sequencer = sequencer;
      this.sequence = sequence;
      this.gatingSequence = gatingSequence;
   }

   public EventPoller.PollState poll(EventPoller.Handler<T> eventHandler) throws Exception {
      long currentSequence = this.sequence.get();
      long nextSequence = currentSequence + 1L;
      long availableSequence = this.sequencer.getHighestPublishedSequence(nextSequence, this.gatingSequence.get());
      if (nextSequence > availableSequence) {
         return this.sequencer.getCursor() >= nextSequence ? EventPoller.PollState.GATING : EventPoller.PollState.IDLE;
      } else {
         long processedSequence = currentSequence;

         boolean processNextEvent;
         try {
            do {
               T event = this.dataProvider.get(nextSequence);
               processNextEvent = eventHandler.onEvent(event, nextSequence, nextSequence == availableSequence);
               processedSequence = nextSequence++;
            } while (nextSequence <= availableSequence & processNextEvent);
         } finally {
            this.sequence.set(processedSequence);
         }

         return EventPoller.PollState.PROCESSING;
      }
   }

   public static <T> EventPoller<T> newInstance(
      DataProvider<T> dataProvider, Sequencer sequencer, Sequence sequence, Sequence cursorSequence, Sequence... gatingSequences
   ) {
      Sequence gatingSequence;
      if (gatingSequences.length == 0) {
         gatingSequence = cursorSequence;
      } else if (gatingSequences.length == 1) {
         gatingSequence = gatingSequences[0];
      } else {
         gatingSequence = new FixedSequenceGroup(gatingSequences);
      }

      return new EventPoller<>(dataProvider, sequencer, sequence, gatingSequence);
   }

   public Sequence getSequence() {
      return this.sequence;
   }

   public interface Handler<T> {
      boolean onEvent(T var1, long var2, boolean var4) throws Exception;
   }

   public static enum PollState {
      PROCESSING,
      GATING,
      IDLE;
   }
}
