package jakarta.activation;

public interface MimeTypeRegistry {
   MimeTypeEntry getMimeTypeEntry(String var1);

   default String getMIMETypeString(String file_ext) {
      MimeTypeEntry entry = this.getMimeTypeEntry(file_ext);
      return entry != null ? entry.getMIMEType() : null;
   }

   void appendToRegistry(String var1);
}
