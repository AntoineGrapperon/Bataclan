/**
 * 
 */
package ActivityChoiceModel;

import java.util.ArrayList;
import java.util.HashMap;

import Smartcard.UtilsSM;
import Utils.Utils;

/**
 * @author Antoine
 *
 */
public class BiogemeChoice {
	
	public int biogeme_id;
	public int biogeme_case_id;
	public HashMap<String,Integer> choiceCombination = new HashMap<String,Integer> ();
	public double probability;
	public double utility;
	public HashMap<String,String> myAttributes = new HashMap<String, String>();
	public String nest = new String();
	
	public BiogemeChoice(){
		
	}

	public boolean isAffecting(BiogemeHypothesis currH, BiogemeAgent currAgent) {
		// TODO Auto-generated method stub
		
		for(int i = 0; i < currH.affectingCategories.size();i++){
			if(choiceCombination.containsKey(currH.affectingDimensionName)){
				if(choiceCombination.get(currH.affectingDimensionName) == currH.affectingCategories.get(i)){
					return true;
				}
			}
			else if(currAgent.myAttributes.containsKey(currH.affectingDimensionName)){
				if(currH.affectingCategories.get(i) == Integer.parseInt(currAgent.myAttributes.get(currH.affectingDimensionName))){
					return true;
				}
			}
			else{
				//System.out.println("oups " + currH.affectingDimensionName);	
			}
		}
		return false;
	}
	
	
	public boolean isAffected(BiogemeHypothesis currH){
		// TODO Auto-generated method stub
		
		for(int i = 0; i < currH.affectedCategories.size();i++){
			if(choiceCombination.get(currH.affectedDimensionName) == currH.affectedCategories.get(i)){
				return true;
			}
		}
		//System.out.println("oups " + currH.affectingDimensionName);
		return false;
	}
	
	/*public boolean isCst(BiogemeHypothesis currH){
		// TODO Auto-generated method stub
		if(currH.coefName.equals(getConstantName())){
			return true;
		}
		
		return false;
	}*/

	public double getAffectingValue(BiogemeHypothesis currH, BiogemeAgent currAgent) {
		// TODO Auto-generated method stub
		for(int i = 0; i < currH.affectingCategories.size();i++){
			if(choiceCombination.get(currH.affectingDimensionName) == currH.affectingCategories.get(i)){
				return currH.affectingCategories.get(i);
			}
			if(choiceCombination.get(currH.affectingDimensionName) == Integer.parseInt(currAgent.myAttributes.get(currH.affectingDimensionName))){
				return Double.parseDouble(currAgent.myAttributes.get(currH.affectingDimensionName));
			}
		}
		return (Double) null;
	}
	
	public String getConstantName(){
		return getConstantName(choiceCombination);
		/*String constantName = new String();
		
		if(choiceCombination.get(UtilsTS.nAct) == 0 || choiceCombination.get(UtilsTS.fidelPtRange)==0){
			constantName = UtilsSM.noPt;
		}
		/*if(choiceCombination.get(UtilsTS.nAct) == 0){
			constantName = "C_HOME";
		}
		else if(choiceCombination.get(UtilsTS.nAct)!= 0 && choiceCombination.get(UtilsTS.fidelPtRange)==0){
			constantName = "C_NOT_PT_RIDER";
		}*/
		/*else if(choiceCombination.get(UtilsTS.nAct)!=0 && choiceCombination.get(UtilsTS.fidelPtRange)!=0){
			/*constantName = "C";
			for(String key: choiceCombination.keySet()){
				constantName+= "_"+choiceCombination.get(key);
			}*/
		/*	constantName = "C_"+choiceCombination.get(UtilsTS.fidelPtRange) +
					"_" + choiceCombination.get(UtilsTS.firstDep+"Short") +
					"_" + choiceCombination.get(UtilsTS.nAct) +
					"_" + choiceCombination.get(UtilsTS.lastDep+"Short");
		}
		return constantName;*/
	}
	
	public static String getConstantName(HashMap<String, Integer> combination){
		String constantName = new String();
		if(combination.get(UtilsTS.nest) == 0){
			constantName = "C_" + UtilsTS.carDriver;
		}
		else if(combination.get(UtilsTS.nest) == 1){
			constantName = "C_" + UtilsTS.carPassenger;
		}
		else if(combination.get(UtilsTS.nest) == 2 &&
				combination.get(UtilsTS.nAct)!=0 &&
				combination.get(UtilsTS.fidelPtRange)!=0){
			constantName = "C_"+combination.get(UtilsTS.fidelPtRange) +
					"_" + combination.get(UtilsTS.firstDep+"Short") +
					"_" + combination.get(UtilsTS.nAct) +
					"_" + combination.get(UtilsTS.lastDep+"Short");
		}
		else if(combination.get(UtilsTS.nest) == 3){
			constantName = "C_" + UtilsTS.ptUserNoSto;
		}
		else if(combination.get(UtilsTS.nest) == 4){
			constantName = "C_" + UtilsTS.activeMode;
		}
		else{ //last case is active mode and we added all 'bad record of the other cases
			constantName = "-1";
		}
		return constantName;
	}
	
	public  String getNestName(){
		return getNestName(choiceCombination);
	}
	
	public static String getNestName(HashMap<String, Integer> combination){
		String constantName = new String();
		if(combination.get(UtilsTS.nest) == 0){
			constantName = UtilsTS.carDriver;
		}
		else if(combination.get(UtilsTS.nest) == 1){
			constantName = UtilsTS.carPassenger;
		}
		else if(combination.get(UtilsTS.nest) == 2 &&
				combination.get(UtilsTS.nAct)!=0 && combination.get(UtilsTS.fidelPtRange)!=0){
			constantName = UtilsTS.stoUser;
		}
		else if(combination.get(UtilsTS.nest) == 3){
			constantName = UtilsTS.ptUserNoSto;
		}
		else{ //last case is active mode and we added all 'bad record of the other cases
			constantName = UtilsTS.activeMode;
		}
		return constantName;
	}
	
	public String toString(){
		String answer = Integer.toString(biogeme_id);
		for(String key: choiceCombination.keySet()){
			answer+= Utils.COLUMN_DELIMETER + key + Utils.COLUMN_DELIMETER + choiceCombination.get(key);
		}
		return answer;
	}
	
}
