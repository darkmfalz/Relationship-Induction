package ingester;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeSet;

import egonetwork.ChineseWhispersSimilarity;
import egonetwork.EgoNetwork;
import egonetwork.McAuleySimilarity;
import egonetwork.SocialCircleInduction;

import error.Error;

public class Kaggle {
	
	//For data retrieved from this source:
	//https://www.kaggle.com/c/learning-social-circles/data
	
	private static int currIndex = -1;
	private static int maxIndex = 27022;

	public static void testAll() throws NumberFormatException, IOException{
		
		BufferedWriter writer = new BufferedWriter(new FileWriter("results_from_" + (currIndex + 1) + ".txt"));
		
		while(currIndex <= maxIndex){
			
			EgoNetwork egoNetwork = createTestableEgoNetwork();
			System.out.println(currIndex);
			//writer.write(currIndex);
			
			//Get the ground-truth circles
			FileReader fileReader = new FileReader(currIndex + ".circles");
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			ArrayList<TreeSet<String>> tempCircles = new ArrayList<TreeSet<String>>();
			String line = null;
			while((line = bufferedReader.readLine()) != null){
				
				String[] lineSep = line.split("\\s+");
				TreeSet<String> circle = new TreeSet<String>();
				for(int i = 1; i < lineSep.length; i++)
					circle.add(lineSep[i]);
				tempCircles.add(circle);
				
			}
			String[][] circles = new String[tempCircles.size()][];
			for(int i = 0; i < tempCircles.size(); i++){
				
				circles[i] = tempCircles.get(i).toArray(new String[0]);
				//writer.write(Arrays.toString(circles[i]));
				
			}
			
			//McAuley Similarity
			fileReader = new FileReader("features.txt");
			bufferedReader = new BufferedReader(fileReader);
			TreeSet<String> egoFeatures = new TreeSet<String>();
			HashMap<String, TreeSet<String>> featureList = new HashMap<String, TreeSet<String>>();
			
			line = null;
			while((line = bufferedReader.readLine()) != null){
				
				String[] lineSep = line.split("\\s+");
				//If this is the feature set of a non-ego
				if(Integer.parseInt(lineSep[0]) != currIndex){
					
					TreeSet<String> features = new TreeSet<String>();
					for(int i = 1; i < lineSep.length; i++)
						features.add(lineSep[i].replace(";", " "));
					featureList.put(lineSep[0], features);
					
				}
				//If this is the feature list of the ego
				else if(Integer.toString(currIndex).equals(lineSep[0])){
					
					for(int i = 1; i < lineSep.length; i++)
						egoFeatures.add(lineSep[i].replace(";", " "));
					
				}
				
			}
			
			bufferedReader.close();
			fileReader.close();
			
			egoNetwork.addFeatures(egoFeatures, featureList, new McAuleySimilarity(1, egoFeatures, featureList));
			
			String[][] mcAuleyCircles = SocialCircleInduction.McAuleyInduction(egoNetwork);
			System.out.println(Error.<String>error(mcAuleyCircles, circles, egoNetwork.numVertices()));
			//String[][] mcAuleyCircles = SocialCircleInduction.McAuleyInduction(egoNetwork, writer);
			//Error.error(mcAuleyCircles, circles, egoNetwork.numVertices(), writer);
			
			//Chinese Whispers Similarity
			ChineseWhispersSimilarity chineseWhispers = new ChineseWhispersSimilarity(egoNetwork);
			egoFeatures = new TreeSet<String>();
			for(int i = 0; i < egoNetwork.numVertices(); i++)
				egoFeatures.add(i + " " + 1);
			featureList = new HashMap<String, TreeSet<String>>();
			for(int i = 0; i < egoNetwork.numVertices(); i++)
				featureList.put(egoNetwork.getName(i), chineseWhispers.chineseWhispersVector(egoNetwork, i));
			egoNetwork.addFeatures(egoFeatures, featureList, chineseWhispers);
			
			String[][] chineseWhispersCircles = SocialCircleInduction.McAuleyInduction(egoNetwork);
			System.out.println(Error.<String>error(chineseWhispersCircles, circles, egoNetwork.numVertices()));
			//String[][] chineseWhispersCircles = SocialCircleInduction.McAuleyInduction(egoNetwork, writer);
			//Error.error(chineseWhispersCircles, circles, egoNetwork.numVertices(), writer);
			
		}
		writer.close();
		
	}
	
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
