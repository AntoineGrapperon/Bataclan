package SimulationObjects;

import java.io.Serializable;
import java.util.ArrayList;

import Utils.ConfigFile;
import Utils.KeyValPair;

/*
 * created by: b farooq, poly montreal
 * on: 22 october, 2013
 * last edited by: b farooq, poly montreal
 * on: 22 october, 2013
 * summary: 
 * comments:
 */

public class SimulationObject implements Serializable
{
    protected int myID;

    public int GetID()
    {
        return myID;
    }

    public void SetID(int id)
    {
        myID = id;
    }

    protected int myType;
    public int GetAgentType()
    {
        return myType;
    }

    public void SetAgentType(int curTyp)
    {
        myType = curTyp;
    }

    public String GetNewJointKey(String baseDim)
    {
        return "";
    }

    public SimulationObject CreateNewCopy(String baseDim, int baseDimVal)
    {
        return null;
    }
    
    public ArrayList <KeyValPair> myAttributes;
    
    public int GetAttributesCount()
    {
    	return myAttributes.size();
    }
   
    public String GetAttributeValuesAsString()
    {
        String currStrAgent = Integer.toString(GetID());
        for (int j = 0; j < myAttributes.size(); j++) 
        {	    
        	currStrAgent +=
        			"," + Integer.toString((int)myAttributes.get(j).value);                                                         
        }
        return currStrAgent;
    }
    public String GetAttributeValuesAsString(SpatialZone currZone)
    {
        String currStrAgent = Integer.toString(GetID()) + Utils.Utils.COLUMN_DELIMETER + ConfigFile.fmt(currZone.myAttributes.get(0).value);
        for (int j = 0; j < myAttributes.size(); j++) 
        {	    
        	currStrAgent +=
        			Utils.Utils.COLUMN_DELIMETER + Integer.toString((int)myAttributes.get(j).value);                                                         
        }
        return currStrAgent;
    }
    
    public int GetValueOfAttribute(String attName)
    {
        for (int j = 0; j < myAttributes.size(); j++)
        {    
        	if(myAttributes.get(j).category == attName)
        	{
        		return (int) myAttributes.get(j).value;
        	}
        }
        return -1;
    }
}
