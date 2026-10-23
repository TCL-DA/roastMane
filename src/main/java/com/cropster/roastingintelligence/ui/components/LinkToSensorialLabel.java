package com.cropster.roastingintelligence.ui.components;

import org.cropster.roastingintelligence.model.Analysis;

public class LinkToSensorialLabel extends AbstractLinkLabel<Analysis> {
   public void init(Analysis analysis) {
      this.setText(analysis.getIdTag());
      this.path = String.format("/r/sensorial/%s", analysis.getId());
   }
}
