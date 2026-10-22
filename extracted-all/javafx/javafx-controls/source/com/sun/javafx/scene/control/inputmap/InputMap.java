package com.sun.javafx.scene.control.inputmap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Pair;

public class InputMap<N extends Node> implements EventHandler<Event> {
   private final N node;
   private final ObservableList<InputMap<N>> childInputMaps;
   private final ObservableList<InputMap.Mapping<?>> mappings;
   private final Map<EventType<?>, List<EventHandler<? super Event>>> installedEventHandlers;
   private final Map<EventType, List<InputMap.Mapping>> eventTypeMappings;
   private ReadOnlyObjectWrapper<InputMap<N>> parentInputMap = new ReadOnlyObjectWrapper<InputMap<N>>(this, "parentInputMap") {
      protected void invalidated() {
         InputMap.this.reprocessAllMappings();
      }
   };
   private ObjectProperty<Predicate<? extends Event>> interceptor = new SimpleObjectProperty(this, "interceptor");

   public InputMap(N var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Node can not be null");
      } else {
         this.node = (N)var1;
         this.eventTypeMappings = new HashMap<>();
         this.installedEventHandlers = new HashMap<>();
         this.mappings = FXCollections.observableArrayList();
         this.mappings.addListener(var1x -> {
            while (var1x.next()) {
               if (var1x.wasRemoved()) {
                  for (InputMap.Mapping var3 : var1x.getRemoved()) {
                     this.removeMapping(var3);
                  }
               }

               if (var1x.wasAdded()) {
                  ArrayList var5 = new ArrayList();

                  for (InputMap.Mapping var4 : var1x.getAddedSubList()) {
                     if (var4 == null) {
                        var5.add(null);
                     } else {
                        this.addMapping(var4);
                     }
                  }

                  if (!var5.isEmpty()) {
                     this.getMappings().removeAll(var5);
                     throw new IllegalArgumentException("Null mappings not permitted");
                  }
               }
            }
         });
         this.childInputMaps = FXCollections.observableArrayList();
         this.childInputMaps.addListener(var1x -> {
            while (var1x.next()) {
               if (var1x.wasRemoved()) {
                  for (InputMap var3 : var1x.getRemoved()) {
                     var3.setParentInputMap(null);
                  }
               }

               if (var1x.wasAdded()) {
                  ArrayList var5 = new ArrayList();

                  for (InputMap var4 : var1x.getAddedSubList()) {
                     if (var4.getNode() != this.getNode()) {
                        var5.add(var4);
                     } else {
                        var4.setParentInputMap(this);
                     }
                  }

                  if (!var5.isEmpty()) {
                     this.getChildInputMaps().removeAll(var5);
                     throw new IllegalArgumentException("Child InputMap intances need to share a common Node object");
                  }
               }
            }
         });
      }
   }

   private final void setParentInputMap(InputMap<N> var1) {
      this.parentInputMap.set(var1);
   }

   private final InputMap<N> getParentInputMap() {
      return (InputMap<N>)this.parentInputMap.get();
   }

   private final ReadOnlyObjectProperty<InputMap<N>> parentInputMapProperty() {
      return this.parentInputMap.getReadOnlyProperty();
   }

   public final Predicate<? extends Event> getInterceptor() {
      return (Predicate<? extends Event>)this.interceptor.get();
   }

   public final void setInterceptor(Predicate<? extends Event> var1) {
      this.interceptor.set(var1);
   }

   public final ObjectProperty<Predicate<? extends Event>> interceptorProperty() {
      return this.interceptor;
   }

   public final N getNode() {
      return this.node;
   }

   public ObservableList<InputMap.Mapping<?>> getMappings() {
      return this.mappings;
   }

   public ObservableList<InputMap<N>> getChildInputMaps() {
      return this.childInputMaps;
   }

   public void dispose() {
      for (InputMap var2 : this.getChildInputMaps()) {
         var2.dispose();
      }

      this.removeAllEventHandlers();
      this.getMappings().clear();
   }

   public void handle(Event var1) {
      if (var1 != null && !var1.isConsumed()) {
         for (InputMap.Mapping var4 : this.lookup(var1, true)) {
            EventHandler var5 = var4.getEventHandler();
            if (var5 != null) {
               var5.handle(var1);
            }

            if (var4.isAutoConsume()) {
               var1.consume();
            }

            if (var1.isConsumed()) {
               break;
            }
         }
      }
   }

   public Optional<InputMap.Mapping<?>> lookupMapping(Object var1) {
      if (var1 == null) {
         return Optional.empty();
      } else {
         List var2 = this.lookupMappingKey(var1);

         for (int var3 = 0; var3 < this.getChildInputMaps().size(); var3++) {
            InputMap var4 = (InputMap)this.getChildInputMaps().get(var3);
            List var5 = var4.lookupMappingKey(var1);
            var2.addAll(0, var5);
         }

         return var2.size() > 0 ? Optional.of((InputMap.Mapping<?>)var2.get(0)) : Optional.empty();
      }
   }

   private List<InputMap.Mapping<?>> lookupMappingKey(Object var1) {
      return this.getMappings().stream().filter(var0 -> !var0.isDisabled()).filter(var1x -> var1.equals(var1x.getMappingKey())).collect(Collectors.toList());
   }

   private List<InputMap.Mapping<?>> lookup(Event var1, boolean var2) {
      if (var2) {
         boolean var3 = this.testInterceptor(var1, this.getInterceptor());
         if (var3) {
            return Collections.emptyList();
         }
      }

      ArrayList var8 = new ArrayList();
      int var4 = 0;
      List var5 = this.lookupMappingAndSpecificity(var1, var4);
      if (!var5.isEmpty()) {
         var4 = (Integer)((Pair)var5.get(0)).getKey();
         var8.addAll(var5.stream().map(var0 -> (InputMap.Mapping)var0.getValue()).collect(Collectors.toList()));
      }

      for (int var6 = 0; var6 < this.getChildInputMaps().size(); var6++) {
         InputMap var7 = (InputMap)this.getChildInputMaps().get(var6);
         var4 = this.scanRecursively(var7, var1, var2, var4, var8);
      }

      return var8;
   }

   private int scanRecursively(InputMap<?> var1, Event var2, boolean var3, int var4, List<InputMap.Mapping<?>> var5) {
      if (var3) {
         boolean var6 = this.testInterceptor(var2, var1.getInterceptor());
         if (var6) {
            return var4;
         }
      }

      List var9 = var1.lookupMappingAndSpecificity(var2, var4);
      if (!var9.isEmpty()) {
         int var7 = (Integer)((Pair)var9.get(0)).getKey();
         List var8 = var9.stream().map(var0 -> (InputMap.Mapping)var0.getValue()).collect(Collectors.toList());
         if (var7 == var4) {
            var5.addAll(0, var8);
         } else if (var7 > var4) {
            var5.clear();
            var4 = var7;
            var5.addAll(var8);
         }
      }

      for (int var10 = 0; var10 < var1.getChildInputMaps().size(); var10++) {
         var4 = this.scanRecursively((InputMap<?>)var1.getChildInputMaps().get(var10), var2, var3, var4, var5);
      }

      return var4;
   }

   private InputMap<N> getRootInputMap() {
      InputMap var1 = this;

      while (var1 != null) {
         InputMap var2 = var1.getParentInputMap();
         if (var2 == null) {
            break;
         }

         var1 = var2;
      }

      return var1;
   }

   private void addMapping(InputMap.Mapping<?> var1) {
      InputMap var2 = this.getRootInputMap();
      var2.addEventHandler(var1.eventType);
      EventType var3 = var1.getEventType();
      List var4 = this.eventTypeMappings.computeIfAbsent(var3, var0 -> new ArrayList<>());
      var4.add(var1);
   }

   private void removeMapping(InputMap.Mapping<?> var1) {
      EventType var2 = var1.getEventType();
      if (this.eventTypeMappings.containsKey(var2)) {
         List var3 = this.eventTypeMappings.get(var2);
         var3.remove(var1);
      }
   }

   private void addEventHandler(EventType var1) {
      List var2 = this.installedEventHandlers.computeIfAbsent(var1, var0 -> new ArrayList<>());
      EventHandler var3 = this::handle;
      if (var2.isEmpty()) {
         this.node.addEventHandler(var1, var3);
      }

      var2.add(var3);
   }

   private void removeAllEventHandlers() {
      for (EventType var2 : this.installedEventHandlers.keySet()) {
         for (EventHandler var5 : this.installedEventHandlers.get(var2)) {
            this.node.removeEventHandler(var2, var5);
         }
      }
   }

   private void reprocessAllMappings() {
      this.removeAllEventHandlers();
      this.mappings.stream().forEach(this::addMapping);

      for (InputMap var2 : this.getChildInputMaps()) {
         var2.reprocessAllMappings();
      }
   }

   private List<Pair<Integer, InputMap.Mapping<?>>> lookupMappingAndSpecificity(Event var1, int var2) {
      int var3 = var2;
      List var4 = this.eventTypeMappings.getOrDefault(var1.getEventType(), Collections.emptyList());
      ArrayList var5 = new ArrayList();

      for (InputMap.Mapping var7 : var4) {
         if (!var7.isDisabled()) {
            boolean var8 = this.testInterceptor(var1, var7.getInterceptor());
            if (!var8) {
               int var9 = var7.getSpecificity(var1);
               if (var9 > 0 && var9 == var3) {
                  var5.add(new Pair(var9, var7));
               } else if (var9 > var3) {
                  var5.clear();
                  var5.add(new Pair(var9, var7));
                  var3 = var9;
               }
            }
         }
      }

      return var5;
   }

   private boolean testInterceptor(Event var1, Predicate var2) {
      return var2 != null && var2.test(var1);
   }

   public static class KeyMapping extends InputMap.Mapping<KeyEvent> {
      private final KeyBinding keyBinding;

      public KeyMapping(KeyCode var1, EventHandler<KeyEvent> var2) {
         this(new KeyBinding(var1), var2);
      }

      public KeyMapping(KeyCode var1, EventType<KeyEvent> var2, EventHandler<KeyEvent> var3) {
         this(new KeyBinding(var1, var2), var3);
      }

      public KeyMapping(KeyBinding var1, EventHandler<KeyEvent> var2) {
         this(var1, var2, null);
      }

      public KeyMapping(KeyBinding var1, EventHandler<KeyEvent> var2, Predicate<KeyEvent> var3) {
         super(var1 == null ? null : var1.getType(), var2);
         if (var1 == null) {
            throw new IllegalArgumentException("KeyMapping keyBinding constructor argument can not be null");
         } else {
            this.keyBinding = var1;
            this.setInterceptor(var3);
         }
      }

      @Override
      public Object getMappingKey() {
         return this.keyBinding;
      }

      @Override
      public int getSpecificity(Event var1) {
         if (this.isDisabled()) {
            return 0;
         } else {
            return !(var1 instanceof KeyEvent) ? 0 : this.keyBinding.getSpecificity((KeyEvent)var1);
         }
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof InputMap.KeyMapping)) {
            return false;
         } else if (!super.equals(var1)) {
            return false;
         } else {
            InputMap.KeyMapping var2 = (InputMap.KeyMapping)var1;
            return this.keyBinding.equals(var2.keyBinding);
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.keyBinding);
      }
   }

   public static class KeyMappingInterceptor implements Predicate<Event> {
      private final KeyBinding keyBinding;

      public KeyMappingInterceptor(KeyBinding var1) {
         this.keyBinding = var1;
      }

      public boolean test(Event var1) {
         return !(var1 instanceof KeyEvent) ? false : KeyBinding.toKeyBinding((KeyEvent)var1).equals(this.keyBinding);
      }
   }

   public abstract static class Mapping<T extends Event> {
      private final EventType<T> eventType;
      private final EventHandler<T> eventHandler;
      private BooleanProperty disabled = new SimpleBooleanProperty(this, "disabled", false);
      private BooleanProperty autoConsume = new SimpleBooleanProperty(this, "autoConsume", true);
      private ObjectProperty<Predicate<? extends Event>> interceptor = new SimpleObjectProperty(this, "interceptor");

      public Mapping(EventType<T> var1, EventHandler<T> var2) {
         this.eventType = var1;
         this.eventHandler = var2;
      }

      public abstract int getSpecificity(Event var1);

      public final void setDisabled(boolean var1) {
         this.disabled.set(var1);
      }

      public final boolean isDisabled() {
         return this.disabled.get();
      }

      public final BooleanProperty disabledProperty() {
         return this.disabled;
      }

      public final void setAutoConsume(boolean var1) {
         this.autoConsume.set(var1);
      }

      public final boolean isAutoConsume() {
         return this.autoConsume.get();
      }

      public final BooleanProperty autoConsumeProperty() {
         return this.autoConsume;
      }

      public final EventType<T> getEventType() {
         return this.eventType;
      }

      public final EventHandler<T> getEventHandler() {
         return this.eventHandler;
      }

      public final Predicate<? extends Event> getInterceptor() {
         return (Predicate<? extends Event>)this.interceptor.get();
      }

      public final void setInterceptor(Predicate<? extends Event> var1) {
         this.interceptor.set(var1);
      }

      public final ObjectProperty<Predicate<? extends Event>> interceptorProperty() {
         return this.interceptor;
      }

      public Object getMappingKey() {
         return this.eventType;
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof InputMap.Mapping)) {
            return false;
         } else {
            InputMap.Mapping var2 = (InputMap.Mapping)var1;
            return this.eventType != null ? this.eventType.equals(var2.getEventType()) : var2.getEventType() == null;
         }
      }

      @Override
      public int hashCode() {
         return this.eventType != null ? this.eventType.hashCode() : 0;
      }
   }

   public static class MouseMapping extends InputMap.Mapping<MouseEvent> {
      public MouseMapping(EventType<MouseEvent> var1, EventHandler<MouseEvent> var2) {
         super(var1, var2);
         if (var1 == null) {
            throw new IllegalArgumentException("MouseMapping eventType constructor argument can not be null");
         }
      }

      @Override
      public int getSpecificity(Event var1) {
         if (this.isDisabled()) {
            return 0;
         } else if (!(var1 instanceof MouseEvent)) {
            return 0;
         } else {
            EventType var2 = this.getEventType();
            int var3 = 0;
            if (var1.getEventType() == MouseEvent.MOUSE_CLICKED && var2 != MouseEvent.MOUSE_CLICKED) {
               return 0;
            } else {
               var3++;
               if (var1.getEventType() == MouseEvent.MOUSE_DRAGGED && var2 != MouseEvent.MOUSE_DRAGGED) {
                  return 0;
               } else {
                  var3++;
                  if (var1.getEventType() == MouseEvent.MOUSE_ENTERED && var2 != MouseEvent.MOUSE_ENTERED) {
                     return 0;
                  } else {
                     var3++;
                     if (var1.getEventType() == MouseEvent.MOUSE_ENTERED_TARGET && var2 != MouseEvent.MOUSE_ENTERED_TARGET) {
                        return 0;
                     } else {
                        var3++;
                        if (var1.getEventType() == MouseEvent.MOUSE_EXITED && var2 != MouseEvent.MOUSE_EXITED) {
                           return 0;
                        } else {
                           var3++;
                           if (var1.getEventType() == MouseEvent.MOUSE_EXITED_TARGET && var2 != MouseEvent.MOUSE_EXITED_TARGET) {
                              return 0;
                           } else {
                              var3++;
                              if (var1.getEventType() == MouseEvent.MOUSE_MOVED && var2 != MouseEvent.MOUSE_MOVED) {
                                 return 0;
                              } else {
                                 var3++;
                                 if (var1.getEventType() == MouseEvent.MOUSE_PRESSED && var2 != MouseEvent.MOUSE_PRESSED) {
                                    return 0;
                                 } else {
                                    var3++;
                                    return var1.getEventType() == MouseEvent.MOUSE_RELEASED && var2 != MouseEvent.MOUSE_RELEASED ? 0 : var3 + 1;
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public static class MouseMappingInterceptor implements Predicate<Event> {
      private final EventType<MouseEvent> eventType;

      public MouseMappingInterceptor(EventType<MouseEvent> var1) {
         this.eventType = var1;
      }

      public boolean test(Event var1) {
         return !(var1 instanceof MouseEvent) ? false : var1.getEventType() == this.eventType;
      }
   }
}
