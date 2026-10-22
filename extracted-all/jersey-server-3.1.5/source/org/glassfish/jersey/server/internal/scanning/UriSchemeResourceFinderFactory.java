package org.glassfish.jersey.server.internal.scanning;

import java.net.URI;
import java.util.Set;
import org.glassfish.jersey.server.ResourceFinder;

interface UriSchemeResourceFinderFactory {
   Set<String> getSchemes();

   ResourceFinder create(URI var1, boolean var2);
}
