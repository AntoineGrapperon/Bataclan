/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Utils;

/**
 *
 * @author XPS 1645
 */
public class Utils
{
    //KeyValPair
    public String category;
    public double value;

    //Constants
    public static double INVALID_VAL = -1000.00;
    public static int INVALID_UINT_VAL = 0;
    public static String CATEGORY_DELIMITER = "|";
    public static String CONDITIONAL_DELIMITER = "-";
    public static String COLUMN_DELIMETER = ",";
    public static String CONDITIONAL_GENERIC = "...";
    public static String NEW_LINE_DELIMITER = "\n";
    public static String NULL = "NULL";

    public static int WARMUP_ITERATIONS = 500000;
    public static int SKIP_ITERATIONS = 1000;
    public static int POOL_COUNT = 10000;
    public static int numberOfSeeds =1;

    public static double BFRANC_TO_EURO = 40.3399;

    public static String DATA_DIR;
    //public static String DATA_DIR = "D:\\Recherche\\modelGatineau\\";
    
    public static String pathToMarginalChecks = "D:\\Recherche\\CharlieWorkspace\\PopSynz\\marginalChecks.csv";
    public static String pathToMetroMarginalChecks = "D:\\Recherche\\CharlieWorkspace\\PopSynz\\metroMarginalChecks.csv";
    
    
    String city;
    
    public static boolean createLocal = false;
	public static int personId = 0;
	public static String population = "Population";
	public static boolean occupationCriterion = true;
    
    public Utils(String city){
    	this.city = city;
    	if(city.equals("Montreal")){
    		DATA_DIR = "D:\\Recherche\\CharlieWorkspace\\Bataclan\\";
    	}
    	else if(city.equals("Gatineau")){
    		DATA_DIR = "D:\\Recherche\\modelGatineau\\";
    	}
    }
    
    public static String fmt(double d)
    {
        if(d == (long) d)
            return String.format("%d",(long)d);
        else
            return String.format("%s",d);
    }
    
    public static double max(double... n) {
        int i = 0;
        double max = n[i];

        while (++i < n.length)
            if (n[i] > max)
                max = n[i];

        return max;
    }
    
    
}
