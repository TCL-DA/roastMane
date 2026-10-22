package retrofit2.mock;

import java.io.IOException;

final class MockRetrofitIOException extends IOException {
   MockRetrofitIOException() {
      super("Failure triggered by MockRetrofit's NetworkBehavior");
   }
}
