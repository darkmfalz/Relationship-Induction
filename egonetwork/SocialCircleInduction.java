package egonetwork;

import java.util.Arrays;

public class SocialCircleInduction {
	
	public static String[][] McAuleyInduction(EgoNetwork egoNetwork){
		
		//TODO int numCircles = [INSERT ESTIMATION METHOD HERE];
		//Read: https://www.cs.princeton.edu/courses/archive/fall11/cos597C/lectures/mixed-membership.pdf
		//Read: http://jmlr.csail.mit.edu/papers/volume9/airoldi08a/airoldi08a.pdf
		//Read: Fuzzy membership
		int numCircles = 10;
		System.out.println(numCircles);
		
		return McAuleyInduction(egoNetwork, numCircles);
		
	}
	
	public static String[][] McAuleyInduction(EgoNetwork egoNetwork, int numCircles){
		
		//|V||C| space-complexity for storing circle membership -- bad time comparison, as well: |V||C|
		//Storing each circle as a set of vertices is O(|V||C|). Time for comparison between sets of circles? Well, O(|V||C|) -- it's just a better method?
		
		//Now we initialize our variables:
		//These represent each iteration of the circles
		//Remember to order circles somehow -- by granularity, hash, whatever
		Integer[][] prevCircles = new Integer[numCircles][1];
		for(int i = 0; i < prevCircles.length; i++)
			prevCircles[i][0] = 0;
		Integer[][] currCircles = new Integer[numCircles][];
		//Just assign every vertex to a circle once
		double verticesPerCircle = ((double) egoNetwork.numVertices())/((double) numCircles);
		int currIndex = 0;
		for(int i = 0; i < currCircles.length; i++){
			
			if(((double)egoNetwork.numVertices() - currIndex) >= verticesPerCircle){
				
				currCircles[i] = new Integer[(int) Math.ceil(verticesPerCircle)];
				int j = 0;
				while(j < verticesPerCircle)
					currCircles[i][j++] = currIndex++;
				
			}
			else{
				
				//If there are no more vertices to be assigned, it's an empty circle
				currCircles[i] = new Integer[egoNetwork.numVertices() - currIndex];
				int j = 0;
				while(egoNetwork.numVertices() - currIndex > 0)
					currCircles[i][j++] = currIndex++;
				
			}
			
		}
		
		for(int i = 0; i < currCircles.length; i++){
			
			System.out.print(i + ": ");
			for(int j = 0; j < currCircles[i].length; j++)
				System.out.print(currCircles[i][j] + " ");
			System.out.println();
			
		}
		
		//Optimization loop
		while(!circlesAreEqual(prevCircles, currCircles)){
			
			//Optimize Theta -- this is the pairs of theta vectors and alpha scalars
			//Here we use gradient descent
			double[][] theta = new double[numCircles][egoNetwork.getSimilarityVector(0, 1).length];
			//Initialize theta_k to the 1-vector
			//Also note: theta encodes the feature similarity parameters of a circle
			for(int i = 0; i < theta.length; i++)
				for(int j = 0; j < theta[i].length; j++)
					theta[i][j] = 1.0;
			
			double[][] thetaPrev = new double[numCircles][egoNetwork.getSimilarityVector(0, 1).length];
			for(int i = 0; i < thetaPrev.length; i++)
				for(int j = 0; j < thetaPrev[i].length; j++)
					thetaPrev[i][j] = 0.0;
			
			//Initialize alpha_k to 1
			//That is, there is no modulation between the effect of superset circles and non-superset circles
			double[] alpha = new double[numCircles];
			for(int i = 0; i < alpha.length; i++)
				alpha[i] = 1.0;
			
			double[] alphaPrev = new double[numCircles];
			for(int i = 0; i < alphaPrev.length; i++)
				alphaPrev[i] = 0.0;
			
			//Initialize some variables
			double precision = 0.1;
			//Initialize step size with something RIDICULOUS
			double gamma = 10.0;
			//Perform gradient descent
			while(distanceOnParameters(theta, alpha, thetaPrev, alphaPrev) > precision){
				
				//Calculate value of function
				double f = lTheta(egoNetwork, currCircles, theta, alpha);
				//Get partial derivative
				//Select gamma
				double c = 1.0;
				break;
				
			}
			
			//Optimize Circles
			break;
			
		}
		
		//TODO Convert currCircles to circles
		String[][] circles = new String[numCircles][];
		
		return circles;
		
	}

	public static boolean circlesAreEqual(Integer[][] circles1, Integer[][] circles2){
		
		//This method is pretty straightforward
		if(circles1.length != circles2.length)
			return false;
		
		for(int i = 0; i < circles1.length; i++){
			
			if(circles1[i].length != circles2[i].length)
				return false;
			
			for(int j = 0; j < circles1[i].length; j++){
				
				if(!circles1[i][j].equals(circles2[i][j]))
					return false;
				
			}
			
		}
		
		return true;
		
	}
	
	public static double distanceOnParameters(double[][] theta, double[] alpha, double[][] thetaPrev, double[] alphaPrev){
		
		double[] vec1 = new double[theta.length*theta[0].length + alpha.length];
		double[] vec2 = new double[thetaPrev.length*thetaPrev[0].length + alphaPrev.length];
		
		for(int i = 0; i < alpha.length; i++){
			
			vec1[i] = alpha[i];
			vec2[i] = alphaPrev[i];
			
		}
		
		for(int i = 0; i < theta.length; i++){
			
			for(int j = 0; j < theta[i].length; j++){
				
				vec1[alpha.length + i*theta[i].length + j] = theta[i][j];
				vec2[alphaPrev.length + i*thetaPrev[i].length + j] = thetaPrev[i][j];
				
			}
			
		}
		
		return euclideanDistance(vec1, vec2);
		
	}
	
	public static double lTheta(EgoNetwork egoNetwork, Integer[][] circles, double[][] theta, double[] alpha){
		
		double lTheta = 0.0;
		int[][] adjMatrix = egoNetwork.getAdjMatrix();
		for(int i = 0; i < adjMatrix.length; i++){
			
			for(int j = 0; j < adjMatrix[i].length; j++){
				
				//If they're adjacent
				if(adjMatrix[i][j] != 1){
					
					lTheta += bigPhi(egoNetwork, circles, theta, alpha, i, j);
					
				}
				
				lTheta -= Math.log(1 + Math.exp(bigPhi(egoNetwork, circles, theta, alpha, i, j)));
				
			}
			
		}
		
		return lTheta;
		
	}
	
	public static double bigPhi(EgoNetwork egoNetwork, Integer[][] circles, double[][] theta, double[] alpha, int x, int y){
		
		double bigPhi = 0.0;
		for(int i = 0; i < circles.length; i++)
			bigPhi += dK(i, circles[i], theta, alpha, x, y)*dotProduct(egoNetwork.getSimilarityVector(x, y), theta[i]);	
		return bigPhi;
		
	}
	
	public static double dK(int k, Integer[] circle, double[][] theta, double[] alpha, int x, int y){
		
		boolean containsX = false;
		boolean containsY = false;
		for(int i = 0; i < circle.length; i++){
			
			containsX = containsX || (circle[i] == x);
			containsY = containsY || (circle[i] == y);
			
		}
		
		if(containsX && containsY)
			return 1.0;
		else
			return -1.0*alpha[k];
		
	}
	
	public static double euclideanDistance(double[] vec1, double[] vec2){
		
		try{
			
			double distance = 0.0;
			for(int i = 0; i < vec1.length; i++)
				distance += Math.pow(vec1[i] -  vec2[i], 2.0);
			
			return Math.sqrt(distance);
			
		}
		catch(ArrayIndexOutOfBoundsException e){
			
			System.err.println("Different length vectors, vec1.length > vec2.length: vec1 is of length " +  vec1.length + " and vec2 is of length " + vec2.length + ".");
			return Double.NaN;
			
		}
		
	}
	
	public static double dotProduct(double[] vec1, double[] vec2){
		
		try{
			
			double dotProduct = 0.0;
			for(int i = 0; i < vec1.length; i++)
				dotProduct += vec1[i]*vec2[i];
			
			return dotProduct;
			
		}
		catch(ArrayIndexOutOfBoundsException e){
			
			System.err.println("Different length vectors, vec1.length > vec2.length: vec1 is of length " +  vec1.length + " and vec2 is of length " + vec2.length + ".");
			return Double.NaN;
			
		}
		
	}
	
	public static String[][] spectralInduction(EgoNetwork egoNetwork){
		
		return null;
		
	}
	
}