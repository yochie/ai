package student_player.mytools;

import hus.HusMove;

public class MoveEvalTuple{
	public MoveEvalTuple(HusMove moveFromParent, Double newChildEval) {
		move = moveFromParent;
		eval = newChildEval;
	}
	public HusMove move;
	public Double eval;
};