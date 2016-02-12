/**
 * 
 */
package Smartcard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import ActivityChoiceModel.BiogemeControlFileGenerator;

/**
 * @author Antoine
 *
 */
public class PublicTransitSystem {
	
	public static HashMap<Integer, Station> myStations = new HashMap<Integer, Station>();
	public static ArrayList<Smartcard> mySmartcards = new ArrayList<Smartcard>();

	BiogemeControlFileGenerator myCtrlGenerator;
	
	public PublicTransitSystem(){
		
	}
	
	public void initialize(BiogemeControlFileGenerator ctrlGenerator, String pathSmartcard, String pathStations) throws IOException{
		myCtrlGenerator = ctrlGenerator;
		SmartcardDataManager mySmartcardManager = new SmartcardDataManager(myCtrlGenerator);
		StationDataManager myStationManager = new StationDataManager();
		myStations = myStationManager.prepareStations(pathStations);
		System.out.println("--stations were prepared");
		mySmartcards = mySmartcardManager.prepareSmartcards(pathSmartcard);
		
	}
	
	
}
