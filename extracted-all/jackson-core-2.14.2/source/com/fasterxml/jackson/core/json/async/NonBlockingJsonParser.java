package com.fasterxml.jackson.core.json.async;

import com.fasterxml.jackson.core.async.ByteArrayFeeder;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.sym.ByteQuadsCanonicalizer;
import java.io.IOException;
import java.io.OutputStream;

public class NonBlockingJsonParser extends NonBlockingUtf8JsonParserBase implements ByteArrayFeeder {
   private byte[] _inputBuffer = NO_BYTES;

   public NonBlockingJsonParser(IOContext ctxt, int parserFeatures, ByteQuadsCanonicalizer sym) {
      super(ctxt, parserFeatures, sym);
   }

   public ByteArrayFeeder getNonBlockingInputFeeder() {
      return this;
   }

   @Override
   public void feedInput(byte[] buf, int start, int end) throws IOException {
      if (this._inputPtr < this._inputEnd) {
         this._reportError("Still have %d undecoded bytes, should not call 'feedInput'", Integer.valueOf(this._inputEnd - this._inputPtr));
      }

      if (end < start) {
         this._reportError("Input end (%d) may not be before start (%d)", Integer.valueOf(end), Integer.valueOf(start));
      }

      if (this._endOfInput) {
         this._reportError("Already closed, can not feed more input");
      }

      this._currInputProcessed = this._currInputProcessed + (long)this._origBufferLen;
      this._currInputRowStart = start - (this._inputEnd - this._currInputRowStart);
      this._currBufferStart = start;
      this._inputBuffer = buf;
      this._inputPtr = start;
      this._inputEnd = end;
      this._origBufferLen = end - start;
   }

   @Override
   public int releaseBuffered(OutputStream out) throws IOException {
      int avail = this._inputEnd - this._inputPtr;
      if (avail > 0) {
         out.write(this._inputBuffer, this._inputPtr, avail);
      }

      return avail;
   }

   @Override
   protected byte getNextSignedByteFromBuffer() {
      return this._inputBuffer[this._inputPtr++];
   }

   @Override
   protected int getNextUnsignedByteFromBuffer() {
      return this._inputBuffer[this._inputPtr++] & 0xFF;
   }

   @Override
   protected byte getByteFromBuffer(int ptr) {
      return this._inputBuffer[ptr];
   }
}
