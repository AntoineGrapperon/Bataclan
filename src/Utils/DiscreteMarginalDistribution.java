/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Utils;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author XPS 1645
 */
public class DiscreteMarginalDistribution extends MarginalDistribution
{
    private HashMap<String, Object> myCollection;
    private String dimension;

    public String GetDimensionName()
    {
        return dimension;
    }

    public void SetDimensionName(String dim)
    {
        dimension = dim;
    }

    public DiscreteMarginalDistribution()
    {
        myCollection = new HashMap<String, Object>();
    }

    public int GetCategoryCount()
    {
        return myCollection.size();
    }

    public boolean AddValue(String category, double val)
    {
        if (!myCollection.containsKey(category))
        {
            myCollection.put(category, val);
            return true;
        }
        return true;
    }

    @Override
    public double GetValue(String category)
    {
        if(myCollection.containsKey(category))
        {    
            return Double.parseDouble(myCollection.get(category).toString());
        }
        return 0.00;
    }

	public boolean CumulValue(String category, double val) {
		// TODO Auto-generated method stub
		if (!myCollection.containsKey(category))
        {
            myCollection.put(category, val);
            double tempVal = (double)myCollection.get(category);
            //System.out.println("--added : " + category + "   " + tempVal);
        }
		else{
			double tempVal;
			tempVal = (double) myCollection.get(category);
			tempVal=tempVal+val;
			myCollection.put(category, tempVal);
			//System.out.println(category + "  " +tempVal);
		}
		

        return true;
	}

	public void update(String category) {
		// TODO Auto-generated method stub
		if (!myCollection.containsKey(category))
        {
            myCollection.put(category, 1);
        }
		else{
			double tempVal;
			tempVal = Double.parseDouble(myCollection.get(category).toString());
			tempVal++;
			myCollection.put(category, tempVal);
		}	
	}
	
	public static String fmt(double d)
    {
        if(d == (long) d)
            return String.format("%d",(long)d);
        else
            return String.format("%s",d);
    }
	
	public void FlushOutData()
    {
        for (Map.Entry<String, Object> currEnt : myCollection.entrySet())
        {
            ((HashMap)currEnt.getValue()).clear();
        }
        myCollection.clear();
    }
}
