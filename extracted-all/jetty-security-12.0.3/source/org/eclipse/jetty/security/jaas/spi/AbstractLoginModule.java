package org.eclipse.jetty.security.jaas.spi;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import org.eclipse.jetty.security.UserPrincipal;
import org.eclipse.jetty.security.jaas.JAASRole;
import org.eclipse.jetty.security.jaas.callback.ObjectCallback;

public abstract class AbstractLoginModule implements LoginModule {
   private CallbackHandler callbackHandler;
   private boolean authState = false;
   private boolean commitState = false;
   private AbstractLoginModule.JAASUser currentUser;
   private Subject subject;

   public abstract AbstractLoginModule.JAASUser getUser(String var1) throws Exception;

   public Subject getSubject() {
      return this.subject;
   }

   public void setSubject(Subject s) {
      this.subject = s;
   }

   public AbstractLoginModule.JAASUser getCurrentUser() {
      return this.currentUser;
   }

   public void setCurrentUser(AbstractLoginModule.JAASUser u) {
      this.currentUser = u;
   }

   public CallbackHandler getCallbackHandler() {
      return this.callbackHandler;
   }

   public void setCallbackHandler(CallbackHandler h) {
      this.callbackHandler = h;
   }

   public boolean isAuthenticated() {
      return this.authState;
   }

   public boolean isCommitted() {
      return this.commitState;
   }

   public void setAuthenticated(boolean authState) {
      this.authState = authState;
   }

   public void setCommitted(boolean commitState) {
      this.commitState = commitState;
   }

   @Override
   public boolean abort() throws LoginException {
      this.currentUser = null;
      return this.isAuthenticated() && this.isCommitted();
   }

   @Override
   public boolean commit() throws LoginException {
      if (!this.isAuthenticated()) {
         this.currentUser = null;
         this.setCommitted(false);
         return false;
      } else {
         this.setCommitted(true);
         this.currentUser.setJAASInfo(this.subject);
         return true;
      }
   }

   public Callback[] configureCallbacks() {
      return new Callback[]{new NameCallback("Enter user name"), new ObjectCallback(), new PasswordCallback("Enter password", false)};
   }

   public boolean isIgnored() {
      return false;
   }

   @Override
   public boolean login() throws LoginException {
      try {
         if (this.isIgnored()) {
            return false;
         } else if (this.callbackHandler == null) {
            throw new LoginException("No callback handler");
         } else {
            Callback[] callbacks = this.configureCallbacks();
            this.callbackHandler.handle(callbacks);
            String webUserName = ((NameCallback)callbacks[0]).getName();
            Object webCredential = null;
            webCredential = ((ObjectCallback)callbacks[1]).getObject();
            if (webCredential == null) {
               webCredential = ((PasswordCallback)callbacks[2]).getPassword();
            }

            if (webUserName != null && webCredential != null) {
               AbstractLoginModule.JAASUser user = this.getUser(webUserName);
               if (user == null) {
                  this.setAuthenticated(false);
                  throw new FailedLoginException();
               } else {
                  this.currentUser = user;
                  this.setAuthenticated(this.currentUser.checkCredential(webCredential));
                  if (this.isAuthenticated()) {
                     this.currentUser.fetchRoles();
                     return true;
                  } else {
                     throw new FailedLoginException();
                  }
               }
            } else {
               this.setAuthenticated(false);
               throw new FailedLoginException();
            }
         }
      } catch (IOException var5) {
         throw new LoginException(var5.toString());
      } catch (UnsupportedCallbackException var6) {
         throw new LoginException(var6.toString());
      } catch (Exception var7) {
         if (var7 instanceof LoginException) {
            throw (LoginException)var7;
         } else {
            throw new LoginException(var7.toString());
         }
      }
   }

   @Override
   public boolean logout() throws LoginException {
      this.currentUser.unsetJAASInfo(this.subject);
      this.currentUser = null;
      return true;
   }

   @Override
   public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
      this.callbackHandler = callbackHandler;
      this.subject = subject;
   }

   public abstract static class JAASUser {
      private final UserPrincipal _user;
      private List<JAASRole> _roles;

      public JAASUser(UserPrincipal u) {
         this._user = u;
      }

      public String getUserName() {
         return this._user.getName();
      }

      public void setJAASInfo(Subject subject) {
         if (this._user != null) {
            this._user.configureSubject(subject);
            if (this._roles != null) {
               subject.getPrincipals().addAll(this._roles);
            }
         }
      }

      public void unsetJAASInfo(Subject subject) {
         if (this._user != null) {
            this._user.deconfigureSubject(subject);
            if (this._roles != null) {
               subject.getPrincipals().removeAll(this._roles);
            }
         }
      }

      public boolean checkCredential(Object suppliedCredential) {
         return this._user.authenticate(suppliedCredential);
      }

      public void fetchRoles() throws Exception {
         List<String> rolenames = this.doFetchRoles();
         if (rolenames != null) {
            this._roles = rolenames.stream().map(JAASRole::new).collect(Collectors.toList());
         }
      }

      public abstract List<String> doFetchRoles() throws Exception;
   }
}
