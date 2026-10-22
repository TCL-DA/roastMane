package org.eclipse.jetty.websocket.core.internal;

import java.nio.channels.ClosedChannelException;
import org.eclipse.jetty.util.thread.AutoLock;
import org.eclipse.jetty.websocket.core.CloseStatus;
import org.eclipse.jetty.websocket.core.Frame;
import org.eclipse.jetty.websocket.core.OpCode;
import org.eclipse.jetty.websocket.core.exception.ProtocolException;

public class WebSocketSessionState {
   private final AutoLock lock = new AutoLock();
   private WebSocketSessionState.State _sessionState = WebSocketSessionState.State.CONNECTING;
   private byte _incomingContinuation = -1;
   private byte _outgoingContinuation = -1;
   CloseStatus _closeStatus = null;

   public void onConnected() {
      AutoLock l = this.lock.lock();

      try {
         if (this._sessionState != WebSocketSessionState.State.CONNECTING) {
            throw new IllegalStateException(this._sessionState.toString());
         }

         this._sessionState = WebSocketSessionState.State.CONNECTED;
      } catch (Throwable var5) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (l != null) {
         l.close();
      }
   }

   public void onOpen() {
      AutoLock l = this.lock.lock();

      try {
         switch (this._sessionState) {
            case CONNECTED:
               this._sessionState = WebSocketSessionState.State.OPEN;
               break;
            case OPEN:
            case ISHUT:
            default:
               throw new IllegalStateException(this._sessionState.toString());
            case OSHUT:
            case CLOSED:
         }
      } catch (Throwable var5) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (l != null) {
         l.close();
      }
   }

   private WebSocketSessionState.State getState() {
      AutoLock l = this.lock.lock();

      WebSocketSessionState.State var2;
      try {
         var2 = this._sessionState;
      } catch (Throwable var5) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (l != null) {
         l.close();
      }

      return var2;
   }

   public boolean isClosed() {
      return this.getState() == WebSocketSessionState.State.CLOSED;
   }

   public boolean isInputOpen() {
      WebSocketSessionState.State state = this.getState();
      return state == WebSocketSessionState.State.OPEN || state == WebSocketSessionState.State.OSHUT;
   }

   public boolean isOutputOpen() {
      WebSocketSessionState.State state = this.getState();
      return state == WebSocketSessionState.State.CONNECTED || state == WebSocketSessionState.State.OPEN || state == WebSocketSessionState.State.ISHUT;
   }

   public CloseStatus getCloseStatus() {
      AutoLock l = this.lock.lock();

      CloseStatus var2;
      try {
         var2 = this._closeStatus;
      } catch (Throwable var5) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (l != null) {
         l.close();
      }

      return var2;
   }

   public boolean onClosed(CloseStatus closeStatus) {
      AutoLock l = this.lock.lock();

      boolean var7;
      label43: {
         try {
            if (this._sessionState == WebSocketSessionState.State.CLOSED) {
               var7 = false;
               break label43;
            }

            this._closeStatus = closeStatus;
            this._sessionState = WebSocketSessionState.State.CLOSED;
            var7 = true;
         } catch (Throwable var6) {
            if (l != null) {
               try {
                  l.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (l != null) {
            l.close();
         }

         return var7;
      }

      if (l != null) {
         l.close();
      }

      return var7;
   }

   public void onError(Throwable t) {
      AutoLock l = this.lock.lock();

      try {
         if (this._sessionState != WebSocketSessionState.State.CLOSED || this._closeStatus == null) {
            throw new IllegalArgumentException();
         }

         if (!this._closeStatus.isAbnormal()) {
            this._closeStatus = new CloseStatus(1011, t);
         }

         if (this._closeStatus.getCause() == null) {
            this._closeStatus = new CloseStatus(this._closeStatus.getCode(), this._closeStatus.getReason(), t);
         }
      } catch (Throwable var6) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (l != null) {
         l.close();
      }
   }

   public boolean onEof() {
      AutoLock l = this.lock.lock();

      boolean var6;
      label50: {
         try {
            switch (this._sessionState) {
               case ISHUT:
               case CLOSED:
                  var6 = false;
                  break label50;
            }

            if (this._closeStatus == null || CloseStatus.isOrdinary(this._closeStatus.getCode())) {
               this._closeStatus = new CloseStatus(1006, "Session Closed", new ClosedChannelException());
            }

            this._sessionState = WebSocketSessionState.State.CLOSED;
            var6 = true;
         } catch (Throwable var5) {
            if (l != null) {
               try {
                  l.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (l != null) {
            l.close();
         }

         return var6;
      }

      if (l != null) {
         l.close();
      }

      return var6;
   }

   public boolean onOutgoingFrame(Frame frame) throws Exception {
      byte opcode = frame.getOpCode();
      boolean fin = frame.isFin();
      AutoLock l = this.lock.lock();

      boolean var10;
      label73: {
         label74: {
            label75: {
               try {
                  if (!this.isOutputOpen()) {
                     throw new ClosedChannelException();
                  }

                  if (opcode == 8) {
                     this._closeStatus = CloseStatus.getCloseStatus(frame);
                     if (this._closeStatus.isAbnormal()) {
                        this._sessionState = WebSocketSessionState.State.CLOSED;
                        var10 = true;
                        break label73;
                     }

                     switch (this._sessionState) {
                        case CONNECTED:
                        case OPEN:
                           this._sessionState = WebSocketSessionState.State.OSHUT;
                           var10 = false;
                           break label75;
                        case ISHUT:
                           this._sessionState = WebSocketSessionState.State.CLOSED;
                           var10 = true;
                           break label74;
                        default:
                           throw new IllegalStateException(this._sessionState.toString());
                     }
                  }

                  if (frame.isDataFrame()) {
                     this._outgoingContinuation = checkDataSequence(opcode, fin, this._outgoingContinuation);
                  }
               } catch (Throwable var8) {
                  if (l != null) {
                     try {
                        l.close();
                     } catch (Throwable var7) {
                        var8.addSuppressed(var7);
                     }
                  }

                  throw var8;
               }

               if (l != null) {
                  l.close();
               }

               return false;
            }

            if (l != null) {
               l.close();
            }

            return var10;
         }

         if (l != null) {
            l.close();
         }

         return var10;
      }

      if (l != null) {
         l.close();
      }

      return var10;
   }

   public boolean onIncomingFrame(Frame frame) throws ProtocolException, ClosedChannelException {
      byte opcode = frame.getOpCode();
      boolean fin = frame.isFin();
      AutoLock l = this.lock.lock();

      boolean var5;
      label61: {
         label62: {
            try {
               if (!this.isInputOpen()) {
                  throw new ClosedChannelException();
               }

               if (opcode == 8) {
                  this._closeStatus = CloseStatus.getCloseStatus(frame);
                  switch (this._sessionState) {
                     case OPEN:
                        this._sessionState = WebSocketSessionState.State.ISHUT;
                        var5 = false;
                        break label62;
                     case OSHUT:
                        this._sessionState = WebSocketSessionState.State.CLOSED;
                        var5 = true;
                        break label61;
                     default:
                        throw new IllegalStateException(this._sessionState.toString());
                  }
               }

               if (frame.isDataFrame()) {
                  this._incomingContinuation = checkDataSequence(opcode, fin, this._incomingContinuation);
               }
            } catch (Throwable var8) {
               if (l != null) {
                  try {
                     l.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (l != null) {
               l.close();
            }

            return false;
         }

         if (l != null) {
            l.close();
         }

         return var5;
      }

      if (l != null) {
         l.close();
      }

      return var5;
   }

   @Override
   public String toString() {
      return String.format(
         "%s@%x{%s,i=%s,o=%s,c=%s}",
         this.getClass().getSimpleName(),
         this.hashCode(),
         this._sessionState,
         OpCode.name(this._incomingContinuation),
         OpCode.name(this._outgoingContinuation),
         this._closeStatus
      );
   }

   private static byte checkDataSequence(byte opcode, boolean fin, byte lastOpCode) throws ProtocolException {
      switch (opcode) {
         case 0:
            if (lastOpCode == -1) {
               throw new ProtocolException("CONTINUATION after fin==true");
            } else {
               if (fin) {
                  return -1;
               }

               return lastOpCode;
            }
         case 1:
         case 2:
            if (lastOpCode != -1) {
               throw new ProtocolException("DataFrame before fin==true");
            } else {
               if (!fin) {
                  return opcode;
               }

               return -1;
            }
         default:
            return lastOpCode;
      }
   }

   static enum State {
      CONNECTING,
      CONNECTED,
      OPEN,
      ISHUT,
      OSHUT,
      CLOSED;
   }
}
