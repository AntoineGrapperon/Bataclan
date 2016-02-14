/**
 * 
 */
package Smartcard;

import java.util.ArrayList;

/**
 * This class is a dictionary which contains all references required from the data sets.
 * All times are given in minutes, with 0min being equivalent to midnight.
 * All distances are given in meters.
 * @author Antoine
 *
 */

public class UtilsSM {
	public static String cardId = "numCarte";
	public static String date = "dateComp";
	public static String firstTrans = "dailyFirstTransaction";
	public static String time = "heureComp";
	public static String isNotFirst = "F";
	public static String isFirst = "T";
	public static String zoneId = "zoneId";
	public static String lineId = "NM_LI";
	public static String stationId = "NUM_ARRET";
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
	public static int choiceSetSize = 8;
	
	
}