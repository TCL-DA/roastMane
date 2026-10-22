package org.eclipse.jetty.websocket.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.jetty.http.BadMessageException;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.websocket.core.exception.WebSocketException;
import org.eclipse.jetty.websocket.core.util.DemandChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject("Extension Stack")
public class ExtensionStack implements IncomingFrames, OutgoingFrames, Dumpable {
   private static final Logger LOG = LoggerFactory.getLogger(ExtensionStack.class);
   private final WebSocketComponents components;
   private final Behavior behavior;
   private List<Extension> extensions;
   private IncomingFrames incoming;
   private OutgoingFrames outgoing;
   private final Extension[] rsvClaims = new Extension[3];
   private DemandChain lastDemand;
   private DemandChain demandChain = () -> this.lastDemand.demand();

   public ExtensionStack(WebSocketComponents components, Behavior behavior) {
      this.components = components;
      this.behavior = behavior;
   }

   public void close() {
      for (Extension ext : this.extensions) {
         try {
            ext.close();
         } catch (Throwable var4) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Extension Error During Close", var4);
            }
         }
      }
   }

   @ManagedAttribute(
      name = "Extension List",
      readonly = true
   )
   public List<Extension> getExtensions() {
      return this.extensions;
   }

   public List<ExtensionConfig> getNegotiatedExtensions() {
      return this.extensions == null
         ? Collections.emptyList()
         : this.extensions.stream().filter(e -> !e.getName().startsWith("@")).map(Extension::getConfig).collect(Collectors.toList());
   }

   @ManagedAttribute(
      name = "Next Incoming Frames Handler",
      readonly = true
   )
   public IncomingFrames getNextIncoming() {
      return this.incoming;
   }

   @ManagedAttribute(
      name = "Next Outgoing Frames Handler",
      readonly = true
   )
   public OutgoingFrames getNextOutgoing() {
      return this.outgoing;
   }

   public boolean hasNegotiatedExtensions() {
      return this.extensions != null && this.extensions.size() > 0;
   }

   @Override
   public void onFrame(Frame frame, Callback callback) {
      if (this.incoming == null) {
         throw new IllegalStateException();
      } else {
         this.incoming.onFrame(frame, callback);
      }
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void negotiate(List<ExtensionConfig> offeredConfigs, List<ExtensionConfig> negotiatedConfigs) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Extension Configs={}", negotiatedConfigs);
      }

      this.extensions = new ArrayList<>();

      for (ExtensionConfig config : negotiatedConfigs) {
         Extension ext;
         try {
            ext = this.components.getExtensionRegistry().newInstance(config, this.components);
         } catch (Throwable var10) {
            switch (this.behavior) {
               case SERVER:
                  String parameterizedName = config.getParameterizedName();

                  for (ExtensionConfig offeredx : offeredConfigs) {
                     if (offeredx.getParameterizedName().equals(parameterizedName)) {
                        throw new BadMessageException("could not instantiate offered extension", var10);
                     }
                  }

                  throw new WebSocketException("could not instantiate negotiated extension", var10);
               case CLIENT:
                  String parameterizedName = config.getParameterizedName();

                  for (ExtensionConfig offered : offeredConfigs) {
                     if (offered.getParameterizedName().equals(parameterizedName)) {
                        throw new WebSocketException("could not instantiate offered extension", var10);
                     }
                  }

                  throw new BadMessageException("could not instantiate negotiated extension", var10);
               default:
                  throw new IllegalStateException();
            }
         }

         if (ext != null) {
            if (ext.isRsv1User() && this.rsvClaims[0] != null) {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Not adding extension {}. Extension {} already claimed RSV1", config, this.rsvClaims[0]);
               }
            } else if (ext.isRsv2User() && this.rsvClaims[1] != null) {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Not adding extension {}. Extension {} already claimed RSV2", config, this.rsvClaims[1]);
               }
            } else if (!ext.isRsv3User() || this.rsvClaims[2] == null) {
               this.extensions.add(ext);
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Adding Extension: {}", config);
               }

               if (ext.isRsv1User()) {
                  this.rsvClaims[0] = ext;
               }

               if (ext.isRsv2User()) {
                  this.rsvClaims[1] = ext;
               }

               if (ext.isRsv3User()) {
                  this.rsvClaims[2] = ext;
               }
            } else if (LOG.isDebugEnabled()) {
               LOG.debug("Not adding extension {}. Extension {} already claimed RSV3", config, this.rsvClaims[2]);
            }
         }
      }

      if (this.extensions != null && this.extensions.size() > 0) {
         for (Extension extx : this.extensions) {
            extx.setNextOutgoingFrames(this.outgoing);
            this.outgoing = extx;
            if (extx instanceof DemandChain demandingExtension) {
               demandingExtension.setNextDemand(this.demandChain);
               this.demandChain = demandingExtension;
            }
         }

         <unknown> var11;
         while (var11.hasPrevious()) {
            Extension extxx = (Extension)var11.previous();
            extxx.setNextIncomingFrames(this.incoming);
            this.incoming = extxx;
         }
      }
   }

   @Override
   public void sendFrame(Frame frame, Callback callback, boolean batch) {
      if (this.outgoing == null) {
         throw new IllegalStateException();
      } else {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Extending out {} {} {}", new Object[]{frame, callback, batch});
         }

         this.outgoing.sendFrame(frame, callback, batch);
      }
   }

   public void initialize(IncomingFrames incoming, OutgoingFrames outgoing, CoreSession coreSession) {
      if (this.extensions == null) {
         throw new IllegalStateException();
      } else {
         if (this.extensions.isEmpty()) {
            this.incoming = incoming;
            this.outgoing = outgoing;
         } else {
            this.extensions.get(0).setNextOutgoingFrames(outgoing);
            this.extensions.get(this.extensions.size() - 1).setNextIncomingFrames(incoming);
         }

         for (Extension extension : this.extensions) {
            extension.setCoreSession(coreSession);
         }
      }
   }

   public void demand() {
      this.demandChain.demand();
   }

   public void setLastDemand(DemandChain lastDemand) {
      this.lastDemand = lastDemand;
   }

   public Extension getRsv1User() {
      return this.rsvClaims[0];
   }

   public Extension getRsv2User() {
      return this.rsvClaims[1];
   }

   public Extension getRsv3User() {
      return this.rsvClaims[2];
   }

   public boolean isRsv1Used() {
      return this.rsvClaims[0] != null;
   }

   public boolean isRsv2Used() {
      return this.rsvClaims[1] != null;
   }

   public boolean isRsv3Used() {
      return this.rsvClaims[2] != null;
   }

   public String dump() {
      return Dumpable.dump(this);
   }

   public void dump(Appendable out, String indent) throws IOException {
      Dumpable.dumpObjects(out, indent, this, new Object[]{this.extensions == null ? Collections.emptyList() : this.extensions});
   }

   public String dumpSelf() {
      return String.format("%s@%x[size=%d]", this.getClass().getSimpleName(), this.hashCode(), this.extensions.size());
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      s.append("ExtensionStack[extensions=");
      if (this.extensions == null) {
         s.append("<null>");
      } else {
         s.append('[');
         boolean delim = false;

         for (Extension ext : this.extensions) {
            if (delim) {
               s.append(',');
            }

            if (ext == null) {
               s.append("<null>");
            } else {
               s.append(ext.getName());
            }

            delim = true;
         }

         s.append(']');
      }

      s.append(",incoming=").append(this.incoming == null ? "<null>" : this.incoming.getClass().getName());
      s.append(",outgoing=").append(this.outgoing == null ? "<null>" : this.outgoing.getClass().getName());
      s.append("]");
      return s.toString();
   }
}
