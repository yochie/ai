package student_player.mytools;


import hus.HusBoardState;

public class MyTools {

    public static double getSomething(){
        return Math.random();
    }
    
	public static int evaluateUtility(StateNode node, int player_id, int opponent_id) {
	// Get the contents of the pits so we can use it to make decisions.
		if (node.isLeaf()){
				int[][] pits = node.getState().getPits();
	
		      // Use ``player_id`` and ``opponent_id`` to get my pits and opponent pits.
		      int[] my_pits = pits[player_id];
		      int[] op_pits = pits[opponent_id];
		      
		      int my_num = sum(my_pits);
		      int op_num = sum(op_pits);
		      int evaluation = my_num - op_num;
		      node.setEvaluation(evaluation);
		      return evaluation;
		}
		else if (node.isMyturn())
		{
			int bestYet = Integer.MIN_VALUE;
			//choose max of children evaluations
			for (Node<HusBoardState> child : node.getChildren()){
				int current = evaluateUtility((StateNode) child, player_id, opponent_id);
				if ( current > bestYet){
					bestYet = current;
				}
			}
			node.setEvaluation(bestYet);
			return bestYet;
		}
		else
		{
			int bestYet = Integer.MAX_VALUE;
			//choose max of children evaluations
			for (Node<HusBoardState> child : node.getChildren()){
				int current = evaluateUtility((StateNode) child, player_id, opponent_id);
				if ( current < bestYet){
					bestYet = current;
				}
			}
			node.setEvaluation(bestYet);
			return bestYet;
		}
	}

	private static int sum(int[] my_pits) {
		int total = 0;
		for (int pit : my_pits)
		{
			total+= pit;
		}
		return total;
	}
}

