package org.cropster.roastingintelligence;

import org.cropster.roastingintelligence.listener.MessageListener;
import org.cropster.roastingintelligence.remote.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VersionChecker implements Runnable {
   private final Logger logger = LoggerFactory.getLogger(VersionChecker.class);
   private final MessageListener _messageListener;
   private final Version _version;
   private Version _lastestVersion;
   private Version _recommendedVersion;
   private Version _requiredVersion;

   public VersionChecker(MessageListener messageListener, Version version) {
      this._messageListener = messageListener;
      this._version = version;
   }

   @Override
   public void run() {
   }
}
