package student_player.mytools;

import static java.nio.file.StandardOpenOption.*;
import java.nio.file.*;
import java.io.*;

public class Climber {
	//defines how many times the algorithm will iterate if no change in evaluation is made that is superior to minDiff
	private static int initNumPatienceTokens = 20;
	
	//defines the minimal absolute difference between two steps so that patience is refreshed
	private static double minDiff = 0.01;
	
	//Starting temperature
	private static double startTemp = 100;
	
	//factor to multiply temperature by at each iteration
	private static double alpha = 0.85;
	
	public static void execute()
	{
		double stepsize = 0.01;
		Answer[][] tableOutput = new Answer[11][10]; 
		Answer result;
		
		//Iterate some Hill climbing algorithms
		for (int i = 0; i < 10; i++) {
			for (int x = 0; x < 11; x++) {
				result = climb((double) x, stepsize, 0);
				tableOutput[x][i] = result;
			} 
			stepsize += 0.01;
		}
		
		//Save output to file for better viewing
		String header =  "X,0.01,0.02,0.03,0.04,0.05,0.06,0.07,0.08,0.09,0.1\n";
		createCSV(tableOutput, "hillclimbing.csv", header);		
		
		//Since 0.1 was the best stepsize when taking into account the number of iterations required to achieve answer,
		//only use this stepsize for simulated annealing
		stepsize = 0.10;
		
		//Iterate for Simulated annealing
		tableOutput = new Answer[11][3];
		
		//for initial temp between 10 and 100
		for (int i = 1; i <= 3; i ++)
		{
			//for alpha = 0.95, 0.9 and 0.85
			for (int j = 0; j < 3; j++) 
			{
				//for x between 0 and 10
				for (int x = 0; x < 11; x++) {
					result = annealClimb((double) x, stepsize, 0, initNumPatienceTokens, startTemp * i, alpha + (j*0.05));
					tableOutput[x][j] = result;
		
				}
			}
			
			header =  "T_0 = " + startTemp * i + ",Alpha\nX,0.85, 0.90, 0.95\n";
			//Save output to file for better viewing
			createCSV(tableOutput, "simulated_annealing.csv", header);
		}
	}

	
	private static double fn (double x)
	{
		//return the lowest possible value if x = 0, since function is undefined for this value
		//this ensures we will consider it as an undesirable stopping point
		if (x == 0) {return -Integer.MAX_VALUE;}
		double y = (Math.sin((x*x)/2))/Math.sqrt(x);
		return y;
	}
	
	private static Answer annealClimb(double x, double stepsize, int iteration, int patienceTokens, double temp, double tReductionFactor)
	{
		double currentEval = fn(x);
		iteration++;
		double leftEval;
		double rightEval;
		
		if (patienceTokens == 0) {return new Answer(x, currentEval, iteration);}
		
		double leftX;
		double rightX;
		
		//Make sure we stay within bounds
		if (x - stepsize >= 0)
		{
			leftX = x - stepsize;
			leftEval = fn(x - stepsize);
		}
		else
		{
			leftX = x;
			leftEval = currentEval;
		}
		
		if (x + stepsize <= 10)
		{
			rightX = x + stepsize;
			rightEval = fn(x + stepsize);
			
		}
		else
		{
			rightX = x;
			rightEval = currentEval;
		}
		
		//roll dice to decide which way to go
		double rand = Math.random() * 100;
		
		//use left neighbour
		if (rand < 50){
			
			double newX = leftX;
			
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
						return annealClimb(newX, stepsize, iteration, initNumPatienceTokens, temp * tReductionFactor, tReductionFactor);
					}
					//otherwise reduce patience by 1
					else
					{
						return annealClimb(newX, stepsize, iteration, patienceTokens - 1, temp * tReductionFactor, tReductionFactor);
					}
				}
				//Stay here, repeat algorithm and reduce patience
				else
				{
					return annealClimb(x, stepsize, iteration, patienceTokens - 1, temp * tReductionFactor, tReductionFactor);
				}
				
			}
			//if improvement, just go there
			else {
				//if difference is enough, refresh patience
				if (deltaT > minDiff)
				{
					return annealClimb(newX, stepsize, iteration, initNumPatienceTokens, temp * tReductionFactor, tReductionFactor);
				}
				//otherwise reduce patience by 1
				else
				{
					return annealClimb(newX, stepsize, iteration, patienceTokens - 1, temp * tReductionFactor, tReductionFactor);
				}
			}
		}
		//use right neighbour
		else {
			double deltaT = rightEval - currentEval;
			double newX = rightX;
			
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
						return annealClimb(newX, stepsize, iteration, initNumPatienceTokens, temp * tReductionFactor, tReductionFactor);
					}
					//otherwise reduce patience by 1
					else
					{
						return annealClimb(newX, stepsize, iteration, patienceTokens - 1, temp * tReductionFactor, tReductionFactor);
					}
				}
				//Stay here, repeat algorithm and reduce patience
				else
				{
					return annealClimb(x, stepsize, iteration, patienceTokens - 1, temp * tReductionFactor, tReductionFactor);
				}
				
			}
			//if improvement, just go there
			else {
				//if difference is enough, refresh patience
				if (deltaT > minDiff)
				{
					return annealClimb(newX, stepsize, iteration, initNumPatienceTokens, temp * tReductionFactor, tReductionFactor);
				}
				//otherwise reduce patience by 1
				else
				{
					return annealClimb(newX, stepsize, iteration, patienceTokens - 1, temp * tReductionFactor, tReductionFactor);
				}
			}
		}
	}
	
	private static Answer climb(double x, double stepsize, int iteration)
	{
		double currentEval = fn(x);
		iteration++;
		double leftEval;
		double rightEval;
		
		//Make sure we stay within bounds
		if (x - stepsize >= 0)
		{
			leftEval = fn(x - stepsize);
		}
		else
		{
			leftEval = currentEval;
		}
		
		if (x + stepsize <= 10)
		{
			rightEval = fn(x + stepsize);
		}
		else
		{
			rightEval = currentEval;
		}
		
		//if both directions are just as good (and better than current)
		if (leftEval == rightEval && leftEval > currentEval)
		{
			//roll dice to decide which way to go so as to avoid direction bias
			double rand = Math.random() * 100;
			if (rand < 50){
				return climb (x - stepsize, stepsize, iteration);
			}
			else {
				return climb (x + stepsize, stepsize, iteration);
			}
		}
		else
		{
			//left is better than current and right
			if (leftEval > currentEval && leftEval > rightEval)
			{
				return climb (x - stepsize, stepsize, iteration);
			}
			//right is better than current and left
			else if (rightEval > currentEval && leftEval < rightEval)
			{
				return climb (x + stepsize, stepsize, iteration);
			}
			//current is better than both
			else
			{
				Answer ans = new Answer(x, currentEval, iteration);
				return ans;
			}
		}
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
		private double x;
		private double y;
		private int i;
		public Answer(double px, double py, int pi){
			x = px;
			y = py;
			i = pi;
		}
		public double getX() {
			return x;
		}
	
		public double getY() {
			return y;
		}
		
		public int getI() {
			return i;
		}

	}

	//CODE PARTLY COPIED FROM: https://docs.oracle.com/javase/tutorial/essential/io/file.html
	private static void createCSV(Answer[][] ansTable, String path, String header)
	{
	    // Convert the string to a
	    // byte array.
	    String s = header;
	    byte data[] = s.getBytes();
	    Path p = Paths.get(path);

	    try (OutputStream out = new BufferedOutputStream(
	      Files.newOutputStream(p, CREATE, APPEND))) {
	      out.write(data, 0, data.length);
	      for (int i = 0; i < ansTable.length; i++)
	      {
	    	  s = "" + i + ",";
    		  data = s.getBytes(); 
    		  out.write(data, 0, data.length);
	    	  for (Answer ans : ansTable[i])
	    	  {
	    		  s = "Y = " + Math.round(ans.getY() * 100.0) / 100.0  + "  X = " + Math.round(ans.getX()*100.0)/100.0 + "  iterations = " + ans.getI() + ",";
	    		  data = s.getBytes(); 
	    		  out.write(data, 0, data.length);
	    	  }
	    	  s = "\n";
    		  data = s.getBytes(); 
    		  out.write(data, 0, data.length);
	      }
	    } catch (IOException x) {
	      System.err.println(x);
	    }
	}
}
