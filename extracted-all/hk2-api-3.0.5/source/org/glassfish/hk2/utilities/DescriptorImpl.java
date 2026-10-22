package org.glassfish.hk2.utilities;

import jakarta.inject.Singleton;
import java.io.BufferedReader;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.DescriptorType;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.utilities.general.GeneralUtilities;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;

public class DescriptorImpl implements Descriptor, Externalizable {
   private static final long serialVersionUID = 1558442492395467828L;
   private static final String CONTRACT_KEY = "contract=";
   private static final String NAME_KEY = "name=";
   private static final String SCOPE_KEY = "scope=";
   private static final String QUALIFIER_KEY = "qualifier=";
   private static final String TYPE_KEY = "type=";
   private static final String VISIBILITY_KEY = "visibility=";
   private static final String METADATA_KEY = "metadata=";
   private static final String RANKING_KEY = "rank=";
   private static final String PROXIABLE_KEY = "proxiable=";
   private static final String PROXY_FOR_SAME_SCOPE_KEY = "proxyForSameScope=";
   private static final String ANALYSIS_KEY = "analysis=";
   private static final String PROVIDE_METHOD_DT = "PROVIDE";
   private static final String LOCAL_DT = "LOCAL";
   private static final String START_START = "[";
   private static final String END_START = "]";
   private static final char END_START_CHAR = ']';
   private static final String SINGLETON_DIRECTIVE = "S";
   private static final String NOT_IN_CONTRACTS_DIRECTIVE = "-";
   private static final char SINGLETON_DIRECTIVE_CHAR = 'S';
   private static final char NOT_IN_CONTRACTS_DIRECTIVE_CHAR = '-';
   private static final Set<String> EMPTY_CONTRACTS_SET = Collections.emptySet();
   private static final Set<String> EMPTY_QUALIFIER_SET = Collections.emptySet();
   private static final Map<String, List<String>> EMPTY_METADATAS_MAP = Collections.emptyMap();
   private Set<String> contracts;
   private String implementation;
   private String name;
   private String scope = PerLookup.class.getName();
   private Map<String, List<String>> metadatas;
   private Set<String> qualifiers;
   private DescriptorType descriptorType = DescriptorType.CLASS;
   private DescriptorVisibility descriptorVisibility = DescriptorVisibility.NORMAL;
   private transient HK2Loader loader;
   private int rank;
   private Boolean proxiable;
   private Boolean proxyForSameScope;
   private String analysisName;
   private Long id;
   private Long locatorId;

   public DescriptorImpl() {
   }

   public DescriptorImpl(Descriptor copyMe) {
      this.name = copyMe.getName();
      this.scope = copyMe.getScope();
      this.implementation = copyMe.getImplementation();
      this.descriptorType = copyMe.getDescriptorType();
      this.descriptorVisibility = copyMe.getDescriptorVisibility();
      this.loader = copyMe.getLoader();
      this.rank = copyMe.getRanking();
      this.proxiable = copyMe.isProxiable();
      this.proxyForSameScope = copyMe.isProxyForSameScope();
      this.id = copyMe.getServiceId();
      this.locatorId = copyMe.getLocatorId();
      this.analysisName = copyMe.getClassAnalysisName();
      if (copyMe.getAdvertisedContracts() != null && !copyMe.getAdvertisedContracts().isEmpty()) {
         this.contracts = new LinkedHashSet<>();
         this.contracts.addAll(copyMe.getAdvertisedContracts());
      }

      if (copyMe.getQualifiers() != null && !copyMe.getQualifiers().isEmpty()) {
         this.qualifiers = new LinkedHashSet<>();
         this.qualifiers.addAll(copyMe.getQualifiers());
      }

      if (copyMe.getMetadata() != null && !copyMe.getMetadata().isEmpty()) {
         this.metadatas = new LinkedHashMap<>();
         this.metadatas.putAll(ReflectionHelper.deepCopyMetadata(copyMe.getMetadata()));
      }
   }

   public DescriptorImpl(
      Set<String> contracts,
      String name,
      String scope,
      String implementation,
      Map<String, List<String>> metadatas,
      Set<String> qualifiers,
      DescriptorType descriptorType,
      DescriptorVisibility descriptorVisibility,
      HK2Loader loader,
      int rank,
      Boolean proxiable,
      Boolean proxyForSameScope,
      String analysisName,
      Long id,
      Long locatorId
   ) {
      if (contracts != null && !contracts.isEmpty()) {
         this.contracts = new LinkedHashSet<>();
         this.contracts.addAll(contracts);
      }

      this.implementation = implementation;
      this.name = name;
      this.scope = scope;
      if (metadatas != null && !metadatas.isEmpty()) {
         this.metadatas = new LinkedHashMap<>();
         this.metadatas.putAll(ReflectionHelper.deepCopyMetadata(metadatas));
      }

      if (qualifiers != null && !qualifiers.isEmpty()) {
         this.qualifiers = new LinkedHashSet<>();
         this.qualifiers.addAll(qualifiers);
      }

      this.descriptorType = descriptorType;
      this.descriptorVisibility = descriptorVisibility;
      this.id = id;
      this.rank = rank;
      this.proxiable = proxiable;
      this.proxyForSameScope = proxyForSameScope;
      this.analysisName = analysisName;
      this.locatorId = locatorId;
      this.loader = loader;
   }

   @Override
   public synchronized Set<String> getAdvertisedContracts() {
      return this.contracts == null ? EMPTY_CONTRACTS_SET : Collections.unmodifiableSet(this.contracts);
   }

   public synchronized void addAdvertisedContract(String addMe) {
      if (addMe != null) {
         if (this.contracts == null) {
            this.contracts = new LinkedHashSet<>();
         }

         this.contracts.add(addMe);
      }
   }

   public synchronized boolean removeAdvertisedContract(String removeMe) {
      return removeMe != null && this.contracts != null ? this.contracts.remove(removeMe) : false;
   }

   @Override
   public synchronized String getImplementation() {
      return this.implementation;
   }

   public synchronized void setImplementation(String implementation) {
      this.implementation = implementation;
   }

   @Override
   public synchronized String getScope() {
      return this.scope;
   }

   public synchronized void setScope(String scope) {
      this.scope = scope;
   }

   @Override
   public synchronized String getName() {
      return this.name;
   }

   public synchronized void setName(String name) {
      this.name = name;
   }

   @Override
   public synchronized Set<String> getQualifiers() {
      return this.qualifiers == null ? EMPTY_QUALIFIER_SET : Collections.unmodifiableSet(this.qualifiers);
   }

   public synchronized void addQualifier(String addMe) {
      if (addMe != null) {
         if (this.qualifiers == null) {
            this.qualifiers = new LinkedHashSet<>();
         }

         this.qualifiers.add(addMe);
      }
   }

   public synchronized boolean removeQualifier(String removeMe) {
      if (removeMe == null) {
         return false;
      } else {
         return this.qualifiers == null ? false : this.qualifiers.remove(removeMe);
      }
   }

   @Override
   public synchronized DescriptorType getDescriptorType() {
      return this.descriptorType;
   }

   public synchronized void setDescriptorType(DescriptorType descriptorType) {
      if (descriptorType == null) {
         throw new IllegalArgumentException();
      } else {
         this.descriptorType = descriptorType;
      }
   }

   @Override
   public synchronized DescriptorVisibility getDescriptorVisibility() {
      return this.descriptorVisibility;
   }

   public synchronized void setDescriptorVisibility(DescriptorVisibility descriptorVisibility) {
      if (descriptorVisibility == null) {
         throw new IllegalArgumentException();
      } else {
         this.descriptorVisibility = descriptorVisibility;
      }
   }

   @Override
   public synchronized Map<String, List<String>> getMetadata() {
      return this.metadatas == null ? EMPTY_METADATAS_MAP : Collections.unmodifiableMap(this.metadatas);
   }

   public synchronized void setMetadata(Map<String, List<String>> metadata) {
      if (this.metadatas == null) {
         this.metadatas = new LinkedHashMap<>();
      } else {
         this.metadatas.clear();
      }

      this.metadatas.putAll(ReflectionHelper.deepCopyMetadata(metadata));
   }

   public synchronized void addMetadata(Map<String, List<String>> metadata) {
      if (this.metadatas == null) {
         this.metadatas = new LinkedHashMap<>();
      }

      this.metadatas.putAll(ReflectionHelper.deepCopyMetadata(metadata));
   }

   public synchronized void addMetadata(String key, String value) {
      if (this.metadatas == null) {
         this.metadatas = new LinkedHashMap<>();
      }

      ReflectionHelper.addMetadata(this.metadatas, key, value);
   }

   public synchronized boolean removeMetadata(String key, String value) {
      return this.metadatas == null ? false : ReflectionHelper.removeMetadata(this.metadatas, key, value);
   }

   public synchronized boolean removeAllMetadata(String key) {
      return this.metadatas == null ? false : ReflectionHelper.removeAllMetadata(this.metadatas, key);
   }

   public synchronized void clearMetadata() {
      this.metadatas = null;
   }

   @Override
   public synchronized HK2Loader getLoader() {
      return this.loader;
   }

   public synchronized void setLoader(HK2Loader loader) {
      this.loader = loader;
   }

   @Override
   public synchronized int getRanking() {
      return this.rank;
   }

   @Override
   public synchronized int setRanking(int ranking) {
      int retVal = this.rank;
      this.rank = ranking;
      return retVal;
   }

   @Override
   public synchronized Long getServiceId() {
      return this.id;
   }

   public synchronized void setServiceId(Long id) {
      this.id = id;
   }

   @Override
   public Boolean isProxiable() {
      return this.proxiable;
   }

   public void setProxiable(Boolean proxiable) {
      this.proxiable = proxiable;
   }

   @Override
   public Boolean isProxyForSameScope() {
      return this.proxyForSameScope;
   }

   public void setProxyForSameScope(Boolean proxyForSameScope) {
      this.proxyForSameScope = proxyForSameScope;
   }

   @Override
   public String getClassAnalysisName() {
      return this.analysisName;
   }

   public void setClassAnalysisName(String name) {
      this.analysisName = name;
   }

   @Override
   public synchronized Long getLocatorId() {
      return this.locatorId;
   }

   public synchronized void setLocatorId(Long locatorId) {
      this.locatorId = locatorId;
   }

   @Override
   public int hashCode() {
      int retVal = 0;
      if (this.implementation != null) {
         retVal ^= this.implementation.hashCode();
      }

      if (this.contracts != null) {
         for (String contract : this.contracts) {
            retVal ^= contract.hashCode();
         }
      }

      if (this.name != null) {
         retVal ^= this.name.hashCode();
      }

      if (this.scope != null) {
         retVal ^= this.scope.hashCode();
      }

      if (this.qualifiers != null) {
         for (String qualifier : this.qualifiers) {
            retVal ^= qualifier.hashCode();
         }
      }

      if (this.descriptorType != null) {
         retVal ^= this.descriptorType.hashCode();
      }

      if (this.descriptorVisibility != null) {
         retVal ^= this.descriptorVisibility.hashCode();
      }

      if (this.metadatas != null) {
         for (Entry<String, List<String>> entries : this.metadatas.entrySet()) {
            retVal ^= entries.getKey().hashCode();

            for (String value : entries.getValue()) {
               retVal ^= value.hashCode();
            }
         }
      }

      if (this.proxiable != null) {
         if (this.proxiable) {
            retVal ^= 1;
         } else {
            retVal = ~retVal;
         }
      }

      if (this.proxyForSameScope != null) {
         if (this.proxyForSameScope) {
            retVal ^= 2;
         } else {
            retVal ^= -2;
         }
      }

      if (this.analysisName != null) {
         retVal ^= this.analysisName.hashCode();
      }

      return retVal;
   }

   private static <T> boolean equalOrderedCollection(Collection<T> a, Collection<T> b) {
      if (a == b) {
         return true;
      } else if (a == null) {
         return false;
      } else if (b == null) {
         return false;
      } else if (a.size() != b.size()) {
         return false;
      } else {
         Object[] aAsArray = a.toArray();
         Object[] bAsArray = b.toArray();

         for (int lcv = 0; lcv < a.size(); lcv++) {
            if (!GeneralUtilities.safeEquals(aAsArray[lcv], bAsArray[lcv])) {
               return false;
            }
         }

         return true;
      }
   }

   private static <T> boolean equalMetadata(Map<String, List<String>> a, Map<String, List<String>> b) {
      if (a == b) {
         return true;
      } else if (a == null) {
         return false;
      } else if (b == null) {
         return false;
      } else if (a.size() != b.size()) {
         return false;
      } else {
         for (Entry<String, List<String>> entry : a.entrySet()) {
            String aKey = entry.getKey();
            List<String> aValue = entry.getValue();
            List<String> bValue = b.get(aKey);
            if (bValue == null) {
               return false;
            }

            if (!equalOrderedCollection(aValue, bValue)) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean descriptorEquals(Descriptor a, Descriptor b) {
      if (a == null && b == null) {
         return true;
      } else if (a != null && b != null) {
         if (!GeneralUtilities.safeEquals(a.getImplementation(), b.getImplementation())) {
            return false;
         } else if (!equalOrderedCollection(a.getAdvertisedContracts(), b.getAdvertisedContracts())) {
            return false;
         } else if (!GeneralUtilities.safeEquals(a.getName(), b.getName())) {
            return false;
         } else if (!GeneralUtilities.safeEquals(a.getScope(), b.getScope())) {
            return false;
         } else if (!equalOrderedCollection(a.getQualifiers(), b.getQualifiers())) {
            return false;
         } else if (!GeneralUtilities.safeEquals(a.getDescriptorType(), b.getDescriptorType())) {
            return false;
         } else if (!GeneralUtilities.safeEquals(a.getDescriptorVisibility(), b.getDescriptorVisibility())) {
            return false;
         } else if (!equalMetadata(a.getMetadata(), b.getMetadata())) {
            return false;
         } else if (!GeneralUtilities.safeEquals(a.isProxiable(), b.isProxiable())) {
            return false;
         } else {
            return !GeneralUtilities.safeEquals(a.isProxyForSameScope(), b.isProxyForSameScope())
               ? false
               : GeneralUtilities.safeEquals(a.getClassAnalysisName(), b.getClassAnalysisName());
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean equals(Object a) {
      if (a == null) {
         return false;
      } else if (!(a instanceof Descriptor)) {
         return false;
      } else {
         Descriptor d = (Descriptor)a;
         return descriptorEquals(this, d);
      }
   }

   public static void pretty(StringBuffer sb, Descriptor d) {
      if (sb != null && d != null) {
         sb.append("\n\timplementation=" + d.getImplementation());
         if (d.getName() != null) {
            sb.append("\n\tname=" + d.getName());
         }

         sb.append("\n\tcontracts=");
         sb.append(ReflectionHelper.writeSet(d.getAdvertisedContracts()));
         sb.append("\n\tscope=" + d.getScope());
         sb.append("\n\tqualifiers=");
         sb.append(ReflectionHelper.writeSet(d.getQualifiers()));
         sb.append("\n\tdescriptorType=" + d.getDescriptorType());
         sb.append("\n\tdescriptorVisibility=" + d.getDescriptorVisibility());
         sb.append("\n\tmetadata=");
         sb.append(ReflectionHelper.writeMetadata(d.getMetadata()));
         sb.append("\n\trank=" + d.getRanking());
         sb.append("\n\tloader=" + d.getLoader());
         sb.append("\n\tproxiable=" + d.isProxiable());
         sb.append("\n\tproxyForSameScope=" + d.isProxyForSameScope());
         sb.append("\n\tanalysisName=" + d.getClassAnalysisName());
         sb.append("\n\tid=" + d.getServiceId());
         sb.append("\n\tlocatorId=" + d.getLocatorId());
         sb.append("\n\tidentityHashCode=" + System.identityHashCode(d));
      }
   }

   @Override
   public synchronized String toString() {
      StringBuffer sb = new StringBuffer("Descriptor(");
      pretty(sb, this);
      sb.append(")");
      return sb.toString();
   }

   public void writeObject(PrintWriter out) throws IOException {
      out.print("[");
      if (this.implementation != null) {
         out.print(this.implementation);
      }

      out.print("]");
      if (this.scope != null && this.scope.equals(Singleton.class.getName())) {
         out.print("S");
      }

      boolean implementationInContracts = true;
      if (this.contracts != null && this.implementation != null && !this.contracts.contains(this.implementation)) {
         out.print("-");
         implementationInContracts = false;
      }

      out.println();
      if (this.contracts != null && !this.contracts.isEmpty() && (!implementationInContracts || this.contracts.size() > 1)) {
         String excluded = implementationInContracts ? this.implementation : null;
         out.println("contract=" + ReflectionHelper.writeSet(this.contracts, excluded));
      }

      if (this.name != null) {
         out.println("name=" + this.name);
      }

      if (this.scope != null && !this.scope.equals(PerLookup.class.getName()) && !this.scope.equals(Singleton.class.getName())) {
         out.println("scope=" + this.scope);
      }

      if (this.qualifiers != null && !this.qualifiers.isEmpty()) {
         out.println("qualifier=" + ReflectionHelper.writeSet(this.qualifiers));
      }

      if (this.descriptorType != null && this.descriptorType.equals(DescriptorType.PROVIDE_METHOD)) {
         out.println("type=PROVIDE");
      }

      if (this.descriptorVisibility != null && this.descriptorVisibility.equals(DescriptorVisibility.LOCAL)) {
         out.println("visibility=LOCAL");
      }

      if (this.rank != 0) {
         out.println("rank=" + this.rank);
      }

      if (this.proxiable != null) {
         out.println("proxiable=" + this.proxiable);
      }

      if (this.proxyForSameScope != null) {
         out.println("proxyForSameScope=" + this.proxyForSameScope);
      }

      if (this.analysisName != null && !"default".equals(this.analysisName)) {
         out.println("analysis=" + this.analysisName);
      }

      if (this.metadatas != null && !this.metadatas.isEmpty()) {
         out.println("metadata=" + ReflectionHelper.writeMetadata(this.metadatas));
      }

      out.println();
   }

   private void reinitialize() {
      this.contracts = null;
      this.implementation = null;
      this.name = null;
      this.scope = PerLookup.class.getName();
      this.metadatas = null;
      this.qualifiers = null;
      this.descriptorType = DescriptorType.CLASS;
      this.descriptorVisibility = DescriptorVisibility.NORMAL;
      this.loader = null;
      this.rank = 0;
      this.proxiable = null;
      this.proxyForSameScope = null;
      this.analysisName = null;
      this.id = null;
      this.locatorId = null;
   }

   public boolean readObject(BufferedReader in) throws IOException {
      this.reinitialize();
      String line = in.readLine();

      boolean sectionStarted;
      for (sectionStarted = false; line != null; line = in.readLine()) {
         String trimmed = line.trim();
         if (!sectionStarted) {
            if (trimmed.startsWith("[")) {
               sectionStarted = true;
               int endStartIndex = trimmed.indexOf(93, 1);
               if (endStartIndex < 0) {
                  throw new IOException("Start of implementation ends without ] character: " + trimmed);
               }

               if (endStartIndex > 1) {
                  this.implementation = trimmed.substring(1, endStartIndex);
               }

               String directives = trimmed.substring(endStartIndex + 1);
               boolean doesNotContainImplementation = false;
               if (directives != null) {
                  for (int lcv = 0; lcv < directives.length(); lcv++) {
                     char charAt = directives.charAt(lcv);
                     if (charAt == 'S') {
                        this.scope = Singleton.class.getName();
                     } else if (charAt == '-') {
                        doesNotContainImplementation = true;
                     }
                  }
               }

               if (!doesNotContainImplementation && this.implementation != null) {
                  if (this.contracts == null) {
                     this.contracts = new LinkedHashSet<>();
                  }

                  this.contracts.add(this.implementation);
               }
            }
         } else {
            if (trimmed.length() <= 0) {
               return true;
            }

            int equalsIndex = trimmed.indexOf(61);
            if (equalsIndex >= 1) {
               String leftHandSide = trimmed.substring(0, equalsIndex + 1);
               String rightHandSide = trimmed.substring(equalsIndex + 1);
               if (leftHandSide.equalsIgnoreCase("contract=")) {
                  if (this.contracts == null) {
                     this.contracts = new LinkedHashSet<>();
                  }

                  ReflectionHelper.readSet(rightHandSide, this.contracts);
               } else if (leftHandSide.equals("qualifier=")) {
                  LinkedHashSet<String> localQualifiers = new LinkedHashSet<>();
                  ReflectionHelper.readSet(rightHandSide, localQualifiers);
                  if (!localQualifiers.isEmpty()) {
                     this.qualifiers = localQualifiers;
                  }
               } else if (leftHandSide.equals("name=")) {
                  this.name = rightHandSide;
               } else if (leftHandSide.equals("scope=")) {
                  this.scope = rightHandSide;
               } else if (leftHandSide.equals("type=")) {
                  if (rightHandSide.equals("PROVIDE")) {
                     this.descriptorType = DescriptorType.PROVIDE_METHOD;
                  }
               } else if (leftHandSide.equals("visibility=")) {
                  if (rightHandSide.equals("LOCAL")) {
                     this.descriptorVisibility = DescriptorVisibility.LOCAL;
                  }
               } else if (leftHandSide.equals("metadata=")) {
                  LinkedHashMap<String, List<String>> localMetadatas = new LinkedHashMap<>();
                  ReflectionHelper.readMetadataMap(rightHandSide, localMetadatas);
                  if (!localMetadatas.isEmpty()) {
                     this.metadatas = localMetadatas;
                  }
               } else if (leftHandSide.equals("rank=")) {
                  this.rank = Integer.parseInt(rightHandSide);
               } else if (leftHandSide.equals("proxiable=")) {
                  this.proxiable = Boolean.parseBoolean(rightHandSide);
               } else if (leftHandSide.equals("proxyForSameScope=")) {
                  this.proxyForSameScope = Boolean.parseBoolean(rightHandSide);
               } else if (leftHandSide.equals("analysis=")) {
                  this.analysisName = rightHandSide;
               }
            }
         }
      }

      return sectionStarted;
   }

   @Override
   public void writeExternal(ObjectOutput out) throws IOException {
      StringWriter sw = new StringWriter();
      this.writeObject(new PrintWriter(sw));
      out.writeObject(sw.toString());
   }

   @Override
   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      String descriptorString = (String)in.readObject();
      this.readObject(new BufferedReader(new StringReader(descriptorString)));
   }
}
