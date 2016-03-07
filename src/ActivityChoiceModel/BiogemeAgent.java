/**
 * 
 */
package ActivityChoiceModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import Smartcard.PublicTransitSystem;
import Smartcard.Smartcard;
import Smartcard.Station;
import Smartcard.UtilsSM;
import Utils.RandomNumberGen;
import Utils.Utils;

/**
 * @author Antoine
 *
 */
public class BiogemeAgent {
	
	public HashMap<String, String> myAttributes;
	protected static RandomNumberGen randGen = new RandomNumberGen();
	ArrayList<BiogemeChoice> myChoices = new ArrayList<BiogemeChoice>();
	public boolean isDistributed = false;
	public double smartcard = 0.0;
	
	public BiogemeAgent(){
		myAttributes = new HashMap<String, String>();
	}

	public void setAttributes(ArrayList<String> attributeNames, ArrayList<String> attributeValues) {
		// TODO Auto-generated method stub
		for(int i = 0; i < attributeNames.size(); i++){
			myAttributes.put(attributeNames.get(i), attributeValues.get(i));
		}
	}

	@Deprecated
	public void applyModel(ArrayList<Integer> choiceSet) {
		// TODO Auto-generated method stub
		//rigth I am just able to apply dummies
		ArrayList<Double> utilities = new ArrayList<Double>();
		for(int i = 0; i < choiceSet.size(); i++){
			
			double utility = 0;
			BiogemeChoice currChoice =BiogemeControlFileGenerator.choiceIndex.get(choiceSet.get(i));
			
			for(BiogemeHypothesis currH: BiogemeSimulator.modelHypothesis){
				if(currH.isCst()){
					utility += currH.getCoefficientValue();
				}
				else if(currChoice.isAffected(currH)  && currH.isDummy){
					if( currChoice.isAffecting(currH, this)){
						utility += currH.getCoefficientValue();
					}
				}
				else if(currChoice.isAffected(currH) && !currH.isDummy){
					if(currH.isAgentSpecificVariable){
						String att = currH.affectingDimensionName ;
						utility += currH.getCoefficientValue() * Double.parseDouble(myAttributes.get(att));
					}
					else if(currH.isAlternatibeSpecificVariable){
						String att = currH.affectingDimensionName;
						utility += currH.getCoefficientValue() * Double.parseDouble(currChoice.myAttributes.get(att));
					}
					else{
						System.out.println(currH.coefName + " was not considered");
					}
					//utility += currH.getCoefficientValue() * currChoice.getAffectingValue(currH, this);
				}
			}
			//currChoice.utility = utility;
			utilities.add(utility);
			
			/*for(BiogemeHypothesis currH: BiogemeSimulator.modelHypothesis){
				if(currH.isCst()){
					utility += currH.getCoefficientValue();
				}
				else if(currChoice.isAffected(currH) && currChoice.isAffecting(currH, this) && currH.isDummy){
					utility += currH.getCoefficientValue();
				}
				else if(currChoice.isAffected(currH) && !currH.isDummy){
					utility += currH.getCoefficientValue() * currChoice.getAffectingValue(currH, this);
				}
			}
			utilities.add(utility);*/
		}
		
		ArrayList<Double> cumProbabilities = processUtilities(utilities);
		int choiceIndex = antitheticDraw(cumProbabilities);
		myAttributes.put(UtilsTS.sim, Integer.toString(choiceSet.get(choiceIndex)));
	}
	
	/*public void applyModelSmartcard(ArrayList<BiogemeChoice> choiceSet) {
		// TODO Auto-generated method stub
		//rigth I am just able to apply dummies
		ArrayList<Double> utilities = new ArrayList<Double>();
		for(int i = 0; i < choiceSet.size(); i++){
			
			double utility = 0;
			int choiceIndex = choiceSet.get(i).biogeme_group_id;
			BiogemeChoice currChoice =BiogemeControlFileGenerator.choiceIndex.get(choiceIndex);
			
			for(BiogemeHypothesis currH: BiogemeSimulator.modelHypothesis){
				if(currH.isCst()){
					utility += currH.getCoefficientValue();
				}
				else if(currChoice.isAffected(currH) && currChoice.isAffecting(currH, this) && currH.isDummy){
					utility += currH.getCoefficientValue();
				}
				else if(currChoice.isAffected(currH) && !currH.isDummy){
					utility += currH.getCoefficientValue() * currChoice.getAffectingValue(currH, this);
				}
			}
			utilities.add(utility);
		}
		
		ArrayList<Double> cumProbabilities = processUtilities(utilities);
		int choiceIndex = antitheticDraw(cumProbabilities);
		myAttributes.put(UtilsTS.sim, Integer.toString(choiceSet.get(choiceIndex).biogeme_group_id));
	}*/
	
	
	
	

	/*private boolean isCst(BiogemeHypothesis currH) {
		// TODO Auto-generated method stub
		for(BiogemeChoice currChoice: BiogemeControlFileGenerator.choiceIndex){
			if(currChoice.isCst(currH)){
				return true;
			}
		}
		return false;
	}*/

	private ArrayList<Double> processUtilities(ArrayList<Double> utilities) {
		// TODO Auto-generated method stub
		ArrayList<Double> cumProbabilities = new ArrayList<Double>();
		Double logsum = 0.0;
		for(int i = 0; i < utilities.size(); i++){
			logsum += Math.exp(utilities.get(i));
		}
		double currProbability = 0;
		for(int i = 0; i < utilities.size(); i++){
			currProbability += Math.exp(utilities.get(i)) / logsum;
			cumProbabilities.add(currProbability);
		}
		
		return cumProbabilities;
	}

	public ArrayList<Integer> processChoiceSetFromTravelSurvey() {
		// TODO Auto-generated method stub
		ArrayList<Integer> choiceSet = new ArrayList<Integer>();
		for(String header: myAttributes.keySet()){
			//System.out.println(header);
			//System.out.println(header);
			if(header.contains(UtilsTS.alternative)){
				choiceSet.add(Integer.parseInt(myAttributes.get(header)));
			}
		}
		return choiceSet;
	}
	
	public int antitheticDraw(ArrayList<Double> cumulativeProbabilities){

		double randVal = randGen.NextDoubleInRange(0, 1);
		int index =0;
		for(int i =0; i<cumulativeProbabilities.size(); i++){
			if(randVal>cumulativeProbabilities.get(i)){
			}
			else{
				index = i;
				i+=cumulativeProbabilities.size()+1;
			}
		}
		//System.out.println(utilities);
		//System.out.println(cumulativeProbabilities);
		//System.out.println("choice : " + index + " rand value  " + randVal);
		return index;
	}

	public ArrayList<Smartcard> generateChoiceSet(int choiceSetSize) {
		// TODO Auto-generated method stub
		double myZone = Double.parseDouble(myAttributes.get(UtilsSM.zoneId));
		//ArrayList<Integer> myStations = PublicTransitSystem.geoDico.get(myZone);
		//HashMap<Double,ArrayList<Smartcard>> temp = PublicTransitSystem.zonalChoiceSets;
		ArrayList<Smartcard> potentialSmartcard = PublicTransitSystem.zonalChoiceSets.get(myZone);
		ArrayList<Smartcard> agentChoiceSet = new ArrayList<Smartcard>();
		Random random = new Random();
		for(int i = 0; i < choiceSetSize; i++){
			if(potentialSmartcard.size()!=0){
				if(potentialSmartcard.size() > choiceSetSize){
					int nextChoice = random.nextInt(potentialSmartcard.size());
					Smartcard currChoice = potentialSmartcard.get(nextChoice);
					agentChoiceSet.add(currChoice);
				}
			}

			else{
				agentChoiceSet = potentialSmartcard;
			}
		}
		
		Smartcard stayHome = PublicTransitSystem.myCtrlGen.getStayHomeChoice();
		agentChoiceSet.add(stayHome);
		
		return agentChoiceSet;
	}
	
	public ArrayList<Smartcard> generateChoiceSet(int choiceSetSize, ArrayList<Smartcard> mySmartcards) {
		// TODO Auto-generated method stub
		ArrayList<Smartcard> choiceSet = new ArrayList<Smartcard>();
		Random random = new Random();
		for(int i = 0; i < choiceSetSize; i++){
			if(mySmartcards.size()!=0){
				if(mySmartcards.size() > choiceSetSize){
					int nextChoice = random.nextInt(mySmartcards.size());
					Smartcard currChoice = mySmartcards.get(nextChoice);
					choiceSet.add(currChoice);
				}
				else{
					choiceSet = mySmartcards;
				}
			}
			else{
				System.out.println("--in zone " + myAttributes.get(UtilsSM.zoneId) + " there is no smartcards");
			}
		}
		Smartcard stayHome = PublicTransitSystem.myCtrlGen.getStayHomeChoice();
		choiceSet.add(stayHome);
		return choiceSet;
	}
	
	public ArrayList<Smartcard> generateChoiceSet(int choiceSetSize, 
			HashMap<Double,ArrayList<Smartcard>> closeSmartcards){
		// TODO Auto-generated method stub
		
		double myZone = Double.parseDouble(myAttributes.get(UtilsSM.zoneId));
		ArrayList<Smartcard> agentChoiceSet = new ArrayList<Smartcard>();
		//ArrayList<Integer> myStations = PublicTransitSystem.geoDico.get(myZone);
		if(isDistributed){
			Smartcard stayHome = PublicTransitSystem.myCtrlGen.getStayHomeChoice();
			agentChoiceSet.add(stayHome);
		}
		else{
			ArrayList<Smartcard> potentialSmartcard = closeSmartcards.get(myZone);
			
			if(potentialSmartcard.size() > choiceSetSize){
				Random random = new Random();
				for(int i = 0; i < choiceSetSize; i++){
					if(potentialSmartcard.size()!=0){
						int nextChoice = random.nextInt(potentialSmartcard.size());
						Smartcard currChoice = potentialSmartcard.get(nextChoice);
						if(!currChoice.isDistributed){
							if(Utils.occupationCriterion){
								if((Integer.parseInt(myAttributes.get("occ")) == currChoice.fare) ||
										(myAttributes.get("occ").equals("3") && 
												currChoice.fare ==0) ){
									agentChoiceSet.add(currChoice);
								}
								else{
									i= i - 1;
								}
							}
							else{
								agentChoiceSet.add(currChoice);
							}
						}
						else{
							i = i-1;
						}
					}
				}
			}
			
			
			else{
				for(Smartcard currChoice: potentialSmartcard){
					if(!currChoice.isDistributed){
						agentChoiceSet.add(currChoice);
					}
				}
			}
			Smartcard stayHome = PublicTransitSystem.myCtrlGen.getStayHomeChoice();
			agentChoiceSet.add(stayHome);
		}
		
		return agentChoiceSet;
	}
	
	public ArrayList<Smartcard> generateChoiceSet( HashMap<Double,ArrayList<Smartcard>> closeSmartcards){
		// TODO Auto-generated method stub
		
		double myZone = Double.parseDouble(myAttributes.get(UtilsSM.zoneId));
		ArrayList<Smartcard> agentChoiceSet = new ArrayList<Smartcard>();
		//ArrayList<Integer> myStations = PublicTransitSystem.geoDico.get(myZone);
		if(isDistributed){
			Smartcard stayHome = PublicTransitSystem.myCtrlGen.getStayHomeChoice();
			agentChoiceSet.add(stayHome);
		}
		else{
			ArrayList<Smartcard> potentialSmartcard = closeSmartcards.get(myZone);
			
			if(potentialSmartcard.size()!=0){
				for(int i = 0; i < potentialSmartcard.size();i++){
					Smartcard currChoice = potentialSmartcard.get(i);
					if(!currChoice.isDistributed){
						if(Utils.occupationCriterion){
							if((Integer.parseInt(myAttributes.get("occ")) == currChoice.fare) ||
									(myAttributes.get("occ").equals("3") && 
											currChoice.fare ==0) ){
								agentChoiceSet.add(currChoice);
							}
						}
						else{
							agentChoiceSet.add(currChoice);
						}
					}
				}
			}
			Smartcard stayHome = PublicTransitSystem.myCtrlGen.getStayHomeChoice();
			agentChoiceSet.add(stayHome);
		}
		return agentChoiceSet;
	}
	
	

	public double[] writeCosts(int size, int smartcardCount) {
		// TODO Auto-generated method stub
		
		double[] newRow = new double[size];
		//intialization avec une valeur de cout tres elevee
		for(int i = 0; i < size; i++){
			newRow[i] = Double.MAX_VALUE;
		}
		for(BiogemeChoice currChoice: myChoices){
			if(currChoice.getConstantName().equals(UtilsSM.noPt)){
				double stayHomeCost = currChoice.probability;
				stayHomeCost = 1/stayHomeCost;
				for(int i = smartcardCount; i < size; i++){
					newRow[i] = stayHomeCost;
				}
			}
			else{
				newRow[((Smartcard)currChoice).columnId] = 1/currChoice.probability;//maybe not "probability, but something like it.
			}
		}
		return newRow;
	}
	
	public void processSmartcardChoiceSet(ArrayList<Smartcard> choiceSet){
		ArrayList<Double> utilities	 = new ArrayList<Double>();
		for(int i = 0; i < choiceSet.size(); i++){
			
			double utility = 0;
			//int choiceId = choiceSet.get(i).biogeme_group_id;
			BiogemeChoice currChoice = choiceSet.get(i);
			
			for(BiogemeHypothesis currH: BiogemeSimulator.modelHypothesis){
				if(currH.isCst()){
					utility += currH.getCoefficientValue();
				}
				else if(currChoice.isAffected(currH)  && currH.isDummy){
					if( currChoice.isAffecting(currH, this)){
						utility += currH.getCoefficientValue();
					}
				}
				else if(currChoice.isAffected(currH) && !currH.isDummy){
					if(currH.isAgentSpecificVariable){
						String att = UtilsSM.dictionnary.get(currH.affectingDimensionName) ;
						utility += currH.getCoefficientValue() * Double.parseDouble(myAttributes.get(att));
					}
					else if(currH.isAlternatibeSpecificVariable){
						String att = UtilsSM.dictionnary.get(currH.affectingDimensionName);
						utility += currH.getCoefficientValue() * Double.parseDouble(currChoice.myAttributes.get(att));
					}
					else{
						System.out.println(currH.coefName + " was not considered");
					}
					//utility += currH.getCoefficientValue() * currChoice.getAffectingValue(currH, this);
				}
			}
			//######################################
			//ATTENTION CECI N EST PAS BO A VOIR
			if(currChoice.biogeme_group_id == 0){
				utility += BiogemeSimulator.STOnest;
			}
			//######################################
			currChoice.utility = utility;
			utilities.add(utility);
		}
				// TODO Auto-generated method stub
			
		Double logsum = 0.0;
		for(int i = 0; i < choiceSet.size(); i++){
			logsum += Math.exp(choiceSet.get(i).utility);
		}
		double currProbability = 0;
		for(BiogemeChoice currChoice: choiceSet){
			currProbability = Math.exp(currChoice.utility) / logsum;
			currChoice.probability = currProbability;
		}
		myChoices.addAll(choiceSet);
	}
	
	@Deprecated
	public void createAndWeighChoiceSet(int choiceSetSize) {
		// TODO Auto-generated method stub
		//rigth I am just able to apply dummies
		ArrayList<Smartcard> choiceSet = generateChoiceSet(choiceSetSize);
		ArrayList<Double> utilities	 = new ArrayList<Double>();
		for(int i = 0; i < choiceSet.size(); i++){
			
			double utility = 0;
			//int choiceId = choiceSet.get(i).biogeme_group_id;
			BiogemeChoice currChoice = choiceSet.get(i);
			
			for(BiogemeHypothesis currH: BiogemeSimulator.modelHypothesis){
				if(currH.isCst()){
					utility += currH.getCoefficientValue();
				}
				else if(currChoice.isAffected(currH) && currChoice.isAffecting(currH, this) && currH.isDummy){
					utility += currH.getCoefficientValue();
				}
				else if(currChoice.isAffected(currH) && !currH.isDummy){
					utility += currH.getCoefficientValue() * currChoice.getAffectingValue(currH, this);
				}
			}
			currChoice.utility = utility;
			utilities.add(utility);
		}
				// TODO Auto-generated method stub
			
		Double logsum = 0.0;
		for(int i = 0; i < choiceSet.size(); i++){
			logsum += Math.exp(choiceSet.get(i).utility);
		}
		double currProbability = 0;
		for(BiogemeChoice currChoice: choiceSet){
			currProbability = Math.exp(currChoice.utility) / logsum;
			currChoice.probability = currProbability;
		}
		myChoices.addAll(choiceSet);
	}
	
	@Deprecated
	public void createAndWeighChoiceSet(int choiceSetSize,HashMap<Double,ArrayList<Smartcard>> closeSmartcards ) {
		// TODO Auto-generated method stub
		//rigth I am just able to apply dummies
		ArrayList<Smartcard> choiceSet = generateChoiceSet(choiceSetSize, closeSmartcards);
		ArrayList<Double> utilities	 = new ArrayList<Double>();
		for(int i = 0; i < choiceSet.size(); i++){
			
			double utility = 0;
			//int choiceId = choiceSet.get(i).biogeme_group_id;
			BiogemeChoice currChoice = choiceSet.get(i);
			
			for(BiogemeHypothesis currH: BiogemeSimulator.modelHypothesis){
				if(currH.isCst()){
					utility += currH.getCoefficientValue();
				}
				else if(currChoice.isAffected(currH)  && currH.isDummy){
					if( currChoice.isAffecting(currH, this)){
						utility += currH.getCoefficientValue();
					}
				}
				else if(currChoice.isAffected(currH) && !currH.isDummy){
					if(currH.isAgentSpecificVariable){
						String att = UtilsSM.dictionnary.get(currH.affectingDimensionName) ;
						utility += currH.getCoefficientValue() * Double.parseDouble(myAttributes.get(att));
					}
					else if(currH.isAlternatibeSpecificVariable){
						String att = UtilsSM.dictionnary.get(currH.affectingDimensionName);
						utility += currH.getCoefficientValue() * Double.parseDouble(currChoice.myAttributes.get(att));
					}
					else{
						System.out.println(currH.coefName + " was not considered");
					}
					//utility += currH.getCoefficientValue() * currChoice.getAffectingValue(currH, this);
				}
			}
			currChoice.utility = utility;
			utilities.add(utility);
		}
				// TODO Auto-generated method stub
			
		Double logsum = 0.0;
		for(int i = 0; i < choiceSet.size(); i++){
			logsum += Math.exp(choiceSet.get(i).utility);
		}
		double currProbability = 0;
		for(BiogemeChoice currChoice: choiceSet){
			currProbability = Math.exp(currChoice.utility) / logsum;
			currChoice.probability = currProbability;
		}
		myChoices.addAll(choiceSet);
	}
	
	public HashMap<Integer,Double> getChoiceSet(int size, int smartcardCount) {
		// TODO Auto-generated method stub
		HashMap<Integer,Double> newRow = new HashMap<Integer,Double>();
		//intialization avec une valeur de cout tres elevee
		for(BiogemeChoice currChoice: myChoices){
			/*if(currChoice.getConstantName().equals(UtilsTS.noPt)){
				double stayHomeCost = currChoice.probability / (size - smartcardCount);
				stayHomeCost = 1/stayHomeCost;
				for(int i = smartcardCount; i < size; i++){
					newRow.put(i, stayHomeCost)
					newRow[i] = stayHomeCost;
				}
			}
			else{*/
				newRow.put(((Smartcard)currChoice).columnId , 1/currChoice.probability);
				//newRow[((Smartcard)currChoice).columnId] = 1/currChoice.probability;//maybe not "probability, but something like it.
			//}
		}
		return newRow;
	}


	
	

	

}
