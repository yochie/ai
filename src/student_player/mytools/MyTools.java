package student_player.mytools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import autoplay.Autoplay;
import hus.HusBoardState;

public class MyTools {
	
	//weights that are used by the student player both in performing in actual games and in training
	//note: generic player has his weights in its own class
	public static Double[] WEIGHTS = {10.0, 1.0, 0.2};
	
	public static Double[] BALANCED_WEIGHTS = {1.0, 0.0, 0.0}; /*new Double[MyTools.HEURISTICS.length];
	static {
		for (int i =0; i< WEIGHTS.length; i++){
			WEIGHTS[i] = (Double) 1.0/WEIGHTS.length;
		}
	}
	*/
	public static final Heuristic[] HEURISTICS = {
			//First heuristic: maximize number of stones in my pits - number of stones in opponent's pits
			new Heuristic(){
				@Override
				public double evaluate(HusBoardState state, boolean isMyTurn){
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
					double h1 = my_num - op_num;
					
					//Normalize heuristic by dividng by its maximum theoretical value to get a number 0<h1<1
					h1 /= 96;
					
					return h1;				}
			},	
			
			//Second heuristic : minimize number of stones that opponent has in rightmost portion of his front row.
			new Heuristic(){
				@Override
				public double evaluate(HusBoardState state, boolean isMyTurn){
					int[][] pits = state.getPits();
					int[] op_pits;

					if (isMyTurn){
						op_pits = pits[(state.getTurnPlayer() + 1) % 2];
					}
					else{
						op_pits = pits[state.getTurnPlayer()];						
					}
					
					int numStones = 0;
					//for the rightmost 3 pits of the oppponents front row
					for (int i = HusBoardState.BOARD_WIDTH; i <= HusBoardState.BOARD_WIDTH + 2; i++){
						numStones += op_pits[i];
					}
					
					//return negative number total of stones
					//divided by 10 to normalize somewhat
					//this is sort of abritrary, but should help balance heuristic so that it is closer to 1 
					return (double) -numStones/10.0;
				}
			},
			//Third heuristic : maximize number of protected pits on my side
			new Heuristic(){
				@Override
				public double evaluate(HusBoardState state, boolean isMyTurn){
					int[][] pits = state.getPits();
					
					int[] my_pits;

					if (isMyTurn){
						my_pits = pits[state.getTurnPlayer()];
					}
					else{
						my_pits = pits[(state.getTurnPlayer() + 1) % 2];
					}
					
					double hidden = 0;
					
					for (int i = HusBoardState.BOARD_WIDTH; i <= (HusBoardState.BOARD_WIDTH * 2) - 1; i++){
						if (my_pits[i] == 0){
							hidden += my_pits[HusBoardState.BOARD_WIDTH - (i - HusBoardState.BOARD_WIDTH )];
						}
					}
					
					//Again, normalize before returning by a somewhat arbitrary factor so that we are closer to 1
					return hidden/15.0;
				}
			}
	};
	
	
	//tests different weight values and uses hill climbing to optimize
	public static void main(String args[]){
		
		Double [] weights = new Double[HEURISTICS.length];
		
		//initialize weights so that they add up to 1
		for (int i = 0; i < HEURISTICS.length; i++)
		{
			weights[i] = 1.0;//(Double) (1.0/HEURISTICS.length);
		}
		
		//Evaluation function for hill climbing, returns probability of win for given weight setup
		Function evalFunction = new Function(){
			@Override
			public double evaluate(Double[] w){
				//play n games, see who comes out the winner
				int numIterations = 5;
				
				//set static class Weights to those to be tested
				Double[] weightsBackup = WEIGHTS;
				
				WEIGHTS = w;
				String[] argForAutoplay = {Integer.toString(numIterations)};
				Autoplay.main(argForAutoplay); 
				
				//open log file and read last n lines, count number of wins
				int numWins = 0;			
				
				File file = new File("D:\\Code\\comp424_project\\logs\\outcomes.txt");
				
				String lastLines = tail(file, numIterations);
				
				String[] result =lastLines.split("\\n");

				String[] line;
				String winnerId;
				String myId = "260585399";
				for (int i = 1; i < result.length; i++){
					line = result[i].split(",");
					winnerId = line[4];
					if(winnerId.compareTo(myId) == 0)
					{
						numWins++;
					}
				}
				WEIGHTS = weightsBackup;
				
				String line1 ="Evaluated win rate for weights: " + w[0].toString() + ", " + w[1].toString() +", " + w[2].toString() + " won  " + numWins + " out of " + numIterations + "\n";
				try {
				    Files.write(Paths.get("myownlog.txt"), line1.getBytes(), StandardOpenOption.APPEND);
				}catch (IOException e) {
				    //exception handling left as an exercise for the reader
				}
				
				System.out.println("Evaluated win rate for weights: " + w[0].toString() + ", " + w[1].toString() +", " + w[2].toString() + " won  " + numWins + " out of " + numIterations);
				
				return (double) numWins/(double) numIterations;
			}
		};
		//run hill climbing over space of heuristic weights
		Climber.execute(evalFunction);
		
	}
	

    //Returns the estimated utiliy of some state of the game    
	public static double evaluateUtility(StateNode node, int player_id, int opponent_id, Double[] weights) {
		//check if node was already evaluated, if so just return that eval
		if (node.isEvaluated()){
			return node.getEvaluation();
		}
		
		//calculate evaluation using heuristic (we won't be going any further into the tree)
		if (node.isLeaf()){
			
			//list of values returned by heuristics for the given state
			ArrayList<Double> computedHeurisitcs = new ArrayList<Double>();
			
			//compute value returned by all heuristics
			for (int i = 0; i < HEURISTICS.length; i++)
			{
				computedHeurisitcs.add(HEURISTICS[i].evaluate(node.getState(), node.isMyturn()));
			}
			
			//Compute evaluation given weights on each heuristic
			Double evaluation = 0.0;
			int counter = 0;
			for (Double h : computedHeurisitcs)
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
					node.setMinRange(current);
					if (outsideParentsRange(node)){
						break;
					}
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
					node.setMaxRange(current);
					if (outsideParentsRange(node)){
						break;
					}
				}
			}
			node.setEvaluation(bestYet);
			node.setEvaluated(true);
			return bestYet;
		}
	}

	private static boolean outsideParentsRange(StateNode node) {
		StateNode parent = (StateNode) node.getParent();
		while (node.getParent() != null){
			if (node.getRange()[0] > parent.getRange()[1] || node.getRange()[1] < parent.getRange()[0])
			{
				return true;
			}
			node = parent;
			parent = (StateNode) node.getParent();
		}
		return false;
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
	//grabs the last "lines" lines of a file
	public static String tail( File file, int lines) {
		lines *= 2;
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

