package io.github.classgraph;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import nonapi.io.github.classgraph.reflection.ReflectionUtils;
import nonapi.io.github.classgraph.utils.CollectionUtils;

public class ModuleRef implements Comparable<ModuleRef> {
   private final String name;
   private final Object reference;
   private final Object layer;
   private final Object descriptor;
   private final List<String> packages;
   private final URI location;
   private String locationStr;
   private File locationFile;
   private String rawVersion;
   private final ClassLoader classLoader;
   ReflectionUtils reflectionUtils;

   public ModuleRef(Object moduleReference, Object moduleLayer, ReflectionUtils reflectionUtils) {
      if (moduleReference == null) {
         throw new IllegalArgumentException("moduleReference cannot be null");
      } else if (moduleLayer == null) {
         throw new IllegalArgumentException("moduleLayer cannot be null");
      } else {
         this.reference = moduleReference;
         this.layer = moduleLayer;
         this.reflectionUtils = reflectionUtils;
         this.descriptor = reflectionUtils.invokeMethod(true, moduleReference, "descriptor");
         if (this.descriptor == null) {
            throw new IllegalArgumentException("moduleReference.descriptor() should not return null");
         } else {
            this.name = (String)reflectionUtils.invokeMethod(true, this.descriptor, "name");
            Set<String> modulePackages = (Set<String>)reflectionUtils.invokeMethod(true, this.descriptor, "packages");
            if (modulePackages == null) {
               throw new IllegalArgumentException("moduleReference.descriptor().packages() should not return null");
            } else {
               this.packages = new ArrayList<>(modulePackages);
               CollectionUtils.sortIfNotEmpty(this.packages);
               Object optionalRawVersion = reflectionUtils.invokeMethod(true, this.descriptor, "rawVersion");
               if (optionalRawVersion != null) {
                  Boolean isPresent = (Boolean)reflectionUtils.invokeMethod(true, optionalRawVersion, "isPresent");
                  if (isPresent != null && isPresent) {
                     this.rawVersion = (String)reflectionUtils.invokeMethod(true, optionalRawVersion, "get");
                  }
               }

               Object moduleLocationOptional = reflectionUtils.invokeMethod(true, moduleReference, "location");
               if (moduleLocationOptional == null) {
                  throw new IllegalArgumentException("moduleReference.location() should not return null");
               } else {
                  Object moduleLocationIsPresent = reflectionUtils.invokeMethod(true, moduleLocationOptional, "isPresent");
                  if (moduleLocationIsPresent == null) {
                     throw new IllegalArgumentException("moduleReference.location().isPresent() should not return null");
                  } else {
                     if ((Boolean)moduleLocationIsPresent) {
                        this.location = (URI)reflectionUtils.invokeMethod(true, moduleLocationOptional, "get");
                        if (this.location == null) {
                           throw new IllegalArgumentException("moduleReference.location().get() should not return null");
                        }
                     } else {
                        this.location = null;
                     }

                     this.classLoader = (ClassLoader)reflectionUtils.invokeMethod(true, moduleLayer, "findLoader", String.class, this.name);
                  }
               }
            }
         }
      }
   }

   public String getName() {
      return this.name;
   }

   public Object getReference() {
      return this.reference;
   }

   public Object getLayer() {
      return this.layer;
   }

   public Object getDescriptor() {
      return this.descriptor;
   }

   public List<String> getPackages() {
      return this.packages;
   }

   public URI getLocation() {
      return this.location;
   }

   public String getLocationStr() {
      if (this.locationStr == null && this.location != null) {
         this.locationStr = this.location.toString();
      }

      return this.locationStr;
   }

   public File getLocationFile() {
      if (this.locationFile == null && this.location != null && "file".equals(this.location.getScheme())) {
         this.locationFile = new File(this.location);
      }

      return this.locationFile;
   }

   public String getRawVersion() {
      return this.rawVersion;
   }

   public boolean isSystemModule() {
      return this.name != null && !this.name.isEmpty()
         ? this.name.startsWith("java.") || this.name.startsWith("jdk.") || this.name.startsWith("javafx.") || this.name.startsWith("oracle.")
         : false;
   }

   public ClassLoader getClassLoader() {
      return this.classLoader;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof ModuleRef)) {
         return false;
      } else {
         ModuleRef modRef = (ModuleRef)obj;
         return modRef.reference.equals(this.reference) && modRef.layer.equals(this.layer);
      }
   }

   @Override
   public int hashCode() {
      return this.reference.hashCode() * this.layer.hashCode();
   }

   @Override
   public String toString() {
      return this.reference.toString();
   }

   public int compareTo(ModuleRef o) {
      int diff = this.name.compareTo(o.name);
      return diff != 0 ? diff : this.hashCode() - o.hashCode();
   }

   public ModuleReaderProxy open() throws IOException {
      return new ModuleReaderProxy(this);
   }
}
