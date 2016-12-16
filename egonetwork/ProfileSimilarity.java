package egonetwork;

import java.util.TreeSet;

public interface ProfileSimilarity {

	//returns similarity on two users
	public double[] similarity(TreeSet<String> profile1, TreeSet<String> profile2);
	
}
