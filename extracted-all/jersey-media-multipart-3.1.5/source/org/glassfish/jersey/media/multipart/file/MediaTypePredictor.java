package org.glassfish.jersey.media.multipart.file;

import jakarta.ws.rs.core.MediaType;
import java.io.File;

public interface MediaTypePredictor {
   MediaType getMediaTypeFromFile(File var1);

   MediaType getMediaTypeFromFileName(String var1);
}
