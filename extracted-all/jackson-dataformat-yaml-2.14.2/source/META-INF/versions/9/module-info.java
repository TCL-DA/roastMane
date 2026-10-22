module com.fasterxml.jackson.dataformat.yaml {
   requires com.fasterxml.jackson.core;
   requires com.fasterxml.jackson.databind;
   requires org.yaml.snakeyaml;

   exports com.fasterxml.jackson.dataformat.yaml;
   exports com.fasterxml.jackson.dataformat.yaml.snakeyaml.error;
   exports com.fasterxml.jackson.dataformat.yaml.util;

   provides com.fasterxml.jackson.core.JsonFactory with
      com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
   provides com.fasterxml.jackson.core.ObjectCodec with
      com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
}
