package com.sun.javafx.scene;

import com.sun.javafx.PlatformUtil;
import com.sun.javafx.collections.ObservableListWrapper;
import com.sun.javafx.collections.ObservableMapWrapper;
import com.sun.javafx.event.BasicEventDispatcher;
import com.sun.javafx.scene.traversal.Direction;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.Mnemonic;

public final class KeyboardShortcutsHandler extends BasicEventDispatcher {
   private ObservableMap<KeyCombination, Runnable> accelerators;
   private KeyboardShortcutsHandler.CopyOnWriteMap<KeyCombination, Runnable> acceleratorsBackingMap;
   private ObservableMap<KeyCombination, ObservableList<Mnemonic>> mnemonics;
   private boolean mnemonicsDisplayEnabled = false;

   public void addMnemonic(Mnemonic var1) {
      Object var2 = (ObservableList)this.getMnemonics().get(var1.getKeyCombination());
      if (var2 == null) {
         var2 = new ObservableListWrapper(new ArrayList());
         this.getMnemonics().put(var1.getKeyCombination(), var2);
      }

      boolean var3 = false;

      for (Mnemonic var5 : var2) {
         if (var5 == var1) {
            var3 = true;
            break;
         }
      }

      if (!var3) {
         var2.add(var1);
      }
   }

   public void removeMnemonic(Mnemonic var1) {
      ObservableList var2 = (ObservableList)this.getMnemonics().get(var1.getKeyCombination());
      if (var2 != null) {
         for (int var3 = 0; var3 < var2.size(); var3++) {
            if (((Mnemonic)var2.get(var3)).getNode() == var1.getNode()) {
               var2.remove(var3);
            }
         }
      }
   }

   public ObservableMap<KeyCombination, ObservableList<Mnemonic>> getMnemonics() {
      if (this.mnemonics == null) {
         this.mnemonics = new ObservableMapWrapper(new HashMap());
      }

      return this.mnemonics;
   }

   public ObservableMap<KeyCombination, Runnable> getAccelerators() {
      if (this.accelerators == null) {
         this.acceleratorsBackingMap = new KeyboardShortcutsHandler.CopyOnWriteMap<>();
         this.accelerators = new ObservableMapWrapper(this.acceleratorsBackingMap);
      }

      return this.accelerators;
   }

   private void traverse(Event var1, Node var2, Direction var3) {
      if (NodeHelper.traverse(var2, var3)) {
         var1.consume();
      }
   }

   public void processTraversal(Event var1) {
      if (var1.getEventType() == KeyEvent.KEY_PRESSED) {
         if (var1 instanceof KeyEvent) {
            KeyEvent var2 = (KeyEvent)var1;
            if (!var2.isMetaDown() && !var2.isControlDown() && !var2.isAltDown()) {
               EventTarget var3 = var1.getTarget();
               if (!(var3 instanceof Node)) {
                  return;
               }

               Node var4 = (Node)var3;
               switch (var2.getCode()) {
                  case TAB:
                     if (var2.isShiftDown()) {
                        this.traverse(var1, var4, Direction.PREVIOUS);
                     } else {
                        this.traverse(var1, var4, Direction.NEXT);
                     }
                     break;
                  case UP:
                     this.traverse(var1, var4, Direction.UP);
                     break;
                  case DOWN:
                     this.traverse(var1, var4, Direction.DOWN);
                     break;
                  case LEFT:
                     this.traverse(var1, var4, Direction.LEFT);
                     break;
                  case RIGHT:
                     this.traverse(var1, var4, Direction.RIGHT);
               }
            }
         }
      }
   }

   public Event dispatchBubblingEvent(Event var1) {
      if (!(var1 instanceof KeyEvent)) {
         return var1;
      } else {
         boolean var2 = var1.getEventType() == KeyEvent.KEY_PRESSED;
         KeyEvent var3 = (KeyEvent)var1;
         if (var2) {
            if (!var1.isConsumed()) {
               this.processAccelerators(var3);
            }

            if (!var1.isConsumed()) {
               this.processTraversal(var1);
            }
         }

         return var1;
      }
   }

   public Event dispatchCapturingEvent(Event var1) {
      if (!(var1 instanceof KeyEvent)) {
         return var1;
      } else {
         boolean var2 = var1.getEventType() == KeyEvent.KEY_PRESSED;
         boolean var3 = var1.getEventType() == KeyEvent.KEY_TYPED;
         boolean var4 = var1.getEventType() == KeyEvent.KEY_RELEASED;
         KeyEvent var5 = (KeyEvent)var1;
         if (var2 || var3) {
            if (PlatformUtil.isMac()) {
               if (var5.isMetaDown()) {
                  this.processMnemonics(var5);
               }
            } else if (var5.isAltDown() || this.isMnemonicsDisplayEnabled()) {
               this.processMnemonics(var5);
            }
         }

         if (!PlatformUtil.isMac() && !var1.isConsumed()) {
            if (var2) {
               if (var5.isAltDown()) {
                  if (!this.isMnemonicsDisplayEnabled()) {
                     this.setMnemonicsDisplayEnabled(true);
                  } else if (PlatformUtil.isWindows()) {
                     this.setMnemonicsDisplayEnabled(!this.isMnemonicsDisplayEnabled());
                  }
               } else if (var5.getCode() == KeyCode.ESCAPE) {
                  this.setMnemonicsDisplayEnabled(false);
               }
            }

            if (var4 && !var5.isAltDown() && !PlatformUtil.isWindows()) {
               this.setMnemonicsDisplayEnabled(false);
            }
         }

         return var1;
      }
   }

   private void processMnemonics(KeyEvent var1) {
      if (this.mnemonics != null) {
         KeyEvent var2 = var1;
         if (var1.getEventType() == KeyEvent.KEY_TYPED) {
            var2 = new KeyEvent(
               null,
               var1.getTarget(),
               KeyEvent.KEY_PRESSED,
               " ",
               var1.getCharacter(),
               KeyCode.getKeyCode(var1.getCharacter()),
               var1.isShiftDown(),
               var1.isControlDown(),
               this.isMnemonicsDisplayEnabled(),
               var1.isMetaDown()
            );
         } else if (this.isMnemonicsDisplayEnabled()) {
            var2 = new KeyEvent(
               null,
               var1.getTarget(),
               KeyEvent.KEY_PRESSED,
               var1.getCharacter(),
               var1.getText(),
               var1.getCode(),
               var1.isShiftDown(),
               var1.isControlDown(),
               this.isMnemonicsDisplayEnabled(),
               var1.isMetaDown()
            );
         }

         ObservableList var3 = null;

         for (Entry var5 : this.mnemonics.entrySet()) {
            if (((KeyCombination)var5.getKey()).match(var2)) {
               var3 = (ObservableList)var5.getValue();
               break;
            }
         }

         if (var3 != null) {
            boolean var12 = false;
            Node var13 = null;
            Mnemonic var6 = null;
            int var7 = -1;
            int var8 = -1;

            for (int var9 = 0; var9 < var3.size(); var9++) {
               Mnemonic var10 = (Mnemonic)var3.get(var9);
               Node var11 = var10.getNode();
               if (var6 == null && NodeHelper.isTreeVisible(var11) && !var11.isDisabled()) {
                  var6 = var10;
               }

               if (NodeHelper.isTreeVisible(var11) && var11.isFocusTraversable() && !var11.isDisabled()) {
                  if (var13 == null) {
                     var13 = var11;
                  } else {
                     var12 = true;
                     if (var7 != -1 && var8 == -1) {
                        var8 = var9;
                     }
                  }
               }

               if (var11.isFocused()) {
                  var7 = var9;
               }
            }

            if (var13 != null) {
               if (!var12) {
                  var13.requestFocus();
                  var1.consume();
               } else if (var7 == -1) {
                  var13.requestFocus();
                  var1.consume();
               } else if (var7 >= var3.size()) {
                  var13.requestFocus();
                  var1.consume();
               } else {
                  if (var8 != -1) {
                     ((Mnemonic)var3.get(var8)).getNode().requestFocus();
                  } else {
                     var13.requestFocus();
                  }

                  var1.consume();
               }
            }

            if (!var12 && var6 != null) {
               if (var1.getEventType() == KeyEvent.KEY_TYPED) {
                  var1.consume();
               } else {
                  var6.fire();
                  var1.consume();
               }
            }
         }
      }
   }

   private void processAccelerators(KeyEvent var1) {
      if (this.acceleratorsBackingMap != null) {
         this.acceleratorsBackingMap.lock();

         try {
            for (Entry var3 : this.acceleratorsBackingMap.backingMap.entrySet()) {
               if (((KeyCombination)var3.getKey()).match(var1)) {
                  Runnable var4 = (Runnable)var3.getValue();
                  if (var4 != null) {
                     var4.run();
                     var1.consume();
                  }
               }
            }
         } finally {
            this.acceleratorsBackingMap.unlock();
         }
      }
   }

   private void processMnemonicsKeyDisplay() {
      Object var1 = null;
      if (this.mnemonics != null) {
         for (Entry var3 : this.mnemonics.entrySet()) {
            var1 = (ObservableList)var3.getValue();
            if (var1 != null) {
               for (int var4 = 0; var4 < var1.size(); var4++) {
                  Node var5 = ((Mnemonic)var1.get(var4)).getNode();
                  NodeHelper.setShowMnemonics(var5, this.mnemonicsDisplayEnabled);
               }
            }
         }
      }
   }

   public boolean isMnemonicsDisplayEnabled() {
      return this.mnemonicsDisplayEnabled;
   }

   public void setMnemonicsDisplayEnabled(boolean var1) {
      if (var1 != this.mnemonicsDisplayEnabled) {
         this.mnemonicsDisplayEnabled = var1;
         this.processMnemonicsKeyDisplay();
      }
   }

   public void clearNodeMnemonics(Node var1) {
      if (this.mnemonics != null) {
         for (ObservableList var3 : this.mnemonics.values()) {
            Iterator var4 = var3.iterator();

            while (var4.hasNext()) {
               Mnemonic var5 = (Mnemonic)var4.next();
               if (var5.getNode() == var1) {
                  var4.remove();
               }
            }
         }
      }
   }

   private static class CopyOnWriteMap<K, V> extends AbstractMap<K, V> {
      private Map<K, V> backingMap = new HashMap<>();
      private boolean lock;

      public void lock() {
         this.lock = true;
      }

      public void unlock() {
         this.lock = false;
      }

      @Override
      public V put(K var1, V var2) {
         if (this.lock) {
            this.backingMap = new HashMap<>(this.backingMap);
            this.lock = false;
         }

         return this.backingMap.put((K)var1, (V)var2);
      }

      @Override
      public Set<Entry<K, V>> entrySet() {
         return new AbstractSet<Entry<K, V>>() {
            @Override
            public Iterator<Entry<K, V>> iterator() {
               return new Iterator<Entry<K, V>>() {
                  private Iterator<Entry<K, V>> backingIt = CopyOnWriteMap.this.backingMap.entrySet().iterator();
                  private Map<K, V> backingMapAtCreation = CopyOnWriteMap.this.backingMap;
                  private Entry<K, V> lastNext = null;

                  @Override
                  public boolean hasNext() {
                     this.checkCoMod();
                     return this.backingIt.hasNext();
                  }

                  private void checkCoMod() {
                     if (CopyOnWriteMap.this.backingMap != this.backingMapAtCreation) {
                        throw new ConcurrentModificationException();
                     }
                  }

                  public Entry<K, V> next() {
                     this.checkCoMod();
                     return this.lastNext = this.backingIt.next();
                  }

                  @Override
                  public void remove() {
                     this.checkCoMod();
                     if (this.lastNext == null) {
                        throw new IllegalStateException();
                     } else {
                        if (CopyOnWriteMap.this.lock) {
                           CopyOnWriteMap.this.backingMap = new HashMap<>(CopyOnWriteMap.this.backingMap);
                           this.backingIt = CopyOnWriteMap.this.backingMap.entrySet().iterator();

                           while (!this.lastNext.equals(this.backingIt.next())) {
                           }

                           CopyOnWriteMap.this.lock = false;
                        }

                        this.backingIt.remove();
                        this.lastNext = null;
                     }
                  }
               };
            }

            @Override
            public int size() {
               return CopyOnWriteMap.this.backingMap.size();
            }
         };
      }
   }
}
