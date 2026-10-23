package com.cropster.roastingintelligence.ui.chart;

import com.cropster.roastingintelligence.ui.components.HintsButton;
import com.cropster.roastingintelligence.ui.components.HintsFooterElement;
import com.cropster.roastingintelligence.ui.components.LineBreak;
import com.cropster.roastingintelligence.ui.util.Icon;
import javafx.event.ActionEvent;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.text.Text;
import org.controlsfx.control.PopOver.ArrowLocation;
import org.cropster.roastingintelligence.ui.chart.CurveChart;

public class BetweenBatchChartWrapper extends ChartWrapper {
   private final HintsButton hintsButton = new HintsButton();
   private final Button moreButton = new Button(null, Icon.SEE_MORE.load());
   private final MenuItem restartMenuItem = new MenuItem(org.cropster.roastingintelligence.util.Messages.m_("Restart recording"), Icon.RESET.load());
   private final MenuItem stopMenuItem = new MenuItem(org.cropster.roastingintelligence.util.Messages.m_("Stop recording"), Icon.STOP.load());

   @Override
   public void init(CurveChart curveChart) {
      super.init(curveChart);
      Label footerTitle = new Label(org.cropster.roastingintelligence.util.Messages.m_("Actions, usage and keyboard shortcuts"));
      footerTitle.getStyleClass().add("hints-footer-title");
      this.hintsButton.setArrowLocation(ArrowLocation.LEFT_TOP);
      this.hintsButton
         .getContent()
         .addAll(
            new Node[]{
               new Text(
                  org.cropster.roastingintelligence.util.Messages.m_(
                     "Use the available actions to control the recording of your protocol in preparation for the following roast."
                  )
               ),
               new LineBreak(),
               new LineBreak(),
               new Text(
                  org.cropster.roastingintelligence.util.Messages.m_(
                     "Since only up to {0} minutes of between batch data can be saved, you can restart the recording from scratch at any time.", 15L
                  )
               )
            }
         );
      this.hintsButton
         .getFooter()
         .addAll(
            new Node[]{
               footerTitle,
               new HintsFooterElement(org.cropster.roastingintelligence.util.Messages.m_("More actions (button)"), null, null, Icon.SEE_MORE.load("icon--link"))
            }
         );
      this.addToRightToolbarSide(this.hintsButton);
      this.moreButton.getStyleClass().add("button--small-more");
      this.moreButton.setContextMenu(new ContextMenu(new MenuItem[]{this.stopMenuItem, this.restartMenuItem}));
      this.moreButton.setOnAction(this::showContextMenu);
      this.addToLeftToolbarSide(this.moreButton);
   }

   private void showContextMenu(ActionEvent e) {
      this.moreButton.getContextMenu().show(this.moreButton, Side.BOTTOM, 0.0, 0.0);
   }

   public HintsButton getHintsButton() {
      return this.hintsButton;
   }

   public Button getMoreButton() {
      return this.moreButton;
   }

   public MenuItem getRestartMenuItem() {
      return this.restartMenuItem;
   }

   public MenuItem getStopMenuItem() {
      return this.stopMenuItem;
   }
}
