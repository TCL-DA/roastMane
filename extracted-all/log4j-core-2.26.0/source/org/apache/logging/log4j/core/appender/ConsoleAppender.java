package org.apache.logging.log4j.core.appender;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.util.Booleans;
import org.apache.logging.log4j.core.util.CloseShieldOutputStream;
import org.apache.logging.log4j.util.PropertiesUtil;

@Plugin(
   name = "Console",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public final class ConsoleAppender extends AbstractOutputStreamAppender<OutputStreamManager> {
   public static final String PLUGIN_NAME = "Console";
   private static final ConsoleAppender.ConsoleManagerFactory factory = new ConsoleAppender.ConsoleManagerFactory();
   private static final ConsoleAppender.Target DEFAULT_TARGET = ConsoleAppender.Target.SYSTEM_OUT;
   private static final AtomicInteger COUNT = new AtomicInteger();
   private final ConsoleAppender.Target target;

   private ConsoleAppender(
      final String name,
      final Layout<? extends Serializable> layout,
      final Filter filter,
      final OutputStreamManager manager,
      final boolean ignoreExceptions,
      final ConsoleAppender.Target target,
      final Property[] properties
   ) {
      super(name, layout, filter, ignoreExceptions, true, properties, manager);
      this.target = target;
   }

   @Deprecated
   public static ConsoleAppender createAppender(
      Layout<? extends Serializable> layout, final Filter filter, final String target, final String name, final String follow, final String ignoreExceptions
   ) {
      return ((ConsoleAppender.Builder)((ConsoleAppender.Builder)((ConsoleAppender.Builder)((ConsoleAppender.Builder)newBuilder().setLayout(layout))
                  .setFilter(filter))
               .setTarget(target == null ? DEFAULT_TARGET : ConsoleAppender.Target.valueOf(target))
               .setName(name))
            .setFollow(Boolean.parseBoolean(follow))
            .setIgnoreExceptions(Booleans.parseBoolean(ignoreExceptions, true)))
         .build();
   }

   @Deprecated
   public static ConsoleAppender createAppender(
      Layout<? extends Serializable> layout,
      final Filter filter,
      ConsoleAppender.Target target,
      final String name,
      final boolean follow,
      final boolean direct,
      final boolean ignoreExceptions
   ) {
      return ((ConsoleAppender.Builder)((ConsoleAppender.Builder)((ConsoleAppender.Builder)((ConsoleAppender.Builder)newBuilder().setLayout(layout))
                  .setFilter(filter))
               .setTarget(target == null ? DEFAULT_TARGET : target)
               .setName(name))
            .setFollow(follow)
            .setDirect(direct)
            .setIgnoreExceptions(ignoreExceptions))
         .build();
   }

   public static ConsoleAppender createDefaultAppenderForLayout(final Layout<? extends Serializable> layout) {
      return new ConsoleAppender("DefaultConsole-" + COUNT.incrementAndGet(), layout, null, getDefaultManager(layout), true, DEFAULT_TARGET, null);
   }

   @PluginBuilderFactory
   public static <B extends ConsoleAppender.Builder<B>> B newBuilder() {
      return new ConsoleAppender.Builder<B>().asBuilder();
   }

   private static OutputStreamManager getDefaultManager(final Layout<? extends Serializable> layout) {
      OutputStream os = getDefaultOutputStream(DEFAULT_TARGET);
      String managerName = DEFAULT_TARGET.name() + ".false.false-" + COUNT.get();
      return OutputStreamManager.getManager(managerName, new ConsoleAppender.FactoryData(os, managerName, layout), factory);
   }

   private static OutputStream getDefaultOutputStream(ConsoleAppender.Target target) {
      return new CloseShieldOutputStream(target == ConsoleAppender.Target.SYSTEM_OUT ? System.out : System.err);
   }

   private static OutputStream getDirectOutputStream(ConsoleAppender.Target target) {
      return new CloseShieldOutputStream(new FileOutputStream(target == ConsoleAppender.Target.SYSTEM_OUT ? FileDescriptor.out : FileDescriptor.err));
   }

   private static OutputStream getFollowOutputStream(ConsoleAppender.Target target) {
      return (OutputStream)(target == ConsoleAppender.Target.SYSTEM_OUT ? new ConsoleAppender.SystemOutStream() : new ConsoleAppender.SystemErrStream());
   }

   public ConsoleAppender.Target getTarget() {
      return this.target;
   }

   public static class Builder<B extends ConsoleAppender.Builder<B>>
      extends AbstractOutputStreamAppender.Builder<B>
      implements org.apache.logging.log4j.core.util.Builder<ConsoleAppender> {
      @PluginBuilderAttribute
      @Required
      private ConsoleAppender.Target target = ConsoleAppender.DEFAULT_TARGET;
      @PluginBuilderAttribute
      private boolean follow;
      @PluginBuilderAttribute
      private boolean direct;

      public B setTarget(final ConsoleAppender.Target aTarget) {
         this.target = aTarget;
         return this.asBuilder();
      }

      public B setFollow(final boolean shouldFollow) {
         this.follow = shouldFollow;
         return this.asBuilder();
      }

      public B setDirect(final boolean shouldDirect) {
         this.direct = shouldDirect;
         return this.asBuilder();
      }

      public ConsoleAppender build() {
         if (!this.isValid()) {
            return null;
         } else if (this.direct && this.follow) {
            ConsoleAppender.LOGGER.error("Cannot use both `direct` and `follow` on ConsoleAppender.");
            return null;
         } else {
            Layout<? extends Serializable> layout = this.getOrCreateLayout(this.target.getDefaultCharset());
            OutputStream stream = this.direct
               ? ConsoleAppender.getDirectOutputStream(this.target)
               : (this.follow ? ConsoleAppender.getFollowOutputStream(this.target) : ConsoleAppender.getDefaultOutputStream(this.target));
            String managerName = this.target.name() + '.' + this.follow + '.' + this.direct;
            OutputStreamManager manager = OutputStreamManager.getManager(
               managerName, new ConsoleAppender.FactoryData(stream, managerName, layout), ConsoleAppender.factory
            );
            return new ConsoleAppender(this.getName(), layout, this.getFilter(), manager, this.isIgnoreExceptions(), this.target, this.getPropertyArray());
         }
      }
   }

   private static class ConsoleManagerFactory implements ManagerFactory<OutputStreamManager, ConsoleAppender.FactoryData> {
      private ConsoleManagerFactory() {
      }

      public OutputStreamManager createManager(final String name, final ConsoleAppender.FactoryData data) {
         return new OutputStreamManager(data.os, data.name, data.layout, true);
      }
   }

   private static class FactoryData {
      private final OutputStream os;
      private final String name;
      private final Layout<? extends Serializable> layout;

      public FactoryData(final OutputStream os, final String type, final Layout<? extends Serializable> layout) {
         this.os = os;
         this.name = type;
         this.layout = layout;
      }
   }

   private static class SystemErrStream extends OutputStream {
      public SystemErrStream() {
      }

      @Override
      public void close() {
      }

      @Override
      public void flush() {
         System.err.flush();
      }

      @Override
      public void write(final byte[] b) throws IOException {
         System.err.write(b);
      }

      @Override
      public void write(final byte[] b, final int off, final int len) throws IOException {
         System.err.write(b, off, len);
      }

      @Override
      public void write(final int b) {
         System.err.write(b);
      }
   }

   private static class SystemOutStream extends OutputStream {
      public SystemOutStream() {
      }

      @Override
      public void close() {
      }

      @Override
      public void flush() {
         System.out.flush();
      }

      @Override
      public void write(final byte[] b) throws IOException {
         System.out.write(b);
      }

      @Override
      public void write(final byte[] b, final int off, final int len) throws IOException {
         System.out.write(b, off, len);
      }

      @Override
      public void write(final int b) throws IOException {
         System.out.write(b);
      }
   }

   public static enum Target {
      SYSTEM_OUT {
         @Override
         public Charset getDefaultCharset() {
            return this.getCharset("sun.stdout.encoding", Charset.defaultCharset());
         }
      },
      SYSTEM_ERR {
         @Override
         public Charset getDefaultCharset() {
            return this.getCharset("sun.stderr.encoding", Charset.defaultCharset());
         }
      };

      private Target() {
      }

      public abstract Charset getDefaultCharset();

      protected Charset getCharset(final String property, final Charset defaultCharset) {
         return new PropertiesUtil(PropertiesUtil.getSystemProperties()).getCharsetProperty(property, defaultCharset);
      }
   }
}
