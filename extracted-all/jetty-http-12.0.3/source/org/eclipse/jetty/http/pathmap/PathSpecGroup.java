package org.eclipse.jetty.http.pathmap;

public enum PathSpecGroup {
   ROOT,
   EXACT,
   MIDDLE_GLOB,
   PREFIX_GLOB,
   SUFFIX_GLOB,
   DEFAULT;
}
