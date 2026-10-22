package org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "methodDoc",
   propOrder = {}
)
public class MethodDocType {
   private String methodName;
   @XmlElement(
      required = false,
      nillable = false
   )
   protected String methodSignature;
   protected String commentText;
   private String returnDoc;
   private String returnTypeExample;
   private RequestDocType requestDoc;
   private ResponseDocType responseDoc;
   @XmlElementWrapper(
      name = "paramDocs"
   )
   protected List<ParamDocType> paramDoc;
   @XmlAnyElement(
      lax = true
   )
   private List<Object> any;

   public List<ParamDocType> getParamDocs() {
      if (this.paramDoc == null) {
         this.paramDoc = new ArrayList<>();
      }

      return this.paramDoc;
   }

   public List<Object> getAny() {
      if (this.any == null) {
         this.any = new ArrayList<>();
      }

      return this.any;
   }

   public String getMethodName() {
      return this.methodName;
   }

   public void setMethodName(String methodName) {
      this.methodName = methodName;
   }

   public String getMethodSignature() {
      return this.methodSignature;
   }

   public void setMethodSignature(String methodSignature) {
      this.methodSignature = methodSignature;
   }

   public String getCommentText() {
      return this.commentText;
   }

   public void setCommentText(String value) {
      this.commentText = value;
   }

   public String getReturnDoc() {
      return this.returnDoc;
   }

   public void setReturnDoc(String returnDoc) {
      this.returnDoc = returnDoc;
   }

   public String getReturnTypeExample() {
      return this.returnTypeExample;
   }

   public void setReturnTypeExample(String returnTypeExample) {
      this.returnTypeExample = returnTypeExample;
   }

   public RequestDocType getRequestDoc() {
      return this.requestDoc;
   }

   public void setRequestDoc(RequestDocType requestDoc) {
      this.requestDoc = requestDoc;
   }

   public ResponseDocType getResponseDoc() {
      return this.responseDoc;
   }

   public void setResponseDoc(ResponseDocType responseDoc) {
      this.responseDoc = responseDoc;
   }
}
