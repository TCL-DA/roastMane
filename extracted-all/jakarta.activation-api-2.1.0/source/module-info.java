module jakarta.activation {
   requires java.logging;
   requires static java.desktop;

   exports jakarta.activation;
   exports jakarta.activation.spi;

   uses jakarta.activation.spi.MailcapRegistryProvider;
   uses jakarta.activation.spi.MimeTypeRegistryProvider;
}
