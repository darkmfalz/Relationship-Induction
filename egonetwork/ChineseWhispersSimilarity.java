package egonetwork;

import java.util.Iterator;
import java.util.TreeSet;
import org.apache.commons.math3.linear.*;

public class ChineseWhispersSimilarity implements ProfileSimilarity {

	//nx7 matrix representing a reduced eigenbasis
	//That is, it's the transpose of the eigenbasis formed from row vectors of the eigenvectors
	private double[][] eigenbasis;
	private int reducedSize = 7;
	
	public ChineseWhispersSimilarity(EgoNetwork egoNetwork){
		
		//get covariance matrix as a double[][] cov
		double[][] cov = new double[egoNetwork.numVertices()][egoNetwork.numVertices()];
		int[][] adjMatrix = egoNetwork.getAdjMatrix();
		double[] means = new double[adjMatrix.length];
		for(int i = 0; i < adjMatrix.length; i++){
			
			double sum = 0.0;
			for(int j = 0; j < adjMatrix[i].length; j++)
				sum += (double) adjMatrix[i][j];
			means[i] = sum/((double) adjMatrix.length);
			
		}
		for(int i = 0; i < adjMatrix.length; i++){
			
			for(int j = i; j < adjMatrix.length; j++){
				
				double sum = 0.0;
				for(int k = 0; k < adjMatrix.length; k++)
					sum += (double) adjMatrix[k][i] * adjMatrix[k][j];
				double covariance = sum/((double) adjMatrix.length) - means[i] * means[j];
				cov[i][j] = covariance;
				cov[j][i] = covariance;
				
			}
			
		}
		Array2DRowRealMatrix covMatrix = new Array2DRowRealMatrix(cov);
		EigenDecomposition eigen = new EigenDecomposition(covMatrix);
		eigenbasis = new double[adjMatrix.length][reducedSize];
		for(int i = 0; i < reducedSize; i++){
			
			double[] eigenvector = eigen.getEigenvector(i).toArray();
			for(int j = 0; j < eigenvector.length; j++)
				eigenbasis[j][i] = eigenvector[j];
			
		}
		
	}
	
	public TreeSet<String> chineseWhispersVector(EgoNetwork egoNetwork, int v){
		
		TreeSet<String> vector = new TreeSet<String>();
		int[][] adjMatrix = egoNetwork.getAdjMatrix();
		double[] adjList = new double[adjMatrix.length];
		for(int i = 0; i < adjMatrix.length; i++)
			adjList[i] = (double) adjMatrix[v][i];
		double[] pc = new double[reducedSize];
		for(int i = 0; i < reducedSize; i++){
			
			for(int k = 0; k < adjMatrix.length; k++){
				
				pc[i] += adjList[k] * eigenbasis[k][i];
				
			}
			vector.add(i + " " + pc[i]);
			
		}
		
		return vector;
		
	}
	
	@Override
	public double[] similarity(TreeSet<String> profile1, TreeSet<String> profile2) {
		
		double[] sim = new double[profile1.size() + 1];
		Iterator<String> iterator = profile1.iterator();
		Iterator<String> iterator2 = profile2.iterator();
		int i = 0;
		while(iterator.hasNext() && iterator2.hasNext()){
			
			double feature1 = Double.parseDouble(iterator.next().split(" ")[1]);
			double feature2 = Double.parseDouble(iterator2.next().split(" ")[1]);
			sim[i++] = Math.abs(feature1 - feature2);
			
		}
		sim[profile1.size()] = 1.0;
		return sim;
		
	}

}
