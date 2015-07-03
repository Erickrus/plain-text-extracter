package org.extractor.text;

import org.extractor.util.GsonHelper;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ExtracterParam {
	private String baseFolder = "";
	private String fileList = "";
	private String visitorFactoryFilename = "";
	
	public String getBaseFolder() {
		return baseFolder;
	}
	public void setBaseFolder(String baseFolder) {
		this.baseFolder = baseFolder;
	}
	public String getFileList() {
		return fileList;
	}
	public void setFileList(String fileList) {
		this.fileList = fileList;
	}
	public String getVisitorFactoryFilename() {
		return visitorFactoryFilename;
	}
	public void setVisitorFactoryFilename(String visitorFactoryFilename) {
		this.visitorFactoryFilename = visitorFactoryFilename;
	}
	
	public ExtracterParam(String jsonLine) {
		JsonObject jobject = (new JsonParser().parse(jsonLine)).getAsJsonObject();
		baseFolder = GsonHelper.getString(jobject, "baseFolder");
		fileList = GsonHelper.getString(jobject, "fileList");
		visitorFactoryFilename = GsonHelper.getString(jobject, "visitorFactory");
	}
}
