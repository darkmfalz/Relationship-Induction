package error;

import java.util.HashMap;
import java.util.HashSet;

public class Error {
	
	public static double error(Integer[][] prediction, Integer[][] target, int size){
		
		if(prediction.length < target.length)
			return function1(prediction, target, new HashMap<Integer, Integer>(), new HashSet<Integer>(), size)[0]/((double) prediction.length);
		else
			return function2(prediction, target, new HashMap<Integer, Integer>(), new HashSet<Integer>(), size)[0]/((double) target.length);
		
	}
	
	public static double[] function1(Integer[][] prediction, Integer[][] target, HashMap<Integer, Integer> circleMapping, HashSet<Integer> assigned, int size){
		
		if(circleMapping.size() == prediction.length){
			
			//TODO BER
			double[] val = new double[2];
			for(int i = 0; i < prediction.length; i++){
				
				double numMatches = 0.0;
				for(int j = 0; j < prediction[i].length; j++){
					
					boolean match = false;
					for(int k = 0; k < target[circleMapping.get(i)].length; k++){
						
						if(prediction[i][j] == target[circleMapping.get(i)][k]){
							
							match = true;
							break;
							
						}
						
					}
					if(match)
						numMatches++;
					
				}
				val[0] += (((double)target[circleMapping.get(i)].length)-numMatches)/((double)target[circleMapping.get(i)].length)
						+ (((double)prediction[i].length)-numMatches)/(((double) size) - (double)target[circleMapping.get(i)].length);
				val[1] += 1.0 - val[0];
				
			}
			return val;
			
		}
		else{
			
			double[] val = new double[2];
			for(int i = circleMapping.size(); i < prediction.length; i++){
				
				for(int j = 0; j < target.length; j++){
					
					if(!assigned.contains(j)){
						
						circleMapping.put(i, j);
						assigned.add(j);
						double[] temp = function1(prediction, target, circleMapping, assigned, size);
						if(temp[1] > val[1])
							val = temp;
						circleMapping.remove(i);
						assigned.remove(j);
						
					}
					
				}
				
			}
			return val;
			
		}
		
	}
	
	public static double[] function2(Integer[][] prediction, Integer[][] target, HashMap<Integer, Integer> circleMapping, HashSet<Integer> assigned, int size){
		
		if(circleMapping.size() == target.length){
			
			//TODO BER
			double[] val = new double[2];
			for(int i = 0; i < target.length; i++){
				
				double numMatches = 0.0;
				for(int j = 0; j < target[i].length; j++){
					
					boolean match = false;
					for(int k = 0; k < prediction[circleMapping.get(i)].length; k++){
						
						if(target[i][j] == prediction[circleMapping.get(i)][k]){
							
							match = true;
							break;
							
						}
						
					}
					if(match)
						numMatches++;
					
				}
				val[0] += (((double)target[circleMapping.get(i)].length)-numMatches)/((double)target[circleMapping.get(i)].length)
						+ (((double)prediction[i].length)-numMatches)/(((double) size) - (double)target[circleMapping.get(i)].length);
				val[1] += 1.0 - val[0];
				
			}
			return val;
			
		}
		else{
			
			double[] val = new double[2];
			for(int i = circleMapping.size(); i < target.length; i++){
				
				for(int j = 0; j < prediction.length; j++){
					
					if(!assigned.contains(j)){
						
						circleMapping.put(i, j);
						assigned.add(j);
						double[] temp = function2(prediction, target, circleMapping, assigned, size);
						if(temp[1] > val[1])
							val = temp;
						circleMapping.remove(i);
						assigned.remove(j);
						
					}
					
				}
				
			}
			return val;
			
		}
		
	}


}
