package student_player.mytools;

import java.util.ArrayList;

import hus.HusBoardState;
import hus.HusMove;

public class StateNode extends Node<HusBoardState> implements Cloneable{
	private float evaluation;
	private boolean evaluated = false;
	private int depth = -1;
	private boolean myturn;
	private boolean leaf;
	private HusMove moveFromParent;
	
	public StateNode(StateNode parent, HusBoardState state, ArrayList<StateNode> children){
		this.setState((HusBoardState)state);
		this.setParent(parent);
		for (StateNode child : children){
			this.addChild(child);			
		}
	}
	
	public StateNode(HusBoardState state, ArrayList<StateNode> children){
		this.setState((HusBoardState)state);
		this.setParent(null);
		for (StateNode child : children){
			this.addChild(child);			
		}
	}
	
	public StateNode(StateNode parent, HusBoardState state){
		this.setState((HusBoardState)state);
		this.setParent(parent);
	}
	
	public StateNode(HusBoardState state){
		this.setState((HusBoardState)state);
	}

	@Override
	public HusBoardState getState() {
		return (HusBoardState) this.state.clone();
	}

	@Override
	public void setState(HusBoardState state) {	
		this.state = (HusBoardState) state.clone();
	}


	public float getEvaluation() {
		return evaluation;
	}


	public void setEvaluation(float evaluation) {
		this.evaluation = evaluation;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public boolean isMyturn() {
		return myturn;
	}

	public void setMyturn(boolean myturn) {
		this.myturn = myturn;
	}

	public boolean isLeaf() {
		return leaf;
	}

	public void setLeaf(boolean isleaf) {
		this.leaf = isleaf;
	}

	public HusMove getMoveFromParent() {
		return moveFromParent;
	}

	public void setMoveFromParent(HusMove moveFromParent) {
		this.moveFromParent = moveFromParent;
	}

	public boolean isEvaluated() {
		return evaluated;
	}

	public void setEvaluated(boolean evaluated) {
		this.evaluated = evaluated;
	}

}
