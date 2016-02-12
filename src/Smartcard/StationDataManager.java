/**
 * 
 */
package Smartcard;

import java.io.IOException;
import java.util.HashMap;

import ActivityChoiceModel.DataManager;

/**
 * @author Antoine
 *
 */
public class StationDataManager extends DataManager{

	HashMap<Integer, Station> myStations = new HashMap<Integer,Station>();
	
	public StationDataManager(){
		
	}
	
	public HashMap<Integer, Station> prepareStations(String pathToStationsData) throws IOException{
		initialize(pathToStationsData);
		createStations();
		return myStations;
	}

	private void createStations() {
		// TODO Auto-generated method stub
		for(int i = 0; i < myData.get(UtilsST.stationId).size(); i++){
			Station newS = new Station();
			newS.myId = (int)Double.parseDouble(myData.get(UtilsST.stationId).get(i));
			newS.x = Double.parseDouble(myData.get(UtilsST.x).get(i));
			newS.y = Double.parseDouble(myData.get(UtilsST.y).get(i));
			myStations.put(newS.myId, newS);
		}
	}
}
