package org.extractor.text.visitor;


import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

import java.util.ArrayList;
import java.util.Stack;

import org.extractor.text.NodeFilter;

	public class PlainTextVisitor implements NodeVisitor {
		
		public Node effectiveNode = null;
		public NodeFilter effectiveNodeFilter = null;
		public String name = "default";
		
		private Stack<Node> parents = new Stack<Node>(); 
		private ArrayList<Node> afters = new ArrayList<Node>();
		
		private StringBuilder texts = new StringBuilder();
		private ArrayList<NodeFilter> filters = new ArrayList<NodeFilter>();

		public PlainTextVisitor(String name) {
			this.name = name;
		}
		
		//perform heavy recognition process
		//so it should be called after roughlyRecognize() usually
		public boolean preciselyRecognize(String html) {
			
			ArrayList<NodeFilter> anf = new ArrayList<NodeFilter>();
			
			//get all nodes in the HTML through nodeCollector
			Document doc = Jsoup.parse(html);
			NodeCollectVisitor nodeCollector = new NodeCollectVisitor();
			NodeTraversor traversor = new NodeTraversor(nodeCollector);
			traversor.traverse(doc);
			
			//for each filter, add all those mandatory NodeFilter(s)
			for (int i=0;i<filters.size();i++) {
				NodeFilter hnf = filters.get(i);
				anf.add(hnf);
				if (hnf.under!= null) {
					anf.add(hnf.under);
				}
				if (hnf.after!= null) {
					anf.add(hnf.after);
				}
			}
			
			//traverse through all the nodes
			//if accepted remove immediately from the list 
			for (int i=0;i<nodeCollector.nodes.size();i++) {
				Node node = nodeCollector.nodes.get(i);
				if (anf.size() == 0) break;
				for (int j=anf.size()-1;j>=0;j--) {
					NodeFilter hnf = anf.get(j);
					if (hnf.accept(node)) {
						anf.remove(j);
					}
				}
			}
			
			//if nothing is left in the candidate list
			//this means all candidates match in the given html
			return (anf.size()==0);
		}
		
		//just check
		//if the content of filters are part of HTML
		public boolean roughlyRecognize(String html) {			
			boolean result = true;
			String sHtml = html.toLowerCase();
			for (int i = 0; i < filters.size(); i++) {
				NodeFilter hnf = filters.get(i);
	
				//skip optional nodes
				if (hnf.optional) continue;
				
				if (!hnf.isPartOf(sHtml)) {
					return false;
				}
				
				if (hnf.after!=null && !hnf.after.isPartOf(sHtml)) {
					return false;
				}
				
				if (hnf.under!=null && !hnf.under.isPartOf(sHtml)) {
					return false;
				}
			}
	
			return result;
		}
		
		//invoked by VisitorFactory
		//to create an instance of matched visitor
		public PlainTextVisitor clone() {
			PlainTextVisitor instance = new PlainTextVisitor(this.name);
			instance.filters.addAll(this.filters);
			instance.effectiveNode = null;
			instance.effectiveNodeFilter = null;
			return instance;
		}
		
		public void addFilter(NodeFilter af) {
			filters.add(af);
		}

		private boolean isAfterAccept(NodeFilter af) {
			boolean result = false;
			for (int i=0;i<afters.size();i++) {
				Node n = afters.get(i);
				if (af.after != null && af.after.accept(n)) {
					return true;
				}
			}
			return result;
		}
		
		
		private boolean isUnderAccept(NodeFilter af) {
			boolean result = false;
			for (int i=0;i<this.parents.size();i++) {
				Node n = parents.get(i);
				if (af.under != null && af.under.accept(n)) {
					return true;
				}
			}
			return result;
		}
		
		private boolean isBeforeAccept(Node node) {
			boolean result = false;
	
			if (effectiveNodeFilter != null && effectiveNodeFilter.before != null) {
				for (int i = 0; i < afters.size(); i++) {
					Node n = afters.get(i);
					if (effectiveNodeFilter.before.accept(n)) {
						return true;
					}
				}
			}
			return result;
		}
		
		public void head(Node node, int depth) {
			
			parents.push(node);
			afters.add(node);
			
			for (int i=0;i<filters.size();i++) {
				NodeFilter af = filters.get(i);
				boolean acceptance = af.accept(node);
				if (af.after!=null) {
					acceptance = acceptance && isAfterAccept(af);
				}
				if (af.under!=null) {
					acceptance = acceptance && isUnderAccept(af);
				}
				if (acceptance) {
					effectiveNode = node;
					effectiveNodeFilter = af;
					break;
				}
			}
			
			if (isBeforeAccept(node)) {
				effectiveNode = null;
				effectiveNodeFilter = null;
				return;
			}
			
			// this is added default visitor, since no attrs will be added
			if (filters.size() == 0) {
				effectiveNode = node;
				effectiveNodeFilter = null;
			}
			
			if (node instanceof TextNode && effectiveNode!=null) {
				
				String text = ((TextNode) node).text();
				text = text.replaceAll("\t", " ");
				text = text.trim();
				
				
				if (effectiveNodeFilter != null && effectiveNodeFilter.except.size()>0) {
					int effectiveNodeFilterLevel = getLevel(effectiveNodeFilter);
					for (int i=0;i<effectiveNodeFilter.except.size();i++) {
						NodeFilter ex = effectiveNodeFilter.except.get(i);
						int exceptNodeFilterLevel = getLevel(ex);
						if (effectiveNodeFilterLevel>=0 &&
							exceptNodeFilterLevel>=0 &&
							effectiveNodeFilterLevel < exceptNodeFilterLevel
							) {
							text = "";
							break;
						}
					}
				}
				
				
				if (text.length()>0) {
				   texts.append(text);
				}
			}
		}
		
		public int getLevel(NodeFilter nf) {
			int result = -1;
			for (int i=parents.size()-1;i>=0;i--) {
				Node n = parents.get(i);
				if (nf.accept(n)) {
					return i;
				}
			}
			return result;
		}

		public void tail(Node node, int depth) {
			parents.pop();
			
			for (int i=0;i<filters.size();i++) {
				NodeFilter af = filters.get(i);
				if (af.accept(node)) {
					effectiveNode = null;
					effectiveNodeFilter = null;
					break;
				}
			}
			
			if (StringUtil.in(node.nodeName().toLowerCase(), 
					"p", "tr", "ul", "pre", "br", 
					"h1", "h2", "h3", "h4", "h5",
					"span", "div", "hr"
					)) {
				if (!texts.toString().endsWith("\r\n\r\n"))
				texts.append("\r\n");
			}
			
			// this is added default visitor, since no attrs will be added
			if (filters.size() == 0) {
				effectiveNode = node;
				effectiveNodeFilter = null;
			}
		}

		@Override
		public String toString() {
			return texts.toString();
		}
	}