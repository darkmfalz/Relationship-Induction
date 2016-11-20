package egonetwork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

public class McAuleySimilarity implements ProfileSimilarity {

	private int maxDepth;
	private HashMap<String, Integer> indices;
	
	public McAuleySimilarity(int maxDepth, ArrayList<String> egoFeatures, HashMap<String, ArrayList<String>> featureList) {
	
		this.maxDepth = maxDepth;
		indices = new HashMap<String, Integer>();
		
		//Get all possible features
		TreeSet<String> validFeatures = new TreeSet<String>();
		int actualDepth = 0;
		for(int i = 0; i < egoFeatures.size(); i++){
			
			actualDepth = Math.max(actualDepth, egoFeatures.get(i).length());
			validFeatures.add(egoFeatures.get(i));
			
		}
		
		Iterator<ArrayList<String>> featureIterator = featureList.values().iterator();
		while(featureIterator.hasNext()){
			
			ArrayList<String> next = featureIterator.next();
			for(int i = 0; i < next.size(); i++){
				
				actualDepth = Math.max(actualDepth, next.get(i).length());
				validFeatures.add(next.get(i));
				
			}
			
		}
		
		//Get all VALID features
		for(int i = actualDepth; i > maxDepth; i--){
			
			//Find offending branches with features more granular than specified
			ArrayList<String> offendingBranches = new ArrayList<String>();
			Iterator<String> iterator = validFeatures.iterator();
			while(iterator.hasNext()){
				
				String curr = iterator.next();
				if(curr.split("\\s+").length == i)
					offendingBranches.add(curr);
				
			}
			
			//Get the branch prefixes
			while(offendingBranches.size() > 0){
				
				iterator = offendingBranches.iterator();
				String next = iterator.next();
				String prefix = "";
				String[] split = next.split("\\s+");
				for(int k = 0; k < split.length - 1; k++)
					prefix = prefix + split[k];
				
				validFeatures.remove(next);
				iterator.remove();
				
				while(iterator.hasNext()){
					
					next = iterator.next();
					if(next.contains(prefix)){
						
						validFeatures.remove(next);
						iterator.remove();
						
					}
					
				}
				
				validFeatures.add(prefix);
				
			}
			
		}
		
		//Assign all indices
		Iterator<String> iterator = validFeatures.iterator();
		int i = 0;
		while(iterator.hasNext())
			indices.put(iterator.next(), i++);
		
	}

	@Override
	public double[] similarity(ArrayList<String> profile1, ArrayList<String> profile2) {
		
		HashMap<String, Boolean> simVec = new HashMap<String, Boolean>();
		int actualDepth = 0;
		//See which leaves match and which don't
		for(int i = 0; i < profile1.size(); i++){
			
			//Track the full depth of the trees being compared
			actualDepth = Math.max(actualDepth, profile1.get(i).split("\\s+").length);
			
			boolean didMatch = false;
			for(int j = 0; j < profile2.size(); j++){
				
				if(i == 0)
					actualDepth = Math.max(actualDepth, profile1.get(i).split("\\s+").length);
				
				if(profile1.get(i).equals(profile2.get(j))){
					simVec.put(profile1.get(i), true);
					didMatch = true;
					break;
				}
				
			}
			if(!didMatch)
				simVec.put(profile1.get(i), false);
			
		}
		//The constant feature in the McAuley similarity vector
		simVec.put("Adeeb", true);
		
		if(maxDepth >= 0){
			
			for(int i = actualDepth; i > maxDepth; i--){
				
				//Find offending branches
				ArrayList<String> offendingBranches = new ArrayList<String>();
				Iterator<String> iterator = simVec.keySet().iterator();
				while(iterator.hasNext()){
					
					String curr = iterator.next();
					if(curr.split("\\s+").length == i)
						offendingBranches.add(curr);
					
				}
				
				//Get value for branches
				while(offendingBranches.size() > 0){
					
					iterator = offendingBranches.iterator();
					String next = iterator.next();
					String prefix = "";
					String[] split = next.split("\\s+");
					for(int k = 0; k < split.length - 1; k++)
						prefix = prefix + split[k];
					
					boolean isSimilar = false;
					isSimilar = isSimilar || simVec.get(next);
					simVec.remove(next);
					iterator.remove();
					
					while(iterator.hasNext()){
						
						next = iterator.next();
						if(next.contains(prefix)){
							
							isSimilar = isSimilar || simVec.get(next);
							simVec.remove(next);
							iterator.remove();
							
						}
						
					}
					
					simVec.put(prefix, isSimilar);
					
				}
				
			}
			
		}
		
		double sim[] = new double[indices.size()];
		Iterator<String> iterator = simVec.keySet().iterator();
		while(iterator.hasNext()){
			
			String next = iterator.next();
			if(simVec.get(next))
				sim[indices.get(next)] = 1.0;
			else
				sim[indices.get(next)] = 0.0;
			
		}
		
		return sim;
		
	}
	
}
