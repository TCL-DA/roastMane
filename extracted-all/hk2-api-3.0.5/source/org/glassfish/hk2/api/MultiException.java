package org.glassfish.hk2.api;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

public class MultiException extends HK2RuntimeException {
   private static final long serialVersionUID = 2112432697858621044L;
   private final Object lock = new byte[0];
   private final List<Throwable> throwables = new LinkedList<>();
   private boolean reportToErrorService = true;

   public MultiException() {
   }

   public MultiException(List<Throwable> ths) {
      super(ths.get(0).getMessage(), ths.get(0));

      for (Throwable th : ths) {
         if (th instanceof MultiException) {
            MultiException me = (MultiException)th;
            this.throwables.addAll(me.throwables);
         } else {
            this.throwables.add(th);
         }
      }
   }

   public MultiException(Throwable th, boolean reportToErrorService) {
      super(th.getMessage(), th);
      if (th instanceof MultiException) {
         MultiException me = (MultiException)th;
         this.throwables.addAll(me.throwables);
      } else {
         this.throwables.add(th);
      }

      this.reportToErrorService = reportToErrorService;
   }

   public MultiException(Throwable th) {
      this(th, true);
   }

   public List<Throwable> getErrors() {
      synchronized (this.lock) {
         return new LinkedList<>(this.throwables);
      }
   }

   public void addError(Throwable error) {
      synchronized (this.lock) {
         this.throwables.add(error);
      }
   }

   @Override
   public String getMessage() {
      List<Throwable> listCopy = this.getErrors();
      StringBuffer sb = new StringBuffer("A MultiException has " + listCopy.size() + " exceptions.  They are:\n");
      int lcv = 1;

      for (Throwable th : listCopy) {
         sb.append(lcv++ + ". " + th.getClass().getName() + (th.getMessage() != null ? ": " + th.getMessage() : "") + "\n");
      }

      return sb.toString();
   }

   @Override
   public void printStackTrace(PrintStream s) {
      List<Throwable> listCopy = this.getErrors();
      if (listCopy.size() <= 0) {
         super.printStackTrace(s);
      } else {
         int lcv = 1;

         for (Throwable th : listCopy) {
            s.println("MultiException stack " + lcv++ + " of " + listCopy.size());
            th.printStackTrace(s);
         }
      }
   }

   @Override
   public void printStackTrace(PrintWriter s) {
      List<Throwable> listCopy = this.getErrors();
      if (listCopy.size() <= 0) {
         super.printStackTrace(s);
      } else {
         int lcv = 1;

         for (Throwable th : listCopy) {
            s.println("MultiException stack " + lcv++ + " of " + listCopy.size());
            th.printStackTrace(s);
         }
      }
   }

   public boolean getReportToErrorService() {
      return this.reportToErrorService;
   }

   public void setReportToErrorService(boolean report) {
      this.reportToErrorService = report;
   }

   @Override
   public String toString() {
      return this.getMessage();
   }
}
