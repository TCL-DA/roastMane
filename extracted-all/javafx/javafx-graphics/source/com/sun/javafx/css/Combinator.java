package com.sun.javafx.css;

public enum Combinator {
   CHILD {
      @Override
      public String toString() {
         return ">";
      }
   },
   DESCENDANT {
      @Override
      public String toString() {
         return " ";
      }
   };
}
