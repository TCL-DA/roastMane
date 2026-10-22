package org.glassfish.hk2.utilities.general.internal;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

public class DoubleNode<K, V> {
   private final WeakReference<K> weakKey;
   private final V value;
   private DoubleNode<K, V> previous;
   private DoubleNode<K, V> next;
   private K hardenedKey;

   public DoubleNode(K key, V value, ReferenceQueue<? super K> queue) {
      this.weakKey = new WeakReference<>(key, queue);
      this.value = value;
   }

   public DoubleNode<K, V> getPrevious() {
      return this.previous;
   }

   public void setPrevious(DoubleNode<K, V> previous) {
      this.previous = previous;
   }

   public DoubleNode<K, V> getNext() {
      return this.next;
   }

   public void setNext(DoubleNode<K, V> next) {
      this.next = next;
   }

   public WeakReference<K> getWeakKey() {
      return this.weakKey;
   }

   public V getValue() {
      return this.value;
   }

   public K getHardenedKey() {
      return this.hardenedKey;
   }

   public void setHardenedKey(K hardenedKey) {
      this.hardenedKey = hardenedKey;
   }
}
