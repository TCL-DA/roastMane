package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.model.ControlStateConfiguration;
import com.cropster.roastingintelligence.model.SeparatorType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import org.cropster.roastingintelligence.AppModel;
import org.cropster.roastingintelligence.measurement.ControlStateSource;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.model.ControlState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControlStateBar extends VBox {
   private final Map<ControlStateConfiguration, ControlSwitch> controlSwitches = new HashMap<>();
   private final Set<ControlStateSource> sources = new HashSet<>();
   private final Logger logger = LoggerFactory.getLogger(ControlStateBar.class);

   public boolean updateControls(AppModel appModel) {
      List<ControlStateConfiguration> toBeRemoved = new ArrayList<>(this.controlSwitches.keySet());
      List<ControlStateConfiguration> stateConfigurations = appModel.getPreferences().getStates();
      stateConfigurations.forEach(
         configuration -> {
            ControlSwitch controlSwitchx = this.controlSwitches.get(configuration);
            if (controlSwitchx == null) {
               ControlStateSource controlStateSource = this.sourceFor(configuration);
               if (controlStateSource != null) {
                  controlSwitchx = new ControlSwitch(configuration, controlStateSource);
                  this.controlSwitches.put(configuration, controlSwitchx);
                  this.getChildren().add(controlSwitchx);
               } else {
                  this.logger
                     .warn(
                        "No source found for control state {} ({})",
                        configuration.getState().getName(),
                        configuration.getState().getSourceFactory().getSimpleName()
                     );
               }
            }

            toBeRemoved.remove(configuration);
         }
      );
      toBeRemoved.forEach(this.controlSwitches::remove);
      this.getChildren().clear();

      for (int i = 0; i < stateConfigurations.size(); i++) {
         ControlStateConfiguration stateConfiguration = stateConfigurations.get(i);
         if (i > 0 && stateConfiguration.getSeparator() == SeparatorType.ABOVE && !(this.getChildren().get(this.getChildren().size() - 1) instanceof Separator)
            )
          {
            this.getChildren().add(new Separator());
         }

         ControlSwitch controlSwitch = this.controlSwitches.get(stateConfiguration);
         if (controlSwitch != null) {
            controlSwitch.updateSwitch();
            this.getChildren().add(controlSwitch);
         }

         if (i < stateConfigurations.size() - 1 && stateConfiguration.getSeparator() == SeparatorType.BELOW) {
            this.getChildren().add(new Separator());
         }
      }

      return this.controlSwitches.values().stream().anyMatch(Node::isVisible);
   }

   public Map<ControlState, ControlSwitch> getControlSwitches() {
      return this.controlSwitches
         .entrySet()
         .stream()
         .map(entry -> Map.entry(entry.getKey().getState(), entry.getValue()))
         .collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), Map::putAll);
   }

   public void setSources(Set<MeasurementSource> measurementSources) {
      this.sources.clear();
      this.sources.addAll(measurementSources.stream().filter(ControlStateSource.class::isInstance).map(ControlStateSource.class::cast).toList());
   }

   private ControlStateSource sourceFor(ControlStateConfiguration stateConfiguration) {
      ControlState controlState = stateConfiguration.getState();
      return this.sources
         .stream()
         .filter(source -> List.of(source.getFactory().getSupportedStates(source.getFactory().getParameters())).contains(controlState))
         .findFirst()
         .orElse(null);
   }
}
