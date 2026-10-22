package nonapi.io.github.classgraph.classpath;

import io.github.classgraph.ModuleRef;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import nonapi.io.github.classgraph.reflection.ReflectionUtils;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.CollectionUtils;
import nonapi.io.github.classgraph.utils.LogNode;

public class ModuleFinder {
   private List<ModuleRef> systemModuleRefs;
   private List<ModuleRef> nonSystemModuleRefs;
   private boolean forceScanJavaClassPath;
   private final ReflectionUtils reflectionUtils;

   public List<ModuleRef> getSystemModuleRefs() {
      return this.systemModuleRefs;
   }

   public List<ModuleRef> getNonSystemModuleRefs() {
      return this.nonSystemModuleRefs;
   }

   public boolean forceScanJavaClassPath() {
      return this.forceScanJavaClassPath;
   }

   private void findLayerOrder(Object layer, Set<Object> layerVisited, Set<Object> parentLayers, Deque<Object> layerOrderOut) {
      if (layerVisited.add(layer)) {
         List<Object> parents = (List<Object>)this.reflectionUtils.invokeMethod(true, layer, "parents");
         if (parents != null) {
            parentLayers.addAll(parents);

            for (Object parent : parents) {
               this.findLayerOrder(parent, layerVisited, parentLayers, layerOrderOut);
            }
         }

         layerOrderOut.push(layer);
      }
   }

   private List<ModuleRef> findModuleRefs(LinkedHashSet<Object> layers, ScanSpec scanSpec, LogNode log) {
      if (layers.isEmpty()) {
         return Collections.emptyList();
      } else {
         Deque<Object> layerOrder = new ArrayDeque<>();
         Set<Object> parentLayers = new HashSet<>();

         for (Object layer : layers) {
            if (layer != null) {
               this.findLayerOrder(layer, new HashSet<>(), parentLayers, layerOrder);
            }
         }

         if (scanSpec.addedModuleLayers != null) {
            for (Object layerx : scanSpec.addedModuleLayers) {
               if (layerx != null) {
                  this.findLayerOrder(layerx, new HashSet<>(), parentLayers, layerOrder);
               }
            }
         }

         List<Object> layerOrderFinal;
         if (scanSpec.ignoreParentModuleLayers) {
            layerOrderFinal = new ArrayList<>();

            for (Object layerxx : layerOrder) {
               if (!parentLayers.contains(layerxx)) {
                  layerOrderFinal.add(layerxx);
               }
            }
         } else {
            layerOrderFinal = new ArrayList<>(layerOrder);
         }

         Set<Object> addedModules = new HashSet<>();
         LinkedHashSet<ModuleRef> moduleRefOrder = new LinkedHashSet<>();

         for (Object layerxxx : layerOrderFinal) {
            Object configuration = this.reflectionUtils.invokeMethod(true, layerxxx, "configuration");
            if (configuration != null) {
               Set<Object> modules = (Set<Object>)this.reflectionUtils.invokeMethod(true, configuration, "modules");
               if (modules != null) {
                  List<ModuleRef> modulesInLayer = new ArrayList<>();

                  for (Object module : modules) {
                     Object moduleReference = this.reflectionUtils.invokeMethod(true, module, "reference");
                     if (moduleReference != null && addedModules.add(moduleReference)) {
                        try {
                           modulesInLayer.add(new ModuleRef(moduleReference, layerxxx, this.reflectionUtils));
                        } catch (IllegalArgumentException var18) {
                           if (log != null) {
                              log.log("Exception while creating ModuleRef for module " + moduleReference, var18);
                           }
                        }
                     }
                  }

                  CollectionUtils.sortIfNotEmpty(modulesInLayer);
                  moduleRefOrder.addAll(modulesInLayer);
               }
            }
         }

         return new ArrayList<>(moduleRefOrder);
      }
   }

   private List<ModuleRef> findModuleRefsFromCallstack(Class<?>[] callStack, ScanSpec scanSpec, boolean scanNonSystemModules, LogNode log) {
      LinkedHashSet<Object> layers = new LinkedHashSet<>();
      if (callStack != null) {
         for (Class<?> stackFrameClass : callStack) {
            Object module = this.reflectionUtils.invokeMethod(false, stackFrameClass, "getModule");
            if (module != null) {
               Object layer = this.reflectionUtils.invokeMethod(true, module, "getLayer");
               if (layer != null) {
                  layers.add(layer);
               } else if (scanNonSystemModules) {
                  this.forceScanJavaClassPath = true;
               }
            }
         }
      }

      Class<?> moduleLayerClass = null;

      try {
         moduleLayerClass = Class.forName("java.lang.ModuleLayer");
      } catch (LinkageError | ClassNotFoundException var12) {
      }

      if (moduleLayerClass != null) {
         Object bootLayer = this.reflectionUtils.invokeStaticMethod(false, moduleLayerClass, "boot");
         if (bootLayer != null) {
            layers.add(bootLayer);
         } else if (scanNonSystemModules) {
            this.forceScanJavaClassPath = true;
         }
      }

      return this.findModuleRefs(layers, scanSpec, log);
   }

   public ModuleFinder(
      Class<?>[] callStack, ScanSpec scanSpec, boolean scanNonSystemModules, boolean scanSystemModules, ReflectionUtils reflectionUtils, LogNode log
   ) {
      this.reflectionUtils = reflectionUtils;
      List<ModuleRef> allModuleRefsList = null;
      if (scanSpec.overrideModuleLayers == null) {
         if (callStack != null && callStack.length > 0) {
            allModuleRefsList = this.findModuleRefsFromCallstack(callStack, scanSpec, scanNonSystemModules, log);
         }
      } else {
         if (log != null) {
            LogNode subLog = log.log("Overriding module layers");

            for (Object moduleLayer : scanSpec.overrideModuleLayers) {
               subLog.log(moduleLayer.toString());
            }
         }

         allModuleRefsList = this.findModuleRefs(new LinkedHashSet<>(scanSpec.overrideModuleLayers), scanSpec, log);
      }

      if (allModuleRefsList != null) {
         this.systemModuleRefs = new ArrayList<>();
         this.nonSystemModuleRefs = new ArrayList<>();

         for (ModuleRef moduleRef : allModuleRefsList) {
            if (moduleRef != null) {
               boolean isSystemModule = moduleRef.isSystemModule();
               if (isSystemModule && scanSystemModules) {
                  this.systemModuleRefs.add(moduleRef);
               } else if (!isSystemModule && scanNonSystemModules) {
                  this.nonSystemModuleRefs.add(moduleRef);
               }
            }
         }
      }

      if (log != null) {
         if (scanSystemModules) {
            LogNode sysSubLog = log.log("System modules found:");
            if (this.systemModuleRefs != null && !this.systemModuleRefs.isEmpty()) {
               for (ModuleRef moduleRefx : this.systemModuleRefs) {
                  sysSubLog.log(moduleRefx.toString());
               }
            } else {
               sysSubLog.log("[None]");
            }
         } else {
            log.log("Scanning of system modules is not enabled");
         }

         if (scanNonSystemModules) {
            LogNode nonSysSubLog = log.log("Non-system modules found:");
            if (this.nonSystemModuleRefs != null && !this.nonSystemModuleRefs.isEmpty()) {
               for (ModuleRef moduleRefx : this.nonSystemModuleRefs) {
                  nonSysSubLog.log(moduleRefx.toString());
               }
            } else {
               nonSysSubLog.log("[None]");
            }
         } else {
            log.log("Scanning of non-system modules is not enabled");
         }
      }
   }
}
