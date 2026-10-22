package com.cropster.roastingintelligence.ui.whatsnew;

import java.net.URL;
import org.apache.commons.lang3.SystemUtils;

public abstract class AbstractPage implements WhatsNewPage {
   private final String[] content;
   private final String title;

   protected AbstractPage(String title, String... content) {
      this.content = content;
      this.title = title;
   }

   @Override
   public String[] content() {
      return this.content;
   }

   public AbstractPage.PlatformPostfix fallbackPlatformPostfix() {
      return AbstractPage.PlatformPostfix.WIN;
   }

   @Override
   public URL image() {
      URL imageUrl = null;

      for (AbstractPage.ImageFormat format : AbstractPage.ImageFormat.values()) {
         for (String extension : format.getExtensions()) {
            imageUrl = this.getClass().getResource(this.getClass().getSimpleName() + extension);
            if (imageUrl == null) {
               if (SystemUtils.IS_OS_MAC_OSX) {
                  imageUrl = this.getClass().getResource(this.getClass().getSimpleName() + AbstractPage.PlatformPostfix.MAC.getPostfix() + extension);
               } else if (SystemUtils.IS_OS_WINDOWS) {
                  imageUrl = this.getClass().getResource(this.getClass().getSimpleName() + AbstractPage.PlatformPostfix.WIN.getPostfix() + extension);
               } else if (SystemUtils.IS_OS_LINUX) {
                  imageUrl = this.getClass().getResource(this.getClass().getSimpleName() + AbstractPage.PlatformPostfix.LINUX.getPostfix() + extension);
               }

               if (imageUrl == null && this.fallbackPlatformPostfix() != null) {
                  imageUrl = this.getClass().getResource(this.getClass().getSimpleName() + this.fallbackPlatformPostfix().getPostfix() + extension);
               }
            }

            if (imageUrl != null) {
               return imageUrl;
            }
         }
      }

      return imageUrl;
   }

   @Override
   public String title() {
      return this.title;
   }

   private static enum ImageFormat {
      PNG(".png"),
      GIF(".gif"),
      JPEG(".jpeg", ".jpg");

      private final String[] extensions;

      private ImageFormat(String... extensions) {
         this.extensions = extensions;
      }

      public String[] getExtensions() {
         return this.extensions;
      }
   }

   public static enum PlatformPostfix {
      MAC("_mac"),
      WIN("_win"),
      LINUX("_linux");

      private final String postfix;

      private PlatformPostfix(String postfix) {
         this.postfix = postfix;
      }

      public String getPostfix() {
         return this.postfix;
      }
   }
}
