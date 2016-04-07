package student_player;

import java.io.File;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

import hus.HusBoardState;
import hus.HusMove;
import hus.HusPlayer;
import student_player.mytools.MoveEvalTuple;
import student_player.mytools.MyTools;
import student_player.mytools.StateNode;

/** A Hus player submitted by a student. */
public class StudentPlayer extends HusPlayer {

    /** You must modify this constructor to return your student number.
     * This is important, because this is what the code that runs the
     * competition uses to associate you with your agent.
     * The constructor should do nothing else. */
    public StudentPlayer() { super("260585399"); }

    /** This is the primary method that you need to implement.
     * The ``board_state`` object contains the current state of the game,
     * which your agent can use to make decisions. See the class hus.RandomHusPlayer
     * for another example agent. */
    public HusMove chooseMove(HusBoardState board_state)
    {
        
        //create the starting node for our minimax tree
        StateNode rootNode = new StateNode((HusBoardState) board_state);
        rootNode.setMyturn(true);
        rootNode.setDepth(0);
        
        MyTools.bestMove = null;
        
    //FINAL VERSION
        //Double [] weights = MyTools.MY_PLAYER_WEIGHTS;

    //FOR TESTING DIFFERENT WEIGHTS
        //TODO : REMOVE FROM FINAL VERSION
        Double [] weights = new Double[MyTools.HEURISTICS.length];
    	
		File file = new File("D:\\Code\\comp424_project\\testingweights.txt");
		
		String strw = MyTools.tail(file, 1);
		
		String[] split_strw = strw.split(",");

		System.out.println("Finding usigine the folllowing weights read from file: ");
		for (int i = 0; i < weights.length; i++){
			weights[i] = Double.parseDouble(split_strw[i]);
			System.out.println(weights[i]);
		}
    	

        MyTools.evaluateUtility(rootNode, player_id, opponent_id, weights);
        
        if (MyTools.bestMove == null){
        	MyTools.bestMove = ((StateNode)rootNode.getChildren().get(0)).getMoveFromParent();
        }
                
        
      //choose one of the best three moves
        int index;
        Random r = new Random();
        if (MyTools.bestMoves.size() >= 2){
			index = r.nextInt(2);
        }
        else {	index = r.nextInt(MyTools.bestMoves.size());}
        MoveEvalTuple toreturn = MyTools.bestMoves.peek();
        
        //choose one of the best three moves
        for (int i = 0; i <= index; i++){
        	toreturn =  MyTools.bestMoves.remove();
        	
        }
        
        //refresh bestmoves for next time
        MyTools.bestMoves.clear();
        return toreturn.move;
    }

}
