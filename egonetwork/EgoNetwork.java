package egonetwork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

public class EgoNetwork {
	
	private String ego;
	//For the similarity matrix
	private int egoIndex;
	//intra-object indices for each person corresponding with outside labels
	private String[] indices;
	//Would adjacency lists be better?
	private int[][] adjMatrix;
	//Similarity of features
	//Has ego as last element
	private double[][][] simMatrix;
	
	public EgoNetwork(String ego, HashMap<String, Integer> indices, HashMap<String, ArrayList<String>> adjLists){
		
		this.ego = ego;
		//Invert the hashmap to an array where the external indices are indexed by internal indices
		this.indices = new String[indices.size()];
		Iterator<String> iterator = indices.keySet().iterator();
		while(iterator.hasNext()){
			
			String next = iterator.next();
			this.indices[indices.get(next).intValue()] = next;
			
		}
		//Construct the adjacency matrix
		adjMatrix = new int[this.indices.length][this.indices.length];
		iterator = adjLists.keySet().iterator();
		while(iterator.hasNext()){
			
			String next = iterator.next();
			Iterator<String> iterator2 = adjLists.get(next).iterator();
			while(iterator2.hasNext())
				adjMatrix[indices.get(next).intValue()][indices.get(iterator2.next()).intValue()] = 1;
			
		}
		
		simMatrix = null;
		
	}
	
	public void addFeatures(TreeSet<String> egoFeatures, HashMap<String, TreeSet<String>> featureList, ProfileSimilarity sim){
		
		//This entire method is meant to convert features into a similarity matrix on the egoNetwork
		
		//Reproduce HashMap between strings and indices
		HashMap<String, Integer> indices = reverseIndices();
		
		egoIndex = adjMatrix.length;
		simMatrix = new double[adjMatrix.length+1][adjMatrix[0].length+1][];
		Iterator<String> iterator = featureList.keySet().iterator();
		
		while(iterator.hasNext()){
			
			String nextI = iterator.next();
			int i = indices.get(nextI);
			Iterator<String> iterator2 = featureList.keySet().iterator();
			
			while(iterator2.hasNext()){
				
				String nextJ = iterator2.next();
				int j = indices.get(nextJ);
				//We don't assume similarity is symmetric -- because, who knows? There are people with weird fetishes.
				simMatrix[i][j] = sim.similarity(featureList.get(nextI), featureList.get(nextJ));
				
			}
			
		}
			
		for(int i = 0; i < simMatrix.length - 1; i++){
			
			simMatrix[egoIndex][i] = sim.similarity(egoFeatures, featureList.get(this.indices[i]));
			simMatrix[i][egoIndex] = sim.similarity(featureList.get(this.indices[i]), egoFeatures);
					
		}
		simMatrix[egoIndex][egoIndex] = sim.similarity(egoFeatures, egoFeatures);
		
	}
	
	public double[] getSimilarityVector(int from, int to){
		
		return simMatrix[from][to];
		
	}
	
	public HashMap<String, Integer> reverseIndices(){
		
		HashMap<String, Integer> indices = new HashMap<String, Integer>();
		for(int i = 0; i < this.indices.length; i++)
			indices.put(this.indices[i], i);
		
		return indices;
		
	}
	
	public double[][] constructCovMatrix(){
		
		double[] means = new double[adjMatrix.length];
		for(int i = 0; i < means.length; i++){
			
			for(int j = 0; j < adjMatrix.length; j++)
				means[i] += ((double)adjMatrix[j][i])/((double)adjMatrix.length);
			
		}
		
		double[][] covMatrix = new double[adjMatrix.length][adjMatrix[0].length];
		for(int i = 0; i < covMatrix.length; i++){
			
			for(int j = 0; j < covMatrix[i].length; j++){
				
				for(int k = 0; k < adjMatrix.length; k++)
					covMatrix[i][j] += (((double)adjMatrix[k][i]) - means[i])*(((double)adjMatrix[k][j]) - means[j])/((double)adjMatrix.length);
				
			}
			
		}
		
		return covMatrix;
		
	}
	
	public String getEgo(){
		
		return ego;
		
	}
	
	public int[][] getAdjMatrix(){
		
		return adjMatrix;
		
	}
	
	public boolean getEdge(int from, int to){
		
		return (adjMatrix[from][to] != 0);
		
	}
	
	public int numVertices(){
		
		return indices.length;
		
	}
	
	public int numEdges(){
		
		int sum = 0;
		for(int i = 0; i < adjMatrix.length; i++){
			
			for(int j = 0; j < adjMatrix.length; j++){
				
				if(adjMatrix[i][j] != 0)
					sum += adjMatrix[i][j]/adjMatrix[i][j];
				
			}
			
		}
		return sum;
			
	}
	
	public void printAdjLists(){
		
		for(int i = 0; i < adjMatrix.length; i++){
			
			System.out.print(indices[i] + ": ");
			
			for(int j = 0; j < adjMatrix[i].length; j++){
				
				if(adjMatrix[i][j] > 0)
					System.out.print(indices[j] + " ");
				
			}
			
			System.out.println();
			
		}
		
	}
	
}
