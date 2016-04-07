package student_player.mytools;

import hus.HusBoardState;
import hus.HusMove;
import hus.HusPlayer;

/** A Hus player used to compare my player to
 * uses a uniformly balanced weight function */
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
        
        
        return MyTools.bestMove;
    }
	
}