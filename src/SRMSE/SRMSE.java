/**
 * 
 */
package SRMSE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Antoine
 *
 */
public class SRMSE {
	
	HashMap<String,Integer> data;
	HashMap<String, Integer> ref;
	double IJK;
	
	public void getDistributions(String pathData, String pathRef) throws IOException{
		JointDistributionTravelSurvey refManager =new JointDistributionTravelSurvey();
		JointDistributionOutput dataManager = new JointDistributionOutput();
		
		ref = refManager.loadJointDistribution(pathRef);
		data = dataManager.loadJointDistribution(pathData);
		IJK = refManager.IJK;
	}

	public double computeSRMSE(){
		ArrayList<String> noOverLap = new ArrayList<String>();
		double sqDif = 0;
		double popData = getPopSize(data);
		double popRef = getPopSize(ref);
		
		for(String key:data.keySet()){
			if(!ref.containsKey(key)){
				noOverLap.add(key);
			}
		}
		
		for(String key: ref.keySet()){
			int n = ref.get(key);
			if(data.containsKey(key)){
				int n1 = data.get(key);
				//sqDif+= Math.pow(n1/popData-n/popRef,2);
				sqDif+= Math.pow(n1-n*popData/popRef,2);
			}
			else{
				
				//sqDif+=Math.pow(n/popRef,2);
				sqDif+=Math.pow(n*popData/popRef,2);
			}
			
		}
		for(int i = 0; i < noOverLap.size();i++){
			String key = noOverLap.get(i);
			int n = data.get(key);
			//sqDif+=Math.pow(n/popData,2);
			sqDif+=Math.pow(n,2);
		}
		//double sme = Math.sqrt((double)sqDif*IJK);
		double sme = Math.sqrt((double)sqDif*IJK)/popData;
		return sme;
	}
	
	public double computeSRMSEwithWeights(){
		ArrayList<String> noOverLap = new ArrayList<String>();
		double sqDif = 0;
		double popData = getPopSize(data);
		double popRef = getPopSize(ref);
		
		for(String key:data.keySet()){
			if(!ref.containsKey(key)){
				noOverLap.add(key);
			}
		}
		
		for(String key: ref.keySet()){
			int n = ref.get(key);
			if(data.containsKey(key)){
				int n1 = data.get(key);
				//sqDif+= Math.pow(n1/popData-n/popRef,2);
				sqDif+= Math.pow(n1-n*popData/popRef,2);
			}
			else{
				
				//sqDif+=Math.pow(n/popRef,2);
				sqDif+=Math.pow(n*popData/popRef,2);
			}
			
		}
		for(int i = 0; i < noOverLap.size();i++){
			String key = noOverLap.get(i);
			int n = data.get(key);
			//sqDif+=Math.pow(n/popData,2);
			sqDif+=Math.pow(n,2);
		}
		//double sme = Math.sqrt((double)sqDif*IJK);
		double sme = Math.sqrt((double)sqDif*IJK)/popData;
		return sme;
	}

	private double getPopSize(HashMap<String, Integer> distribution) {
		// TODO Auto-generated method stub
		double count = 0.0;
		
		for(String key: distribution.keySet()){
			count += distribution.get(key);
		}
		return count;
	}
}
