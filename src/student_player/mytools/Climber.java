package student_player.mytools;

import java.util.Arrays;

public class Climber {
	//defines how many times the algorithm will iterate if no change in evaluation is made
	private static int initNumPatienceTokens = 3;

	private static int min_range = -100;
	
	private static int max_range = 100;
	
	public static void execute(Function fn)
	{
		double stepsize = 5;
		Answer result;
		
		//Starting weights
		Double x1 = 10.0;
		Double x2 = 10.0;
		Double x3 = 10.0;
		
		result = climb(fn, new Double[] {x1, x2, x3}, stepsize, -1, null, initNumPatienceTokens);
		
		System.out.println("FOUND OPTIMUM : ");
		
		for (int i = 0; i <3; i++){
			System.out.println(result.getX()[i]);
		}
		System.out.println("with probability of winning: " + result.getY());
		System.out.println("It took " + result.getI() + " iterations");

	}
	

	private static Answer climb(Function fn, Double[] x, double stepsize, int iteration, Double currentEval, int patience)
	{
		if (currentEval == null){
			currentEval = fn.evaluate(x);
		}
		iteration++;
		
		//remembers which parameters we tried changing
		boolean changedParam[] = {false, false, false};

		//roll dice to decide which which parameter to change
		double rand_1 = Math.random() * 100;
		
		int paramToChange = (int) (rand_1 / (100.0/x.length));
		changedParam[paramToChange] = true;
		
		double partialLeftX;
		double partialRightX;
		double leftEval;
		double rightEval;
		for (int i = 0; i < 3; i++){

			//get new x for the chosen dimension
			partialRightX = calculateStep(true,x[paramToChange], stepsize);
			Double[] rightX = Arrays.copyOf(x, x.length);
		
			rightX[paramToChange] = partialRightX;
		
			rightEval = fn.evaluate(rightX);
			
			partialLeftX = calculateStep(false,x[paramToChange], stepsize);
			
			Double[] leftX = Arrays.copyOf(x, x.length);
			
			leftX[paramToChange] = partialLeftX;
			
			leftEval = fn.evaluate(leftX);
			
			//if both directions are just as good (and better than current)
			if (leftEval == rightEval && leftEval > currentEval)
			{
				//roll dice to decide which way to go so as to avoid direction bias
				double rand = Math.random() * 100;
				if (rand < 50){
					return climb (fn, leftX, stepsize/2, iteration, leftEval, initNumPatienceTokens);
				}
				else {
					return climb (fn, rightX, stepsize/2, iteration, rightEval, initNumPatienceTokens);
				}
			}
			else
			{
				//left is better than current and right
				if (leftEval > currentEval && leftEval > rightEval)
				{
					return climb (fn, leftX, stepsize/2, iteration, leftEval, initNumPatienceTokens);
				}
				//right is better than current and left
				else if (rightEval > currentEval && leftEval < rightEval)
				{
					return climb (fn, rightX, stepsize/2, iteration, rightEval, initNumPatienceTokens);
				}
				//current is better than (or equal to) both
				else
				{
					//try changing other parameters
					switch (paramToChange) {
		            	case 0:  
		            		if (!changedParam[1]){
		            			//try changing second param
			            		paramToChange = 1;

		            		}
		            		else {
		            			//try changing third param
		            			paramToChange = 2;
		            		}

		                     break;	
		            	case 1:  		            		
		            		if (!changedParam[0]){
		            			//try changing second param
		            			paramToChange = 0;

		            		}
		            		else {
		            			//try changing third param
		            			paramToChange = 2;
		            		}
		            		
		                     break;
		            	case 2:  
		            		if (!changedParam[0]){
		            			//try changing second param
			            		paramToChange = 0;

			            		}
			            		else {
			            			//try changing third param
			            			paramToChange = 1;
			            		}         		
		                     break;
					}
				}
			}
		}
		patience--;
		if (patience == 0){
			 return new Answer(x, currentEval, iteration);

		}
		else{			
			
			return climb (fn, x, stepsize*2, iteration, currentEval, patience - 1);
		}
	}
	
	//a struct that stores the different data points of a single execution of the hill climbing/annealing  algorithm
	private static class Answer
	{
		private Double[] x;
		private double y;
		private int i;
		public Answer(Double[] px, double py, int pi){
			x = px;
			y = py;
			i = pi;
		}
		public Double[] getX() {
			return x;
		}
	
		public double getY() {
			return y;
		}
		
		public int getI() {
			return i;
		}

	}
	
	//direction 0 is left, 1 is right
	//returns the parameter coordinate one step away, within the domain and given a direction
	private static double calculateStep(boolean isRight, Double x, double stepsize) {
		double newX;
		//if direction is left (ie down)
		if (!isRight){
			//Make sure we stay within bounds
			if (x - stepsize >= min_range)
			{
				newX = x - stepsize;
			}
			else
			{
				newX = x;
			}
		}
		else {
			if (x + stepsize <= max_range)
			{
				newX = x + stepsize;
			}
			else
			{
				newX = x;
			}
		}
		return newX;
	}


}
