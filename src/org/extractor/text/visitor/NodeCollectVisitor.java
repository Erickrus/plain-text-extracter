package org.extractor.text.visitor;

import java.util.ArrayList;

import org.jsoup.nodes.Node;
import org.jsoup.select.NodeVisitor;

public class NodeCollectVisitor implements NodeVisitor {

	public ArrayList<Node> nodes = new ArrayList<Node>();
	@Override
	public void head(Node node, int depth) {
		nodes.add(node);
	}

	@Override
	public void tail(Node node, int depth) {
	}

}
