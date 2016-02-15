/**
 * 
 */
package Smartcard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import ActivityChoiceModel.BiogemeAgent;
import ActivityChoiceModel.BiogemeChoice;
import ActivityChoiceModel.BiogemeControlFileGenerator;
import ActivityChoiceModel.BiogemeSimulator;
import Utils.Utils;

/**
 * @author Antoine
 *
 */
public class PublicTransitSystem {
	
	public static HashMap<Integer, Station> myStations = new HashMap<Integer, Station>();
	public static ArrayList<Smartcard> mySmartcards = new ArrayList<Smartcard>();
	public static HashMap<Double,ArrayList<BiogemeAgent>> myDaZones = new HashMap<Double,ArrayList<BiogemeAgent>>();
	public static HashMap<Double, ArrayList<Smartcard>> zonalChoiceSets = new HashMap<Double, ArrayList<Smartcard>>();
	public static ArrayList<BiogemeAgent> myPopulation = new ArrayList<BiogemeAgent>();
	PopulationWriter myPopWriter = new PopulationWriter();
	double[][] costMatrix;
	
	/**
	 * The geoDico has zonal identifier has keys and an ArrayList of close station identifiers.
	 */
	public static HashMap<Double,ArrayList<Integer>> geoDico = new HashMap<Double,ArrayList<Integer>>();
	public static BiogemeSimulator mySimulator = new BiogemeSimulator();

	BiogemeControlFileGenerator myCtrlGenerator;
	
	public PublicTransitSystem(){
		
	}
	
	public void initialize(BiogemeControlFileGenerator ctrlGenerator, String pathSmartcard, String pathStations, String pathGeoDico, String pathPop) throws IOException{
		myCtrlGenerator = ctrlGenerator;
		SmartcardDataManager mySmartcardManager = new SmartcardDataManager(myCtrlGenerator);
		StationDataManager myStationManager = new StationDataManager();
		GeoDicoManager myGeoDico = new GeoDicoManager();
		
		myStations = myStationManager.prepareStations(pathStations);
		myStationManager = null;
		mySmartcards = mySmartcardManager.prepareSmartcards(pathSmartcard);
		mySmartcardManager = null;
		geoDico = myGeoDico.getDico(pathGeoDico);
		myGeoDico= null;
		myPopulation = mySimulator.initialize(pathPop);
	}
	
	public void assignPotentialSmartcardsToZones(){
		for(double currZone : geoDico.keySet()){
			ArrayList<Integer> closeStations = geoDico.get(currZone);
			ArrayList<Smartcard> zonalChoiceSet = new ArrayList<Smartcard>();
			Iterator<Smartcard> universalChoiceSet = mySmartcards.iterator();
			while(universalChoiceSet.hasNext()){
				Smartcard currCard = universalChoiceSet.next();
				if(closeStations.contains(currCard.stationId)){
					zonalChoiceSet.add(currCard);
				}
			}
			zonalChoiceSets.put(currZone,zonalChoiceSet);
		}
	}
	
	/*public void applyModelOnSmartcard(String outputPath) throws IOException{
		int n = 0;
		int N = myPopulation.size();
		
		for(BiogemeAgent person: myPopulation){
			ArrayList<BiogemeChoice> choiceSet = person.processChoiceSetFromSmartcard(UtilsSM.choiceSetSize);
			person.applyModelSmartcard(choiceSet);
			n++;
			if(n%1000 == 0){System.out.println("-- " + n + " agents were processed out of " + N);}
		}
		myPopWriter.writeSimulationResults(outputPath, myPopulation);
	}*/
	
		int n = 0;
		public double[][] createCostMatrix() throws IOException{
		int N = myPopulation.size();
		int rowIndex = 0;
		costMatrix = new double[N][N];
		
		
		for(BiogemeAgent person: myPopulation){
			double zoneId = Double.parseDouble(person.myAttributes.get("zoneId"));
			if(zonalChoiceSets.containsKey(zoneId)){
				person.createAndWeighChoiceSet(UtilsSM.choiceSetSize);
				costMatrix[rowIndex] = person.writeCosts(myPopulation.size(), mySmartcards.size());
				rowIndex++;
			}
			else{
				double[] newRow = new double[myPopulation.size()];
				for(int i = 0; i < myPopulation.size(); i++){newRow[i] = 999999.00;}
				costMatrix[rowIndex] = newRow;
				rowIndex++;
			}
		}
		return costMatrix;
	}

	
}
