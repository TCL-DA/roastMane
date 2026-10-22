package org.eclipse.jetty.security;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.security.Credential;

public class UserStore extends ContainerLifeCycle {
   protected final Map<String, UserStore.User> _users = new ConcurrentHashMap<>();

   public void addUser(String username, Credential credential, String[] roles) {
      this._users.put(username, new UserStore.User(username, credential, roles));
   }

   public void removeUser(String username) {
      this._users.remove(username);
   }

   public UserPrincipal getUserPrincipal(String username) {
      UserStore.User user = this._users.get(username);
      return user == null ? null : user.getUserPrincipal();
   }

   public List<RolePrincipal> getRolePrincipals(String username) {
      UserStore.User user = this._users.get(username);
      return user == null ? null : user.getRolePrincipals();
   }

   public String toString() {
      return String.format("%s@%x[users.count=%d]", this.getClass().getSimpleName(), this.hashCode(), this._users.size());
   }

   protected class User {
      protected UserPrincipal _userPrincipal;
      protected List<RolePrincipal> _rolePrincipals;

      protected User(String username, Credential credential, String[] roles) {
         this._userPrincipal = new UserPrincipal(username, credential);
         this._rolePrincipals = roles != null && roles.length != 0
            ? Arrays.stream(roles).map(RolePrincipal::new).collect(Collectors.toList())
            : Collections.emptyList();
      }

      protected UserPrincipal getUserPrincipal() {
         return this._userPrincipal;
      }

      protected List<RolePrincipal> getRolePrincipals() {
         return this._rolePrincipals;
      }
   }
}
