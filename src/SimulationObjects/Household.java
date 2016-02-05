package SimulationObjects;

import SimulationObjects.AgentAttributes.*;
import Utils.KeyValPair;
import Utils.RandomNumberGen;
import Utils.Utils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import Utils.ConfigFile;

/*
 * created by: b farooq, poly montreal
 * on: 22 october, 2013
 * last edited by: b farooq, poly montreal
 * on: 22 october, 2013
 * summary: 
 * comments:
 */

public class Household extends SimulationObject implements Serializable
{
    private static int idCounter = 0;

    // BF: In future it should be changed to a collection of Characteristics
    //     Iterators should be defined for them
    //     Dimension/Characteristics should be in form of structures
    private String myZoneID;
    public String GetZoneID()
    {
        return myZoneID;
    }

    public void SetZoneID(String id)
    {
        myZoneID = id;
    }
    
    public Household()
    {
    	myID = ++idCounter;
        
        Random r = new Random();
        ArrayList <KeyValPair> ConfigArray = ConfigFile.AttributeDefinitions;
        myAttributes = new ArrayList <KeyValPair>(ConfigArray.size());
                    
        for(int i=0; i<ConfigArray.size(); i++)
        {       
            int randvalue = r.nextInt((int)ConfigArray.get(i).value -1);
            KeyValPair currAttribute = new KeyValPair();
            
            currAttribute.value = randvalue;
         
            currAttribute.category = ConfigArray.get(i).category;
            
            myAttributes.add(currAttribute);
        }
    }

    public Household(String currZone)
    {
    	myZoneID = currZone;
        myID = ++idCounter;
        
        Random r = new Random();
        ArrayList <KeyValPair> ConfigArray = ConfigFile.AttributeDefinitions;
        myAttributes = new ArrayList <KeyValPair>(ConfigArray.size());
                    
        for(int i=0; i<ConfigArray.size(); i++) 
        {
       
        int randvalue = r.nextInt((int)ConfigArray.get(i).value -1);
        
        KeyValPair currAttribute = new KeyValPair();

        currAttribute.value = randvalue;
        currAttribute.category = ConfigArray.get(i).category;

        myAttributes.add(currAttribute);
        }          
    }

    // Returns a string that gives the value of characteristics
    // except for base characteristics
    //
    // [BF] It has to be changed to dictionary iterator based
    //      once we have the characteritics in a collection
    @Override
    public String GetNewJointKey(String baseDim)
    {    	       
        String jointKey = null;
		try
		{
			for (int i=0; i<myAttributes.size(); i++)
			{
				
			if (!((String) myAttributes.get(i).category).equals(baseDim))
			{
				if(jointKey==null)
				{
					jointKey = Integer.toString((int) myAttributes.get(i).value);
				}
				else
				{
					jointKey += Utils.CONDITIONAL_DELIMITER + Integer.toString((int) myAttributes.get(i).value) ;
				}				    	
			}
			}
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
        return jointKey;
   }
    @Override
    public SimulationObject CreateNewCopy(String baseDim, int baseDimVal)
    {   		
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos;
        try 
        {
            oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
            oos.flush();
            oos.close();
            bos.close();
            byte [] byteData = bos.toByteArray();

            ByteArrayInputStream bais = new ByteArrayInputStream(byteData);
            try
            {
                Person myCopy = (Person)new ObjectInputStream(bais).readObject();
                        
                for (int i=0; i<myAttributes.size(); i++)
                {
                	if (baseDim.equals(myAttributes.get(i).category))
                		myCopy.myAttributes.get(i).value = baseDimVal;
                }
                return myCopy;
            }
            catch (ClassNotFoundException ex) 
            {
                Logger.getLogger(Household.class.getName()).log(Level.SEVERE, null, ex);
            }

        } 
        catch (IOException ex)
        {
            Logger.getLogger(Household.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
   /* public Household CreateNewCopy(int income)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
            oos.flush();
            oos.close();
            bos.close();
            byte [] byteData = bos.toByteArray();

            ByteArrayInputStream bais = new ByteArrayInputStream(byteData);
            try
            {
                Household myCopy = (Household) new ObjectInputStream(bais).readObject();

                myCopy.myIncome = income;
                myCopy.myIncomeLevel = IncomeConvertor.ConvertValueToLevel(income);
                CheckLogicalInconsistencies(myCopy);
                return myCopy;
            
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Household.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (IOException ex) {
            Logger.getLogger(Household.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

        private boolean CheckLogicalInconsistencies(Household currHhld)
        {
            int totPer = (int)currHhld.GetHhldSize();
            int totWrk = (int)currHhld.GetNumOfWorkers();
            int totUnv = (int)currHhld.GetNumOfUnivDegree();
            int totKid = (int)currHhld.GetNumOfKids();

            if (totPer == 0)
            {
                totPer = 1;
            }

            if (totUnv > totPer)
            {
                currHhld.SetNumOfUnivDegree(totPer);
                currHhld.SetNumOfWorkers(totPer);
                currHhld.SetNumOfKids(NumOfKids.None);
                return true;
            }
            if (totWrk > totPer)
            {
                currHhld.SetNumOfWorkers(totPer);
                currHhld.SetNumOfUnivDegree(totPer);
                currHhld.SetNumOfKids(NumOfKids.None);
                return true;
            }
            if (totKid >= totPer)
            {
                currHhld.SetNumOfWorkers(totPer);
                currHhld.SetNumOfUnivDegree(totPer);
                currHhld.SetNumOfKids(NumOfKids.None);
                return true;
            }

            // number of univ degrees
            if (totKid > 0)
            {
                if ((totWrk < totUnv)
                    && (totPer < (totWrk - totUnv)+totKid+totWrk))
                {
                    currHhld.SetNumOfUnivDegree(totWrk);
                }
            }

            // number of kids
            if (totWrk == 0)
            {
                currHhld.SetNumOfKids(NumOfKids.None);
            }

            if ((totPer - totWrk == 0 && totKid > 0 )
               || (totPer - totUnv == 0 && totKid > 0))
            {
                currHhld.SetNumOfKids(NumOfKids.None);
            } else if (totKid > totPer - totWrk)
            {
                currHhld.SetNumOfKids(NumOfKids.None);
            }
            return true;
        }
        public static class IncomeConvertor
    {
        private static RandomNumberGen randGen = new RandomNumberGen();
        public static int ConvertLevelToValue(int currHhldIncLvl)
        {
            int income = 0;
            if (currHhldIncLvl == IncomeLevel.ThirtyOrLess)
            {
                if (randGen.NextDouble() > 0.3)
                {
                    income = (int)Math.round(
                        randGen.NextDoubleInRange(10000, 30000));
                }
                else
                {
                    income = (int)Math.round(
                        randGen.NextDoubleInRange(1000, 10000));
                }
            }
            else if (currHhldIncLvl == IncomeLevel.ThirtyToSevetyFive)
            {
                income = (int) Math.round(randGen.NextDoubleInRange(30000, 75000));
            }
            else if (currHhldIncLvl == IncomeLevel.SeventyFiveToOneTwentyFive)
            {
                income = (int)Math.round(randGen.NextDoubleInRange(75000, 125000));
            }
            else if (currHhldIncLvl == IncomeLevel.OneTwentyFiveToTwoHundred)
            {
                income = (int)Math.round(randGen.NextDoubleInRange(125000, 200000));
            }
            else
            {
                if (randGen.NextDouble() > 0.3)
                {
                    income = (int)Math.round(randGen.NextDoubleInRange(200000, 2000000));
                }
                else
                {
                    income = (int)Math.round(randGen.NextDoubleInRange(2000000, 10000000));
                }
            }
            return income;
        }

        public static int ConvertValueToLevel(int currHhldInc)
        {
            if (currHhldInc <= 30000)
            {
                return IncomeLevel.ThirtyOrLess;
            }
            else if (currHhldInc > 30000 && currHhldInc <= 75000)
            {
                return IncomeLevel.ThirtyToSevetyFive;
            }
            else if (currHhldInc > 75000 && currHhldInc <= 125000)
            {
                return IncomeLevel.SeventyFiveToOneTwentyFive;
            }
            else if (currHhldInc > 125000 && currHhldInc <= 200000)
            {
                return IncomeLevel.OneTwentyFiveToTwoHundred;
            }
            else
            {
                return IncomeLevel.TwohundredOrMore;
            }
        }
        public static int GetEuroIncome(int currHhhldInc)
        {
            return (int) Math.round(currHhhldInc / Utils.BFRANC_TO_EURO);
        }

        public static int GetEuroIncomeCont(int currHhldIncLvl)
        {
            int income = 0;
            if (currHhldIncLvl == IncomeLevel.ThirtyOrLess)
            {
                income = (int)Math.round(randGen.NextDoubleInRange(20000, 30000) /
                    Utils.BFRANC_TO_EURO);
            }
            else if (currHhldIncLvl == IncomeLevel.ThirtyToSevetyFive)
            {
                income = (int)Math.round(randGen.NextDoubleInRange(30000, 75000) /
                    Utils.BFRANC_TO_EURO);
            }
            else if (currHhldIncLvl == IncomeLevel.SeventyFiveToOneTwentyFive)
            {
                income = (int)Math.round(randGen.NextDoubleInRange(75000, 125000) /
                    Utils.BFRANC_TO_EURO);
            }
            else if (currHhldIncLvl == IncomeLevel.OneTwentyFiveToTwoHundred)
            {
                income = (int)Math.round(randGen.NextDoubleInRange(125000, 200000) /
                    Utils.BFRANC_TO_EURO);
            }
            else
            {
                income = (int)Math.round(randGen.NextDoubleInRange(200000, 1000000) /
                    Utils.BFRANC_TO_EURO);
            }
            return income;
        }
    }*/
}
