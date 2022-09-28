package project1;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class Node implements Iterable<Node> {
	
	public enum AstKey {
		STRING, NUMBER,
		WINDOW, FLOW, GRID, BUTTON, LABEL, PANEL, TEXTFIELD,
		RADIOS, WIDGETS;
	}
	
	public AstKey key;
	public Object value;
	public LinkedList<Node> nodes;
	
	public Node(AstKey key, Object value) {
		this.key = key;
		this.value = value;
		nodes = new LinkedList<>();
	}
	
	public Node(AstKey key) {
		this(key, null);
	}
	
	public void add(Node n) {
		if(nodes != null) {
			nodes.add(n);
		}
	}
	
	public void add(AstKey key, Object value) {
		add(new Node(key, value));
	}
	
	public void add(AstKey key) {
		add(new Node(key));
	}
	
	@Override
	public String toString() {
		String s = key + (value == null ? "" : ": " + value);
		return s;
	}
	
	public String treeToString1(int spacing) {
		return treeToString1(spacing, "");
	}
	
	private String treeToString1(int spacing, String indent) {
		String s = "";
		s += indent + toString();
		for(int i = 0; i < spacing; i++) {
			indent += " ";
		}
		for(Node n : nodes) {
			s += "\n";
			s += n.treeToString1(spacing, indent);
		}
		return s;
	}
	
	public String treeToString2(int spacing) {
		return treeToString2(spacing, "", true, true);
	}
	
	private String treeToString2(int spacing, String indent, boolean root, boolean last) {
		String s = "";
		if(root) {
			s += toString();
		} else {
			s += indent + "+";
			indent += last ? " " : "|";
			for(int i = 0; i < spacing; i++) {
				s += "-";
				indent += " ";
			}
			s += toString();
		}
		for(int i = 0; i < nodes.size(); i++) {
			s += "\n";
			s += nodes.get(i).treeToString2(spacing, indent, false, i == nodes.size() - 1);
		}
		return s;
	}
	
	public List<Node> toPreOrderList() {
		List<Node> list = new LinkedList<>();
		toPreOrderList(list);
		return list;
	}
	
	private void toPreOrderList(List<Node> list) {
		list.add(this);
		for(Node node : nodes) {
			node.toPreOrderList(list);
		}
	}
	
	@Override
	public Iterator<Node> iterator() {
		return toPreOrderList().iterator();
	}
	
	public ListIterator<Node> listIterator() {
		return toPreOrderList().listIterator();
	}
	
	public String getString() {
		return key == AstKey.STRING ? (String) value : null;
	}
	
	public int getInt() {
		return key == AstKey.NUMBER ? (Integer) value : null;
	}
	
}
