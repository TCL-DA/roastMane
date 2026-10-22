package com.lmax.disruptor;

abstract class SingleProducerSequencerFields extends SingleProducerSequencerPad {
   long nextValue = -1L;
   long cachedValue = -1L;

   SingleProducerSequencerFields(int bufferSize, WaitStrategy waitStrategy) {
      super(bufferSize, waitStrategy);
   }
}
