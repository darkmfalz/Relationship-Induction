package ingester;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import egonetwork.EgoNetwork;
import egonetwork.McAuleySimilarity;

public class Kaggle {
	
	//For data retrieved from this source:
	//https://www.kaggle.com/c/learning-social-circles/data
	
	private static int currIndex = -1;
	private static int maxIndex = 27022;
	
	public static EgoNetwork createTestableEgoNetwork(){
		
		//Select EgoNetworks that are testable
		FileReader fileReader = null;
		findIndex:
		for(currIndex = currIndex+1; currIndex <= maxIndex; currIndex++){
		
			try{
				//If it has an associated .circles file, it's testable
				fileReader = new FileReader(currIndex + ".circles");
				fileReader.close();
				fileReader = new FileReader(currIndex + ".egonet");
				break findIndex;
			}
			catch(IOException e){
				fileReader = null;
				continue;
			}
			
		}
		
		try{
			
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			HashMap<String, Integer> indices = new HashMap<String, Integer>();
			HashMap<String, ArrayList<String>> adjLists = new HashMap<String, ArrayList<String>>();
			
			String line = null;
			while((line = bufferedReader.readLine()) != null){
				
				String[] lineSep = line.split("\\s");
				//Extract the actual head of the list
				String curr = lineSep[0].substring(0, lineSep[0].length() - 1);
				indices.put(curr, indices.size());
				//Construct the adjacency list
				ArrayList<String> adjList = new ArrayList<String>();
				for(int i = 1; i < lineSep.length; i++)
					adjList.add(lineSep[i]);
				adjLists.put(curr, adjList);
				
			}
			
			bufferedReader.close();
			fileReader.close();
			EgoNetwork egoNetwork = new EgoNetwork(Integer.toString(currIndex), indices, adjLists);
			
			//Add all the features
			fileReader = new FileReader("features.txt");
			bufferedReader = new BufferedReader(fileReader);
			ArrayList<String> egoFeatures = new ArrayList<String>();
			HashMap<String, ArrayList<String>> featureList = new HashMap<String, ArrayList<String>>();
			
			while((line = bufferedReader.readLine()) != null){
				
				String[] lineSep = line.split("\\s+");
				if(indices.keySet().contains(lineSep[0])){
					
					ArrayList<String> features = new ArrayList<String>();
					for(int i = 1; i < lineSep.length; i++)
						features.add(lineSep[i].replace(";", " "));
					featureList.put(lineSep[0], features);
					
				}
				else if(Integer.toString(currIndex).equals(lineSep[0])){
					
					for(int i = 1; i < lineSep.length; i++)
						egoFeatures.add(lineSep[i].replace(";", " "));
					
				}
				
			}
			
			bufferedReader.close();
			fileReader.close();
			egoNetwork.addFeatures(egoFeatures, featureList, new McAuleySimilarity(2,egoFeatures,featureList));
			
			return egoNetwork;
			
		}
		catch(IOException e){
			System.err.println(e.getMessage());
			e.printStackTrace(System.err);
			System.err.println("No further Kaggle files to be ingested.");
			return null;
		}
		catch(NullPointerException e){
			System.err.println(e.getMessage());
			e.printStackTrace(System.err);
			System.err.println("No further Kaggle files to be ingested.");
			return null;
		}
		
	}

}
