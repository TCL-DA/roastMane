package kotlin

class NotImplementedError(message: String) : Error(message) {
   fun NotImplementedError() {
      this(null, 1, null);
   }
}
