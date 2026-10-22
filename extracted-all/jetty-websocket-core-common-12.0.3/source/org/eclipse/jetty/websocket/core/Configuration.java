package org.eclipse.jetty.websocket.core;

import java.time.Duration;

public interface Configuration {
   Duration getIdleTimeout();

   Duration getWriteTimeout();

   void setIdleTimeout(Duration var1);

   void setWriteTimeout(Duration var1);

   boolean isAutoFragment();

   void setAutoFragment(boolean var1);

   long getMaxFrameSize();

   void setMaxFrameSize(long var1);

   int getOutputBufferSize();

   void setOutputBufferSize(int var1);

   int getInputBufferSize();

   void setInputBufferSize(int var1);

   long getMaxBinaryMessageSize();

   void setMaxBinaryMessageSize(long var1);

   long getMaxTextMessageSize();

   void setMaxTextMessageSize(long var1);

   int getMaxOutgoingFrames();

   void setMaxOutgoingFrames(int var1);

   public static class ConfigurationCustomizer implements Configuration, Configuration.Customizer {
      private Duration idleTimeout;
      private Duration writeTimeout;
      private Boolean autoFragment;
      private Long maxFrameSize;
      private Integer outputBufferSize;
      private Integer inputBufferSize;
      private Long maxBinaryMessageSize;
      private Long maxTextMessageSize;
      private Integer maxOutgoingFrames;

      @Override
      public Duration getIdleTimeout() {
         return this.idleTimeout == null ? WebSocketConstants.DEFAULT_IDLE_TIMEOUT : this.idleTimeout;
      }

      @Override
      public Duration getWriteTimeout() {
         return this.writeTimeout == null ? WebSocketConstants.DEFAULT_WRITE_TIMEOUT : this.writeTimeout;
      }

      @Override
      public void setIdleTimeout(Duration timeout) {
         this.idleTimeout = timeout;
      }

      @Override
      public void setWriteTimeout(Duration timeout) {
         this.writeTimeout = timeout;
      }

      @Override
      public boolean isAutoFragment() {
         return this.autoFragment == null ? true : this.autoFragment;
      }

      @Override
      public void setAutoFragment(boolean autoFragment) {
         this.autoFragment = autoFragment;
      }

      @Override
      public long getMaxFrameSize() {
         return this.maxFrameSize == null ? 65536L : this.maxFrameSize;
      }

      @Override
      public void setMaxFrameSize(long maxFrameSize) {
         this.maxFrameSize = maxFrameSize;
      }

      @Override
      public int getOutputBufferSize() {
         return this.outputBufferSize == null ? 4096 : this.outputBufferSize;
      }

      @Override
      public void setOutputBufferSize(int outputBufferSize) {
         this.outputBufferSize = outputBufferSize;
      }

      @Override
      public int getInputBufferSize() {
         return this.inputBufferSize == null ? 4096 : this.inputBufferSize;
      }

      @Override
      public void setInputBufferSize(int inputBufferSize) {
         this.inputBufferSize = inputBufferSize;
      }

      @Override
      public long getMaxBinaryMessageSize() {
         return this.maxBinaryMessageSize == null ? 65536L : this.maxBinaryMessageSize;
      }

      @Override
      public void setMaxBinaryMessageSize(long maxBinaryMessageSize) {
         this.maxBinaryMessageSize = maxBinaryMessageSize;
      }

      @Override
      public long getMaxTextMessageSize() {
         return this.maxTextMessageSize == null ? 65536L : this.maxTextMessageSize;
      }

      @Override
      public void setMaxTextMessageSize(long maxTextMessageSize) {
         this.maxTextMessageSize = maxTextMessageSize;
      }

      @Override
      public int getMaxOutgoingFrames() {
         return this.maxOutgoingFrames == null ? -1 : this.maxOutgoingFrames;
      }

      @Override
      public void setMaxOutgoingFrames(int maxOutgoingFrames) {
         this.maxOutgoingFrames = maxOutgoingFrames;
      }

      @Override
      public void customize(Configuration configurable) {
         if (this.idleTimeout != null) {
            configurable.setIdleTimeout(this.idleTimeout);
         }

         if (this.writeTimeout != null) {
            configurable.setWriteTimeout(this.writeTimeout);
         }

         if (this.autoFragment != null) {
            configurable.setAutoFragment(this.autoFragment);
         }

         if (this.maxFrameSize != null) {
            configurable.setMaxFrameSize(this.maxFrameSize);
         }

         if (this.inputBufferSize != null) {
            configurable.setInputBufferSize(this.inputBufferSize);
         }

         if (this.outputBufferSize != null) {
            configurable.setOutputBufferSize(this.outputBufferSize);
         }

         if (this.maxBinaryMessageSize != null) {
            configurable.setMaxBinaryMessageSize(this.maxBinaryMessageSize);
         }

         if (this.maxTextMessageSize != null) {
            configurable.setMaxTextMessageSize(this.maxTextMessageSize);
         }

         if (this.maxOutgoingFrames != null) {
            configurable.setMaxOutgoingFrames(this.maxOutgoingFrames);
         }
      }

      @Override
      public String toString() {
         return String.format(
            "%s@%x{idleTimeout=%s, writeTimeout=%s, autoFragment=%s, maxFrameSize=%s, inputBufferSize=%s, outputBufferSize=%s, maxBinaryMessageSize=%s, maxTextMessageSize=%s, maxOutgoingFrames=%s}",
            this.getClass().getSimpleName(),
            this.hashCode(),
            this.idleTimeout,
            this.writeTimeout,
            this.autoFragment,
            this.maxFrameSize,
            this.inputBufferSize,
            this.outputBufferSize,
            this.maxBinaryMessageSize,
            this.maxTextMessageSize,
            this.maxOutgoingFrames
         );
      }
   }

   public interface Customizer {
      void customize(Configuration var1);
   }
}
