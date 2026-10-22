package org.eclipse.jetty.websocket.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.websocket.api.Callback;
import org.eclipse.jetty.websocket.api.Frame;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketContainer;
import org.eclipse.jetty.websocket.api.Session.Listener;
import org.eclipse.jetty.websocket.api.Session.Listener.AutoDemanding;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketFrame;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketOpen;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.exceptions.InvalidWebSocketException;
import org.eclipse.jetty.websocket.common.internal.ByteBufferMessageSink;
import org.eclipse.jetty.websocket.common.internal.PartialByteBufferMessageSink;
import org.eclipse.jetty.websocket.core.WebSocketComponents;
import org.eclipse.jetty.websocket.core.exception.InvalidSignatureException;
import org.eclipse.jetty.websocket.core.messages.InputStreamMessageSink;
import org.eclipse.jetty.websocket.core.messages.PartialStringMessageSink;
import org.eclipse.jetty.websocket.core.messages.ReaderMessageSink;
import org.eclipse.jetty.websocket.core.messages.StringMessageSink;
import org.eclipse.jetty.websocket.core.util.InvokerUtils;
import org.eclipse.jetty.websocket.core.util.ReflectUtils;
import org.eclipse.jetty.websocket.core.util.InvokerUtils.Arg;

public class JettyWebSocketFrameHandlerFactory extends ContainerLifeCycle {
   private static final Arg[] textCallingArgs = new Arg[]{new Arg(Session.class), new Arg(String.class).required()};
   private static final Arg[] binaryBufferCallingArgs = new Arg[]{
      new Arg(Session.class), new Arg(ByteBuffer.class).required(), new Arg(Callback.class).required()
   };
   private static final Arg[] inputStreamCallingArgs = new Arg[]{new Arg(Session.class), new Arg(InputStream.class).required()};
   private static final Arg[] readerCallingArgs = new Arg[]{new Arg(Session.class), new Arg(Reader.class).required()};
   private static final Arg[] textPartialCallingArgs = new Arg[]{new Arg(Session.class), new Arg(String.class).required(), new Arg(boolean.class).required()};
   private static final Arg[] binaryPartialBufferCallingArgs = new Arg[]{
      new Arg(Session.class), new Arg(ByteBuffer.class).required(), new Arg(boolean.class).required(), new Arg(Callback.class).required()
   };
   private final WebSocketContainer container;
   private final WebSocketComponents components;
   private final Map<Class<?>, JettyWebSocketFrameHandlerMetadata> metadataMap = new ConcurrentHashMap<>();

   public JettyWebSocketFrameHandlerFactory(WebSocketContainer container, WebSocketComponents components) {
      this.container = container;
      this.components = components;
   }

   public WebSocketComponents getWebSocketComponents() {
      return this.components;
   }

   public JettyWebSocketFrameHandlerMetadata getMetadata(Class<?> endpointClass) {
      JettyWebSocketFrameHandlerMetadata metadata = this.metadataMap.get(endpointClass);
      if (metadata == null) {
         metadata = this.createMetadata(endpointClass);
         this.metadataMap.put(endpointClass, metadata);
      }

      return metadata;
   }

   public JettyWebSocketFrameHandlerMetadata createMetadata(Class<?> endpointClass) {
      if (Listener.class.isAssignableFrom(endpointClass)) {
         return this.createListenerMetadata(endpointClass);
      } else {
         WebSocket websocket = endpointClass.getAnnotation(WebSocket.class);
         if (websocket != null) {
            return this.createAnnotatedMetadata(websocket, endpointClass);
         } else {
            throw new InvalidWebSocketException("Unrecognized WebSocket endpoint: " + endpointClass.getName());
         }
      }
   }

   public JettyWebSocketFrameHandler newJettyFrameHandler(Object endpointInstance) {
      JettyWebSocketFrameHandlerMetadata metadata = this.getMetadata(endpointInstance.getClass());
      this.components.getObjectFactory().decorate(endpointInstance);
      return new JettyWebSocketFrameHandler(this.container, endpointInstance, metadata);
   }

   private MethodHandle toMethodHandle(Lookup lookup, Method method) {
      try {
         return lookup.unreflect(method);
      } catch (IllegalAccessException var4) {
         throw new RuntimeException("Unable to access method " + method, var4);
      }
   }

   private JettyWebSocketFrameHandlerMetadata createListenerMetadata(Class<?> endpointClass) {
      JettyWebSocketFrameHandlerMetadata metadata = new JettyWebSocketFrameHandlerMetadata();
      metadata.setAutoDemand(AutoDemanding.class.isAssignableFrom(endpointClass));
      Lookup lookup = getServerMethodHandleLookup();
      Method openMethod = this.findMethod(endpointClass, "onWebSocketOpen", Session.class);
      if (openMethod != null) {
         MethodHandle connectHandle = this.toMethodHandle(lookup, openMethod);
         metadata.setOpenHandle(connectHandle, openMethod);
      }

      Method frameMethod = this.findMethod(endpointClass, "onWebSocketFrame", Frame.class, Callback.class);
      if (frameMethod != null) {
         MethodHandle frameHandle = this.toMethodHandle(lookup, frameMethod);
         metadata.setFrameHandle(frameHandle, frameMethod);
      }

      Method pingMethod = this.findMethod(endpointClass, "onWebSocketPing", ByteBuffer.class);
      if (pingMethod != null) {
         MethodHandle pingHandle = this.toMethodHandle(lookup, pingMethod);
         metadata.setPingHandle(pingHandle, pingMethod);
      }

      Method pongMethod = this.findMethod(endpointClass, "onWebSocketPong", ByteBuffer.class);
      if (pongMethod != null) {
         MethodHandle pongHandle = this.toMethodHandle(lookup, pongMethod);
         metadata.setPongHandle(pongHandle, pongMethod);
      }

      Method partialTextMethod = this.findMethod(endpointClass, "onWebSocketPartialText", String.class, boolean.class);
      if (partialTextMethod != null) {
         MethodHandle partialTextHandle = this.toMethodHandle(lookup, partialTextMethod);
         metadata.setTextHandle(PartialStringMessageSink.class, partialTextHandle, partialTextMethod);
      }

      Method partialBinaryMethod = this.findMethod(endpointClass, "onWebSocketPartialBinary", ByteBuffer.class, boolean.class, Callback.class);
      if (partialBinaryMethod != null) {
         MethodHandle partialBinaryHandle = this.toMethodHandle(lookup, partialBinaryMethod);
         metadata.setBinaryHandle(PartialByteBufferMessageSink.class, partialBinaryHandle, partialBinaryMethod);
      }

      Method textMethod = this.findMethod(endpointClass, "onWebSocketText", String.class);
      if (textMethod != null) {
         MethodHandle textHandle = this.toMethodHandle(lookup, textMethod);
         metadata.setTextHandle(StringMessageSink.class, textHandle, textMethod);
      }

      Method binaryMethod = this.findMethod(endpointClass, "onWebSocketBinary", ByteBuffer.class, Callback.class);
      if (binaryMethod != null) {
         MethodHandle binaryHandle = this.toMethodHandle(lookup, binaryMethod);
         metadata.setBinaryHandle(ByteBufferMessageSink.class, binaryHandle, binaryMethod);
      }

      Method errorMethod = this.findMethod(endpointClass, "onWebSocketError", Throwable.class);
      if (errorMethod != null) {
         MethodHandle errorHandle = this.toMethodHandle(lookup, errorMethod);
         metadata.setErrorHandle(errorHandle, errorMethod);
      }

      Method closeMethod = this.findMethod(endpointClass, "onWebSocketClose", int.class, String.class);
      if (closeMethod != null) {
         MethodHandle closeHandle = this.toMethodHandle(lookup, closeMethod);
         metadata.setCloseHandle(closeHandle, closeMethod);
      }

      return metadata;
   }

   private Method findMethod(Class<?> klass, String name, Class<?>... parameters) {
      Method method = ReflectUtils.findMethod(klass, name, parameters);
      if (method == null) {
         return null;
      } else if (!this.isOverridden(method)) {
         return null;
      } else {
         if (!Modifier.isPublic(klass.getModifiers())) {
            method = ReflectUtils.findMethod(Listener.class, name, parameters);
         }

         return method;
      }
   }

   private boolean isOverridden(Method method) {
      return method != null && method.getDeclaringClass() != Listener.class;
   }

   private JettyWebSocketFrameHandlerMetadata createAnnotatedMetadata(WebSocket anno, Class<?> endpointClass) {
      JettyWebSocketFrameHandlerMetadata metadata = new JettyWebSocketFrameHandlerMetadata();
      metadata.setAutoDemand(anno.autoDemand());
      Lookup lookup = getApplicationMethodHandleLookup(endpointClass);
      Method onmethod = ReflectUtils.findAnnotatedMethod(endpointClass, OnWebSocketOpen.class);
      if (onmethod != null) {
         this.assertSignatureValid(endpointClass, onmethod, OnWebSocketOpen.class);
         Arg SESSION = new Arg(Session.class).required();
         MethodHandle methodHandle = InvokerUtils.mutatedInvoker(lookup, endpointClass, onmethod, new Arg[]{SESSION});
         metadata.setOpenHandle(methodHandle, onmethod);
      }

      onmethod = ReflectUtils.findAnnotatedMethod(endpointClass, OnWebSocketClose.class);
      if (onmethod != null) {
         this.assertSignatureValid(endpointClass, onmethod, OnWebSocketClose.class);
         Arg SESSION = new Arg(Session.class);
         Arg STATUS_CODE = new Arg(int.class);
         Arg REASON = new Arg(String.class);
         MethodHandle methodHandle = InvokerUtils.mutatedInvoker(lookup, endpointClass, onmethod, new Arg[]{SESSION, STATUS_CODE, REASON});
         metadata.setCloseHandle(methodHandle, onmethod);
      }

      onmethod = ReflectUtils.findAnnotatedMethod(endpointClass, OnWebSocketError.class);
      if (onmethod != null) {
         this.assertSignatureValid(endpointClass, onmethod, OnWebSocketError.class);
         Arg SESSION = new Arg(Session.class);
         Arg CAUSE = new Arg(Throwable.class).required();
         MethodHandle methodHandle = InvokerUtils.mutatedInvoker(lookup, endpointClass, onmethod, new Arg[]{SESSION, CAUSE});
         metadata.setErrorHandle(methodHandle, onmethod);
      }

      onmethod = ReflectUtils.findAnnotatedMethod(endpointClass, OnWebSocketFrame.class);
      if (onmethod != null) {
         this.assertSignatureValid(endpointClass, onmethod, OnWebSocketFrame.class);
         Arg SESSION = new Arg(Session.class);
         Arg FRAME = new Arg(Frame.class).required();
         Arg CALLBACK = new Arg(Callback.class).required();
         MethodHandle methodHandle = InvokerUtils.mutatedInvoker(lookup, endpointClass, onmethod, new Arg[]{SESSION, FRAME, CALLBACK});
         metadata.setFrameHandle(methodHandle, onmethod);
      }

      Method[] onMessages = ReflectUtils.findAnnotatedMethods(endpointClass, OnWebSocketMessage.class);
      if (onMessages != null) {
         for (Method onMsg : onMessages) {
            this.assertSignatureValid(endpointClass, onMsg, OnWebSocketMessage.class);
            MethodHandle methodHandle = InvokerUtils.optionalMutatedInvoker(lookup, endpointClass, onMsg, textCallingArgs);
            if (methodHandle != null) {
               this.assertSignatureValid(endpointClass, onMsg, OnWebSocketMessage.class);
               metadata.setTextHandle(StringMessageSink.class, methodHandle, onMsg);
            } else {
               methodHandle = InvokerUtils.optionalMutatedInvoker(lookup, endpointClass, onMsg, binaryBufferCallingArgs);
               if (methodHandle != null) {
                  this.assertSignatureValid(endpointClass, onMsg, OnWebSocketMessage.class);
                  metadata.setBinaryHandle(ByteBufferMessageSink.class, methodHandle, onMsg);
               } else {
                  methodHandle = InvokerUtils.optionalMutatedInvoker(lookup, endpointClass, onMsg, inputStreamCallingArgs);
                  if (methodHandle != null) {
                     if (!metadata.isAutoDemand()) {
                        throw new InvalidWebSocketException("InputStream methods require auto-demanding WebSocket endpoints");
                     }

                     this.assertSignatureValid(endpointClass, onMsg, OnWebSocketMessage.class);
                     metadata.setBinaryHandle(InputStreamMessageSink.class, methodHandle, onMsg);
                  } else {
                     methodHandle = InvokerUtils.optionalMutatedInvoker(lookup, endpointClass, onMsg, readerCallingArgs);
                     if (methodHandle != null) {
                        if (!metadata.isAutoDemand()) {
                           throw new InvalidWebSocketException("Reader methods require auto-demanding WebSocket endpoints");
                        }

                        this.assertSignatureValid(endpointClass, onMsg, OnWebSocketMessage.class);
                        metadata.setTextHandle(ReaderMessageSink.class, methodHandle, onMsg);
                     } else {
                        methodHandle = InvokerUtils.optionalMutatedInvoker(lookup, endpointClass, onMsg, textPartialCallingArgs);
                        if (methodHandle != null) {
                           this.assertSignatureValid(endpointClass, onMsg, OnWebSocketMessage.class);
                           metadata.setTextHandle(PartialStringMessageSink.class, methodHandle, onMsg);
                        } else {
                           methodHandle = InvokerUtils.optionalMutatedInvoker(lookup, endpointClass, onMsg, binaryPartialBufferCallingArgs);
                           if (methodHandle == null) {
                              throw InvalidSignatureException.build(endpointClass, OnWebSocketMessage.class, onMsg);
                           }

                           this.assertSignatureValid(endpointClass, onMsg, OnWebSocketMessage.class);
                           metadata.setBinaryHandle(PartialByteBufferMessageSink.class, methodHandle, onMsg);
                        }
                     }
                  }
               }
            }
         }
      }

      return metadata;
   }

   private void assertSignatureValid(Class<?> endpointClass, Method method, Class<? extends Annotation> annotationClass) {
      int mods = method.getModifiers();
      if (!Modifier.isPublic(mods)) {
         StringBuilder err = new StringBuilder();
         err.append("@").append(annotationClass.getSimpleName());
         err.append(" method must be public: ");
         ReflectUtils.append(err, endpointClass, method);
         throw new InvalidSignatureException(err.toString());
      } else if (Modifier.isStatic(mods)) {
         StringBuilder err = new StringBuilder();
         err.append("@").append(annotationClass.getSimpleName());
         err.append(" method must not be static: ");
         ReflectUtils.append(err, endpointClass, method);
         throw new InvalidSignatureException(err.toString());
      } else {
         Class<?> returnType = method.getReturnType();
         if (returnType != void.class && returnType != Void.class) {
            StringBuilder err = new StringBuilder();
            err.append("@").append(annotationClass.getSimpleName());
            err.append(" return must be void: ");
            ReflectUtils.append(err, endpointClass, method);
            throw new InvalidSignatureException(err.toString());
         }
      }
   }

   public static Lookup getServerMethodHandleLookup() {
      return MethodHandles.lookup();
   }

   public static Lookup getApplicationMethodHandleLookup(Class<?> lookupClass) {
      return MethodHandles.publicLookup().in(lookupClass);
   }

   public void dump(Appendable out, String indent) throws IOException {
      this.dumpObjects(out, indent, new Object[]{this.metadataMap});
   }
}
