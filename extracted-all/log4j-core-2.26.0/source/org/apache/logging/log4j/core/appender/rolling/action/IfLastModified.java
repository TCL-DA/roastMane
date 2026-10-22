package org.apache.logging.log4j.core.appender.rolling.action;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.util.Clock;
import org.apache.logging.log4j.core.util.ClockFactory;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "IfLastModified",
   category = "Core",
   printObject = true
)
public final class IfLastModified implements PathCondition {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final Clock CLOCK = ClockFactory.getClock();
   private final java.time.Duration age;
   private final PathCondition[] nestedConditions;

   private IfLastModified(final java.time.Duration age, final PathCondition[] nestedConditions) {
      this.age = Objects.requireNonNull(age, "age");
      this.nestedConditions = PathCondition.copy(nestedConditions);
   }

   @Deprecated
   public Duration getAge() {
      return Duration.ofMillis(this.age.toMillis());
   }

   public List<PathCondition> getNestedConditions() {
      return Collections.unmodifiableList(Arrays.asList(this.nestedConditions));
   }

   @Override
   public boolean accept(final Path basePath, final Path relativePath, final BasicFileAttributes attrs) {
      FileTime fileTime = attrs.lastModifiedTime();
      long millis = fileTime.toMillis();
      long ageMillis = CLOCK.currentTimeMillis() - millis;
      boolean result = ageMillis >= this.age.toMillis();
      String match = result ? ">=" : "<";
      String accept = result ? "ACCEPTED" : "REJECTED";
      LOGGER.trace("IfLastModified {}: {} ageMillis '{}' {} '{}'", accept, relativePath, ageMillis, match, this.age);
      return result ? IfAll.accept(this.nestedConditions, basePath, relativePath, attrs) : result;
   }

   @Override
   public void beforeFileTreeWalk() {
      IfAll.beforeFileTreeWalk(this.nestedConditions);
   }

   @Deprecated
   public static IfLastModified createAgeCondition(final Duration age, final PathCondition... pathConditions) {
      return newBuilder().setAge(java.time.Duration.ofMillis(age.toMillis())).setNestedConditions(pathConditions).build();
   }

   @Override
   public String toString() {
      String nested = this.nestedConditions.length == 0 ? "" : " AND " + Arrays.toString((Object[])this.nestedConditions);
      return "IfLastModified(age=" + this.age + nested + ")";
   }

   @PluginBuilderFactory
   public static IfLastModified.Builder newBuilder() {
      return new IfLastModified.Builder();
   }

   public static final class Builder implements org.apache.logging.log4j.core.util.Builder<IfLastModified> {
      @PluginBuilderAttribute
      @Required(
         message = "No age provided for IfLastModified"
      )
      private Duration age;
      @PluginElement("nestedConditions")
      private PathCondition[] nestedConditions;

      public IfLastModified.Builder setAge(final java.time.Duration age) {
         this.age = Duration.ofMillis(age.toMillis());
         return this;
      }

      public IfLastModified.Builder setNestedConditions(final PathCondition... nestedConditions) {
         this.nestedConditions = nestedConditions;
         return this;
      }

      public IfLastModified build() {
         return this.isValid() ? new IfLastModified(java.time.Duration.ofMillis(this.age.toMillis()), this.nestedConditions) : null;
      }
   }
}
