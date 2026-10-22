package com.sun.webkit.dom;

import com.sun.webkit.Disposer;
import com.sun.webkit.DisposerRecord;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeListImpl implements NodeList {
   private final long peer;

   NodeListImpl(long var1) {
      this.peer = var1;
      Disposer.addRecord(this, new NodeListImpl.SelfDisposer(var1));
   }

   static NodeList create(long var0) {
      return var0 == 0L ? null : new NodeListImpl(var0);
   }

   long getPeer() {
      return this.peer;
   }

   @Override
   public boolean equals(Object var1) {
      return var1 instanceof NodeListImpl && this.peer == ((NodeListImpl)var1).peer;
   }

   @Override
   public int hashCode() {
      long var1 = this.peer;
      return (int)(var1 ^ var1 >> 17);
   }

   static long getPeer(NodeList var0) {
      return var0 == null ? 0L : ((NodeListImpl)var0).getPeer();
   }

   private static native void dispose(long var0);

   static NodeList getImpl(long var0) {
      return create(var0);
   }

   @Override
   public int getLength() {
      return getLengthImpl(this.getPeer());
   }

   static native int getLengthImpl(long var0);

   @Override
   public Node item(int var1) {
      return NodeImpl.getImpl(itemImpl(this.getPeer(), var1));
   }

   static native long itemImpl(long var0, int var2);

   private static class SelfDisposer implements DisposerRecord {
      private final long peer;

      SelfDisposer(long var1) {
         this.peer = var1;
      }

      @Override
      public void dispose() {
         NodeListImpl.dispose(this.peer);
      }
   }
}
