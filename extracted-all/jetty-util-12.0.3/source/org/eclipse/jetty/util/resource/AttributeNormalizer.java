package org.eclipse.jetty.util.resource;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.eclipse.jetty.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttributeNormalizer {
   private static final Logger LOG = LoggerFactory.getLogger(AttributeNormalizer.class);
   private static final Pattern __propertyPattern = Pattern.compile("(?<=[^$]|^)\\$\\{([^}]*)\\}");
   private static final Comparator<AttributeNormalizer.Attribute> attrComparator = (o1, o2) -> {
      if (o1.value == null && o2.value != null) {
         return -1;
      } else if (o1.value != null && o2.value == null) {
         return 1;
      } else if (o1.value == null) {
         return 0;
      } else {
         int diff = o2.value.length() - o1.value.length();
         if (diff != 0) {
            return diff;
         } else {
            diff = o2.value.compareTo(o1.value);
            return diff != 0 ? diff : o2.weight - o1.weight;
         }
      }
   };
   private final List<AttributeNormalizer.PathAttribute> paths = new ArrayList<>();
   private final List<AttributeNormalizer.URIAttribute> uris = new ArrayList<>();

   public static URI toCanonicalURI(URI uri) {
      uri = uri.normalize();
      String path = uri.getPath();
      if (path != null && path.length() > 1 && path.endsWith("/")) {
         try {
            String ascii = uri.toASCIIString();
            uri = new URI(ascii.substring(0, ascii.length() - 1));
         } catch (URISyntaxException var3) {
            throw new IllegalArgumentException(var3);
         }
      }

      return uri;
   }

   public static String toCanonicalURI(String uri) {
      return uri != null && uri.length() > 1 && uri.endsWith("/") ? uri.substring(0, uri.length() - 1) : uri;
   }

   public static Path toCanonicalPath(String path) {
      if (path == null) {
         return null;
      } else {
         if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
         }

         return toCanonicalPath(FileSystems.getDefault().getPath(path));
      }
   }

   private static Path toCanonicalPath(Path path) {
      if (path == null) {
         return null;
      } else if (Files.exists(path)) {
         try {
            return path.toRealPath();
         } catch (IOException var2) {
            throw new IllegalArgumentException(var2);
         }
      } else {
         return path.toAbsolutePath();
      }
   }

   public AttributeNormalizer(Resource baseResource) {
      if (baseResource == null) {
         throw new IllegalArgumentException("No base resource!");
      } else if (Resources.isCombined(baseResource)) {
         throw new IllegalArgumentException("Base resource cannot be combined!");
      } else {
         this.addSystemProperty("jetty.base", 9);
         this.addSystemProperty("jetty.home", 8);
         this.addSystemProperty("user.home", 7);
         this.addSystemProperty("user.dir", 6);
         URI warURI = toCanonicalURI(baseResource.getURI());
         if (!warURI.isAbsolute()) {
            throw new IllegalArgumentException("WAR URI is not absolute: " + warURI);
         } else {
            Path path = baseResource.getPath();
            if (path != null) {
               this.paths.add(new AttributeNormalizer.PathAttribute("WAR.path", toCanonicalPath(path), 10));
            }

            this.uris.add(new AttributeNormalizer.URIAttribute("WAR.uri", warURI, 9));
            this.uris.add(new AttributeNormalizer.URIAttribute("WAR", warURI, 8));
            this.paths.sort(attrComparator);
            this.uris.sort(attrComparator);
            if (LOG.isDebugEnabled()) {
               Stream.concat(this.paths.stream(), this.uris.stream()).map(Object::toString).forEach(LOG::debug);
            }
         }
      }
   }

   private void addSystemProperty(String key, int weight) {
      String value = System.getProperty(key);
      if (value != null) {
         Path path = toCanonicalPath(value);
         this.paths.add(new AttributeNormalizer.PathAttribute(key, path, weight));
         this.uris.add(new AttributeNormalizer.URIAttribute(key + ".uri", path.toUri(), weight));
      }
   }

   public String normalize(Object o) {
      try {
         URI uri = null;
         Path path = null;
         if (o instanceof URI) {
            uri = toCanonicalURI((URI)o);
         } else if (o instanceof Resource) {
            uri = toCanonicalURI(((Resource)o).getURI());
         } else if (o instanceof URL) {
            uri = toCanonicalURI(((URL)o).toURI());
         } else if (o instanceof File) {
            path = ((File)o).getAbsoluteFile().getCanonicalFile().toPath();
         } else if (o instanceof Path) {
            path = (Path)o;
         } else {
            String s = o.toString();

            try {
               uri = new URI(s);
               if (uri.getScheme() == null) {
                  return s;
               }
            } catch (URISyntaxException var8) {
               return s;
            }
         }

         if (uri != null) {
            if ("jar".equalsIgnoreCase(uri.getScheme())) {
               String raw = uri.getRawSchemeSpecificPart();
               int bang = raw.indexOf("!/");
               String normal = this.normalize(raw.substring(0, bang));
               String suffix = raw.substring(bang);
               return "jar:" + normal + suffix;
            }

            if (uri.isAbsolute()) {
               return this.normalizeUri(uri);
            }
         } else if (path != null) {
            return this.normalizePath(path);
         }
      } catch (Exception var9) {
         LOG.warn("Failed to normalize {}", o, var9);
      }

      return String.valueOf(o);
   }

   protected String normalizeUri(URI uri) {
      for (AttributeNormalizer.URIAttribute a : this.uris) {
         if (uri.compareTo(a.uri) == 0) {
            return String.format("${%s}", a.key);
         }

         if (a.uri.getScheme().equalsIgnoreCase(uri.getScheme())
            && (a.uri.getHost() != null || uri.getHost() == null)
            && (a.uri.getHost() == null || a.uri.getHost().equals(uri.getHost()))) {
            String aPath = a.uri.getPath();
            String uPath = uri.getPath();
            if (aPath.equals(uPath)) {
               return a.value;
            }

            if (uPath.startsWith(aPath)) {
               if (uPath.length() == aPath.length()) {
                  return String.format("${%s}", a.key);
               }

               String s = uPath.substring(aPath.length());
               if (s.charAt(0) == '/') {
                  return String.format("${%s}%s", a.key, s);
               }
            }
         }
      }

      return uri.toASCIIString();
   }

   protected String normalizePath(Path path) {
      for (AttributeNormalizer.PathAttribute a : this.paths) {
         try {
            if (path.equals(a.path) || Files.isSameFile(path, a.path)) {
               return String.format("${%s}", a.key);
            }
         } catch (IOException var5) {
            LOG.trace("IGNORED", var5);
         }

         if (path.startsWith(a.path)) {
            return String.format("${%s}%c%s", a.key, File.separatorChar, a.path.relativize(path));
         }
      }

      return path.toString();
   }

   public String expand(String str) {
      if (str == null) {
         return str;
      } else if (!str.contains("${")) {
         return str;
      } else {
         Matcher mat = __propertyPattern.matcher(str);
         if (mat.find(0)) {
            String prefix = str.substring(0, mat.start());
            String property = mat.group(1);
            String suffix = str.substring(mat.end());
            str = this.expand(prefix, property, suffix);
         }

         return StringUtil.replace(str, "$$", "$");
      }
   }

   private String expand(String prefix, String property, String suffix) {
      if (property == null) {
         return null;
      } else {
         for (AttributeNormalizer.URIAttribute attr : this.uris) {
            if (property.equals(attr.key)) {
               return prefix + attr.value + suffix;
            }
         }

         for (AttributeNormalizer.PathAttribute attrx : this.paths) {
            if (property.equals(attrx.key)) {
               return prefix + attrx.value + suffix;
            }
         }

         String system = System.getProperty(property);
         if (system != null) {
            return prefix + system + suffix;
         } else {
            String unexpanded = prefix + "${" + property + "}" + suffix;
            LOG.warn("Cannot expand: {}", unexpanded);
            return unexpanded;
         }
      }
   }

   private static class Attribute {
      final String key;
      final String value;
      final int weight;

      public Attribute(String key, String value, int weight) {
         this.key = key;
         this.value = value;
         this.weight = weight;
      }
   }

   private static class PathAttribute extends AttributeNormalizer.Attribute {
      public final Path path;

      public PathAttribute(String key, Path path, int weight) {
         super(key, path.toString(), weight);
         this.path = path;
      }

      @Override
      public String toString() {
         return String.format("PathAttribute[%s=>%s]", this.key, this.path);
      }
   }

   private static class URIAttribute extends AttributeNormalizer.Attribute {
      public final URI uri;

      public URIAttribute(String key, URI uri, int weight) {
         super(key, AttributeNormalizer.toCanonicalURI(uri.toASCIIString()), weight);
         this.uri = AttributeNormalizer.toCanonicalURI(uri);
      }

      @Override
      public String toString() {
         return String.format("URIAttribute[%s=>%s]", this.key, this.uri);
      }
   }
}
