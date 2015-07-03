package org.extractor.text;

import java.util.ArrayList;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

public class NodeFilter {
	
	public String nodeName;
	public String attributeValues;
	public String attributeName;
	public boolean optional = false;
	public NodeFilter under = null;
	public NodeFilter after = null;
	public NodeFilter before = null;
	
	public ArrayList<NodeFilter> except = new ArrayList<NodeFilter>();
	
	public void addExcept(NodeFilter exceptNode) {
		this.except.add(exceptNode);
	}
	
	public NodeFilter getUnder() {
		return under;
	}

	public void setUnder(NodeFilter under) {
		this.under = under;
	}

	public NodeFilter getAfter() {
		return after;
	}

	public void setAfter(NodeFilter after) {
		this.after = after;
	}
	
	public NodeFilter getBefore() {
		return before;
	}

	public void setBefore(NodeFilter before) {
		this.before = before;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getAttributeValues() {
		return attributeValues;
	}

	public void setAttributeValues(String attributeValues) {
		this.attributeValues = attributeValues;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public boolean isOptional() {
		return optional;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	public NodeFilter() {
		
	}
	
	public NodeFilter(String pNodeName, String pAttributeName, String pAttributeValues, boolean optional) {
		this.nodeName = pNodeName.toLowerCase();
		this.attributeValues = pAttributeValues.toLowerCase();
		this.attributeName = pAttributeName.toLowerCase();
		this.optional = optional;
	}
	
	
	//lookup for the node, if exists in the html
	public boolean isPartOf(String html) {
		boolean result = true;
		
		if (this.nodeName.equals("#comment") ||
			this.nodeName.equals("#text")) {
			return html.indexOf(attributeValues) >= 0;
		}
		
		if ((html.indexOf(this.nodeName.toLowerCase()) < 0)
				|| (html.indexOf(this.attributeName.toLowerCase()) < 0)
				|| (html.indexOf(this.attributeValues.toLowerCase()) < 0)) {
			return false;
		}
		return result;
	}
	
	public boolean accept(Node n) {
		boolean result = false;
		String name = n.nodeName().toLowerCase();

		
		if (name.equals(nodeName)) {
			if (name.equals("#comment")) {
				String nodeValue = n.outerHtml().toLowerCase().replaceAll("\r", "").replaceAll("\n", "");
				return attributeValues.toLowerCase().equals(nodeValue);
			}
			
			if (name.equals("#text")) {
				String nodeValue = ((TextNode) n).text().toLowerCase().replaceAll("\r", "").replaceAll("\n", "");
				return attributeValues.toLowerCase().equals(nodeValue);
			}
			
			Attributes attrs = n.attributes();
			String attrValues = attrs.get(attributeName);
			return equalSubValue(attributeValues, attrValues );
		}
		
		return result;
	}

	//make sure each attributes are matched
    //without considering their order, separated by white-space
	public static boolean equalSubValue(String value1, String value2) {
		boolean result = true;
		
		//process for those null cases
		if (value1 == null && value2==null) {
			return true;
		}
		
		if ((value1 == null && value2!= null)|| 
			(value2 == null && value1!= null)) {
			return false;
		}
		
		
		String [] c1 = value1.toLowerCase().split(" ");
		String [] c2 = value2.toLowerCase().split(" ");
		for (int i=0;i<c1.length;i++) {
			boolean contains = false;
			for (int j=0;j<c2.length;j++) {
				String s1 = c1[i];
				String s2 = c2[j];
				
				if (s1.length()>0 && s2.length()>0) {
					if (s1.equals(s2)) {
						contains = true;
						break;
					}
				}
			}
			if (!contains) {
				return false;
			}
		}
		
		return result;
	}
	
	public String toString() {
		return "nodeName:"+nodeName+"\r\n"+
		"attributeName:"+attributeName+"\r\n"+
		"attributeValues:"+attributeValues+"\r\n"+
		"optional:"+optional+"\r\n";
	}
}
