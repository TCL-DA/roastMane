package org.apache.logging.log4j.core.pattern;

interface ThrowableRenderer {
   void renderThrowable(final StringBuilder buffer, final Throwable throwable, final String lineSeparator);
}
