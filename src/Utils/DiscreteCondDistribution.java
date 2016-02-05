/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Utils;

//*******************************************//

import SimulationObjects.SpatialZone;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

    //*******************************************//
    // [BF] Change the name of the ckass, so that it represents
    //      that it deals with the discrete distributions
    //*******************************************//
    //*******************************************//

    // A DatasetCollection keep all the conditionals or counts related to
    // one dimension of the data
    // We assue that keys are ordered in following order
    // A | B, C, D, E, F
    // B | A, C, D, E, F
    // C | A, B, D, E, F
    // E | A, B, C, D, F
    // F | A, B, C, D, E
    // Each DatasetCollection will have dataset for A |... , B|..., and so on
    // Each category witin DatasetCollection will have a seperate hashtable
public class DiscreteCondDistribution extends ConditionalDistribution
{
    // Hashtable represents each category of the dimension
    // Each dimension will have hashtable for values from other dimension
    private HashMap<String, Object> myCollection;

    public DiscreteCondDistribution()
    {    
        myCollection = new HashMap<String, Object>();
        missingDimStatus = new ArrayList();
        SetDistributionType(true);
    }

    public int GetCategoryCount()
    {
        return myCollection.size();
    }

    public void FlushOutData()
    {
        for (Map.Entry<String, Object> currEnt : myCollection.entrySet())
        {
            ((HashMap)currEnt.getValue()).clear();
        }
        myCollection.clear();
    }

    // For A=a|B=b,C=c,D=d
    // category = a
    // key = b,c,d
    // val = P(a|b,c,d) or Count(a|b,c,d)
    public boolean AddValue(String category, String key, double val)
    {
    	int errorFlag = 0;
        if (category != null)
        {
        	if(key.contains("--")){
        		System.out.println("--wrong key format: contains -- ");
        	}
            if ( myCollection.isEmpty())
            {
                String[] currKeyToken = key.split(Utils.CONDITIONAL_DELIMITER);

                for (String currStr: currKeyToken)
                {    
                    if( currStr.equals(Utils.CONDITIONAL_GENERIC))
                    {
                        missingDimStatus.add(0);
                    }
                    else if (currStr.equals("")|| currStr.equals(" ")){
                    	errorFlag++;
                    }
                    else
                    {
                        missingDimStatus.add(1);
                    }    
                }
            }
            if (!myCollection.containsKey(category))
            {    
                myCollection.put(category, new HashMap<String, Object>());
            }
            else{
            	//System.out.println("--the process tried to fill in the same category twice");
            }
            if ((key != null) && (val >= 0.00) && (errorFlag==0))
            {
                HashMap<String, Object> currCatData = (HashMap)myCollection.get(category);
                currCatData.put(key, val);
                return true;
            }
            else if (errorFlag != 1){
            	System.out.println("--the key had " + Integer.toString(errorFlag) + " empty attributes" );
            }
            else{
            	System.out.println("-- the key or the count value was null");
            }
            return false;
        }
        return false;
    }

    // individual probability or count with the key
    @Override
    public double GetValue(String dim, String fullKey, SpatialZone curZ)
    {    
        String cat = fullKey.substring(0, fullKey.indexOf(Utils.CATEGORY_DELIMITER)-1);
        String key = fullKey.substring(fullKey.indexOf(Utils.CATEGORY_DELIMITER) + 1, fullKey.length() - 1);
        return GetValue(dim,cat, key, null);
    }

    @Override
    public double GetValue(String dim, String category, String key, SpatialZone curZ)
    {    
        if (myCollection.containsKey(category))
        {
            HashMap<String, Object> currCat = (HashMap)myCollection.get(category);
            if (currCat.containsKey(key))
            {    
                return Double.parseDouble(currCat.get(key).toString());
            }    
            return Utils.INVALID_UINT_VAL;

        }
        return Utils.INVALID_UINT_VAL;
    }

    // commulative probability or count for the key
    @Override
    public ArrayList GetCommulativeValue(String key, SpatialZone curZ)
    {
        ArrayList currComm = new ArrayList();
        double commCnt = 0;
        //System.out.println("avant" + key);
        key = ProcessKey(key);
        //System.out.println("apres" + key);

        for (Map.Entry<String, Object> currEnt : myCollection.entrySet())
        {
            KeyValPair currPair = new KeyValPair();
            currPair.category = (String) currEnt.getKey();
            
            //System.out.println(currPair.category);
            
            /*for (Map.Entry<String, Object> curr : (((HashMap<String, Object>)currEnt.getValue()).entrySet()))
            {
            	System.out.println(curr.getKey() + "  " + curr.getValue() + "  " + key);
            	if(curr.getKey().equals(key)){
            		System.out.println("youhouh");
            	}
            }*/
            
            
            if (((HashMap<String, Object>)currEnt.getValue()).containsKey(key))
            {    
                currPair.value = Double.parseDouble(((HashMap)currEnt.getValue()).get(key).toString());
                currPair.value += commCnt;
                //System.out.println("youhouh");
            }
            else
            {
                currPair.value = commCnt;
            }
            commCnt = currPair.value;
            currComm.add(currPair);
        }
        return currComm;
    }
    
    // commulative probability or count for the key
    
    
}
