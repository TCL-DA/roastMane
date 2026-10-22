package org.cropster.roastingintelligence;

import java.io.Closeable;
import java.util.List;
import org.cropster.roastingintelligence.listener.MessageListener;

public interface ApplicationController extends Closeable, MessageListener {
   void start(List<String> var1) throws Throwable;

   boolean startRoasting();

   void stopRoasting();

   void finishRoasting();

   @Override
   void close();

   void setShutdownHook(Runnable var1);
}
