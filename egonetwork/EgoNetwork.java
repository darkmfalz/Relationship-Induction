package egonetwork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
	
	public void addFeatures(ArrayList<String> egoFeatures, HashMap<String, ArrayList<String>> featureList, ProfileSimilarity sim){
		
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
				
				String nextJ = iterator.next();
				int j = indices.get(nextJ);
				//We don't assume similarity is symmetric -- because, who knows? There are people with weird fetishes.
				simMatrix[i][j] = sim.similarity(featureList.get(nextI), featureList.get(nextJ));
				
			}
			
		}
			
		for(int i = 0; i < simMatrix.length; i++){
			
			simMatrix[egoIndex][i] = sim.similarity(egoFeatures, featureList.get(this.indices[i]));
			simMatrix[i][egoIndex] = sim.similarity(featureList.get(this.indices[i]), egoFeatures);
					
		}
		
	}
	
	public HashMap<String, Integer> reverseIndices(){
		
		HashMap<String, Integer> indices = new HashMap<String, Integer>();
		for(int i = 0; i < this.indices.length; i++)
			indices.put(this.indices[i], i);
		
		return indices;
		
	}
	
	public String getEgo(){
		
		return ego;
		
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
