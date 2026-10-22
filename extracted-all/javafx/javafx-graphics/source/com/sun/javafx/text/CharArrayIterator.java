package com.sun.javafx.text;

import java.text.CharacterIterator;

class CharArrayIterator implements CharacterIterator {
   private char[] chars;
   private int pos;
   private int begin;

   public CharArrayIterator(char[] var1) {
      this.reset(var1, 0);
   }

   public CharArrayIterator(char[] var1, int var2) {
      this.reset(var1, var2);
   }

   @Override
   public char first() {
      this.pos = 0;
      return this.current();
   }

   @Override
   public char last() {
      if (this.chars.length > 0) {
         this.pos = this.chars.length - 1;
      } else {
         this.pos = 0;
      }

      return this.current();
   }

   @Override
   public char current() {
      return this.pos >= 0 && this.pos < this.chars.length ? this.chars[this.pos] : '\uffff';
   }

   @Override
   public char next() {
      if (this.pos < this.chars.length - 1) {
         this.pos++;
         return this.chars[this.pos];
      } else {
         this.pos = this.chars.length;
         return '\uffff';
      }
   }

   @Override
   public char previous() {
      if (this.pos > 0) {
         this.pos--;
         return this.chars[this.pos];
      } else {
         this.pos = 0;
         return '\uffff';
      }
   }

   @Override
   public char setIndex(int var1) {
      var1 -= this.begin;
      if (var1 >= 0 && var1 <= this.chars.length) {
         this.pos = var1;
         return this.current();
      } else {
         throw new IllegalArgumentException("Invalid index");
      }
   }

   @Override
   public int getBeginIndex() {
      return this.begin;
   }

   @Override
   public int getEndIndex() {
      return this.begin + this.chars.length;
   }

   @Override
   public int getIndex() {
      return this.begin + this.pos;
   }

   @Override
   public Object clone() {
      CharArrayIterator var1 = new CharArrayIterator(this.chars, this.begin);
      var1.pos = this.pos;
      return var1;
   }

   void reset(char[] var1) {
      this.reset(var1, 0);
   }

   void reset(char[] var1, int var2) {
      this.chars = var1;
      this.begin = var2;
      this.pos = 0;
   }
}
