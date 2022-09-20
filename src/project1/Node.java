package project1;

import java.util.LinkedList;

public class Node {
	
	public enum AstKeyword {
		STRING, NUMBER,
		WINDOW, FLOW, GRID, BUTTON, LABEL, PANEL, TEXTFIELD,
		RADIOS, WIDGETS;
	}
	
	public AstKeyword key;
	public Object value;
	public LinkedList<Node> nodes;
	
	public Node(AstKeyword key, Object value) {
		this.key = key;
		this.value = value;
		nodes = new LinkedList<>();
	}
	
	public Node(AstKeyword key) {
		this(key, null);
	}
	
	public void add(Node n) {
		if(nodes != null) {
			nodes.add(n);
		}
	}
	
	public void add(AstKeyword key, Object value) {
		add(new Node(key, value));
	}
	
	public void add(AstKeyword key) {
		add(new Node(key));
	}
	
	@Override
	public String toString() {
		String s = key + (value == null ? "" : ": " + value);
		return s;
	}
	
	public String inOrder() {
		String s = toString();
		for(Node n : nodes) {
			s += "\n";
			s += n.inOrder();
		}
		return s;
	}
	
}
