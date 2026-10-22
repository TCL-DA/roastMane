package com.cropster.roastingintelligence.ui.whatsnew.pages;

import com.cropster.roastingintelligence.ui.whatsnew.AbstractPage;

public class N09_ToastNotifications extends AbstractPage {
   public N09_ToastNotifications() {
      super(
         org.cropster.roastingintelligence.util.Messages.m_("Ready for timely and clear system notifications?"),
         org.cropster.roastingintelligence.util.Messages.m_(
            "RI always aims to keep its users informed about what is going on. And while most events are relevant, many should be communicated to users without the need for them to react.\n\nThat is why in RI 5 we implemented “In-app notifications”: key event information without disruption."
         )
      );
   }
}
