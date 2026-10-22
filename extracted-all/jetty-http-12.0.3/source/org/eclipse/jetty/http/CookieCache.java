package org.eclipse.jetty.http;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import org.eclipse.jetty.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CookieCache implements CookieParser.Handler {
   protected static final Logger LOG = LoggerFactory.getLogger(CookieCache.class);
   protected final List<String> _rawFields = new ArrayList<>();
   protected List<HttpCookie> _cookieList;
   private final CookieParser _parser;

   public CookieCache() {
      this(CookieCompliance.RFC6265, null);
   }

   public CookieCache(CookieCompliance compliance, ComplianceViolation.Listener complianceListener) {
      this._parser = CookieParser.newParser(this, compliance, complianceListener);
   }

   @Override
   public void addCookie(String cookieName, String cookieValue, int cookieVersion, String cookieDomain, String cookiePath, String cookieComment) {
      if (StringUtil.isEmpty(cookieDomain) && StringUtil.isEmpty(cookiePath) && cookieVersion <= 0 && StringUtil.isEmpty(cookieComment)) {
         this._cookieList.add(HttpCookie.from(cookieName, cookieValue));
      } else {
         Map<String, String> attributes = new HashMap<>();
         if (!StringUtil.isEmpty(cookieDomain)) {
            attributes.put("Domain", cookieDomain);
         }

         if (!StringUtil.isEmpty(cookiePath)) {
            attributes.put("Path", cookiePath);
         }

         if (!StringUtil.isEmpty(cookieComment)) {
            attributes.put("Comment", cookieComment);
         }

         this._cookieList.add(HttpCookie.from(cookieName, cookieValue, cookieVersion, attributes));
      }
   }

   public List<HttpCookie> getCookies(HttpFields headers) {
      boolean building = false;
      ListIterator<String> raw = this._rawFields.listIterator();

      for (HttpField field : headers) {
         if (HttpHeader.COOKIE.equals(field.getHeader())) {
            String value = field.getValue();
            if (!StringUtil.isBlank(value)) {
               if (building) {
                  this._rawFields.add(value);
               } else if (!raw.hasNext()) {
                  building = true;
                  this._rawFields.add(value);
               } else if (!value.equals(raw.next())) {
                  building = true;
                  raw.remove();

                  while (raw.hasNext()) {
                     raw.next();
                     raw.remove();
                  }

                  this._rawFields.add(value);
               }
            }
         }
      }

      if (!building && raw.hasNext()) {
         building = true;

         while (raw.hasNext()) {
            raw.next();
            raw.remove();
         }
      }

      if (building) {
         this._cookieList = new ArrayList<>();

         try {
            this._parser.parseFields(this._rawFields);
         } catch (CookieParser.InvalidCookieException var7) {
            throw new BadMessageException(var7.getMessage(), var7);
         }
      }

      return this._cookieList == null ? Collections.emptyList() : this._cookieList;
   }

   public void replaceCookieList(List<HttpCookie> cookies) {
      assert this._cookieList.equals(cookies);

      this._cookieList = cookies;
   }
}
