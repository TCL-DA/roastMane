package local;

import org.objectweb.asm.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.jar.*;

/** Chỉ thay nhận diện hiển thị trên JAR đã được vá chức năng offline. */
public final class PatchJar implements Opcodes {
 static final Set<String> OLD_LOGOS=Set.of("images/cropster-logo.svg","images/cropster-branding.png","icons/logo-cropster.svg","icons/logo-cropster-color.svg");
 static boolean legalNotice(String value){String lower=value.toLowerCase(Locale.ROOT);return lower.contains("copyright")||lower.contains("license")||lower.contains("licensed under")||lower.contains("trademark");}
 static String brand(String value){
  if(value==null||legalNotice(value))return value;
  if(value.contains("support@cropster.com"))return "Online support is unavailable in this offline build.";
  return value.replace("/images/cropster-logo.svg","/images/roastmane-logo.svg").replace("/images/cropster-branding.png","/images/roastmane-branding.png").replace("/images/cropster%sx%s.png","/images/roastmane%sx%s.png").replace("/icons/logo-cropster-color.svg","/icons/logo-roastmane-color.svg").replace("/icons/logo-cropster.svg","/icons/logo-roastmane.svg").replace("Cropster Roasting Intelligence","roastMane").replace("Roasting Intelligence","roastMane").replace("Roasting intelligence","roastMane").replace("Cropster's","roastMane").replace("Cropster","roastMane");
 }
 static String pathElement(String svg){int start=svg.indexOf("<path ");int end=start<0?-1:svg.indexOf("/>",start);if(start<0||end<0)throw new IllegalArgumentException("Approved SVG has no self-closing path element");return svg.substring(start,end+2);}
 static byte[] wordmark(byte[] approvedSvg){String source=new String(approvedSvg,StandardCharsets.ISO_8859_1);String path=pathElement(source);String svg="<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"298\" height=\"50\" viewBox=\"0 0 1788 300\">"+path+"<text x=\"360\" y=\"215\" font-family=\"Arial,sans-serif\" font-size=\"180\" font-weight=\"700\" fill=\"#254C3B\">roastMane</text></svg>";return svg.getBytes(StandardCharsets.UTF_8);}
 static byte[] png(BufferedImage source,int size)throws Exception{if(size==1024){ByteArrayOutputStream exact=new ByteArrayOutputStream();ImageIO.write(source,"png",exact);return exact.toByteArray();}BufferedImage image=new BufferedImage(size,size,BufferedImage.TYPE_INT_ARGB);Graphics2D g=image.createGraphics();g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);g.drawImage(source,0,0,size,size,null);g.dispose();ByteArrayOutputStream out=new ByteArrayOutputStream();ImageIO.write(image,"png",out);return out.toByteArray();}
 static boolean textResource(String name){String lower=name.toLowerCase(Locale.ROOT);return lower.endsWith(".fxml")||lower.endsWith(".css")||lower.endsWith(".properties")||lower.endsWith(".yaml")||lower.endsWith(".xml");}
 static void put(JarOutputStream out,String name,byte[] bytes)throws Exception{out.putNextEntry(new JarEntry(name));out.write(bytes);out.closeEntry();}
 static byte[] patchClass(byte[] bytes){ClassReader cr=new ClassReader(bytes);ClassWriter cw=new ClassWriter(0);cr.accept(new ClassVisitor(ASM9,cw){public FieldVisitor visitField(int access,String n,String d,String sig,Object value){return super.visitField(access,n,d,sig,value instanceof String?brand((String)value):value);}public MethodVisitor visitMethod(int access,String n,String d,String sig,String[]ex){MethodVisitor mv=super.visitMethod(access,n,d,sig,ex);return new MethodVisitor(ASM9,mv){public void visitLdcInsn(Object value){super.visitLdcInsn(value instanceof String?brand((String)value):value);}};}},0);return cw.toByteArray();}
 public static void main(String[]args)throws Exception{
  if(args.length!=3)throw new IllegalArgumentException("Usage: PatchJar <offline-patched-input.jar> <output.jar> <brand-kit-dir>");
  Path exports=Path.of(args[2],"exports");Path greenSvg=exports.resolve("monogram_xanh.svg"),avatar=exports.resolve("avatar_xanh_1024.png");
  if(!Files.isRegularFile(greenSvg)||!Files.isRegularFile(avatar))throw new IllegalArgumentException("Approved v06 brand assets are missing from "+exports);
  byte[] mark=Files.readAllBytes(greenSvg),wordmark=wordmark(mark),avatarBytes=Files.readAllBytes(avatar);BufferedImage avatarImage=ImageIO.read(avatar.toFile());if(avatarImage==null||avatarImage.getWidth()!=1024||avatarImage.getHeight()!=1024)throw new IllegalArgumentException("Approved avatar must be a 1024x1024 PNG");
  try(JarFile in=new JarFile(args[0]);JarOutputStream out=new JarOutputStream(Files.newOutputStream(Path.of(args[1])))){
   var entries=in.entries();int changed=0;
   while(entries.hasMoreElements()){JarEntry e=entries.nextElement();String name=e.getName();byte[] bytes=in.getInputStream(e).readAllBytes();if(OLD_LOGOS.contains(name)||name.matches("images/cropster\\d+x\\d+(-mac)?\\.png"))continue;if(name.endsWith(".class")){bytes=patchClass(bytes);changed++;}else if(textResource(name)&&!legalNotice(name)){bytes=brand(new String(bytes,StandardCharsets.UTF_8)).getBytes(StandardCharsets.UTF_8);}put(out,name,bytes);}
   byte[] branding=png(avatarImage,30);put(out,"images/roastmane-logo.svg",wordmark);put(out,"images/roastmane-branding.png",branding);put(out,"icons/logo-roastmane.svg",mark);put(out,"icons/logo-roastmane-color.svg",mark);
   // Giữ bí danh tài nguyên cũ vì enum Icon tạo đường dẫn động từ tên hằng nhị phân.
   put(out,"images/cropster-logo.svg",wordmark);put(out,"images/cropster-branding.png",branding);put(out,"icons/logo-cropster.svg",mark);put(out,"icons/logo-cropster-color.svg",mark);
   for(int size:new int[]{16,32,48,64,96,128,256,512,1024}){byte[] image=size==1024?avatarBytes:png(avatarImage,size);put(out,"images/roastmane"+size+"x"+size+".png",image);put(out,"images/roastmane"+size+"x"+size+"-mac.png",image);put(out,"images/cropster"+size+"x"+size+".png",image);put(out,"images/cropster"+size+"x"+size+"-mac.png",image);}
   put(out,"brand-kit/monogram_xanh.svg",mark);put(out,"brand-kit/avatar_xanh_1024.png",avatarBytes);put(out,"brand-kit/palette.properties","xanh=#254C3B\nbe=#EAE2D2\nden=#191D19\nnau=#785440\ntrang=#FFFFFF\nxam=#737A70\n".getBytes(StandardCharsets.UTF_8));
   System.out.println("Rebranded classes with approved v06 monogram: "+changed);
  }
 }
}
