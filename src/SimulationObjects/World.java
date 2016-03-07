 package SimulationObjects;

import Samplers.GibbsSampler;
import SimulationObjects.AgentAttributes.*;
import Utils.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.*;
import java.util.concurrent.*;
import java.lang.Math;

import com.sun.jmx.snmp.internal.SnmpAccessControlSubSystem;


/*
 * created by: b farooq, poly montreal
 * on: 22 october, 2013
 * last edited by: b farooq, poly montreal
 * on: 22 october, 2013
 * summary: 
 * comments:
 */
public class World extends SimulationObject
{
	
        private DiscreteCondDistribution mobelWrkrsConditionals;
        private DiscreteCondDistribution mobelKidsConditionals;
        private DiscreteCondDistribution mobelPersConditionals;
        
        private ArrayList<DiscreteCondDistribution> attributesMainConditionals = new ArrayList<DiscreteCondDistribution>();

        private InputDataReader mobelWrkrsFileReader;
        private InputDataReader mobelKidsFileReader;
        private InputDataReader mobelPersFileReader;

        private InputDataReader censusDwellFileReader;
        private InputDataReader censusCarFileReader;
        private InputDataReader censusPersonFileReader;
        private InputDataReader censusUnivDegFileReader;
        
        private ArrayList<InputDataReader> censusAttributesFileReader = new ArrayList<InputDataReader>();
        private ArrayList<InputDataReader> mainAttributesFileReader = new ArrayList<InputDataReader>();
        private ArrayList<InputDataReader> importanceAttributesFileReader = new ArrayList<InputDataReader>();
        private ArrayList<InputDataReader> modelAttributesFileReader = new ArrayList<InputDataReader>();
       
        private OutputFileWritter agentsOutputFile;

        public HashMap<String, Object> myZonalCollection;
        private ArrayList myHhldsPool;
        private ArrayList myPersonPool;

        private static int idCounter = 0;

        private HashMap<String, Object> zonalControlTotals;
        private static GibbsSampler myGibbsSampler;
        
        private static int numofcounts;
        private static int numofmodels;
        
        public HashMap<String, DiscreteMarginalDistribution> marginalCounters = new HashMap<String, DiscreteMarginalDistribution>();
        public HashMap<String, DiscreteMarginalDistribution> marginalTargets = new HashMap<String, DiscreteMarginalDistribution>();
        
        //structure: key1: attribute (age, sex..) key2: local area (zone 1, zone2), value: standard deviation or absolute errors => allows further analysis
        public HashMap<String, HashMap> localStandardDeviations = new HashMap<String, HashMap>(); 
        public HashMap<String, HashMap> localAbsoluteErrors = new HashMap<String, HashMap>();
        

        public World()
        {
            myID = ++idCounter;
            myHhldsPool = new ArrayList();
            myPersonPool = new ArrayList();
            myZonalCollection = new HashMap<String, Object>();

            myGibbsSampler = new GibbsSampler();
            agentsOutputFile = new OutputFileWritter();
        }
        
        
        public World(double id){
        	myID = ++idCounter;
            myHhldsPool = new ArrayList();
            myPersonPool = new ArrayList();
            myZonalCollection = new HashMap<String, Object>();

            myGibbsSampler = new GibbsSampler();
            agentsOutputFile = new OutputFileWritter();
            KeyValPair i = new KeyValPair();
            i.category = "id";
            i.value = id;
            myAttributes = new ArrayList<KeyValPair>();
            myAttributes.add(i);
            
            
        }

        public void Initialize(boolean createPool, int currType) throws IOException
        {
        	
        	ConfigFile.InitializeImportance(Utils.DATA_DIR+"\\ctrl\\config.txt");
        	System.out.println("--population synthesis successfully configured");
        	
        	InitializeInputDataImportance(currType);
        	System.out.println("--data readers successfully created");
        	
            if (createPool == true)
            {
                LoadZones(currType);
                LoadZonalDataImportance(currType); //redundancy  with load zones ?
                
            }
            else
            {
             /*   if (currType != AgentType.Person)
                {
                	System.out.println("Non person type detected"); // just to make sure of which buckle we are in
                    InputDataReader currReader = new InputDataReader();
                    currReader.OpenFile(Utils.DATA_DIR
                            + "Household\\CensusHhldCountByDwell.csv");
                    zonalControlTotals = new HashMap<String, Object>();
                    currReader.FillControlTotalsByDwellType(zonalControlTotals);
                    currReader.CloseFile();
                }*/
            System.out.println("--functionnality not implemented");
            }
            
            //initialize marginal counters and marginal target (used for evaluating the synthesized population)
            
            int numberofmodels = 1;
        	int numberofcounts = 1;
            for (int i=0; i<ConfigFile.AttributeDefinitions.size(); i++)
            {	
            	if (ConfigFile.TypeOfConditionals.get(i).equals("count"))
            	{ 
            		numberofcounts += numberofcounts;
            		            		
            		//System.out.println(ConfigFile.AttributeDefinitionsImportance.get(i-numberofmodels+1).category);
            		DiscreteMarginalDistribution currCounter = new DiscreteMarginalDistribution();
            		currCounter.SetDimensionName(ConfigFile.AttributeDefinitions.get(i-numberofmodels+1).category);
            		marginalCounters.put(ConfigFile.AttributeDefinitions.get(i-numberofmodels+1).category, currCounter);
            		
            		/*DiscreteMarginalDistribution currTarget = new DiscreteMarginalDistribution();
            		currTarget.SetDimensionName(ConfigFile.AttributeDefinitions.get(i-numberofmodels+1).category);
            		marginalTargets.put(ConfigFile.AttributeDefinitions.get(i-numberofmodels+1).category, currTarget);*/
            		
            		/*for(int j= 0; j< ConfigFile.AttributeDefinitionsImportance.get(i).value ; j++){
            			
            		}*/
            	}
            	
            	else if (ConfigFile.TypeOfConditionalsImportance.get(i).equals("model"))
            	{
            		/*numberofmodels += numberofmodels;
            		
            		ModelDistribution myAttributeModel = new ModelDistribution();
                	myAttributeModel.SetDimensionName(ConfigFile.AttributeDefinitions.get(i-numberofcounts+1).category);
                	myAttributeModel.add(myAttributeModel);*/
            		System.out.println("--functionnality not implemented");
            	}    		
            }
        }
        
        public void Initialize(boolean createPool, int currType, int batchId) throws IOException
        {
        	
        	ConfigFile.InitializeImportance(Utils.DATA_DIR+"\\ctrl\\config" + batchId +".txt");
        	System.out.println("--population synthesis successfully configured");
        	
        	InitializeInputDataImportance(currType);
        	System.out.println("--data readers successfully created");
        	
            if (createPool == true)
            {
                LoadZones(currType);
                LoadZonalDataImportance(currType); //redundancy  with load zones ?
                
            }
            else
            {
             /*   if (currType != AgentType.Person)
                {
                	System.out.println("Non person type detected"); // just to make sure of which buckle we are in
                    InputDataReader currReader = new InputDataReader();
                    currReader.OpenFile(Utils.DATA_DIR
                            + "Household\\CensusHhldCountByDwell.csv");
                    zonalControlTotals = new HashMap<String, Object>();
                    currReader.FillControlTotalsByDwellType(zonalControlTotals);
                    currReader.CloseFile();
                }*/
            System.out.println("--functionnality not implemented");
            }
            
            //initialize marginal counters and marginal target (used for evaluating the synthesized population)
            
            int numberofmodels = 1;
        	int numberofcounts = 1;
            for (int i=0; i<ConfigFile.AttributeDefinitions.size(); i++)
            {	
            	if (ConfigFile.TypeOfConditionals.get(i).equals("count"))
            	{ 
            		numberofcounts += numberofcounts;
            		            		
            		//System.out.println(ConfigFile.AttributeDefinitionsImportance.get(i-numberofmodels+1).category);
            		DiscreteMarginalDistribution currCounter = new DiscreteMarginalDistribution();
            		currCounter.SetDimensionName(ConfigFile.AttributeDefinitions.get(i-numberofmodels+1).category);
            		marginalCounters.put(ConfigFile.AttributeDefinitions.get(i-numberofmodels+1).category, currCounter);
            		
            		/*DiscreteMarginalDistribution currTarget = new DiscreteMarginalDistribution();
            		currTarget.SetDimensionName(ConfigFile.AttributeDefinitions.get(i-numberofmodels+1).category);
            		marginalTargets.put(ConfigFile.AttributeDefinitions.get(i-numberofmodels+1).category, currTarget);*/
            		
            		/*for(int j= 0; j< ConfigFile.AttributeDefinitionsImportance.get(i).value ; j++){
            			
            		}*/
            	}
            	
            	else if (ConfigFile.TypeOfConditionalsImportance.get(i).equals("model"))
            	{
            		/*numberofmodels += numberofmodels;
            		
            		ModelDistribution myAttributeModel = new ModelDistribution();
                	myAttributeModel.SetDimensionName(ConfigFile.AttributeDefinitions.get(i-numberofcounts+1).category);
                	myAttributeModel.add(myAttributeModel);*/
            		System.out.println("--functionnality not implemented");
            	}    		
            }
        }

        private void InitializeInputData(int currType)
        {
            if (currType == AgentType.Household)
            {
                mobelWrkrsConditionals = new DiscreteCondDistribution();
                mobelKidsConditionals = new DiscreteCondDistribution();
                mobelPersConditionals = new DiscreteCondDistribution();

                mobelWrkrsFileReader = new InputDataReader();
                mobelKidsFileReader = new InputDataReader();
                mobelPersFileReader = new InputDataReader();

                censusUnivDegFileReader = new InputDataReader();
                censusDwellFileReader = new InputDataReader();
                censusCarFileReader = new InputDataReader();
                censusPersonFileReader = new InputDataReader();
            }
            else if (currType == AgentType.Person)
            {
            	for (int i=0; i< ConfigFile.AttributeDefinitions.size(); i++) 
            	{
            		if (ConfigFile.TypeOfConditionals.get(i).equals("count"))
            		{
            		InputDataReader censusAttributeFileReader = new InputDataReader();
            		censusAttributesFileReader.add(censusAttributeFileReader);
            		}
            		
            		else if(ConfigFile.TypeOfConditionals.get(i).equals("model"))
            		{
            		InputDataReader modelAttributeFileReader = new InputDataReader();
            		modelAttributesFileReader.add(modelAttributeFileReader);
            		}
            	}
            }
        }
        
        private void InitializeInputDataImportance(int currType)
        {
        	int numberofmodels = 1;
            if (currType == AgentType.Household)
            {
                mobelWrkrsConditionals = new DiscreteCondDistribution();
                mobelKidsConditionals = new DiscreteCondDistribution();
                mobelPersConditionals = new DiscreteCondDistribution();

                mobelWrkrsFileReader = new InputDataReader();
                mobelKidsFileReader = new InputDataReader();
                mobelPersFileReader = new InputDataReader();

                censusUnivDegFileReader = new InputDataReader();
                censusDwellFileReader = new InputDataReader();
                censusCarFileReader = new InputDataReader();
                censusPersonFileReader = new InputDataReader();
            }
            else if (currType == AgentType.Person)
            {
            	for (int i=0; i< ConfigFile.AttributeDefinitions.size(); i++) 
            	{
            		if (ConfigFile.TypeOfConditionals.get(i).equals("count"))
            		{
            		InputDataReader attributeFileReader = new InputDataReader();
            		mainAttributesFileReader.add(attributeFileReader);
            		
            		//create world level discrete conditionals
            		DiscreteCondDistribution myAttributeDiscConditional = new DiscreteCondDistribution();
            		myAttributeDiscConditional.SetDimensionName(ConfigFile.AttributeDefinitions.get(i-numberofmodels+1).category);
            		attributesMainConditionals.add(myAttributeDiscConditional);
            		
            		//Create targets counter
            		DiscreteMarginalDistribution myTarget = new DiscreteMarginalDistribution();
            		myTarget.SetDimensionName(ConfigFile.AttributeDefinitions.get(i-numberofmodels+1).category);
            		marginalTargets.put(ConfigFile.AttributeDefinitions.get(i-numberofmodels+1).category, myTarget);
            		
            		//create standard deviation dataset and aboslute error datasets
            		localStandardDeviations.put(ConfigFile.AttributeDefinitions.get(i-numberofmodels+1).category, new HashMap<String, Object>());
            		localAbsoluteErrors.put(ConfigFile.AttributeDefinitions.get(i-numberofmodels+1).category, new HashMap<String, Object>());
            		}
            		
            		/*else if(ConfigFile.TypeOfConditionals.get(i).equals("model"))
            		{
            			
            		InputDataReader modelAttributeFileReader = new InputDataReader();
            		modelAttributesFileReader.add(modelAttributeFileReader);
            			System.out.println("--functionnality for households was not implemented");
            		}*/
            	}
            	for (int i=0; i< ConfigFile.AttributeDefinitionsImportance.size(); i++) 
            	{
            		if (ConfigFile.TypeOfConditionalsImportance.get(i).equals("count"))
            		{
            		InputDataReader attributeFileReader = new InputDataReader();
            		importanceAttributesFileReader.add(attributeFileReader);
            		}
            		
            		else if(ConfigFile.TypeOfConditionalsImportance.get(i).equals("model"))
            		{
            			/*
            		InputDataReader modelAttributeFileReader = new InputDataReader();
            		modelAttributesFileReader.add(modelAttributeFileReader);*/
            			System.out.println("--functionnality for households was not implemented");
            		}
            	}
            }
        }

        public void LoadZonalData(int currType) throws IOException
        //avant de commencer, myZonalCollection est constitué de 1) un identifiant et 2) un object qui le KeyValPair (nom de categorie/valeur de la proba ou du comptage)
        //myZonalCOllection a été créé a partir du fichier zonal
        {
            if (currType == AgentType.Household)
            {
                LoadMobelData();
                LoadMarginalsForCars();
                LoadMarginalsForDwellings();
                LoadMarginalsForPersons();
            }
            else if (currType == AgentType.Person)
            {
                for (Map.Entry<String, Object> ent : myZonalCollection.entrySet())
                {
                    
                    SpatialZone currZone = (SpatialZone)ent.getValue();
                    OpenFiles(currType, currZone);
                    LoadPesronData(currZone);
                    CloseFiles(currType);
                }
              //  LoadMarginalsForAge();
              //  LoadMarginalsForSex();
              //  LoadMarginalsForHhldSize2();
              //  LoadMarginalsForEducation();

            }
        }
        
        public void LoadZonalDataImportance(int currType) throws IOException
        //avant de commencer, myZonalCollection est constitué de 1) un identifiant et 2) un object qui le KeyValPair (nom de categorie/valeur de la proba ou du comptage)
        //myZonalCOllection a été créé a partir du fichier zonal
        {
            if (currType == AgentType.Household)
            {
                LoadMobelData();
                LoadMarginalsForCars();
                LoadMarginalsForDwellings();
                LoadMarginalsForPersons();
            }
            else if (currType == AgentType.Person)
            {
            	OpenFilesMain(currType);
                LoadPersonDataMain();
                CloseFilesMain(currType);
            	for (Map.Entry<String, Object> ent : myZonalCollection.entrySet())
                {
                    SpatialZone currZone = (SpatialZone)ent.getValue();
                    OpenFilesImportance(currType, currZone);
                    LoadPersonDataImportance(currZone);
                    CloseFilesImportance(currType);
                }
              //  LoadMarginalsForAge();
              //  LoadMarginalsForSex();
              //  LoadMarginalsForHhldSize2();
              //  LoadMarginalsForEducation();

            }
        }

        void LoadPesronData(SpatialZone currZone) throws IOException
        {

        	int numberofmodels = 1; // what does this represent ? (not number of models since this could be 0)
        	int numberofcounts = 1;
        	for (int i=0; i<ConfigFile.AttributeDefinitions.size(); i++)
        	{

        		if (ConfigFile.TypeOfConditionals.get(i).equals("count"))
        		{
        			{
        				numberofcounts += numberofcounts; // not sure this work +=1 ? (does not matter to me but still...)
        	
        				currZone.myAttributesDiscConditional.get(i-numberofmodels+1).FlushOutData();
            			censusAttributesFileReader.get(i-numberofmodels+1).FillCollection2(currZone.myAttributesDiscConditional.get(i-numberofmodels+1));
            			currZone.myAttributesDiscConditional.get(i-numberofmodels+1).SetDimensionName(ConfigFile.AttributeDefinitions.get(i).category);
            		
        			}
        		}
        		
        		
        		else if (ConfigFile.TypeOfConditionals.get(i).equals("model"))
        		{
        		
        			numberofmodels += numberofmodels;
        			
              		modelAttributesFileReader.get(i-numberofcounts+1).FillCollection3(currZone.myAttributesModel.get(i-numberofcounts+1),i-numberofcounts+1,currZone);
            		currZone.myAttributesModel.get(i-numberofcounts+1).SetDimensionName(ConfigFile.AttributeDefinitions.get(i).category);    		
        			}
        		}
        		
        }
        
        void LoadPersonDataImportance(SpatialZone currZone) throws IOException
        {

        	int numberofmodels = 1; // what does this represent ? (not number of models since this could be 0)
        	int numberofcounts = 1;
        	for (int i=0; i<ConfigFile.AttributeDefinitionsImportance.size(); i++)
        	{

        		if (ConfigFile.TypeOfConditionalsImportance.get(i).equals("count"))
        		{
        			{
        				numberofcounts += numberofcounts; // not sure this work +=1 ? (does not matter to me but still...)
        				currZone.myAttributesDiscConditional.get(i-numberofmodels+1).SetDimensionName(ConfigFile.AttributeDefinitionsImportance.get(i).category);
            			importanceAttributesFileReader.get(i-numberofmodels+1).FillCollection2(currZone.myAttributesDiscConditional.get(i-numberofmodels+1),
            					currZone.marginalTargets.get(currZone.myAttributesDiscConditional.get(i-numberofmodels+1).GetDimensionName()));
        				//importanceAttributesFileReader.get(i-numberofmodels+1).FillCollection2(currZone.myAttributesDiscConditional.get(i-numberofmodels+1));
            			
            		
        			}
        		}
        		
        		/*
        		else if (ConfigFile.TypeOfConditionalsImportance.get(i).equals("model"))
        		{
        		
        			numberofmodels += numberofmodels;
        			
              		modelAttributesFileReader.get(i-numberofcounts+1).FillCollection3(currZone.myAttributesModel.get(i-numberofcounts+1),i-numberofcounts+1,currZone);
            		currZone.myAttributesModel.get(i-numberofcounts+1).SetDimensionName(ConfigFile.AttributeDefinitions.get(i).category);    		
        		}*/
        	}
        		
        }
        
        void LoadPersonDataMain() throws IOException
        {

        	int numberofmodels = 1; // what does this represent ? (not number of models since this could be 0)
        	int numberofcounts = 1;
        	
        	for (int i=0; i<ConfigFile.AttributeDefinitions.size(); i++)
        	{

        		if (ConfigFile.TypeOfConditionals.get(i).equals("count"))
        		{
        			{
        				numberofcounts += numberofcounts; // not sure this work +=1 ? (does not matter to me but still...)
        	
        				attributesMainConditionals.get(i-numberofmodels+1).FlushOutData();
        				attributesMainConditionals.get(i-numberofmodels+1).SetDimensionName(ConfigFile.AttributeDefinitions.get(i).category);
        				marginalTargets.get(attributesMainConditionals.get(i-numberofmodels+1).GetDimensionName()).FlushOutData();
        				marginalTargets.get(attributesMainConditionals.get(i-numberofmodels+1).GetDimensionName()).SetDimensionName(ConfigFile.AttributeDefinitions.get(i).category);
        				
            			mainAttributesFileReader.get(i-numberofmodels+1).FillCollection2(attributesMainConditionals.get(i-numberofmodels+1),
            					marginalTargets.get(attributesMainConditionals.get(i-numberofmodels+1).GetDimensionName()));
        			
        			}
        		}
        		
        		
        		/*else if (ConfigFile.TypeOfConditionals.get(i).equals("model"))
        		{
        		
        			numberofmodels += numberofmodels;
        			
              		modelAttributesFileReader.get(i-numberofcounts+1).FillCollection3(currZone.myAttributesModel.get(i-numberofcounts+1),i-numberofcounts+1,currZone);
            		currZone.myAttributesModel.get(i-numberofcounts+1).SetDimensionName(ConfigFile.AttributeDefinitions.get(i).category);    		
        		}*/
        	}
        		
        }


        void LoadMobelData() throws IOException
        {
            mobelWrkrsFileReader.OpenFile(
                Utils.DATA_DIR + "Household\\MobelNbWrkr.csv");
            mobelWrkrsConditionals.FlushOutData();
            mobelWrkrsFileReader.FillCollection2(mobelWrkrsConditionals);
            mobelWrkrsConditionals.SetDimensionName("NumOfWorkers");
            mobelWrkrsFileReader.CloseFile();

            mobelKidsFileReader.OpenFile(
                Utils.DATA_DIR + "Household\\MobelNbKids.csv");
            mobelKidsConditionals.FlushOutData();
            mobelKidsFileReader.FillCollection2(mobelKidsConditionals);
            mobelKidsConditionals.SetDimensionName("NumOfKids");
            mobelKidsFileReader.CloseFile();

            mobelPersFileReader.OpenFile(
                Utils.DATA_DIR + "Household\\MobelNbPers.csv");
            mobelPersConditionals.FlushOutData();
            mobelPersFileReader.FillCollection2(mobelPersConditionals);
            mobelPersConditionals.SetDimensionName("HouseholdSize");
            mobelPersFileReader.CloseFile();
        }

        private void OpenFiles(int currType) throws IOException
        {
            if (currType == AgentType.Household)
            {
                censusPersonFileReader.OpenFile(
                    Utils.DATA_DIR + "\\Household\\CensusNumOfPers.csv");
                censusDwellFileReader.OpenFile(
                    Utils.DATA_DIR + "\\Household\\CensusDwellingType.csv");
                censusCarFileReader.OpenFile(
                    Utils.DATA_DIR + "\\Household\\CensusNumOfCars.csv");

                censusPersonFileReader.GetConditionalList();
                censusDwellFileReader.GetConditionalList();
                censusCarFileReader.GetConditionalList();
            }
            else if (currType == AgentType.Person)
            {
            	int numberofmodels = 1;
            	int numberofcounts = 1;
            	
            	for (int i=0; i<ConfigFile.AttributeDefinitions.size();i++)
            	{	
            		if (ConfigFile.TypeOfConditionals.get(i).equals("count"))
            		{
            				numberofcounts += numberofcounts;
            			
            			censusAttributesFileReader.get(i-numberofmodels+1).OpenFile(Utils.DATA_DIR + "\\"+ConfigFile.AttributesPaths.get(i));
            		}
            		else if(ConfigFile.TypeOfConditionals.get(i).equals("model"))
            		{
            			
            				numberofmodels += numberofmodels;
            			
            			modelAttributesFileReader.get(i-numberofcounts+1).OpenFile(Utils.DATA_DIR + "\\"+ConfigFile.AttributesPaths.get(i));
            		}
            	}
            	}
              
            	}
        
        //[AG] I made this OpenFiles function so that I can Open files specificly linked to a zone. If using the first openfiles functionnality, then you are basicly filling the same distribution for all your zones
        private void OpenFiles(int currType, SpatialZone currZone) throws IOException
        {
            if (currType == AgentType.Household)
            {
            	System.out.println("--functionnality not implemented");
            	/*
                censusPersonFileReader.OpenFile(
                    Utils.DATA_DIR + "\\Household\\CensusNumOfPers.csv");
                censusDwellFileReader.OpenFile(
                    Utils.DATA_DIR + "\\Household\\CensusDwellingType.csv");
                censusCarFileReader.OpenFile(
                    Utils.DATA_DIR + "\\Household\\CensusNumOfCars.csv");

                censusPersonFileReader.GetConditionalList();
                censusDwellFileReader.GetConditionalList();
                censusCarFileReader.GetConditionalList();*/
            }
            if (currType == AgentType.Person)
            {
            	int numberofmodels = 1;
            	int numberofcounts = 1;
            	
            	for (int i=0; i<ConfigFile.AttributeDefinitions.size();i++)
            	{	
            		if (ConfigFile.TypeOfConditionals.get(i).equals("count"))
            		{
            				numberofcounts += numberofcounts;
            			System.out.println(Utils.DATA_DIR + 
            					"data\\" + 
            					fmt(currZone.myAttributes.get(0).value) + // careful of the type that returns from the Double.toString : it had a ".0" to the values            					
            					"\\" +
            					ConfigFile.AttributesPaths.get(i));
            					
            			censusAttributesFileReader.get(i-numberofmodels+1).OpenFile(
            					Utils.DATA_DIR + 
            					"\\data\\" + 
            					fmt(currZone.myAttributes.get(0).value) +
            					"\\" +
            					ConfigFile.AttributesPaths.get(i));
            		}
            		else if(ConfigFile.TypeOfConditionals.get(i).equals("model"))
            		{	
            			/*	numberofmodels += numberofmodels;
            			modelAttributesFileReader.get(i-numberofcounts+1).OpenFile(Utils.DATA_DIR + "\\"+ConfigFile.AttributesPaths.get(i));*/
            			System.out.println("--functionnality not implemented");
            		}
            	}
            	}
              
            	}
        
        private void OpenFilesImportance(int currType, SpatialZone currZone) throws IOException
        {
            if (currType == AgentType.Household)
            {
            	System.out.println("--functionnality not implemented");
            	/*
                censusPersonFileReader.OpenFile(
                    Utils.DATA_DIR + "\\Household\\CensusNumOfPers.csv");
                censusDwellFileReader.OpenFile(
                    Utils.DATA_DIR + "\\Household\\CensusDwellingType.csv");
                censusCarFileReader.OpenFile(
                    Utils.DATA_DIR + "\\Household\\CensusNumOfCars.csv");

                censusPersonFileReader.GetConditionalList();
                censusDwellFileReader.GetConditionalList();
                censusCarFileReader.GetConditionalList();*/
            }
            if (currType == AgentType.Person)
            {
            	int numberofmodels = 1;
            	int numberofcounts = 1;
            	
            	
            	for (int i=0; i<ConfigFile.AttributeDefinitionsImportance.size();i++)
            	{	
            		if (ConfigFile.TypeOfConditionalsImportance.get(i).equals("count"))
            		{
            			
        				numberofcounts += numberofcounts;
            			System.out.println(Utils.DATA_DIR + 
            					"data\\" + 
            					fmt(currZone.myAttributes.get(0).value) + // careful of the type that returns from the Double.toString : it had a ".0" to the values            					
            					"\\" +
            					ConfigFile.AttributesPathsImportance.get(i));
            					
            			importanceAttributesFileReader.get(i-numberofmodels+1).OpenFile(
            					Utils.DATA_DIR + 
            					"\\data\\" + 
            					fmt(currZone.myAttributes.get(0).value) +
            					"\\" +
            					ConfigFile.AttributesPathsImportance.get(i));
            			
            			
            				
            		}
            		else if(ConfigFile.TypeOfConditionals.get(i).equals("model"))
            		{	
            			/*	numberofmodels += numberofmodels;
            			modelAttributesFileReader.get(i-numberofcounts+1).OpenFile(Utils.DATA_DIR + "\\"+ConfigFile.AttributesPaths.get(i));*/
            			System.out.println("--functionnality not implemented");
            		}
            	}
            }
              
         }
        
        private void OpenFilesMain(int currType) throws IOException
        {
            if (currType == AgentType.Household)
            {
            	System.out.println("--functionnality not implemented");
            	/*
                censusPersonFileReader.OpenFile(
                    Utils.DATA_DIR + "\\Household\\CensusNumOfPers.csv");
                censusDwellFileReader.OpenFile(
                    Utils.DATA_DIR + "\\Household\\CensusDwellingType.csv");
                censusCarFileReader.OpenFile(
                    Utils.DATA_DIR + "\\Household\\CensusNumOfCars.csv");

                censusPersonFileReader.GetConditionalList();
                censusDwellFileReader.GetConditionalList();
                censusCarFileReader.GetConditionalList();*/
            }
            if (currType == AgentType.Person)
            {
            	int numberofmodels = 1;
            	int numberofcounts = 1;
            	for (int i=0; i<ConfigFile.AttributeDefinitions.size();i++)
            	{	
            		if (ConfigFile.TypeOfConditionals.get(i).equals("count"))
            		{
            				numberofcounts += numberofcounts;
            			System.out.println(Utils.DATA_DIR + 
            					"data\\" + 
            					fmt(myAttributes.get(0).value) + // careful of the type that returns from the Double.toString : it had a ".0" to the values            					
            					"\\" +
            					ConfigFile.AttributesPaths.get(i));
            					
            			mainAttributesFileReader.get(i-numberofmodels+1).OpenFile(
            					Utils.DATA_DIR + 
            					"\\data\\" + 
            					fmt(myAttributes.get(0).value) +
            					"\\" +
            					ConfigFile.AttributesPaths.get(i));
            		}
            		else if(ConfigFile.TypeOfConditionals.get(i).equals("model"))
            		{	
            			/*	numberofmodels += numberofmodels;
            			modelAttributesFileReader.get(i-numberofcounts+1).OpenFile(Utils.DATA_DIR + "\\"+ConfigFile.AttributesPaths.get(i));*/
            			System.out.println("--functionnality not implemented");
            		}
            	}
            }
              
         }
            
        


/*        void LoadCensusData(SpatialZone currZone)
        {
            currZone.censusPersonConditionals.FlushOutData();
            censusPersonFileReader.
              FillCollection1(currZone.censusPersonConditionals);
            currZone.censusPersonConditionals.SetDimensionName("HouseholdSize");

        }*/

        void CloseFiles(int currType) throws IOException
        {
            if (currType == AgentType.Household)
            {

                censusDwellFileReader.CloseFile();
                censusCarFileReader.CloseFile();
                censusPersonFileReader.CloseFile();
            }
            else if (currType == AgentType.Person)
            {

        		int numberofmodels = 1;
            	int numberofcounts = 1;
            	
            	for (int i=0; i <ConfigFile.AttributeDefinitions.size(); i++)
            	{
                	
            		if(ConfigFile.TypeOfConditionals.get(i).equals("count"))
            		{
            			
            				numberofcounts += numberofcounts;
      

            				censusAttributesFileReader.get(i-numberofmodels+1).CloseFile();	
            		}
            		
            		else if(ConfigFile.TypeOfConditionals.get(i).equals("model"))
            		{
            			numberofmodels += numberofmodels;
            			modelAttributesFileReader.get(i-numberofcounts+1).CloseFile();
            		}
            	}
            }
        }
        
        void CloseFilesMain(int currType) throws IOException
        {
            if (currType == AgentType.Household)
            {

                censusDwellFileReader.CloseFile();
                censusCarFileReader.CloseFile();
                censusPersonFileReader.CloseFile();
            }
            else if (currType == AgentType.Person)
            {

        		int numberofmodels = 1;
            	int numberofcounts = 1;
            	
            	for (int i=0; i <ConfigFile.AttributeDefinitions.size(); i++)
            	{
                	
            		if(ConfigFile.TypeOfConditionals.get(i).equals("count"))
            		{
            			
            				numberofcounts += numberofcounts;
      

            				mainAttributesFileReader.get(i-numberofmodels+1).CloseFile();	
            		}
            		
            		/*else if(ConfigFile.TypeOfConditionals.get(i).equals("model"))
            		{
            			numberofmodels += numberofmodels;
            			modelAttributesFileReader.get(i-numberofcounts+1).CloseFile();
            		}*/
            	}
            }
        }
        
        void CloseFilesImportance(int currType) throws IOException
        {
            if (currType == AgentType.Household)
            {

                /*censusDwellFileReader.CloseFile();
                censusCarFileReader.CloseFile();
                censusPersonFileReader.CloseFile();*/
            }
            else if (currType == AgentType.Person)
            {

        		int numberofmodels = 1;
            	int numberofcounts = 1;
            	
            	for (int i=0; i <ConfigFile.AttributeDefinitionsImportance.size(); i++)
            	{
                	
            		if(ConfigFile.TypeOfConditionalsImportance.get(i).equals("count"))
            		{
            			
            				numberofcounts += numberofcounts;
      

            				importanceAttributesFileReader.get(i-numberofmodels+1).CloseFile();	
            		}
            		
            		/*else if(ConfigFile.TypeOfConditionals.get(i).equals("model"))
            		{
            			numberofmodels += numberofmodels;
            			modelAttributesFileReader.get(i-numberofcounts+1).CloseFile();
            		}*/
            	}
            }
        }

        public void CreateHoseholdPopulationPool(String fileName) throws IOException
        {
            agentsOutputFile.OpenFile(fileName);
            int agentsCreated = 1;
            int counter = 0;
            ArrayList mobelCond = new ArrayList();
            mobelCond.add((ConditionalDistribution)mobelWrkrsConditionals);
            mobelCond.add((ConditionalDistribution)mobelKidsConditionals);
            mobelCond.add((ConditionalDistribution)mobelPersConditionals);


            for (Map.Entry<String, Object> entry : myZonalCollection.entrySet())
            {
                SpatialZone currZone = (SpatialZone) entry.getValue();
                // warmup time
                myGibbsSampler.GenerateAgents(null, currZone,
                                Utils.WARMUP_ITERATIONS,
                                new Household(String.valueOf(currZone.myAttributes.get(0).value)), true,
                                mobelCond,
                                agentsOutputFile);
                myHhldsPool.clear();
                myGibbsSampler.SetAgentCounter(agentsCreated+counter);
                // actual generation
                myHhldsPool = myGibbsSampler.GenerateAgents(null, currZone,
                                Utils.POOL_COUNT,
                                new Household(String.valueOf(currZone.myAttributes.get(0).value)), false,
                                mobelCond,
                                agentsOutputFile);
                agentsCreated += myHhldsPool.size();
            }
            agentsOutputFile.CloseFile();
        }

        public void CreatePersonPopulationPool(String fileName) throws IOException
        {
            agentsOutputFile.OpenFile(fileName);
            int agentsCreated = 1;
            int counter = 0;
            ArrayList mobelCond = new ArrayList();

            for (Map.Entry<String, Object> entry : myZonalCollection.entrySet())
            {
                SpatialZone currZone = (SpatialZone)entry.getValue();
              /*  if (!currZone.GetName().equals("1004"))
                {
                    continue;
                }*/
                // warmup time
                Person p = new Person(String.valueOf(currZone.myAttributes.get(0).value));
                p.SetAgentType(1);
                myGibbsSampler.GenerateAgents(null, currZone,
                                Utils.WARMUP_ITERATIONS,
                                p, true,
                                attributesMainConditionals,
                                agentsOutputFile);
                myPersonPool.clear();
                myGibbsSampler.SetAgentCounter(agentsCreated + counter);
                
                
                //System.out.println(String.valueOf(currZone.myAttributes.get(0).value));
                // actual generation
                myPersonPool = myGibbsSampler.GenerateAgents(null, currZone,
                                Utils.POOL_COUNT,
                                new Person(String.valueOf(currZone.myAttributes.get(0).value)), false,
                                attributesMainConditionals,
                                agentsOutputFile);
                agentsCreated += myPersonPool.size();
            }
            agentsOutputFile.CloseFile();
        }
        
        public void CreatePersonPopulationPoolImportance(String fileName, String pathToSeeds) throws IOException
        {
        	OutputFileWritter outputFile;
            int agentsCreated = 1;
            int counter = 0;
            ArrayList mobelCond = new ArrayList();

            for (Map.Entry<String, Object> entry : myZonalCollection.entrySet())
            {
            	outputFile = new OutputFileWritter();
            	
                SpatialZone currZone = (SpatialZone)entry.getValue();
                
                System.out.println(Utils.DATA_DIR + "data\\" + fmt(currZone.myAttributes.get(0).value) + "\\personPool.txt");
                outputFile.OpenFile(Utils.DATA_DIR + "data\\" + fmt(currZone.myAttributes.get(0).value) + "\\personPool.txt");
                System.out.println(currZone.myAttributes.get(1).value);
              /*  if (!currZone.GetName().equals("1004"))
                {
                    continue;
                }*/
                // warmup time
                
                //String pathToSeeds = Utils.DATA_DIR+"data\\ImportanceSamplingConditionals\\Vancouver.txt";
                
                myGibbsSampler.GenerateAgents(null, currZone,
                                Utils.WARMUP_ITERATIONS,
                                new Person(pathToSeeds, true), true,
                                attributesMainConditionals,
                                outputFile);
                myPersonPool.clear();
                myGibbsSampler.SetAgentCounter(agentsCreated + counter);
                
                
                System.out.println(String.valueOf(currZone.myAttributes.get(0).value));
                // actual generation
                myPersonPool = myGibbsSampler.GenerateAgents(null, currZone,
                                (int)currZone.myAttributes.get(1).value,
                                new Person(pathToSeeds,true), false,
                                attributesMainConditionals,
                                outputFile);
                
                agentsCreated += myPersonPool.size();
                
                outputFile.CloseFile();
            }
            
        }
        
        public void CreatePersonPopulationPoolMetroLevel(String fileName, String pathToSeeds) throws IOException
        {
        	OutputFileWritter outputFile;
            int agentsCreated = 1;
            int counter = 0;
            
            outputFile = new OutputFileWritter();
            System.out.println(Utils.DATA_DIR + "data\\" + fmt(myAttributes.get(0).value) + "\\personPool.csv");
            outputFile.OpenFile(Utils.DATA_DIR + "data\\" + fmt(myAttributes.get(0).value) + "\\personPool.csv");
            
            myGibbsSampler.GenerateAgentsMetroLevel(this,
                    Utils.WARMUP_ITERATIONS,
                    new Person(pathToSeeds, true), true,
                    attributesMainConditionals,
                    outputFile);
		    myPersonPool.clear();
		    myGibbsSampler.SetAgentCounter(agentsCreated + counter);
		    
		    
		    System.out.println(String.valueOf(myAttributes.get(0).value));
		    // actual generation
		    myPersonPool = myGibbsSampler.GenerateAgentsMetroLevel(this,
		                    Utils.POOL_COUNT,
		                    new Person(pathToSeeds,true), false,
		                    attributesMainConditionals,
		                    outputFile);
		    
		    agentsCreated += myPersonPool.size();
    
		    outputFile.CloseFile();
            

            /*for (Map.Entry<String, Object> entry : myZonalCollection.entrySet())
            {
                SpatialZone currZone = (SpatialZone)entry.getValue();
                System.out.println(currZone.myAttributes.get(1).value);
              /*  if (!currZone.GetName().equals("1004"))
                {
                    continue;
                }
                // warmup time
               
                
                
            }*/
            
        }
        
        public void CreatePersonPopulationPoolLocalLevel(String fileName, String pathToSeeds) throws IOException
        {
        	OutputFileWritter outputFile;
            int agentsCreated = 1;
            int counter = 0;
            
            //Initialize the statistical log
            OutputFileWritter localStatAnalysis = new OutputFileWritter();
            localStatAnalysis.OpenFile(Utils.DATA_DIR + "data\\" + fmt(myAttributes.get(0).value) + "\\localStatAnalysis.csv");
            String headers = "zoneId" + Utils.COLUMN_DELIMETER + "targetTotPop";
            for(int i = 0; i < ConfigFile.AttributeDefinitionsImportance.size(); i++){
    			headers = headers + Utils.COLUMN_DELIMETER + ConfigFile.AttributeDefinitionsImportance.get(i).category + "_MSE"
    					 + Utils.COLUMN_DELIMETER + ConfigFile.AttributeDefinitionsImportance.get(i).category + "_absErr"
    							 + Utils.COLUMN_DELIMETER + ConfigFile.AttributeDefinitionsImportance.get(i).category + "_%Err" ;
            }
            localStatAnalysis.WriteToFile(headers);
            //
            // We warm the sampler once for all the spatial zones.
            myGibbsSampler.GenerateAgentsMetroLevel(this,
                    Utils.WARMUP_ITERATIONS,
                    new Person(pathToSeeds, true), true,
                    attributesMainConditionals, null);
		    myPersonPool.clear();
		    myGibbsSampler.SetAgentCounter(agentsCreated + counter);
            
            for (Map.Entry<String, Object> entry : myZonalCollection.entrySet())
            {
            	outputFile = new OutputFileWritter();
            	
                SpatialZone currZone = (SpatialZone)entry.getValue();
                
                System.out.println(Utils.DATA_DIR + "data\\" + fmt(currZone.myAttributes.get(0).value) + "\\personPool.txt");
                outputFile.OpenFile(Utils.DATA_DIR + "data\\" + fmt(currZone.myAttributes.get(0).value) + "\\personPool.txt");
                System.out.println(currZone.myAttributes.get(1).value);
            
	            /*myGibbsSampler.GenerateAgents(this, currZone,
	                    Utils.WARMUP_ITERATIONS,
	                    new Person(pathToSeeds, true), true,
	                    attributesMainConditionals,
	                    outputFile);
			    myPersonPool.clear();
			    myGibbsSampler.SetAgentCounter(agentsCreated + counter);*/
			    
			    
			    System.out.println(String.valueOf(currZone.myAttributes.get(0).value));
			    // actual generation
			    myPersonPool = myGibbsSampler.GenerateAgents(null, currZone,
                        (int)currZone.myAttributes.get(1).value,
                        new Person(pathToSeeds,true), false,
                        attributesMainConditionals,
                        outputFile);
			    agentsCreated += myPersonPool.size();
			    
			    
	    
			    outputFile.CloseFile();
	            String statistics = currZone.printLocalMarginalFittingAnalysis((int)currZone.myAttributes.get(1).value);
	            localStatAnalysis.WriteToFile(statistics);
	            //currZone.updateStatisticsDatasets((int)currZone.myAttributes.get(1).value, localStandardDeviations, localAbsoluteErrors);
	
	            /*for (Map.Entry<String, Object> entry : myZonalCollection.entrySet())
	            {
	                SpatialZone currZone = (SpatialZone)entry.getValue();
	                System.out.println(currZone.myAttributes.get(1).value);
	              /*  if (!currZone.GetName().equals("1004"))
	                {
	                    continue;
	                }
	                // warmup time
	               
	                
	                
	            }*/
            }
            localStatAnalysis.CloseFile();
            
        }
        
        @Deprecated
        public void CreatePersonPopulationPoolLocalLevelMultiThreads(String fileName, String pathToSeeds, int numberOfCores) throws IOException
        {
        	
            int agentsCreated = 1;
            int counter = 0;
            
            //Initialize the statistical log
            OutputFileWritter localStatAnalysis = new OutputFileWritter();
            localStatAnalysis.OpenFile(Utils.DATA_DIR + "data\\" + fmt(myAttributes.get(0).value) + "\\localStatAnalysis.csv");
            String headers = "zoneId";
            for(int i = 0; i < ConfigFile.AttributeDefinitionsImportance.size(); i++){
    			headers = headers + Utils.COLUMN_DELIMETER + ConfigFile.AttributeDefinitionsImportance.get(i).category + "_MSE"
    					 + Utils.COLUMN_DELIMETER + ConfigFile.AttributeDefinitionsImportance.get(i).category + "_absErr"
    							 + Utils.COLUMN_DELIMETER + ConfigFile.AttributeDefinitionsImportance.get(i).category + "_%Err" ;
            }
            localStatAnalysis.WriteToFile(headers);
            
            // Initialize the population pool log
            OutputFileWritter population =  new OutputFileWritter();
        	population.OpenFile(Utils.DATA_DIR + "data\\" + fmt(myAttributes.get(0).value) + "\\createdPopulation.csv");
        	headers = "zoneId" + Utils.COLUMN_DELIMETER + "zoneId";
            for(int i = 0; i < ConfigFile.AttributeDefinitions.size(); i++){
    			headers = headers + Utils.COLUMN_DELIMETER + ConfigFile.AttributeDefinitions.get(i).category ;
            }
            population.WriteToFile(headers);
            
            //
            // We warm the sampler once for all the spatial zones.
            myGibbsSampler.GenerateAgentsMetroLevel(this,
                    Utils.WARMUP_ITERATIONS,
                    new Person(pathToSeeds, true), true,
                    attributesMainConditionals, null);
		    myPersonPool.clear();
		    myGibbsSampler.SetAgentCounter(agentsCreated + counter);
		    
		    //creates sub group of zone that can be processed using multithreading to accelerate computation
		    int subSampleSize = myZonalCollection.size()/(numberOfCores);
		    ArrayList<Object> subSamples = new ArrayList<Object>();
		    for (int i = 0; i<numberOfCores; i++){
		    	HashMap<String, Object> currSubSample = new HashMap<String, Object>();
	    		int k = 0;
	    		int l = 0;
	    		for (Map.Entry<String, Object> entry : myZonalCollection.entrySet()){
	    			if((k>=i*subSampleSize && k < (i+1)*subSampleSize)|| (k>=i*subSampleSize && i == numberOfCores-1)){// || (k>=i*subSampleSize && i == numberOfCores-1)
	    				SpatialZone currZone = (SpatialZone)entry.getValue();
	    				currSubSample.put(ConfigFile.fmt(currZone.myAttributes.get(0).value), currZone);
	    				k++;
	    			}
	    			/*if((k>=(i+1)*subSampleSize && i == numberOfCores-1)){
	    				if(l == i){
	    					SpatialZone currZone = (SpatialZone)entry.getValue();
		    				currSubSample.put(ConfigFile.fmt(currZone.myAttributes.get(0).value), currZone);
		    				if(l==numberOfCores-1){l=0;}else{l++;}
	    				}
	    				else{
	    					if(l==numberOfCores-1){l=0;}else{l++;}
	    				}
	    			}*/
	    			/*else if(k>(i+1)*subSampleSize){
	    				break;
	    			}*/
	    			else{
	    				k++;
	    			}
	    		}
	    		System.out.println("sample " + i + ": " + currSubSample.size());
	    		subSamples.add(currSubSample);	    	
		    }
		    
		    //create and run multiple threads
		    ExecutorService cores = Executors.newFixedThreadPool(numberOfCores);
        	//Set<Future<Integer>> set = new HashSet<Future<Integer>>();
        	Set<Future<ArrayList>> set = new HashSet<Future<ArrayList>>();
        	
        	
        	for(int i = 0; i< numberOfCores; i++){
        		/*Callable<Integer> callable = new processZone(subSamples.get(i), pathToSeeds, myPersonPool, attributesMainConditionals);
            	Future<Integer> future = cores.submit(callable);*/
        		Callable<ArrayList> callable = new processZone(subSamples.get(i), pathToSeeds, myPersonPool, attributesMainConditionals);
            	Future<ArrayList> future = cores.submit(callable);
                set.add(future);
        	}
        	String analysis = new String();
        	String pop = new String();
        	try {
	            for (Future<ArrayList> future : set) {
	            	ArrayList<String> answer = future.get();
	            	analysis = analysis +  future.get().get(0);//Utils.NEW_LINE_DELIMITER +
	            	pop = pop + future.get().get(1);
	            }
        	} 
        	catch (InterruptedException | ExecutionException ex) {
        		ex.printStackTrace(); 
        	}
        	
        	localStatAnalysis.WriteToFile(analysis);
            localStatAnalysis.CloseFile();
            population.WriteToFile(pop);
            population.CloseFile();
        }


        public void LoadZones(int CurrType) throws IOException
        {
        	 InputDataReader currReader = new InputDataReader();
        	 System.out.println(Utils.DATA_DIR + ConfigFile.ZonalFile);
             currReader.OpenFile(Utils.DATA_DIR + ConfigFile.ZonalFile);
             currReader.FillZonalData(myZonalCollection);
             currReader.CloseFile();
             System.out.println("--zonal collection filled");
        }

      
        public void CreatePopulationByDwellingType(int seed, String poolfileName,
                String fileName) throws IOException
        {
            ArrayList hhldPool = GetHouseholdPoolForClonning(poolfileName);

            InputDataReader currReader = new InputDataReader();
            currReader.OpenFile(poolfileName);
            ArrayList currPool = new ArrayList();
            RandomNumberGen currRandGen = new RandomNumberGen(seed);
            OutputFileWritter currOutputFile = new OutputFileWritter();
            currOutputFile.OpenFile(fileName);
            currOutputFile.WriteToFile("HhldID,SectorID,HhldSize,NbOfWorkers,"
                        + "NbofKids,NbofUnivDegree,IncLvl,NumbCars,"
                        + "DwellTyp(PopSynt),EPFL_SectorID,BuildingID");
            int totSingle = 0;
            int totSemi = 0;
            int totDb = 0;
            int toApt = 0;

            while (currReader.LoadZonalPopulationPool(currPool) == true)
            {
                String[] currStrTok = ((String)currPool.get(0)).split(",");
                int indx = 0;

                String currKey = "";
                if (zonalControlTotals.containsKey(currStrTok[1]))
                {
                    // for each type of dwelling
                    for (int i = 0; i < 4; i++)
                    {
                        ArrayList currDwellHhld = new ArrayList();
                        for (int j = 0; j < currPool.size(); j++)
                        {
                            String currHhld = (String) currPool.get(j);
                            String[] currHhldVal = currHhld.split(",");
                            if (currHhldVal[(currHhldVal.length - 1)].equals(String.valueOf(i)))
                            {
                                currDwellHhld.add(currHhld);
                            }
                        }
                        currKey = (String)currStrTok[1];
                        String[] contTotStr = (String[])zonalControlTotals.get(currKey);

                        // number of dwellings of certain type
                        indx = Integer.parseInt(contTotStr[i + 2]);
                        String ZnID = contTotStr[0];
                        String ZnEFPLID = contTotStr[1];
                        String bldId = "0" + String.valueOf(i + 1);

                        if (indx > currDwellHhld.size())
                        {
                            indx = indx - currDwellHhld.size();
                            for (int x = 0; x < currDwellHhld.size(); x++)
                            {
                                String[] hhldValues = ((String)
                                        currDwellHhld.get(x)).split(",");
                                String currHhldStr = hhldValues[0] + "," + ZnID.substring(0, 5)
                                            + "," + hhldValues[3] + "," + hhldValues[4]
                                            + "," + hhldValues[5] + "," + hhldValues[6]
                                            + "," + hhldValues[7] + "," + hhldValues[8]
                                            + "," + hhldValues[9] + ","
                                            + contTotStr[1] + "," + contTotStr[1] + bldId;
                                currOutputFile.WriteToFile(currHhldStr);
                                //Console.WriteLine(currHhldStr);
                                if (i == 0) totSingle++;
                                else if (i == 1) totSemi++;
                                else if (i == 2) totDb++;
                                else toApt++;
                            }
                            ArrayList currRandList = currRandGen.GetNNumbersInRange(0,
                                hhldPool.size() - 1, indx);
                            for (int j = 0; j < currRandList.size(); j++)
                            {
                                String[] hhldValues =
                                    ((String)hhldPool.get(Integer.parseInt(currRandList.get(j).toString()))).split(",");
                                String currHhldStr = ZnID + String.valueOf(i) + String.valueOf(j)
                                        + "," + ZnID.substring(0, 5)
                                        + "," + hhldValues[0] + "," + hhldValues[1]
                                        + "," + hhldValues[2] + "," + hhldValues[3]
                                        + "," + hhldValues[4] + "," + hhldValues[5]
                                        + "," + hhldValues[6] + ","
                                        + ZnEFPLID + "," + ZnEFPLID + bldId;
                                currOutputFile.WriteToFile(currHhldStr);
                                //Console.WriteLine(currHhldStr);
                                if (i == 0) totSingle++;
                                else if (i == 1) totSemi++;
                                else if (i == 2) totDb++;
                                else toApt++;
                            }
                        }
                        else
                        {
                            ArrayList currRandList = currRandGen.GetNNumbersInRange(0,
                                currDwellHhld.size() - 1, indx);
                            for (int j = 0; j < currRandList.size(); j++)
                            {
                                String[] hhldValues = ((String)
                                    currDwellHhld.get(Integer.valueOf(currRandList.get(j).toString()))).split(",");
                                String currHhldStr = hhldValues[0] + "," + ZnID.substring(0, 5)
                                        + "," + hhldValues[3] + "," + hhldValues[4]
                                        + "," + hhldValues[5] + "," + hhldValues[6]
                                        + "," + hhldValues[7] + "," + hhldValues[8]
                                        + "," + hhldValues[9] + ","
                                        + contTotStr[1] + "," + contTotStr[1] + bldId;
                                currOutputFile.WriteToFile(currHhldStr);
                                //Console.WriteLine(currHhldStr);
                                if (i == 0) totSingle++;
                                else if (i == 1) totSemi++;
                                else if (i == 2) totDb++;
                                else toApt++;
                            }
                        }
                    }
                }
                currPool.clear();
            }
            currOutputFile.CloseFile();
            currReader.CloseFile();
            System.out.println("Total Detached:\t" + totSingle
                              +"\nTotal SemiDetached:\t" + totSemi
                              +"\nTotal Attached:\t" + totDb
                              + "\nTotal Apartment:\t" + toApt);
        }

        public ArrayList GetHouseholdPoolForClonning(String fileName) throws IOException
        {
            ArrayList currArrayList = new ArrayList();
            ArrayList currPool = new ArrayList();
            InputDataReader currReader = new InputDataReader();
            currReader.OpenFile(
                Utils.DATA_DIR + "Household\\SyntheticHhld.csv");
            RandomNumberGen currRand = new RandomNumberGen();
            while (currReader.LoadZonalPopulationPoolByType
                (currPool, "3") == true)
            {
                if (currArrayList.size() > 60000)
                {
                    currReader.CloseFile();
                    return currArrayList;
                }

                if (currPool.size() > 0)
                {
                    int numB = (int)Math.ceil((currPool.size() * 0.1));
                    ArrayList curDrw = currRand.GetNNumbersInRange(
                                            0, currPool.size() - 1, (numB));
                    if (curDrw.size() > 0)
                    {
                        for (int i = 0; i < numB; i++)
                        {
                            currArrayList.add(currPool.get(Integer.valueOf(curDrw.get(i).toString())));
                        }
                    }
                }
                currPool.clear();
            }
            currReader.CloseFile();
            return currArrayList;
        }

        public void CreateHouseholdPopulation() throws IOException
        {
            InputDataReader currReader = new InputDataReader();
            currReader.OpenFile(
                Utils.DATA_DIR + "SyntheticHhld_withourImpSamp.csv");
            ArrayList currPool = new ArrayList();
            RandomNumberGen currRandGen = new RandomNumberGen();
            OutputFileWritter currOutputFile = new OutputFileWritter();
            currOutputFile.OpenFile(
                Utils.DATA_DIR + "PopulationRealization20k.csv");
            currOutputFile.WriteToFile("HhldID,SectorID,HhldSize,NbOfWorkers,"
                        + "NbofKids,NbofUnivDegree,IncLvl,NumbCars,"
                        + "DwellTyp(PopSynt),EPFL_SectorID,BuildingID");
            while (currReader.LoadZonalPopulationPool(currPool) == true)
            {
                String[] currStrTok = ((String)currPool.get(0)).split(",");
                int indx = 0;
                String currKey = "";
                if (zonalControlTotals.containsKey(currStrTok[1]))
                {
                    currKey = (String)currStrTok[1];
                    indx = Integer.valueOf(zonalControlTotals.get(currKey).toString());
                    ArrayList currRandList = currRandGen.GetNNumbersInRange(0,
                        Utils.POOL_COUNT - 1, indx);
                    for (int i = 0; i < currRandList.size(); i++)
                    {
                        String[] hhldValues = ((String)
                            currPool.get(Integer.valueOf(currRandList.get(i).toString()))).split(",");
                        int bld = Integer.parseInt(hhldValues[9]) + 1;
                        currOutputFile.WriteToFile(hhldValues[0] + "," + hhldValues[1]
                                + "," + hhldValues[3] + "," + hhldValues[4]
                                + "," + hhldValues[5] + "," + hhldValues[6]
                                + "," + hhldValues[7] + "," + hhldValues[8]
                                + "," + hhldValues[9] + ","
                                + hhldValues[2] + "," + hhldValues[2]
                                + "0" + bld);
                    }
                }
                currPool.clear();
            }
            currOutputFile.CloseFile();
            currReader.CloseFile();
        }

        protected static class ZonalStat
        {
            public String zoneName;
            public double count;
            public double sum;
        }

        public void ComputeCommuneLevelStatisticsIncome(String poplFile) throws IOException
        {
            BufferedReader currReader = new BufferedReader(new FileReader(poplFile));

            HashMap<String, Object> currIncome = new HashMap<String, Object>();

            BufferedWriter currOutputFile = new
                BufferedWriter(new FileWriter(Utils.DATA_DIR + "CommuneStatisticsIncome.csv"));
            String currHhld;
            currReader.readLine();
            while (currReader.ready())
            {
                currHhld = currReader.readLine();
                String[] currHhldTok = currHhld.split(",");
                String currsector = currHhldTok[1].substring(0, 5);
                if (currIncome.containsKey(currsector))
                {
                    ZonalStat currStat = (ZonalStat) currIncome.get(currsector);
                    currStat.count++;
                    currStat.sum += Integer.parseInt(currHhldTok[6]);
                    currIncome.put(currsector, currStat);
                    int cntUn = Integer.parseInt(currHhldTok[5]);
                    int cntTot = Integer.parseInt(currHhldTok[2]);

                }
                else
                {
                    ZonalStat currStat = new ZonalStat();
                    currStat.zoneName = currsector;
                    currStat.sum = Integer.parseInt(currHhldTok[6]);
                    currStat.count = 1;
                    currIncome.put(currsector,currStat);

                }
            }

            currOutputFile.write("Commune,Income");
            currOutputFile.newLine();
             for (Map.Entry<String, Object> ent : currIncome.entrySet())
            {
                ZonalStat curSt = (ZonalStat)ent.getValue();
                currOutputFile.write(curSt.zoneName +","+
                    curSt.sum / curSt.count);
                currOutputFile.newLine();
            }
            currReader.close();
            currOutputFile.close();
        }

        public void DiscretizeIncomeAgain(String poplFile) throws IOException
        {
            BufferedReader currReader = new BufferedReader(new FileReader(poplFile));

            HashMap<String, Object> currIncome = new HashMap<String, Object>();

            BufferedWriter currOutputFile = new
                BufferedWriter(new FileWriter(Utils.DATA_DIR + "BrusselsPopulation_DiscIncome.csv"));
            String currHhld;
            currOutputFile.write(currReader.readLine()+",IncLvl");
            currOutputFile.newLine();
            while (currReader.ready())
            {
                currHhld = currReader.readLine();
                String[] currHhldTok = currHhld.split(",");
                int currIncomeVal = Integer.parseInt(currHhldTok[6]);

                if (currIncomeVal < 745 )
                {
                    currHhld += ",0";
                }
                else if (currIncomeVal >= 745 && currIncomeVal < 1860)
                {
                    currHhld += ",1";
                }
                else if (currIncomeVal >= 1860 && currIncomeVal < 3100)
                {
                    currHhld += ",2";
                }
                else if (currIncomeVal >= 3100 && currIncomeVal < 4959)
                {
                    currHhld += ",3";
                }
                else if (currIncomeVal >= 4959)
                {
                    currHhld += ",4";
                }
                currOutputFile.write(currHhld);
                currOutputFile.newLine();
            }

            currOutputFile.close();
            currReader.close();
        }

        public void ComputeCommuneLevelStatisticsDiscInc(String poplFile, int category) throws IOException
        {
            BufferedReader currReader = new BufferedReader(new FileReader(poplFile));

            HashMap<String, Object> currEdu = new HashMap<String, Object>();

            BufferedWriter currOutputFile = new
                BufferedWriter(new FileWriter(Utils.DATA_DIR + "CommuneStatistics4IncLvl.csv"));
            String currHhld;
            currReader.readLine();
            while (currReader.ready())
            {
                currHhld = currReader.readLine();
                String[] currHhldTok = currHhld.split(",");
                String currsector = currHhldTok[1].substring(0, 5);
                int cntUn = Integer.parseInt(currHhldTok[11]);
                if (currEdu.containsKey(currsector) && cntUn == category)
                {
                    ZonalStat currStat = (ZonalStat)currEdu.get(currsector);
                    currStat.count++;
                    currEdu.put(currsector, currStat);
                }
                else if (cntUn == category)
                {
                    ZonalStat currStat = new ZonalStat();
                    currStat.zoneName = currsector;
                    currStat.count = 1;
                    currEdu.put(currsector, currStat);

                }
            }

            currOutputFile.write("Commune,IncLvl4");
            currOutputFile.newLine();
            for (Map.Entry<String, Object> ent : currEdu.entrySet())
            {
                ZonalStat curSt = (ZonalStat)ent.getValue();
                currOutputFile.write(curSt.zoneName + "," +
                    curSt.count);
                currOutputFile.newLine();
            }
            currReader.close();
            currOutputFile.close();
        }

        public void ComputeCommuneLevelStatisticsEdu(String poplFile, int category) throws IOException
        {
            BufferedReader currReader = new BufferedReader(new FileReader(poplFile));

            HashMap<String, Object> currEdu = new HashMap<String, Object>();

            BufferedWriter currOutputFile = new
                BufferedWriter(new FileWriter(Utils.DATA_DIR + "CommuneStatistics2High.csv"));
            String currHhld;
            currReader.readLine();
            while (currReader.ready())
            {
                currHhld = currReader.readLine();
                String[] currHhldTok = currHhld.split(",");
                String currsector = currHhldTok[1].substring(0, 5);
                int cntUn = Integer.parseInt(currHhldTok[5]);
                if (currEdu.containsKey(currsector) && cntUn == category)
                {
                    ZonalStat currStat = (ZonalStat)currEdu.get(currsector);
                    currStat.count++;
                    currEdu.put(currsector, currStat);
                }
                else if ( cntUn == category)
                {
                    ZonalStat currStat = new ZonalStat();
                    currStat.zoneName = currsector;
                    currStat.count = 1;
                    currEdu.put(currsector, currStat);

                }
            }

            currOutputFile.write("Commune,Edu2");
            currOutputFile.newLine();
            for (Map.Entry<String, Object> ent : currEdu.entrySet())
            {
                ZonalStat curSt = (ZonalStat)ent.getValue();
                currOutputFile.write(curSt.zoneName + "," +
                    curSt.count);
                currOutputFile.newLine();
            }
            currReader.close();
            currOutputFile.close();
        }

        public void ComputeCommuneLevelStatisticsPeople(String poplFile, int category) throws IOException
        {
            BufferedReader currReader = new BufferedReader(new FileReader(poplFile));

            HashMap<String, Object> currEdu = new HashMap<String, Object>();

            BufferedWriter currOutputFile = new
                BufferedWriter(new FileWriter(Utils.DATA_DIR + "CommuneStatistics5Per.csv"));
            String currHhld;
            currReader.readLine();
            while (currReader.ready())
            {
                currHhld = currReader.readLine();
                String[] currHhldTok = currHhld.split(",");
                String currsector = currHhldTok[1].substring(0, 5);
                int cntUn = Integer.parseInt(currHhldTok[2]);
                if (currEdu.containsKey(currsector) && cntUn == category)
                {
                    ZonalStat currStat = (ZonalStat)currEdu.get(currsector);
                    currStat.count++;
                    currEdu.put(currsector,currStat);
                }
                else if (cntUn == category)
                {
                    ZonalStat currStat = new ZonalStat();
                    currStat.zoneName = currsector;
                    currStat.count = 1;
                    currEdu.put(currsector, currStat);
                }
            }

            currOutputFile.write("Commune,People5");
            currOutputFile.newLine();
            for (Map.Entry<String, Object> ent : currEdu.entrySet())
            {
                ZonalStat curSt = (ZonalStat)ent.getValue();
                currOutputFile.write(curSt.zoneName + "," +
                    curSt.count);
                currOutputFile.newLine();
            }
            currReader.close();
            currOutputFile.close();
        }

        public void ComputeCommuneLevelStatisticsPeople(String poplFile,
                    String fileNam, String comList) throws IOException
        {
            BufferedReader currReader = new BufferedReader(new FileReader(poplFile));
            BufferedReader currComuneList = new BufferedReader(new FileReader(comList));
            HashMap<String, Object> currPerM = new HashMap<String, Object>();
            HashMap<String, Object> currPerF = new HashMap<String, Object>();
            HashMap<String, Object> currPerTwo = new HashMap<String, Object>();
            HashMap<String, Object> currPerThree = new HashMap<String, Object>();
            HashMap<String, Object> currPerFour = new HashMap<String, Object>();
            HashMap<String, Object> currPerFive = new HashMap<String, Object>();

            BufferedWriter currOutputFile = new
                BufferedWriter(new FileWriter(fileNam));
            String currHhld;
            currReader.readLine();
            while (currReader.ready())
            {
                currHhld = currReader.readLine();
                String[] currHhldTok = currHhld.split(",");
                String currsector = currHhldTok[1].substring(0, 5);
                int cntUn = Integer.parseInt(currHhldTok[2]);
                if (currPerM.containsKey(currsector) && cntUn == 0)
                {
                    ZonalStat currStat = (ZonalStat)currPerM.get(currsector);
                    currStat.count++;
                    currPerM.put(currsector,currStat);
                }
                else if (cntUn == 0)
                {
                    ZonalStat currStat = new ZonalStat();
                    currStat.zoneName = currsector;
                    currStat.count = 1;
                    currPerM.put(currsector, currStat);

                }
                if (currPerF.containsKey(currsector) && cntUn == 1)
                {
                    ZonalStat currStat = (ZonalStat)currPerF.get(currsector);
                    currStat.count++;
                    currPerF.put(currsector,currStat);
                }
                else if (cntUn == 1)
                {
                    ZonalStat currStat = new ZonalStat();
                    currStat.zoneName = currsector;
                    currStat.count = 1;
                    currPerF.put(currsector, currStat);

                }
                if (currPerTwo.containsKey(currsector) && cntUn == 2)
                {
                    ZonalStat currStat = (ZonalStat)currPerTwo.get(currsector);
                    currStat.count++;
                    currPerTwo.put(currsector,currStat);
                }
                else if (cntUn == 2)
                {
                    ZonalStat currStat = new ZonalStat();
                    currStat.zoneName = currsector;
                    currStat.count = 1;
                    currPerTwo.put(currsector, currStat);
                }
                if (currPerThree.containsKey(currsector) && cntUn == 3)
                {
                    ZonalStat currStat = (ZonalStat)currPerThree.get(currsector);
                    currStat.count++;
                    currPerThree.put(currsector,currStat);
                }
                else if (cntUn == 3)
                {
                    ZonalStat currStat = new ZonalStat();
                    currStat.zoneName = currsector;
                    currStat.count = 1;
                    currPerThree.put(currsector, currStat);

                }
                if (currPerFour.containsKey(currsector) && cntUn == 4)
                {
                    ZonalStat currStat = (ZonalStat)currPerFour.get(currsector);
                    currStat.count++;
                    currPerFour.put(currsector,currStat);
                }
                else if (cntUn == 4)
                {
                    ZonalStat currStat = new ZonalStat();
                    currStat.zoneName = currsector;
                    currStat.count = 1;
                    currPerFour.put(currsector, currStat);

                }
                if (currPerFive.containsKey(currsector) && cntUn == 5)
                {
                    ZonalStat currStat = (ZonalStat)currPerFive.get(currsector);
                    currStat.count++;
                    currPerFive.put(currsector,currStat);
                }
                else if (cntUn == 5)
                {
                    ZonalStat currStat = new ZonalStat();
                    currStat.zoneName = currsector;
                    currStat.count = 1;
                    currPerFive.put(currsector, currStat);

                }
            }
            currOutputFile.write("Commune,Male,Female,Per2,Per3,Per4,Per5");
            currOutputFile.newLine();
            String str = currComuneList.readLine();

            while (currComuneList.ready())
            {
                str = currComuneList.readLine();
                String strConcat = str;
                if (currPerM.containsKey(str))
                {
                    ZonalStat curSt = (ZonalStat)currPerM.get(str);
                    strConcat += "," + curSt.count;
                }
                else
                {
                    strConcat += ",0";
                }

                if (currPerF.containsKey(str))
                {
                    ZonalStat curSt = (ZonalStat)currPerF.get(str);
                    strConcat += "," + curSt.count;
                }
                else
                {
                    strConcat += ",0";
                }

                if (currPerTwo.containsKey(str))
                {
                    ZonalStat curSt = (ZonalStat)currPerTwo.get(str);
                    strConcat += "," + curSt.count;
                }
                else
                {
                    strConcat += ",0";
                }

                if (currPerThree.containsKey(str))
                {
                    ZonalStat curSt = (ZonalStat)currPerThree.get(str);
                    strConcat += "," + curSt.count;
                }
                else
                {
                    strConcat += ",0";
                }
                if (currPerFour.containsKey(str))
                {
                    ZonalStat curSt = (ZonalStat)currPerFour.get(str);
                    strConcat += "," + curSt.count;
                }
                else
                {
                    strConcat += ",0";
                }
                if (currPerFive.containsKey(str))
                {
                    ZonalStat curSt = (ZonalStat)currPerFive.get(str);
                    strConcat += "," + curSt.count;
                }
                else
                {
                    strConcat += ",0";
                }
                currOutputFile.write(strConcat);
                currOutputFile.newLine();
            }

            currReader.close();
            currComuneList.close();
            currOutputFile.close();
        }

        public void ComputeCommuneLevelStatisticsCars(String poplFile,
                            String fileNam, String comList) throws IOException
        {
            BufferedReader currReader = new BufferedReader(new FileReader(poplFile));
            BufferedReader currComuneList = new BufferedReader(new FileReader(comList));
            HashMap<String, Object> currCarZero = new HashMap<String, Object>();
            HashMap<String, Object> currCarOne = new HashMap<String, Object>();
            HashMap<String, Object> currCarTwo = new HashMap<String, Object>();
            HashMap<String, Object> currCarThree = new HashMap<String, Object>();

            BufferedWriter currOutputFile = new
                BufferedWriter(new FileWriter(fileNam));
            String currHhld;
            currReader.readLine();
            while (currReader.ready())
            {
                currHhld = currReader.readLine();
                String[] currHhldTok = currHhld.split(",");
                String currsector = currHhldTok[1].substring(0, 5);
                int cntUn = Integer.parseInt(currHhldTok[7]);
                if (currCarZero.containsKey(currsector) && cntUn == 0)
                {
                    ZonalStat currStat = (ZonalStat)currCarZero.get(currsector);
                    currStat.count++;
                    currCarZero.put(currsector, currStat);
                }
                else if (cntUn == 0)
                {
                    ZonalStat currStat = new ZonalStat();
                    currStat.zoneName = currsector;
                    currStat.count = 1;
                    currCarZero.put(currsector, currStat);

                }
                if (currCarOne.containsKey(currsector) && cntUn == 1)
                {
                    ZonalStat currStat = (ZonalStat)currCarOne.get(currsector);
                    currStat.count++;
                    currCarOne.put(currsector,currStat);
                }
                else if (cntUn == 1)
                {
                    ZonalStat currStat = new ZonalStat();
                    currStat.zoneName = currsector;
                    currStat.count = 1;
                    currCarOne.put(currsector, currStat);

                }
                if (currCarTwo.containsKey(currsector) && cntUn == 2)
                {
                    ZonalStat currStat = (ZonalStat)currCarTwo.get(currsector);
                    currStat.count++;
                    currCarTwo.put(currsector, currStat);
                }
                else if (cntUn == 2)
                {
                    ZonalStat currStat = new ZonalStat();
                    currStat.zoneName = currsector;
                    currStat.count = 1;
                    currCarTwo.put(currsector, currStat);
                }
                if (currCarThree.containsKey(currsector) && cntUn == 3)
                {
                    ZonalStat currStat = (ZonalStat)currCarThree.get(currsector);
                    currStat.count++;
                    currCarThree.put(currsector, currStat);
                }
                else if (cntUn == 3)
                {
                    ZonalStat currStat = new ZonalStat();
                    currStat.zoneName = currsector;
                    currStat.count = 1;
                    currCarThree.put(currsector, currStat);

                }
            }
            currOutputFile.write("Commune,Car0,Car1,Car2,Car3");
            currOutputFile.newLine();
            String str = currComuneList.readLine();

            while (currComuneList.ready())
            {
                str = currComuneList.readLine();
                String strConcat = str;
                if(currCarZero.containsKey(str))
                {
                    ZonalStat curSt = (ZonalStat)currCarZero.get(str);
                    strConcat += "," + curSt.count;
                }
                else
                {
                    strConcat += ",0";
                }

                if (currCarOne.containsKey(str))
                {
                    ZonalStat curSt = (ZonalStat)currCarOne.get(str);
                    strConcat += "," + curSt.count;
                }
                else
                {
                    strConcat += ",0";
                }

                if (currCarTwo.containsKey(str))
                {
                    ZonalStat curSt = (ZonalStat)currCarTwo.get(str);
                    strConcat += "," + curSt.count;
                }
                else
                {
                    strConcat += ",0";
                }

                if (currCarThree.containsKey(str))
                {
                    ZonalStat curSt = (ZonalStat)currCarThree.get(str);
                    strConcat += "," + curSt.count;
                }
                else
                {
                    strConcat += ",0";
                }
                currOutputFile.write(strConcat);
                currOutputFile.newLine();
            }

            currReader.close();
            currComuneList.close();
            currOutputFile.close();
        }

        public ArrayList ComputeCommuneMCStatsCars(int runNum, int seed,
                            String poolFileName, boolean delRealizations) throws IOException
        {
            String poplFile = Utils.DATA_DIR +
                            "Household\\PopulationRealization" + runNum + ".csv";
            CreatePopulationByDwellingType(seed,poolFileName,poplFile);
            BufferedReader currReader = new BufferedReader(new FileReader(poplFile));
            HashMap<String, Object> currCarZero = new HashMap<String, Object>();
            HashMap<String, Object> currCarOne = new HashMap<String, Object>();
            HashMap<String, Object> currCarTwo = new HashMap<String, Object>();
            HashMap<String, Object> currCarThree = new HashMap<String, Object>();
            String currHhld;
            currReader.readLine();
            while (currReader.ready())
            {
                currHhld = currReader.readLine();
                String[] currHhldTok = currHhld.split(",");
                String currsector = currHhldTok[1].substring(0, 5);
                int cntUn = Integer.parseInt(currHhldTok[7]);
                if (currCarZero.containsKey(currsector) && cntUn == 0)
                {
                    ZonalStat currStat = (ZonalStat)currCarZero.get(currsector);
                    currStat.count++;
                    currCarZero.put(currsector, currStat);
                }
                else if (cntUn == 0)
                {
                    ZonalStat currStat = new ZonalStat();
                    currStat.zoneName = currsector;
                    currStat.count = 1;
                    currCarZero.put(currsector, currStat);

                }
                if (currCarOne.containsKey(currsector) && cntUn == 1)
                {
                    ZonalStat currStat = (ZonalStat)currCarOne.get(currsector);
                    currStat.count++;
                    currCarOne.put(currsector, currStat);
                }
                else if (cntUn == 1)
                {
                    ZonalStat currStat = new ZonalStat();
                    currStat.zoneName = currsector;
                    currStat.count = 1;
                    currCarOne.put(currsector, currStat);

                }
                if (currCarTwo.containsKey(currsector) && cntUn == 2)
                {
                    ZonalStat currStat = (ZonalStat)currCarTwo.get(currsector);
                    currStat.count++;
                    currCarTwo.put(currsector, currStat);
                }
                else if (cntUn == 2)
                {
                    ZonalStat currStat = new ZonalStat();
                    currStat.zoneName = currsector;
                    currStat.count = 1;
                    currCarTwo.put(currsector, currStat);
                }
                if (currCarThree.containsKey(currsector) && cntUn == 3)
                {
                    ZonalStat currStat = (ZonalStat)currCarThree.get(currsector);
                    currStat.count++;
                    currCarThree.put(currsector, currStat);
                }
                else if (cntUn == 3)
                {
                    ZonalStat currStat = new ZonalStat();
                    currStat.zoneName = currsector;
                    currStat.count = 1;
                    currCarThree.put(currsector, currStat);

                }
            }

            ArrayList currArrayList = new ArrayList();
            currArrayList.add(currCarZero);
            currArrayList.add(currCarOne);
            currArrayList.add(currCarTwo);
            currArrayList.add(currCarThree);
            currReader.close();
            if (delRealizations == true)
            {
                File f = new File(poplFile);
                f.delete();
            }
            return currArrayList;
        }

        public void WriteMCStatsToFile(String CommuneList, ArrayList RunsOutput,
                                int category) throws IOException
        {
            BufferedReader currComuneList = new BufferedReader(new FileReader(CommuneList));
            BufferedWriter currOutputFile = new
                BufferedWriter(new FileWriter(Utils.DATA_DIR +
                "CommuneStatistics_Cars"+category+".csv"));

            //currOutputFile.WriteLine("Commune,Car0,Car1,Car2,Car3");
            String str = currComuneList.readLine();

            while (currComuneList.ready())
            {
                str = currComuneList.readLine();
                String strConcat = str;
                for (int j = 0; j < RunsOutput.size(); j++)
                {
                    HashMap<String, Object> currStat = (HashMap<String, Object>)RunsOutput.get(j);
                    if (currStat.containsKey(str))
                    {
                        ZonalStat curSt = (ZonalStat)currStat.get(str);
                        strConcat += "," + curSt.count;
                    }
                    else
                    {
                        strConcat += ",0";
                    }
                }
                currOutputFile.write(strConcat);
                currOutputFile.newLine();
            }

            currComuneList.close();
            currOutputFile.close();
        }

        public void CreatePersonPopulation(String popPoolFileNm,
                            String outFileNm, int cnt) throws IOException
        {
            BufferedReader currReader = new BufferedReader(new FileReader(popPoolFileNm));
            currReader.readLine();
            Date date = new Date();
            RandomNumberGen currRandGen = new RandomNumberGen((int) date.getTime());
            BufferedWriter currOutputFile = new BufferedWriter(new FileWriter(outFileNm));
            currOutputFile.write("ID,Age,Sex,HhldSize,Edu_Lvl");
            currOutputFile.newLine();
            String currInStr;
            int currCnt = 0;
            while ((currInStr = currReader.readLine()) != null)
            {
                String[] currStrTok = currInStr.split(",");

                if (currRandGen.NextDouble() < 0.5 && currCnt < cnt)
                {
                    currCnt++;
                    currOutputFile.write(currStrTok[0]
                                            + "," + currStrTok[2]
                                            + "," + currStrTok[3]
                                            + "," + currStrTok[4]
                                            + "," + currStrTok[5]);
                    currOutputFile.newLine();
                }
            }
            currOutputFile.close();
            currReader.close();
        }

        public void ComputeSectorLevelStatistics(String poplFile, int sectIndx,
                                int dimIndx, int catCnt) throws IOException
        {
            BufferedReader currReader = new BufferedReader(new FileReader(poplFile));

            HashMap<String, Object> currDimension = new HashMap<String, Object>();

            BufferedWriter currOutputFile = new
                BufferedWriter(new FileWriter(Utils.DATA_DIR
                            + "SectorStatistics"+dimIndx+".csv"));
            String currHhld;
            currReader.readLine();
            for (int i = 1001; i < 5946; i++)
            {
                currDimension.put(Integer.toString(i), new HashMap<String, Object>());
            }
            while (currReader.ready())
            {
                currHhld = currReader.readLine();
                String[] currHhldTok = currHhld.split(",");
                String currsector = currHhldTok[sectIndx];
                /*if (currDimension.Contains(currsector))
                {*/
                    HashMap<String, Object> currData = (HashMap<String, Object>) currDimension.get(currsector);
                    if(currData.containsKey(currHhldTok[dimIndx]))
                    {
                        KeyValPair currStat = (KeyValPair)
                            currData.get(currHhldTok[dimIndx]);
                        currStat.value++;
                        currData.put(currHhldTok[dimIndx], currStat);
                        currDimension.put(currsector, currData);
                    }else
                    {
                        KeyValPair currStat = new KeyValPair();
                        currStat.category = currHhldTok[dimIndx];
                        currStat.value = 1;
                        HashMap<String, Object> currCat = (HashMap<String, Object>)
                                currDimension.get(currsector);
                        currCat.put(currStat.category, currStat);
                        currDimension.put(currsector, currCat);
                    }
                /*}
                else
                {
                    Hashtable currCat = new Hashtable();
                    KeyValPair currStat = new KeyValPair();
                    currStat.category = currHhldTok[dimIndx];
                    currStat.value = 1;
                    currCat.Add(currStat.category, currStat);
                    currDimension.Add(currsector, currCat);
                }*/
            }
            String firstRow = "Sector";
            for (int i = 0; i < catCnt; i++)
            {
                firstRow += "," + i;
            }
            currOutputFile.write(firstRow);
            currOutputFile.newLine();
            for (Map.Entry<String, Object> ent : currDimension.entrySet())
            {
                HashMap<String, Object> catEnt = (HashMap<String, Object>) ent.getValue();
                String curString = (String) ent.getKey();
                for(int i = 0; i < catCnt; i++)
                {
                    if (catEnt.containsKey(Integer.toString(i)))
                    {
                        KeyValPair curSt = (KeyValPair)catEnt.get(Integer.toString(i));
                        curString.concat( "," +
                            ((int)curSt.value));
                    }
                    else
                    {
                        curString += ",0";
                    }
                }
                currOutputFile.write(curString);
                 currOutputFile.newLine();
            }
            currReader.close();
            currOutputFile.close();
        }

        // [BF] make it proper
        private void LoadMarginalsForDwellings() throws IOException
        {
            BufferedReader myFileReader = new BufferedReader(new FileReader(Utils.DATA_DIR+
            "Household\\CensusDwellingType.csv"));
            String strTok;
            myFileReader.readLine();
            while ((strTok = myFileReader.readLine()) != null)
            {
                String[] strToken = strTok.split(",");
                double n_sep =  Double.parseDouble(strToken[1]);
                double n_sem =  Double.parseDouble(strToken[2]);
                double n_att = Double.parseDouble(strToken[3]);
                double n_app = Double.parseDouble(strToken[4]);
                double sumD = n_sep + n_sem + n_att + n_app;
                SpatialZone currZone = (SpatialZone)myZonalCollection.get(strToken[0]);

                if (sumD == 0)
                {
                    currZone.myDwellMarginal.AddValue(
                        "0", 0.25);
                    currZone.myDwellMarginal.AddValue(
                        "1", 0.25);
                    currZone.myDwellMarginal.AddValue(
                        "2", 0.25);
                    currZone.myDwellMarginal.AddValue(
                        "3", 0.25);
                }
                else
                {
                    currZone.myDwellMarginal.AddValue(
                        "0", n_sep / sumD);
                    currZone.myDwellMarginalCounts.AddValue(
                        "0", n_sep);
                    currZone.myDwellMarginal.AddValue(
                        "1", n_sem / sumD);
                    currZone.myDwellMarginalCounts.AddValue(
                        "1", n_sem);
                    currZone.myDwellMarginal.AddValue(
                        "2", n_att / sumD);
                    currZone.myDwellMarginalCounts.AddValue(
                        "2", n_att);
                    currZone.myDwellMarginal.AddValue(
                        "3", n_app / sumD);
                    currZone.myDwellMarginalCounts.AddValue(
                        "3", n_app);
                }
            }
            myFileReader.close();
        }

        private void LoadMarginalsForCars() throws IOException
        {
            BufferedReader myFileReader = new BufferedReader(new FileReader(Utils.DATA_DIR +
            "Household\\CensusNumOfCars.csv"));
            String strTok;
            myFileReader.readLine();
            while ((strTok = myFileReader.readLine()) != null)
            {
                String[] strToken = strTok.split(",");
                double n_zero = Double.parseDouble(strToken[1]);
                double n_one = Double.parseDouble(strToken[2]);
                double n_two = Double.parseDouble(strToken[3]);
                double n_three = Double.parseDouble(strToken[4]);
                double sumD = n_zero + n_one + n_two + n_three;
                SpatialZone currZone = (SpatialZone)myZonalCollection.get(strToken[0]);
                if (sumD == 0.00)
                {
                    currZone.myCarsMarginal.AddValue(
                        "0", 0.4);
                    currZone.myCarsMarginal.AddValue(
                        "1", 0.3);
                    currZone.myCarsMarginal.AddValue(
                        "2", 0.15);
                    currZone.myCarsMarginal.AddValue(
                        "3", 0.15);
                }
                else
                {
                    currZone.myCarsMarginal.AddValue(
                        "0", n_zero / sumD);
                    currZone.myCarsMarginal.AddValue(
                        "1", n_one / sumD);
                    currZone.myCarsMarginal.AddValue(
                        "2", n_two / sumD);
                    currZone.myCarsMarginal.AddValue(
                        "3", n_three / sumD);
                }
            }
            myFileReader.close();
        }

        private void LoadMarginalsForPersons() throws IOException
        {
            BufferedReader myFileReader = new BufferedReader(new FileReader(Utils.DATA_DIR +
            "Household\\CensusNumOfPers.csv"));
            String strTok;
            myFileReader.readLine();
            while ((strTok = myFileReader.readLine()) != null)
            {
                String[] strToken = strTok.split(",");
                double n_zero = Double.parseDouble(strToken[1]);
                double n_one = Double.parseDouble(strToken[2]);
                double n_two = Double.parseDouble(strToken[3]);
                double n_three = Double.parseDouble(strToken[4]);
                double n_four = Double.parseDouble(strToken[5]);
                double n_five = Double.parseDouble(strToken[6]);

                double sumD =
                    n_zero + n_one + n_two + n_three + n_four + n_five;
                SpatialZone currZone =
                    (SpatialZone)myZonalCollection.get(strToken[0]);
                if (sumD == 0.00)
                {
                    currZone.myPersonMarginal.AddValue(
                        "0", 0.4);
                    currZone.myPersonMarginal.AddValue(
                        "1", 0.4);
                    currZone.myPersonMarginal.AddValue(
                        "2", 0.2);
                    currZone.myPersonMarginal.AddValue(
                        "3", 0.0);
                    currZone.myPersonMarginal.AddValue(
                        "4", 0.0);
                    currZone.myPersonMarginal.AddValue(
                        "5", 0.0);
                }
                else
                {
                    currZone.myPersonMarginal.AddValue(
                        "0", n_zero / sumD);
                    currZone.myPersonMarginal.AddValue(
                        "1", n_one / sumD);
                    currZone.myPersonMarginal.AddValue(
                        "2", n_two / sumD);
                    currZone.myPersonMarginal.AddValue(
                        "3", n_three / sumD);
                    currZone.myPersonMarginal.AddValue(
                        "4", n_four / sumD);
                    currZone.myPersonMarginal.AddValue(
                        "5", n_five / sumD);
                }
            }
            myFileReader.close();
        }

        private void LoadMarginalsForHhldSize2() throws IOException
        {
            BufferedReader myFileReader = new BufferedReader(new FileReader(Utils.DATA_DIR +
            "\\CensusHhldSize2Marginal.csv"));
            String strTok;
            myFileReader.readLine();
            while ((strTok = myFileReader.readLine()) != null)
            {
                String[] strToken = strTok.split(",");
                if (!strToken[0].equals("1004"))
                {
                    continue;
                }
                double n_zero = Double.parseDouble(strToken[1]);
                double n_one = Double.parseDouble(strToken[2]);
                double n_two = Double.parseDouble(strToken[3]);
                double n_three = Double.parseDouble(strToken[4]);
                double n_four = Double.parseDouble(strToken[5]);
                double n_five = Double.parseDouble(strToken[6]);

                double sumD =
                    n_zero + n_one + n_two + n_three + n_four + n_five;
                SpatialZone currZone =
                    (SpatialZone)myZonalCollection.get(strToken[0]);
                if (sumD == 0.00)
                {
                    /*currZone.myHhldSize2Marginal.AddValue(
                        "0", 0.15);
                    currZone.myHhldSize2Marginal.AddValue(
                        "1", 0.3);
                    currZone.myHhldSize2Marginal.AddValue(
                        "2", 0.2);
                    currZone.myHhldSize2Marginal.AddValue(
                        "3", 0.2);
                    currZone.myHhldSize2Marginal.AddValue(
                        "4", 0.05);
                    currZone.myHhldSize2Marginal.AddValue(
                        "5", 0.1);*/
                }
                else
                {
                    currZone.myHhldSize2Marginal.AddValue(
                        "0", n_zero);
                    currZone.myHhldSize2Marginal.AddValue(
                        "1", n_one);
                    currZone.myHhldSize2Marginal.AddValue(
                        "2", n_two);
                    currZone.myHhldSize2Marginal.AddValue(
                        "3", n_three);
                    currZone.myHhldSize2Marginal.AddValue(
                        "4", n_four);
                    currZone.myHhldSize2Marginal.AddValue(
                        "5", n_five);
                }
            }
            myFileReader.close();
        }

        private void LoadMarginalsForAge() throws IOException
        {
            BufferedReader myFileReader = new BufferedReader(new FileReader(Utils.DATA_DIR +
            "\\CensusAgeMarginal.csv"));
            String strTok;
            myFileReader.readLine();
            while ((strTok = myFileReader.readLine()) != null)
            {
                String[] strToken = strTok.split(",");
                if (!strToken[0].equals("1004"))
                {
                    continue;
                }
                double n_zero = Double.parseDouble(strToken[1]);
                double n_one = Double.parseDouble(strToken[2]);
                double n_two = Double.parseDouble(strToken[3]);
                double n_three = Double.parseDouble(strToken[4]);
                double n_four = Double.parseDouble(strToken[5]);
                double n_five = Double.parseDouble(strToken[6]);
                double n_six = Double.parseDouble(strToken[7]);
                double n_seven = Double.parseDouble(strToken[8]);

                double sumD =
                    n_zero + n_one + n_two + n_three + n_four + n_five
                    + n_six + n_seven;
                SpatialZone currZone =
                    (SpatialZone)myZonalCollection.get(strToken[0]);
                if (sumD == 0.00)
                {
                    /*currZone.myHhldSize2Marginal.AddValue(
                        "0", 0.15);
                    currZone.myHhldSize2Marginal.AddValue(
                        "1", 0.3);
                    currZone.myHhldSize2Marginal.AddValue(
                        "2", 0.2);
                    currZone.myHhldSize2Marginal.AddValue(
                        "3", 0.2);
                    currZone.myHhldSize2Marginal.AddValue(
                        "4", 0.05);
                    currZone.myHhldSize2Marginal.AddValue(
                        "5", 0.1);*/
                }
                else
                {
                    currZone.myAgeMarginal.AddValue(
                        "0", n_zero);
                    currZone.myAgeMarginal.AddValue(
                        "1", n_one);
                    currZone.myAgeMarginal.AddValue(
                        "2", n_two);
                    currZone.myAgeMarginal.AddValue(
                        "3", n_three);
                    currZone.myAgeMarginal.AddValue(
                        "4", n_four);
                    currZone.myAgeMarginal.AddValue(
                        "5", n_five);
                    currZone.myAgeMarginal.AddValue(
                        "6", n_six);
                    currZone.myAgeMarginal.AddValue(
                        "7", n_seven);
                }
            }
            myFileReader.close();
        }

        private void LoadMarginalsForSex() throws IOException
        {
            BufferedReader myFileReader = new BufferedReader(new FileReader(Utils.DATA_DIR +
            "\\CensusSexMarginal.csv"));
            String strTok;
            myFileReader.readLine();
            while ((strTok = myFileReader.readLine()) != null)
            {
                String[] strToken = strTok.split(",");
                if (!strToken[0].equals("1004"))
                {
                    continue;
                }
                double n_zero = Double.parseDouble(strToken[1]);
                double n_one = Double.parseDouble(strToken[2]);

                double sumD = n_zero + n_one;
                SpatialZone currZone =
                    (SpatialZone)myZonalCollection.get(strToken[0]);
                if (sumD == 0.00)
                {
                    /*currZone.myHhldSize2Marginal.AddValue(
                        "0", 0.15);
                    currZone.myHhldSize2Marginal.AddValue(
                        "1", 0.3);
                    currZone.myHhldSize2Marginal.AddValue(
                        "2", 0.2);
                    currZone.myHhldSize2Marginal.AddValue(
                        "3", 0.2);
                    currZone.myHhldSize2Marginal.AddValue(
                        "4", 0.05);
                    currZone.myHhldSize2Marginal.AddValue(
                        "5", 0.1);*/
                }
                else
                {
                    currZone.mySexMarginal.AddValue(
                        "0", n_zero);
                    currZone.mySexMarginal.AddValue(
                        "1", n_one);
                }
            }
            myFileReader.close();
        }

        private void LoadMarginalsForEducation() throws IOException
        {
            BufferedReader myFileReader = new BufferedReader(new FileReader(Utils.DATA_DIR +
            "\\CensusEducationMarginal.csv"));
            String strTok;
            myFileReader.readLine();
            while ((strTok = myFileReader.readLine()) != null)
            {
                String[] strToken = strTok.split(",");
                if (!strToken[0].equals("1004"))
                {
                    continue;
                }
                double n_zero = Double.parseDouble(strToken[1]);
                double n_one = Double.parseDouble(strToken[2]);
                double n_two = Double.parseDouble(strToken[1]);
                double n_three = Double.parseDouble(strToken[2]);

                double sumD = n_zero + n_one;
                SpatialZone currZone =
                    (SpatialZone)myZonalCollection.get(strToken[0]);
                if (sumD == 0.00)
                {
                    /*currZone.myHhldSize2Marginal.AddValue(
                        "0", 0.15);
                    currZone.myHhldSize2Marginal.AddValue(
                        "1", 0.3);
                    currZone.myHhldSize2Marginal.AddValue(
                        "2", 0.2);
                    currZone.myHhldSize2Marginal.AddValue(
                        "3", 0.2);
                    currZone.myHhldSize2Marginal.AddValue(
                        "4", 0.05);
                    currZone.myHhldSize2Marginal.AddValue(
                        "5", 0.1);*/
                }
                else
                {
                    currZone.myEducationMarginal.AddValue(
                        "0", n_zero);
                    currZone.myEducationMarginal.AddValue(
                        "1", n_one);
                    currZone.myEducationMarginal.AddValue(
                        "2", n_two);
                    currZone.myEducationMarginal.AddValue(
                        "3", n_three);
                }
            }
            myFileReader.close();
        }
        
        public static String fmt(double d)
        {
            if(d == (long) d)
                return String.format("%d",(long)d);
            else
                return String.format("%s",d);
        }
        
        public void printLocalMarginalFittingAnalysis(){
        	
        	OutputFileWritter outputFile = new OutputFileWritter();
        	try{
        		outputFile.OpenFile(Utils.pathToMarginalChecks);
            	String headers = "daId";
            	for(int i = 0; i<ConfigFile.AttributeDefinitionsImportance.size(); i++){
            		headers = headers + Utils.COLUMN_DELIMETER + ConfigFile.AttributeDefinitionsImportance.get(i).category;
            		outputFile.WriteToFile(headers);
            	}

            	for (Map.Entry<String, Object> entry : myZonalCollection.entrySet())
                {
            		
            		SpatialZone currZone = (SpatialZone)entry.getValue();
            		String zonalMarginals = fmt(currZone.myAttributes.get(0).value);
            		Double delta;
            		Double mTargeted;
            		Double mReached;
            		Double sum;
            		for(int i = 0; i<ConfigFile.AttributeDefinitionsImportance.size(); i++){
            			delta = 0.0;
            			sum =0.0;
            			
            			for(int j = 0; j < ConfigFile.AttributeDefinitionsImportance.get(i).value; j++){
            				mTargeted = currZone.marginalTargets.get(ConfigFile.AttributeDefinitionsImportance.get(i)).GetValue(fmt(j));
            				mReached = currZone.marginalCounters.get(ConfigFile.AttributeDefinitionsImportance.get(i)).GetValue(fmt(j));
            				delta = delta + Math.abs(mTargeted-mReached);
            				sum = sum + mTargeted;
            			}
                		zonalMarginals = zonalMarginals + Utils.COLUMN_DELIMETER + Double.toString(delta/sum);
                	}
            		outputFile.WriteToFile(zonalMarginals);
                }
            	outputFile.CloseFile();
        	}
        	catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    			
    		}
         
        }
        
public void printMetroMarginalFittingAnalysis(String metro, long startTime){
        	
        	OutputFileWritter outputFile = new OutputFileWritter();
        	try{
        		outputFile.OpenFile(Utils.DATA_DIR + "data\\" + fmt(myAttributes.get(0).value) + "\\marginalCheck.csv");
        		
        		String config = "Marginal checking for " + metro;
        		outputFile.WriteToFile(config);
        		config = "Warmup iterations: " + Utils.COLUMN_DELIMETER + Utils.WARMUP_ITERATIONS;
        		outputFile.WriteToFile(config);
        		config = "Skip iterations:" + Utils.COLUMN_DELIMETER + Utils.SKIP_ITERATIONS;
        		outputFile.WriteToFile(config);
        		config = "Pool count:" + Utils.COLUMN_DELIMETER + Utils.POOL_COUNT;
        		outputFile.WriteToFile(config);

        		for(int i = 0; i < ConfigFile.AttributeDefinitions.size(); i++){
        			String headers = attributesMainConditionals.get(i).GetDimensionName();
        			for(int j = 0; j < ConfigFile.AttributeDefinitions.get(i).value; j++){
        				headers = headers + Utils.COLUMN_DELIMETER  + Integer.toString(j);	
        			}
        			outputFile.WriteToFile(headers);
        			String target = "Distribution target";
        			String result = "Number generated";
        			for(int j = 0; j < ConfigFile.AttributeDefinitions.get(i).value; j++){
        				//System.out.println();
        				String tempCat = attributesMainConditionals.get(i).GetDimensionName();
        				target = target + Utils.COLUMN_DELIMETER + marginalTargets.get(tempCat).GetValue(fmt(j));
        				result = result + Utils.COLUMN_DELIMETER + marginalCounters.get(tempCat).GetValue(fmt(j));
        			}
        			outputFile.WriteToFile(target);
        			outputFile.WriteToFile(result);
        		}
        		
        		long endTime = System.currentTimeMillis();
        		String clock = "Run time (s)," + String.valueOf((endTime-startTime)/1000);
        		outputFile.WriteToFile(clock);
        		clock = "Run time (h), " + String.valueOf((endTime-startTime)/1000/3600);
        		outputFile.WriteToFile(clock);
        		
            	outputFile.CloseFile();
        	}
        	catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    			
    		}
         
        }

public void printStandardDeviationAnalysis(String metro, long startTime){
	
	OutputFileWritter outputFile = new OutputFileWritter();
	try{
		outputFile.OpenFile(Utils.DATA_DIR + "data\\" + fmt(myAttributes.get(0).value) + "\\marginalCheck.csv");
		
		String config = "Marginal checking for " + metro;
		outputFile.WriteToFile(config);
		config = "Warmup iterations: " + Utils.COLUMN_DELIMETER + Utils.WARMUP_ITERATIONS;
		outputFile.WriteToFile(config);
		config = "Skip iterations:" + Utils.COLUMN_DELIMETER + Utils.SKIP_ITERATIONS;
		outputFile.WriteToFile(config);
		config = "Pool count:" + Utils.COLUMN_DELIMETER + Utils.POOL_COUNT;
		outputFile.WriteToFile(config);

		for(int i = 0; i < ConfigFile.AttributeDefinitions.size(); i++){
			String headers = attributesMainConditionals.get(i).GetDimensionName();
			for(int j = 0; j < ConfigFile.AttributeDefinitions.get(i).value; j++){
				headers = headers + Utils.COLUMN_DELIMETER  + Integer.toString(j);	
			}
			outputFile.WriteToFile(headers);
			String target = "Distribution target";
			String result = "Number generated";
			for(int j = 0; j < ConfigFile.AttributeDefinitions.get(i).value; j++){
				//System.out.println();
				String tempCat = attributesMainConditionals.get(i).GetDimensionName();
				target = target + Utils.COLUMN_DELIMETER + marginalTargets.get(tempCat).GetValue(fmt(j));
				result = result + Utils.COLUMN_DELIMETER + marginalCounters.get(tempCat).GetValue(fmt(j));
			}
			outputFile.WriteToFile(target);
			outputFile.WriteToFile(result);
		}
		
		long endTime = System.currentTimeMillis();
		String clock = "Run time (s)," + String.valueOf((endTime-startTime)/1000);
		outputFile.WriteToFile(clock);
		clock = "Run time (h), " + String.valueOf((endTime-startTime)/1000/3600);
		outputFile.WriteToFile(clock);
		
    	outputFile.CloseFile();
	}
	catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		
	}
 
}

public void printLocalMarginalFittingAnalysis(String metro, long startTime){
	
	
	
	for (Map.Entry<String, Object> entry : myZonalCollection.entrySet())
    {
		
		OutputFileWritter outputFile = new OutputFileWritter();
		SpatialZone currZone = (SpatialZone)entry.getValue();
		try{
			outputFile.OpenFile(Utils.DATA_DIR + "data\\" + fmt(currZone.myAttributes.get(0).value) + "\\marginalCheck.csv");
			
			String config = "Marginal checking for " + fmt(currZone.myAttributes.get(0).value);
			outputFile.WriteToFile(config);
			config = "Warmup iterations: " + Utils.COLUMN_DELIMETER + Utils.WARMUP_ITERATIONS;
			outputFile.WriteToFile(config);
			config = "Skip iterations:" + Utils.COLUMN_DELIMETER + Utils.SKIP_ITERATIONS;
			outputFile.WriteToFile(config);
			config = "Pool count:" + Utils.COLUMN_DELIMETER + Utils.POOL_COUNT;
			outputFile.WriteToFile(config);
	
			for(int i = 0; i < ConfigFile.AttributeDefinitionsImportance.size(); i++){
				String headers = currZone.myAttributesDiscConditional.get(i).GetDimensionName();
				for(int j = 0; j < ConfigFile.AttributeDefinitionsImportance.get(i).value; j++){
					headers = headers + Utils.COLUMN_DELIMETER  + Integer.toString(j);	
				}
				outputFile.WriteToFile(headers);
				String target = "Distribution target";
				String result = "Number generated";
				for(int j = 0; j < ConfigFile.AttributeDefinitionsImportance.get(i).value; j++){
					//System.out.println();
					String tempCat = attributesMainConditionals.get(i).GetDimensionName();
					target = target + Utils.COLUMN_DELIMETER + currZone.marginalTargets.get(tempCat).GetValue(fmt(j));
					result = result + Utils.COLUMN_DELIMETER + currZone.marginalCounters.get(tempCat).GetValue(fmt(j));
				}
				outputFile.WriteToFile(target);
				outputFile.WriteToFile(result);
			}
			
			long endTime = System.currentTimeMillis();
			String clock = "Run time (s)," + String.valueOf((endTime-startTime)/1000);
			outputFile.WriteToFile(clock);
			clock = "Run time (h), " + String.valueOf((endTime-startTime)/1000/3600);
			outputFile.WriteToFile(clock);
			
	    	outputFile.CloseFile();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
    }
 
}
        
        public ArrayList GetZonalCondCollections()
        {
        	ArrayList<ConditionalDistribution> currColl = new ArrayList<ConditionalDistribution>();
        	
        	int numberofmodels = 1;
        	int numberofcounts = 1;
        	
        	for (int i=0; i<ConfigFile.AttributeDefinitionsImportance.size(); i++)
        	{
        		if (ConfigFile.TypeOfConditionals.get(i).equals("count"))
        		{
        				numberofcounts += numberofcounts;
        			
        				currColl.add((ConditionalDistribution) attributesMainConditionals.get(i-numberofmodels+1));
        		}
            	
        		else if (ConfigFile.TypeOfConditionals.get(i).equals("model"))
        		{
        				numberofmodels += numberofmodels;
        				currColl.add((ConditionalDistribution) attributesMainConditionals.get(i-numberofcounts+1));
        		}
        	}
        	   return currColl;
        }
        
        /*public void updateCounters(Person agent) {
    		// TODO Auto-generated method stub
    		Iterator it = agent.myAttributes.iterator();
    		while(it.hasNext()){
    			KeyValPair k = (KeyValPair)it.next();
    			if(marginalCounters.containsKey(k.category)){
    				marginalCounters.get(k.category).update(ConfigFile.fmt(k.value));
    			}
    			else{
    				System.out.println("littleproblem");
    			}
    			
    		}
        }*/
        
        private static class processZone
        implements Callable, Runnable {
        	HashMap<String, Object> zones;
        	String pathToSeeds;
        	ArrayList myPersonPool;
        	ArrayList attributesMainConditionals;
        	String statistics;
        	World thisWorld;
        	public processZone(Object subSample, String path2seeds, ArrayList pool, ArrayList mainCdt, World curWorld){
        		zones = (HashMap)subSample;
        		pathToSeeds= path2seeds;
        		myPersonPool = pool;
        		attributesMainConditionals = mainCdt;
        		thisWorld = curWorld;
        		
        	}
        	
	        public ArrayList<String> call()  throws Exception {
	        	
	        	myGibbsSampler.GenerateAgentsMetroLevel(thisWorld,
	                    Utils.WARMUP_ITERATIONS,
	                    new Person(pathToSeeds, true), true,
	                    attributesMainConditionals, null);
			    myPersonPool.clear();
			    //myGibbsSampler.SetAgentCounter(agentsCreated + counter);
			    
			    
	        	String statistics = new String();// Utils.NEW_LINE_DELIMITER; //new String();
	        	String population = new String();
	        	/*if(Utils.createLocal){
	        		
		        	try{
		        		 for (Map.Entry<String, Object> entry : zones.entrySet())
		                 {
		        			    SpatialZone currZone = (SpatialZone)entry.getValue();
		        			    
		        			    OutputFileWritter outputFile = new OutputFileWritter();
			 		            //System.out.println(Utils.DATA_DIR + "data\\" + fmt(currZone.myAttributes.get(0).value) + "\\personPool.txt");
			 		            outputFile.OpenFile(Utils.DATA_DIR + "data\\" + fmt(currZone.myAttributes.get(0).value) + "\\personPool.txt");
			 		            //System.out.println(currZone.myAttributes.get(1).value);
			 		        
			 				    
			 				    //System.out.println(String.valueOf(currZone.myAttributes.get(0).value));
			 		            ArrayList<Object> answer = myGibbsSampler.GenerateAgents(null, currZone,
			 				    // actual generation
			 		                    (int)currZone.myAttributes.get(1).value,
			 		                    new Person(pathToSeeds,true), false,
			 		                    attributesMainConditionals,
			 		                    outputFile, population);
			 				    myPersonPool = (ArrayList)answer.get(0);
			 				    population += answer.get(1);
			 				    //agentsCreated += myPersonPool.size();
			 				    //System.out.println("population " + population);
			 				    
			 				    outputFile.CloseFile();
			 		            statistics = statistics+
			 		            		currZone.printLocalMarginalFittingAnalysis((int)currZone.myAttributes.get(1).value);
			 		            
			 		            //+ Utils.NEW_LINE_DELIMITER;
			 		           
			 		            //return statistics;
		                 }
		        		 
			        	
		        	}
		        	catch (IOException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    			
		    		}
		        	ArrayList<String> answer = new ArrayList<String>();
		        	answer.add(statistics);
		        	answer.add(population);
		        	return answer;
	        	}
	        	
	        	else{*/
		        	try{
		        		 for (Map.Entry<String, Object> entry : zones.entrySet())
		                 {
		        			    SpatialZone currZone = (SpatialZone)entry.getValue();
			 		            System.out.println( fmt(currZone.myAttributes.get(0).value) + "  population: " + (int)currZone.myAttributes.get(1).value);
			 		            //System.out.println(currZone.myAttributes.get(1).value);
			 		        
			 				    
			 				    //System.out.println(String.valueOf(currZone.myAttributes.get(0).value));
			 				    // actual generation
			 		            ArrayList<Object> answer = myGibbsSampler.GenerateAgents(null, currZone,
			 		                    (int)currZone.myAttributes.get(1).value,
			 		                    new Person(pathToSeeds,true), false,
			 		                    attributesMainConditionals,
			 		                    null, population);
			 		            
			 				    myPersonPool = (ArrayList)answer.get(0);
			 				    population += answer.get(1);
			 				    //agentsCreated += myPersonPool.size();
			 		            statistics = statistics+ Utils.NEW_LINE_DELIMITER+
			 		            		currZone.printLocalMarginalFittingAnalysis((int)currZone.myAttributes.get(1).value);
			 		           statistics += currZone.getTotalAbsoluteError() 
			 		        		   + currZone.getStandardizedAbsoluteError()
			 		        		   + currZone.getTargets()
			 		        		   +currZone.getResultStats();
			 		           
			 		            //return statistics;
		                 }
		        		//System.out.println(population);
			        	
		        	}
		        	catch (IOException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    			
		    		}
		        	ArrayList<String> answer = new ArrayList<String>();
		        	answer.add(statistics);
		        	answer.add(population);
		        	return answer;
	        	//}
	        	
	        }

			@Override
			public void run() {
				// TODO Auto-generated method stub
				//return statistics;
			}
	            
	     }

		public String[] CreatePersonPopulationPoolLocalLevelMultiThreadsBatch(String string, String pathToSeeds,
				int numberOfCores) throws IOException {
			// TODO Auto-generated method stub
			
			int agentsCreated = 1;
            int counter = 0;
            
          
            
            //
            // We warm the sampler once for all the spatial zones.
            /*myGibbsSampler.GenerateAgentsMetroLevel(this,
                    Utils.WARMUP_ITERATIONS,
                    new Person(pathToSeeds, true), true,
                    attributesMainConditionals, null);
		    myPersonPool.clear();
		    myGibbsSampler.SetAgentCounter(agentsCreated + counter);*/
		    
		    //creates sub group of zone that can be processed using multithreading to accelerate computation
		    int subSampleSize = myZonalCollection.size()/(numberOfCores);
		    ArrayList<Object> subSamples = new ArrayList<Object>();
		    for (int i = 0; i<numberOfCores; i++){
		    	HashMap<String, Object> currSubSample = new HashMap<String, Object>();
	    		int k = 0;
	    		int l = 0;
	    		for (Map.Entry<String, Object> entry : myZonalCollection.entrySet()){
	    			if((k>=i*subSampleSize && k < (i+1)*subSampleSize)|| (k>=i*subSampleSize && i == numberOfCores-1)){// || (k>=i*subSampleSize && i == numberOfCores-1)
	    				SpatialZone currZone = (SpatialZone)entry.getValue();
	    				currSubSample.put(ConfigFile.fmt(currZone.myAttributes.get(0).value), currZone);
	    				k++;
	    			}
	    			/*if((k>=(i+1)*subSampleSize && i == numberOfCores-1)){
	    				if(l == i){
	    					SpatialZone currZone = (SpatialZone)entry.getValue();
		    				currSubSample.put(ConfigFile.fmt(currZone.myAttributes.get(0).value), currZone);
		    				if(l==numberOfCores-1){l=0;}else{l++;}
	    				}
	    				else{
	    					if(l==numberOfCores-1){l=0;}else{l++;}
	    				}
	    			}*/
	    			/*else if(k>(i+1)*subSampleSize){
	    				break;
	    			}*/
	    			else{
	    				k++;
	    			}
	    		}
	    		System.out.println("sample " + i + ": " + currSubSample.size());
	    		subSamples.add(currSubSample);	    	
		    }
		    
		    //create and run multiple threads
		    ExecutorService cores = Executors.newFixedThreadPool(numberOfCores);
        	//Set<Future<Integer>> set = new HashSet<Future<Integer>>();
        	Set<Future<ArrayList>> set = new HashSet<Future<ArrayList>>();
        	
        	
        	for(int i = 0; i< numberOfCores; i++){
        		/*Callable<Integer> callable = new processZone(subSamples.get(i), pathToSeeds, myPersonPool, attributesMainConditionals);
            	Future<Integer> future = cores.submit(callable);*/
        		Callable<ArrayList> callable = new processZone(subSamples.get(i), pathToSeeds, myPersonPool, attributesMainConditionals, this);
            	Future<ArrayList> future = cores.submit(callable);
                set.add(future);
        	}
        	String analysis = new String();
        	String pop = new String();
        	try {
	            for (Future<ArrayList> future : set) {
	            	ArrayList<String> answer = future.get();
	            	analysis = analysis +  future.get().get(0);//Utils.NEW_LINE_DELIMITER +
	            	pop = pop + future.get().get(1);
	            }
        	} 
        	catch (InterruptedException | ExecutionException ex) {
        		ex.printStackTrace(); 
        	}
        	
        	String answer[] = new String[2];
        	answer[0] = analysis;
        	answer[1] = pop;
        	
        	cores.shutdown();
        	return answer;
        	
        }
}