package org.apache.commons.io.input;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;

public class DemuxInputStream extends InputStream {
   private final InheritableThreadLocal<InputStream> inputStreamLocal = new InheritableThreadLocal<>();

   public InputStream bindStream(InputStream input) {
      InputStream oldValue = this.inputStreamLocal.get();
      this.inputStreamLocal.set(input);
      return oldValue;
   }

   @Override
   public void close() throws IOException {
      IOUtils.close(this.inputStreamLocal.get());
   }

   @Override
   public int read() throws IOException {
      InputStream inputStream = this.inputStreamLocal.get();
      return null != inputStream ? inputStream.read() : -1;
   }
}
