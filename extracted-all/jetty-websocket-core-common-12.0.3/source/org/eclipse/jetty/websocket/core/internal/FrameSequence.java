package org.eclipse.jetty.websocket.core.internal;

import org.eclipse.jetty.util.thread.AutoLock;
import org.eclipse.jetty.websocket.core.OpCode;
import org.eclipse.jetty.websocket.core.exception.ProtocolException;

public class FrameSequence {
   private final AutoLock lock = new AutoLock();
   private byte state = -1;

   public void check(byte opcode, boolean fin) throws ProtocolException {
      AutoLock l = this.lock.lock();

      try {
         if (this.state == 8) {
            throw new ProtocolException(OpCode.name(opcode) + " after CLOSE");
         }

         switch (opcode) {
            case -1:
               throw new ProtocolException("UNDEFINED OpCode: " + OpCode.name(opcode));
            case 0:
               if (this.state == -1) {
                  throw new ProtocolException("CONTINUATION after fin==true");
               }

               if (fin) {
                  this.state = -1;
               }
               break;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            default:
               if (this.state != -1) {
                  throw new ProtocolException("DataFrame before fin==true");
               }

               if (!fin) {
                  this.state = opcode;
               }
               break;
            case 8:
               this.state = 8;
            case 9:
            case 10:
         }
      } catch (Throwable var7) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (l != null) {
         l.close();
      }
   }
}
