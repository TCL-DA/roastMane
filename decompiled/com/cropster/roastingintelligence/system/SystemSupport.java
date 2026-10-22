package com.cropster.roastingintelligence.system;

import java.io.IOException;
import java.util.Map;

public interface SystemSupport {
   String PROPERTY_UPDATE_VERSION = "update-version";
   String PROPERTY_UPDATE_CALLBACK = "update-callback";
   String PROPERTY_UPDATE_DOWNLOADED = "update-downloaded";

   Map<String, Object> checkForUpdate() throws IOException;

   void restart() throws IOException;
}
