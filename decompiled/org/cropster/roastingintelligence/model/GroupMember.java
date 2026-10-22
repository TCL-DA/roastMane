package org.cropster.roastingintelligence.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collection;
import java.util.Collections;

public class GroupMember {
   public static final String MODULE_RI = "roast.roastingintelligence";
   public static final String MODULE_GOALS = "roast.goals";
   public static final String MODULE_TASK = "task";
   public static final String PERMISSION_RI = "roastRoastingIntelligence";
   public static final String PERMISSION_RI_BLEND = "roastRoastingIntelligenceBlend";
   public static final String PERMISSION_RI_MARK_REFERENCE = "roastRoastingIntelligenceMarkReference";
   private final String groupCode;
   private final Collection<String> modules;
   private final Collection<String> permissions;
   private final String username;

   @JsonCreator
   public GroupMember(
      @JsonProperty("groupCode") String groupCode,
      @JsonProperty("username") String username,
      @JsonProperty("modules") Collection<String> modules,
      @JsonProperty("permissions") Collection<String> permissions
   ) {
      this.groupCode = groupCode;
      this.username = username;
      this.modules = modules;
      this.permissions = permissions;
   }

   public String getGroupCode() {
      return this.groupCode;
   }

   public Collection<String> getModules() {
      return Collections.unmodifiableCollection(this.modules);
   }

   public Collection<String> getPermissions() {
      return Collections.unmodifiableCollection(this.permissions);
   }

   public String getUsername() {
      return this.username;
   }
}
