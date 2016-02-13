/**
 * 
 */
package Smartcard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import ActivityChoiceModel.BiogemeControlFileGenerator;
import ActivityChoiceModel.DataManager;
import Utils.InputDataReader;
import Utils.OutputFileWritter;

/**
 * @author Antoine
 *
 */
public class SmartcardDataManager extends DataManager{
	
	
	BiogemeControlFileGenerator myCtrlFileGenerator = new BiogemeControlFileGenerator();
	ArrayList<Smartcard> mySmartcards = new ArrayList<Smartcard>();
	
	SmartcardDataManager( BiogemeControlFileGenerator biogemeGenerator){
		myCtrlFileGenerator = biogemeGenerator;
	}
	
	public ArrayList<Smartcard> prepareSmartcards(String smartcardData) throws IOException{
		initialize(smartcardData);
		System.out.println("--smartcard manager initialized");
		createSmartcards();
		System.out.println("--smartcard created");
		identifyMostFrequentStation();
		System.out.println("--assign bus station");
		assignChoiceId();
		System.out.println("--identify choice Id");
		return mySmartcards;
	}

	private void assignChoiceId() {
		// TODO Auto-generated method stub
		for(Smartcard currSm: mySmartcards){
			currSm.setChoiceId();
		}
	}

	/**
	 * This method aims to identify the dissemination area ID of the smartcard holder.
	 * The basic assumption is that every morning, the smart card holder validate his smart card at a bus stop.
	 * The location of the bus stop is known, we look over a month of data and we infer that the most frequent dissemination area is the place of living.
	 */
	private void identifyMostFrequentStation() {
		// TODO Auto-generated method stub
		for(Smartcard currSm : mySmartcards){
			currSm.tagFirstTransaction();
			currSm.identifyMostFrequentStation();
		}
	}

	
	private void createSmartcards() {
		// TODO Auto-generated method stub
		System.out.println("-- number of records to process " + myData.get(UtilsSM.cardId).size());
		for(int i = 0; i < myData.get(UtilsSM.cardId).size(); i++){
			Smartcard currSm = getSmartcard(i);
			updateSmartcard(currSm,i);
		}
		System.out.println("--number of smart cards " + mySmartcards.size());
	}
	
	/**
	 * This method check if the smart card corresponding to line i is already created and stored in mySmartcards.
	 * If it is an existing smart card, then it returns it. Otherwise, it create a new one, add it to the smartcards and return it.
	 * @param i
	 * @return
	 */
	public Smartcard getSmartcard(int i){
		double id = Double.parseDouble(myData.get(UtilsSM.cardId).get(i));
		for(Smartcard currS : mySmartcards){
			if(currS.cardId == id){
				return currS;
			}
		}
		Smartcard newSm = new Smartcard();
		newSm.setId(Double.parseDouble(myData.get(UtilsSM.cardId).get(i)));
		createDataStructure(newSm);
		mySmartcards.add(newSm);
		return newSm;
	}
	
	public boolean containsSmartcard(int id){
		for(Smartcard currS : mySmartcards){
			if(currS.cardId == id){
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Update the smart card information with the information from the new line i.
	 * @param sm
	 * @param i
	 */
	public void updateSmartcard(Smartcard sm, int i){
		for(String key: myData.keySet()){
			String value = myData.get(key).get(i);
			sm.myData.get(key).add(value);
		}
	}
	
	/**
	 * Replicate in the smartcard, the data structure from the data set.
	 * @param sm
	 */
	public void createDataStructure(Smartcard sm){
		for(String key: myData.keySet()){
			sm.myData.put(key, new ArrayList<String>());
		}
	}
}
