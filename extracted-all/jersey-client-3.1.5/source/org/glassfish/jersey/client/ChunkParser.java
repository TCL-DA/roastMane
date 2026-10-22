package org.glassfish.jersey.client;

import java.io.IOException;
import java.io.InputStream;

public interface ChunkParser {
   byte[] readChunk(InputStream var1) throws IOException;
}
