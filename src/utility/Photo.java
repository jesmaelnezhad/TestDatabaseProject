/**
 * 
 */
package utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.Part;


/**
 * @author jam
 *
 */
public class Photo {
	
	
	public static void savePhoto(String group, int customerId, Part photoFile, ServletContext context) throws IOException {
		String photoName = photoFile.getName();
		InputStream photoContent = photoFile.getInputStream();
	    byte[] buffer = new byte[photoContent.available()];
	    photoContent.read(buffer);
	    
//	    response.setContentType("text/html");
//	    response.setCharacterEncoding("UTF-8");
//	    PrintWriter out = response.getWriter();
//	    out.print("title : " + title + " content : " + newsContent);
	    String contextPath = context.getRealPath(File.separator);
	    File userImagesDir = new File(contextPath + "/"+group+"_images");
	    if(! userImagesDir.exists()) {
	    	userImagesDir.mkdir();
	    }
	    
	    File targetFile = new File(userImagesDir + "/" + customerId + "_" + photoName);
	    OutputStream outStream = new FileOutputStream(targetFile);
	    outStream.write(buffer);
	}
	
	public static String getPhotoPath(String group, int id, String photoName) {
		return "./"+group+"_images/" + "/" + id + "_" + photoName;
	}
}
