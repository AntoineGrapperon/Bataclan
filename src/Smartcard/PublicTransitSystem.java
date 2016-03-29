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
import java.util.Random;

import ActivityChoiceModel.BiogemeAgent;
import ActivityChoiceModel.BiogemeChoice;
import ActivityChoiceModel.BiogemeControlFileGenerator;
import ActivityChoiceModel.BiogemeHypothesis;
import ActivityChoiceModel.BiogemeSimulator;
import ActivityChoiceModel.UtilsTS;
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
	
	public static HashMap<Double,ArrayList<BiogemeAgent>> zonalPopulation = new HashMap<Double,ArrayList<BiogemeAgent>>();
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
	
	public void initialize(BiogemeControlFileGenerator ctrlGenerator, 
			String pathSmartcard, 
			String pathStations, 
			String pathGeoDico, 
			String pathPop,
			String pathModel) throws IOException{
		myCtrlGen = ctrlGenerator;
		SmartcardDataManager mySmartcardManager = new SmartcardDataManager(myCtrlGen);
		StationDataManager myStationManager = new StationDataManager();
		GeoDicoManager myGeoDico = new GeoDicoManager();
		PopulationDataManager myPopGenerator = new PopulationDataManager();
		
		mySimulator.extractChoiceUniverse();
		mySimulator.setHypothesis();
		mySimulator.importBiogemeModel(pathModel);
		mySimulator.importNest(pathModel);
		
		
		myStations = myStationManager.prepareStations(pathStations);
		mySmartcards = mySmartcardManager.prepareSmartcards(pathSmartcard);
		geoDico = myGeoDico.getDico(pathGeoDico);
		System.out.println("--geodico assigned");
		myPopulation = myPopGenerator.getAgents(pathPop);
		
		
	}
	
	
	
	public void createZonalSmartcardIndex(){
		for(double currZone : geoDico.keySet()){
			ArrayList<Integer> closeStations = geoDico.get(currZone);
			ArrayList<Smartcard> zonalChoiceSet = new ArrayList<Smartcard>();
			Iterator<Smartcard> universalChoiceSet = mySmartcards.iterator();
			while(universalChoiceSet.hasNext()){
				Smartcard currCard = universalChoiceSet.next();
				if(currCard.stationId == 1 || currCard.stationId == 2){
					
				}
				else if(closeStations.contains(currCard.stationId)){
					zonalChoiceSet.add(currCard);
				}
			}
			//System.out.println(zonalChoiceSet.size());
			zonalChoiceSets.put(currZone,zonalChoiceSet);
		}
	}
	
	private HashMap<Double, ArrayList<Smartcard>> createZonalSmartcardIndex(ArrayList<Smartcard> mySmartcards){
		
		HashMap<Double, ArrayList<Smartcard>> localZonalChoiceSets = new HashMap<Double, ArrayList<Smartcard>>();
		for(double currZone : geoDico.keySet()){
			ArrayList<Integer> closeStations = geoDico.get(currZone);
			ArrayList<Smartcard> zonalChoiceSet = new ArrayList<Smartcard>();
			Iterator<Smartcard> universalChoiceSet = mySmartcards.iterator();
			while(universalChoiceSet.hasNext()){
				Smartcard currCard = universalChoiceSet.next();
				if(currCard.stationId == 1 || currCard.stationId == 2){
					
				}
				else if(closeStations.contains(currCard.stationId)){
					zonalChoiceSet.add(currCard);
				}
			}
			//System.out.println(zonalChoiceSet.size());
			localZonalChoiceSets.put(currZone,zonalChoiceSet);
		}
		return localZonalChoiceSets;
	}
	
	public void createZonalPopulationIndex() {
		// TODO Auto-generated method stub
		for(double currZone : geoDico.keySet()){
			Iterator<BiogemeAgent> it = myPopulation.iterator();
			zonalPopulation.put(currZone, new ArrayList<BiogemeAgent>());
			while(it.hasNext()){
				BiogemeAgent currAgent = it.next();
				if(Double.parseDouble(currAgent.myAttributes.get(UtilsSM.zoneId)) == currZone){
					zonalPopulation.get(currZone).add(currAgent);
				}
			}
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
	
		
	/**
	 * Use this function only if you have access to a 1To + RAM system: it is creating a square matrix which size is
	 * the size of the population: for a 300 000 population this is 600 Go of RAM required.	
	 */
	/*private double[][] createCostMatrix() throws IOException{
		int n = 0;
		int N = myPopulation.size();
		int M = mySmartcards.size();
		int rowIndex = 0;
		System.out.println("bouh" + N + "  " + M);
		costMatrix = new double[N][M];
		System.out.println("bouh");
		
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
	}*/
	
	/**
	 * This is still generating a choice set from the whole population. Use the same function including the located smartcard.
	 * @param myPopulation
	 * @param mySmartcards
	 * @return
	 * @throws IOException
	 */
	/*private double[][] createLocalCostMatrix(ArrayList<BiogemeAgent> myPopulation, ArrayList<Smartcard> mySmartcards) throws IOException{
		int n = 0;
		int N = myPopulation.size();
		int M = mySmartcards.size();
		int rowIndex = 0;
		double[][] costMatrix = new double[N][N];
		
		for(BiogemeAgent person: myPopulation){
			double zoneId = Double.parseDouble(person.myAttributes.get(UtilsSM.zoneId));
			ArrayList<Smartcard> choiceSet = person.generateChoiceSet(UtilsSM.choiceSetSize, mySmartcards);
			person.processSmartcardChoiceSet(choiceSet);
			costMatrix[rowIndex] = person.writeCosts(myPopulation.size(), mySmartcards.size());
			rowIndex++;
		}
		return costMatrix;
	}*/
	
	private double[][] createLocalCostMatrix(
			ArrayList<BiogemeAgent> myPopulation, 
			ArrayList<Smartcard> mySmartcards, 
			HashMap<Double, ArrayList<Smartcard>> zonalSmartcardIndex
			) throws IOException{
		
		int n = 0;
		int N = myPopulation.size();
		int M = mySmartcards.size();
		int rowIndex = 0;
		double[][] costMatrix = new double[N][N];
		int count = 0;
		for(BiogemeAgent person: myPopulation){
			double zoneId = Double.parseDouble(person.myAttributes.get(UtilsSM.zoneId));
			if(zonalSmartcardIndex.containsKey(zoneId)){
				//ArrayList<Smartcard> choiceSet = person.generateChoiceSet(UtilsSM.choiceSetSize, zonalSmartcardIndex);
				ArrayList<Smartcard> choiceSet = person.generateChoiceSet(zonalSmartcardIndex);
				person.computeUtilities(choiceSet);
				//person.processSmartcardChoiceSet(choiceSet);
				//person.createAndWeighChoiceSet(UtilsSM.choiceSetSize, zonalSmartcardIndex );
				costMatrix[rowIndex] = person.writeCosts(N, M);
				rowIndex++;
			}
			else{
				double[] newRow = new double[myPopulation.size()];
				for(int i = 0; i < myPopulation.size(); i++){newRow[i] = Double.MAX_VALUE;}
				costMatrix[rowIndex] = newRow;
				rowIndex++;
				System.out.println("--this guy shouldn't be there...");
			}
			if(count%1000 ==0){System.out.println("processed agent: " + count);}
			count++;
		}
		return costMatrix;
	}
	
	
	
	
	/**
	 * This never worked
	 * @return
	 * @throws IOException
	 */
	
	/*@Deprecated
	private ArrayList<HashMap<Integer,Double>> createCostMatrixOptimized() throws IOException{
		int n = 0;
		int N = myPopulation.size();
		int M = mySmartcards.size();
		int rowIndex = 0;
		/**
		 * Assumption: there is a bigger population than the number of smartcards.
		 
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
	}*/
	
	
	/*
	 * This is way too long due to hard memory access
	 * CAREFUL: the costmatrix can be a few Go, you may want to write in an external hard-drive to avoid completely overflooding your harddrive.
	 * @param path
	 * @throws IOException
	 */
	/*
	@Deprecated
	private void createCostMatrixHardCopy(String path) throws IOException{
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
	
	
	private void writeNextMatrixLine(double[] temp, OutputFileWritter myCopy) throws IOException{
		String nextLine = new String();
		for(int i = 0; i< temp.length; i++){
			nextLine+= Utils.COLUMN_DELIMETER + temp[i];
		}
		nextLine = nextLine.substring(1);
		myCopy.WriteToFile(nextLine);
	}

	private void makeRoomForCostMatrix() {
		// TODO Auto-generated method stub
		myStations = null;
	}

	@Deprecated
	private ArrayList<double[][]> createCostMatrixByBatch(int numberOfStationBatch) throws IOException {
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
			HashMap<Double, ArrayList<Smartcard>> currZonalChoiceSets = createZonalSmartcardIndex(currLocalSmartcards);
			//myCostMatrices.add(createLocalCostMatrix(currLocalPopulation, currLocalSmartcards));
			myCostMatrices.add(createLocalCostMatrix(currLocalPopulation, currLocalSmartcards, currZonalChoiceSets));
		}
		return myCostMatrices;
	}*/
	
	

	private void assignColumnIndex(ArrayList<Smartcard> mySmartcards) {
		// TODO Auto-generated method stub
		int column = 0;
		for(Smartcard tempS : mySmartcards){
			tempS.columnId = column;
			column++;
		}
	}
	
	public void processMatchingOnPtRiders() throws IOException {
		// TODO Auto-generated method stub
		HashMap<Double, ArrayList<Smartcard>> zonalSmartcardIndex = zonalChoiceSets;// createZonalSmartcardIndex(mySmartcards);
		System.out.println("--prepare to get pt riders");
		ArrayList<Station> stations = new ArrayList<Station>();
		for(Station st: myStations.values()){
			stations.add(st);
		}
		ArrayList<BiogemeAgent> ptRiders = getPtRiders(stations);
		System.out.println("--pt riders generated");
		
		
		ArrayList<Smartcard> consistentSmartcards = sortSmartcard(mySmartcards);
		assignColumnIndex(consistentSmartcards);
		
		double[][] costMatrix = createLocalCostMatrix(ptRiders, consistentSmartcards, zonalSmartcardIndex);
	
					
		int[] result;
		HungarianAlgorithm hu =new HungarianAlgorithm(costMatrix);
		result=hu.execute();

		for(int j=0;j<result.length;j++){
			if(consistentSmartcards.size()>result[j]){
				consistentSmartcards.get(result[j]).isDistributed = true;
				ptRiders.get(j).isDistributed = true;
				ptRiders.get(j).smartcard = consistentSmartcards.get(result[j]).cardId;
			}
			else{
			}
		} 
	}
	
	public void processMatchingOnPtRidersByBatch(int n) throws IOException {
		// TODO Auto-generated method stub
		HashMap<Double, ArrayList<Smartcard>> zonalSmartcardIndex;
		System.out.println("--prepare to get pt riders");
		
		ArrayList<ArrayList<Station>> batches = new ArrayList<ArrayList<Station>>();
		for(int i =0; i < n; i++){ batches.add(new ArrayList<Station>());}
		int batchCount = 0;
		for(Station st: myStations.values()){
			if(st.getSmartcards().size()!= 0){
				batches.get(batchCount).add(st);
				batchCount++;
				if(batchCount==n){batchCount = 0;}
			}
		}
		
		for(ArrayList<Station> batch: batches){
			ArrayList<BiogemeAgent> ptRidersBatch = getPtRiders(batch);
			ArrayList<Smartcard> smartcardsBatch = getSmartcard(batch);
			resetDistributionIndicator(ptRidersBatch);
			smartcardsBatch = sortSmartcard(smartcardsBatch);
			assignColumnIndex(smartcardsBatch);
			zonalSmartcardIndex = createZonalSmartcardIndex(smartcardsBatch);
			System.out.println("--pt riders generated: " + ptRidersBatch.size());
			
			double[][] costMatrix = createLocalCostMatrix(ptRidersBatch, smartcardsBatch, zonalSmartcardIndex);
			int[] result;
			HungarianAlgorithm hu =new HungarianAlgorithm(costMatrix);
			result=hu.execute();

			for(int j=0;j<result.length;j++){
				if(smartcardsBatch.size()>result[j]){
					smartcardsBatch.get(result[j]).isDistributed = true;
					ptRidersBatch.get(j).isDistributed = true;
					ptRidersBatch.get(j).smartcard = smartcardsBatch.get(result[j]).cardId;
				}
				else{
				}
			} 
		}
	}
	
	private void resetDistributionIndicator(ArrayList<BiogemeAgent> ptRidersBatch) {
		// TODO Auto-generated method stub
		for(BiogemeAgent ag: ptRidersBatch){
			ag.isDistributed = false;
		}
	}

	private ArrayList<Smartcard> getSmartcard(ArrayList<Station> batch) {
		// TODO Auto-generated method stub
		ArrayList<Smartcard> temp = new ArrayList<Smartcard>();
		for(Station st: batch){
			if(st.myId==1 || st.myId == 2){
				
			}
			else{
				temp.addAll(st.getSmartcards());
			}
		}
		return temp;
	}

	private ArrayList<Smartcard> sortSmartcard(ArrayList<Smartcard> mySmartcards) {
		// TODO Auto-generated method stub
		ArrayList<Smartcard> sorted = new ArrayList<Smartcard>();
		for(Smartcard sm: mySmartcards){
			if(sm.stationId == 1 || sm.stationId == 2){
				
			}
			else{
				sorted.add(sm);
			}
		}
		return sorted;
	}

	public void processMatchingStationByStation() throws IOException {
		// TODO Auto-generated method stub
		int count = 0;
		for(int key : myStations.keySet()){
			count++;
			ArrayList<Smartcard> currLocalSmartcards = new ArrayList<Smartcard>();
			ArrayList<BiogemeAgent> currLocalPopulation = new ArrayList<BiogemeAgent>();
			Station currStation = myStations.get(key);
			if(currStation.myId == 1 || currStation.myId== 2){
				
			}
			else{
				currLocalSmartcards.addAll(currStation.getSmartcards());
				if(currLocalSmartcards.size() != 0){
					currLocalPopulation.addAll(currStation.getLocalPopulation());
					
					assignColumnIndex(currLocalSmartcards);
					HashMap<Double, ArrayList<Smartcard>> zonalSmartcardIndex = createZonalSmartcardIndex(currLocalSmartcards);
					
					
					double[][] costMatrix = createLocalCostMatrix(currLocalPopulation, currLocalSmartcards, zonalSmartcardIndex);
					//double[][] costMatrix = createLocalCostMatrix(currLocalPopulation, currLocalSmartcards, zonalSmartcardIndex);
					
					System.out.println("count : " + count + 
							" station " + key + 
							" with " + currLocalSmartcards.size() +" local smart cards " +
							"costMatrix size " + costMatrix.length);
					int[] result;
					HungarianAlgorithm hu =new HungarianAlgorithm(costMatrix);
					result=hu.execute();

					for(int j=0;j<result.length;j++){
						if(currLocalSmartcards.size()>result[j]){
							currLocalSmartcards.get(result[j]).isDistributed = true;
							currLocalPopulation.get(j).isDistributed = true;
							currLocalPopulation.get(j).smartcard = currLocalSmartcards.get(result[j]).cardId;
						}
						else{
						}
					} 
				}
			}
		}
	}
	


	private ArrayList<BiogemeAgent> getPtRiders(ArrayList<Station> batch) {
		// TODO Auto-generated method stub
		int multPool =1;
		
		ArrayList<BiogemeAgent> ptRiders = new ArrayList<BiogemeAgent>();
		Random r = new Random();
		
		for(Station st: batch){
			if(st.myId==1 || st.myId == 2){
				
			}
			else{
				
				ArrayList<Smartcard> localSm = st.getSmartcards();
				if(localSm.size()>0){
					ArrayList<BiogemeAgent> localPop = st.getLocalPopulation();
					
					if(!Utils.occupationCriterion){
						int i = 0;
						while(i<multPool * localSm.size()){
							int n = r.nextInt(localPop.size());
							BiogemeAgent curAgent = localPop.get(n);
							if(curAgent.isStoRider()&& !curAgent.isDistributed){
								curAgent.isDistributed = true;
								ptRiders.add(curAgent);
								i++;
							}
						}
					}
					else{
						ArrayList<Smartcard> regularCards = getRegularCard(localSm);
						ArrayList<Smartcard>  studentCards = getStudentCard(localSm);
						ArrayList<Smartcard>  retireeCards = getRetireeCard(localSm);
						ArrayList<BiogemeAgent> regular = getRegularPersons(localPop);
						ArrayList<BiogemeAgent> student = getStudentPersons(localPop);
						ArrayList<BiogemeAgent> retiree = getRetireePersons(localPop);
						int regCount = 0;
						int stdtCount = 0;
						int retCount = 0;
						while(regCount<multPool *regularCards.size()){
							int n = r.nextInt(regular.size());
							BiogemeAgent curAgent = regular.get(n);
							int occupation = Integer.parseInt(curAgent.myAttributes.get(UtilsSM.dictionnary.get(UtilsTS.occupation)));
							if(curAgent.isStoRider()&& !curAgent.isDistributed && (occupation == 0 || occupation == 3)){
								curAgent.isDistributed = true;
								ptRiders.add(curAgent);
								regCount++;
							}
						}
						while(stdtCount<multPool *studentCards.size()){
							int n = r.nextInt(student.size());
							BiogemeAgent curAgent = student.get(n);
							int occupation = Integer.parseInt(curAgent.myAttributes.get(UtilsSM.dictionnary.get(UtilsTS.occupation)));
							if(curAgent.isStoRider()&& !curAgent.isDistributed && occupation == 1 ){
								curAgent.isDistributed = true;
								ptRiders.add(curAgent);
								stdtCount++;
							}
						}
						while(retCount<multPool *retireeCards.size()){
							int n = r.nextInt(retiree.size());
							BiogemeAgent curAgent = retiree.get(n);
							int occupation = Integer.parseInt(curAgent.myAttributes.get(UtilsSM.dictionnary.get(UtilsTS.occupation)));
							if(curAgent.isStoRider()&& !curAgent.isDistributed && occupation == 2){
								curAgent.isDistributed = true;
								ptRiders.add(curAgent);
								retCount++;
							}
						}
					}
				}
			}
		}		
		return ptRiders;
	}
	

	private ArrayList<BiogemeAgent> getRetireePersons(ArrayList<BiogemeAgent> localPop) {
		// TODO Auto-generated method stub
		ArrayList<BiogemeAgent> temp = new ArrayList<BiogemeAgent>();
		for(BiogemeAgent ag: localPop){
			int occupation = Integer.parseInt(ag.myAttributes.get(UtilsSM.dictionnary.get(UtilsTS.occupation)));
			if(occupation == 2){
				temp.add(ag);
			}
		}
		return temp;
	}

	private ArrayList<BiogemeAgent> getStudentPersons(ArrayList<BiogemeAgent> localPop) {
		// TODO Auto-generated method stub
		ArrayList<BiogemeAgent> temp = new ArrayList<BiogemeAgent>();
		for(BiogemeAgent ag: localPop){
			int occupation = Integer.parseInt(ag.myAttributes.get(UtilsSM.dictionnary.get(UtilsTS.occupation)));
			if(occupation == 1){
				temp.add(ag);
			}
		}
		return temp;
	}

	private ArrayList<BiogemeAgent> getRegularPersons(ArrayList<BiogemeAgent> localPop) {
		// TODO Auto-generated method stub
		ArrayList<BiogemeAgent> temp = new ArrayList<BiogemeAgent>();
		for(BiogemeAgent ag: localPop){
			int occupation = Integer.parseInt(ag.myAttributes.get(UtilsSM.dictionnary.get(UtilsTS.occupation)));
			if(occupation == 0 || occupation == 3){
				temp.add(ag);
			}
		}
		return temp;
	}

	private ArrayList<Smartcard> getRetireeCard(ArrayList<Smartcard> localSm) {
		// TODO Auto-generated method stub
		ArrayList<Smartcard> temp = new ArrayList<Smartcard>();
		for(Smartcard sm:localSm){
			if(sm.fare == 2){
				temp.add(sm);
			}
		}
		return temp;
	}

	private ArrayList<Smartcard> getStudentCard(ArrayList<Smartcard> localSm) {
		// TODO Auto-generated method stub
		ArrayList<Smartcard> temp = new ArrayList<Smartcard>();
		for(Smartcard sm:localSm){
			if(sm.fare == 1){
				temp.add(sm);
			}
		}
		return temp;
	}

	private ArrayList<Smartcard> getRegularCard(ArrayList<Smartcard> localSm) {
		// TODO Auto-generated method stub
		ArrayList<Smartcard> temp = new ArrayList<Smartcard>();
		for(Smartcard sm:localSm){
			if(sm.fare == 0){
				temp.add(sm);
			}
		}
		return temp;
	}

	public void localRandomMatch() {
		// TODO Auto-generated method stub
		
		//TO BE CONTINUED
		int count = 0;
		Random random = new Random();
		for(int key : myStations.keySet()){
			count++;
			ArrayList<Smartcard> currLocalSmartcards = new ArrayList<Smartcard>();
			ArrayList<BiogemeAgent> currLocalPopulation = new ArrayList<BiogemeAgent>();
			Station currStation = myStations.get(key);
			if(currStation.myId == 1 || currStation.myId== 2){
				
			}
			else{
				currLocalSmartcards.addAll(currStation.getSmartcards());
				if(currLocalSmartcards.size() != 0){
					currLocalPopulation.addAll(currStation.getLocalPopulation());
					
					Iterator<Smartcard> it = currLocalSmartcards.iterator();
					while(it.hasNext()){
						Smartcard sm = it.next();
						int r = random.nextInt(currLocalPopulation.size());
						currLocalPopulation.get(r).smartcard = sm.cardId;
						sm.isDistributed = true;
						currLocalPopulation.get(r).isDistributed = true;
					}
				}
			}
		}
	}
	
	public void globalRandomMatch(){
		Random random = new Random();
		for(int i = 0; i < mySmartcards.size(); i++){
			int r = random.nextInt(myPopulation.size());
			BiogemeAgent ag = myPopulation.get(r);
			Smartcard sm = mySmartcards.get(i);
			if(ag.isDistributed){
				i=i-1;
			}
			else{
				sm.isDistributed = true;
				ag.isDistributed = true;
				ag.smartcard = sm.cardId;
			}
		}
	}

	
	/*
	public void processMatchingZoneByZone() throws IOException {
		// TODO Auto-generated method stub
		for(double zoneId: zonalChoiceSets.keySet()){
			
			ArrayList<Smartcard> currLocalSmartcards = new ArrayList<Smartcard>();
			ArrayList<BiogemeAgent> currLocalPopulation = new ArrayList<BiogemeAgent>();
			
			currLocalSmartcards = zonalChoiceSets.get(zoneId);	
			currLocalPopulation = zonalPopulation.get(zoneId);
			assignColumnIndex(currLocalSmartcards);
			
			System.out.println("zone " + zoneId + " , population : " + currLocalPopulation.size() + ", smartcard count " + currLocalSmartcards.size());
			
			double[][] costMatrix = createLocalCostMatrix(currLocalPopulation, currLocalSmartcards);
			int[] result;
			
			HungarianAlgorithm hu =new HungarianAlgorithm(costMatrix);
			result=hu.execute();
			
			BufferedWriter write = new BufferedWriter(new FileWriter(Utils.DATA_DIR + "ptSystem\\AAAtest" + zoneId + ".csv"));
			for(int j=0;j<result.length;j++){
				if(currLocalSmartcards.size()>result[j]){
					write.write(result[j]+ Utils.COLUMN_DELIMETER
							+currLocalPopulation.get(j).myAttributes.get(UtilsSM.agentId) + Utils.COLUMN_DELIMETER
							+ currLocalSmartcards.get(result[j]).cardId + "\n");
					write.flush();
				}
				else{
					write.write(result[j]+ Utils.COLUMN_DELIMETER 
							+currLocalPopulation.get(j).myAttributes.get(UtilsSM.agentId) + Utils.COLUMN_DELIMETER
							+ "-1" + "\n");
					write.flush();
				}
			} 
			write.close();
		}
			
	}*/

	public void printSmartcards(String path) throws IOException {
		// TODO Auto-generated method stub
		OutputFileWritter write = new OutputFileWritter();
		write.OpenFile(path);
		Smartcard tempSm = mySmartcards.get(0);
		BiogemeAgent tempAgent = myPopulation.get(0);
		String header = new String();
		for(String key : tempAgent.myAttributes.keySet()){
			header+= key + Utils.COLUMN_DELIMETER;
		}
		header+= UtilsSM.cardId + Utils.COLUMN_DELIMETER
				+UtilsSM.stationId + Utils.COLUMN_DELIMETER
				+ UtilsSM.fare + Utils.COLUMN_DELIMETER
				+ UtilsTS.choice;
		for(String key: mySmartcards.get(0).myAttributes.keySet()){
			header+= Utils.COLUMN_DELIMETER + key;
		}
		write.WriteToFile(header);
		
		for(BiogemeAgent currAgent: myPopulation){
			if(currAgent.smartcard != 0){
				String newLine = new String();
				Smartcard sm = getSmartcard(currAgent);
				for(String key: currAgent.myAttributes.keySet()){
					newLine+= currAgent.myAttributes.get(key) + Utils.COLUMN_DELIMETER;
				}
				newLine+= sm.cardId + Utils.COLUMN_DELIMETER
						+ sm.stationId + Utils.COLUMN_DELIMETER
						+ sm.fare + Utils.COLUMN_DELIMETER +
						sm.getConstantName();
				for(String key : sm.myAttributes.keySet()){
					newLine+= Utils.COLUMN_DELIMETER +sm.myAttributes.get(key) ;
				}
				write.WriteToFile(newLine);
			}
		}
		write.CloseFile();
		
	}

	private Smartcard getSmartcard(BiogemeAgent currAgent) {
		// TODO Auto-generated method stub
		if(currAgent.smartcard != 0){
			for(Smartcard sm: mySmartcards){
				if(sm.cardId == currAgent.smartcard){
					return sm;
				}
			}
		}
		return null;
	}

	public void printStation(String string) throws IOException {
		// TODO Auto-generated method stub
		OutputFileWritter writer = new OutputFileWritter();
		writer.OpenFile(string);
		writer.WriteToFile("stationId, smartcard count, pop count");
		
		for(int key : myStations.keySet()){
			ArrayList<Smartcard> currLocalSmartcards = new ArrayList<Smartcard>();
			ArrayList<BiogemeAgent> currLocalPopulation = new ArrayList<BiogemeAgent>();
			Station currStation = myStations.get(key);
			currLocalSmartcards.addAll(currStation.getSmartcards());
			currLocalPopulation.addAll(currStation.getLocalPopulation());
			writer.WriteToFile(currStation.myId + Utils.COLUMN_DELIMETER + 
					currLocalSmartcards.size() + Utils.COLUMN_DELIMETER +
					currLocalPopulation.size());
		}
		writer.CloseFile();
	}

	public void getValues() {
		// TODO Auto-generated method stub
		for(BiogemeHypothesis currH: BiogemeSimulator.modelHypothesis){
			
			System.out.println(currH.coefName + " " + currH.coefValue);
		}
	}

	
	
}
