package egonetwork;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeSet;

public class SocialCircleInduction {
	
	public static String[][] McAuleyInduction(EgoNetwork egoNetwork){
		
		int numCircles = 0;
		double bicMin = Double.POSITIVE_INFINITY;
		Random random = new Random();
		//Too inconsistent
		for(int a = 2; a < egoNetwork.numVertices(); a++){
			
			//We need to have some good initialization on the parameters
			//So, let's just call t_k the inner product of the profile and theta
			//We want to select t_k
			//Let's build clusters in a k-means way
			//The method is detailed here: http://stats.stackexchange.com/questions/30723/initializing-k-means-clustering
			TreeSet<Integer> initialCenters = new TreeSet<Integer>();
			while(initialCenters.size() < a)
				initialCenters.add(random.nextInt(egoNetwork.numVertices()));
			for(int i = 0; i < egoNetwork.numVertices(); i++){                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
				
				if(!initialCenters.contains(i)){
					
					Iterator<Integer> iterator = initialCenters.iterator();
					int[] indices = new int[a];
					//Distances from vertex i
					double[] distances = new double[a];
					//The index of one of the two centers closest to each other that is closest to vertex i
					int minDistanceClosest = Integer.MAX_VALUE;
					//The minimum distance between centers
					double minDistance = Double.POSITIVE_INFINITY;
					//The center closest to vertex i
					int closestCenter = -1;
					//The distance to this center
					double closestCenterDistance = Double.POSITIVE_INFINITY;
					int index = 0;
					while(iterator.hasNext()){
						
						int next = iterator.next();
						indices[index] = next;
						//Check if this center is the closest one
						distances[index] = dotProduct(egoNetwork.getSimilarityVector(i, next), egoNetwork.getSimilarityVector(i, next));
						if(distances[index] < closestCenterDistance){
							
							closestCenter = next;
							closestCenterDistance = distances[index];
							
						}
						
						//Extract the closest two centers
						Iterator<Integer> iterator2 = initialCenters.iterator();
						while(iterator2.hasNext()){
							
							int next2 = iterator2.next();
							double distance = dotProduct(egoNetwork.getSimilarityVector(next2, next), egoNetwork.getSimilarityVector(next2, next));
							if(next != next2 && distance < minDistance){
								
								minDistance = distance;
								//Select the closer of the two centers
								if(dotProduct(egoNetwork.getSimilarityVector(i, next), egoNetwork.getSimilarityVector(i, next))
										< dotProduct(egoNetwork.getSimilarityVector(i, next2), egoNetwork.getSimilarityVector(i, next2)))
									minDistanceClosest = next;
								else
									minDistanceClosest = next2;
								
							}
							
						}
						index++;
						
					}
					
					//Update the closest center distance, if possible
					if(closestCenterDistance > minDistance){
						
						initialCenters.remove(minDistanceClosest);
						initialCenters.add(i);
						
					}
					else{
						
						double secondDistance = Double.POSITIVE_INFINITY;
						iterator = initialCenters.iterator();
						while(iterator.hasNext()){
							
							int next = iterator.next();
							if(next != closestCenter){
								int index1 = 0;
								for(int b = 0; b < indices.length; b++){
									
									if(indices[b] == next)
										index1 = b;
									
								}
								if(distances[index1] < secondDistance)
									secondDistance = distances[index1];
								
							}
							
						}
						
						//Extract distances on the closest center
						iterator = initialCenters.iterator();
						double closestCenterMinDistance = Double.POSITIVE_INFINITY;
						while(iterator.hasNext()){
							
							int next = iterator.next();
							double distance = dotProduct(egoNetwork.getSimilarityVector(closestCenter, next), egoNetwork.getSimilarityVector(closestCenter, next));
							closestCenterMinDistance = Math.min(distance, closestCenterMinDistance);
							
						}
						
						if(secondDistance > minDistance + closestCenterMinDistance){
							
							initialCenters.remove(closestCenter);
							initialCenters.add(i);
							
						}
						
					}
					
				}
				
			}
			//Expand the centers into clusters
			//OR use the centers as theta?
			Integer[][] currCircles = new Integer[a][];
			HashMap<Integer, TreeSet<Integer>> tempCircles = new HashMap<Integer, TreeSet<Integer>>();
			TreeSet<Integer> assigned = new TreeSet<Integer>();
			
			while(assigned.size() < egoNetwork.numVertices()){
				
				Iterator<Integer> iterator = initialCenters.iterator();
				while(iterator.hasNext()){
					
					int next = iterator.next();
					if(!tempCircles.containsKey(next)){
						TreeSet<Integer> branch = new TreeSet<Integer>();
						branch.add(next);
						tempCircles.put(next, branch);
					}
					int index = -1;
					double distance = Double.POSITIVE_INFINITY;
					for(int i = 0; i < egoNetwork.numVertices(); i++){
						
						if(!tempCircles.get(next).contains(i) && distance > dotProduct(egoNetwork.getSimilarityVector(next, i), egoNetwork.getSimilarityVector(next, i))){
							
							index = i;
							distance = dotProduct(egoNetwork.getSimilarityVector(next, i), egoNetwork.getSimilarityVector(next, i));
							
						}
						
					}
					tempCircles.get(next).add(index);
					assigned.add(index);
					
				}
				
			}
			
			Iterator<Integer> iterator = initialCenters.iterator();
			int index = 0;
			double[][] theta = new double[a][];
			
			while(iterator.hasNext()){
				int next = iterator.next();
				currCircles[index] = tempCircles.get(next).toArray(new Integer[0]);
				theta[index] = egoNetwork.getSimilarityVector(next, egoNetwork.numVertices());
				index++;
			}
			
			double[] alpha = new double[a];
			for(int i = 0; i < alpha.length; i++)
				alpha[i] = 1.0;
			
			double bic = -2.0*lTheta(egoNetwork, currCircles, theta, alpha) + a*Math.log(egoNetwork.numEdges());
			
			if(bic < bicMin){
				
				bicMin = bic;
				numCircles = a;
				
			}
			
		}
		System.out.println(numCircles);
		
		numCircles = 10;
		return McAuleyInduction(egoNetwork, numCircles);
		//return null;
		
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
		//TODO fix the problems in this assignment -- it's functionally random, so all of them are very close
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
		
		double[][] theta = new double[numCircles][egoNetwork.getSimilarityVector(0, 1).length];
		//Initialize theta_k to the 1-vector
		//Also note: theta encodes the feature similarity parameters of a circle
		Random random = new Random();
		for(int i = 0; i < theta.length; i++)
			for(int j = 0; j < theta[i].length; j++)
				theta[i][j] = -0.5*random.nextDouble();
		
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
		
		//Optimization loop
		int convergenceStreak = 0;
		while(true){
			
			if(convergenceStreak > 2)
				break;
			
			if(circlesAreEqual(prevCircles, currCircles))
				convergenceStreak++;
			else
				convergenceStreak = 0;
			
			//Optimize Theta -- this is the pairs of theta vectors and alpha scalars
			//Here we use gradient descent
			
			//Initialize some variables
			alphaPrev = new double[numCircles];
			for(int i = 0; i < alphaPrev.length; i++)
				alphaPrev[i] = 0.0;
			thetaPrev = new double[numCircles][egoNetwork.getSimilarityVector(0, 1).length];
			for(int i = 0; i < thetaPrev.length; i++)
				for(int j = 0; j < thetaPrev[i].length; j++)
					thetaPrev[i][j] = 0.0;
			double precision = 0.1;
			//Initialize step size with something RIDICULOUS
			double gamma = 100.0;
			//Perform gradient descent
			while(distanceOnParameters(theta, alpha, thetaPrev, alphaPrev) > precision){
				
				//Calculate value of function
				double f = lTheta(egoNetwork, currCircles, theta, alpha) - omega(theta);
				//Get partial derivatives
				double[][] gradientTheta = gradientTheta(egoNetwork, currCircles, theta, alpha);
				double[] gradientAlpha = gradientAlpha(egoNetwork, currCircles, theta, alpha);
				//Select gamma
				double c = 0.5;
				double tau = 0.5;
				double m = dotProduct(gradientAlpha, gradientAlpha);
				for(int i = 0; i < gradientTheta.length; i++)
					m += dotProduct(gradientTheta[i], gradientTheta[i]);
				//The magnitude of the gradient
				m = Math.sqrt(m);
				while((lTheta(egoNetwork, currCircles, vectorSum(theta, vectorScalarMultiple(gradientTheta, gamma/m)), vectorSum(alpha, vectorScalarMultiple(gradientAlpha, gamma/m))) - omega(vectorSum(theta, vectorScalarMultiple(gradientTheta, gamma/m)))) - f < gamma*c*m)
					gamma = gamma*tau;
				thetaPrev = theta;
				theta = vectorSum(theta, vectorScalarMultiple(gradientTheta, gamma/m));
				alphaPrev = alpha;
				alpha = vectorSum(alpha, vectorScalarMultiple(gradientAlpha, gamma/m));
				
			}
			System.out.println(lTheta(egoNetwork, currCircles, theta, alpha) - omega(theta));
			
			//CLAIM: each circle is independent of each other and dependent only on the optimal theta
			//CLAIM: we can use Lloyd's algorithm to optimize a single cluster
			prevCircles = currCircles;
			//A working copy of prevCircles that we can alter during the expectation-maximization
			Integer[][] workingCircles = new Integer[numCircles][];
			for(int i = 0; i < numCircles; i++)
				workingCircles[i] = prevCircles[i];
			currCircles = new Integer[numCircles][];
			for(int i = 0; i < numCircles; i++)
				currCircles[i] = prevCircles[i];
			
			//Expectation-Maximization some number of times -- change to convergence condition later?
			for(int n = 0; n < 30; n++){
				
				//Iterate over all clusters
				for(int k = 0; k < numCircles; k++){
					
					TreeSet<Integer> circle = new TreeSet<Integer>(Arrays.asList(currCircles[k]));
					//Iterate over all vertices
					for(int v = 0; v < egoNetwork.numVertices(); v++){
						
						double delta = 0.0;
						
						//Determine if v is in circle k or not
						boolean vIn = false;
						for(int j = 0; j < currCircles[k].length; j++){
							
							if(currCircles[k][j] == v)
								vIn = true;
							
						}
						
						//Iterate over all edges on v
						for(int u = 0; u < egoNetwork.numVertices(); u++){
							
							
							boolean uIn = false;
							for(int j = 0; j < currCircles[k].length; j++){
								
								if(currCircles[k][j] == u)
									uIn = true;
								
							}
							
							//The only interesting delta are when vertex u is in the cluster
							if(uIn){
							
								double dotProduct = dotProduct(egoNetwork.getSimilarityVector(v, u), theta[k]);
								//Iterate over all circles to get the constant
								double constant = 0.0;
								for(int i = 0; i < numCircles; i++){
									
									//We don't want to get the value on the current cluster
									if(i == k)
										i++;
									if(i >= numCircles)
										break;
									
									boolean vInI = false;
									boolean uInI = false;
									
									for(int j = 0; j < currCircles[i].length; j++){
										
										if(currCircles[i][j] == v)
											vInI = true;
										if(currCircles[i][j] == u)
											uInI = true;
										
									}
									
									if(vInI && uInI)
										constant += dotProduct(egoNetwork.getSimilarityVector(v, u), theta[i]);
									else
										constant += -1.0 * alpha[i] * dotProduct(egoNetwork.getSimilarityVector(v, u), theta[i]);
									
								}
								
								//Now we determine the change in l_\theta
								if(vIn){
									
									if(egoNetwork.getEdge(v, u))
										delta -= (alpha[k] + 1.0)*dotProduct;
									
									delta += Math.log(1 + Math.exp(constant + dotProduct))
											- Math.log(1 + Math.exp(constant + -1.0 * alpha[k] * dotProduct));
									
								}
								else{
									
									if(egoNetwork.getEdge(v, u))
										delta += (alpha[k] + 1.0)*dotProduct;
									
									delta -= Math.log(1 + Math.exp(constant + dotProduct))
											- Math.log(1 + Math.exp(constant + -1.0 * alpha[k] * dotProduct));
									
								}
							
							}
								
						}
						
						//If the change incurred by moving the vertex's cluster is positive
						if(delta > 0.0){
							
							if(vIn)
								circle.remove(v);
							else
								circle.add(v);
							
						}
						
					}
					
					workingCircles[k] = circle.toArray(new Integer[0]);
					
				}
				//Update currCircles with values from workingCircles
				for(int i = 0; i < numCircles; i++)
					currCircles[i] = workingCircles[i];
				
			}
			
			//System.out.println(Error.function2(prevCircles, currCircles, new HashMap<Integer, Integer>(), new HashSet<Integer>(), egoNetwork.numVertices())[1]/((double) numCircles));
			//BER
			double[] val = new double[2];
			for(int i = 0; i < currCircles.length; i++){
				
				double numMatches = 0.0;
				for(int j = 0; j < currCircles[i].length; j++){
					
					boolean match = false;
					for(int k = 0; k < prevCircles[i].length; k++){
						
						if(currCircles[i][j] == prevCircles[i][k]){
							
							match = true;
							break;
							
						}
						
					}
					if(match)
						numMatches++;
					
				}
				if(currCircles[i].length > 0 && currCircles[i].length < egoNetwork.numVertices()){
					
					val[0] += 0.5*(((double)currCircles[i].length)-numMatches)/((double)currCircles[i].length)
							+ (((double)prevCircles[i].length)-numMatches)/(((double) egoNetwork.numVertices()) - (double)currCircles[i].length);
					val[1] += 1.0 - 0.5*((((double)currCircles[i].length)-numMatches)/((double)currCircles[i].length)
							+ (((double)prevCircles[i].length)-numMatches)/(((double) egoNetwork.numVertices()) - (double)currCircles[i].length));
				
				}
				
			}
			System.out.println(val[0]/((double) numCircles));
			
		}
		
		String[][] circles = new String[numCircles][];
		for(int i = 0; i < currCircles.length; i++){
			
			circles[i] = new String[currCircles[i].length];
			for(int j = 0; j < currCircles[i].length; j++)
				circles[i][j] = egoNetwork.getName(currCircles[i][j]);
			
		}
		
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
		for(int i = 0; i < egoNetwork.numVertices(); i++){
			
			for(int j = 0; j < egoNetwork.numVertices(); j++){
				
				double bigPhi = bigPhi(egoNetwork, circles, theta, alpha, i, j);
				//If they're adjacent
				if(egoNetwork.getEdge(i, j))
					lTheta += bigPhi;
				lTheta -= Math.log(1 + Math.exp(bigPhi));
				
			}
			
		}
		
		return lTheta;
		
	}
	
	public static double omega(double[][] theta){
		
		double omega = 0.0;
		for(int i = 0; i < theta.length; i++){
			
			for(int j = 0; j < theta[i].length; j++){
				
				omega += Math.abs(theta[i][j]);
				
			}
			
		}
		return omega;
		
	}
	
	public static double[] gradientAlpha(EgoNetwork egoNetwork, Integer[][] circles, double[][] theta, double[] alpha){
		
		double[] gradientAlpha = new double[theta.length];
		int[][] adjMatrix = egoNetwork.getAdjMatrix();
		
		for(int i = 0; i < gradientAlpha.length; i++){
			
			double partial = 0.0;
			for(int j = 0; j < adjMatrix.length; j++){
				
				for(int k = 0; k < adjMatrix[j].length; k++){
					
					boolean containsJ = false;
					boolean containsK = false;
					for(int a = 0; a < circles[i].length; a++){
						
						containsJ = containsK || (circles[i][a] == j);
						containsJ = containsK || (circles[i][a] == k);
						
					}
					
					if(!containsJ || !containsK) {
						
						double dotProduct = dotProduct(egoNetwork.getSimilarityVector(j, k), theta[i]);
						double expBigPhi = Math.exp(bigPhi(egoNetwork, circles, theta, alpha, j, k));
						
						partial += dotProduct
								* expBigPhi
								/ (1 + expBigPhi);
						
						if(adjMatrix[j][k] != 0)
							partial -= dotProduct;
						
					}
					
				}
				
			}
			
			gradientAlpha[i] = partial;
			
		}
		
		return gradientAlpha;
		
	}
	
	public static double[][] gradientTheta(EgoNetwork egoNetwork, Integer[][] circles, double[][] theta, double[] alpha){
		
		double[][] gradientTheta = new double[theta.length][theta[0].length];
		int[][] adjMatrix = egoNetwork.getAdjMatrix();
		
		for(int i = 0; i < gradientTheta.length; i++){
				
			double[] partial = new double[gradientTheta[i].length];
			
			for(int j = 0; j < adjMatrix.length; j++){
				
				for(int k = 0; k < adjMatrix[j].length; k++){
					
					for(int a = 0; a < gradientTheta[i].length; a++){
						
						double component = egoNetwork.getSimilarityVector(j, k)[a];
						double expBigPhi = Math.exp(bigPhi(egoNetwork, circles, theta, alpha, j, k));
						
						partial[a] += -1.0 * dK(i, circles[i], theta, alpha, j, k)
								* component
								* expBigPhi
								/ (1 + expBigPhi);
						
						if(adjMatrix[j][k] != 0){
							
							partial[a] += dK(i, circles[i], theta, alpha, j, k)
									* component;
							
						}
						
					}
					
				}
				
			}
			
			//The partial differential on omega
			double[] omega = new double[gradientTheta[i].length];
			for(int a = 0; a < omega.length; a++)
				omega[a] = Math.signum(theta[i][a]);
			
			gradientTheta[i] = vectorSum(partial, vectorScalarMultiple(omega, -1.0));
			
		}
		
		return gradientTheta;
		
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
	
	public static double[] vectorSum(double[] vec1, double[] vec2){
		
		double[] sum = new double[vec1.length];
		for(int i = 0; i < vec1.length; i++)
			sum[i] = vec1[i] + vec2[i];
		return sum;
		
	}
	
	public static double[][] vectorSum(double[][] vec1, double[][] vec2){
		
		double[][] sum = new double[vec1.length][];
		for(int i = 0; i < vec1.length; i++)
			sum[i] = vectorSum(vec1[i], vec2[i]);
		return sum;
		
	}
	
	public static double[] vectorScalarMultiple(double[] vec, double scalar){
		
		double[] product = new double[vec.length];
		for(int i = 0; i < vec.length; i++)
			product[i] = vec[i]*scalar;
		return product;
		
	}
	
	public static double[][] vectorScalarMultiple(double[][] vec, double scalar){
		
		double[][] product = new double[vec.length][];
		for(int i = 0; i < vec.length; i++)
			product[i] = vectorScalarMultiple(vec[i], scalar);
		return product;
		
	}
	
	public static String[][] spectralInduction(EgoNetwork egoNetwork){
		
		return null;
		
	}
	
}
