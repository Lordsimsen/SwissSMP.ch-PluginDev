package ch.swisssmp.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

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
			copyDirectory(source, target, new ArrayList<String>());
		}
		public static void copyDirectory(File source, File target, ArrayList<String> ignore){
		    try {
		    	if(ignore==null){
		    		ignore = new ArrayList<String>();
		    	}
		        if(!ignore.contains(source.getName())) {
		            if(source.isDirectory()) {
		                if(!target.exists())
		                target.mkdirs();
		                String files[] = source.list();
		                for (String file : files) {
		                    File srcFile = new File(source, file);
		                    File destFile = new File(target, file);
		                    copyDirectory(srcFile, destFile, ignore);
		                }
		            } else {
		                InputStream in = new FileInputStream(source);
		                OutputStream out = new FileOutputStream(target);
		                byte[] buffer = new byte[1024];
		                int length;
		                while ((length = in.read(buffer)) > 0)
		                    out.write(buffer, 0, length);
		                in.close();
		                out.close();
		            }
		        }
		    } catch (IOException e) {
		    	e.printStackTrace();
		    }
		}
}
