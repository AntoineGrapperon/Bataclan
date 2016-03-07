/**
 * 
 */
package SRMSE;

import java.io.IOException;
import java.util.HashMap;

import ActivityChoiceModel.DataManager;

/**
 * @author Antoine
 *
 */
public class JointDistributionManager extends DataManager {

	//dico
	String id;
	String age;
	String sex;
	String occ;
	String car;
	String nPers;
	public int IJK = 7*2*4*4*5;
	
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
			String key = curAge + "-" + curSex + "-" + curOcc + "-" + curCar + "-" + curnPers;
			if(curDist.containsKey(key)){
				curDist.put(key, curDist.get(key)+1);
			}
			else{
				curDist.put(key,1);
			}
			
		}
		
		return curDist;
		
	}
}
