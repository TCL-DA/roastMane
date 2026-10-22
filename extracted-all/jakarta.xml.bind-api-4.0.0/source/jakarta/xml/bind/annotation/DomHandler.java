package jakarta.xml.bind.annotation;

import jakarta.xml.bind.ValidationEventHandler;
import javax.xml.transform.Result;
import javax.xml.transform.Source;

public interface DomHandler<ElementT, ResultT extends Result> {
   ResultT createUnmarshaller(ValidationEventHandler var1);

   ElementT getElement(ResultT var1);

   Source marshal(ElementT var1, ValidationEventHandler var2);
}
