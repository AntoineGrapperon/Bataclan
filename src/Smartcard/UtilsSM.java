/**
 * 
 */
package Smartcard;

import java.util.ArrayList;
import java.util.HashMap;

import ActivityChoiceModel.UtilsTS;

/**
 * This class is a dictionary which contains all references required from the data sets.
 * All times are given in minutes, with 0min being equivalent to midnight.
 * All distances are given in meters.
 * @author Antoine
 *
 */

public class UtilsSM {
	public static final double INFINIT = 10000;
	public static String cardId = "numCarte";
	public static String date = "dateComp";
	public static String firstTrans = "dailyFirstTransaction";
	public static String lastTrans = "dailyLastTransaction";
	public static String time = "heureComp";
	public static String isNotFirst = "F";
	public static String isNotLast = "F";
	public static String isFirst = "T";
	public static String isLast = "T";
	public static String zoneId = "DAUID";
	public static String lineId = "NM_LI";
	public static String stationId = "NUM_ARRET";
	public static String destStationId = "DER_Num_Arret_Dest";
	public static String nextStationId = "DER_Num_Arret_Dest";
	
	public static double morningPeakHourStart = 420; 
	public static double morningPeakHourEnd = 540;
	public static double eveningPeakHourStart = 930;
	public static double eveningPeakHourEnd = 1080;
	public static String[] weekEnd = {
			"05/10/2005",
			"06/10/2005",
			"12/10/2005",
			"13/10/2005",
			"19/10/2005",
			"20/10/2005",
			"26/10/2005",
			"27/10/2005"
			};
	public static double timeThreshold = 30;
	public static double distanceThreshold = 1000;
	public static int choiceSetSize = 20;
	public static String noPt = "C_NOPT";
	
	public static HashMap<String, String> dictionnary = new HashMap<String, String>();
	public static String agentId = "agentId";
	public static String fare = "paidFare";
	
	
	public UtilsSM(){
		/*dictionnary.put(UtilsTS.ageGroup, "age");
		dictionnary.put(UtilsTS.sex, "sex");
		dictionnary.put(UtilsTS.mStat, "mStat");
		dictionnary.put(UtilsTS.pers, "nPers");
		dictionnary.put(UtilsTS.inc, "inc");
		dictionnary.put(UtilsTS.cars, "car");
		dictionnary.put(UtilsTS.occupation, "occ");
		dictionnary.put(UtilsTS.edu, "edu");
		dictionnary.put(UtilsTS.motor, "car");*/
		
		dictionnary.put(UtilsTS.motor, "car");
		dictionnary.put(UtilsTS.edu, "edu");
		dictionnary.put(UtilsTS.ageGroup, UtilsTS.ageGroup);
		dictionnary.put(UtilsTS.sex, UtilsTS.sex);
		dictionnary.put(UtilsTS.mStat, UtilsTS.mStat);
		dictionnary.put(UtilsTS.pers, UtilsTS.pers);
		dictionnary.put(UtilsTS.inc, UtilsTS.inc);
		dictionnary.put(UtilsTS.cars, UtilsTS.cars);
		dictionnary.put(UtilsTS.occupation, UtilsTS.occupation);
		dictionnary.put(UtilsTS.edu, UtilsTS.edu);
		dictionnary.put(UtilsTS.motor, UtilsTS.motor);
		
	}
	
	
	
}