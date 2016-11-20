package ego.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class EgoNetwork {
	
	private int ego;
	//intra-object indices for each person corresponding with outside labels
	private int[] indices;
	//Would adjacency lists be better?
	private int[][] adjMatrix;
	
	public EgoNetwork(int ego, HashMap<Integer, Integer> indices, HashMap<Integer, ArrayList<Integer>> adjLists){
		
		this.ego = ego;
		//Invert the hashmap to an array where the external indices are indexed by internal indices
		this.indices = new int[indices.size()];
		Iterator<Integer> iterator = indices.keySet().iterator();
		while(iterator.hasNext()){
			
			Integer next = iterator.next();
			this.indices[indices.get(next).intValue()] = next;
			
		}
		//Construct the adjacency matrix
		adjMatrix = new int[this.indices.length][this.indices.length];
		iterator = adjLists.keySet().iterator();
		while(iterator.hasNext()){
			
			Integer next = iterator.next();
			Iterator<Integer> iterator2 = adjLists.get(next).iterator();
			while(iterator2.hasNext())
				adjMatrix[indices.get(next).intValue()][indices.get(iterator2.next()).intValue()] = 1;
			
		}
		
	}
	
	public int getEgo(){
		
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
