package SimPSynz;



import java.io.IOException;
import java.util.Hashtable;

import org.omg.CORBA.Environment;

import ActivityChoiceModel.BiogemeControlFileGenerator;
import ActivityChoiceModel.BiogemeSimulator;
import ActivityChoiceModel.CensusPreparator;
import ActivityChoiceModel.TravelSurveyPreparator;
import ActivityChoiceModel.UtilsTS;
import SimulationObjects.World;

import java.util.*;

import Utils.*;

/*
 * created by: Antoine Grapperon
 * on: 29/12/2015
 * last edited by: 
 * on: 
 * summary: 
 * comments:
 */
public class Main {

    public static void main(String[] args) {
        // TODO code application logic here
    	long startTime = System.currentTimeMillis();
	    World myWorld = new World(505);
	    ConditionalGenerator condGenerator = new ConditionalGenerator();
	    
	    //TravelSurveyPreparator odPreparator = new TravelSurveyPreparator("D:\\Recherche\\model\\model\\ODprepared.csv");
	    TravelSurveyPreparator odGatineau = new TravelSurveyPreparator();
	    BiogemeSimulator odGatineauValidation;
	    
	    String city = "Gatineau";
	    UtilsTS odDictionnary = new UtilsTS(city);
	    Utils utils = new Utils(city);
	    
	    String workingDir = System.getProperty("user.dir");
		System.out.println("Current working directory : " + workingDir);
	   
	    CensusPreparator census = new CensusPreparator(Utils.DATA_DIR + "CMA505CENSUSPROFIL2006.csv");
	    System.out.println("--census file was found");
		
		
	    
	    
	    try {
	    	//###############################################################################
	    	//Create conditional distributions at the metro level from PUMF
	    	//###############################################################################
	    	/*String data = Utils.DATA_DIR + "data\\CMA505PUMF2006.csv";
	    	String descFile = Utils.DATA_DIR + "ctrl\\descFile.txt";
	    	String zonalData = "D:\\Recherche\\modelGatineau\\gatineau_zonalFile.csv";// should be removed from the function
	    	String destPath = Utils.DATA_DIR + "data\\505";
	    	condGenerator.GenerateConditionalsStepByStep(data,descFile,zonalData,destPath);*/
	    	
	    	//###############################################################################
	    	//Create conditional distributions at the metro level from OD survey (for 
	    	//occupation and car ownership distribution
	    	//###############################################################################
	    	/*String data = Utils.DATA_DIR + "data\\gatineau505_od_prepared.txt";
	    	String descFile =Utils.DATA_DIR + "ctrl\\descFile.txt";
	    	String zonalData = "D:\\Recherche\\modelGatineau\\gatineau_zonalFile.csv";// should be removed
	    	String destPath = Utils.DATA_DIR + "data\\505";
	    	condGenerator.GenerateConditionalsStepByStep(data,descFile,zonalData,destPath);*/
	    	
	    	//###############################################################################
	    	//create local conditional distributions
	    	//###############################################################################
	    	//census.prepareDataColumnStorage();
	    	
	    	//###############################################################################
	    	//Create the zonal input file for population sinthesis (DAUID , Population)
	    	//###############################################################################
	    	//census.writeZonalInputFile();
	    	/*int nBatch = 150;
	    	census.writeZonalInputFile(nBatch);	
	    	census.writeCtrlFile(nBatch);
	    	
	    	//###############################################################################
	    	//Synthetic population generator by batches
	    	//###############################################################################
	    	String pathToSeeds = Utils.DATA_DIR + "data\\CMA505PUMF2006.csv";
	    	myWorld.Initialize(true, 1);// we need this for writting headers
	    		    	
	    	//Initialize the statistical log
            OutputFileWritter localStatAnalysis = new OutputFileWritter();
            localStatAnalysis.OpenFile(Utils.DATA_DIR + "data\\505\\localStatAnalysis.csv");
            String headers = "zoneId, population";
            for(int i = 0; i < ConfigFile.AttributeDefinitionsImportance.size(); i++){
    			headers = headers + Utils.COLUMN_DELIMETER + ConfigFile.AttributeDefinitionsImportance.get(i).category + "_MSE"
    					 + Utils.COLUMN_DELIMETER + ConfigFile.AttributeDefinitionsImportance.get(i).category + "_absErr"
    							 + Utils.COLUMN_DELIMETER + ConfigFile.AttributeDefinitionsImportance.get(i).category + "_%Err" ;
            }
            localStatAnalysis.myFileWritter.write(headers);
            
            // Initialize the population pool log
            OutputFileWritter population =  new OutputFileWritter();
        	population.OpenFile(Utils.DATA_DIR + "data\\505\\createdPopulation.csv");
        	headers = "zoneId" + Utils.COLUMN_DELIMETER + "zoneId";
            for(int i = 0; i < ConfigFile.AttributeDefinitions.size(); i++){
    			headers = headers + Utils.COLUMN_DELIMETER + ConfigFile.AttributeDefinitions.get(i).category ;
            }
            population.myFileWritter.write(headers);
            
            ConfigFile.resetConfigFile();
            myWorld = null;
	    	
	    	for(int i = 0; i < nBatch; i++){
	    		
	    		
	    		World currWorld = new World(505);
	    		currWorld.Initialize(true, 1, i);
	    		int numberOfLogicalProcessors = Runtime.getRuntime().availableProcessors() -1;
		    	System.out.println("--computation with: " + numberOfLogicalProcessors + " logical processors");
		    	String[] answer = currWorld.CreatePersonPopulationPoolLocalLevelMultiThreadsBatch(Utils.DATA_DIR + "myPersonPool.csv", pathToSeeds,5);
				currWorld = null;
				//System.out.println(answer[1]);
		    	localStatAnalysis.myFileWritter.write(answer[0]);
	            population.myFileWritter.write(answer[1]);
	            ConfigFile.resetConfigFile();
	    	}

            localStatAnalysis.CloseFile();
	    	population.CloseFile();*/
	    	
	    	//############################################################################################
	    	//prepare OD data for modeling no multithreading, input: travel survey as CSV file
	    	//############################################################################################
	    	//odPreparator.processData( 1 );
	    	
	    	//############################################################################################
	    	//prepare OD data for modeling using multithreading, input: travel survey as CSV file
	    	//############################################################################################
	    	
	    	//BE CAREFUL !!! By doing multithreading I am assuming that my different sub sample does not interact with each other.
	    	//However, the time consuming operation I have been trying to avoid is going through all possible alternatives and finding the closest ones.
	    	//Therefore, it is no honest to separate in subsample, because by doing so I am assuming that alternatives in the other samples are not reachable.
	    	//Which is false. Therefore, the non multithreading function should be used. (it is 8 hours against 1/2 hours).
	    	odGatineau.initialize("D:\\Recherche\\modelGatineau\\odGatineau.csv");
	    	int numberOfLogicalProcessors = Runtime.getRuntime().availableProcessors() -1;
	    	numberOfLogicalProcessors = 1;
	    	System.out.println("--computation with: " + numberOfLogicalProcessors + " logical processors");
	    	//odPreparator.processDataMultiThreads(numberOfLogicalProcessors, 7);
	    	odGatineau.processDataMultiThreads(numberOfLogicalProcessors, 8, 
	    			Utils.DATA_DIR + "\\ctrl\\biogeme_ctrl_file.txt", 
	    			Utils.DATA_DIR + "\\biogeme\\biogeme_input_prepared.mod",
	    			Utils.DATA_DIR + "\\ctrl\\biogeme_hypothesis_desc.txt");
	    	
	    	
	    	//############################################################################################
	    	//Load Agents and simulate their choices
	    	//############################################################################################
	    	/*odGatineauValidation = new BiogemeSimulator( 
	    			Utils.DATA_DIR + "\\ctrl\\biogeme_ctrl_file.txt", 
	    			Utils.DATA_DIR + "\\ctrl\\biogeme_hypothesis_desc.txt");
	    	System.out.println("--initialization of simulator");
	    	odGatineauValidation.initialize(Utils.DATA_DIR + "\\odGatineau_prepared.csv");
	    	System.out.println("--loading hypothesis and cases");
	    	odGatineauValidation.importBiogemeModel(Utils.DATA_DIR + "\\biogeme\\biogeme_input_prepared.F12");
	    	System.out.println("--importing model");
	    	odGatineauValidation.biogemeGenerator.printChoiceIndex(Utils.DATA_DIR + "\\biogeme\\choiceIndex.csv");
	    	odGatineauValidation.applyModelOnTravelSurveyPopulation(Utils.DATA_DIR + "\\biogeme\\simulationResults.csv");
	    	System.out.println("--applied");
	    	odGatineauValidation.printHypothesis(Utils.DATA_DIR + "\\biogeme\\hypothesisIndex.csv");
	    	
	    	//###############################################################################
	    	//Generate a synthetic population and output statistical analysis of the goodness
	    	//of fit for Gatineau
	    	//###############################################################################
	    	/*myWorld.Initialize(true, 1);
	    	System.out.println("--initialization completed");
	    	String pathToSeeds = Utils.DATA_DIR+"data\\CMA505PUMF2006.csv";
			//myWorld.CreatePersonPopulationPoolMetroLevel(Utils.DATA_DIR + "myPersonPool.csv", pathToSeeds);
	    	//myWorld.CreatePersonPopulationPoolLocalLevel(Utils.DATA_DIR + "myPersonPool.csv", pathToSeeds);
	    	
	    	int numberOfLogicalProcessors = Runtime.getRuntime().availableProcessors() -1;
	    	System.out.println("--computation with: " + numberOfLogicalProcessors + " logical processors");
	    	myWorld.CreatePersonPopulationPoolLocalLevelMultiThreads(Utils.DATA_DIR + "myPersonPool.csv", pathToSeeds,numberOfLogicalProcessors);
			myWorld.printMetroMarginalFittingAnalysis(UtilsTS.city, startTime);*/
	    	
			
			//###############################################################################
	    	//Generate a synthetic population and output statistical analysis of the goodness
	    	//of fit for Vancouver
	    	//###############################################################################
			/*myWorld.Initialize(true, 1);
	    	System.out.println("--initialization completed");
	    	String pathToSeeds = Utils.DATA_DIR+"data\\ImportanceSamplingConditionals\\Vancouver.txt";
			//myWorld.CreatePersonPopulationPoolMetroLevel(Utils.DATA_DIR + "myPersonPool.csv", pathToSeeds);
	    	//myWorld.CreatePersonPopulationPoolLocalLevel(Utils.DATA_DIR + "myPersonPool.csv", pathToSeeds);
	    	
	    	int numberOfLogicalProcessors = Runtime.getRuntime().availableProcessors() -1;
	    	System.out.println("--computation with: " + numberOfLogicalProcessors + " logical processors");
	    	myWorld.CreatePersonPopulationPoolLocalLevelMultiThreads(Utils.DATA_DIR + "myPersonPool.csv", pathToSeeds,numberOfLogicalProcessors);
			myWorld.printMetroMarginalFittingAnalysis("Vancouver", startTime);*/
	    	
	    	//###############################################################################
	    	//Uses OD survey, extract information from it and apply the model specified
	    	//##############################################################################
	    	/*odPreparator.storeData();
			System.out.println("--data stored");
			odPreparator.processMobility();
			System.out.println("--mobility processed");
			odPreparator.processTourTypes();
			System.out.println("--tour types processed");
			odPreparator.processModalClass();
			System.out.println("--modal class and fidelity to public transit processed");
			odPreparator.processLastDepartureHour();
			System.out.println("--last departure hours processed ");
			odPreparator.processAverageTourLength();
			System.out.println("--average tour length processed");
			odPreparator.processNumberOfKids();
			System.out.println("--number of kids in household processed");
			odPreparator.processActivityDuration();
			System.out.println("--max activity duration computed");
			odPreparator.processMinMaxTourLength();
			System.out.println("-- min and max distance for a trip processed");
			odPreparator.processMotorRate();
			System.out.println("--motorazation rate computed");
			odPreparator.processDummies();
			odPreparator.nActivitiesSimulation();
			odPreparator.firstDepartureSimulation();*/
	    	
	    	
	    	
	    	
			
	    	
	    	
	    	//Generate conditional distributions at metro level for Vancouver
	    	/*String data = Utils.DATA_DIR+"data\\ImportanceSamplingConditionals\\Vancouver.txt";
	    	String descFile = Utils.DATA_DIR+"data\\ImportanceSamplingConditionals\\descFile.txt";
	    	String zonalData = "D://Recherche//CharlieWorkspace//PopSynz//data//933VancouverDA.csv";
	    	String  destPath = "933";
	    	condGenerator.GenerateConditionalsStepByStep(data,descFile,zonalData,destPath);*/
	    	
	    	
	    	
	    	/*String data = Utils.DATA_DIR+"data\\ImportanceSamplingConditionals\\Vancouver.txt";
	    	String descFile = Utils.DATA_DIR+"data\\ImportanceSamplingConditionals\\descFile.txt";
	    	String zonalData = "D://Recherche//CharlieWorkspace//PopSynz//data//933VancouverDA.csv";
	    	String  destPath = "933";
	    	condGenerator.GenerateConditionalsStepByStep(data,descFile,zonalData,destPath);*/
	    	
	    	
	    	
	    	
	    	
		
	    }
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
	    
	    long endTime = System.currentTimeMillis();
		
		System.out.println("--time to compute age x gender : "+ (endTime-startTime) + "ms");
    }

	
		// TODO Auto-generated method stub
		
	}

