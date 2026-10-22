package org.glassfish.jersey.server;

import jakarta.ws.rs.core.SecurityContext;
import java.security.PrivilegedAction;

public interface SubjectSecurityContext extends SecurityContext {
   Object doAsSubject(PrivilegedAction var1);
}
