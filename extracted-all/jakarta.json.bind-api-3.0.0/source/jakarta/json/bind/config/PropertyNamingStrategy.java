package jakarta.json.bind.config;

public interface PropertyNamingStrategy {
   String IDENTITY = "IDENTITY";
   String LOWER_CASE_WITH_DASHES = "LOWER_CASE_WITH_DASHES";
   String LOWER_CASE_WITH_UNDERSCORES = "LOWER_CASE_WITH_UNDERSCORES";
   String UPPER_CAMEL_CASE = "UPPER_CAMEL_CASE";
   String UPPER_CAMEL_CASE_WITH_SPACES = "UPPER_CAMEL_CASE_WITH_SPACES";
   String CASE_INSENSITIVE = "CASE_INSENSITIVE";

   String translateName(String var1);
}
