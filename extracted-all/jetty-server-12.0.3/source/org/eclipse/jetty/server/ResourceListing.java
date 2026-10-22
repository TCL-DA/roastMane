package org.eclipse.jetty.server;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.eclipse.jetty.util.Fields;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.util.UrlEncoded;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollators;
import org.eclipse.jetty.util.resource.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceListing {
   public static final Logger LOG = LoggerFactory.getLogger(ResourceListing.class);

   public static String getAsXHTML(Resource resource, String base, boolean parent, String query) {
      base = URIUtil.normalizePath(base);
      if (base == null) {
         return null;
      } else if (!Resources.isReadableDirectory(resource)) {
         return null;
      } else {
         List<Resource> listing = resource.list().stream().filter(distinctBy(Resource::getFileName)).collect(Collectors.toCollection(ArrayList::new));
         boolean sortOrderAscending = true;
         String sortColumn = "N";
         if (query != null) {
            Fields params = new Fields();
            UrlEncoded.decodeUtf8To(query, 0, query.length(), params);
            String paramO = params.getValue("O");
            String paramC = params.getValue("C");
            if (StringUtil.isNotBlank(paramO)) {
               switch (paramO) {
                  case "A":
                     sortOrderAscending = true;
                     break;
                  case "D":
                     sortOrderAscending = false;
               }
            }

            if (StringUtil.isNotBlank(paramC) && (paramC.equals("N") || paramC.equals("M") || paramC.equals("S"))) {
               sortColumn = paramC;
            }
         }
         Comparator<? super Resource> sort = switch (sortColumn) {
            case "M" -> ResourceCollators.byLastModified(sortOrderAscending);
            case "S" -> ResourceCollators.bySize(sortOrderAscending);
            default -> ResourceCollators.byFileName(sortOrderAscending);
         };
         listing.sort(sort);
         String decodedBase = URIUtil.decodePath(base);
         String title = "Directory: " + deTag(decodedBase);
         StringBuilder buf = new StringBuilder(4096);
         buf.append(
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">\n<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\" xml:lang=\"en\">\n"
         );
         buf.append("<head>\n");
         buf.append("<link href=\"jetty-dir.css\" rel=\"stylesheet\" />\n");
         buf.append("<title>");
         buf.append(title);
         buf.append("</title>\n");
         buf.append("</head>\n");
         buf.append("<body>\n");
         buf.append("<h1 class=\"title\">").append(title).append("</h1>\n");
         String ARROW_DOWN = "&nbsp; &#8681;";
         String ARROW_UP = "&nbsp; &#8679;";
         buf.append("<table class=\"listing\">\n");
         buf.append("<thead>\n");
         String arrow = "";
         String order = "A";
         if (sortColumn.equals("N")) {
            if (sortOrderAscending) {
               order = "D";
               arrow = "&nbsp; &#8679;";
            } else {
               order = "A";
               arrow = "&nbsp; &#8681;";
            }
         }

         buf.append("<tr><th class=\"name\"><a href=\"?C=N&amp;O=").append(order).append("\">");
         buf.append("Name").append(arrow);
         buf.append("</a></th>");
         arrow = "";
         order = "A";
         if (sortColumn.equals("M")) {
            if (sortOrderAscending) {
               order = "D";
               arrow = "&nbsp; &#8679;";
            } else {
               order = "A";
               arrow = "&nbsp; &#8681;";
            }
         }

         buf.append("<th class=\"lastmodified\"><a href=\"?C=M&amp;O=").append(order).append("\">");
         buf.append("Last Modified").append(arrow);
         buf.append("</a></th>");
         arrow = "";
         order = "A";
         if (sortColumn.equals("S")) {
            if (sortOrderAscending) {
               order = "D";
               arrow = "&nbsp; &#8679;";
            } else {
               order = "A";
               arrow = "&nbsp; &#8681;";
            }
         }

         buf.append("<th class=\"size\"><a href=\"?C=S&amp;O=").append(order).append("\">");
         buf.append("Size").append(arrow);
         buf.append("</a></th></tr>\n");
         buf.append("</thead>\n");
         buf.append("<tbody>\n");
         String encodedBase = hrefEncodeURI(base);
         if (parent) {
            buf.append("<tr><td class=\"name\"><a href=\"");
            buf.append(URIUtil.addPaths(encodedBase, "../"));
            buf.append("\">Parent Directory</a></td>");
            buf.append("<td class=\"lastmodified\">-</td>");
            buf.append("<td>-</td>");
            buf.append("</tr>\n");
         }

         DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.MEDIUM).withZone(ZoneId.systemDefault());

         for (Resource item : listing) {
            String name = item.getFileName();
            if (!StringUtil.isBlank(name)) {
               if (item.isDirectory() && !name.endsWith("/")) {
                  name = name + "/";
               }

               buf.append("<tr><td class=\"name\"><a href=\"");
               String path = URIUtil.addEncodedPaths(encodedBase, URIUtil.encodePath(name));
               buf.append(path);
               buf.append("\">");
               buf.append(deTag(name));
               buf.append("&nbsp;</a></td>");
               buf.append("<td class=\"lastmodified\">");
               Instant lastModified = item.lastModified();
               buf.append(formatter.format(lastModified));
               buf.append("&nbsp;</td>");
               buf.append("<td class=\"size\">");
               long length = item.length();
               if (length >= 0L) {
                  buf.append(String.format("%,d bytes", item.length()));
               }

               buf.append("&nbsp;</td></tr>\n");
            }
         }

         buf.append("</tbody>\n");
         buf.append("</table>\n");
         buf.append("</body></html>\n");
         return buf.toString();
      }
   }

   private static <T> Predicate<T> distinctBy(Function<? super T, Object> keyExtractor) {
      HashSet<Object> map = new HashSet<>();
      return t -> map.add(keyExtractor.apply(t));
   }

   private static String hrefEncodeURI(String raw) {
      StringBuffer buf = null;

      label33:
      for (int i = 0; i < raw.length(); i++) {
         char c = raw.charAt(i);
         switch (c) {
            case '"':
            case '\'':
            case '<':
            case '>':
               buf = new StringBuffer(raw.length() << 1);
               break label33;
         }
      }

      if (buf == null) {
         return raw;
      } else {
         for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            switch (c) {
               case '"':
                  buf.append("%22");
                  break;
               case '\'':
                  buf.append("%27");
                  break;
               case '<':
                  buf.append("%3C");
                  break;
               case '>':
                  buf.append("%3E");
                  break;
               default:
                  buf.append(c);
            }
         }

         return buf.toString();
      }
   }

   private static String deTag(String raw) {
      return StringUtil.sanitizeXmlString(raw);
   }
}
