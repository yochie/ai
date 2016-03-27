package student_player.mytools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Node<T> implements Cloneable{
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
	
	@SuppressWarnings("unchecked")
	public Node<T> clone(){
		try{
			Node<T> c = (Node<T>) super.clone();
			return c;
		}
		catch(CloneNotSupportedException e)
		{
			
			return null;
		}
	}

}
