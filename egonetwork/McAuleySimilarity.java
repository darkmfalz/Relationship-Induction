package egonetwork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

public class McAuleySimilarity implements ProfileSimilarity {

	private int maxDepth;
	private HashMap<String, Integer> indices;
	private TreeSet<String> leaves;
	private HashMap<String, TreeSet<String>> tree;
	
	public McAuleySimilarity(int maxDepth, TreeSet<String> egoFeatures, HashMap<String, TreeSet<String>> featureList) {
	
		this.maxDepth = maxDepth;
		indices = new HashMap<String, Integer>();
		
		//Get all possible features
		TreeSet<String> validFeatures = new TreeSet<String>();
		leaves = new TreeSet<String>();
		tree = new HashMap<String, TreeSet<String>>();
		//Add the constant feature
		validFeatures.add("Adeeb");
		leaves.add("Adeeb");
		TreeSet<String> adeeb = new TreeSet<String>();
		adeeb.add("Adeeb");
		tree.put("Adeeb", adeeb);
		int actualDepth = 0;
		//Add the ego
		Iterator<String> egoIterator = egoFeatures.iterator();
		while(egoIterator.hasNext()){
			
			String next = egoIterator.next();
			actualDepth = Math.max(actualDepth, next.split(" ").length);
			validFeatures.add(next);
			leaves.add(next);
			if(!tree.containsKey(next)){
				
				TreeSet<String> branch = new TreeSet<String>();
				branch.add(next);
				tree.put(next, branch);
				
			}
			
		}
		
		Iterator<TreeSet<String>> featureIterator = featureList.values().iterator();
		while(featureIterator.hasNext()){
			
			TreeSet<String> next = featureIterator.next();
			Iterator<String> iterator = next.iterator();
			while(iterator.hasNext()){
				
				String nextString = iterator.next();
				actualDepth = Math.max(actualDepth, nextString.split(" ").length);
				validFeatures.add(nextString);
				leaves.add(nextString);
				
				if(!tree.containsKey(nextString)){
					
					TreeSet<String> branch = new TreeSet<String>();
					branch.add(nextString);
					tree.put(nextString, branch);
					
				}
				
			}
			
		}
		//Get all VALID features
		for(int i = actualDepth; i > maxDepth; i--){
			
			//Find offending branches with features more granular than specified
			ArrayList<String> offendingBranches = new ArrayList<String>();
			Iterator<String> iterator = validFeatures.iterator();
			while(iterator.hasNext()){
				
				String curr = iterator.next();
				if(curr.split(" ").length == i)
					offendingBranches.add(curr);
				
			}
			
			//Get the branch prefixes
			while(offendingBranches.size() > 0){
				
				iterator = offendingBranches.iterator();
				String next = iterator.next();
				//Get the prefix of the string
				String prefix = "";
				String[] split = next.split("\\s+");
				for(int k = 0; k < split.length - (i - maxDepth); k++){
					if(k == 0)
						prefix = prefix + split[k];
					else
						prefix = prefix + " " + split[k];
				}
				String[] prefixSplit = prefix.split(" ");
				//Remove the current branch
				validFeatures.remove(next);
				TreeSet<String> branch = new TreeSet<String>();
				branch.addAll(tree.get(next));
				tree.remove(next);
				iterator.remove();
				
				while(iterator.hasNext()){
					
					next = iterator.next();
					String[] nextSplit = next.split(" ");
					
					//Test if this contains the prefix
					boolean containsPrefix = true;
					if(nextSplit.length == prefixSplit.length){
						for(int a = 0; a < prefixSplit.length; a++)
							containsPrefix = containsPrefix || nextSplit[a].equals(prefixSplit[a]);
					}
					else
						containsPrefix = false;
					
					if(containsPrefix){
						
						validFeatures.remove(next);
						branch.addAll(tree.get(next));
						tree.remove(next);
						iterator.remove();
						
					}
					
				}
				
				validFeatures.add(prefix);
				if(!tree.containsKey(prefix))
					tree.put(prefix, branch);
				else
					tree.get(prefix).addAll(branch);
				
			}
			
		}
		
		//Assign all indices
		Iterator<String> iterator = tree.keySet().iterator();
		int i = 0;
		while(iterator.hasNext())
			indices.put(iterator.next(), i++);
		
	}

	@Override
	public double[] similarity(TreeSet<String> profile1, TreeSet<String> profile2) {
		
		//REDO SIMILARITY
		
		int actualDepth = 0;
		//Get the max depth
		Iterator<String> featureIterator = profile1.iterator();
		while(featureIterator.hasNext())
			actualDepth = Math.max(actualDepth, featureIterator.next().split("\\s+").length);
		featureIterator = profile2.iterator();
		while(featureIterator.hasNext())
			actualDepth = Math.max(actualDepth, featureIterator.next().split("\\s+").length);
		
		//Construct the leaf vector
		HashMap<String, Double> simVec = new HashMap<String, Double>();
		featureIterator = leaves.iterator();
		while(featureIterator.hasNext()){
			
			String next = featureIterator.next();
			if((profile1.contains(next) && profile2.contains(next)) || (!profile1.contains(next) && !profile2.contains(next)))
				simVec.put(next, 0.0);
			else
				simVec.put(next, -1.0);
			
		}
		//The constant feature in the McAuley similarity vector
		simVec.put("Adeeb", 1.0);
		
		Iterator<String> iterator = tree.keySet().iterator();
		while(iterator.hasNext()){
			
			String feature = iterator.next();
			Iterator<String> set = tree.get(feature).iterator();
			double value = 0.0;
			while(set.hasNext()){
				
				String leaf = set.next();
				value += simVec.get(leaf);
				simVec.remove(leaf);
				
			}
			simVec.put(feature, value);
			
		}
		
		double sim[] = new double[indices.size()];
		iterator = simVec.keySet().iterator();
		while(iterator.hasNext()){
			
			String next = iterator.next();
			sim[indices.get(next)] = simVec.get(next);
			
		}
		
		return sim;
		
	}
	
	public int getMaxDepth(){
		
		return maxDepth;
		
	}
	
}
