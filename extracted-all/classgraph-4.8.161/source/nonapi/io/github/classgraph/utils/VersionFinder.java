package nonapi.io.github.classgraph.utils;

import io.github.classgraph.ClassGraph;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;
import org.w3c.dom.Document;

public final class VersionFinder {
   private static final String MAVEN_PACKAGE = "io.github.classgraph";
   private static final String MAVEN_ARTIFACT = "classgraph";
   public static final VersionFinder.OperatingSystem OS;
   public static final String JAVA_VERSION = getProperty("java.version");
   public static final int JAVA_MAJOR_VERSION;
   public static final int JAVA_MINOR_VERSION;
   public static final int JAVA_SUB_VERSION;
   public static final boolean JAVA_IS_EA_VERSION;

   private VersionFinder() {
   }

   public static String getProperty(String propName) {
      try {
         return System.getProperty(propName);
      } catch (SecurityException var2) {
         return null;
      }
   }

   public static String getProperty(String propName, String defaultVal) {
      try {
         return System.getProperty(propName, defaultVal);
      } catch (SecurityException var3) {
         return null;
      }
   }

   public static synchronized String getVersion() {
      Class<?> cls = ClassGraph.class;

      try {
         String className = cls.getName();
         URL classpathResource = cls.getResource("/" + JarUtils.classNameToClassfilePath(className));
         if (classpathResource != null) {
            Path absolutePackagePath = Paths.get(classpathResource.toURI()).getParent();
            int packagePathSegments = className.length() - className.replace(".", "").length();
            Path path = absolutePackagePath;

            for (int i = 0; i < packagePathSegments && path != null; i++) {
               path = path.getParent();
            }

            for (int i = 0; i < 3 && path != null; path = path.getParent()) {
               Path pom = path.resolve("pom.xml");

               try (InputStream is = Files.newInputStream(pom)) {
                  Document doc = getSecureDocumentBuilderFactory().newDocumentBuilder().parse(is);
                  doc.getDocumentElement().normalize();
                  String version = (String)getSecureXPathFactory().newXPath().compile("/project/version").evaluate(doc, XPathConstants.STRING);
                  if (version != null) {
                     version = version.trim();
                     if (!version.isEmpty()) {
                        return version;
                     }
                  }
               } catch (IOException var49) {
               }

               i++;
            }
         }
      } catch (Exception var50) {
      }

      try (InputStream isx = cls.getResourceAsStream("/META-INF/maven/io.github.classgraph/classgraph/pom.properties")) {
         if (isx != null) {
            Properties p = new Properties();
            p.load(isx);
            String version = p.getProperty("version", "").trim();
            if (!version.isEmpty()) {
               return version;
            }
         }
      } catch (IOException var46) {
      }

      Package pkg = cls.getPackage();
      if (pkg != null) {
         String version = pkg.getImplementationVersion();
         if (version == null) {
            version = "";
         }

         version = version.trim();
         if (version.isEmpty()) {
            version = pkg.getSpecificationVersion();
            if (version == null) {
               version = "";
            }

            version = version.trim();
         }

         if (!version.isEmpty()) {
            return version;
         }
      }

      return "unknown";
   }

   private static DocumentBuilderFactory getSecureDocumentBuilderFactory() throws ParserConfigurationException {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setXIncludeAware(false);
      dbf.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
      dbf.setAttribute("http://javax.xml.XMLConstants/property/accessExternalDTD", "");
      dbf.setAttribute("http://javax.xml.XMLConstants/property/accessExternalSchema", "");
      dbf.setExpandEntityReferences(false);
      dbf.setNamespaceAware(true);
      dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
      dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
      dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
      return dbf;
   }

   private static XPathFactory getSecureXPathFactory() throws XPathFactoryConfigurationException {
      XPathFactory xPathFactory = XPathFactory.newInstance();
      xPathFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
      return xPathFactory;
   }

   static {
      int javaMajorVersion = 0;
      int javaMinorVersion = 0;
      int javaSubVersion = 0;
      List<Integer> versionParts = new ArrayList<>();
      if (JAVA_VERSION != null) {
         for (String versionPart : JAVA_VERSION.split("[^0-9]+")) {
            try {
               versionParts.add(Integer.parseInt(versionPart));
            } catch (NumberFormatException var9) {
            }
         }

         if (!versionParts.isEmpty() && versionParts.get(0) == 1) {
            versionParts.remove(0);
         }

         if (versionParts.isEmpty()) {
            throw new RuntimeException("Could not determine Java version: " + JAVA_VERSION);
         }

         javaMajorVersion = versionParts.get(0);
         if (versionParts.size() > 1) {
            javaMinorVersion = versionParts.get(1);
         }

         if (versionParts.size() > 2) {
            javaSubVersion = versionParts.get(2);
         }
      }

      JAVA_MAJOR_VERSION = javaMajorVersion;
      JAVA_MINOR_VERSION = javaMinorVersion;
      JAVA_SUB_VERSION = javaSubVersion;
      JAVA_IS_EA_VERSION = JAVA_VERSION != null && JAVA_VERSION.endsWith("-ea");
      String osName = getProperty("os.name", "unknown").toLowerCase(Locale.ENGLISH);
      if (File.separatorChar == '\\') {
         OS = VersionFinder.OperatingSystem.Windows;
      } else if (osName == null) {
         OS = VersionFinder.OperatingSystem.Unknown;
      } else if (osName.contains("win")) {
         OS = VersionFinder.OperatingSystem.Windows;
      } else if (osName.contains("mac") || osName.contains("darwin")) {
         OS = VersionFinder.OperatingSystem.MacOSX;
      } else if (osName.contains("nux")) {
         OS = VersionFinder.OperatingSystem.Linux;
      } else if (osName.contains("sunos") || osName.contains("solaris")) {
         OS = VersionFinder.OperatingSystem.Solaris;
      } else if (osName.contains("bsd")) {
         OS = VersionFinder.OperatingSystem.Unix;
      } else if (!osName.contains("nix") && !osName.contains("aix")) {
         OS = VersionFinder.OperatingSystem.Unknown;
      } else {
         OS = VersionFinder.OperatingSystem.Unix;
      }
   }

   public static enum OperatingSystem {
      Windows,
      MacOSX,
      Linux,
      Solaris,
      BSD,
      Unix,
      Unknown;
   }
}
