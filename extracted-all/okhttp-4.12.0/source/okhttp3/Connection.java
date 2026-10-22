package okhttp3

import java.net.Socket

interface Connection {
   public abstract fun route(): Route {
   }

   public abstract fun socket(): Socket {
   }

   public abstract fun handshake(): Handshake? {
   }

   public abstract fun protocol(): Protocol {
   }
}
