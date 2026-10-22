package org.eclipse.jetty.util.preventers;

import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractLeakPreventer extends AbstractLifeCycle {
   protected static final Logger LOG = LoggerFactory.getLogger(AbstractLeakPreventer.class);

   public abstract void prevent(ClassLoader var1);

   @Override
   protected void doStart() throws Exception {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();

      try {
         Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
         this.prevent(this.getClass().getClassLoader());
         super.doStart();
      } finally {
         Thread.currentThread().setContextClassLoader(loader);
      }
   }
}
