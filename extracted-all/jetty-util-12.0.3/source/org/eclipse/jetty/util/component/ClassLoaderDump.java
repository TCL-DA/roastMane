package org.eclipse.jetty.util.component;

import java.io.IOException;
import java.net.URLClassLoader;

public class ClassLoaderDump implements Dumpable {
   final ClassLoader _loader;

   public ClassLoaderDump(ClassLoader loader) {
      this._loader = loader;
   }

   @Override
   public String dump() {
      return Dumpable.dump(this);
   }

   @Override
   public void dump(Appendable out, String indent) throws IOException {
      if (this._loader == null) {
         out.append("No ClassLoader\n");
      } else if (this._loader instanceof Dumpable) {
         ((Dumpable)this._loader).dump(out, indent);
      } else if (this._loader instanceof URLClassLoader) {
         String loader = this._loader.toString();
         DumpableCollection urls = DumpableCollection.fromArray("URLs", ((URLClassLoader)this._loader).getURLs());
         ClassLoader parent = this._loader.getParent();
         if (parent == null) {
            Dumpable.dumpObjects(out, indent, loader, urls);
         } else if (parent == ClassLoaderDump.class.getClassLoader()) {
            Dumpable.dumpObjects(out, indent, loader, urls, Dumpable.named("parent(core)", parent.toString()));
         } else if (parent instanceof Dumpable) {
            Dumpable.dumpObjects(out, indent, loader, urls, Dumpable.named("parent", parent));
         } else {
            Dumpable.dumpObjects(out, indent, loader, urls, Dumpable.named("parent", new ClassLoaderDump(parent)));
         }
      } else if (this._loader.getDefinedPackages() != null && this._loader.getDefinedPackages().length > 0) {
         DumpableCollection packages = DumpableCollection.from("packages", this._loader.getDefinedPackages());
         ClassLoader parent = this._loader.getParent();
         if (parent == ClassLoaderDump.class.getClassLoader()) {
            Dumpable.dumpObjects(out, indent, this._loader, packages, Dumpable.named("parent(core)", parent.toString()));
         } else if (parent instanceof Dumpable) {
            Dumpable.dumpObjects(out, indent, this._loader, packages, Dumpable.named("parent", parent));
         } else if (parent != null) {
            Dumpable.dumpObjects(out, indent, this._loader, packages, Dumpable.named("parent", new ClassLoaderDump(parent)));
         } else {
            Dumpable.dumpObjects(out, indent, this._loader, packages);
         }
      } else {
         String loader = this._loader.toString();
         ClassLoader parent = this._loader.getParent();
         if (parent == null) {
            Dumpable.dumpObject(out, loader);
         } else if (parent == ClassLoaderDump.class.getClassLoader()) {
            Dumpable.dumpObjects(out, indent, loader, Dumpable.named("parent(core)", parent.toString()));
         } else if (parent instanceof Dumpable) {
            Dumpable.dumpObjects(out, indent, loader, Dumpable.named("parent", parent));
         } else {
            Dumpable.dumpObjects(out, indent, loader, Dumpable.named("parent", new ClassLoaderDump(parent)));
         }
      }
   }
}
