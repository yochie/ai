package student_player.mytools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import autoplay.Autoplay;
import hus.HusBoardState;
import hus.HusMove;

public class MyTools {
	//static variable that holds best move from the top level node when executing EvaluateUtility on root node
	public static HusMove bestMove = null;
	
	
	//Holds possible moves from rootnode, orders them so that one with best evaluation comes out first 
	public static PriorityQueue<MoveEvalTuple> bestMoves = new PriorityQueue<MoveEvalTuple>(32, 
			new Comparator<MoveEvalTuple>(){

				@Override
				public int compare(MoveEvalTuple o1, MoveEvalTuple o2) {
					if (o1.eval < o2.eval ){
						return 1;
					}
					else if (o1.eval == o2.eval){
						return 0;
					}
					else{					
						return -1 ;
					}
				}
		
	});
	
	
	//defines maximum min-max tree depth
	private static final int MAX_DEPTH = 5;
	
	//How many games to simulate when evaluating some weight configuration during climbing algorithm
	protected static final int NUM_GAMES_SIMULATED = 5;	
	
	//weights that are used by the student player 
	public static Double[] MY_PLAYER_WEIGHTS = {12.5, 12.5, 5.0};
	
	//Weights used by balancedPlayer during climbing alrgorithm
	public static Double[] BALANCED_WEIGHTS = {10.0, 0.0, 0.0}; 

	//Set of heuristics that we will try to balance
	//note: they return values of somewhat arbitrary magnitude, so weights will have to compensate
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
					
					return h1;				
				}
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
					return (double) -numStones;
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
					
					int hidden = 0;
					int maxPit = (HusBoardState.BOARD_WIDTH * 2) - 1;
					for (int i = HusBoardState.BOARD_WIDTH; i <= maxPit; i++){
						if (my_pits[i] == 0){
							hidden += my_pits[HusBoardState.BOARD_WIDTH - (i - HusBoardState.BOARD_WIDTH )];
						}
					}
					
					return (double) hidden;
				}
			}
	};


	//tests different weight values and uses hill climbing to optimize
	//not used for actual competition
	public static void main(String args[]){
		
		//Evaluation function for hill climbing, returns probability of win for given weight setup
		Function evalFunction = new Function(){
			@Override
			public double evaluate(Double[] w){
				//play n games, see who comes out the winner
				int numIterations = NUM_GAMES_SIMULATED;
				
				//set static class Weights to those to be tested
				

				String[] argForAutoplay = {Integer.toString(numIterations)};
				
				String strw = w[0].toString() + "," + w[1].toString() +"," + w[2].toString() + "\n";
				System.out.println(strw + " Written to file");
				try {
				    Files.write(Paths.get("D:\\Code\\comp424_project\\testingweights.txt"), strw.getBytes());
				}catch (IOException e) {
					e.printStackTrace();
				}

				
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
				
				String line1 ="Evaluated win rate for weights: " + w[0].toString() + ", " + w[1].toString() +", " + w[2].toString() + " won  " + numWins + " out of " + numIterations + "\n";
				try {
				    Files.write(Paths.get("myownlog.txt"), line1.getBytes(), StandardOpenOption.APPEND);
				}catch (IOException e) {
					e.printStackTrace();				
				}
				
				System.out.println("Evaluated win rate for weights: " + w[0].toString() + ", " + w[1].toString() +", " + w[2].toString() + " won  " + numWins + " out of " + numIterations);
				
				return (double) numWins/(double) numIterations;
			}
		};
		//run hill climbing over space of heuristic weights
		Climber.execute(evalFunction);
		
	}
	

    //Returns the estimated utiliy of some state of the game
	//Side effect: fills static class list of bestMoves for first given node, also sets  bestMove
	public static double evaluateUtility(StateNode currentNode, int player_id, int opponent_id, Double[] weights) {
    	int currentDepth = currentNode.getDepth();

		//get legal moves for this state
		 ArrayList<HusMove> moves = currentNode.getState().getLegalMoves();
    	
    	//if its depth is 5 or more, we'll stop here and calculate its utility based on our heuristic
    	if ( currentDepth >= MAX_DEPTH){
    		currentNode.setLeaf(true);
			
			//list of values returned by heuristics for the given state
			ArrayList<Double> computedHeurisitcs = new ArrayList<Double>();
			
			//compute value returned by all heuristics
			for (int i = 0; i < HEURISTICS.length; i++)
			{
				if (weights[i] != 0){
					computedHeurisitcs.add(HEURISTICS[i].evaluate(currentNode.getState(), currentNode.isMyturn()));
					//System.out.println("Heuristic # " + i + " evaluated to " + computedHeurisitcs.get(i));
				}
				else {
					computedHeurisitcs.add(0.0);
				}
			}
			
			//Compute evaluation given weights on each heuristic
			Double evaluation = 0.0;
			int counter = 0;
			for (Double h : computedHeurisitcs)
			{
				evaluation += h * weights[counter];
				counter++;
			}
			
			currentNode.setEvaluation(evaluation);
			currentNode.setEvaluated(true);
			return evaluation;
		}
    	
		else{ 
			StateNode newNode = null;
	    	
	    	HusBoardState newState;
			
	    	Double newChildEval = null;

	    	//MAX level node
			if (currentNode.isMyturn())
			{
				Double bestYet = -Double.MAX_VALUE;
				
				//choose max of children evaluations
				for (HusMove m : moves){
		        	//create state copy
					newState = (HusBoardState) currentNode.getState().clone();
		        	
		        	//do move on that copy
		        	newState.move(m);
		        	
		        	//add new state as child in tree
		        	newNode = new StateNode(currentNode, newState);
		        	newNode.setDepth(currentDepth+1);
		        	newNode.setMyturn(!currentNode.isMyturn());
		        	newNode.setMoveFromParent(m);
		        	currentNode.addChild(newNode);
					
					newChildEval = evaluateUtility(newNode, player_id, opponent_id, weights);
					
					//If we are in rootnode
					//note: rootnode is always a max node
					if (currentDepth == 0){
						//add move to list along with its evaluation
						bestMoves.add(new MoveEvalTuple(m, newChildEval));
					}
					if ( newChildEval > bestYet){
						bestYet = newChildEval;
						if (currentDepth == 0){
							//set static variable so that Studentplayer can retrieve best move
							bestMove = m;
						}
						currentNode.setMinRange(newChildEval);
						
						//if outside range of parents, stop looking through this nodes children
						//and just return current value (not important since we wont be using it)
						if (outsideParentsRange(currentNode)){
							break;
						}
					}
				}
				currentNode.setEvaluation(bestYet);
				currentNode.setEvaluated(true);
				return bestYet;
			}
			//MIN level node
			else
			{
				//init iterators
				Double bestYet = Double.MAX_VALUE;
				
				//choose min of children evaluations
				for (HusMove m : moves){
		        	//create state copy
					newState = (HusBoardState) currentNode.getState().clone();
		        	
		        	//do move on that copy
		        	newState.move(m);
		        	
		        	//add new state as child in tree
		        	newNode = new StateNode(currentNode, newState);
		        	newNode.setDepth(currentDepth+1);
		        	newNode.setMyturn(!currentNode.isMyturn());
		        	newNode.setMoveFromParent(m);
		        	currentNode.addChild(newNode);
					
					newChildEval = evaluateUtility(newNode, player_id, opponent_id, weights);
					if ( newChildEval < bestYet){
						bestYet = newChildEval;
						currentNode.setMaxRange(newChildEval);
						if (outsideParentsRange(currentNode)){
							break;
						}
					}
				}
				currentNode.setEvaluation(bestYet);
				currentNode.setEvaluated(true);
				return bestYet;
			
			}
		}
	}

	private static boolean outsideParentsRange(StateNode node) {
		StateNode parent = (StateNode) node.getParent();
		while (parent != null){
			if (node.getRange()[0] >= parent.getRange()[1] || node.getRange()[1] <= parent.getRange()[0])
			{
				return true;
			}
			
			parent = (StateNode) parent.getParent();
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

