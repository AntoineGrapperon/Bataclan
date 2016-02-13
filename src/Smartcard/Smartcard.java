/**
 * 
 */
package Smartcard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import ActivityChoiceModel.BiogemeControlFileGenerator;
import ActivityChoiceModel.UtilsTS;
import Utils.Utils;

/**
 * @author Antoine
 *
 */
public class Smartcard {

	double cardId;
	String stationId;
	int choiceId;
	protected HashMap<String, ArrayList<String>> myData = new HashMap<String, ArrayList<String>>();
	
	public void setId(double id) {
		// TODO Auto-generated method stub
		this.cardId = id;
	}
	
	public void setChoiceId(){
		HashMap<String, Integer> myCombination = new HashMap<String, Integer>();
		int firstDep = getWeekDayAverageFirstDep();
		int lastDep = getWeekDayAverageLastDep();
		int nAct = getWeekDayAverageActivityCount(UtilsSM.timeThreshold);
		int ptFidelity = getWeekDayAveragePtFidelity(UtilsSM.distanceThreshold);
		myCombination.put(UtilsTS.firstDep+"Short", firstDep);
		myCombination.put(UtilsTS.lastDep+"Short", lastDep);
		myCombination.put(UtilsTS.nAct, nAct);
		myCombination.put(UtilsTS.fidelPtRange, ptFidelity);
		choiceId = BiogemeControlFileGenerator.returnChoiceId(myCombination);
	}
	
	/**
	 * This function process the departure hour which is assumed to be in a string format HHMM based on the public transit authority journey which starts at 00AM and end at 2730 (in case of Gatineau, 2005, public transit system.)
	 * It returns a category of departure hour (defined with respect to:
	 * a) granularity available in the travel survey for calibrating the activity choice model
	 * b) hypothesis made in the activity choice model in terms of power of making a difference between categories.
	 * For first departure of the day, this function implements the following categorization (0am to 7 am, 7am to 9 am, after 9am)
	 * @return the departure hour category according the methodology developed 
	 */
	private int getWeekDayAverageFirstDep() {
		// TODO Auto-generated method stub
		int counter = 0;
		double averageDepHour = 0;
		for(int i = 0; i < myData.get(UtilsSM.cardId).size(); i++){
			String currDate = myData.get(UtilsSM.date).get(i);
			if(isWeekDay(currDate)){
				if(myData.get(UtilsSM.firstTrans).get(i).equals(UtilsSM.isFirst)){
					counter++;
					averageDepHour += hourStrToDouble(myData.get(UtilsSM.time).get(i));
				}
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
	private int getWeekDayAverageLastDep() {
		// TODO Auto-generated method stub
		int counter = 0;
		double averageDepHour = 0;
		for(int i = 0; i < myData.get(UtilsSM.cardId).size(); i++){
			String date = myData.get(UtilsSM.date).get(i);
			double time = hourStrToDouble(myData.get(UtilsSM.time).get(i));
			if(isWeekDay(date)){
				if(isLast(date,time)){
					counter++;
					averageDepHour += hourStrToDouble(myData.get(UtilsSM.time).get(i));
				}
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
	
	/**
	 * This function identifies activities out of boarding time.
	 * @param timeThreshold in minutes, is the time limit between two boardings to consider it as an activity.
	 * @return the number of activities
	 */
	public int getWeekDayAverageActivityCount(double timeThreshold){
		
		HashMap<String, ArrayList<Double>> boardingTimes = new HashMap<String, ArrayList<Double>>();
		for(int i = 0; i < myData.get(UtilsSM.cardId).size();i++){
			String currDate = myData.get(UtilsSM.date).get(i);
			Double time = hourStrToDouble(myData.get(UtilsSM.time).get(i));
			if(isWeekDay(currDate)){
				if(!boardingTimes.containsKey(currDate)){
					boardingTimes.put(currDate, new ArrayList<Double>());
					boardingTimes.get(currDate).add(time);
				}
				else{
					boardingTimes.get(currDate).add(time);
				}
			}
		}
		int nAct = 0;
		int dayCounter = 0;
		for(String date: boardingTimes.keySet()){
			dayCounter++;			
			Collections.sort(boardingTimes.get(date));
			for(int j = 0; j < boardingTimes.get(date).size()-1; j++){
				double timeInterval = boardingTimes.get(date).get(j+1) - boardingTimes.get(date).get(j);
				if(timeInterval < timeThreshold){
					nAct++;
				}
			}
		}
		
		double avgNAct = nAct/dayCounter;
		int answer = (int) Math.round(avgNAct);
		if(answer<3){return answer;}
		else{return 3;}
	}
	
	public int getWeekDayAveragePtFidelity(double distanceThreshold){
		int counterTripLegs = 0;
		int counterNonPt = 0;
		for(int i = 0; i < myData.get(UtilsSM.cardId).size()-1;i++){
			String currDate = myData.get(UtilsSM.date).get(i);
			if(isWeekDay(currDate)){
				int stationId = Integer.parseInt(myData.get(UtilsSM.stationId).get(i));
				int nextStationId = Integer.parseInt(myData.get(UtilsSM.nextStationId).get(i));
				if(nextStationId != 0){
					Station station = PublicTransitSystem.myStations.get(stationId);
					Station nextStation = PublicTransitSystem.myStations.get(nextStationId);
					station.getDistance(nextStation);
					if(station.getDistance(nextStation)>distanceThreshold){
						counterNonPt ++;
					}
				}
				else{
					counterNonPt++;
				}
				counterTripLegs++;
			}
		}
		if(counterTripLegs != 0){
			double ptFidelity = counterNonPt/counterTripLegs;
			if(ptFidelity < 0.5){return 0;}
			else if(ptFidelity < 0.95){return 1;}
			else{return 2;}
		}
		else{
			return 0;
		}
		
	}
	
	private boolean isWeekDay(String currDate) {
		// TODO Auto-generated method stub
		return !Arrays.asList(UtilsSM.weekEnd).contains(currDate);
	}

	

	/**
	 * Assign to the smart card instance the identifier of the local area where the smart card holder did validate the most frequently.
	 */
	public void identifyMostFrequentStation(){
		HashMap<String, Integer> stationFrequencies = getStationFrequencies();
		String myStationId = new String();
		Integer freq = 0;
		for(String key: stationFrequencies.keySet()){
			if(stationFrequencies.get(key)>freq){
				myStationId = key;
				freq = stationFrequencies.get(key);
			}
		}
		stationId = myStationId;
	}

	/**
	 * This function returns a HashMap those key set is constituted of all the zone id that the smart card holder validated in for the first trip of each day.
	 * The value is the number of time he validated in in the zone.
	 * @return a HashMap which keyset is the local zone identifier and the value is the count of time the first validation of day was made in this specific local area.
	 */
	public HashMap<String,Integer> getStationFrequencies() {
		// TODO Auto-generated method stub
		HashMap<String, Integer> stopFrequencies = new HashMap<String, Integer>();
		for(int i = 0; i < myData.get(UtilsSM.cardId).size(); i++){
			String date = myData.get(UtilsSM.date).get(i);
			double time = hourStrToDouble(myData.get(UtilsSM.time).get(i));
			if(isFirst(date, time)){
				String stopId = myData.get(UtilsSM.stationId).get(i);
				if(stopFrequencies.containsKey(stopId)){
					int fr = stopFrequencies.get(stopId) + 1;
					stopFrequencies.put(stopId, fr);
				}
				else{
					stopFrequencies.put(stopId, 1);
				}
			}
		}
		return stopFrequencies;
	}
	
	public void tagFirstTransaction(){
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
