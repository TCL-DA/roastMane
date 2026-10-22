package org.cropster.roastingintelligence;

import java.io.Console;
import java.util.List;
import org.cropster.roastingintelligence.listener.MessageListener;
import org.cropster.roastingintelligence.remote.CsarConnection;

public class ConsoleController implements ApplicationController {
   private final AppModel _appModel;
   private final CsarConnection _connection;
   private final Console _console;
   private Runnable _shutdownHook;

   public ConsoleController(AppModel appModel, CsarConnection connection) {
      this._appModel = appModel;
      this._connection = connection;
      this._console = System.console();
   }

   @Override
   public void setShutdownHook(Runnable shutdownHook) {
      this._shutdownHook = shutdownHook;
   }

   @Override
   public void start(List<String> setupWarnings) throws Throwable {
      if (!setupWarnings.isEmpty()) {
         this._console.printf("WARNING\n");

         for (String warning : setupWarnings) {
            this._console.printf(warning + "\n");
         }

         this._console.printf("\n");
      }

      this._console.printf("Please select:\n");
      this._console.printf("1. Start roasting\n");
      this._console.printf("2. Quit\n");

      while (true) {
         String line = this._console.readLine().trim();
         System.out.println("You chose " + line);
         if (line.matches("1.*")) {
            this.startRoasting();
         } else {
            if (line.matches("2.*")) {
               this.close();
               return;
            }

            this._console.printf("Wrong input!");
         }
      }
   }

   @Override
   public boolean startRoasting() {
      return true;
   }

   @Override
   public void stopRoasting() {
   }

   @Override
   public void finishRoasting() {
   }

   @Override
   public void close() {
      try {
         this._connection.close();
      } catch (Exception var2) {
         System.err.println("Error closing connection");
         var2.printStackTrace();
      }

      if (this._shutdownHook != null) {
         this._shutdownHook.run();
      }
   }

   @Override
   public void publishMessage(MessageListener.MessageType type, String message) {
      if (!MessageListener.MessageType.SUCCESS.equals(type)
         && !MessageListener.MessageType.INFO.equals(type)
         && !MessageListener.MessageType.TRACE.equals(type)) {
         System.err.println(message);
      } else {
         System.out.println(message);
      }
   }
}
