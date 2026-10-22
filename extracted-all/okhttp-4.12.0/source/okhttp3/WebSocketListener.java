package okhttp3

import okio.ByteString

abstract class WebSocketListener {
   public open fun onOpen(webSocket: WebSocket, response: Response) {
   }

   public open fun onMessage(webSocket: WebSocket, text: String) {
   }

   public open fun onMessage(webSocket: WebSocket, bytes: ByteString) {
   }

   public open fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
   }

   public open fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
   }

   public open fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
   }
}
