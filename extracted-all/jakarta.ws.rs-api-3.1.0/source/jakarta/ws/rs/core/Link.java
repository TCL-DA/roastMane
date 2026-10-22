package jakarta.ws.rs.core;

import jakarta.ws.rs.ext.RuntimeDelegate;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import javax.xml.namespace.QName;

public abstract class Link {
   public static final String TITLE = "title";
   public static final String REL = "rel";
   public static final String TYPE = "type";

   public abstract URI getUri();

   public abstract UriBuilder getUriBuilder();

   public abstract String getRel();

   public abstract List<String> getRels();

   public abstract String getTitle();

   public abstract String getType();

   public abstract Map<String, String> getParams();

   @Override
   public abstract String toString();

   public static Link valueOf(String value) {
      Link.Builder b = RuntimeDelegate.getInstance().createLinkBuilder();
      b.link(value);
      return b.build();
   }

   public static Link.Builder fromUri(URI uri) {
      Link.Builder b = RuntimeDelegate.getInstance().createLinkBuilder();
      b.uri(uri);
      return b;
   }

   public static Link.Builder fromUri(String uri) {
      Link.Builder b = RuntimeDelegate.getInstance().createLinkBuilder();
      b.uri(uri);
      return b;
   }

   public static Link.Builder fromUriBuilder(UriBuilder uriBuilder) {
      Link.Builder b = RuntimeDelegate.getInstance().createLinkBuilder();
      b.uriBuilder(uriBuilder);
      return b;
   }

   public static Link.Builder fromLink(Link link) {
      Link.Builder b = RuntimeDelegate.getInstance().createLinkBuilder();
      b.link(link);
      return b;
   }

   public static Link.Builder fromPath(String path) {
      return fromUriBuilder(UriBuilder.fromPath(path));
   }

   public static Link.Builder fromResource(Class<?> resource) {
      return fromUriBuilder(UriBuilder.fromResource(resource));
   }

   public static Link.Builder fromMethod(Class<?> resource, String method) {
      return fromUriBuilder(UriBuilder.fromMethod(resource, method));
   }

   public interface Builder {
      Link.Builder link(Link var1);

      Link.Builder link(String var1);

      Link.Builder uri(URI var1);

      Link.Builder uri(String var1);

      Link.Builder baseUri(URI var1);

      Link.Builder baseUri(String var1);

      Link.Builder uriBuilder(UriBuilder var1);

      Link.Builder rel(String var1);

      Link.Builder title(String var1);

      Link.Builder type(String var1);

      Link.Builder param(String var1, String var2);

      Link build(Object... var1);

      Link buildRelativized(URI var1, Object... var2);
   }

   @Deprecated
   public static class JaxbAdapter extends XmlAdapter<Link.JaxbLink, Link> {
      public Link unmarshal(Link.JaxbLink v) {
         Link.Builder lb = Link.fromUri(v.getUri());

         for (Entry<QName, Object> e : v.getParams().entrySet()) {
            lb.param(e.getKey().getLocalPart(), e.getValue().toString());
         }

         return lb.build();
      }

      public Link.JaxbLink marshal(Link v) {
         Link.JaxbLink jl = new Link.JaxbLink(v.getUri());

         for (Entry<String, String> e : v.getParams().entrySet()) {
            String name = e.getKey();
            jl.getParams().put(new QName("", name), e.getValue());
         }

         return jl;
      }
   }

   @Deprecated
   public static class JaxbLink {
      private URI uri;
      private Map<QName, Object> params;

      public JaxbLink() {
      }

      public JaxbLink(URI uri) {
         this.uri = uri;
      }

      public JaxbLink(URI uri, Map<QName, Object> params) {
         this.uri = uri;
         this.params = params;
      }

      @XmlAttribute(
         name = "href"
      )
      public URI getUri() {
         return this.uri;
      }

      @XmlAnyAttribute
      public Map<QName, Object> getParams() {
         if (this.params == null) {
            this.params = new HashMap<>();
         }

         return this.params;
      }

      void setUri(URI uri) {
         this.uri = uri;
      }

      void setParams(Map<QName, Object> params) {
         this.params = params;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (!(o instanceof Link.JaxbLink)) {
            return false;
         } else {
            Link.JaxbLink jaxbLink = (Link.JaxbLink)o;
            if (this.uri != null ? this.uri.equals(jaxbLink.uri) : jaxbLink.uri == null) {
               if (this.params == jaxbLink.params) {
                  return true;
               } else if (this.params == null) {
                  return jaxbLink.params.isEmpty();
               } else {
                  return jaxbLink.params == null ? this.params.isEmpty() : this.params.equals(jaxbLink.params);
               }
            } else {
               return false;
            }
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.uri, this.params);
      }
   }
}
