package org.eclipse.jetty.server.handler;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Locale;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.Connection.Listener;
import org.eclipse.jetty.server.AbstractConnector;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.DateCache;
import org.eclipse.jetty.util.RolloverFileOutputStream;

public class DebugHandler extends Handler.Wrapper implements Listener {
   private final DateCache _date = new DateCache("HH:mm:ss", Locale.US);
   private OutputStream _out;
   private PrintStream _print;

   public DebugHandler() {
      this(null);
   }

   public DebugHandler(Handler handler) {
      super(handler);
   }

   @Override
   public boolean handle(Request request, Response response, Callback callback) throws Exception {
      Thread thread = Thread.currentThread();
      String name = thread.getName() + ":" + request.getHttpURI();
      String ex = null;

      boolean x;
      try {
         this.print(
            name,
            "REQUEST "
               + Request.getRemoteAddr(request)
               + " "
               + request.getMethod()
               + " "
               + request.getHeaders().get("Cookie")
               + "; "
               + request.getHeaders().get("User-Agent")
         );
         thread.setName(name);
         x = this.getHandler().handle(request, response, callback);
      } catch (Throwable var11) {
         ex = var11 + ":" + var11.getCause();
         throw var11;
      } finally {
         this.print(name, "RESPONSE " + response.getStatus() + (ex == null ? "" : "/" + ex) + " " + response.getHeaders().get(HttpHeader.CONTENT_TYPE));
      }

      return x;
   }

   private void print(String name, String message) {
      long now = System.currentTimeMillis();
      String d = this._date.format(now);
      int ms = (int)(now % 1000L);
      this._print.println(d + (ms > 99 ? "." : (ms > 9 ? ".0" : ".00")) + ms + ":" + name + " " + message);
   }

   @Override
   protected void doStart() throws Exception {
      if (this._out == null) {
         this._out = new RolloverFileOutputStream("./logs/yyyy_mm_dd.debug.log", true);
      }

      this._print = new PrintStream(this._out);

      for (Connector connector : this.getServer().getConnectors()) {
         if (connector instanceof AbstractConnector) {
            ((AbstractConnector)connector).addBean(this, false);
         }
      }

      super.doStart();
   }

   @Override
   protected void doStop() throws Exception {
      super.doStop();
      this._print.close();

      for (Connector connector : this.getServer().getConnectors()) {
         if (connector instanceof AbstractConnector) {
            ((AbstractConnector)connector).removeBean(this);
         }
      }
   }

   public OutputStream getOutputStream() {
      return this._out;
   }

   public void setOutputStream(OutputStream out) {
      this._out = out;
   }

   public void onOpened(Connection connection) {
      this.print(Thread.currentThread().getName(), "OPENED " + connection.toString());
   }

   public void onClosed(Connection connection) {
      this.print(Thread.currentThread().getName(), "CLOSED " + connection.toString());
   }
}
