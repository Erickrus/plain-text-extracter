package org.extractor.text.visitor;

import java.util.ArrayList;

import org.jsoup.nodes.Element;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.extractor.util.GsonHelper;
import org.extractor.text.NodeFilter;

public class VisitorFactory {
	private String name = "";
	private ArrayList<PlainTextVisitor> visitors = new ArrayList<PlainTextVisitor>();
	private PlainTextVisitor defaultVisitor = new PlainTextVisitor("default");
	
	public VisitorFactory(String json) {
		reload(json);
	}
	
	public VisitorFactory() {
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<PlainTextVisitor> getVisitors() {
		return visitors;
	}
	public void setVisitors(ArrayList<PlainTextVisitor> visitors) {
		this.visitors = visitors;
	}
	
	public void reload(String jsonLine) {
		visitors = new ArrayList<PlainTextVisitor>();
		defaultVisitor = new PlainTextVisitor("default");
		JsonObject joRoot = (new JsonParser().parse(jsonLine)).getAsJsonObject();
		ArrayList<PlainTextVisitor> visitors = new ArrayList<PlainTextVisitor>();
		
		this.setName(GsonHelper.getString(joRoot, "name"));
		JsonArray jaVisitors = joRoot.getAsJsonArray("visitors");
		
		for (int i = 0; i < jaVisitors.size(); i++) {
			JsonObject joVisitor = jaVisitors.get(i).getAsJsonObject();
			PlainTextVisitor hnv = new PlainTextVisitor(GsonHelper.getString(joVisitor, "name"));
			
			JsonArray jaFilters = joVisitor.getAsJsonArray("filters");
			for (int j = 0; j < jaFilters.size(); j++) {
				NodeFilter hnf = new NodeFilter();
				
				JsonObject joFilter = jaFilters.get(j).getAsJsonObject();
				hnf.setAttributeName(GsonHelper.getString(joFilter, "attributeName"));
				hnf.setAttributeValues(GsonHelper.getString(joFilter,"attributeValues"));
				hnf.setNodeName(GsonHelper.getString(joFilter, "nodeName"));
				hnf.setOptional(GsonHelper.getBoolean(joFilter, "optional"));
				
				if (joFilter.has("except")) {
					JsonArray jaExcepts = joFilter.getAsJsonArray("except");
					for (int k=0;k<jaExcepts.size();k++) {
						JsonObject joExcept = jaExcepts.get(k).getAsJsonObject();
						NodeFilter hnf3 = new NodeFilter();
						hnf3.setAttributeName(GsonHelper.getString(joExcept, "attributeName"));
						hnf3.setAttributeValues(GsonHelper.getString(joExcept,"attributeValues"));
						hnf3.setNodeName(GsonHelper.getString(joExcept, "nodeName"));
						hnf3.setOptional(GsonHelper.getBoolean(joExcept, "optional"));
						hnf.addExcept(hnf3);
					}
				}
				
				if (joFilter.has("after")) {
					JsonObject joAfter=  joFilter.getAsJsonObject("after");
					NodeFilter hAfter = new NodeFilter();
					
					hAfter.setAttributeName(GsonHelper.getString(joAfter, "attributeName"));
					hAfter.setAttributeValues(GsonHelper.getString(joAfter,"attributeValues"));
					hAfter.setNodeName(GsonHelper.getString(joAfter, "nodeName"));
					hAfter.setOptional(GsonHelper.getBoolean(joAfter, "optional"));
					hnf.setAfter(hAfter);
				}
				
				if (joFilter.has("before")) {
					JsonObject joBefore=  joFilter.getAsJsonObject("before");
					NodeFilter hBefore = new NodeFilter();
					
					hBefore.setAttributeName(GsonHelper.getString(joBefore, "attributeName"));
					hBefore.setAttributeValues(GsonHelper.getString(joBefore,"attributeValues"));
					hBefore.setNodeName(GsonHelper.getString(joBefore, "nodeName"));
					hBefore.setOptional(GsonHelper.getBoolean(joBefore, "optional"));
					hnf.setBefore(hBefore);
				}
				
				if (joFilter.has("under")) {
					JsonObject joUnder=  joFilter.getAsJsonObject("under");
					NodeFilter hUnder = new NodeFilter();
					
					hUnder.setAttributeName(GsonHelper.getString(joUnder, "attributeName"));
					hUnder.setAttributeValues(GsonHelper.getString(joUnder,"attributeValues"));
					hUnder.setNodeName(GsonHelper.getString(joUnder, "nodeName"));
					hUnder.setOptional(GsonHelper.getBoolean(joUnder, "optional"));
					hnf.setUnder(hUnder);
				}
				
				hnv.addFilter(hnf);
			}
			visitors.add(hnv);
		}
		this.setVisitors(visitors);
	}
	
	
	public PlainTextVisitor getPlainTextVisitor(Element element) {
		for (int i=0;i<visitors.size();i++) {
			PlainTextVisitor visitor = visitors.get(i);
			if (visitor.roughlyRecognize(element.html()) && 
				visitor.preciselyRecognize(element.html())) {
				System.out.println("visitor: "+visitor.name);
				return visitor.clone();
			}
		}
		System.out.println("visitor: "+defaultVisitor.name);
		return defaultVisitor.clone();
	}
}
