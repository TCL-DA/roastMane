package org.glassfish.jersey.message.internal;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Variant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.glassfish.jersey.internal.util.collection.Ref;

public final class VariantSelector {
   private static final VariantSelector.DimensionChecker<AcceptableMediaType, MediaType> MEDIA_TYPE_DC = new VariantSelector.DimensionChecker<AcceptableMediaType, MediaType>() {
      public MediaType getDimension(VariantSelector.VariantHolder v) {
         return v.v.getMediaType();
      }

      public boolean isCompatible(AcceptableMediaType t, MediaType u) {
         return t.isCompatible(u);
      }

      public int getQualitySource(VariantSelector.VariantHolder v, MediaType u) {
         return v.mediaTypeQs;
      }

      @Override
      public String getVaryHeaderValue() {
         return "Accept";
      }
   };
   private static final VariantSelector.DimensionChecker<AcceptableLanguageTag, Locale> LANGUAGE_TAG_DC = new VariantSelector.DimensionChecker<AcceptableLanguageTag, Locale>() {
      public Locale getDimension(VariantSelector.VariantHolder v) {
         return v.v.getLanguage();
      }

      public boolean isCompatible(AcceptableLanguageTag t, Locale u) {
         return t.isCompatible(u);
      }

      public int getQualitySource(VariantSelector.VariantHolder qsv, Locale u) {
         return 0;
      }

      @Override
      public String getVaryHeaderValue() {
         return "Accept-Language";
      }
   };
   private static final VariantSelector.DimensionChecker<AcceptableToken, String> CHARSET_DC = new VariantSelector.DimensionChecker<AcceptableToken, String>() {
      public String getDimension(VariantSelector.VariantHolder v) {
         MediaType m = v.v.getMediaType();
         return m != null ? (String)m.getParameters().get("charset") : null;
      }

      public boolean isCompatible(AcceptableToken t, String u) {
         return t.isCompatible(u);
      }

      public int getQualitySource(VariantSelector.VariantHolder qsv, String u) {
         return 0;
      }

      @Override
      public String getVaryHeaderValue() {
         return "Accept-Charset";
      }
   };
   private static final VariantSelector.DimensionChecker<AcceptableToken, String> ENCODING_DC = new VariantSelector.DimensionChecker<AcceptableToken, String>() {
      public String getDimension(VariantSelector.VariantHolder v) {
         return v.v.getEncoding();
      }

      public boolean isCompatible(AcceptableToken t, String u) {
         return t.isCompatible(u);
      }

      public int getQualitySource(VariantSelector.VariantHolder qsv, String u) {
         return 0;
      }

      @Override
      public String getVaryHeaderValue() {
         return "Accept-Encoding";
      }
   };

   private VariantSelector() {
   }

   private static <T extends Qualified, U> LinkedList<VariantSelector.VariantHolder> selectVariants(
      List<VariantSelector.VariantHolder> variantHolders, List<T> acceptableValues, VariantSelector.DimensionChecker<T, U> dimensionChecker, Set<String> vary
   ) {
      int cq = 0;
      int cqs = 0;
      LinkedList<VariantSelector.VariantHolder> selected = new LinkedList<>();

      for (T a : acceptableValues) {
         int q = a.getQuality();
         Iterator<VariantSelector.VariantHolder> iv = variantHolders.iterator();

         while (iv.hasNext()) {
            VariantSelector.VariantHolder v = iv.next();
            U d = dimensionChecker.getDimension(v);
            if (d != null) {
               vary.add(dimensionChecker.getVaryHeaderValue());
               int qs = dimensionChecker.getQualitySource(v, d);
               if (qs >= cqs && dimensionChecker.isCompatible(a, d)) {
                  if (qs > cqs) {
                     cqs = qs;
                     cq = q;
                     selected.clear();
                     selected.add(v);
                  } else if (q > cq) {
                     cq = q;
                     selected.addFirst(v);
                  } else if (q == cq) {
                     selected.add(v);
                  }

                  iv.remove();
               }
            }
         }
      }

      for (VariantSelector.VariantHolder v : variantHolders) {
         if (dimensionChecker.getDimension(v) == null) {
            selected.add(v);
         }
      }

      return selected;
   }

   private static LinkedList<VariantSelector.VariantHolder> getVariantHolderList(List<Variant> variants) {
      LinkedList<VariantSelector.VariantHolder> l = new LinkedList<>();

      for (Variant v : variants) {
         MediaType mt = v.getMediaType();
         if (mt != null) {
            if (!(mt instanceof QualitySourceMediaType) && !mt.getParameters().containsKey("qs")) {
               l.add(new VariantSelector.VariantHolder(v));
            } else {
               int qs = QualitySourceMediaType.getQualitySource(mt);
               l.add(new VariantSelector.VariantHolder(v, qs));
            }
         } else {
            l.add(new VariantSelector.VariantHolder(v));
         }
      }

      return l;
   }

   public static Variant selectVariant(InboundMessageContext context, List<Variant> variants, Ref<String> varyHeaderValue) {
      List<Variant> selectedVariants = selectVariants(context, variants, varyHeaderValue);
      return selectedVariants.isEmpty() ? null : selectedVariants.get(0);
   }

   public static List<Variant> selectVariants(InboundMessageContext context, List<Variant> variants, Ref<String> varyHeaderValue) {
      LinkedList<VariantSelector.VariantHolder> vhs = getVariantHolderList(variants);
      Set<String> vary = new HashSet<>();
      vhs = selectVariants(vhs, context.getQualifiedAcceptableMediaTypes(), MEDIA_TYPE_DC, vary);
      vhs = selectVariants(vhs, context.getQualifiedAcceptableLanguages(), LANGUAGE_TAG_DC, vary);
      vhs = selectVariants(vhs, context.getQualifiedAcceptCharset(), CHARSET_DC, vary);
      vhs = selectVariants(vhs, context.getQualifiedAcceptEncoding(), ENCODING_DC, vary);
      if (vhs.isEmpty()) {
         return Collections.emptyList();
      } else {
         StringBuilder varyHeader = new StringBuilder();

         for (String v : vary) {
            if (varyHeader.length() > 0) {
               varyHeader.append(',');
            }

            varyHeader.append(v);
         }

         String varyValue = varyHeader.toString();
         if (!varyValue.isEmpty()) {
            varyHeaderValue.set(varyValue);
         }

         return vhs.stream().map(variantHolder -> variantHolder.v).collect(Collectors.toList());
      }
   }

   private interface DimensionChecker<T, U> {
      U getDimension(VariantSelector.VariantHolder var1);

      int getQualitySource(VariantSelector.VariantHolder var1, U var2);

      boolean isCompatible(T var1, U var2);

      String getVaryHeaderValue();
   }

   private static class VariantHolder {
      private final Variant v;
      private final int mediaTypeQs;

      VariantHolder(Variant v) {
         this(v, 1000);
      }

      VariantHolder(Variant v, int mediaTypeQs) {
         this.v = v;
         this.mediaTypeQs = mediaTypeQs;
      }
   }
}
