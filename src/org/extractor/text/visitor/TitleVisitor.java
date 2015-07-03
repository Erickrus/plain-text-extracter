package org.extractor.text.visitor;

import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeVisitor;

	public class TitleVisitor implements NodeVisitor {
		
		public boolean isText = false;

		private StringBuilder texts = new StringBuilder();

		public void head(Node node, int depth) {
			
			if (node.nodeName().toLowerCase().equals("title")) {
				isText = true;
			}
			
			if (node instanceof TextNode && isText) {
				String text = ((TextNode) node).text();
				text = text.replaceAll("\t", " ");
				text = text.trim();
				if (text.length()>0)
				texts.append(text);
			}
		}

		public void tail(Node node, int depth) {
			if (node.nodeName().toLowerCase().equals("title")) {
				isText = false;
			}
		}

		@Override
		public String toString() {
			return texts.toString();
		}
	}