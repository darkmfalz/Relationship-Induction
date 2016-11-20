package egonetwork;

import java.util.ArrayList;

public interface ProfileSimilarity {

	//returns similarity on two users
	public double[] similarity(ArrayList<String> profile1, ArrayList<String> profile2);
	
}
