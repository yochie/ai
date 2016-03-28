package student_player.mytools;

import hus.HusBoardState;

public interface Heuristic {
	
	public int evaluate(HusBoardState input, boolean isMyTurn);
}
