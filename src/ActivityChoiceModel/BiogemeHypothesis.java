/**
 * 
 */
package ActivityChoiceModel;

import java.util.ArrayList;

/**
 * @author Antoine
 *
 */
public class BiogemeHypothesis {
	public String coefName;
	String affectedDimensionName;
	ArrayList<Integer> affectedCategories;
	String affectingDimensionName;	
	ArrayList<Integer> affectingCategories;
	public double coefValue;
	public boolean isDummy = true;
	public boolean isAgentSpecificVariable;
	public boolean isAlternativeSpecificVariable;
	
	public BiogemeHypothesis(){
		
	}
	
	public void setCoefName(String name){
		coefName = name;
	}
	
	public void setAffectedDimension(String dimName, ArrayList<Integer> categories){
		affectedDimensionName = dimName;
		affectedCategories = categories;
	}
	
	public void setAffectingDimension(String dimName, ArrayList<Integer> categories){
		affectingDimensionName = dimName;
		affectingCategories = categories;
	}

	public double getCoefficientValue() {
		// TODO Auto-generated method stub
		return coefValue;
	}
	
	public String toString(){
		String answer = new String();
		answer = coefName + Utils.Utils.COLUMN_DELIMETER 
				+ coefValue + Utils.Utils.COLUMN_DELIMETER 
				+ affectedDimensionName +  Utils.Utils.COLUMN_DELIMETER
				+ affectedCategories +  Utils.Utils.COLUMN_DELIMETER 
				+ affectingDimensionName +  Utils.Utils.COLUMN_DELIMETER
				+ affectingCategories +  Utils.Utils.COLUMN_DELIMETER ;			
		return answer;
	}
	
	public boolean isCst() {
		// TODO Auto-generated method stub
		for(BiogemeChoice currChoice: BiogemeControlFileGenerator.choiceIndex){
			if(this.coefName.equals(currChoice.getConstantName())){
				return true;
			}
		}
		return false;
	}
}
