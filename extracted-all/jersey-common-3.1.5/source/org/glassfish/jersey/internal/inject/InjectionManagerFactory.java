package org.glassfish.jersey.internal.inject;

public interface InjectionManagerFactory {
   default InjectionManager create() {
      return this.create(null);
   }

   InjectionManager create(Object var1);
}
