
package Samplers;

import SimulationObjects.*;
import SimulationObjects.AgentAttributes.*;
import SimulationObjects.Household.*;
/*
 * created by: b farooq, poly montreal
 * on: 22 october, 2013
 * last edited by: b farooq, poly montreal
 * on: 22 october, 2013
 * summary: 
 * comments:
 */
import Utils.*;

import java.io.IOException;
import java.util.ArrayList;

public class GibbsSampler extends Sampler
{
        int agentIDCounter;
        public void SetAgentCounter(int cntr)
        {
            agentIDCounter = cntr;
        }
        public int GetAgentCounter()
        {
            return agentIDCounter;
        }

        private ImportanceSampler myImportantSampler;
        private MetropolisHasting myMHSampler;

        public GibbsSampler()
        {
            warmupTime = 0;
            samplingInterval = 0;
            agentIDCounter = 0;
            myImportantSampler = new ImportanceSampler();
            myMHSampler = new MetropolisHasting();
            Initialze();
        }

        public GibbsSampler(int warmup, int samplingIntv)
        {
            warmupTime = warmup;
            samplingInterval = samplingIntv;
            agentIDCounter = 0;
            Initialze();
        }

        public GibbsSampler(int warmup, int samplingIntv, int randSeed)
        {
            warmupTime = warmup;
            samplingInterval = samplingIntv;
            agentIDCounter = 0;
            Initialze(randSeed);
        }

        // the method assumes that the conditionals are full conditionals
        // The data processing has already been done
        // [BF] The method should be changed so that it can generate any
        //      kind of agents
        public ArrayList GenerateAgents(World world, SpatialZone currZone, int numAgents,
        		SimulationObject initAgent, boolean warmUpStatus,
                        ArrayList worldConditionals,
                        OutputFileWritter currWriter) throws IOException
        {
            /*if (initAgent.GetAgentType() == AgentType.Household)
            {
            	System.out.println("--type was household");
                return GenerateHousholds(currZone, numAgents,
                                    (Household) initAgent, warmUpStatus,
                                        mobelCond, currWriter);
            }*/
                       
            if (ConfigFile.GetAgentTyp.equals("Person"))
            {
            	return GeneratePersons(world, currZone, numAgents,
                                    (Person) initAgent, warmUpStatus,
                                        currWriter, worldConditionals);
            }
            else{
            	System.out.println("--pool not created");
            }
            return null;
        }
        
        public ArrayList GenerateAgents(World world, SpatialZone currZone, int numAgents,
        		SimulationObject initAgent, boolean warmUpStatus,
                        ArrayList worldConditionals,
                        OutputFileWritter currWriter, String population) throws IOException
        {
        	String testPopulation = new String();
            /*if (initAgent.GetAgentType() == AgentType.Household)
            {
            	System.out.println("--type was household");
                return GenerateHousholds(currZone, numAgents,
                                    (Household) initAgent, warmUpStatus,
                                        mobelCond, currWriter);
            }*/
                       
            if (ConfigFile.GetAgentTyp.equals("Person"))
            {
            	return GeneratePersons(world, currZone, numAgents,
                                    (Person) initAgent, warmUpStatus,
                                        currWriter, worldConditionals, testPopulation);
            }
            else{
            	System.out.println("--pool not created");
            }
            return null;
        }
        
        public ArrayList GenerateAgentsMetroLevel(World world, int numAgents,
        		SimulationObject initAgent, boolean warmUpStatus,
                        ArrayList worldConditionals,
                        OutputFileWritter currWriter) throws IOException
        {
            /*if (initAgent.GetAgentType() == AgentType.Household)
            {
            	System.out.println("--type was household");
                return GenerateHousholds(currZone, numAgents,
                                    (Household) initAgent, warmUpStatus,
                                        mobelCond, currWriter);
            }*/
                       
            if (ConfigFile.GetAgentTyp.equals("Person"))
            {
            	return GeneratePersonsMetroLevel(world, numAgents,
                                    (Person) initAgent, warmUpStatus,
                                        currWriter, worldConditionals);
            }
            else{
            	System.out.println("--pool not created");
            }
            return null;
        }
        
        

 /*       private ArrayList GenerateHousholds(SpatialZone currZone, int numHousehold,
                        Household initAgent, boolean warmUpStatus,
                        ArrayList mobelCond,
                        OutputFileWritter currWriter) throws IOException
        {
            int seltdDim = 0;
            ArrayList <ConditionalDistribution> condList = currZone.GetDataHhldCollectionsList();
            condList.add(mobelCond.get(0));
            condList.add(mobelCond.get(1));
            condList.add(mobelCond.get(2));
            ArrayList generatedAgents = new ArrayList();
            Household prevAgent = initAgent;

            ImportanceSampler currImpSampler = new ImportanceSampler();
            MetropolisHasting currMHSampler = new MetropolisHasting();
            int iter = 0;
            if (warmUpStatus == true)
            {
                iter = Utils.WARMUP_ITERATIONS;
            }
            else
            {
                iter = Utils.SKIP_ITERATIONS * numHousehold;
            }
            Household newAgent = new Household();
            for (int i = 0; i < iter; i++)
            {
                seltdDim = randGen.NextInRange(0, condList.size() - 1);

                ConditionalDistribution currDist =
                    (ConditionalDistribution)condList.get(seltdDim);

                // If the selected distribution is dwelling/cars
                // call important sampling

                /*if (currDist.GetDimensionName() == "DwellingType")
                {
                    newAgent = currImpSampler.GetNextAgent(currZone.myDwellMarginal,
                        currDist, currDist.GetDimensionName(),
                        prevAgent, currZone);
                }
                else if (currDist.GetDimensionName() == "NumOfCars")
                {
                    newAgent = currImpSampler.GetNextAgent(currZone.myCarsMarginal,
                        currDist, currDist.GetDimensionName(),
                        prevAgent, currZone);
                }*/

                // If the selected distribution is income
                // call MH
                //                else if (((ConditionalDistribution)condList[seltdDim])
                //                                .GetDimensionName() == "IncomeLevel")
                //                {
                //                    newAgent = myMHSampler.GetNextAgent((ModelDistribution)currDist,
                //                            currDist.GetDimensionName(), prevAgent, currZone);
                //                }
                /*if (currDist.GetDimensionName().equals("HouseholdSize"))
                {
                    newAgent = (Household)currImpSampler.GetNextAgent(
                        currZone.GetHousholdSizeDist(),
                        currDist, currDist.GetDimensionName(),
                        prevAgent, currZone);
                }
                else
                {
                  * ArrayList currComm = currDist.GetCommulativeValue(
                        prevAgent.GetNewJointKey(currDist.GetDimensionName())
                        , currZone);
                    newAgent = (Household)GenerateNextAgent(currComm, prevAgent,
                        currDist.GetDimensionName()); 
                }

               /* prevAgent = newAgent;
                if (warmUpStatus == false && (i % Utils.SKIP_ITERATIONS == 0))
                {
                    newAgent.SetID(agentIDCounter);
                    agentIDCounter++;
                    generatedAgents.add(newAgent);
                    int currIncome = IncomeConvertor.GetEuroIncome((int)
                                        newAgent.GetIncome());

                    String currStrAgent = newAgent.GetID()
                        + "," + newAgent.GetZoneID()
                        + "," + currZone.GetEPFLName()
                        + "," + String.valueOf((int)newAgent.GetHhldSize())
                        + "," + String.valueOf((int)newAgent.GetNumOfWorkers())
                        + "," + String.valueOf((int)newAgent.GetNumOfKids())
                        + "," + String.valueOf((int)newAgent.GetNumOfUnivDegree())
                        + "," + String.valueOf((int)newAgent.GetIncomeLevel())
                        + "," + String.valueOf((int)newAgent.GetNumOfCars())
                        + "," + String.valueOf((int)newAgent.GetDwellingType());
                    currWriter.WriteToFile(currStrAgent); /*
                    //Console.WriteLine(currStrAgent);
              /*  }
            }
            return generatedAgents;
        } */

        private ArrayList GeneratePersons(SpatialZone currZone, int numPerson,
                            Person initAgent, boolean warmUpStatus,
                            OutputFileWritter currWriter) throws IOException
        {
            int seltdDim = 0;
            ArrayList<ConditionalDistribution> condList = currZone.GetPersonDataCollectionsList();
            ArrayList generatedAgents = new ArrayList();
            Person prevAgent = initAgent;
            ImportanceSampler currImpSampler = new ImportanceSampler();

            int iter = 0;
            if (warmUpStatus == true)
            {
                iter = Utils.WARMUP_ITERATIONS;
            }
            else
            {
                iter = Utils.SKIP_ITERATIONS * numPerson;
            }
            Person newAgent = new Person();
            for (int i = 0; i < iter; i++)
            {
                seltdDim = randGen.NextInRange(0, condList.size() - 1);

                ConditionalDistribution currDist =
                    (ConditionalDistribution)condList.get(seltdDim);

                /*if (currDist.GetDimensionName() == "HouseholdSize2")
                {
                    newAgent = (Person) currImpSampler.GetNextAgent(
                                currZone.myHhldSize2Marginal,
                                currDist, currDist.GetDimensionName(),
                                (SimulationObject) prevAgent, currZone);
                }
                else if (currDist.GetDimensionName() == "Age")
                {
                    newAgent = (Person)currImpSampler.GetNextAgent(
                                currZone.myAgeMarginal,
                                currDist, currDist.GetDimensionName(),
                                (SimulationObject)prevAgent, currZone);
                }
                else if (currDist.GetDimensionName().equals("Sex"))
                {
                    newAgent = (Person)currImpSampler.GetNextAgent(
                                currZone.mySexMarginal,
                                currDist, currDist.GetDimensionName(),
                                (SimulationObject)prevAgent, currZone);
                }
                else if (currDist.GetDimensionName() == "EducationLevel")
                {
                    newAgent = (Person)currImpSampler.GetNextAgent(
                                currZone.myEducationMarginal,
                                currDist, currDist.GetDimensionName(),
                                (SimulationObject)prevAgent, currZone);
                }
                else
                {*/
                ArrayList currComm = currDist.GetCommulativeValue(   // it seems that Conditional distribution does not contains any defined "GetComulativeValue" : return null
                         prevAgent.GetNewJointKey(currDist.GetDimensionName())
                            ,currZone);
                    newAgent = (Person)GenerateNextAgent(currComm,
                            (SimulationObject)prevAgent,
                            currDist.GetDimensionName());
               // }

                prevAgent = newAgent;
                if (warmUpStatus == false && (i % Utils.SKIP_ITERATIONS == 0))
                {
                    newAgent.SetID(agentIDCounter);
                    agentIDCounter++;
                    generatedAgents.add(newAgent);
                    
                    currWriter.WriteToFile(newAgent.GetAttributeValuesAsString());
                    
                    //Console.WriteLine(currStrAgent);
                    }
                }
            
            return generatedAgents;
        }
        
        private ArrayList GeneratePersons(World world, SpatialZone currZone, int numPerson,
                Person initAgent, boolean warmUpStatus,
                OutputFileWritter currWriter, ArrayList<DiscreteCondDistribution> mainAttributesCollection) throws IOException
		{
        	
        	int index;
        	int seltdDim = 0;
			ArrayList<ConditionalDistribution> condList = currZone.GetZonalCondCollections();
			ArrayList generatedAgents = new ArrayList();
			Person prevAgent = initAgent;
			ImportanceSampler currImpSampler = new ImportanceSampler();
			
			int iter = 0;
			if (warmUpStatus == true)
			{
			    iter = Utils.WARMUP_ITERATIONS;
			}
			else
			{
			    iter = Utils.SKIP_ITERATIONS * numPerson;
			}
			Person newAgent = new Person();
			for (int i = 0; i < iter; i++)
			{
			    seltdDim = randGen.NextInRange(0, mainAttributesCollection.size() - 1);
			
			    ConditionalDistribution currDist =
			        (ConditionalDistribution)mainAttributesCollection.get(seltdDim);
			    
			    //System.out.println(currDist.GetDimensionName());
			    			
			    if (currDist.GetDimensionName().equals("age"))
			    {
			    	index = ConfigFile.AttributesImportanceNames.indexOf("age");			    
			        newAgent = (Person) currImpSampler.GetNextAgent(
			                    currZone.myAttributesDiscConditional.get(index),
			                    currDist, currDist.GetDimensionName(),
			                    prevAgent, currZone);
			    }
			    else if (currDist.GetDimensionName() == "sex")
			    {
			    	index = ConfigFile.AttributesImportanceNames.indexOf("sex");			    
			        newAgent = (Person) currImpSampler.GetNextAgent(
			                    currZone.myAttributesDiscConditional.get(index),
			                    currDist, currDist.GetDimensionName(),
			                    prevAgent, currZone);
			    }
			    /*else if (currDist.GetDimensionName().equals("mStat"))
			    {
			    	index = ConfigFile.AttributesImportanceNames.indexOf("mStat");			    
			        newAgent = (Person) currImpSampler.GetNextAgent(
			                    currZone.myAttributesDiscConditional.get(index),
			                    currDist, currDist.GetDimensionName(),
			                    prevAgent, currZone);
			    }
			    else if (currDist.GetDimensionName() == "nPers")
			    {
			    	index = ConfigFile.AttributesImportanceNames.indexOf("nPers");			    
			        newAgent = (Person) currImpSampler.GetNextAgent(
			                    currZone.myAttributesDiscConditional.get(index),
			                    currDist, currDist.GetDimensionName(),
			                    prevAgent, currZone);
			    } */// we are letting mStat and nPers as long as I did not figure out the problem related to the sum of marginals that do not match the whole population
			    else
			    {
				    ArrayList currComm = currDist.GetCommulativeValue(   // it seems that Conditional distribution does not contains any defined "GetComulativeValue" : return null
				             prevAgent.GetNewJointKey(currDist.GetDimensionName())
				                ,currZone);
				        newAgent = (Person)GenerateNextAgent(currComm,
				                (SimulationObject)prevAgent,
				                currDist.GetDimensionName());  
			    }
			    if(!(newAgent == null)){
			    	prevAgent = newAgent;
				    if (warmUpStatus == false && (i % Utils.SKIP_ITERATIONS == 0))
				    {
				        newAgent.SetID(agentIDCounter);
				        agentIDCounter++;
				        generatedAgents.add(newAgent);
				        
				        currWriter.WriteToFile(newAgent.GetAttributeValuesAsString(currZone));
				        
				        ConfigFile.updateCounters(newAgent, currZone.marginalCounters);
				        //ConfigFile.updateCounters(newAgent, world.marginalCounters);
				        //Console.WriteLine(currStrAgent);
				     }
		        }
			    else{
			    	i = i-1;
			    }
			}
			
			
			
			return generatedAgents;
		}
        
        private ArrayList GeneratePersons(World world, SpatialZone currZone, int numPerson,
                Person initAgent, boolean warmUpStatus,
                OutputFileWritter currWriter, ArrayList<DiscreteCondDistribution> mainAttributesCollection, String population) throws IOException
		{
        	
        	int index;
        	int seltdDim = 0;
			ArrayList<ConditionalDistribution> condList = currZone.GetZonalCondCollections();
			ArrayList generatedAgents = new ArrayList();
			Person prevAgent = initAgent;
			ImportanceSampler currImpSampler = new ImportanceSampler();
			
			int iter = 0;
			if (warmUpStatus == true)
			{
			    iter = Utils.WARMUP_ITERATIONS;
			}
			else
			{
			    iter = Utils.SKIP_ITERATIONS * numPerson;
			}
			Person newAgent = new Person();
			for (int i = 0; i < iter; i++)
			{
			    seltdDim = randGen.NextInRange(0, mainAttributesCollection.size() - 1);
			
			    ConditionalDistribution currDist =
			        (ConditionalDistribution)mainAttributesCollection.get(seltdDim);
			    
			    //System.out.println(currDist.GetDimensionName());
			    
			    //Importance sampling
			    /*if (currDist.GetDimensionName().equals("age"))
			    {
			    	index = ConfigFile.AttributesImportanceNames.indexOf("age");			    
			        newAgent = (Person) currImpSampler.GetNextAgent(
			                    currZone.myAttributesDiscConditional.get(index),
			                    currDist, currDist.GetDimensionName(),
			                    prevAgent, currZone);
			    }
			    else if (currDist.GetDimensionName() == "sex")
			    {
			    	index = ConfigFile.AttributesImportanceNames.indexOf("sex");			    
			        newAgent = (Person) currImpSampler.GetNextAgent(
			                    currZone.myAttributesDiscConditional.get(index),
			                    currDist, currDist.GetDimensionName(),
			                    prevAgent, currZone);
			    }*/
			    
			    //Gibbs sampling with local distributions
			    if (currDist.GetDimensionName().equals("age"))
			    {
			    	
			    	index = ConfigFile.AttributesImportanceNames.indexOf("age");			    
			        currDist= currZone.myAttributesDiscConditional.get(index);
			        
			        ArrayList currComm = currDist.GetCommulativeValue(   // it seems that Conditional distribution does not contains any defined "GetComulativeValue" : return null
				             prevAgent.GetNewJointKey(currDist.GetDimensionName())
				                ,currZone);
				        newAgent = (Person)GenerateNextAgent(currComm,
				                (SimulationObject)prevAgent,
				                currDist.GetDimensionName()); 
				        
				        
			    }
			    else if (currDist.GetDimensionName() == "sex")
			    {
			    	index = ConfigFile.AttributesImportanceNames.indexOf("sex");			    
			        currDist= currZone.myAttributesDiscConditional.get(index);
			        
			        ArrayList currComm = currDist.GetCommulativeValue(   // it seems that Conditional distribution does not contains any defined "GetComulativeValue" : return null
				             prevAgent.GetNewJointKey(currDist.GetDimensionName())
				                ,currZone);
				        newAgent = (Person)GenerateNextAgent(currComm,
				                (SimulationObject)prevAgent,
				                currDist.GetDimensionName());
			    }
			    
			    
			    /*else if (currDist.GetDimensionName().equals("mStat"))
			    {
			    	index = ConfigFile.AttributesImportanceNames.indexOf("mStat");			    
			        newAgent = (Person) currImpSampler.GetNextAgent(
			                    currZone.myAttributesDiscConditional.get(index),
			                    currDist, currDist.GetDimensionName(),
			                    prevAgent, currZone);
			    }
			    else if (currDist.GetDimensionName() == "nPers")
			    {
			    	index = ConfigFile.AttributesImportanceNames.indexOf("nPers");			    
			        newAgent = (Person) currImpSampler.GetNextAgent(
			                    currZone.myAttributesDiscConditional.get(index),
			                    currDist, currDist.GetDimensionName(),
			                    prevAgent, currZone);
			    } */// we are letting mStat and nPers as long as I did not figure out the problem related to the sum of marginals that do not match the whole population
			    else
			    {
				    ArrayList currComm = currDist.GetCommulativeValue(   // it seems that Conditional distribution does not contains any defined "GetComulativeValue" : return null
				             prevAgent.GetNewJointKey(currDist.GetDimensionName())
				                ,currZone);
				        newAgent = (Person)GenerateNextAgent(currComm,
				                (SimulationObject)prevAgent,
				                currDist.GetDimensionName());  
			    }
			    if(!(newAgent == null)){
			    	prevAgent = newAgent;
				    if (warmUpStatus == false && (i % Utils.SKIP_ITERATIONS == 0))
				    {
				        newAgent.SetID(agentIDCounter);
				        agentIDCounter++;
				        generatedAgents.add(newAgent);
				        
				        try{
				        	currWriter.WriteToFile(newAgent.GetAttributeValuesAsString(currZone));
				        }
				        catch(NullPointerException e){
				        	
				        }
				        
				        population += Utils.NEW_LINE_DELIMITER + newAgent.GetAttributeValuesAsString(currZone);
				        
				        ConfigFile.updateCounters(newAgent, currZone.marginalCounters);
				        //ConfigFile.updateCounters(newAgent, world.marginalCounters);
				        //Console.WriteLine(currStrAgent);
				     }
		        }
			    else{
			    	i = i-1;
			    }
			}
			
			//System.out.println(population);
			ArrayList<Object> answer = new ArrayList<Object>();
			answer.add(generatedAgents);
			answer.add(population);
			return answer;
		}
        
        private ArrayList GeneratePersonsMetroLevel(World world, int numPerson,
                Person initAgent, boolean warmUpStatus,
                OutputFileWritter currWriter, ArrayList<DiscreteCondDistribution> mainAttributesCollection) throws IOException
		{
        	
        	int index;
        	int seltdDim = 0;
			ArrayList<ConditionalDistribution> condList = world.GetZonalCondCollections();
			ArrayList generatedAgents = new ArrayList();
			Person prevAgent = initAgent;
			ImportanceSampler currImpSampler = new ImportanceSampler();
			
			int iter = 0;
			if (warmUpStatus == true)
			{
			    iter = Utils.WARMUP_ITERATIONS;
			}
			else
			{
			    iter = Utils.SKIP_ITERATIONS * numPerson;
			}
			Person newAgent = new Person();
			for (int i = 0; i < iter; i++)
			{
			    seltdDim = randGen.NextInRange(0, mainAttributesCollection.size() - 1);
			
			    ConditionalDistribution currDist =
			        (ConditionalDistribution)mainAttributesCollection.get(seltdDim);
			    
			    //System.out.println(currDist.GetDimensionName());
			    			
			    /*if (currDist.GetDimensionName().equals("age"))
			    {
			    	index = ConfigFile.AttributesImportanceNames.indexOf("age");			    
			        newAgent = (Person) currImpSampler.GetNextAgent(
			                    currZone.myAttributesDiscConditional.get(index),
			                    currDist, currDist.GetDimensionName(),
			                    prevAgent, currZone);
			    }
			    else if (currDist.GetDimensionName() == "sex")
			    {
			    	index = ConfigFile.AttributesImportanceNames.indexOf("sex");			    
			        newAgent = (Person) currImpSampler.GetNextAgent(
			                    currZone.myAttributesDiscConditional.get(index),
			                    currDist, currDist.GetDimensionName(),
			                    prevAgent, currZone);
			    }
			    else if (currDist.GetDimensionName().equals("mStat"))
			    {
			    	index = ConfigFile.AttributesImportanceNames.indexOf("mStat");			    
			        newAgent = (Person) currImpSampler.GetNextAgent(
			                    currZone.myAttributesDiscConditional.get(index),
			                    currDist, currDist.GetDimensionName(),
			                    prevAgent, currZone);
			    }
			    else if (currDist.GetDimensionName() == "nPers")
			    {
			    	index = ConfigFile.AttributesImportanceNames.indexOf("nPers");			    
			        newAgent = (Person) currImpSampler.GetNextAgent(
			                    currZone.myAttributesDiscConditional.get(index),
			                    currDist, currDist.GetDimensionName(),
			                    prevAgent, currZone);
			    }
			    else
			    {*/
				    ArrayList currComm = currDist.GetCommulativeValue(   // it seems that Conditional distribution does not contains any defined "GetComulativeValue" : return null
				             prevAgent.GetNewJointKey(currDist.GetDimensionName())
				                ,null);
				        newAgent = (Person)GenerateNextAgent(currComm,
				                (SimulationObject)prevAgent,
				                currDist.GetDimensionName());  
			    //}
			    if(!(newAgent == null)){
			    	prevAgent = newAgent;
				    /*if (warmUpStatus == false && (i % Utils.SKIP_ITERATIONS == 0))
				    {
				        newAgent.SetID(agentIDCounter);
				        agentIDCounter++;
				        generatedAgents.add(newAgent);
				        
				        currWriter.WriteToFile(newAgent.GetAttributeValuesAsString());
				        
				        ConfigFile.updateCounters(newAgent, world.marginalCounters);
				        //world.updateCounters(newAgent);
				        //Console.WriteLine(currStrAgent);
				     }*/
		        }
			    else{
			    	i = i-1;
			    }
			}
			
			
			
			return generatedAgents;
		}

        // Should generate a deep copy of self
        private SimulationObject GenerateNextAgent(ArrayList curCom,
            SimulationObject prvAgnt, String genDim)
        {
            double currMax = (double)
                ((KeyValPair)curCom.get(curCom.size() - 1)).value;
            if (currMax != 0.00)
            {
                double randVal = randGen.NextDoubleInRange(0, currMax);
                for (int i = 0; i < curCom.size(); i++)
                {
                    if (randVal <= ((KeyValPair)curCom.get(i)).value)
                    {
                    	//System.out.println("not fake");
                        return prvAgnt.CreateNewCopy(genDim, i); // it returns a lot of new agent ???
                    }
                }
            }
            else
            {
            	//System.out.println("fake");
                return prvAgnt.CreateNewCopy(genDim,
                    randGen.NextInRange(0, (curCom.size() - 1)));
            	//return null;
            }
            return null;
        }
}
