package com.fasterxml.jackson.dataformat.yaml;

import com.fasterxml.jackson.core.TSFBuilder;
import com.fasterxml.jackson.dataformat.yaml.util.StringQuotingChecker;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.DumperOptions.Version;

public class YAMLFactoryBuilder extends TSFBuilder<YAMLFactory, YAMLFactoryBuilder> {
   protected int _formatGeneratorFeatures;
   protected StringQuotingChecker _quotingChecker;
   protected Version _version;
   protected LoaderOptions _loaderOptions;
   protected DumperOptions _dumperOptions;

   protected YAMLFactoryBuilder() {
      this._formatGeneratorFeatures = YAMLFactory.DEFAULT_YAML_GENERATOR_FEATURE_FLAGS;
   }

   public YAMLFactoryBuilder(YAMLFactory base) {
      super(base);
      this._formatGeneratorFeatures = base._yamlGeneratorFeatures;
      this._version = base._version;
      this._quotingChecker = base._quotingChecker;
   }

   public YAMLFactoryBuilder enable(YAMLGenerator.Feature f) {
      this._formatGeneratorFeatures = this._formatGeneratorFeatures | f.getMask();
      return this;
   }

   public YAMLFactoryBuilder enable(YAMLGenerator.Feature first, YAMLGenerator.Feature... other) {
      this._formatGeneratorFeatures = this._formatGeneratorFeatures | first.getMask();

      for (YAMLGenerator.Feature f : other) {
         this._formatGeneratorFeatures = this._formatGeneratorFeatures | f.getMask();
      }

      return this;
   }

   public YAMLFactoryBuilder disable(YAMLGenerator.Feature f) {
      this._formatGeneratorFeatures = this._formatGeneratorFeatures & ~f.getMask();
      return this;
   }

   public YAMLFactoryBuilder disable(YAMLGenerator.Feature first, YAMLGenerator.Feature... other) {
      this._formatGeneratorFeatures = this._formatGeneratorFeatures & ~first.getMask();

      for (YAMLGenerator.Feature f : other) {
         this._formatGeneratorFeatures = this._formatGeneratorFeatures & ~f.getMask();
      }

      return this;
   }

   public YAMLFactoryBuilder configure(YAMLGenerator.Feature f, boolean state) {
      return state ? this.enable(f) : this.disable(f);
   }

   public YAMLFactoryBuilder stringQuotingChecker(StringQuotingChecker sqc) {
      this._quotingChecker = sqc;
      return this;
   }

   public YAMLFactoryBuilder yamlVersionToWrite(Version v) {
      this._version = v;
      return this;
   }

   public YAMLFactoryBuilder loaderOptions(LoaderOptions loaderOptions) {
      this._loaderOptions = loaderOptions;
      return this;
   }

   public YAMLFactoryBuilder dumperOptions(DumperOptions dumperOptions) {
      this._dumperOptions = dumperOptions;
      return this;
   }

   public int formatGeneratorFeaturesMask() {
      return this._formatGeneratorFeatures;
   }

   public Version yamlVersionToWrite() {
      return this._version;
   }

   public StringQuotingChecker stringQuotingChecker() {
      return (StringQuotingChecker)(this._quotingChecker != null ? this._quotingChecker : StringQuotingChecker.Default.instance());
   }

   public LoaderOptions loaderOptions() {
      return this._loaderOptions;
   }

   public DumperOptions dumperOptions() {
      return this._dumperOptions;
   }

   public YAMLFactory build() {
      return new YAMLFactory(this);
   }
}
