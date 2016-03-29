package student_player.mytools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import autoplay.Autoplay;
import hus.HusBoardState;

public class MyTools {
	//tests different weight values and uses hill climbing to optimize
	public static void main(String args[]){
		
		Double [] weights = new Double[HEURISTICS.length];
		
		//initialize weights so that they add up to 1
		for (int i = 0; i < HEURISTICS.length; i++)
		{
			weights[i] = 0.0;//(Double) (1.0/HEURISTICS.length);
		}
		Function evalFunction = new Function(){
			@Override
			public double evaluate(Double[] x){
				//play 10 games, see who comes out the winner
				//return probability to win
				int numIterations = 1;
				
				//set Weights to those to be tested
				WEIGHTS = x;
				String[] argForAutoplay = {Integer.toString(numIterations)};
				Autoplay.main(argForAutoplay); 
				
				//open log file and read last 10 lines, counts number of wins
				int numWins = 0;
				
				File file = new File("D:\\Code\\comp424_project\\logs\\outcomes.txt");
				System.out.println(tail(file, 20));
				
				return (double) numWins/numIterations;
			}
		};
		//run hill climbing over space of heuristic weights
		Climber.execute(evalFunction);
		
	}
	
	//weights that are used by the student player both in performing in actual games and in training
	//note: generic player has his weights in its own class
	public static Double[] WEIGHTS = {1.0, 0.0, 0.0};
	
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
			//Second heuristic : 
			new Heuristic(){
				@Override
				public int evaluate(HusBoardState state, boolean isMyTurn){
					return 0;
				}
			}
	};
	
    //Returns the estimated utiliy of some state of the game    
	public static double evaluateUtility(StateNode node, int player_id, int opponent_id, Double[] weights) {
		//check if node was already evaluated, if so just return that eval
		if (node.isEvaluated()){
			return node.getEvaluation();
		}
		
		//calculate evaluation using heuristic (we won't be going any further into the tree)
		if (node.isLeaf()){
			
			//list of values returned by heuristics for the given state
			ArrayList<Integer> computedHeurisitcs = new ArrayList<Integer>();
			
			//compute value returned by all heuristics
			for (int i = 0; i < HEURISTICS.length; i++)
			{
				computedHeurisitcs.add(HEURISTICS[i].evaluate(node.getState(), node.isMyturn()));
			}
			
			//Compute evaluation given weights on each heuristic
			Double evaluation = 0.0;
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
			Double bestYet = -Double.MAX_VALUE;
			//choose max of children evaluations
			for (Node<HusBoardState> child : node.getChildren()){
				Double current = evaluateUtility((StateNode) child, player_id, opponent_id, weights);
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
			Double bestYet = Double.MAX_VALUE;
			//choose min of children evaluations
			for (Node<HusBoardState> child : node.getChildren()){
				Double current = evaluateUtility((StateNode) child, player_id, opponent_id, weights);
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
	
	//COPIED FROM http://stackoverflow.com/questions/686231/quickly-read-the-last-line-of-a-text-file
	//grabs the last "lines"/2 lines of a file
	public static String tail( File file, int lines) {
	    java.io.RandomAccessFile fileHandler = null;
	    try {
	        fileHandler = 
	            new java.io.RandomAccessFile( file, "r" );
	        long fileLength = fileHandler.length() - 1;
	        StringBuilder sb = new StringBuilder();
	        int line = 0;

	        for(long filePointer = fileLength; filePointer != -1; filePointer--){
	            fileHandler.seek( filePointer );
	            int readByte = fileHandler.readByte();

	             if( readByte == 0xA ) {
	                if (filePointer < fileLength) {
	                    line = line + 1;
	                }
	            } else if( readByte == 0xD ) {
	                if (filePointer < fileLength-1) {
	                    line = line + 1;
	                }
	            }
	            if (line >= lines) {
	                break;
	            }
	            sb.append( ( char ) readByte );
	        }

	        String lastLine = sb.reverse().toString();
	        return lastLine;
	    } catch( java.io.FileNotFoundException e ) {
	        e.printStackTrace();
	        return null;
	    } catch( java.io.IOException e ) {
	        e.printStackTrace();
	        return null;
	    }
	    finally {
	        if (fileHandler != null )
	            try {
	                fileHandler.close();
	            } catch (IOException e) {
	            }
	    }
	}
}

