package local;
import org.objectweb.asm.*;
import java.nio.file.*;
import java.util.*;
import java.util.jar.*;
public final class PatchJar implements Opcodes {
 static String app="org/cropster/roastingintelligence/RoastingIntelligenceApp",ui="com/cropster/roastingintelligence/ui/JavaFxController";
 static boolean stub(String owner,String name){
  return (owner.equals(app)&&Set.of("setupSegment","checkForUpdate").contains(name)) ||
   (owner.equals(ui)&&Set.of("startPeriodicUpdateCheck","initSegment","syncAll","logout").contains(name)) ||
   (owner.equals("org/cropster/roastingintelligence/VersionChecker")&&name.equals("run")) ||
   (owner.equals("org/cropster/roastingintelligence/SyncService")&&Set.of("uploadRoasts","uploadRoastsBlocking").contains(name));
 }
 public static void main(String[]args)throws Exception{
  try(JarFile in=new JarFile(args[0]);JarOutputStream out=new JarOutputStream(Files.newOutputStream(Path.of(args[1])))){
   var entries=in.entries();int changed=0;
   while(entries.hasMoreElements()){
    JarEntry e=entries.nextElement();byte[] bytes=in.getInputStream(e).readAllBytes();String owner=e.getName().replaceAll("\\.class$","");
    if(e.getName().endsWith(".class")&&(owner.equals(app)||owner.equals(ui)||owner.equals("org/cropster/roastingintelligence/SyncService")||owner.equals("org/cropster/roastingintelligence/VersionChecker")||owner.equals("org/cropster/roastingintelligence/service/impl/FilePersister")||owner.equals("org/cropster/roastingintelligence/RoastingManager")||owner.equals("com/cropster/roastingintelligence/ui/layout/PreparationPanel")||owner.equals("com/cropster/roastingintelligence/service/impl/StorageServiceImpl")||owner.equals("com/cropster/roastingintelligence/config/FileSystemConfiguration")||owner.equals("com/cropster/roastingintelligence/ui/layout/ProductionSidebar")||owner.equals("com/cropster/roastingintelligence/ui/components/ProductionEditor"))){
     ClassReader cr=new ClassReader(bytes);ClassWriter cw=new ClassWriter(ClassWriter.COMPUTE_MAXS);
     cr.accept(new ClassVisitor(ASM9,cw){
      public MethodVisitor visitMethod(int access,String n,String d,String sig,String[]ex){
       MethodVisitor mv=super.visitMethod(access,n,d,sig,ex);
       if(owner.equals("org/cropster/roastingintelligence/service/impl/FilePersister")&&n.equals("getFilesToUpload")){mv.visitCode();mv.visitInsn(ICONST_0);mv.visitTypeInsn(ANEWARRAY,"java/io/File");mv.visitInsn(ARETURN);mv.visitMaxs(1,2);mv.visitEnd();return null;}

       if(owner.equals("com/cropster/roastingintelligence/service/impl/StorageServiceImpl")&&n.equals("loadDataCache")){mv.visitCode();mv.visitMethodInsn(INVOKESTATIC,"local/OfflineBootstrap","data","()Lcom/cropster/roastingintelligence/Data;",false);mv.visitInsn(ARETURN);mv.visitMaxs(1,1);mv.visitEnd();return null;}
       if(owner.equals("com/cropster/roastingintelligence/config/FileSystemConfiguration")&&n.equals("directories")){mv.visitCode();mv.visitMethodInsn(INVOKESTATIC,"local/OfflineBootstrap","directories","()Lcom/cropster/roastingintelligence/config/FileSystemConfiguration$Directories;",false);mv.visitInsn(ARETURN);mv.visitMaxs(1,3);mv.visitEnd();return null;}

       if(owner.equals("org/cropster/roastingintelligence/service/impl/FilePersister")&&(n.equals("saveNew")||n.equals("save"))&&d.equals("(Lorg/cropster/roastingintelligence/model/Roast;)Ljava/io/File;")){
        mv.visitCode();mv.visitVarInsn(ALOAD,0);mv.visitVarInsn(ALOAD,1);mv.visitInsn(n.equals("saveNew")?ICONST_1:ICONST_0);mv.visitMethodInsn(INVOKESTATIC,"local/OfflinePersistence","save","(Lorg/cropster/roastingintelligence/service/impl/FilePersister;Lorg/cropster/roastingintelligence/model/Roast;Z)Ljava/io/File;",false);mv.visitInsn(ARETURN);mv.visitMaxs(3,2);mv.visitEnd();return null;
       }
       if(stub(owner,n)&&Type.getReturnType(d).equals(Type.VOID_TYPE)){mv.visitCode();mv.visitInsn(RETURN);mv.visitMaxs(0,0);mv.visitEnd();return null;}
       if(owner.equals(ui)&&n.equals("start")&&d.equals("(Ljava/util/List;)V")){mv.visitCode();mv.visitVarInsn(ALOAD,0);mv.visitVarInsn(ALOAD,1);mv.visitFieldInsn(PUTFIELD,ui,"setupWarnings","Ljava/util/List;");mv.visitVarInsn(ALOAD,0);mv.visitMethodInsn(INVOKESTATIC,"local/OfflineBootstrap","start","(L"+ui+";)V",false);mv.visitInsn(RETURN);mv.visitMaxs(1,2);mv.visitEnd();return null;}
       return new MethodVisitor(ASM9,mv){
        public void visitInsn(int op){if(owner.equals("org/cropster/roastingintelligence/RoastingManager")&&n.equals("finalizeRoasting")&&op==ARETURN){super.visitInsn(DUP);super.visitMethodInsn(INVOKESTATIC,"local/OfflinePersistence","verifySaved","(Lorg/cropster/roastingintelligence/model/Roast;)V",false);}super.visitInsn(op);}
        public void visitCode(){super.visitCode();if(owner.equals(app)&&n.equals("setupSourceFactories")){super.visitVarInsn(ALOAD,0);super.visitFieldInsn(GETFIELD,app,"_appModel","Lorg/cropster/roastingintelligence/AppModel;");super.visitMethodInsn(INVOKESTATIC,"local/OfflineBootstrap","configure","(Lorg/cropster/roastingintelligence/AppModel;)V",false);}}
        public void visitTypeInsn(int op,String t){super.visitTypeInsn(op,owner.equals(app)&&t.equals("org/cropster/roastingintelligence/remote/CsarConnectionImpl")?"local/OfflineConnection":t);}
        public void visitMethodInsn(int op,String o,String m,String desc,boolean itf){
         if(o.equals("org/cropster/roastingintelligence/model/Roast")&&m.equals("isSynced")&&((owner.equals(ui)&&n.equals("updateFinalValues"))||owner.endsWith("/ProductionSidebar")||owner.endsWith("/ProductionEditor"))){super.visitMethodInsn(INVOKESTATIC,"local/OfflineBootstrap","locallySaved","(Lorg/cropster/roastingintelligence/model/Roast;)Z",false);return;}

         if(owner.equals("com/cropster/roastingintelligence/ui/layout/PreparationPanel")&&n.equals("getLotComponents")&&m.equals("lotWeightOrZero")&&desc.equals("(Lorg/cropster/roastingintelligence/model/Lot;Lcom/cropster/util/model/Unit;)D")){super.visitMethodInsn(INVOKESTATIC,"local/OfflineBootstrap","availableForBatch","(Ljava/lang/Object;Lorg/cropster/roastingintelligence/model/Lot;Lcom/cropster/util/model/Unit;)D",false);return;}
if(owner.equals(ui)&&o.equals("org/cropster/roastingintelligence/util/BoolMap")&&m.equals("getOr")){super.visitMethodInsn(INVOKESTATIC,"local/OfflineBootstrap","any","(Ljava/util/Map;)Z",false);return;}
         if(owner.equals(ui)&&n.equals("finishRoasting")&&o.equals("org/cropster/roastingintelligence/model/Lot")&&m.equals("getWeight")){super.visitMethodInsn(INVOKESTATIC,"local/OfflineBootstrap","inventoryWeight","(Lorg/cropster/roastingintelligence/model/Lot;)Lorg/cropster/roastingintelligence/model/Measure;",false);return;}
         if(owner.equals(ui)&&n.equals("finishRoasting")&&o.equals("org/cropster/roastingintelligence/model/Lot")&&m.equals("setWeight")){super.visitMethodInsn(INVOKESTATIC,"local/OfflineBootstrap","setInventoryWeight","(Lorg/cropster/roastingintelligence/model/Lot;Lorg/cropster/roastingintelligence/model/Measure;)V",false);return;}
         if(owner.equals(app)&&o.equals("org/cropster/roastingintelligence/remote/CsarConnectionImpl")&&m.equals("<init>"))o="local/OfflineConnection";super.visitMethodInsn(op,o,m,desc,itf);}
       };
      }
     },0);bytes=cw.toByteArray();changed++;
    }
    out.putNextEntry(new JarEntry(e.getName()));out.write(bytes);out.closeEntry();
   }System.out.println("Patched classes: "+changed);
  }
 }
}
