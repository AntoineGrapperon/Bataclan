package Utils;

import java.util.ArrayList;

import SimulationObjects.SpatialZone;
import SimulationObjects.AgentAttributes.*;

/**
 *
 * @author XPS 1645
 */
public class ModelDistribution extends ConditionalDistribution
{

	public static ArrayList<Integer> NumberOfCategories = new ArrayList <Integer>();
	public static double[][] UtilityCoefForAttributes;
	//private static String [][] UtilityVariablesForAttributes;
	//private static double [][] UtilityVariablesForAttributes1;
	private ArrayList<ArrayList<Double>> Utility = new ArrayList<ArrayList<Double>>();
	private double [][] UtilityValues;
	public static int [] CoefSize;
	public static int categorysize;
	public static int numberofcategories;
	public static ArrayList<String> UtilityArray = new ArrayList<String>(); 
	private static int max;

    @Override
    public double GetValue(String dim, String fullKey, SpatialZone curZ)
    {
        String cat = fullKey.substring(0, fullKey.indexOf(Utils.CATEGORY_DELIMITER) - 1);
        String key = fullKey.substring(fullKey.indexOf(Utils.CATEGORY_DELIMITER) + 1, fullKey.length() - 1);
        return GetValue(cat, dim, key, curZ);
    }

    @Override
    public double GetValue(String dimension, String category, String key, SpatialZone curZ)
    {
        //string procdKey = ProcessKey(key);
        // [BF] For now here it will always be income or education
    	for (int i=0; i<InputDataReader.AttributesWithModel.size();i++)
    	{
    		if (dimension.equals(InputDataReader.AttributesWithModel.get(i)))
    		{
            return ComputeAttributeProbabilities(category,curZ).get(i);
    		}
    	}
    	return 0;
    }

    private ArrayList GetCommValue(String dimension, String key, SpatialZone curZ)
    { 
    	for (int i=0; i<InputDataReader.AttributesWithModel.size();i++)
    	
    		if (dimension.equals(InputDataReader.AttributesWithModel.get(i)))
    		{	
    			return ComputeAttributeCommulative(curZ).get(i);
    		}
        
        return null;
    }
    
    	@Override
    public ArrayList GetCommulativeValue(String key, SpatialZone curZ)
    {
        return GetCommValue(GetDimensionName(),key, curZ);
    }
 
    private ArrayList<Double> ComputeAttributeProbabilities(String category, SpatialZone curZ)
    {
    ArrayList<Double> AttributeProbabilities = new ArrayList<Double>(); 
    double probability;
    ArrayList<Double> logsumarray= new ArrayList<Double>();
    double logsum;
    ArrayList<ArrayList<Double>> valarraylist = Utility;
    
    for (int i=0; i<valarraylist.size(); i++)
    {
    	logsum = 0;
    	for (int j=0; j<valarraylist.get(i).size(); j++)		   
    	{
			   logsum += Double.parseDouble(String.valueOf(valarraylist.get(i).get(j)));
			   logsumarray.add(logsum);
    	}
    		   
    	for (int k=0; k<InputDataReader.CategoriesOfAttributesWithModel.get(i).size();k++)
    	{
    		if (category.equals(InputDataReader.CategoriesOfAttributesWithModel.get(k)))
    		{
    			probability = (Double.parseDouble(String.valueOf(valarraylist.get(i).get(k))))/logsumarray.get(i);
    		}
    		else
    		{
    			probability=0;
    		}
		   AttributeProbabilities.add(probability);
    	}   
    }
    return AttributeProbabilities;
    }
    
    private ArrayList<ArrayList<KeyValPair>> ComputeAttributeCommulative(SpatialZone curZ)
    {
    	double comVal = 0.00;
    	ArrayList<ArrayList<Double>> valarraylist = Utility;
        ArrayList <Double> utilSumarray = new ArrayList<Double>();
        double utilSum;
        ArrayList<ArrayList<KeyValPair>> comList = new ArrayList<ArrayList<KeyValPair>>() ;
        comList.add(new ArrayList<KeyValPair>()); 
        
        for (int i=0; i<valarraylist.size(); i++)
        {
        	utilSum = 0;
        	for (int j=0; j<valarraylist.get(i).size(); j++)		   
        	{
        		utilSum += Double.parseDouble(String.valueOf(valarraylist.get(i).get(j)));
        	}
        	utilSumarray.add(utilSum);

        	for (int k=0; k<InputDataReader.CategoriesOfAttributesWithModel.get(i).size();k++)
        	{
        		KeyValPair currPair = new KeyValPair();
        		currPair.category = InputDataReader.CategoriesOfAttributesWithModel.get(i).get(k);
        		currPair.value = comVal + ((Double.parseDouble(String.valueOf(valarraylist.get(i).get(k)))) / utilSumarray.get(i));
        		comVal = currPair.value;
        		comList.get(i).add(currPair);
        	}
        }
  
        return comList;
    }

    public void AddUtility(double[] onedimarray, int numbofmodels)
    {	
    	int i;
    	Utility.add(new ArrayList<Double>());
    	for (i=0; i<InputDataReader.maxNumOfCategories; i++)
    	{
    		Utility.get(numbofmodels).add(onedimarray[i]);	
    	}
    }
}