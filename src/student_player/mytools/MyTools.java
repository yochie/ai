package student_player.mytools;

import java.util.ArrayList;

import hus.HusBoardState;

public class MyTools {
	
	public static float[] WEIGHTS = {1};

    public static double getSomething(){
        return Math.random();
    }
    
	public static float evaluateUtility(StateNode node, int player_id, int opponent_id, float[] weights) {
	// Get the contents of the pits so we can use it to make decisions.
		if (node.isLeaf()){
			
			ArrayList<Integer> heuristics = new ArrayList<Integer>();
			
			//First heuristic: number of stones in my pits - number of stones in opponent's pits
			int[][] pits = node.getState().getPits();
			
			// Use ``player_id`` and ``opponent_id`` to get my pits and opponent pits.
			int[] my_pits = pits[player_id];
			int[] op_pits = pits[opponent_id];
			
			int my_num = sum(my_pits);
			int op_num = sum(op_pits);
			int h1 = my_num - op_num;
			
			heuristics.add(h1);
			
			//Second heurisitc:
			
			//...
			//Compute evaluation given weights
			float evaluation = 0;
			int counter = 0;
			for (Integer h : heuristics)
			{
				evaluation += h * weights[counter];
				counter++;
			}
			
			
			node.setEvaluation(evaluation);
			return evaluation;
		}
		else if (node.isMyturn())
		{
			float bestYet = -Float.MAX_VALUE;
			//choose max of children evaluations
			for (Node<HusBoardState> child : node.getChildren()){
				float current = evaluateUtility((StateNode) child, player_id, opponent_id, weights);
				if ( current > bestYet){
					bestYet = current;
				}
			}
			node.setEvaluation(bestYet);
			return bestYet;
		}
		else
		{
			float bestYet = Float.MAX_VALUE;
			//choose max of children evaluations
			for (Node<HusBoardState> child : node.getChildren()){
				float current = evaluateUtility((StateNode) child, player_id, opponent_id, weights);
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

