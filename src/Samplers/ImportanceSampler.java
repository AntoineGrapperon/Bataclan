package Samplers;

import SimulationObjects.*;
import SimulationObjects.AgentAttributes.*;
import Utils.*;
import java.util.ArrayList;

/*
 * created by: b farooq, poly montreal
 * on: 22 october, 2013
 * last edited by: b farooq, poly montreal
 * on: 22 october, 2013
 * summary: 
 * comments:
 */
public class ImportanceSampler extends Sampler
{
        RandomNumberGen myRand;
        public ImportanceSampler()
        {
            myRand = new RandomNumberGen();
        }

        public SimulationObject GetNextAgent(DiscreteMarginalDistribution f_x,
            ConditionalDistribution g_x, String dimension,
            SimulationObject prvAgent, SpatialZone currZone)
        {
            if (prvAgent.GetAgentType() == AgentType.Household)
            {
                return (SimulationObject) GetNextAgentHousehold(
                    f_x, g_x, dimension, (Household) prvAgent, currZone);
            }
            else if (prvAgent.GetAgentType() == AgentType.Person)
            {
                return (SimulationObject) GetNextAgentPerson(
                    f_x, g_x, dimension, (Person)prvAgent, currZone);
            }
            return null;
        }
        
        public SimulationObject GetNextAgent(DiscreteCondDistribution f_x,
                ConditionalDistribution g_x, String dimension,
                SimulationObject prvAgent, SpatialZone currZone)
            {
                /*if (prvAgent.GetAgentType() == AgentType.Household)
                {
                    /*return (SimulationObject) GetNextAgentHousehold(
                        f_x, g_x, dimension, (Household) prvAgent, currZone);
                	return null;
                }
                else */if (ConfigFile.GetAgentTyp.equals("Person"))
                {
                    return (SimulationObject) GetNextAgentPerson(
                        f_x, g_x, dimension, (Person)prvAgent, currZone);
                }
                return null;
            }

        public Household GetNextAgentHousehold(DiscreteMarginalDistribution f_x,
            ConditionalDistribution g_x, String dimension,
            Household prvAgent, SpatialZone currZone)
        {
            KeyValPair currDimVal;
            double currProb = 0.00;
            double currRatio = 0.00;
            int cnt = 0;
            do{
                currDimVal = GenerateNextFromG_X(g_x, prvAgent,currZone);
                currProb = f_x.GetValue(currDimVal.category);
                currRatio = currProb / currDimVal.value;
                if (currRatio > 1.00)
                {
                    currRatio = 1.00;
                }
                if (cnt > 1000)
                {
                    currRatio = 1;
                }
                cnt++;
            } while (myRand.NextDouble() > currRatio);

            // Create the household object based on the currDimVal.category
            return (Household) prvAgent.CreateNewCopy(g_x.GetDimensionName(), Integer.parseInt(currDimVal.category));
        }

        public Person GetNextAgentPerson(DiscreteMarginalDistribution f_x,
            ConditionalDistribution g_x, String dimension,
                Person prvAgent, SpatialZone currZone)
        {
            KeyValPair currDimVal;
            double currProb = 0.00;
            double currRatio = 0.00;
            int cnt = 0;
            do
            {
                currDimVal = GenerateNextFromG_X(g_x, prvAgent, currZone);
                currProb = f_x.GetValue(currDimVal.category);
                currRatio = currProb / currDimVal.value;
                if (currRatio > 1.00)
                {
                    currRatio = 1.00;
                }
                if (cnt > 1000)
                {
                    currRatio = 1;
                }
                cnt++;
            } while (myRand.NextDouble() > currRatio);

            // Create the household object based on the currDimVal.category
            return (Person)prvAgent.CreateNewCopy(
                g_x.GetDimensionName(), Integer.parseInt(currDimVal.category));
        }
        
        public Person GetNextAgentPerson(DiscreteCondDistribution f_x,
                ConditionalDistribution g_x, String dimension,
                    Person prvAgent, SpatialZone currZone)
            {
                KeyValPair currDimVal;
                double currProb = 0.00;
                double currRatio = 0.00;
                int cnt = 0;
                
                //System.out.println(currDimVal.category);
                do
                {
                    currDimVal = GenerateNextFromG_X(g_x, prvAgent, currZone);
                    currProb = f_x.GetValue(f_x.GetDimensionName(),
                            currDimVal.category,
                            prvAgent.GetNewJointKey(f_x.GetDimensionName()),
                            currZone);
                    currRatio = currProb / currDimVal.value;
                    if (currRatio > 1.00)
                    {
                        currRatio = 1.00;
                    }
                    if (cnt > 1000)
                    {
                        currRatio = 1;
                    }
                    cnt++;
                } while (myRand.NextDouble() > currRatio);

                // Create the household object based on the currDimVal.category
                return (Person)prvAgent.CreateNewCopy(
                    g_x.GetDimensionName(), Integer.parseInt(currDimVal.category));
                    //return null;
            }

        private KeyValPair GenerateNextFromG_X(ConditionalDistribution curG_X,
                                SimulationObject prvAgent, SpatialZone currZone)
        {
        	//System.out.println(prvAgent.GetNewJointKey(curG_X.GetDimensionName()));
            ArrayList curCom = curG_X.GetCommulativeValue(
                prvAgent.GetNewJointKey(curG_X.GetDimensionName())
                    ,currZone);

            double randVal = myRand.NextDoubleInRange(0, (double)
                ((KeyValPair)curCom.get(curCom.size() - 1)).value);
            for (int i = 0; i < curCom.size(); i++)
            {
                if (randVal <= ((KeyValPair)curCom.get(i)).value)
                {
                    KeyValPair myPair = new KeyValPair();
                    myPair.category = ((KeyValPair)curCom.get(i)).category;
                    myPair.value = curG_X.GetValue(curG_X.GetDimensionName(),
                            myPair.category,
                            prvAgent.GetNewJointKey(curG_X.GetDimensionName()),
                            currZone);
                    //System.out.println(myPair.category + myPair.value);
                    return myPair;
                }
            }
            return new KeyValPair();
        }
}
