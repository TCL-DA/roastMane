package javafx.scene.input;

import com.sun.javafx.scene.input.ClipboardHelper;
import com.sun.javafx.tk.PermissionHelper;
import com.sun.javafx.tk.TKClipboard;
import com.sun.javafx.tk.Toolkit;
import java.io.File;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javafx.scene.image.Image;
import javafx.util.Pair;

public class Clipboard {
   private boolean contentPut = false;
   private final AccessControlContext acc = AccessController.getContext();
   TKClipboard peer;
   private static Clipboard systemClipboard;
   private static Clipboard localClipboard;

   public static Clipboard getSystemClipboard() {
      try {
         PermissionHelper.checkClipboardPermission();
         return getSystemClipboardImpl();
      } catch (SecurityException var1) {
         return getLocalClipboardImpl();
      }
   }

   Clipboard(TKClipboard var1) {
      Toolkit.getToolkit().checkFxUserThread();
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         var1.setSecurityContext(this.acc);
         this.peer = var1;
      }
   }

   public final void clear() {
      this.setContent(null);
   }

   public final Set<DataFormat> getContentTypes() {
      return this.peer.getContentTypes();
   }

   public final boolean setContent(Map<DataFormat, Object> var1) {
      Toolkit.getToolkit().checkFxUserThread();
      if (var1 == null) {
         this.contentPut = false;
         this.peer.putContent();
         return true;
      } else {
         Pair[] var2 = new Pair[var1.size()];
         int var3 = 0;

         for (Entry var5 : var1.entrySet()) {
            var2[var3++] = new Pair((DataFormat)var5.getKey(), var5.getValue());
         }

         this.contentPut = this.peer.putContent(var2);
         return this.contentPut;
      }
   }

   public final Object getContent(DataFormat var1) {
      Toolkit.getToolkit().checkFxUserThread();
      return this.getContentImpl(var1);
   }

   Object getContentImpl(DataFormat var1) {
      return this.peer.getContent(var1);
   }

   public final boolean hasContent(DataFormat var1) {
      Toolkit.getToolkit().checkFxUserThread();
      return this.peer.hasContent(var1);
   }

   public final boolean hasString() {
      return this.hasContent(DataFormat.PLAIN_TEXT);
   }

   public final String getString() {
      return (String)this.getContent(DataFormat.PLAIN_TEXT);
   }

   public final boolean hasUrl() {
      return this.hasContent(DataFormat.URL);
   }

   public final String getUrl() {
      return (String)this.getContent(DataFormat.URL);
   }

   public final boolean hasHtml() {
      return this.hasContent(DataFormat.HTML);
   }

   public final String getHtml() {
      return (String)this.getContent(DataFormat.HTML);
   }

   public final boolean hasRtf() {
      return this.hasContent(DataFormat.RTF);
   }

   public final String getRtf() {
      return (String)this.getContent(DataFormat.RTF);
   }

   public final boolean hasImage() {
      return this.hasContent(DataFormat.IMAGE);
   }

   public final Image getImage() {
      return (Image)this.getContent(DataFormat.IMAGE);
   }

   public final boolean hasFiles() {
      return this.hasContent(DataFormat.FILES);
   }

   public final List<File> getFiles() {
      return (List<File>)this.getContent(DataFormat.FILES);
   }

   boolean contentPut() {
      return this.contentPut;
   }

   private static synchronized Clipboard getSystemClipboardImpl() {
      if (systemClipboard == null) {
         systemClipboard = new Clipboard(Toolkit.getToolkit().getSystemClipboard());
      }

      return systemClipboard;
   }

   private static synchronized Clipboard getLocalClipboardImpl() {
      if (localClipboard == null) {
         localClipboard = new Clipboard(Toolkit.getToolkit().createLocalClipboard());
      }

      return localClipboard;
   }

   static {
      ClipboardHelper.setClipboardAccessor(new ClipboardHelper.ClipboardAccessor() {
         @Override
         public boolean contentPut(Clipboard var1) {
            return var1.contentPut();
         }
      });
   }
}
