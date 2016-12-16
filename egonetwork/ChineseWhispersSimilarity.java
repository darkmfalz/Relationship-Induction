package egonetwork;

import java.util.Iterator;
import java.util.TreeSet;

public class ChineseWhispersSimilarity implements ProfileSimilarity {

	public TreeSet<String> chineseWhispersVector(EgoNetwork egoNetwork, int v){
		
		TreeSet<String> vector = new TreeSet<String>();
		int[][] adjMatrix = egoNetwork.getAdjMatrix();
		for(int i = 0; i < adjMatrix.length; i++)
			vector.add(i + " " + adjMatrix[v][i]);
		return vector;
		
	}
	
	@Override
	public double[] similarity(TreeSet<String> profile1, TreeSet<String> profile2) {
		
		double[] sim = new double[profile1.size() + 1];
		Iterator<String> iterator = profile1.iterator();
		Iterator<String> iterator2 = profile2.iterator();
		int i = 0;
		while(iterator.hasNext() && iterator2.hasNext()){
			
			int feature1 = Integer.parseInt(iterator.next().split(" ")[1]);
			int feature2 = Integer.parseInt(iterator2.next().split(" ")[1]);
			sim[i++] = (feature1 == feature2 ? 0.0 : -1.0);
			
		}
		sim[profile1.size()] = 1.0;
		return sim;
		
	}

}
