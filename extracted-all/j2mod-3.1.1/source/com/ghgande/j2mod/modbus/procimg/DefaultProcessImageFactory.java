package com.ghgande.j2mod.modbus.procimg;

public class DefaultProcessImageFactory implements ProcessImageFactory {
   @Override
   public ProcessImageImplementation createProcessImageImplementation() {
      return new SimpleProcessImage();
   }

   @Override
   public DigitalIn createDigitalIn() {
      return new SimpleDigitalIn();
   }

   @Override
   public DigitalIn createDigitalIn(boolean state) {
      return new SimpleDigitalIn(state);
   }

   @Override
   public DigitalOut createDigitalOut() {
      return new SimpleDigitalOut();
   }

   @Override
   public DigitalOut createDigitalOut(boolean b) {
      return new SimpleDigitalOut(b);
   }

   @Override
   public InputRegister createInputRegister() {
      return new SimpleInputRegister();
   }

   @Override
   public InputRegister createInputRegister(byte b1, byte b2) {
      return new SimpleInputRegister(b1, b2);
   }

   @Override
   public Register createRegister() {
      return new SimpleRegister();
   }

   @Override
   public Register createRegister(byte b1, byte b2) {
      return new SimpleRegister(b1, b2);
   }

   public InputRegister createInputRegister(int value) {
      return new SimpleInputRegister(value);
   }

   public Register createRegister(int value) {
      return new SimpleRegister(value);
   }
}
