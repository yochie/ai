package student_player.mytools;

import java.util.ArrayList;

public abstract class Node<T>{
	protected T state;
	protected Node<T> parent;
	protected ArrayList<Node<T>> children = new ArrayList<Node<T>>();
	
	public Node<T> getParent() {
		return parent;
	}
	
	public void setParent(Node<T> parent) {
		this.parent = parent;
	}
	
	public ArrayList<Node<T>> getChildren() {
		return children;
	}
	
	public void addChild(Node<T> child) {
		this.children.add(child);
	}
	
	public abstract T getState();
	
	public abstract void setState(T state);

}
