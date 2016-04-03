package student_player.mytools;

import java.util.ArrayList;
import java.util.Stack;

import hus.HusBoardState;
import hus.HusMove;
import hus.HusPlayer;
import student_player.mytools.MyTools;
import student_player.mytools.Node;
import student_player.mytools.StateNode;

/** A Hus player used to compare my player to
 * uses a uniformly balanced weight function */
public class BalancedPlayer extends HusPlayer {
	

    public BalancedPlayer() { super("balancedPlayer"); }
    
    public HusMove chooseMove(HusBoardState board_state)
    {


        // Get the legal moves for the current board state.
        ArrayList<HusMove> moves = board_state.getLegalMoves();
        
        //create the starting node for our minimax tree
        StateNode rootNode = new StateNode((HusBoardState) board_state);
        rootNode.setMyturn(true);
        rootNode.setDepth(0);
        
        //create stack we'll use to do DFS on move space
        Stack<StateNode> stateStack = new Stack<StateNode>();
        
        //add root node to stack
        stateStack.push(rootNode);
        
        //initialize iterators
        HusBoardState newState = null;
        StateNode newNode = null;        
        StateNode currentNode = null;
                
        while (!stateStack.isEmpty()){
        	//get top node from stack
        	currentNode = stateStack.pop();
        	
        	//get legal moves for its state
        	moves = currentNode.getState().getLegalMoves();

        	//if its depth is 4 or more, we'll stop here and calculate its utility based on our heuristics
        	if (currentNode.getDepth() > 3){
        		currentNode.setLeaf(true);
        		
        		//Uses static balanced player weights from MyTools
        		MyTools.evaluateUtility(currentNode, player_id, opponent_id,MyTools.BALANCED_WEIGHTS);
        		//System.out.println(currentNode.getEvaluation());
        		continue;
    		}
			newNode = null;
        	
	        //for each possible move
	        for (HusMove m : moves){
	        	
	        	//create state copy
				newState = (HusBoardState) currentNode.getState().clone();
	        	
	        	//do move on that copy
	        	newState.move(m);
	        	
	        	//add new state as child in tree
	        	newNode = new StateNode(currentNode, newState);
	        	newNode.setDepth(currentNode.getDepth()+1);
	        	newNode.setMyturn(!currentNode.isMyturn());
	        	newNode.setMoveFromParent(m);
	        	currentNode.addChild(newNode);
	        	
	        	stateStack.push(newNode);
	        	//add to stack
	        }
	        
        }
        
        //Choose the best move to take from current state
        Double bestYet = -Double.MAX_VALUE;
        StateNode bestNode = null;
        
        for (Node<HusBoardState> child : rootNode.getChildren())
        {
        	double current = MyTools.evaluateUtility((StateNode) child, player_id, opponent_id, MyTools.BALANCED_WEIGHTS);
        	if ( current > bestYet){
				bestYet = current;
				bestNode = (StateNode)child;
			}
        }
//        System.out.println("Best move has evaluation : " + bestYet);
		return bestNode.getMoveFromParent();
		

    }
	
}