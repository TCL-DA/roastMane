package org.eclipse.jetty.server.handler.gzip;

import java.util.zip.Deflater;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.compression.CompressionPool;

public interface GzipFactory {
   CompressionPool<Deflater>.Entry getDeflaterEntry(Request var1, long var2);

   boolean isMimeTypeDeflatable(String var1);
}
