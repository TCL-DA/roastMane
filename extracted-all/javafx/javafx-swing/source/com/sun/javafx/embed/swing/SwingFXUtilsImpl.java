package com.sun.javafx.embed.swing;

import com.sun.javafx.embed.swing.newimpl.SwingFXUtilsImplInteropN;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class SwingFXUtilsImpl {
   private static SwingFXUtilsImplInteropN swFXUtilIOP = new SwingFXUtilsImplInteropN();

   private static EventQueue getEventQueue() {
      return AccessController.doPrivileged((PrivilegedAction<EventQueue>)(() -> Toolkit.getDefaultToolkit().getSystemEventQueue()));
   }

   public static void installFwEventQueue() {
      swFXUtilIOP.setFwDispatcher(getEventQueue());
   }

   public static void removeFwEventQueue() {
      swFXUtilIOP.setFwDispatcher(getEventQueue());
   }
}
