package nonapi.io.github.classgraph.json;

class JSONReference {
   Object idObject;

   public JSONReference(Object idObject) {
      if (idObject == null) {
         throw new IllegalArgumentException("idObject cannot be null");
      } else {
         this.idObject = idObject;
      }
   }
}
