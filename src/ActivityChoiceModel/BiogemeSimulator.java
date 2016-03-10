/**
 * 
 */
package ActivityChoiceModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
	
	public static double stoScale = 1;
	public static double noPtScale = 1;
	
	/**
	 * choiceUniverse is required only for simulating the model with smart card data. For simulating the model with the travel survey, alternatives are computed a bit differently.
	 */
	HashMap<String, BiogemeChoice> choiceUniverse = new HashMap<String, BiogemeChoice>();
	
	public BiogemeSimulator(){
	}
	
	public BiogemeSimulator(String pathControleFile, String pathOutput, String pathHypothesis) throws IOException{
		myCtrlGen.generateBiogemeControlFile();
		myCtrlGen.initialize(pathControleFile, pathOutput, pathHypothesis);
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
		boolean noPt = false;
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
			
		}
		return constants;
	}

	public void applyModelOnTravelSurveyPopulation(String outputPath, int mode) throws IOException{
		int n = 0;
		int N = myPopulationSample.size();
		for(BiogemeAgent person: myPopulationSample){
			ArrayList<BiogemeChoice> choiceSet = new ArrayList<BiogemeChoice>();
			if(mode == 1){choiceSet = modelChoiceUniverse;;}
			else if(mode == 2){choiceSet = person.generateChoiceSetFromTravelSurvey();}
			else if(mode == 3){choiceSet = person.generateChoiceSetFromTravelSurveyCHEAT();}

			person.applyModel(choiceSet);
			n++;
			if(n%1000 == 0){
				System.out.println("-- " + n + " agents were processed out of " + N);
			}
		}
		
		myOutputFileWriter.OpenFile(outputPath);
		String headers = "Observed choice, Simulated choice, Weigh";
		myOutputFileWriter.WriteToFile(headers);
		for(BiogemeAgent person: myPopulationSample){
			String newLine = getChoice(person.myAttributes.get(UtilsTS.alternative)) + 
					Utils.COLUMN_DELIMETER +getChoice(person.myAttributes.get(UtilsTS.sim)) +
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
	

	private String getChoice(String string) {
		// TODO Auto-generated method stub
		for(BiogemeChoice temp: myCtrlGen.choiceIndex){
			if(temp.biogeme_group_id == Integer.parseInt(string)){
				return temp.getConstantName();
			}
		}
		return null;
	}
	
	public static BiogemeChoice getChoice(int groupId) {
		// TODO Auto-generated method stub
		for(BiogemeChoice temp: modelChoiceUniverse){
			if(temp.biogeme_group_id == groupId){
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
			if(!list.containsKey(curChoice.biogeme_group_id)){
				list.put(curChoice.biogeme_group_id, true);
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
		}
	}
	
	private void updateNest(String coefName, double coefValue) {
		// TODO Auto-generated method stub
		if(coefName.equals("STO")){
			stoScale = coefValue;
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
		newLine += getChoice(currAgent.myAttributes.get(UtilsTS.alternative)) + 
				Utils.COLUMN_DELIMETER +getChoice(currAgent.myAttributes.get(UtilsTS.sim)) ;
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
