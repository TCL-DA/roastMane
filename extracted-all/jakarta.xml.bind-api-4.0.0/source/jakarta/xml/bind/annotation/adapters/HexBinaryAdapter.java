package jakarta.xml.bind.annotation.adapters;

import jakarta.xml.bind.DatatypeConverter;

public final class HexBinaryAdapter extends XmlAdapter<String, byte[]> {
   public byte[] unmarshal(String s) {
      return s == null ? null : DatatypeConverter.parseHexBinary(s);
   }

   public String marshal(byte[] bytes) {
      return bytes == null ? null : DatatypeConverter.printHexBinary(bytes);
   }
}
