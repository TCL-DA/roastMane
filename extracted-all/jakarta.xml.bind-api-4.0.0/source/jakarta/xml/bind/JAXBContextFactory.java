package jakarta.xml.bind;

import java.util.Map;

public interface JAXBContextFactory {
   JAXBContext createContext(Class<?>[] var1, Map<String, ?> var2) throws JAXBException;

   JAXBContext createContext(String var1, ClassLoader var2, Map<String, ?> var3) throws JAXBException;
}
