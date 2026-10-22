package org.yaml.snakeyaml;

public class LoaderOptions {
   private boolean allowDuplicateKeys = true;
   private boolean wrappedToRootException = false;
   private int maxAliasesForCollections = 50;
   private boolean allowRecursiveKeys = false;
   private boolean processComments = false;
   private boolean enumCaseSensitive = true;
   private int nestingDepthLimit = 50;
   private int codePointLimit = 3145728;

   public final boolean isAllowDuplicateKeys() {
      return this.allowDuplicateKeys;
   }

   public void setAllowDuplicateKeys(boolean allowDuplicateKeys) {
      this.allowDuplicateKeys = allowDuplicateKeys;
   }

   public final boolean isWrappedToRootException() {
      return this.wrappedToRootException;
   }

   public void setWrappedToRootException(boolean wrappedToRootException) {
      this.wrappedToRootException = wrappedToRootException;
   }

   public final int getMaxAliasesForCollections() {
      return this.maxAliasesForCollections;
   }

   public void setMaxAliasesForCollections(int maxAliasesForCollections) {
      this.maxAliasesForCollections = maxAliasesForCollections;
   }

   public void setAllowRecursiveKeys(boolean allowRecursiveKeys) {
      this.allowRecursiveKeys = allowRecursiveKeys;
   }

   public final boolean getAllowRecursiveKeys() {
      return this.allowRecursiveKeys;
   }

   public LoaderOptions setProcessComments(boolean processComments) {
      this.processComments = processComments;
      return this;
   }

   public final boolean isProcessComments() {
      return this.processComments;
   }

   public final boolean isEnumCaseSensitive() {
      return this.enumCaseSensitive;
   }

   public void setEnumCaseSensitive(boolean enumCaseSensitive) {
      this.enumCaseSensitive = enumCaseSensitive;
   }

   public final int getNestingDepthLimit() {
      return this.nestingDepthLimit;
   }

   public void setNestingDepthLimit(int nestingDepthLimit) {
      this.nestingDepthLimit = nestingDepthLimit;
   }

   public final int getCodePointLimit() {
      return this.codePointLimit;
   }

   public void setCodePointLimit(int codePointLimit) {
      this.codePointLimit = codePointLimit;
   }
}
