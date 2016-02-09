/**
 * 
 */
package Smartcard;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Antoine
 *
 */
public class Smartcard {

	int cardId;
	String zoneId;
	int choiceId;
	protected HashMap<String, ArrayList<String>> myData = new HashMap<String, ArrayList<String>>();
	
	public void setId(int id) {
		// TODO Auto-generated method stub
		this.cardId = id;
	}
	
	public void setChoiceId(){
		HashMap<String, Integer> myCombination = new HashMap<String, Integer>();
		int firstDep = getFirstDep();
		int lastDep = getLastDep();
		int nAct = getActivityCount();
		int ptFidelity = getPtFidelity();
		
		
	}
	
	/**
	 * This function process the departure hour which is assumed to be in a string format HHMM based on the public transit authority journey which starts at 00AM and end at 2730 (in case of Gatineau, 2005, public transit system.)
	 * It returns a category of departure hour (defined with respect to:
	 * a) granularity available in the travel survey for calibrating the activity choice model
	 * b) hypothesis made in the activity choice model in terms of power of making a difference between categories.
	 * For first departure of the day, this function implements the following categorization (0am to 7 am, 7am to 9 am, after 9am)
	 * @return the departure hour category according the methodology developed 
	 */
	private int getFirstDep() {
		// TODO Auto-generated method stub
		int counter = 0;
		double averageDepHour = 0;
		for(int i = 0; i < myData.get(UtilsSM.cardId).size(); i++){
			if(myData.get(UtilsSM.firstTrans).get(i).equals(UtilsSM.isFirst)){
				counter++;
				averageDepHour += hourStrToDouble(myData.get(UtilsSM.time).get(i));
			}
		}
		averageDepHour = averageDepHour/counter;
		
		if(averageDepHour < UtilsSM.morningPeakHourStart){
			return 0;
		}
		else if(averageDepHour < UtilsSM.morningPeakHourEnd){
			return 1;
		}
		else if(averageDepHour > UtilsSM.morningPeakHourEnd){
			return 2;
		}
		else{
			System.out.println("--error affecting departure hours");
			return 10;
		}
	}
	
	/**
	 * This function process the departure hour which is assumed to be in a string format HHMM based on the public transit authority journey which starts at 00AM and end at 2730 (in case of Gatineau, 2005, public transit system.)
	 * It returns a category of departure hour (defined with respect to:
	 * a) granularity available in the travel survey for calibrating the activity choice model
	 * b) hypothesis made in the activity choice model in terms of power of making a difference between categories.
	 * For first departure of the day, this function implements the following categorization (before 3.30pm, 3.30pm to 6pm, after 6pm)
	 * @return the departure hour category according the methodology developed 
	 */
	private int getLastDep() {
		// TODO Auto-generated method stub
		int counter = 0;
		double averageDepHour = 0;
		for(int i = 0; i < myData.get(UtilsSM.cardId).size(); i++){
			String date = myData.get(UtilsSM.date).get(i);
			double time = hourStrToDouble(myData.get(UtilsSM.time).get(i));
			if(isLast(date,time)){
				counter++;
				averageDepHour += hourStrToDouble(myData.get(UtilsSM.time).get(i));
			}
		}
		averageDepHour = averageDepHour/counter;
		
		if(averageDepHour < UtilsSM.eveningPeakHourStart){
			return 0;
		}
		else if(averageDepHour < UtilsSM.eveningPeakHourEnd){
			return 1;
		}
		else if(averageDepHour > UtilsSM.eveningPeakHourEnd){
			return 2;
		}
		else{
			System.out.println("--error affecting departure hours");
			return 10;
		}
	}
	
	public int getActivityCount(){
		return 0;
	}
	
	public int getPtFidelity(){
		
		return 0;
	}

	/**
	 * Assign to the smart card instance the identifier of the local area where the smart card holder did validate the most frequently.
	 */
	public void identifyZoneOfLiving(){
		HashMap<String, Integer> zoneFrequencies = getZoneFrequencies();
		String myZoneId = new String();
		Integer freq = 0;
		for(String key: zoneFrequencies.keySet()){
			if(zoneFrequencies.get(key)>freq){
				myZoneId = key;
				freq = zoneFrequencies.get(key);
			}
		}
		zoneId = myZoneId;
	}

	/**
	 * This function returns a HashMap those key set is constituted of all the zone id that the smart card holder validated in for the first trip of each day.
	 * The value is the number of time he validated in in the zone.
	 * @return a HashMap which keyset is the local zone identifier and the value is the count of time the first validation of day was made in this specific local area.
	 */
	public HashMap<String,Integer> getZoneFrequencies() {
		// TODO Auto-generated method stub
		HashMap<String, Integer> zoneFrequencies = new HashMap<String, Integer>();
		for(int i = 0; i < myData.get(UtilsSM.cardId).size(); i++){
			String date = myData.get(UtilsSM.date).get(i);
			double time = hourStrToDouble(myData.get(UtilsSM.time).get(i));
			if(isFirst(date, time)){
				String zoneId = myData.get(UtilsSM.zoneId).get(i);
				if(zoneFrequencies.containsKey(zoneId)){
					int fr = zoneFrequencies.get(zoneId) + 1;
					zoneFrequencies.put(zoneId, fr);
				}
				else{
					zoneFrequencies.put(zoneId, 1);
				}
			}
		}
		return zoneFrequencies;
	}
	
	public void identifyDailyFirstTransaction(){
		myData.put(UtilsSM.firstTrans, new ArrayList<String>());
		for(int i = 0; i < myData.get(UtilsSM.cardId).size(); i++){
			String date = myData.get(UtilsSM.date).get(i);
			double time = hourStrToDouble(myData.get(UtilsSM.time).get(i));
			if(isFirst(date,time)){
				myData.get(UtilsSM.firstTrans).add(UtilsSM.isFirst);
			}
			else{
				myData.get(UtilsSM.firstTrans).add(UtilsSM.isNotFirst);
			}
		}
	}
	
	public boolean isFirst(String date, double time){
		for(int j = 0; j < myData.get(UtilsSM.cardId).size(); j++){
			String date2 = myData.get(UtilsSM.date).get(j);
			double time2 = hourStrToDouble(myData.get(UtilsSM.time).get(j));
			if(date.equals(date2)){
				if(time2 < time){
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean isLast(String date, double time){
		for(int j = 0; j < myData.get(UtilsSM.cardId).size(); j++){
			String date2 = myData.get(UtilsSM.date).get(j);
			double time2 = hourStrToDouble(myData.get(UtilsSM.time).get(j));
			if(date.equals(date2)){
				if(time2 > time){
					return false;
				}
			}
		}
		return true;
	}
	
	private double hourStrToDouble(String time) {
		// TODO Auto-generated method stub
		
		time.trim();
		double hour = 0;
		if(time.length()==4){
			hour = 60 * Double.parseDouble(time.substring(0, 1)) + Double.parseDouble(time.substring(2,3));
		}
		else if(time.length() == 3){
			hour = 60 * Double.parseDouble(Character.toString(time.charAt(0))) + Double.parseDouble(time.substring(1,2));
		}
		return hour; //here, hour is in minutes
	}
}