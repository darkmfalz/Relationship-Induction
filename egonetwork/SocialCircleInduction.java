package egonetwork;

import java.util.ArrayList;
import java.util.TreeSet;

public class SocialCircleInduction {
	
	public static String[][] McAuleyInduction(EgoNetwork egoNetwork){
		
		int numCircles = hierarchicalInduction(egoNetwork).length;
		System.out.println(numCircles);
		
		return null;
		
	}

	public static String[][] hierarchicalInduction(EgoNetwork egoNetwork){
		
		//determine the number of circles by selecting the model with the least estimated BIC
		//Estimate likelihood by hierarchical clustering on graph
		//Determine post-priori probabilities equal to average degree in-cluster divided by edges in cluster
		//Determine likelihood based on those post-priori probabilities (e.g. P(e|t_k) = P(e_1)*...*P(e_|E|))
		//So -- what if we modelled it by doing the stupidest hierarchical agglomerative clustering?
		//i.e. make a complete graph, rank the edges based on vertex similarity in original graph
		//add edges dependent on rank, forming clusters by cliques, until you get the number of clusters you want
		//Do we want to cluster by similarity vector or just adjacency vector?
		
		//Okay, let's first form the hierarchy
		//We'll cluster by cosine similarity
				
		int[][] circles = null;
		ArrayList<TreeSet<Integer>> hierarchy = new ArrayList<TreeSet<Integer>>();
		for(int i = 0; i < egoNetwork.numVertices(); i++){
			
			TreeSet<Integer> set = 
			
		}
		double minBIC = Double.MAX_VALUE;
		double numEdges = (double)egoNetwork.numEdges();
		for(int i = egoNetwork.numVertices() - 1; i > 0; i--){
			
			String[][] prospCircles = new String[i][];
			
			double bic = 2.0*((double) i)*Math.log(numEdges);
			double logLikelihood = 0.0;
			bic -= 2.0*logLikelihood;
			if(bic < minBIC)
				circles = prospCircles;
			
		}
		
		return null;
	}
	
	public static String[][] spectralInduction(EgoNetwork egoNetwork){
		
		return null;
		
	}
	
}
