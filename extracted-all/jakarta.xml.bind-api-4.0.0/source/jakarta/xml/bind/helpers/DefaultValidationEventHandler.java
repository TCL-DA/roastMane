package jakarta.xml.bind.helpers;

import jakarta.xml.bind.ValidationEvent;
import jakarta.xml.bind.ValidationEventHandler;
import jakarta.xml.bind.ValidationEventLocator;
import java.net.URL;
import org.w3c.dom.Node;

public class DefaultValidationEventHandler implements ValidationEventHandler {
   @Override
   public boolean handleEvent(ValidationEvent event) {
      if (event == null) {
         throw new IllegalArgumentException();
      } else {
         String severity = null;
         boolean retVal = false;
         switch (event.getSeverity()) {
            case 0:
               severity = Messages.format("DefaultValidationEventHandler.Warning");
               retVal = true;
               break;
            case 1:
               severity = Messages.format("DefaultValidationEventHandler.Error");
               retVal = false;
               break;
            case 2:
               severity = Messages.format("DefaultValidationEventHandler.FatalError");
               retVal = false;
               break;
            default:
               assert false : Messages.format("DefaultValidationEventHandler.UnrecognizedSeverity", event.getSeverity());
         }

         String location = this.getLocation(event);
         System.out.println(Messages.format("DefaultValidationEventHandler.SeverityMessage", severity, event.getMessage(), location));
         return retVal;
      }
   }

   private String getLocation(ValidationEvent event) {
      StringBuilder msg = new StringBuilder();
      ValidationEventLocator locator = event.getLocator();
      if (locator != null) {
         URL url = locator.getURL();
         Object obj = locator.getObject();
         Node node = locator.getNode();
         int line = locator.getLineNumber();
         if (url != null || line != -1) {
            msg.append("line ").append(line);
            if (url != null) {
               msg.append(" of ").append(url);
            }
         } else if (obj != null) {
            msg.append(" obj: ").append(obj.toString());
         } else if (node != null) {
            msg.append(" node: ").append(node.toString());
         }
      } else {
         msg.append(Messages.format("DefaultValidationEventHandler.LocationUnavailable"));
      }

      return msg.toString();
   }
}
