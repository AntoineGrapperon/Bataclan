/**
 * 
 */
package ActivityChoiceModel;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import Smartcard.PublicTransitSystem;
import Smartcard.Smartcard;
import Smartcard.UtilsSM;
import Utils.InputDataReader;
import Utils.OutputFileWritter;
import Utils.Utils;

/**
 * @author Antoine
 *
 */
public class BiogemeControlFileGenerator {
	InputDataReader descReader = new InputDataReader();
	InputDataReader hypothesisReader = new InputDataReader();
	OutputFileWritter myDataWriter =  new OutputFileWritter();
    HashMap<String, Integer> choiceDimensions = new HashMap<String,Integer>();
    ArrayList<String> order = new ArrayList<String>();
    ArrayList<HashMap<String,Integer>> combinations = new ArrayList<HashMap<String,Integer>>();
    public static ArrayList<BiogemeHypothesis> hypothesis = new ArrayList<BiogemeHypothesis>();
    public static ArrayList<BiogemeChoice> choiceIndex = new ArrayList<BiogemeChoice>();
    
    public BiogemeControlFileGenerator(){
    	
    }
    
    public void initialize(String pathControleFile, String pathToHypothesis) throws IOException{
    	descReader.OpenFile(pathControleFile);
    	hypothesisReader.OpenFile(pathToHypothesis);
    	choiceDimensions = getTripChainAlternatives();
    	hypothesis = getHypothesis();
    	generateCombinations();
    	choiceIndex.addAll(createChoiceIndex());
    }
    
  private ArrayList<BiogemeHypothesis> getHypothesis() throws NumberFormatException, IOException {
		// TODO Auto-generated method stub
	  ArrayList<BiogemeHypothesis> answer = new ArrayList<BiogemeHypothesis> ();
	  String strTok;
	  while ((strTok = hypothesisReader.myFileReader.readLine()) != null){
		  String[] tok = strTok.split(Utils.COLUMN_DELIMETER);
		  BiogemeHypothesis currHypothesis = new BiogemeHypothesis();
		  
		  
		  boolean isDummy = tok[3].trim().equals("dummy");
		  boolean isAgent = tok[3].trim().equals("agent");
		  boolean isAlternative = tok[3].trim().equals("alternative");
		  
		  currHypothesis.isDummy = isDummy;
		  currHypothesis.isAgentSpecificVariable = isAgent;
		  currHypothesis.isAlternativeSpecificVariable = isAlternative;
		  String coefName = tok[0].trim();
		  currHypothesis.setCoefName(coefName);
		  
		  if(isDummy){
			  String[] affectingTok = tok[1].split("=");
			  String dimName = affectingTok[0].trim();
			  String[]affectingCategoriesTok =affectingTok[1].split("-");
			  ArrayList<Integer> categories = new ArrayList<Integer>();
			  for(String e: affectingCategoriesTok){
				  categories.add(Integer.parseInt(e.trim()));
			  }
			  currHypothesis.setAffectingDimension(dimName, categories);
		  }
		  
		  else if(!isDummy){
			  String[] affectingTok = tok[1].split("=");
			  String dimName = affectingTok[0].trim();
			  currHypothesis.setAffectingDimension(dimName, null);
		  }
		  
		  String[] affectedTok = tok[2].split("=");
		  String dimName = affectedTok[0].trim();
		  String[]affectedCategoriesTok = affectedTok[1].split("-");
		  ArrayList<Integer> categories = new ArrayList<Integer>();
		  for(String e: affectedCategoriesTok){
			  categories.add(Integer.parseInt(e.trim()));
		  }
		  currHypothesis.setAffectedDimension(dimName, categories);
		  answer.add(currHypothesis);
	  }
	  
		return answer;
	}

    public HashMap<String, Integer> getTripChainAlternatives() throws IOException{
    	HashMap<String, Integer> answer = new HashMap<String, Integer>();
    	String strTok;
    	while ((strTok = descReader.myFileReader.readLine()) != null){
    		String[] tok = strTok.split(Utils.COLUMN_DELIMETER);
    		answer.put(tok[0], Integer.parseInt(tok[1].trim()));
        }
    	return answer;
    }
    
    public void generateBiogemeControlFile(String pathOutput) throws IOException{
    	myDataWriter.OpenFile(pathOutput);
    	writeUpperPart();
    	writeBetaPart();
    	writeCombinations();
    	writeExpressionsExclusions();
    	writeStructure();
    	descReader.CloseFile();
    	myDataWriter.CloseFile();
    }
    
    private void writeStructure() throws IOException {
		// TODO Auto-generated method stub
    	myDataWriter.WriteToFile("[Model]");
    	//myDataWriter.WriteToFile("$MNL");
    	myDataWriter.WriteToFile("$NL");
    	myDataWriter.WriteToFile("[NLNests]");
    	
    	String casesCar = new String();
    	String casesPass = new String();
    	String casesSto = new String();
    	String casesPt = new String();
    	String casesActive = new String();
    	ArrayList<Integer> alreadyWritten = new ArrayList<Integer>();
    	
    	for(BiogemeChoice c: choiceIndex){
    		if(!alreadyWritten.contains(c.biogeme_case_id)){
    			if(c.nest.equals(UtilsTS.carDriver)){casesCar+= c.biogeme_case_id + " ";}
        		else if(c.nest.equals(UtilsTS.carPassenger)){casesPass+= c.biogeme_case_id + " ";}
        		else if(c.nest.equals(UtilsTS.stoUser)){casesSto+= c.biogeme_case_id + " ";}
        		else if(c.nest.equals(UtilsTS.ptUserNoSto)){casesPt+= c.biogeme_case_id + " ";}
        		else if(c.nest.equals(UtilsTS.activeMode)){casesActive+= c.biogeme_case_id + " ";}
        		alreadyWritten.add(c.biogeme_case_id);
    		}
    	}
    	myDataWriter.WriteToFile(UtilsTS.carDriver + "    1.0     1.0   10.0   1     " + casesCar);
    	myDataWriter.WriteToFile(UtilsTS.carPassenger + "      1.0     1.0   10.0   1     " + casesPass);
    	myDataWriter.WriteToFile(UtilsTS.stoUser + "      7.5     1.0   10.0   1     " + casesSto);	
    	myDataWriter.WriteToFile(UtilsTS.ptUserNoSto + "      1.0     1.0   10.0   1     " + casesPt);	
    	myDataWriter.WriteToFile(UtilsTS.activeMode + "      1.0     1.0   10.0   1     " + casesActive);	
	}

	private void writeBetaPart() throws IOException {
		// TODO Auto-generated method stub
    	myDataWriter.WriteToFile("[Beta]");
    	writeConstants();
    	writeHypothesisBeta();
	}

	public void generateCombinations() throws IOException{
    	for(String key: choiceDimensions.keySet()){
    		updateCombinations(key);
    	}
    }
    
    public void writeConstants() throws IOException{
    	String headers = "// ";
    	for(String key: combinations.get(0).keySet()){
			headers+= "_"+key;
		}
    	myDataWriter.WriteToFile(headers);	
    	
    	ArrayList<String> alreadyWritten = new ArrayList<String>();
    	Iterator<BiogemeChoice> it = choiceIndex.iterator();
    	while(it.hasNext()){
    		BiogemeChoice currChoice = it.next();
    		if(!(currChoice.biogeme_case_id == -1)){
    			String choiceName = currChoice.getConstantName();
        		if(!alreadyWritten.contains(choiceName)){
        			alreadyWritten.add(choiceName);
        			if(choiceName.equals("C_" + UtilsTS.carDriver)){
        				myDataWriter.WriteToFile(choiceName + " 	    1          -10.0     2.0         1");	
        			}
        			else{
        				myDataWriter.WriteToFile(choiceName + " 	    0.0          -10.0     2.0         0");	
        			}
        		}
    		}
    	}
    	
    	/*
    	Iterator<HashMap<String,Integer>> it = combinations.iterator();
		while(it.hasNext()){
			
			HashMap<String,Integer> currCombination = it.next();
			String output = new String();
			String testhead = new String();
			
			if(!first){
				for(String key: currCombination.keySet()){
					testhead += " - " + key;
					order.add(key);
				}
				myDataWriter.WriteToFile("// " + testhead);
				first = true;
			}
			
			if(shouldWrite(currCombination, home, ptUser)){
				for(String key: currCombination.keySet()){
					output+= "_"+currCombination.get(key);
				}
				myDataWriter.WriteToFile("C" + output + " 	    0.0          -10.0     10.0         0");	
			}
		}*/
    }
    
    /*private boolean shouldWrite(HashMap<String, Integer> currCombination, String home, String ptUser){
    	boolean answer = false;
    	if(currCombination.get(Utils.nAct) == 0 && home.equals("0")){
			home = "1";
			answer = true;
		}
		else if(currCombination.get(Utils.nAct)!= 0 && currCombination.get(Utils.fidelPtRange)==0 && ptUser.equals("0")){
			ptUser = "1";
			answer = true;
		}
		else if(currCombination.get(Utils.nAct)!=0 && currCombination.get(Utils.fidelPtRange)!=0){
			answer = true;
		}
    	return answer;
    }*/
    
    public void writeHypothesisBeta() throws IOException{
    	for(BiogemeHypothesis h: hypothesis){
    		myDataWriter.WriteToFile(h.coefName + " 	    0.0          -5.0     5.0         0");
    	}
    }
    
   /* public String processChoice(HashMap<String,Integer> combination, HashMap<String, Integer> dictionnary){
	
		String ref = new String();
		
		if((int)combination.get(Utils.nAct) == 0){
			ref = Utils.stayedHome;
		}
		else if(combination.get(Utils.fidelPtRange)==0){
			ref = Utils.noPT;
		}
		else{
			Iterator<String> it = order.iterator();
			while(it.hasNext()){
				String curr = it.next();
				ref+=combination.get(curr);
			}
		}
		String choice = Integer.toString(dictionnary.get(ref));
		return choice;
	}*/
    
    private void writeCombinations() throws IOException{

    	myDataWriter.WriteToFile("[Utilities]");
    	ArrayList<String> alreadyWritten = new ArrayList<String>();
    	Iterator<BiogemeChoice> it = choiceIndex.iterator();
    	while(it.hasNext()){
    		BiogemeChoice currChoice = it.next();
    		String choiceName = currChoice.getConstantName();
    		Integer choiceId = currChoice.biogeme_case_id;
    		String output = new String();
    		if(choiceId == -1){
    			
    		}
    		else if(!alreadyWritten.contains(choiceName) && choiceName.equals("C_" + UtilsTS.carDriver)){
    			alreadyWritten.add(choiceName);
    			output = choiceId + "	" 
    					+ choiceName + "	avail1 " 
    					+ choiceName + " * one" 
    					+ addCoefficients(currChoice.choiceCombination);	
    			myDataWriter.WriteToFile(output);
    		}
    		else if(!alreadyWritten.contains(choiceName)){
    			alreadyWritten.add(choiceName);
    			output = choiceId + "	" 
    					+ choiceName + "	avail " 
    					+ choiceName + " * one" 
    					+ addCoefficients(currChoice.choiceCombination);	
    			myDataWriter.WriteToFile(output);
    		}
    	}
    }
    
    private String addCoefficients(HashMap<String, Integer> currCombination) {
		// TODO Auto-generated method stub
    	String output = new String();
    	for(BiogemeHypothesis e: hypothesis){
    		String affectedDim = e.affectedDimensionName;
    		ArrayList<Integer> affectedCategories = e.affectedCategories;
    		int category = currCombination.get(affectedDim);
    		boolean requiresCoefficient = false;
    		
    		/*if(currCombination.get(UtilsTS.fidelPtRange) == 0 || currCombination.get(UtilsTS.nAct) == 0){
    			if(affectedDim.equals(UtilsTS.fidelPtRange) && category == 0){
    				for(int i: affectedCategories){
    	    			if(i == category){
    	    				requiresCoefficient = true;
    	    			}
    	    		}
    			}
    		}
    		else{
    			for(int i: affectedCategories){
        			if(i == category){
        				requiresCoefficient = true;
        			}
        		}
    		}*/
    		
    		if(currCombination.get(UtilsTS.nest) == 2){
    			requiresCoefficient = shouldWrite(affectedCategories, category);
    		}
    		else{
    			if(affectedDim.equals(UtilsTS.nest)){
    				requiresCoefficient = shouldWrite(affectedCategories, category);
    			}
    		}
    		
    		if(requiresCoefficient){
    			//output += " + " + e.coefName + " * " + e.coefName + UtilsTS.var;
    			if(e.isDummy){
    				output += " + " + e.coefName + " * " + getDummyName(e);
    			}
    			else if(!e.isDummy){
    				output += " + " + e.coefName + " * " + e.affectingDimensionName;
    			}
    		}
    	}
		return output;
	}
    
    
    
    private boolean shouldWrite(ArrayList<Integer> affectedCategories, int category) {
		// TODO Auto-generated method stub
    	for(int i: affectedCategories){
			if(i == category){
				return true;
			}
		}
		return false;
	}

	private String getDummyName(BiogemeHypothesis hypo){
    	String addendum = new String();
    	addendum+= hypo.affectingDimensionName;
    	for(int i : hypo.affectingCategories){
    		addendum+= Integer.toString(i);
    	}
    	return addendum;
    }

	private void updateCombinations(String key) {

		// TODO Auto-generated method stub
    	ArrayList<HashMap<String,Integer>> tempChoices = new ArrayList<HashMap<String, Integer>>();
    	if(combinations.isEmpty()){
    		for(int i = 0; i < choiceDimensions.get(key); i++){
    			HashMap<String,Integer> nextChoice = new HashMap<String,Integer>();
    			nextChoice.put(key, i);
				tempChoices.add(nextChoice);
			}
    	}
    	else{
    		
    		Iterator<HashMap<String,Integer>> it = combinations.iterator();
    		while(it.hasNext()){
    			HashMap<String,Integer> currChoice = it.next();
    			
    			for(int i = 0; i < choiceDimensions.get(key); i++){
    				HashMap<String,Integer> nextChoice = new HashMap<String,Integer>(currChoice);
    				nextChoice.put(key, i);
    				tempChoices.add(nextChoice);
    			}
    		}
    	}
    	combinations.removeAll(combinations);
		combinations.addAll(tempChoices);
	}

	
	 public ArrayList<BiogemeChoice> createChoiceIndex(){
		ArrayList<BiogemeChoice> choiceIndex = new ArrayList<BiogemeChoice>();
    	//boolean home = false;
    	boolean carDriver = false;
    	boolean carPass = false;
    	boolean sto = false;
    	boolean ptNonSto = false;
    	boolean activeMode = false;
    	
    	
    	Iterator<HashMap<String,Integer>> it = combinations.iterator();
    	int combinationId = 0;
    	int carDriverId = 0;
    	int carPassId = 0;
    	int stoId = 0;
    	int ptNonStoId = 0;
    	int activeModeId = 0;
    	//int combinationGroupHome = 0;
    	
		while(it.hasNext()){
			
			HashMap<String,Integer> currCombination = it.next();
			String ref = new String();
			BiogemeChoice currChoice = new BiogemeChoice();
			
			if(currCombination.get(UtilsTS.nest) == 0){
				currChoice.biogeme_id = combinationId;
				currChoice.choiceCombination = currCombination;
				currChoice.nest = UtilsTS.carDriver;
				if(!carDriver){
					carDriver = true;
					carDriverId = combinationId;
					currChoice.biogeme_case_id = carDriverId;
				}
				else{
					currChoice.biogeme_case_id = carDriverId;
				}
				combinationId++;
				
			}
			else if(currCombination.get(UtilsTS.nest) == 1){
				currChoice.biogeme_id = combinationId;
				currChoice.choiceCombination = currCombination;
				currChoice.nest = UtilsTS.carPassenger;
				if(!carPass){
					carPass = true;
					carPassId = combinationId;
					currChoice.biogeme_case_id = carPassId;
				}
				else{
					currChoice.biogeme_case_id = carPassId;
				}
				combinationId++;
				
			}
			else if(currCombination.get(UtilsTS.nest) == 2 && 
					currCombination.get(UtilsTS.nAct) != 0 && 
					currCombination.get(UtilsTS.fidelPtRange)!=0){
				currChoice.biogeme_id = combinationId;
				currChoice.choiceCombination = currCombination;
				currChoice.nest = UtilsTS.stoUser;
				if(!sto){
					sto = true;
					stoId = combinationId;
					currChoice.biogeme_case_id = stoId;
				}
				else{
					currChoice.biogeme_case_id = combinationId; //for STO nest, sub choices are not aggregated
				}
				combinationId++;
				
			}
			else if(currCombination.get(UtilsTS.nest) == 3){
				currChoice.biogeme_id = combinationId;
				currChoice.choiceCombination = currCombination;
				currChoice.nest = UtilsTS.ptUserNoSto;
				if(!ptNonSto){
					ptNonSto = true;
					ptNonStoId = combinationId;
					currChoice.biogeme_case_id = ptNonStoId;
				}
				else{
					currChoice.biogeme_case_id = ptNonStoId;
				}
				combinationId++;
				
			}
			else if(currCombination.get(UtilsTS.nest) == 4){
				currChoice.biogeme_id = combinationId;
				currChoice.choiceCombination = currCombination;
				currChoice.nest = UtilsTS.activeMode;
				if(!activeMode){
					activeMode = true;
					activeModeId = combinationId;
					currChoice.biogeme_case_id = activeModeId;
				}
				else{
					currChoice.biogeme_case_id = activeModeId;
				}
				combinationId++;
			}
			
			/*if(currCombination.get(UtilsTS.nAct) == 0 || currCombination.get(UtilsTS.fidelPtRange)==0){
				currChoice.biogeme_id = combinationId;
				currChoice.choiceCombination = currCombination;
				if(!home){
					home = true;
					combinationGroupHome = combinationId;
					currChoice.biogeme_group_id = combinationGroupHome;
				}
				else{
					currChoice.biogeme_group_id = combinationGroupHome;
				}
				combinationId++;
			}
			/*else if(currCombination.get(UtilsTS.nAct)!= 0 && currCombination.get(UtilsTS.fidelPtRange)==0){
				currChoice.biogeme_id = combinationId;
				currChoice.choiceCombination = currCombination;
				if(!pt){
					pt = true;
					combinationGroupPt = combinationId;
					currChoice.biogeme_group_id = combinationGroupPt;
				}
				else{
					currChoice.biogeme_group_id = combinationGroupPt;
				}
				combinationId++;
			}
			else if(currCombination.get(UtilsTS.nAct)!=0 && currCombination.get(UtilsTS.fidelPtRange)!=0){
				currChoice.biogeme_id = combinationId;
				currChoice.choiceCombination = currCombination;
				currChoice.biogeme_group_id = combinationId;
				combinationId++;
			}*/
			else{
				
				System.out.println("there was a problem in the index generation, case was labeled -1");
				currChoice.biogeme_id = combinationId;
				currChoice.choiceCombination = currCombination;
				currChoice.biogeme_case_id = -1;
				currChoice.nest = "-1";
				combinationId++;
			}
			choiceIndex.add(currChoice);
		}
    	return choiceIndex;
    }
	 
	 public static int returnChoiceId(HashMap<String, Integer> myCombinationChoice) {
			// TODO Auto-generated method stub
			for(BiogemeChoice currChoice: BiogemeControlFileGenerator.choiceIndex){
				if(areEquals(currChoice.choiceCombination,myCombinationChoice)){
					return currChoice.biogeme_case_id;
				}
			}
			System.out.println("--error: combination index was not found for code: " + myCombinationChoice.toString());
			return 0;
		}
		
		public static boolean areEquals(HashMap<String,Integer> m1,HashMap<String,Integer> m2){
			for(String key: m1.keySet()){
				if(!m2.containsKey(key)){
					System.out.println("mapping problem: key " + key + " is not in " + m2.keySet());
					return false;
				}
				if(m1.get(key) != m2.get(key)){
					return false;
				}
			}
			return true;
		}
	
    /*public HashMap<String, Integer> getCombinations(){
    	HashMap<String, Integer> dictionnary = new HashMap<String,Integer>();
    	
    	Iterator<HashMap<String,Integer>> it = combinations.iterator();
    	int n = 0;
    	boolean home = false;
    	boolean ptUser = true;
    	
		while(it.hasNext()){
			
			HashMap<String,Integer> currCombination = it.next();
			String ref = new String();
			
			if(currCombination.get(Utils.nAct) == 0 && !home){
				ref = Utils.stayedHome;
				dictionnary.put(ref, n);
				n++;
				home = true;
			}
			else if(currCombination.get(Utils.nAct)!= 0 && currCombination.get(Utils.fidelPtRange)==0 && ptUser){
				ref = Utils.noPT;
				dictionnary.put(ref, n);
				n++;
				ptUser = false;
			}
			else if(currCombination.get(Utils.nAct)!=0 && currCombination.get(Utils.fidelPtRange)!=0){
				for(String key: currCombination.keySet()){
					ref += Integer.toString(currCombination.get(key));
				}
				dictionnary.put(ref, n);
				n++;
			}
			else{
				n++;
			}
		}
    	System.out.println(dictionnary.toString());
    	return dictionnary;
    }*/
    
    public void writeUpperPart() throws IOException{
    	myDataWriter.WriteToFile("//Auto generated control file to use with Biogeme for windows");
    	myDataWriter.WriteToFile("//Author: Antoine Grapperon, antoine.grapperon@free.fr");
    	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    	Date date = new Date();
    	myDataWriter.WriteToFile("//date: " + dateFormat.format(date)); 
    	myDataWriter.WriteToFile("[Choice]\r\n" + 
    			UtilsTS.alternative);
    	
    }
    
    public void writeExpressionsExclusions() throws IOException{
    	myDataWriter.WriteToFile("[Expressions] ");
    	myDataWriter.WriteToFile("one = 1 ");
    	myDataWriter.WriteToFile("avail = 1 ");
    	myDataWriter.WriteToFile("//avail = (accessIndicator == 1 ) ");
    	myDataWriter.WriteToFile("freq1 = log(1 + freq ) ");
    	myDataWriter.WriteToFile("avail1 = (GRPAGE != 0) * (NBVEH != 0 )");
    	writeDummies();
    	/*myDataWriter.WriteToFile("EARLY_WORKER_var = (OCCUP == 0 ) * (FIRST_DEPShort * 0 )");
    	myDataWriter.WriteToFile("RETIRE_FIRST_DEP_var = (OCCUP == 2 ) * (FIRST_DEPShort * 2 )");
    	myDataWriter.WriteToFile("MOTOR_var = MOTOR )");*/
    	myDataWriter.WriteToFile("[Exclude]");
    	//myDataWriter.WriteToFile("(GRPAGE == 0) >= 1");
    	myDataWriter.WriteToFile("(OCCUP == -1) + (CHOICE == -1 ) >= 1");
    			
    	//myDataWriter.WriteToFile("((P_GRAGE == 1) + (P_STATUT == 6) + (P_STATUT == 8) + (P_STATUT == 5) + (N_ACT == 0))  >= 1  //+ ((P_STATUT != 1) + (P_STATUT != 2)) / 2)");
    	
    }
    

	private void writeDummies() throws IOException {
		// TODO Auto-generated method stub
		for(BiogemeHypothesis currH : hypothesis){
			if(currH.isDummy){
				String newExpression = getDummyName(currH) + " = ";
				for(int i : currH.affectingCategories){
					newExpression+= " (" + currH.affectingDimensionName + " == " + i + " ) +";
				}
				newExpression = newExpression.trim().substring(0, newExpression.length() -1);
				myDataWriter.WriteToFile(newExpression);
			}
		}
	}

	public void printChoiceIndex(String path) throws IOException{
		OutputFileWritter tempWriter = new OutputFileWritter();
		tempWriter.OpenFile(path);
		for(BiogemeChoice c : choiceIndex){
			tempWriter.WriteToFile(c.toString());
		}
		tempWriter.CloseFile();
	}
	
	public ArrayList<Smartcard> getNestsChoice(){
		int count = 0;
		ArrayList<Smartcard> nestsChoices = new ArrayList<Smartcard>();
		for(BiogemeChoice temp: BiogemeSimulator.modelChoiceUniverse){
			if(temp.getConstantName().equals(UtilsTS.carDriver)||
					temp.getConstantName().equals(UtilsTS.carPassenger)||
					temp.getConstantName().equals(UtilsTS.ptUserNoSto)||
					temp.getConstantName().equals(UtilsTS.activeMode)){
				Smartcard answer = new Smartcard(temp);
				answer.columnId = PublicTransitSystem.mySmartcards.size() + count;
				count++;
				nestsChoices.add(answer);
			}
		}
		return nestsChoices;
	}

	public Smartcard getStayHomeChoice() {
		// TODO Auto-generated method stub
		for(BiogemeChoice temp : choiceIndex){
			if(temp.getConstantName().equals(UtilsSM.noPt)){
				Smartcard answer = new Smartcard(temp);
				answer.columnId = PublicTransitSystem.mySmartcards.size();
				return answer;
			}
		}
		return null;
	}
}
