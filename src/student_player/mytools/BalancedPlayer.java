package student_player.mytools;

import java.util.Random;

import hus.HusBoardState;
import hus.HusMove;
import hus.HusPlayer;

/** A Hus player used to compare my player to
 * uses only the first heuristic function */
public class BalancedPlayer extends HusPlayer {

    public BalancedPlayer() { super("balancedPlayer"); }
    
    public HusMove chooseMove(HusBoardState board_state)
    {
        
        //create the starting node for our minimax tree
        StateNode rootNode = new StateNode((HusBoardState) board_state);
        rootNode.setMyturn(true);
        rootNode.setDepth(0);
 
        MyTools.bestMove = null;
        MyTools.evaluateUtility(rootNode, player_id, opponent_id, MyTools.BALANCED_WEIGHTS);
        
        if (MyTools.bestMove == null){
        	MyTools.bestMove = ((StateNode)rootNode.getChildren().get(0)).getMoveFromParent();
        }
        
        //Return best move version
        return MyTools.bestMove;
//        
//        //choose one of the best three moves (if less than three available then scale random function down appropriately
//        int index;
//        Random r = new Random();
//        if (MyTools.bestMoves.size() >= 2){
//			index = r.nextInt(2);
//        }
//        else {	index = r.nextInt(MyTools.bestMoves.size());}
//        
//        //set default value in case there are no available moves (shouldn't happen)
//        MoveEvalTuple toreturn = MyTools.bestMoves.peek();
//        
//        //choose one of the best three moves
//        for (int i = 0; i <= index; i++){
//        	toreturn =  MyTools.bestMoves.remove();
//        	
//        }
//        
//        //refresh bestmoves for next time
//        MyTools.bestMoves.clear();
//        
//        
//        return toreturn.move;
        
    }
	
}