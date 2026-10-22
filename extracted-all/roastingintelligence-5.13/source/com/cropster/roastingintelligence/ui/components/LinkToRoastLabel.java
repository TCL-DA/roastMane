package com.cropster.roastingintelligence.ui.components;

import org.cropster.roastingintelligence.model.Roast;

public class LinkToRoastLabel extends AbstractLinkLabel<Roast> {
   public void init(Roast roast) {
      this.setText(roast.getLot().getIdTag());
      this.path = String.format("/r/roast/%s", roast.getLot().getId());
   }
}
