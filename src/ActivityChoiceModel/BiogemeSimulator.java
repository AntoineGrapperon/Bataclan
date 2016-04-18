/**
 * 
 */
package ActivityChoiceModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;

import Smartcard.Smartcard;
import Smartcard.UtilsSM;
import Utils.InputDataReader;
import Utils.OutputFileWritter;
import Utils.Utils;

/**
 * @author Antoine
 *
 */
public class BiogemeSimulator {

	public static BiogemeControlFileGenerator myCtrlGen;
	
	InputDataReader myReader = new InputDataReader();
	OutputFileWritter myOutputFileWriter = new OutputFileWritter();
	ArrayList<BiogemeAgent> myPopulationSample = new ArrayList<BiogemeAgent>();
	public static ArrayList<BiogemeHypothesis> modelHypothesis = new ArrayList<BiogemeHypothesis>();
	public static ArrayList<BiogemeChoice> modelChoiceUniverse = new ArrayList<BiogemeChoice>();
	public static HashMap<String, Double> nests = new HashMap<String, Double>();
	
	//public static double stoScale = 1;
	//public static double noPtScale = 1;
	
	/**
	 * choiceUniverse is required only for simulating the model with smart card data. For simulating the model with the travel survey, alternatives are computed a bit differently.
	 */
	//HashMap<String, BiogemeChoice> choiceUniverse = new HashMap<String, BiogemeChoice>();
	
	public BiogemeSimulator(){
	}
	
	public BiogemeSimulator(String pathControleFile, String pathOutput, String pathHypothesis) throws IOException{
		myCtrlGen.generateBiogemeControlFile(pathOutput);
		myCtrlGen.initialize(pathControleFile, pathHypothesis);
	}
	
	public BiogemeSimulator(BiogemeControlFileGenerator ctrlGen){
		myCtrlGen = ctrlGen;
		extractChoiceUniverse();
	}
	
	public void setHypothesis(){
		addHypothesis(BiogemeControlFileGenerator.hypothesis);
		ArrayList<BiogemeHypothesis> constants = generateConstantHypothesis();
		addHypothesis(constants);
	}
	
	
	public ArrayList<BiogemeAgent> initialize(String path ) throws IOException{
		myReader.OpenFile(path);
		createAgents();
		System.out.println("--agents created");
		addHypothesis(myCtrlGen.hypothesis);
		ArrayList<BiogemeHypothesis> constants = generateConstantHypothesis();
		addHypothesis(constants);
		return myPopulationSample;
	}
	
	
	private ArrayList<BiogemeHypothesis> generateConstantHypothesis() {
		// TODO Auto-generated method stub
		ArrayList<BiogemeHypothesis> constants = new ArrayList<BiogemeHypothesis>();
		/*boolean noPt = false;
		for(BiogemeChoice currChoice: BiogemeControlFileGenerator.choiceIndex){
			BiogemeHypothesis currHypothesis = new BiogemeHypothesis();
			String currCstName = currChoice.getConstantName();
			if(!currCstName.equals("C_NOPT")){
				currHypothesis.setCoefName(currCstName);
				constants.add(currHypothesis);
			}
			else if(currCstName.equals("C_NOPT") && !noPt){
				currHypothesis.setCoefName(currCstName);
				constants.add(currHypothesis);
				noPt = true;
			}
			else{
			}
			
			/*for(String dim: currChoice.choiceCombination.keySet()){
				ArrayList<Integer> category = new ArrayList<Integer>();
				category.add(currChoice.choiceCombination.get(dim));
				currHypothesis.setAffectedDimension(dim, category);
			}*/
			
		//}
	for(BiogemeChoice currChoice: modelChoiceUniverse){
			BiogemeHypothesis currHypothesis = new BiogemeHypothesis();
			String currCstName = currChoice.getConstantName();
			currHypothesis.setCoefName(currCstName);
			constants.add(currHypothesis);		
		}
		return constants;
	}

	public void applyModelOnTravelSurveyPopulation(String outputPath, int mode, boolean useAge) throws IOException{
		int n = 0;
		int N = myPopulationSample.size();
		for(BiogemeAgent person: myPopulationSample){
			ArrayList<BiogemeChoice> choiceSet = new ArrayList<BiogemeChoice>();
			if(mode == 1){choiceSet = modelChoiceUniverse;}
			else if(mode == 2){
				System.out.println("this was not coded");
				choiceSet = person.generateChoiceSetFromTravelSurvey();}
			else if(mode == 3){choiceSet = person.generateChoiceSetFromTravelSurveyCHEAT();}
			
			if(useAge){
				choiceSet = person.restrainChoiceSet(choiceSet);
			}
			person.applyModel(choiceSet);
			//applyPostTreatment(person);
			n++;
			if(n%1000 == 0){
				System.out.println("-- " + n + " agents were processed out of " + N);
			}
		}
		
		myOutputFileWriter.OpenFile(outputPath);
		String headers = "Observed choice, Simulated choice, Age, Sex, Occup,Cars, Pers, Weight";
		myOutputFileWriter.WriteToFile(headers);
		for(BiogemeAgent person: myPopulationSample){
			String newLine = getChoiceName(person.myAttributes.get(UtilsTS.alternative)) + 
					Utils.COLUMN_DELIMETER +getChoiceName(person.myAttributes.get(UtilsTS.sim)) +
					Utils.COLUMN_DELIMETER + person.myAttributes.get(UtilsTS.ageGroup) +
					Utils.COLUMN_DELIMETER + person.myAttributes.get(UtilsTS.sex) +
					Utils.COLUMN_DELIMETER + person.myAttributes.get(UtilsTS.occupation) +
					Utils.COLUMN_DELIMETER + person.myAttributes.get(UtilsTS.cars) +
					Utils.COLUMN_DELIMETER + person.myAttributes.get(UtilsTS.pers) +
					Utils.COLUMN_DELIMETER + person.myAttributes.get(UtilsTS.weigth);
			myOutputFileWriter.WriteToFile(newLine);
		}
		myOutputFileWriter.CloseFile();
	}
	
	/*public void applyModelOnSmartcard(String outputPath) throws IOException{
		int n = 0;
		int N = myPopulationSample.size();
		
		for(BiogemeAgent person: myPopulationSample){
			ArrayList<Smartcard> choiceSet = person.processChoiceSetFromSmartcard(UtilsSM.choiceSetSize);
			person.applyModelSmartcard(choiceSet);
			n++;
			if(n%1000 == 0){System.out.println("-- " + n + " agents were processed out of " + N);}
		}
		writeSimulationResults(outputPath);
	}*/
	

	

	private void applyPostTreatment(BiogemeAgent person) {
		// TODO Auto-generated method stub
			// TODO Auto-generated method stub
		int choiceId = Integer.parseInt(person.myAttributes.get(UtilsTS.sim));
		BiogemeChoice tempChoice = getChoice(choiceId);
		if(tempChoice.nest.equals(UtilsTS.stoUser) && person.myAttributes.get(UtilsTS.ageGroup).equals("0")){
			Random r = new Random();
			int rInt = r.nextInt(15);
			if((rInt <= 5)){
				int ptCaseId = getCaseId("C_" + UtilsTS.ptUserNoSto);
				person.myAttributes.put(UtilsTS.sim, Integer.toString(ptCaseId));
			}
		}
		
		if((person.myAttributes.get(UtilsTS.ageGroup).equals("5") ||
				person.myAttributes.get(UtilsTS.ageGroup).equals("6"))
				&& tempChoice.nest.equals(UtilsTS.stoUser) ){
			Random r = new Random();
			int rInt = r.nextInt(10);
			if(rInt < 5){
				int ptCaseId = getCaseId("C_" + UtilsTS.carPassenger);
				person.myAttributes.put(UtilsTS.sim, Integer.toString(ptCaseId));
			}
		}
	}

	private int getCaseId(String caseName) {
		// TODO Auto-generated method stu
		for(BiogemeChoice curChoice: modelChoiceUniverse){
			if(curChoice.getConstantName().equals(caseName)){
				return curChoice.biogeme_case_id;
			}
		}
		return -1;
	}

	private String getChoiceName(String string) {
		// TODO Auto-generated method stub
		for(BiogemeChoice temp: myCtrlGen.choiceIndex){
			if(temp.biogeme_case_id == Integer.parseInt(string)){
				return temp.getConstantName();
			}
		}
		return null;
	}
	
	public static BiogemeChoice getChoice(int groupId) {
		// TODO Auto-generated method stub
		for(BiogemeChoice temp: modelChoiceUniverse){
			if(temp.biogeme_case_id == groupId){
				return temp;
			}
		}
		return null;
	}

	private void addHypothesis(ArrayList<BiogemeHypothesis> hypothesis) {
		// TODO Auto-generated method stub
		modelHypothesis.addAll(hypothesis);
	}

	public void importBiogemeModel(String path) throws IOException {
		// TODO Auto-generated method stub
		InputDataReader modelReader = new InputDataReader();
		modelReader.OpenFile(path);
		ArrayList <String> lines = modelReader.StoreLineByLine();
		String[] list;
		int cur = 0;
		while(!(lines.get(cur).trim().equals("END"))){
			cur++;
		}
		cur++;
	 	while(!(lines.get(cur).trim().equals("-1"))){
	 		String[] strTok = lines.get(cur).split("\\t");
	 		String coefName = strTok[1].trim();
	 		double coefValue = Double.parseDouble(strTok[3]);
	 		updateHypothesis(coefName,coefValue);
	 		cur++;
	 	}
	}
	
	public void importNest(String path) throws IOException {
		// TODO Auto-generated method stub
		InputDataReader modelReader = new InputDataReader();
		modelReader.OpenFile(path);
		ArrayList <String> lines = modelReader.StoreLineByLine();
		String[] list;
		int cur = 0;
		while(!(lines.get(cur).trim().equals("END"))){
			cur++;
		}
		cur++;
	 	while(!(lines.get(cur).trim().equals("-1"))){
	 		String[] strTok = lines.get(cur).split("\\t");
	 		String coefName = strTok[1].trim();
	 		double coefValue = Double.parseDouble(strTok[3]);
	 		updateNest(coefName,coefValue);
	 		cur++;
	 	}
	}
	
	public void extractChoiceUniverse() {
		// TODO Auto-generated method stub
		HashMap<Integer, Boolean> list = new HashMap<Integer,Boolean>();
		ArrayList<BiogemeChoice> choiceUniverse = new ArrayList<BiogemeChoice>();
		for(BiogemeChoice curChoice: BiogemeControlFileGenerator.choiceIndex){
			if(!list.containsKey(curChoice.biogeme_case_id) &&
					curChoice.biogeme_case_id != -1){
				list.put(curChoice.biogeme_case_id, true);
				choiceUniverse.add(curChoice);
			}
		}
		modelChoiceUniverse=choiceUniverse;
	}
	


	
	public void createAgents() throws IOException
    {
    	 ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
    	 data = getData();
    	 
    	 ArrayList<String> decisionMakerAttributes = data.get(0);

    	 for (int i=1; i<data.size(); i++)
    	 {
    			//for (int j=0; j<decisionMakerAttributes.size();j++)
    			//{
    				BiogemeAgent newDecisionMaker = new BiogemeAgent();
    				newDecisionMaker.setAttributes(decisionMakerAttributes, data.get(i));
    				myPopulationSample.add(newDecisionMaker);
    			//}
    	 }
    }
	
	
	public ArrayList<ArrayList<String>> getData() throws IOException
    {
    	String line=null;
    	Scanner scanner = null;
    	ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();

    		int i=0;
    		while((line=myReader.myFileReader.readLine())!= null)
    		{
    			data.add(new ArrayList<String>());
    			scanner = new Scanner(line);
    			scanner.useDelimiter(",");

    				while (scanner.hasNext())
    				{
    					String dat = scanner.next();
    					data.get(i).add(dat);
    				}
    				i++;
    		}
    	return data;
    }
	
	@Deprecated
	public ArrayList<ArrayList<String>> getData(int nMax) throws IOException
    {
    	String line=null;
    	Scanner scanner = null;
    	ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();

    		int i=0;
    		
    		while((line=myReader.myFileReader.readLine())!= null)
    		{
    			data.add(new ArrayList<String>());
    			scanner = new Scanner(line);
    			scanner.useDelimiter(",");

    				while (scanner.hasNext())
    				{
    					String dat = scanner.next();
    					data.get(i).add(dat);
    				}
    				i++;
    				if(i>nMax){
    					break;
    				}
    		}
    	return data;
    }

	public void updateHypothesis(String coefName, double coefValue) {
		// TODO Auto-generated method stub
		boolean wasFound = false;
		for(BiogemeHypothesis h: modelHypothesis){
			if(h.coefName.equals(coefName)){
				h.coefValue = coefValue;
				wasFound = true;
			}
		}
		if(!wasFound){
			System.out.println("--error: one of the coefficient was not loaded: " + coefName);
			BiogemeHypothesis temp = new BiogemeHypothesis();
			temp.coefName = coefName;
			temp.coefValue = coefValue;
			modelHypothesis.add(temp);
		}
	}
	
	private void updateNest(String coefName, double coefValue) {
		// TODO Auto-generated method stub
		if(coefName.equals(UtilsTS.carDriver)){
			nests.put(UtilsTS.carDriver, coefValue);
		}
		else if (coefName.equals(UtilsTS.carPassenger)){
			nests.put(UtilsTS.carPassenger, coefValue);
		}
		else if (coefName.equals(UtilsTS.stoUser)){
			nests.put(UtilsTS.stoUser, coefValue);
		}
		else if (coefName.equals(UtilsTS.ptUserNoSto)){
			nests.put(UtilsTS.ptUserNoSto, coefValue);
		}
		else if (coefName.equals(UtilsTS.activeMode)){
			nests.put(UtilsTS.activeMode, coefValue);
		}
	}
	
	public void printHypothesis(String path) throws IOException{
		OutputFileWritter tempWriter = new OutputFileWritter();
		tempWriter.OpenFile(path);
		for(BiogemeHypothesis h : modelHypothesis){
			tempWriter.WriteToFile(h.toString());
		}
		tempWriter.CloseFile();
	}
	
	public void writeSimulationResults(String outputPath) throws IOException {

		myOutputFileWriter.OpenFile(outputPath);
		printHeaders();
		Iterator<BiogemeAgent> it = myPopulationSample.iterator();
		while(it.hasNext()){
			BiogemeAgent currAgent = it.next();
			printAgent(currAgent);
		}
		myOutputFileWriter.CloseFile();
	}
	
	private void printAgent(BiogemeAgent currAgent) throws IOException {
		// TODO Auto-generated method stub
		String newLine = new String();
		for(String header: currAgent.myAttributes.keySet()){
			newLine += currAgent.myAttributes.get(header) + Utils.COLUMN_DELIMETER;
		}
		newLine += getChoiceName(currAgent.myAttributes.get(UtilsTS.alternative)) + 
				Utils.COLUMN_DELIMETER +getChoiceName(currAgent.myAttributes.get(UtilsTS.sim)) ;
		myOutputFileWriter.WriteToFile(newLine);
	}

	private void printHeaders() throws IOException {
		// TODO Auto-generated method stub
		String headers = new String();
		for(String header: myPopulationSample.get(0).myAttributes.keySet()){
			headers += header + Utils.COLUMN_DELIMETER;
		}
		headers += headers + UtilsTS.alternative + "_DEF" + Utils.COLUMN_DELIMETER + UtilsTS.sim + "_DEF";
		myOutputFileWriter.WriteToFile(headers);
	}

}
