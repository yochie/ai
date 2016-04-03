package student_player;

import java.util.ArrayList;
import java.util.Stack;

import hus.HusBoardState;
import hus.HusMove;
import hus.HusPlayer;
import student_player.mytools.MyTools;
import student_player.mytools.Node;
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
        
        StateNode currentNode = null;
                
        while (!stateStack.isEmpty()){
        	//get top node from stack
        	currentNode = stateStack.pop();
        	
        	//get legal moves for its state
        	moves = currentNode.getState().getLegalMoves();

        	//if its depth is 4 or more, we'll stop here and calculate its utility based on our heuristic
        	if (currentNode.getDepth() > 3){
        		currentNode.setLeaf(true);
        		
        		//uses static weights from MyTools
        		MyTools.evaluateUtility(currentNode, player_id, opponent_id, MyTools.WEIGHTS);
        		//System.out.println(currentNode.getEvaluation());
        		continue;
    		}
        	StateNode newNode = null;
        	
	        //for each possible move
	        for (HusMove m : moves){
	        	
	        	//create state copy
	        	HusBoardState newState = (HusBoardState) currentNode.getState().clone();
	        	
	        	//do move on that copy
	        	newState.move(m);
	        	
	        	//add new state as child in tree
	        	newNode = new StateNode(currentNode, newState);
	        	newNode.setDepth(currentNode.getDepth()+1);
	        	newNode.setMyturn(!currentNode.isMyturn());
	        	newNode.setMoveFromParent(m);
	        	currentNode.addChild(newNode);
	        	
	        	//add to stack
	        	stateStack.push(newNode);
	        }
	        
        }
        
        //Choose the best move to take from current state
        Double bestYet = -Double.MAX_VALUE;
        StateNode bestNode = null;
        
        for (Node<HusBoardState> child : rootNode.getChildren())
        {
        	double current = MyTools.evaluateUtility((StateNode) child, player_id, opponent_id, MyTools.WEIGHTS);
        	if ( current > bestYet){
				bestYet = current;
				bestNode = (StateNode)child;
			}
        }
        
        //if no child found, loose is guaranteed, just pick firt possible move
        if(bestNode == null){
        	rootNode.getChildren().get(0);
        }
        
        System.out.println("Best move has evaluation : " + bestYet);
		return bestNode.getMoveFromParent();
		

    }

}
