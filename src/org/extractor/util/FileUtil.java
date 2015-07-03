package org.extractor.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class FileUtil {
	
	public static String toBaseUri(String uri) {
		return uri.substring(0,uri.lastIndexOf("/")+1);
	}
	
	public static String toUrl(String baseFolder, String filename) {
		String result ="";
		String uriPart = "";
		if (!baseFolder.endsWith("\\")) {
			baseFolder += "\\";
		}
		
		if ( filename.toLowerCase().indexOf(baseFolder.toLowerCase()) == 0 ) {
			uriPart = filename.substring(baseFolder.length());
			uriPart = uriPart.replaceAll("%22","\"")
					.replaceAll("%7c","|")
					.replaceAll("%3c","<")
					.replaceAll("%3e",">")
					.replaceAll("%3a",":")
					.replaceAll("%2a","*")
					.replaceAll("%3f","?")
					.replaceAll("[\\\\]","/");
			if (uriPart.startsWith("ftps/")) {
				uriPart = "ftps://"+ uriPart.substring(5);
			} else if (uriPart.startsWith("ftp/")) {
				uriPart = "ftp://"+ uriPart.substring(4);
			} else if (uriPart.startsWith("http/")) {
				uriPart = "http://"+ uriPart.substring(5);
			} else if (uriPart.startsWith("https/")) {
				uriPart = "https://"+ uriPart.substring(6);
			}
			result = uriPart;
		}
		return result;
	}
	
	public static String toFilename(String baseFolder, String url) {

		String result = "/" + url;
		
		//removing all bad character for the file system
		result = result.replaceAll("https://", "https/")
		.replaceAll("http://", "http/")
		.replaceAll("ftp://", "ftp/")
		.replaceAll("ftps://", "ftps/")
		
		.replaceAll("[\"]", "%22")
		.replaceAll("[|]", "%7c")
		.replaceAll("[<]", "%3c")
		.replaceAll("[>]", "%3e")
		.replaceAll("[:]", "%3a")
		.replaceAll("[\\*]", "%2a")
		.replaceAll("[\\?]", "%3f")
		.replaceAll("[\\/]", "\\\\")
		.replaceAll("\\/\\/", "\\\\")
		;
		
		//append a virtual start up page
		if (result.endsWith("\\")) {
			result += "index.html";
		}
		
		//assemble folder with url
		result = baseFolder+result;
		result = result.replaceAll("\\\\\\\\", "\\\\");
		
		return result;
	}
	
	public static void checkAndCreateFolder(String filename) {
		String fullPath = filename.substring(0,filename.lastIndexOf("\\"));
		String [] paths = fullPath.split("\\\\"); 
		String intermediatePath = paths[0];
		for (int i=1;i<paths.length;i++) {
			intermediatePath += "\\"+paths[i];
			File pathfile = new File(intermediatePath);
			if (!pathfile.exists()) {
				pathfile.mkdir();
			}
		}
	}
	
	public static void writeFile(String filename, byte [] content) {
    	
    	FileOutputStream fos;
		try {
			fos = new FileOutputStream(new File(filename));
	    	fos.write(content);
	    	fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	public static void writeFile(String filename, String content) {
		PrintWriter out;
		try {
			out = new PrintWriter(filename, "GB2312");
			out.print(content);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String readFile(String filename) {
		String result = "";
		try {
			//input = new FileReader(filename);
			//BufferedReader br = new BufferedReader(input);
			BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(filename),"GBK"));   
			String aLineOfFile = "";

			while ((aLineOfFile = br.readLine()) != null) {
				if (result.length() > 0) {
					result += "\r\n" + aLineOfFile;
				} else {
					result = aLineOfFile;
				}
			}
			br.close();
			//input.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    return result;
	}
}
