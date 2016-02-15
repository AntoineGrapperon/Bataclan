/**
 * 
 */
package Smartcard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import ActivityChoiceModel.DataManager;
import ActivityChoiceModel.UtilsTS;

/**
 * @author Antoine
 *
 */
public class GeoDicoManager extends DataManager{
	
	public HashMap<Double,ArrayList<Integer>> getDico(String pathGeoDico) throws IOException{
		HashMap<Double,ArrayList<Integer>> myDico = new HashMap<Double,ArrayList<Integer>>();
		initialize(pathGeoDico);
		for(int i = 0; i < myData.get(UtilsSM.zoneId).size(); i++){
			double nextRecordZone = Double.parseDouble(myData.get(UtilsSM.zoneId).get(i));
			int newStation = (int) Double.parseDouble(myData.get(UtilsST.stationId).get(i));
			if(myDico.containsKey(nextRecordZone)){
				myDico.get(nextRecordZone).add(newStation);
			}
			else{
				myDico.put(nextRecordZone, new ArrayList<Integer>());
				myDico.get(nextRecordZone).add(newStation);
			}
		}
		return myDico;
	}
}
