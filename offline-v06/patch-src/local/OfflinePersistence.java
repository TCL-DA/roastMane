package local;
import java.io.*;
import java.nio.file.*;
import java.nio.channels.FileChannel;
import java.util.*;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.remote.Version;
import org.cropster.roastingintelligence.service.impl.FilePersister;
import org.cropster.roastingintelligence.service.impl.JsonWriter;
public final class OfflinePersistence {
 private static final Map<Roast,Path> saved=Collections.synchronizedMap(new WeakHashMap<>());
 private static Object field(Object obj,String n)throws ReflectiveOperationException{var f=FilePersister.class.getDeclaredField(n);f.setAccessible(true);return f.get(obj);}
 @SuppressWarnings("unchecked") public static File save(FilePersister p,Roast roast,boolean fresh)throws IOException{
  synchronized(p){try{
   Map<Roast,File> files=(Map<Roast,File>)field(p,"_filesMap");
   if(fresh&&files.containsKey(roast))throw new IllegalArgumentException("Existing roast");
   if(!fresh&&!files.containsKey(roast))throw new IllegalArgumentException("New roast");
   // Cấp ID bền vững và giữ nguyên đường cong đầy đủ; không đánh dấu đã tải lên.
   Path probe=Files.createTempFile(((File)field(p,"_outboxDir")).toPath(),"write-check-",".tmp");Files.delete(probe);
   OfflineConnection connection=new OfflineConnection(new Version("5.13"),java.net.URI.create("https://offline.invalid"));
   if(roast.getLot().getId()==null)connection.startRoast(roast);
   File target=fresh?new File((File)field(p,"_outboxDir"),String.format(Locale.ROOT,"%1$tY%1$tm%1$td_%1$tH%1$tM%1$tS_%1$tL.json",roast.getStartDate(),roast.getLot().getId())):files.get(roast);
   if(fresh&&Files.exists(target.toPath()))throw new IOException("A batch with the same timestamp already exists; refusing to overwrite it");
   Path tmp=Files.createTempFile(target.toPath().getParent(),"local-roast-",".tmp");
   try{
    try(var writer=Files.newBufferedWriter(tmp)){((JsonWriter)field(p,"_jsonWriter")).write(writer,roast);}
    try(var channel=FileChannel.open(tmp,StandardOpenOption.WRITE)){channel.force(true);}
    try{Files.move(tmp,target.toPath(),StandardCopyOption.ATOMIC_MOVE,StandardCopyOption.REPLACE_EXISTING);}catch(AtomicMoveNotSupportedException e){Files.move(tmp,target.toPath(),StandardCopyOption.REPLACE_EXISTING);}
   }finally{Files.deleteIfExists(tmp);}
   files.put(roast,target);saved.put(roast,target.toPath());
   if(roast.getEndDate()!=null)connection.finishRoast(roast);else connection.update(roast);
   return target;
  }catch(ReflectiveOperationException e){throw new IOException("Unable to access local persister",e);}}
 }
 public static void verifySaved(Roast roast){Path p=saved.get(roast);try{if(p==null||!Files.isRegularFile(p)||Files.size(p)==0)throw new IllegalStateException("Batch was not saved locally. Preserve the displayed data before closing.");}catch(IOException e){throw new IllegalStateException("Cannot verify saved batch",e);}}
}
