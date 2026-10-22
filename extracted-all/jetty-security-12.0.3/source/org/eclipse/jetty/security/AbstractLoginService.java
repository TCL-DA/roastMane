package org.eclipse.jetty.security;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javax.security.auth.Subject;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Session;
import org.eclipse.jetty.util.component.ContainerLifeCycle;

public abstract class AbstractLoginService extends ContainerLifeCycle implements LoginService {
   protected IdentityService _identityService = new DefaultIdentityService();
   protected String _name;
   protected boolean _fullValidate = false;

   protected abstract List<RolePrincipal> loadRoleInfo(UserPrincipal var1);

   protected abstract UserPrincipal loadUserInfo(String var1);

   protected AbstractLoginService() {
      this.addBean(this._identityService);
   }

   @Override
   public String getName() {
      return this._name;
   }

   @Override
   public void setIdentityService(IdentityService identityService) {
      if (this.isRunning()) {
         throw new IllegalStateException("Running");
      } else {
         this.updateBean(this._identityService, identityService);
         this._identityService = identityService;
      }
   }

   public void setName(String name) {
      if (this.isRunning()) {
         throw new IllegalStateException("Running");
      } else {
         this._name = name;
      }
   }

   @Override
   public String toString() {
      return String.format("%s@%x[%s]", this.getClass().getSimpleName(), this.hashCode(), this._name);
   }

   @Override
   public UserIdentity login(String username, Object credentials, Request request, Function<Boolean, Session> getOrCreateSession) {
      if (username == null) {
         return null;
      } else {
         UserPrincipal userPrincipal = this.loadUserInfo(username);
         if (userPrincipal != null && userPrincipal.authenticate(credentials)) {
            List<RolePrincipal> roles = this.loadRoleInfo(userPrincipal);
            List<String> roleNames = new ArrayList<>();
            Subject subject = new Subject();
            userPrincipal.configureSubject(subject);
            if (roles != null) {
               roles.forEach(p -> {
                  p.configureForSubject(subject);
                  roleNames.add(p.getName());
               });
            }

            subject.setReadOnly();
            return this._identityService.newUserIdentity(subject, userPrincipal, roleNames.toArray(new String[0]));
         } else {
            return null;
         }
      }
   }

   @Override
   public boolean validate(UserIdentity user) {
      if (!this.isFullValidate()) {
         return true;
      } else {
         UserPrincipal fresh = this.loadUserInfo(user.getUserPrincipal().getName());
         if (fresh == null) {
            return false;
         } else if (user.getUserPrincipal() instanceof UserPrincipal) {
            return fresh.authenticate((UserPrincipal)user.getUserPrincipal());
         } else {
            throw new IllegalStateException("UserPrincipal not known");
         }
      }
   }

   @Override
   public IdentityService getIdentityService() {
      return this._identityService;
   }

   @Override
   public void logout(UserIdentity user) {
   }

   public boolean isFullValidate() {
      return this._fullValidate;
   }

   public void setFullValidate(boolean fullValidate) {
      this._fullValidate = fullValidate;
   }
}
