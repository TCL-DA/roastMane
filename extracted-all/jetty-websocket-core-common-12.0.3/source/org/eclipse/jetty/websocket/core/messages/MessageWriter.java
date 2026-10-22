package org.eclipse.jetty.websocket.core.messages;

import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.websocket.core.CoreSession;

public class MessageWriter extends Writer {
   private final MessageOutputStream outputStream;
   private final CharsetEncoder utf8Encoder = StandardCharsets.UTF_8
      .newEncoder()
      .onUnmappableCharacter(CodingErrorAction.REPORT)
      .onMalformedInput(CodingErrorAction.REPORT);

   public MessageWriter(CoreSession coreSession, ByteBufferPool bufferPool) {
      this.outputStream = new MessageOutputStream(coreSession, bufferPool);
      this.outputStream.setMessageType((byte)1);
   }

   @Override
   public void write(char[] cbuf, int off, int len) throws IOException {
      CharBuffer charBuffer = CharBuffer.wrap(cbuf, off, len);
      this.outputStream.write(this.utf8Encoder.encode(charBuffer));
   }

   @Override
   public void flush() throws IOException {
      this.outputStream.flush();
   }

   @Override
   public void close() throws IOException {
      this.outputStream.close();
   }

   public void setCallback(Callback callback) {
      this.outputStream.setCallback(callback);
   }
}
