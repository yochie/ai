package student_player.mytools;

import hus.HusBoardState;

public interface Heuristic {
	
	public double evaluate(HusBoardState input, boolean isMyTurn);
}
