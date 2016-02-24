/**
 * 
 */
package Smartcard;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import ActivityChoiceModel.BiogemeAgent;
import ActivityChoiceModel.BiogemeChoice;
import ActivityChoiceModel.BiogemeControlFileGenerator;
import ActivityChoiceModel.BiogemeSimulator;
import Associations.HungarianAlgorithm;
import Utils.OutputFileWritter;
import Utils.Utils;

/**
 * @author Antoine
 *
 */
public class PublicTransitSystem {
	
	public static HashMap<Integer, Station> myStations = new HashMap<Integer, Station>();
	public static ArrayList<Smartcard> mySmartcards = new ArrayList<Smartcard>();
	//public static HashMap<Double,ArrayList<BiogemeAgent>> myDaZones = new HashMap<Double,ArrayList<BiogemeAgent>>();
	public static HashMap<Double, ArrayList<Smartcard>> zonalChoiceSets = new HashMap<Double, ArrayList<Smartcard>>();
	public static ArrayList<BiogemeAgent> myPopulation = new ArrayList<BiogemeAgent>();
	PopulationWriter myPopWriter = new PopulationWriter();
	double[][] costMatrix;
	//ArrayList<HashMap<Integer,Double>> costMatrixOptimized;
	
	/**
	 * The geoDico has zonal identifiers as keys and an ArrayList of close station identifiers.
	 */
	public static HashMap<Double,ArrayList<Integer>> geoDico = new HashMap<Double,ArrayList<Integer>>();
	public static BiogemeSimulator mySimulator = new BiogemeSimulator();

	public static BiogemeControlFileGenerator myCtrlGen;
	
	public PublicTransitSystem(){
		
	}
	
	public void initialize(BiogemeControlFileGenerator ctrlGenerator, String pathSmartcard, String pathStations, String pathGeoDico, String pathPop) throws IOException{
		myCtrlGen = ctrlGenerator;
		SmartcardDataManager mySmartcardManager = new SmartcardDataManager(myCtrlGen);
		StationDataManager myStationManager = new StationDataManager();
		GeoDicoManager myGeoDico = new GeoDicoManager();
		PopulationDataManager myPopGenerator = new PopulationDataManager();
		
		myStations = myStationManager.prepareStations(pathStations);
		myStationManager = null;
		mySmartcards = mySmartcardManager.prepareSmartcards(pathSmartcard);
		mySmartcardManager = null;
		geoDico = myGeoDico.getDico(pathGeoDico);
		myGeoDico= null;
		System.out.println("--geodico assigned");
		myPopulation = myPopGenerator.getAgents(pathPop);
		
		mySimulator.setHypothesis();
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
			//System.out.println(zonalChoiceSet.size());
			zonalChoiceSets.put(currZone,zonalChoiceSet);
		}
	}
	
	public HashMap<Double, ArrayList<Smartcard>> assignSmartcardToZone(ArrayList<Smartcard> mySmartcards){
		HashMap<Double, ArrayList<Smartcard>> localZonalChoiceSets = new HashMap<Double, ArrayList<Smartcard>>();
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
			//System.out.println(zonalChoiceSet.size());
			localZonalChoiceSets.put(currZone,zonalChoiceSet);
		}
		return localZonalChoiceSets;
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
	
		
		
	public double[][] createCostMatrix() throws IOException{
		int n = 0;
		int N = myPopulation.size();
		int M = mySmartcards.size();
		int rowIndex = 0;
		System.out.println("bouh" + N + "  " + M);
		costMatrix = new double[N][M];
		System.out.println("bouh");
		
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
	
	public ArrayList<HashMap<Integer,Double>> createCostMatrixOptimized() throws IOException{
		int n = 0;
		int N = myPopulation.size();
		int M = mySmartcards.size();
		int rowIndex = 0;
		/**
		 * Assumption: there is a bigger population than the number of smartcards.
		 */
		ArrayList<HashMap<Integer,Double>> costMatrixOptimized = new ArrayList<HashMap<Integer,Double>>();
		
		for(BiogemeAgent person: myPopulation){
			double zoneId = Double.parseDouble(person.myAttributes.get(UtilsSM.zoneId));
			if(zonalChoiceSets.containsKey(zoneId)){
				person.createAndWeighChoiceSet(UtilsSM.choiceSetSize);
				costMatrixOptimized.add(person.getChoiceSet(myPopulation.size(), mySmartcards.size()));	
				//costMatrix[rowIndex] = person.writeCosts(myPopulation.size(), mySmartcards.size());
				rowIndex++;
			}
			else{
				//double[] newRow = new double[myPopulation.size()];
				//for(int i = 0; i < myPopulation.size(); i++){newRow[i] = 999999.00;}
				//costMatrix[rowIndex] = newRow;
				costMatrixOptimized.add(new HashMap<Integer,Double>	());
				rowIndex++;
			}
		}
		return costMatrixOptimized;
	}
	
	
	/**
	 * CAREFUL: the costmatrix can be a few Go, you may want to write in an external hard-drive to avoid completely overflooding your harddrive.
	 * @param path
	 * @throws IOException
	 */
	public void createCostMatrixHardCopy(String path) throws IOException{
		OutputFileWritter myCopy = new OutputFileWritter();
		myCopy.OpenFile(path);
		int n = 0;
		int N = myPopulation.size();
		int M = mySmartcards.size();
		int rowIndex = 0;
		
		for(BiogemeAgent person: myPopulation){
			double zoneId = Double.parseDouble(person.myAttributes.get(UtilsSM.zoneId));
			if(zonalChoiceSets.containsKey(zoneId)){
				person.createAndWeighChoiceSet(UtilsSM.choiceSetSize);
				double[] temp;
				temp = person.writeCosts(myPopulation.size(), mySmartcards.size());
				writeNextMatrixLine(temp, myCopy);
			}
			else{
				double[] temp = new double[myPopulation.size()];
				for(int i = 0; i < myPopulation.size(); i++){temp[i] = 999999.00;}
				writeNextMatrixLine(temp, myCopy);
			}
		}
		myCopy.CloseFile();
	}
	
	public void writeNextMatrixLine(double[] temp, OutputFileWritter myCopy) throws IOException{
		String nextLine = new String();
		for(int i = 0; i< temp.length; i++){
			nextLine+= Utils.COLUMN_DELIMETER + temp[i];
		}
		nextLine = nextLine.substring(1);
		myCopy.WriteToFile(nextLine);
	}

	public void makeRoomForCostMatrix() {
		// TODO Auto-generated method stub
		myStations = null;
	}

	public ArrayList<double[][]> createCostMatrixByBatch(int numberOfStationBatch) throws IOException {
		// TODO Auto-generated method stub
		int batchCount = 0;
		int batchSize = myStations.size() / numberOfStationBatch;
		ArrayList<double[][]> myCostMatrices = new ArrayList<double[][]>();
		
		for(int k = 0; k < numberOfStationBatch ; k ++){
			ArrayList<Smartcard> currLocalSmartcards = new ArrayList<Smartcard>();
			ArrayList<BiogemeAgent> currLocalPopulation = new ArrayList<BiogemeAgent>();
			int i = 0; // we need and external counter because the station id are not numbered from 1 to N.
			for(Integer key : myStations.keySet()){
				
				if(key == 1 || key == 2){
					
				}
				else{
					Station currStation = myStations.get(key);
					if(
							(i >= batchCount * batchSize && i < (batchCount +1) * batchSize) || 
							(i >= batchCount * batchSize && batchCount == numberOfStationBatch -1 )){
						currLocalSmartcards.addAll(currStation.getSmartcards());
						currLocalPopulation.addAll(currStation.getLocalPopulation());
					}
				}
				i++;
			}
			assignColumnIndex(currLocalSmartcards);
			HashMap<Double, ArrayList<Smartcard>> currZonalChoiceSets = assignSmartcardToZone(currLocalSmartcards);
			//myCostMatrices.add(createLocalCostMatrix(currLocalPopulation, currLocalSmartcards));
			myCostMatrices.add(createLocalCostMatrix(currLocalPopulation, currLocalSmartcards, currZonalChoiceSets));
		}
		return myCostMatrices;
	}
	
	public void createCostMatrixStationByStation() throws IOException {
		// TODO Auto-generated method stub
		
		//ArrayList<double[][]> myCostMatrices = new ArrayList<double[][]>();
		
		for(int key : myStations.keySet()){
			ArrayList<Smartcard> currLocalSmartcards = new ArrayList<Smartcard>();
			ArrayList<BiogemeAgent> currLocalPopulation = new ArrayList<BiogemeAgent>();
			Station currStation = myStations.get(key);
			if(currStation.myId == 1 || currStation.myId== 2){
				
			}
			else{
				currLocalSmartcards.addAll(currStation.getSmartcards());
				currLocalPopulation.addAll(currStation.getLocalPopulation());
				assignColumnIndex(currLocalSmartcards);
				HashMap<Double, ArrayList<Smartcard>> currZonalChoiceSets = assignSmartcardToZone(currLocalSmartcards);
				//myCostMatrices.add(createLocalCostMatrix(currLocalPopulation, currLocalSmartcards, currZonalChoiceSets));
				
				int[] result;
				HungarianAlgorithm hu =new HungarianAlgorithm(createLocalCostMatrix(currLocalPopulation, currLocalSmartcards, currZonalChoiceSets));
				//HungarianAlgoRithmOptimized hu =new HungarianAlgoRithmOptimized(costMatrix);
				result=hu.execute();
				
				BufferedWriter write = new BufferedWriter(new FileWriter(Utils.DATA_DIR + "ptSystem\\AAAtest" + key + ".csv"));

				for(int j=0;j<result.length;j++){
					write.write(result[j]+"\n");
					write.flush();
				} //for
				write.close();
			}
			
			
		}
		//return myCostMatrices;
	}

	public double[][] createLocalCostMatrix(ArrayList<BiogemeAgent> myPopulation, ArrayList<Smartcard> mySmartcards) throws IOException{
		int n = 0;
		int N = myPopulation.size();
		int M = mySmartcards.size();
		int rowIndex = 0;
		double[][] costMatrix = new double[N][M];
		
		for(BiogemeAgent person: myPopulation){
			double zoneId = Double.parseDouble(person.myAttributes.get(UtilsSM.zoneId));
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
	
	public double[][] createLocalCostMatrix(
			ArrayList<BiogemeAgent> myPopulation, 
			ArrayList<Smartcard> mySmartcards, 
			HashMap<Double, ArrayList<Smartcard>> myZonalChoiceSets
			) throws IOException{
		
		int n = 0;
		int N = myPopulation.size();
		int M = mySmartcards.size();
		int rowIndex = 0;
		double[][] costMatrix = new double[N][M];
		
		for(BiogemeAgent person: myPopulation){
			double zoneId = Double.parseDouble(person.myAttributes.get(UtilsSM.zoneId));
			if(myZonalChoiceSets.containsKey(zoneId)){
				person.createAndWeighChoiceSet(UtilsSM.choiceSetSize, myZonalChoiceSets );
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

	private void assignColumnIndex(ArrayList<Smartcard> mySmartcards) {
		// TODO Auto-generated method stub
		int column = 0;
		for(Smartcard tempS : mySmartcards){
			tempS.columnId = column;
			column++;
		}
	}
	
}
