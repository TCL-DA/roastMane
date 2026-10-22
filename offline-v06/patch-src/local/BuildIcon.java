package local;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.jar.JarFile;

public final class BuildIcon {
 public static void main(String[] args)throws Exception{
  if(args.length!=2)throw new IllegalArgumentException("Usage: BuildIcon <patched.jar> <output.ico>");
  int[] sizes={16,32,48,64,128,256};List<byte[]> images=new ArrayList<>();
  try(JarFile jar=new JarFile(args[0])){for(int size:sizes)images.add(jar.getInputStream(jar.getJarEntry("images/roastmane"+size+"x"+size+".png")).readAllBytes());}
  try(DataOutputStream out=new DataOutputStream(Files.newOutputStream(Path.of(args[1])))){
   write16(out,0);write16(out,1);write16(out,images.size());int offset=6+16*images.size();
   for(int i=0;i<images.size();i++){int size=sizes[i];byte[] png=images.get(i);out.writeByte(size>=256?0:size);out.writeByte(size>=256?0:size);out.writeByte(0);out.writeByte(0);write16(out,1);write16(out,32);write32(out,png.length);write32(out,offset);offset+=png.length;}
   for(byte[] png:images)out.write(png);
  }
 }
 static void write16(DataOutputStream out,int value)throws IOException{out.writeByte(value&255);out.writeByte((value>>>8)&255);}
 static void write32(DataOutputStream out,int value)throws IOException{write16(out,value&65535);write16(out,(value>>>16)&65535);}
}
