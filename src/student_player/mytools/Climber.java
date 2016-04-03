package student_player.mytools;

import java.util.Arrays;

public class Climber {
	//defines how many times the algorithm will iterate if no change in evaluation is made that is superior to minDiff
	private static int initNumPatienceTokens = 20;
	
	//defines the minimal absolute difference between two steps so that patience is refreshed
	private static double minDiff = 0.3;
	
	//Starting temperature
//	private static double startTemp = 100;
	
	//factor to multiply temperature by at each iteration
//	private static double alpha = 0.5;
	
	private static int min_range = -10;
	
	
	private static int max_range = 10;
	
	//has to evenly divided max - min ranges
//	private static int numStartingPos = 5;

	
	public static void execute(Function fn)
	{
		double stepsize = 2;
//		Answer[][][] tableOutput = new Answer[numStartingPos][numStartingPos][numStartingPos]; 
		Answer result;
		
		Double x1 = 1.0;
		Double x2 = 1.0;
		Double x3 = 1.0;
		
		result = climb(fn, new Double[] {x1, x2, x3}, stepsize, 0);
		
		System.out.println("FOUND OPTIMUM : ");
		
		for (int i = 0; i <3; i++){
			System.out.println(result.getX()[i]);
		}
		System.out.println("with probability of winning: " + result.getY());
		System.out.println("It took " + result.getI() + " iterations");
		
//		for (double x1 = min_range; x1 < max_range; x1+= (max_range - min_range)/numStartingPos) {
//			for (double x2 = min_range; x2 < max_range; x2+= (max_range - min_range)/numStartingPos) {
//				for (double x3 = min_range; x3 < max_range; x3+= (max_range - min_range)/numStartingPos) {
//					result = climb(fn, new Double[] {x1, x2, x3}, stepsize, 0);
//					tableOutput[(int) x1/((max_range - min_range)/numStartingPos)][(int) x2/((max_range - min_range)/numStartingPos)][(int) x3/((max_range - min_range)/numStartingPos)]= result;
//				} 
//			}
//		}

		//Save output to file for better viewing
		//String header =  "X,0.01,0.02,0.03,0.04,0.05,0.06,0.07,0.08,0.09,0.1\n";
		//createCSV(tableOutput, "hillclimbing.csv", header);		
		
		//Iterate for Simulated annealing
//		 tableOutput = new Answer[numStartingPos][numStartingPos][numStartingPos]; 
//		 
//
//		for (double x1 = min_range; x1 < max_range; x1+= (max_range - min_range)/numStartingPos) {
//			for (double x2 = min_range; x2 < max_range; x2+= (max_range - min_range)/numStartingPos) {
//				for (double x3 = min_range; x3 < max_range; x3+= (max_range - min_range)/numStartingPos) {
//					result = annealClimb(fn, new Double[] {x1, x2, x3}, stepsize, 0, initNumPatienceTokens, startTemp, alpha);
//					tableOutput[(int) x1][(int) x2][(int) x3] = result;
//				}
//			}
//		}
		
			
//			//Save output to file for better viewing
//			header =  "T_0 = " + startTemp * i + ",Alpha\nX,0.85, 0.90, 0.95\n";
//			createCSV(tableOutput, "simulated_annealing.csv", header);
		
	}
	
	private static Answer annealClimb(Function fn, Double x[], double stepsize, int iteration, int patienceTokens, double temp, double tReductionFactor)
	{
		double currentEval = fn.evaluate(x);
		iteration++;
		double leftEval;
		double rightEval;
		
		if (patienceTokens == 0) {return new Answer(x, currentEval, iteration);}
		
		//roll dice to decide which which parameter to change
		double rand_1 = Math.random() * 100;
		int paramToChange = (int) (rand_1 / (100.0/x.length));

		double leftX;
		double rightX;
		
		//roll dice to decide which way to go
		double rand = Math.random() * 100;
		
		//use left neighbour
		if (rand < 50){
			
			leftX = calculateStep(false,x[paramToChange], stepsize);
			Double[] newX = Arrays.copyOf(x, x.length);
			
			newX[paramToChange] = leftX;
			
			leftEval = fn.evaluate(newX);
			
			double deltaT = leftEval - currentEval;
			
			//if not an improvement
			if (deltaT < 0) {
				//reroll to decide if we'll follow negative gradient
				rand = Math.random() * 100;
				
				//go there with probability given by temperature
				if (calculateP(deltaT, temp) > rand)
				{	
					//if difference is enough, refresh patience
					if (deltaT > minDiff)
					{
						return annealClimb(fn, newX, stepsize, iteration, initNumPatienceTokens, temp * tReductionFactor, tReductionFactor);
					}
					//otherwise reduce patience by 1
					else
					{
						return annealClimb(fn, newX, stepsize, iteration, patienceTokens - 1, temp * tReductionFactor, tReductionFactor);
					}
				}
				//Stay here, repeat algorithm and reduce patience
				else
				{
					return annealClimb(fn, x, stepsize, iteration, patienceTokens - 1, temp * tReductionFactor, tReductionFactor);
				}
				
			}
			//if improvement, just go there
			else {
				//if difference is enough, refresh patience
				if (deltaT > minDiff)
				{
					return annealClimb(fn, newX, stepsize, iteration, initNumPatienceTokens, temp * tReductionFactor, tReductionFactor);
				}
				//otherwise reduce patience by 1
				else
				{
					return annealClimb(fn, newX, stepsize, iteration, patienceTokens - 1, temp * tReductionFactor, tReductionFactor);
				}
			}
		}
		//use right neighbour
		else {	
			
			rightX = calculateStep(true,x[paramToChange], stepsize);
			Double[] newX = Arrays.copyOf(x, x.length);
		
			newX[paramToChange] = rightX;
		
			rightEval = fn.evaluate(newX);
			
			double deltaT = rightEval - currentEval;
			
			//if not an improvement
			if (deltaT < 0) {
				//reroll to decide if we'll follow negative gradient
				rand = Math.random() * 100;
				
				//go there with probability given by temperature
				if (calculateP(deltaT, temp) > rand)
				{	
					//if difference is enough, refresh patience
					if (deltaT > minDiff)
					{
						return annealClimb(fn, newX, stepsize, iteration, initNumPatienceTokens, temp * tReductionFactor, tReductionFactor);
					}
					//otherwise reduce patience by 1
					else
					{
						return annealClimb(fn, newX, stepsize, iteration, patienceTokens - 1, temp * tReductionFactor, tReductionFactor);
					}
				}
				//Stay here, repeat algorithm and reduce patience
				else
				{
					return annealClimb(fn, x, stepsize, iteration, patienceTokens - 1, temp * tReductionFactor, tReductionFactor);
				}
				
			}
			//if improvement, just go there
			else {
				//if difference is enough, refresh patience
				if (deltaT > minDiff)
				{
					return annealClimb(fn, newX, stepsize, iteration, initNumPatienceTokens, temp * tReductionFactor, tReductionFactor);
				}
				//otherwise reduce patience by 1
				else
				{
					return annealClimb(fn, newX, stepsize, iteration, patienceTokens - 1, temp * tReductionFactor, tReductionFactor);
				}
			}
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

	private static Answer climb(Function fn, Double[] x, double stepsize, int iteration)
	{
		Double currentEval = fn.evaluate(x);
		iteration++;
		

		//roll dice to decide which which parameter to change
		double rand_1 = Math.random() * 100;
		
		int paramToChange = (int) (rand_1 / (100.0/x.length));
		
		
		for (int i = 0; i < 3; i++){
				
			double partialLeftX;
			double partialRightX;
			double leftEval;
			double rightEval;
			
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
			if (leftEval == rightEval && leftEval >= currentEval)
			{
				//roll dice to decide which way to go so as to avoid direction bias
				double rand = Math.random() * 100;
				if (rand < 50){
					return climb (fn, leftX, stepsize, iteration);
				}
				else {
					return climb (fn, rightX, stepsize, iteration);
				}
			}
			else
			{
				//left is better than current and right
				if (leftEval > currentEval && leftEval > rightEval)
				{
					return climb (fn, leftX, stepsize, iteration);
				}
				//right is better than current and left
				else if (rightEval > currentEval && leftEval < rightEval)
				{
					return climb (fn, rightX, stepsize, iteration);
				}
				//current is better than both
				else
				{
					//try changing other parameters
					switch (paramToChange) {
		            	case 0:  
		            		paramToChange = 1 + (int) (rand_1 / (100.0/(x.length -1)));
		                     break;	
		            	case 1:  
		            		paramToChange = (int) (rand_1 / (100.0/(x.length -1)));
		            		if (paramToChange == 1) {paramToChange++;}
		                     break;
		            	case 2:  
		            		paramToChange = (int) (rand_1 / (100.0/(x.length -1)));
		            		if (paramToChange == 2) {paramToChange--;}	            		
		                     break;
					}
				}
			}
		}
		
		Answer ans = new Answer(x, currentEval, iteration);
		
		return ans;
	}
	
	
	//gives the probability of the Boltzmann distribution
	private static double calculateP(double deltaT, double temp)
	{
		//System.out.println((Math.exp(deltaT/temp)) *100);
		return (Math.exp(deltaT/temp)) * 100;
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

//	//CODE PARTLY COPIED FROM: https://docs.oracle.com/javase/tutorial/essential/io/file.html
//	private static void createCSV(Answer[][] ansTable, String path, String header)
//	{
//	    // Convert the string to a
//	    // byte array.
//	    String s = header;
//	    byte data[] = s.getBytes();
//	    Path p = Paths.get(path);
//
//	    try (OutputStream out = new BufferedOutputStream(
//	      Files.newOutputStream(p, CREATE, APPEND))) {
//	      out.write(data, 0, data.length);
//	      for (int i = 0; i < ansTable.length; i++)
//	      {
//	    	  s = "" + i + ",";
//    		  data = s.getBytes(); 
//    		  out.write(data, 0, data.length);
//	    	  for (Answer ans : ansTable[i])
//	    	  {
//	    		  s = "Y = " + Math.round(ans.getY() * 100.0) / 100.0  + "  X = " + Math.round(ans.getX()*100.0)/100.0 + "  iterations = " + ans.getI() + ",";
//	    		  data = s.getBytes(); 
//	    		  out.write(data, 0, data.length);
//	    	  }
//	    	  s = "\n";
//    		  data = s.getBytes(); 
//    		  out.write(data, 0, data.length);
//	      }
//	    } catch (IOException x) {
//	      System.err.println(x);
//	    }
//	}
}
