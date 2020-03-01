package ch.swisssmp.utils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;

public class FileUtil {
	public static boolean deleteRecursive(File path) {
	      if(path.exists()) {
	          File files[] = path.listFiles();
	          for(int i=0; i<files.length; i++) {
	              if(files[i].isDirectory()) {
	            	  deleteRecursive(files[i]);
	              } else {
	            	  try{
	            		  files[i].delete();
	            	  }
	            	  catch(Exception e){
	            		  e.printStackTrace();
	            	  }
	              }
	          }
	      }
	      return(path.delete());
		}
		public static void copyDirectory(File source, File target){
			copyDirectory(source, target, null);
		}
		public static void copyDirectory(File source, File target, Collection<String> ignoreFiles){
			try {
				if(ignoreFiles==null){
					FileUtils.copyDirectory(source, target);
				}
				else{
					FileUtils.copyDirectory(source, target, (File file)->{
						return !ignoreFiles.contains(file.getName());
					});
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
}
