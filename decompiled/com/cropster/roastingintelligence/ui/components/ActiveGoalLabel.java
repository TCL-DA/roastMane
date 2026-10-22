package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.model.livegoals.CheckResult;
import com.cropster.roastingintelligence.model.livegoals.LiveGoal;
import com.cropster.roastingintelligence.model.livegoals.LiveGoalDisplayMode;
import com.cropster.roastingintelligence.model.livegoals.checkers.AbstractChecker;
import com.cropster.roastingintelligence.service.LiveGoalService;
import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.util.Icon;
import com.cropster.util.model.Unit;
import java.net.URL;
import java.util.EnumSet;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.StringUtils;
import org.cropster.roastingintelligence.model.Goal;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.util.MeasureFormat;
import org.girod.javafx.svgimage.SVGImage;

public class ActiveGoalLabel extends VBox implements Initializable {
   private static final String STYLE_FAILED = "goal-failed";
   private static final String STYLE_INVALID = "goal-invalid";
   private static final String STYLE_PASSED = "goal-passed";
   private static final String STYLE_PROGRESS_FAILED = "goal-progress-failed";
   private static final String STYLE_PROGRESS_MISSING = "goal-progress-missing";
   private static final String STYLE_PROGRESS_PASSED = "goal-progress-passed";
   private static final String STYLE_ICON_MUTED = "icon--muted";
   @FXML
   private Label nameLabel;
   @FXML
   private Label displayModeIcon;
   @FXML
   private Label statusIcon;
   @FXML
   private HBox referenceContainer;
   @FXML
   private Label checkedValueLabel;
   @FXML
   private Label unitLabel;
   @FXML
   private Label referenceLabel;
   @FXML
   private VBox progressContainer;
   @FXML
   private ProgressBar goalProgressBar;
   @FXML
   private Label missingLowerBoundLabel;
   @FXML
   private Label lowerBoundLabel;
   @FXML
   private Label upperBoundLabel;
   @FXML
   private Label missingUpperBoundLabel;
   private final Map<String, String> localizedNames;

   private static SVGImage iconFor(CheckResult.CheckStatus checkStatus) {
      if (checkStatus == null) {
         return Icon.WARNING.load("icon--warning", 0.75);
      } else {
         return switch (checkStatus) {
            case FAILED -> Icon.ERROR.load("icon--error", 0.75);
            case INITIALIZED -> null;
            case INVALID -> Icon.WARNING.load("icon--warning", 0.75);
            case MISSING, WAITING -> Icon.LIVE.load("icon--muted", 0.75);
            case PASSED -> Icon.SUCCESS.load("icon--success", 0.75);
         };
      }
   }

   private static SVGImage iconFor(LiveGoalDisplayMode displayMode) {
      return switch (displayMode) {
         case HIDDEN -> null;
         case MEET_THE_REFERENCE -> Icon.PROFILE.load("icon--muted", 0.75);
         case RANGE -> Icon.GOAL.load("icon--muted", 0.75);
         case TARGET -> Icon.TARGET.load("icon--muted", 0.75);
      };
   }

   public ActiveGoalLabel(Map<String, String> localizedNames) {
      JavaFxUtil.loadComponent(this);
      this.localizedNames = localizedNames;
   }

   public void initialize(URL url, ResourceBundle resourceBundle) {
      this.nameLabel.textProperty().addListener((s, o, n) -> {
         if (StringUtils.isBlank(n)) {
            this.nameLabel.setTooltip(null);
         } else if (this.nameLabel.getTooltip() == null || !this.nameLabel.getTooltip().getText().equals(n)) {
            this.nameLabel.setTooltip(new Tooltip(n));
         }
      });
      this.displayModeIcon.visibleProperty().bind(this.displayModeIcon.graphicProperty().isNotNull());
      this.statusIcon.visibleProperty().bind(this.statusIcon.graphicProperty().isNotNull());
      this.lowerBoundLabel.visibleProperty().bind(this.lowerBoundLabel.textProperty().isNotEmpty());
      this.upperBoundLabel.visibleProperty().bind(this.upperBoundLabel.textProperty().isNotEmpty());
      this.referenceContainer
         .visibleProperty()
         .bind(this.referenceLabel.visibleProperty().or(this.unitLabel.textProperty().isNotEmpty()).or(this.checkedValueLabel.visibleProperty()));
   }

   public void init(LiveGoalService.ActiveGoal activeGoal, boolean hideCurrentValue) {
      Goal goal = activeGoal.getGoal();
      LiveGoal liveGoal = activeGoal.getLiveGoal();
      LiveGoalDisplayMode displayMode = liveGoal.getDisplayMode();
      CheckResult checkResult = activeGoal.getLastCheckResult();
      CheckResult.CheckStatus checkStatus = checkResult != null ? checkResult.getCheckStatus() : null;
      this.nameLabel.setText(goal != null ? goal.getLocalizedMessage() : this.localizedNames.get(liveGoal.getId()));
      this.checkedValueLabel.setText("–");
      this.checkedValueLabel.setVisible(!hideCurrentValue);
      this.initIcons(displayMode, checkStatus);
      if (this.initStatusStyle(goal, checkResult, checkStatus)) {
         MeasureFormat measureFormat = MeasureFormat.getInstance();
         Measure referenceValue = this.initReferenceLabel(goal, activeGoal.getReferenceValue(), displayMode, measureFormat);
         if (displayMode != LiveGoalDisplayMode.TARGET && goal != null) {
            this.initRangeAndProgress(goal, referenceValue, measureFormat, checkResult, checkStatus);
            this.progressContainer.setVisible(true);
         } else {
            this.progressContainer.setVisible(false);
            this.initCheckedValue(measureFormat, checkResult);
         }
      }
   }

   private void initIcons(LiveGoalDisplayMode displayMode, CheckResult.CheckStatus checkStatus) {
      if (this.displayModeIcon.getGraphic() == null
         || this.displayModeIcon.getTooltip() == null
         || !this.displayModeIcon.getTooltip().getText().equals(org.cropster.roastingintelligence.util.Messages.m_(displayMode.name()))) {
         this.displayModeIcon.setGraphic(iconFor(displayMode));
         this.displayModeIcon.setTooltip(new Tooltip(org.cropster.roastingintelligence.util.Messages.m_(displayMode.name())));
      }

      if (checkStatus == null
         || this.statusIcon.getGraphic() == null
         || this.statusIcon.getTooltip() == null
         || !this.statusIcon.getTooltip().getText().equals(org.cropster.roastingintelligence.util.Messages.m_(checkStatus.name()))) {
         this.statusIcon.setGraphic(iconFor(checkStatus));
         this.statusIcon.setTooltip(checkStatus != null ? new Tooltip(org.cropster.roastingintelligence.util.Messages.m_(checkStatus.name())) : null);
      }
   }

   private boolean initStatusStyle(Goal goal, CheckResult checkResult, CheckResult.CheckStatus checkStatus) {
      if (goal == null || checkResult == null) {
         this.getChildren().forEach(child -> child.setOpacity(1.0));
         this.getStyleClass().removeAll(new String[]{"goal-invalid", "goal-failed", "goal-passed"});
         return true;
      } else if (checkStatus == CheckResult.CheckStatus.INVALID) {
         this.getChildren().forEach(child -> child.setOpacity(0.5));
         JavaFxUtil.replaceStyles(this.getStyleClass(), "goal-invalid", "goal-failed", "goal-passed");
         return false;
      } else {
         if (checkResult.getEvaluationStatus() == CheckResult.EvaluationStatus.COMPLETED) {
            this.getChildren().forEach(child -> child.setOpacity(0.5));
            this.applyCompletedStyle(checkStatus);
         } else {
            this.getChildren().forEach(child -> child.setOpacity(1.0));
            this.getStyleClass().removeAll(new String[]{"goal-invalid", "goal-failed", "goal-passed"});
         }

         return true;
      }
   }

   private void applyCompletedStyle(CheckResult.CheckStatus checkStatus) {
      if (checkStatus == CheckResult.CheckStatus.PASSED) {
         JavaFxUtil.replaceStyles(this.getStyleClass(), "goal-passed", "goal-invalid", "goal-failed");
      } else if (checkStatus == CheckResult.CheckStatus.FAILED) {
         JavaFxUtil.replaceStyles(this.getStyleClass(), "goal-failed", "goal-invalid", "goal-passed");
      }
   }

   private Measure initReferenceLabel(Goal goal, Measure referenceValue, LiveGoalDisplayMode displayMode, MeasureFormat measureFormat) {
      this.referenceLabel.setVisible(false);
      if (referenceValue != null) {
         this.referenceLabel.setTooltip(null);
         return this.initReferenceWithValue(referenceValue, displayMode, measureFormat);
      } else {
         if (goal != null) {
            this.referenceLabel.setTooltip(null);
            this.initReferenceWithoutValue(goal, measureFormat);
         } else {
            this.referenceLabel.setText(null);
            this.referenceLabel.setGraphic(Icon.WARNING.load("icon--warning", 0.75));
            this.referenceLabel.setVisible(true);
            if (this.referenceLabel.getTooltip() == null) {
               this.referenceLabel
                  .setTooltip(new Tooltip(org.cropster.roastingintelligence.util.Messages.m_("Use the info button at the top to see why data may be missing.")));
            }
         }

         return null;
      }
   }

   private Measure initReferenceWithValue(Measure referenceValue, LiveGoalDisplayMode displayMode, MeasureFormat measureFormat) {
      Measure measure = switch (referenceValue.getUnit()) {
         case MILLISECOND, SECOND, MINUTE, HOUR -> referenceValue.toUnit(Unit.DURATION);
         default -> referenceValue;
      };
      this.unitLabel.setText(measureFormat.formatUnit(measure));
      if (displayMode == LiveGoalDisplayMode.TARGET || displayMode == LiveGoalDisplayMode.MEET_THE_REFERENCE) {
         this.referenceLabel.setText(measureFormat.formatValue(measure));
         this.referenceLabel.setVisible(true);
      }

      return measure;
   }

   private void initReferenceWithoutValue(Goal goal, MeasureFormat measureFormat) {
      Unit unit = goal.getLowerBound().getUnit();
      if (unit == null) {
         unit = goal.getUpperBound().getUnit();
      }

      if (unit != null) {
         this.unitLabel.setText(measureFormat.formatUnit(new Measure(0.0, unit)));
      }
   }

   private void initCheckedValue(MeasureFormat measureFormat, CheckResult checkResult) {
      Measure checkedValue = checkResult != null ? checkResult.getCheckedValue() : null;
      if (checkedValue != null) {
         this.checkedValueLabel.setText(this.formatValue(checkedValue, measureFormat));
         if (StringUtils.isBlank(this.unitLabel.getText())) {
            this.unitLabel.setText(measureFormat.formatUnit(checkedValue));
         }
      } else {
         this.checkedValueLabel.setText("–");
      }
   }

   private void initRangeAndProgress(
      Goal goal, Measure referenceValue, MeasureFormat measureFormat, CheckResult checkResult, CheckResult.CheckStatus checkStatus
   ) {
      Measure lowerBound = AbstractChecker.calculateLowerBound(goal, referenceValue);
      String lowerBoundText = this.formatValue(lowerBound, measureFormat);
      this.lowerBoundLabel.setText(lowerBoundText);
      this.lowerBoundLabel.setTooltip(lowerBound != null ? new Tooltip(lowerBoundText) : null);
      this.missingLowerBoundLabel.setVisible(false);
      Measure upperBound = AbstractChecker.calculateUpperBound(goal, referenceValue);
      String upperBoundText = this.formatValue(upperBound, measureFormat);
      this.upperBoundLabel.setText(upperBoundText);
      this.upperBoundLabel.setTooltip(lowerBound != null ? new Tooltip(upperBoundText) : null);
      this.missingUpperBoundLabel.setVisible(false);
      Measure checkedValue = checkResult.getCheckedValue();
      if (checkedValue != null) {
         this.checkedValueLabel.setText(this.formatValue(checkedValue, measureFormat));
         Double minValue = lowerBound != null ? lowerBound.getAmount() : null;
         Double maxValue = upperBound != null ? upperBound.getAmount() : null;
         this.initProgressBar(checkResult, checkStatus, minValue, checkedValue, maxValue);
      } else {
         this.checkedValueLabel.setText("–");
      }
   }

   private String formatValue(Measure value, MeasureFormat measureFormat) {
      if (value == null) {
         return "–";
      } else {
         Measure toBeFormattedValue = value;
         Unit unit = value.getUnit();
         if (EnumSet.of(Unit.MILLISECOND, Unit.SECOND, Unit.MINUTE, Unit.HOUR).contains(unit)) {
            toBeFormattedValue = value.toUnit(Unit.DURATION);
         }

         return measureFormat.formatValue(toBeFormattedValue);
      }
   }

   private void initProgressBar(CheckResult checkResult, CheckResult.CheckStatus checkStatus, Double minValue, Measure checkedValue, Double maxValue) {
      double progress = Double.NaN;
      Measure error = checkResult.getError();
      if (error != null) {
         progress = error.getAmount();
         this.missingLowerBoundLabel.setVisible(progress < 0.0);
         this.missingUpperBoundLabel.setVisible(progress > 1.0);
      } else {
         this.missingLowerBoundLabel.setVisible(minValue != null && checkedValue.getAmount() < minValue);
         this.missingUpperBoundLabel.setVisible(maxValue != null && checkedValue.getAmount() > maxValue);
         if (minValue != null && maxValue != null) {
            progress = (checkedValue.getAmount() - minValue) / (maxValue - minValue);
         }
      }

      if (Double.isNaN(progress)) {
         this.goalProgressBar.setDisable(true);
         this.goalProgressBar.setProgress(0.0);
      } else {
         this.goalProgressBar.setDisable(false);
         this.goalProgressBar.setProgress(Math.max(0.0, Math.min(1.0, progress)));
         switch (checkStatus) {
            case FAILED:
               JavaFxUtil.replaceStyles(this.goalProgressBar.getStyleClass(), "goal-progress-failed", "goal-progress-missing", "goal-progress-passed");
               break;
            case PASSED:
               JavaFxUtil.replaceStyles(this.goalProgressBar.getStyleClass(), "goal-progress-passed", "goal-progress-failed", "goal-progress-missing");
               break;
            default:
               JavaFxUtil.replaceStyles(this.goalProgressBar.getStyleClass(), "goal-progress-missing", "goal-progress-failed", "goal-progress-passed");
         }
      }
   }
}
