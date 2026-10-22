package org.glassfish.jersey.internal.inject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class CompositeBinder extends AbstractBinder {
   private Collection<Binder> installed = new ArrayList<>();

   private CompositeBinder(Collection<Binder> installed) {
      this.installed = installed;
   }

   public static AbstractBinder wrap(Collection<Binder> binders) {
      return new CompositeBinder(binders);
   }

   public static AbstractBinder wrap(Binder... binders) {
      return new CompositeBinder(Arrays.asList(binders));
   }

   @Override
   public void configure() {
      this.install(this.installed.toArray(new AbstractBinder[0]));
   }
}
