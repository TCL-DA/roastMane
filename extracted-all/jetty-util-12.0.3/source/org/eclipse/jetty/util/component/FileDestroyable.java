package org.eclipse.jetty.util.component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.resource.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileDestroyable implements Destroyable {
   private static final Logger LOG = LoggerFactory.getLogger(FileDestroyable.class);
   final List<Path> _paths = new ArrayList<>();
   private final ContainerLifeCycle mountContainer = new ContainerLifeCycle();

   public FileDestroyable() {
   }

   public FileDestroyable(String file) {
      this._paths.add(ResourceFactory.of(this.mountContainer).newResource(file).getPath());
   }

   public FileDestroyable(Path path) {
      this._paths.add(path);
   }

   public void addFile(String file) {
      this._paths.add(ResourceFactory.of(this.mountContainer).newResource(file).getPath());
   }

   public void addPath(Path path) {
      this._paths.add(path);
   }

   public void addPaths(Collection<Path> paths) {
      this._paths.addAll(paths);
   }

   public void removeFile(String file) {
      this._paths.remove(ResourceFactory.of(this.mountContainer).newResource(file).getPath());
   }

   public void removeFile(Path path) {
      this._paths.remove(path);
   }

   @Override
   public void destroy() {
      for (Path path : this._paths) {
         if (Files.exists(path)) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Destroy {}", path);
            }

            IO.delete(path);
         }
      }

      LifeCycle.stop(this.mountContainer);
   }
}
