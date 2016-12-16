package error;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;

public class Error {

	public static <T> double error(T[][] prediction, T[][] target, int size){
		
		HashMap<Integer, Integer> mapping = mapping(prediction, target, size);
		double error = 0.0;
		Iterator<Integer> iterator = mapping.keySet().iterator();
		while(iterator.hasNext()){
			
			int next = iterator.next();
			
			int numMatches = 0;
			for(int i = 0; i < target[mapping.get(next)].length; i++){
				
				for(int j = 0; j < prediction[next].length; j++){
					
					if(target[mapping.get(next)][i].equals(prediction[next][j])){
						
						numMatches++;
						break;
						
					}
					
				}
				
			}
			double ber = 0.5 * ((target[mapping.get(next)].length - numMatches)/(target[mapping.get(next)].length) + (prediction[next].length - numMatches)/(size - target[mapping.get(next)].length));
			error += ber;
			
		}
		
		return error/((double) mapping.size());
		
	}
	
	public static <T> double error(T[][] prediction, T[][] target, int size, Writer writer) throws IOException{
		
		HashMap<Integer, Integer> mapping = mapping(prediction, target, size);
		double error = 0.0;
		Iterator<Integer> iterator = mapping.keySet().iterator();
		while(iterator.hasNext()){
			
			int next = iterator.next();
			writer.write("Prediction Circle " + next + " maps to Target Circle " + mapping.get(next));
			
			int numMatches = 0;
			for(int i = 0; i < target[mapping.get(next)].length; i++){
				
				for(int j = 0; j < prediction[next].length; j++){
					
					if(target[mapping.get(next)][i].equals(prediction[next][j])){
						
						numMatches++;
						break;
						
					}
					
				}
				
			}
			double ber = 0.5 * ((target[mapping.get(next)].length - numMatches)/(target[mapping.get(next)].length) + (prediction[next].length - numMatches)/(size - target[mapping.get(next)].length));
			error += ber;
			
		}
		
		return error/((double) mapping.size());
		
	}
	
	public static <T> HashMap<Integer, Integer> mapping(T[][] prediction, T[][] target, int size){
		
		//Map from prediction circles to target circles
		HashMap<Integer, Integer> mapping = new HashMap<Integer, Integer>();
		//so, we need to assign to each target a prediction if prediction >= target
		if(prediction.length >= target.length){
			
			for(int i = 0; i < target.length; i++){
				
				int best = -1;
				double maxObjective = 0.0;
				for(int j = 0; j < prediction.length; j++){
					
					int numMatches = 0;
					for(int k = 0; k < target[i].length; k++){
						
						for(int n = 0; n < prediction[j].length; n++){
							
							if(target[i][k].equals(prediction[j][n])){
								
								numMatches++;
								break;
								
							}
							
						}
						
					}
					double ber = 0.5 * ((target[i].length - numMatches)/(target[i].length) + (prediction[j].length - numMatches)/(size - target[i].length));
					if(1.0 - ber > maxObjective){
						
						best = j;
						maxObjective = 1.0 - ber;
						
					}
					
				}
				
				mapping.put(best, i);
				
			}
			
		}
		//if target > prediction, we assign target to prediction
		else{
			
			for(int j = 0; j < prediction.length; j++){
				
				int best = -1;
				double maxObjective = 0.0;
				for(int i = 0; i < target.length; i++){
					
					int numMatches = 0;
					for(int k = 0; k < target[i].length; k++){
						
						for(int n = 0; n < prediction[j].length; n++){
							
							if(target[i][k].equals(prediction[j][n])){
								
								numMatches++;
								break;
								
							}
							
						}
						
					}
					double ber = 0.5 * ((target[i].length - numMatches)/(target[i].length) + (prediction[j].length - numMatches)/(size - target[i].length));
					if(1.0 - ber > maxObjective){
						
						best = i;
						maxObjective = 1.0 - ber;
						
					}
					
				}
				
				mapping.put(j, best);
				
			}
			
		}
		
		return mapping;
		
	}
	
}
