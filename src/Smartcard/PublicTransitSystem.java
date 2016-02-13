/**
 * 
 */
package Smartcard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import ActivityChoiceModel.BiogemeAgent;
import ActivityChoiceModel.BiogemeControlFileGenerator;
import ActivityChoiceModel.BiogemeSimulator;

/**
 * @author Antoine
 *
 */
public class PublicTransitSystem {
	
	public static HashMap<Integer, Station> myStations = new HashMap<Integer, Station>();
	public static ArrayList<Smartcard> mySmartcards = new ArrayList<Smartcard>();
	public static HashMap<Double,ArrayList<BiogemeAgent>> myDaZones = new HashMap<Double,ArrayList<BiogemeAgent>>();
	public static HashMap<Double,ArrayList<Integer>> geoDico = new HashMap<Double,ArrayList<Integer>>();
	public static BiogemeSimulator mySimulator = new BiogemeSimulator();

	BiogemeControlFileGenerator myCtrlGenerator;
	
	public PublicTransitSystem(){
		
	}
	
	public void initialize(BiogemeControlFileGenerator ctrlGenerator, String pathSmartcard, String pathStations, String pathGeoDico) throws IOException{
		myCtrlGenerator = ctrlGenerator;
		SmartcardDataManager mySmartcardManager = new SmartcardDataManager(myCtrlGenerator);
		StationDataManager myStationManager = new StationDataManager();
		GeoDicoManager myGeoDico = new GeoDicoManager();
		
		myStations = myStationManager.prepareStations(pathStations);
		mySmartcards = mySmartcardManager.prepareSmartcards(pathSmartcard);
		geoDico = myGeoDico.getDico(pathGeoDico);
	}
	
	
}
