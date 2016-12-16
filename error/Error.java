package error;

import java.util.HashMap;

public class Error {
	
	public static HashMap<Integer, Integer> mapping(Integer[][] prediction, Integer[][] target, int size){
		
		HashMap<Integer, Integer> mapping = new HashMap<Integer, Integer>();
		//so, we need to assign to each target a prediction if prediction >= target
		if(prediction.length >= target.length){
			
			for(int i = 0; i < target.length; i++){
				
				int best = -1;
				double maxObjective = 0.0;
				for(int j = 0; j < prediction.length; j++){
					
					int numMatches = 0;
					for(int k = 0; k < target[i].length; k++){
						
						for(int n = 0; n < prediction[i].length; n++){
							
							if(target[i][k] == prediction[i][n]){
								
								numMatches++;
								break;
								
							}
							
						}
						
					}
					double ber = 0.5 * ((target[i].length - numMatches)/(target[i].length) + (prediction[i].length - numMatches)/(size - target[i].length));
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
						
						for(int n = 0; n < prediction[i].length; n++){
							
							if(target[i][k] == prediction[i][n]){
								
								numMatches++;
								break;
								
							}
							
						}
						
					}
					double ber = 0.5 * ((target[i].length - numMatches)/(target[i].length) + (prediction[i].length - numMatches)/(size - target[i].length));
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
