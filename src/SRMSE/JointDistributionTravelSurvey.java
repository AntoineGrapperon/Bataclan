/**
 * 
 */
package SRMSE;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author Antoine
 *
 */
public class JointDistributionTravelSurvey extends JointDistributionManager{
	
	String weight = new String();

	//dico
	public JointDistributionTravelSurvey(){
		id = "IPERE";
		age = "GRPAGE";
		sex = "SEXE";
		occ = "OCCUP";
		car = "NBVEH";
		nPers = "NBPERS";
		weight = "FacPer";
	}
	
	public HashMap<String,Integer> loadJointDistribution(String path) throws IOException{
		initialize(path);
		HashMap<String,Integer> dimensions = new HashMap<String,Integer>();// DANS LA FORMULE DE SRMSE IL FAUT DIVISER PAR LE NOMBRE DE CAS POSSIBLE CELA N A PAS ENCORE ETE FAIT
		HashMap<String, Integer> curDist = new HashMap<String, Integer>();
		for(int i = 0; i < myData.get(id).size(); i++){
			int curAge = Integer.parseInt(myData.get(age).get(i));
			int curSex = Integer.parseInt(myData.get(sex).get(i));
			int curOcc = Integer.parseInt(myData.get(occ).get(i));
			int curCar = Integer.parseInt(myData.get(car).get(i));
			int curnPers = Integer.parseInt(myData.get(nPers).get(i));
			int curWeight = (int) Math.round(Double.parseDouble(myData.get(weight).get(i)));
			String key = curAge + "-" + curSex + "-" + curOcc + "-" + curCar + "-" + curnPers;
			if(curDist.containsKey(key)){
				curDist.put(key, curDist.get(key)+curWeight);
			}
			else{
				curDist.put(key,curWeight);
			}
			
		}
		
		return curDist;
		
	}
		
}
