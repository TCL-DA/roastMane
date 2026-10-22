module com.google.gson {
   requires static java.sql;
   requires static jdk.unsupported;

   exports com.google.gson;
   exports com.google.gson.annotations;
   exports com.google.gson.reflect;
   exports com.google.gson.stream;
}
