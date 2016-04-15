package SimPSynz;



import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;

import org.omg.CORBA.Environment;

import ActivityChoiceModel.BiogemeControlFileGenerator;
import ActivityChoiceModel.BiogemeSimulator;
import ActivityChoiceModel.CensusPreparator;
import ActivityChoiceModel.TravelSurveyPreparator;
import ActivityChoiceModel.UtilsTS;
import Associations.HungarianAlgoRithmOptimized;
import Associations.HungarianAlgorithm;
import SRMSE.JointDistributionTravelSurvey;
import SRMSE.SRMSE;
import SimulationObjects.World;
import Smartcard.PublicTransitSystem;
import Smartcard.UtilsSM;
import Smartcard.UtilsST;

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
    	System.out.println(Double.MAX_VALUE);
    	long startTime = System.currentTimeMillis();
	    World myWorld = new World(505);
	    ConditionalGenerator condGenerator = new ConditionalGenerator();
	    
	    //TravelSurveyPreparator odPreparator = new TravelSurveyPreparator("D:\\Recherche\\model\\model\\ODprepared.csv");
	    TravelSurveyPreparator odGatineau = new TravelSurveyPreparator();
	    BiogemeSimulator odGatineauValidation;
	    //BiogemeControlFileGenerator myCtrlGenerator = new BiogemeControlFileGenerator();
	    BiogemeSimulator mySimulator;
	    
	    String city = "Gatineau";
	    UtilsTS odDictionnary = new UtilsTS(city);
	    Utils utils = new Utils(city);
	    UtilsSM utilsSM = new UtilsSM();
	    
	    String workingDir = System.getProperty("user.dir");
		System.out.println("Current working directory : " + workingDir);
	   
	    
	    
	    //PublicTransitSystem myPublicTransitSystem = new PublicTransitSystem();
		
		
	    
	    
	    try {
	    	
	    	
	    	//###############################################################################
	    	//Create conditional distributions at the metro level from PUMF
	    	//###############################################################################
	    	/*String data = Utils.DATA_DIR + "data\\CMA505PUMF2006NEW.csv";
	    	String descFile = Utils.DATA_DIR + "ctrl\\descFile.txt";
	    	String zonalData = "D:\\Recherche\\modelGatineau\\gatineau_zonalFile.csv";// should be removed from the function
	    	String destPath = Utils.DATA_DIR + "data\\505\\PUMF";
	    	condGenerator.GenerateConditionalsStepByStep(data,descFile,zonalData,destPath);*/
	    	
	    	//###############################################################################
	    	//Create conditional distributions at the metro level from OD survey (for 
	    	//occupation and car ownership distribution
	    	//###############################################################################
	    	/*String data = Utils.DATA_DIR + "data\\GATINEAU505OD2005NEW.csv";
	    	String descFile =Utils.DATA_DIR + "ctrl\\descFile.txt";
	    	String zonalData = "D:\\Recherche\\modelGatineau\\gatineau_zonalFile.csv";// should be removed
	    	String destPath = Utils.DATA_DIR + "data\\505\\OD";
	    	condGenerator.GenerateConditionalsStepByStep(data,descFile,zonalData,destPath);*/
	    	
	    	//###############################################################################
	    	//create local conditional distributions
	    	//###############################################################################
	    	/*CensusPreparator census = new CensusPreparator(Utils.DATA_DIR + "CENSUS2006DAAROUNDSTOP.csv");
	    	census.prepareDataColumnStorage();*/
	    	
	    	//###############################################################################
	    	//Create the zonal input file for population sinthesis (DAUID , Population)
	    	//###############################################################################
	    	
	    	/*CensusPreparator census = new CensusPreparator(Utils.DATA_DIR + "CENSUS2006DAAROUNDSTOP.csv");
	    	System.out.println("--census file was found"); 
	     	int nBatch = 15;
	    	census.writeZonalInputFile(nBatch);	
	    	census.writeCtrlFile(nBatch);
	    	
	    	//###############################################################################
	    	//Synthetic population generator by batches
	    	//###############################################################################
	    	// BE SURE YOU ARE USING THE RIGTH DISTRIBUTIONS FROM THE RIGTH DATASET
	    	
	    	String pathToSeeds = Utils.DATA_DIR + "data\\CMA505PUMF2006.csv";
	    	myWorld.Initialize(true, 1);// we need this for writing headers
	    		    	
	    	//Initialize the statistical log
            OutputFileWritter localStatAnalysis = new OutputFileWritter();
            localStatAnalysis.OpenFile(Utils.DATA_DIR + "data\\505\\localStatAnalysis.csv");
            String headers =UtilsSM.zoneId + Utils.COLUMN_DELIMETER + Utils.population;
            for(int i = 0; i < ConfigFile.AttributeDefinitionsImportance.size(); i++){
    			headers = headers + Utils.COLUMN_DELIMETER + ConfigFile.AttributeDefinitionsImportance.get(i).category + "SRMSE"
    					 + Utils.COLUMN_DELIMETER + ConfigFile.AttributeDefinitionsImportance.get(i).category + "TAE_DA"
    							 + Utils.COLUMN_DELIMETER + ConfigFile.AttributeDefinitionsImportance.get(i).category + "%SAE_DA" ;
            }
            for(int i = 0; i < ConfigFile.AttributeDefinitionsImportance.size(); i++){
            	for(int j = 0 ; j < ConfigFile.AttributeDefinitionsImportance.get(i).value; j++){
            		headers = headers + Utils.COLUMN_DELIMETER  + ConfigFile.AttributeDefinitionsImportance.get(i).category + j + "TAE";
            	}
            }
            for(int i = 0; i < ConfigFile.AttributeDefinitionsImportance.size(); i++){
            	for(int j = 0 ; j < ConfigFile.AttributeDefinitionsImportance.get(i).value; j++){
            		headers = headers + Utils.COLUMN_DELIMETER  + ConfigFile.AttributeDefinitionsImportance.get(i).category + "SAE";
            	}
            }
            for(int i = 0; i < ConfigFile.AttributeDefinitionsImportance.size(); i++){
            	for(int j = 0 ; j < ConfigFile.AttributeDefinitionsImportance.get(i).value; j++){
            		headers = headers + Utils.COLUMN_DELIMETER  + ConfigFile.AttributeDefinitionsImportance.get(i).category + j + "Target";
            	}
            }
            for(int i = 0; i < ConfigFile.AttributeDefinitionsImportance.size(); i++){
            	for(int j = 0 ; j < ConfigFile.AttributeDefinitionsImportance.get(i).value; j++){
            		headers = headers + Utils.COLUMN_DELIMETER  + ConfigFile.AttributeDefinitionsImportance.get(i).category + j + "Result";
            	}
            }
            
            localStatAnalysis.myFileWritter.write(headers);
            
            // Initialize the population pool log
            OutputFileWritter population =  new OutputFileWritter();
        	population.OpenFile(Utils.DATA_DIR + "data\\505\\createdPopulation.csv");
        	headers = "agentId" + Utils.COLUMN_DELIMETER + UtilsSM.zoneId;
            for(int i = 0; i < ConfigFile.AttributeDefinitions.size(); i++){
    			headers = headers + Utils.COLUMN_DELIMETER + ConfigFile.AttributeDefinitions.get(i).category ;
            }
            population.myFileWritter.write(headers);
            
            ConfigFile.resetConfigFile();
            myWorld = null;
	    	
            //Create batches
	    	for(int i = 0; i < nBatch; i++){
	    		
	    		
	    		World currWorld = new World(505);
	    		currWorld.Initialize(true, 1, i);
	    		int numberOfLogicalProcessors = Runtime.getRuntime().availableProcessors() -3;
		    	System.out.println("--computation with: " + numberOfLogicalProcessors + " logical processors");
		    	String[] answer = currWorld.CreatePersonPopulationPoolLocalLevelMultiThreadsBatch(Utils.DATA_DIR + "myPersonPool.csv", pathToSeeds,numberOfLogicalProcessors);
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
	    	//Load hypothesis and dimension for the Joint model with Biogeme
	    	//############################################################################################
	    	
	    	String pathControlFile =Utils.DATA_DIR + "biogeme\\ctrl\\biogeme_ctrl_file_with_nest.txt";
			String pathOutput = Utils.DATA_DIR + "\\biogeme\\ctrlWithAccess2.mod";
			String pathHypothesis = Utils.DATA_DIR + "biogeme\\ctrl\\hypoMultiNestWithAccess2.txt";
			//String pathHypothesis = Utils.DATA_DIR + "biogeme\\ctrl\\noHypo.txt";
			BiogemeControlFileGenerator myCtrlGenerator = new BiogemeControlFileGenerator();
	    	myCtrlGenerator.initialize(pathControlFile, pathHypothesis);
			myCtrlGenerator.generateBiogemeControlFile(pathOutput);
			//myCtrlGenerator.printChoiceIndex(Utils.DATA_DIR + "biogeme\\choiceIndex.csv");
			System.out.println("-- control file generator initiated");
	    	
			//############################################################################################
	    	//prepare OD data for modeling using multithreading, input: travel survey as CSV file
	    	// Also prepare OD data for creating conditional distribution using the conditionalGenerator.
	    	//############################################################################################
	    	
	    	//BE CAREFUL !!! By doing multithreading I am assuming that my different sub sample does not interact with each other.
	    	//However, the time consuming operation I have been trying to avoid is going through all possible alternatives and finding the closest ones.
	    	//Therefore, it is no honest to separate in subsample, because by doing so I am assuming that alternatives in the other samples are not reachable.
	    	//Which is false. Therefore, the non multithreading function should be used. (it is 8 hours against 1/2 hours).
			
	    	/*odGatineau.initialize(Utils.DATA_DIR + "\\odGatineau.csv");
	    	int numberOfLogicalProcessors = Runtime.getRuntime().availableProcessors() -1;
	    	numberOfLogicalProcessors = 1;
	    	System.out.println("--computation with: " + numberOfLogicalProcessors + " logical processors");
	    	odGatineau.processDataMultiThreads(numberOfLogicalProcessors, 20, myCtrlGenerator);
	    	
	    	//############################################################################################
	    	//Load Agents and simulate their choices from the travel survey
	    	//############################################################################################
	    	
			//BE CAREFUL : HYPOTHESIS SHOULD NOT BE CHANGED, HOWEVER IT IS IMPORTANT TO EDIT THE CONTROL FILE
			//BEFORE CALIBRATING THE MODEL WITH BIOGEME : THE FIXED PARAMETER SHOULD BE CHOOSEN, DUMMIES SHOULD SPECIFIED
			//AND 
			String pathToModel = Utils.DATA_DIR + "ptSystem\\ctrl11~2.F12";
			mySimulator = new BiogemeSimulator(myCtrlGenerator);
			mySimulator.initialize(Utils.DATA_DIR + "biogeme\\pureData.csv");
			mySimulator.importBiogemeModel(pathToModel);
			mySimulator.importNest(pathToModel);
			mySimulator.applyModelOnTravelSurveyPopulation(Utils.DATA_DIR + "biogeme\\ageCriterion6.csv",1, true);*/
			
			//############################################################################################
	    	//Load Smartcard data and process them to label with a choice id
	    	//############################################################################################
	    	/*String pathControlFile =Utils.DATA_DIR + "biogeme\\ctrl\\biogeme_ctrl_file_with_nest.txt";
			String pathOutput = Utils.DATA_DIR + "\\biogeme\\ctrl11~2.mod";
			String pathHypothesis = Utils.DATA_DIR + "biogeme\\ctrl\\hypoMultiNests11.txt";
			
			BiogemeControlFileGenerator myCtrlGenerator = new BiogemeControlFileGenerator();
			
			PublicTransitSystem myPublicTransitSystem = new PublicTransitSystem();
			
	    	myCtrlGenerator.initialize(pathControlFile, pathHypothesis);
			//myCtrlGenerator.generateBiogemeControlFile();
			//myCtrlGenerator.printChoiceIndex(Utils.DATA_DIR + "biogeme\\choiceIndex.csv");
			System.out.println("-- control file generator initiated");
			
			myPublicTransitSystem.initialize(
					myCtrlGenerator, 
					Utils.DATA_DIR + "ptSystem\\smartcardData.txt", 
					Utils.DATA_DIR + "ptSystem\\stops.txt",
					Utils.DATA_DIR + "ptSystem\\geoDico500.csv",
					Utils.DATA_DIR + "ptSystem\\population.csv",
					Utils.DATA_DIR + "ptSystem\\ctrl11~2.F12"
					);
			myPublicTransitSystem.getValues();
			System.out.println("--pt system initialized");
			myPublicTransitSystem.createZonalSmartcardIndex();
			myPublicTransitSystem.createZonalPopulationIndex();
			
			//myPublicTransitSystem.printStation(Utils.DATA_DIR + "ptSystem\\station_smartcard.csv");
			System.out.println("--potential smartcard assigned");
			
			//########
			Utils.occupationCriterion = true;
			//myPublicTransitSystem.processMatchingStationByStation();
			//myPublicTransitSystem.processMatchingOnPtRiders();
			myPublicTransitSystem.processMatchingOnPtRidersByBatch(3);
			myPublicTransitSystem.printSmartcards(Utils.DATA_DIR + "ptSystem\\matchedSMWithCHoiceSetWithBetterLivingLocation.csv");*/
			
			
			

			//###############################################################################
	    	//COMPUTE THE SRMSE BETWEEN TWO DATA SETS
	    	//###############################################################################
			//String pathData = Utils.DATA_DIR + "SRMSE//matchedSMGlobalRandom.csv";
			//String pathData = Utils.DATA_DIR + "SRMSE//matchedSMLocalRandom.csv";
			//String pathData = Utils.DATA_DIR + "SRMSE//matchedSMWithoutChoiceSet.csv";
			//String pathData = Utils.DATA_DIR + "SRMSE//matchedSMWithChoiceSet.csv";
			//String pathData = Utils.DATA_DIR + "SRMSE//matchedSMWithCHoiceSetWithBetterLivingLocation.csv";
	    	/*String pathData = Utils.DATA_DIR + "SRMSE//matchedSMWithoutChoiceSet4.csv";
			String pathRef = Utils.DATA_DIR + "SRMSE//odStoUsers.csv";
	    	SRMSE srmse = new SRMSE();
			srmse.getDistributions(pathData, pathRef);
			double temp = srmse.computeSRMSE();
			System.out.println(temp);

	    	
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

