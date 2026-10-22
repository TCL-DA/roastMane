package org.eclipse.jetty.websocket.core.messages;

import java.io.IOException;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.websocket.core.CoreSession;
import org.eclipse.jetty.websocket.core.Frame;

public class MessageReader extends Reader implements MessageSink {
   private final ByteBuffer buffer;
   private final MessageInputStream stream;
   private final CharsetDecoder utf8Decoder = StandardCharsets.UTF_8
      .newDecoder()
      .onUnmappableCharacter(CodingErrorAction.REPORT)
      .onMalformedInput(CodingErrorAction.REPORT);

   public MessageReader(CoreSession coreSession) {
      this.stream = new MessageInputStream(coreSession);
      this.buffer = BufferUtil.allocate(coreSession.getInputBufferSize());
   }

   @Override
   public int read(char[] chars, int off, int len) throws IOException {
      CharBuffer charBuffer = CharBuffer.wrap(chars, off, len);

      boolean eof;
      int read;
      do {
         read = this.stream.read(this.buffer);
         eof = read < 0;
      } while (!eof && read != 0);

      CoderResult result = this.utf8Decoder.decode(this.buffer, charBuffer, eof);
      if (result.isError()) {
         result.throwException();
      }

      return eof && charBuffer.position() == off ? -1 : charBuffer.position() - off;
   }

   @Override
   public void fail(Throwable failure) {
      this.stream.fail(failure);
   }

   @Override
   public void close() throws IOException {
      this.stream.close();
   }

   @Override
   public void accept(Frame frame, Callback callback) {
      this.stream.accept(frame, callback);
   }
}
