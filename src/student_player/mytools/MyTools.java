package student_player.mytools;

import java.util.ArrayList;

import hus.HusBoardState;

public class MyTools {
	//tests different weight values and uses hill climbing to optimize
	public static void main(String args[]){
		
		float [] weights = new float[HEURISTICS.length];
		
		//initialize weights so that they add up to 1
		for (int i = 0; i < HEURISTICS.length; i++)
		{
			weights[i] = (float) (1.0/HEURISTICS.length);
		}
		
		//run hill climbing over space of heuristic weights
		//Climber.execute();
		
	}
	
	public static float[] WEIGHTS = {1, 0};
	
	public static final Heuristic[] HEURISTICS = {
			//First heuristic: number of stones in my pits - number of stones in opponent's pits
			new Heuristic(){
				@Override
				public int evaluate(HusBoardState state, boolean isMyTurn){
					int[][] pits = state.getPits();
					
					int[] my_pits;
					int[] op_pits;
					// Use ``player_id`` and ``opponent_id`` to get my pits and opponent pits.
					if (isMyTurn){
						my_pits = pits[state.getTurnPlayer()];
						op_pits = pits[(state.getTurnPlayer() + 1) % 2];
					}
					else{
						my_pits = pits[(state.getTurnPlayer() + 1) % 2];
						op_pits = pits[state.getTurnPlayer()];						
					}
					
					int my_num = sum(my_pits);
					int op_num = sum(op_pits);
					int h1 = my_num - op_num;
					return h1;				}
			},
			//Second heuristic : 
			new Heuristic(){
				@Override
				public int evaluate(HusBoardState state, boolean isMyTurn){
					return 0;
				}
			},
	};
	
    public static double getSomething(){
        return Math.random();
    }
    
	public static float evaluateUtility(StateNode node, int player_id, int opponent_id, float[] weights) {
		//check if node was already evaluated, if so just return that eval
		if (node.isEvaluated()){
			return node.getEvaluation();
		}
		
		//calculate evaluation using heuristic
		if (node.isLeaf()){
			
			//list of values returned by heuristics for the given state
			ArrayList<Integer> computedHeurisitcs = new ArrayList<Integer>();
			
			for (int i = 0; i < HEURISTICS.length; i++)
			{
				computedHeurisitcs.add(HEURISTICS[i].evaluate(node.getState(), node.isMyturn()));
			}
			
			//Compute evaluation given weights
			float evaluation = 0;
			int counter = 0;
			for (Integer h : computedHeurisitcs)
			{
				evaluation += h * weights[counter];
				counter++;
			}
			
			node.setEvaluation(evaluation);
			node.setEvaluated(true);
			return evaluation;
		}
		//MAX level node
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
			node.setEvaluated(true);
			return bestYet;
		}
		//MIN level node
		else
		{
			float bestYet = Float.MAX_VALUE;
			//choose min of children evaluations
			for (Node<HusBoardState> child : node.getChildren()){
				float current = evaluateUtility((StateNode) child, player_id, opponent_id, weights);
				if ( current < bestYet){
					bestYet = current;
				}
			}
			node.setEvaluation(bestYet);
			node.setEvaluated(true);
			return bestYet;
		}
	}

	private static int sum(int[] pits) {
		int total = 0;
		for (int pit : pits)
		{
			total+= pit;
		}
		return total;
	}
}

