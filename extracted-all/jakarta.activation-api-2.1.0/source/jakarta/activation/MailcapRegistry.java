package jakarta.activation;

import java.util.List;
import java.util.Map;

public interface MailcapRegistry {
   Map<String, List<String>> getMailcapList(String var1);

   Map<String, List<String>> getMailcapFallbackList(String var1);

   String[] getMimeTypes();

   String[] getNativeCommands(String var1);

   void appendToMailcap(String var1);
}
