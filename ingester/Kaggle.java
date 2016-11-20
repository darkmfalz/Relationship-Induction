package ingester;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import ego.network.EgoNetwork;

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
			EgoNetwork egoNetwork = new EgoNetwork(currIndex);
			String line = null;
			//Obtain all distinct items and their supports
			while((line = bufferedReader.readLine()) != null){
				
				System.out.println(currIndex + " " + line);
				
			}
			
			bufferedReader.close();
			fileReader.close();
			return egoNetwork;
			
		}
		catch(IOException e){
			System.err.println(e.getMessage());
			System.err.println("No further Kaggle files to be ingested.");
			return null;
		}
		catch(NullPointerException e){
			System.err.println(e.getMessage());
			System.err.println("No further Kaggle files to be ingested.");
			return null;
		}
		
	}

}
