package org.eclipse.jetty.websocket.core;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

public final class WebSocketConstants {
   public static final int SPEC_VERSION = 13;
   public static final String SPEC_VERSION_STRING = Integer.toString(13);
   public static final int DEFAULT_MAX_BINARY_MESSAGE_SIZE = 65536;
   public static final int DEFAULT_MAX_TEXT_MESSAGE_SIZE = 65536;
   public static final int DEFAULT_MAX_FRAME_SIZE = 65536;
   public static final int DEFAULT_INPUT_BUFFER_SIZE = 4096;
   public static final int DEFAULT_OUTPUT_BUFFER_SIZE = 4096;
   public static final int DEFAULT_MAX_OUTGOING_FRAMES = -1;
   public static final boolean DEFAULT_AUTO_FRAGMENT = true;
   public static final Duration DEFAULT_IDLE_TIMEOUT = Duration.ofSeconds(30L);
   public static final Duration DEFAULT_WRITE_TIMEOUT = Duration.ZERO;
   public static final String WEBSOCKET_WRAPPED_REQUEST_ATTRIBUTE = "org.eclipse.jetty.websocket.wrappedRequest";
   public static final String WEBSOCKET_WRAPPED_RESPONSE_ATTRIBUTE = "org.eclipse.jetty.websocket.wrappedResponse";
   public static final byte[] MAGIC = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11".getBytes(StandardCharsets.ISO_8859_1);

   private WebSocketConstants() {
   }
}
