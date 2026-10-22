package com.fasterxml.jackson.core;

import com.fasterxml.jackson.core.util.JacksonFeature;

public interface FormatFeature extends JacksonFeature {
   @Override
   boolean enabledByDefault();

   @Override
   int getMask();

   @Override
   boolean enabledIn(int var1);
}
