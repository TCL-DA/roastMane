package com.ghgande.j2mod.modbus.procimg;

import java.util.Vector;

public class SimpleProcessImage implements ProcessImageImplementation {
   protected final Vector<DigitalIn> digitalIns = new Vector<>();
   protected final Vector<DigitalOut> digitalOuts = new Vector<>();
   protected final Vector<InputRegister> inputRegisters = new Vector<>();
   protected final Vector<Register> registers = new Vector<>();
   protected final Vector<File> files = new Vector<>();
   protected final Vector<FIFO> fifos = new Vector<>();
   protected boolean locked = false;
   protected int unitID = 0;

   public SimpleProcessImage() {
   }

   public SimpleProcessImage(int unit) {
      this.unitID = unit;
   }

   public synchronized boolean isLocked() {
      return this.locked;
   }

   public synchronized boolean setLocked(boolean locked) {
      if (this.locked && locked) {
         return false;
      } else {
         this.locked = locked;
         return true;
      }
   }

   public int getUnitID() {
      return this.unitID;
   }

   @Override
   public DigitalOut[] getDigitalOutRange(int ref, int count) {
      if (ref >= 0 && ref + count <= this.digitalOuts.size()) {
         DigitalOut[] douts = new DigitalOut[count];

         for (int i = 0; i < douts.length; i++) {
            douts[i] = this.getDigitalOut(ref + i);
         }

         return douts;
      } else {
         throw new IllegalAddressException();
      }
   }

   @Override
   public DigitalOut getDigitalOut(int ref) throws IllegalAddressException {
      try {
         DigitalOut result = this.digitalOuts.elementAt(ref);
         if (result == null) {
            throw new IllegalAddressException();
         } else {
            return result;
         }
      } catch (IndexOutOfBoundsException var3) {
         throw new IllegalAddressException();
      }
   }

   @Override
   public int getDigitalOutCount() {
      return this.digitalOuts.size();
   }

   @Override
   public DigitalIn[] getDigitalInRange(int ref, int count) {
      if (ref >= 0 && ref + count <= this.digitalIns.size()) {
         DigitalIn[] dins = new DigitalIn[count];

         for (int i = 0; i < dins.length; i++) {
            dins[i] = this.getDigitalIn(ref + i);
         }

         return dins;
      } else {
         throw new IllegalAddressException();
      }
   }

   @Override
   public DigitalIn getDigitalIn(int ref) throws IllegalAddressException {
      try {
         DigitalIn result = this.digitalIns.elementAt(ref);
         if (result == null) {
            throw new IllegalAddressException();
         } else {
            return result;
         }
      } catch (IndexOutOfBoundsException var3) {
         throw new IllegalAddressException();
      }
   }

   @Override
   public int getDigitalInCount() {
      return this.digitalIns.size();
   }

   @Override
   public InputRegister[] getInputRegisterRange(int ref, int count) {
      if (ref >= 0 && ref + count <= this.inputRegisters.size()) {
         InputRegister[] iregs = new InputRegister[count];

         for (int i = 0; i < iregs.length; i++) {
            iregs[i] = this.getInputRegister(ref + i);
         }

         return iregs;
      } else {
         throw new IllegalAddressException();
      }
   }

   @Override
   public InputRegister getInputRegister(int ref) throws IllegalAddressException {
      try {
         InputRegister result = this.inputRegisters.elementAt(ref);
         if (result == null) {
            throw new IllegalAddressException();
         } else {
            return result;
         }
      } catch (IndexOutOfBoundsException var3) {
         throw new IllegalAddressException();
      }
   }

   @Override
   public int getInputRegisterCount() {
      return this.inputRegisters.size();
   }

   @Override
   public Register[] getRegisterRange(int ref, int count) {
      if (ref >= 0 && ref + count <= this.registers.size()) {
         Register[] iregs = new Register[count];

         for (int i = 0; i < iregs.length; i++) {
            iregs[i] = this.getRegister(ref + i);
         }

         return iregs;
      } else {
         throw new IllegalAddressException();
      }
   }

   @Override
   public Register getRegister(int ref) throws IllegalAddressException {
      try {
         Register result = this.registers.elementAt(ref);
         if (result == null) {
            throw new IllegalAddressException();
         } else {
            return result;
         }
      } catch (IndexOutOfBoundsException var3) {
         throw new IllegalAddressException();
      }
   }

   @Override
   public int getRegisterCount() {
      return this.registers.size();
   }

   @Override
   public File getFile(int fileNumber) {
      try {
         File result = this.files.elementAt(fileNumber);
         if (result == null) {
            throw new IllegalAddressException();
         } else {
            return result;
         }
      } catch (IndexOutOfBoundsException var3) {
         throw new IllegalAddressException();
      }
   }

   @Override
   public File getFileByNumber(int ref) {
      if (ref >= 0 && ref < 10000 && this.files != null) {
         synchronized (this.files) {
            for (File file : this.files) {
               if (file.getFileNumber() == ref) {
                  return file;
               }
            }
         }

         throw new IllegalAddressException();
      } else {
         throw new IllegalAddressException();
      }
   }

   @Override
   public int getFileCount() {
      return this.files.size();
   }

   @Override
   public FIFO getFIFO(int fifoNumber) {
      try {
         FIFO result = this.fifos.elementAt(fifoNumber);
         if (result == null) {
            throw new IllegalAddressException();
         } else {
            return result;
         }
      } catch (IndexOutOfBoundsException var3) {
         throw new IllegalAddressException();
      }
   }

   @Override
   public FIFO getFIFOByAddress(int ref) {
      for (FIFO fifo : this.fifos) {
         if (fifo.getAddress() == ref) {
            return fifo;
         }
      }

      return null;
   }

   @Override
   public int getFIFOCount() {
      return this.fifos == null ? 0 : this.fifos.size();
   }

   @Override
   public void setDigitalOut(int ref, DigitalOut out) throws IllegalAddressException {
      if (!this.isLocked()) {
         try {
            if (this.digitalOuts.get(ref) == null) {
               throw new IllegalAddressException();
            }

            this.digitalOuts.setElementAt(out, ref);
         } catch (IndexOutOfBoundsException var4) {
            throw new IllegalAddressException();
         }
      }
   }

   @Override
   public void addDigitalOut(DigitalOut out) {
      if (!this.isLocked()) {
         this.digitalOuts.addElement(out);
      }
   }

   @Override
   public void addDigitalOut(int ref, DigitalOut dout) {
      if (ref >= 0 && ref < 65536) {
         if (!this.isLocked()) {
            synchronized (this.digitalOuts) {
               if (ref < this.digitalOuts.size()) {
                  this.digitalOuts.setElementAt(dout, ref);
                  return;
               }

               this.digitalOuts.setSize(ref + 1);
               this.digitalOuts.setElementAt(dout, ref);
            }
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public void removeDigitalOut(DigitalOut out) {
      if (!this.isLocked()) {
         this.digitalOuts.removeElement(out);
      }
   }

   @Override
   public void setDigitalIn(int ref, DigitalIn di) throws IllegalAddressException {
      if (!this.isLocked()) {
         try {
            if (this.digitalIns.get(ref) == null) {
               throw new IllegalAddressException();
            }

            this.digitalIns.setElementAt(di, ref);
         } catch (IndexOutOfBoundsException var4) {
            throw new IllegalAddressException();
         }
      }
   }

   @Override
   public void addDigitalIn(DigitalIn di) {
      if (!this.isLocked()) {
         this.digitalIns.addElement(di);
      }
   }

   @Override
   public void addDigitalIn(int ref, DigitalIn d1) {
      if (ref >= 0 && ref < 65536) {
         if (!this.isLocked()) {
            synchronized (this.digitalIns) {
               if (ref < this.digitalIns.size()) {
                  this.digitalIns.setElementAt(d1, ref);
                  return;
               }

               this.digitalIns.setSize(ref + 1);
               this.digitalIns.setElementAt(d1, ref);
            }
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public void removeDigitalIn(DigitalIn di) {
      if (!this.isLocked()) {
         this.digitalIns.removeElement(di);
      }
   }

   @Override
   public void setInputRegister(int ref, InputRegister reg) throws IllegalAddressException {
      if (!this.isLocked()) {
         try {
            if (this.inputRegisters.get(ref) == null) {
               throw new IllegalAddressException();
            }

            this.inputRegisters.setElementAt(reg, ref);
         } catch (IndexOutOfBoundsException var4) {
            throw new IllegalAddressException();
         }
      }
   }

   @Override
   public void addInputRegister(InputRegister reg) {
      if (!this.isLocked()) {
         this.inputRegisters.addElement(reg);
      }
   }

   @Override
   public void addInputRegister(int ref, InputRegister inReg) {
      if (ref >= 0 && ref < 65536) {
         if (!this.isLocked()) {
            synchronized (this.inputRegisters) {
               if (ref < this.inputRegisters.size()) {
                  this.inputRegisters.setElementAt(inReg, ref);
                  return;
               }

               this.inputRegisters.setSize(ref + 1);
               this.inputRegisters.setElementAt(inReg, ref);
            }
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public void removeInputRegister(InputRegister reg) {
      if (!this.isLocked()) {
         this.inputRegisters.removeElement(reg);
      }
   }

   @Override
   public void setRegister(int ref, Register reg) throws IllegalAddressException {
      if (!this.isLocked()) {
         try {
            if (this.registers.get(ref) == null) {
               throw new IllegalAddressException();
            }

            this.registers.setElementAt(reg, ref);
         } catch (IndexOutOfBoundsException var4) {
            throw new IllegalAddressException();
         }
      }
   }

   @Override
   public void addRegister(Register reg) {
      if (!this.isLocked()) {
         this.registers.addElement(reg);
      }
   }

   @Override
   public void addRegister(int ref, Register reg) {
      if (ref >= 0 && ref < 65536) {
         if (!this.isLocked()) {
            synchronized (this.registers) {
               if (ref < this.registers.size()) {
                  this.registers.setElementAt(reg, ref);
                  return;
               }

               this.registers.setSize(ref + 1);
               this.registers.setElementAt(reg, ref);
            }
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public void removeRegister(Register reg) {
      if (!this.isLocked()) {
         this.registers.removeElement(reg);
      }
   }

   @Override
   public void setFile(int fileNumber, File file) {
      if (!this.isLocked()) {
         try {
            if (this.files.get(fileNumber) == null) {
               throw new IllegalAddressException();
            }

            this.files.setElementAt(file, fileNumber);
         } catch (IndexOutOfBoundsException var4) {
            throw new IllegalAddressException();
         }
      }
   }

   @Override
   public void addFile(File newFile) {
      if (!this.isLocked()) {
         this.files.add(newFile);
      }
   }

   @Override
   public void addFile(int ref, File newFile) {
      if (ref >= 0 && ref < 65536) {
         if (!this.isLocked()) {
            synchronized (this.files) {
               if (ref < this.files.size()) {
                  this.files.setElementAt(newFile, ref);
                  return;
               }

               this.files.setSize(ref + 1);
               this.files.setElementAt(newFile, ref);
            }
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public void removeFile(File oldFile) {
      if (!this.isLocked()) {
         this.files.removeElement(oldFile);
      }
   }

   @Override
   public void setFIFO(int fifoNumber, FIFO fifo) {
      if (!this.isLocked()) {
         try {
            if (this.fifos.get(fifoNumber) == null) {
               throw new IllegalAddressException();
            }

            this.fifos.setElementAt(fifo, fifoNumber);
         } catch (IndexOutOfBoundsException var4) {
            throw new IllegalAddressException();
         }
      }
   }

   @Override
   public void addFIFO(FIFO fifo) {
      if (!this.isLocked()) {
         this.fifos.add(fifo);
      }
   }

   @Override
   public void addFIFO(int ref, FIFO newFIFO) {
      if (ref >= 0 && ref < 65536) {
         if (!this.isLocked()) {
            synchronized (this.fifos) {
               if (ref < this.fifos.size()) {
                  this.fifos.setElementAt(newFIFO, ref);
                  return;
               }

               this.fifos.setSize(ref + 1);
               this.fifos.setElementAt(newFIFO, ref);
            }
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public void removeFIFO(FIFO oldFIFO) {
      if (!this.isLocked()) {
         this.fifos.removeElement(oldFIFO);
      }
   }
}
