package com.cropster.roastingintelligence.ui.components;

import org.cropster.roastingintelligence.model.Analysis;

public class LinkToPhysicalLabel extends AbstractLinkLabel<Analysis> {
   public void init(Analysis analysis) {
      this.setText(analysis.getIdTag());
      this.path = String.format("/r/physical/%s", analysis.getPhysical().getId());
   }
}
