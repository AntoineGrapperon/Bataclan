/**
 * 
 */
package ActivityChoiceModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import Utils.InputDataReader;
import Utils.OutputFileWritter;
import Utils.Utils;

/**
 * @author Antoine
 *
 */
public class BiogemeSimulator {

	public BiogemeControlFileGenerator biogemeGenerator = new BiogemeControlFileGenerator();
	
	InputDataReader myInputDataReader = new InputDataReader();
	OutputFileWritter myOutputFileWriter = new OutputFileWritter();
	ArrayList<BiogemeAgent> myPopulationSample = new ArrayList<BiogemeAgent>();
	public static ArrayList<BiogemeHypothesis> modelHypothesis = new ArrayList<BiogemeHypothesis>();
	
	public BiogemeSimulator(String pathControleFile,  String pathHypothesis) throws IOException{
		biogemeGenerator.generateBiogemeControlFile(pathControleFile,  pathHypothesis);
	}
	
	public BiogemeSimulator(String pathControleFile, String pathOutput, String pathHypothesis) throws IOException{
		biogemeGenerator.generateBiogemeControlFile(pathControleFile, pathOutput, pathHypothesis);
	}
	
	public void initialize(String path ) throws IOException{
		myInputDataReader.OpenFile(path);
		createAgents();
		System.out.println("--agents created");
		addHypothesis(biogemeGenerator.hypothesis);
		ArrayList<BiogemeHypothesis> constants = generateConstantHypothesis();
		addHypothesis(constants);
	}
	
	private ArrayList<BiogemeHypothesis> generateConstantHypothesis() {
		// TODO Auto-generated method stub
		ArrayList<BiogemeHypothesis> constants = new ArrayList<BiogemeHypothesis>();
		
		for(BiogemeChoice currChoice: BiogemeControlFileGenerator.choiceIndex){
			BiogemeHypothesis currHypothesis = new BiogemeHypothesis();
			String currCstName = currChoice.getConstantName();
			currHypothesis.setCoefName(currCstName);
			/*for(String dim: currChoice.choiceCombination.keySet()){
				ArrayList<Integer> category = new ArrayList<Integer>();
				category.add(currChoice.choiceCombination.get(dim));
				currHypothesis.setAffectedDimension(dim, category);
			}*/
			constants.add(currHypothesis);
		}
		return constants;
	}

	public void applyModelOnTravelSurveyPopulation(String outputPath) throws IOException{
		for(BiogemeAgent person: myPopulationSample){
			ArrayList<Integer> choiceSet = person.processChoiceSet();
			//System.out.println(choiceSet);
			person.applyModel(choiceSet);
		}
		
		myOutputFileWriter.OpenFile(outputPath);
		String headers = "Observed choice, Simulated choice, Weigh";
		myOutputFileWriter.WriteToFile(headers);
		for(BiogemeAgent person: myPopulationSample){
			
			/*for(BiogemeChoice temp: biogemeGenerator.choiceIndex){
				if(temp.biogeme_id == Integer.parseInt(person.myAttributes.get(UtilsTS.sim))){
					
				}
			}*/
			
			String newLine = getChoice(person.myAttributes.get(UtilsTS.alternative)) + 
					Utils.COLUMN_DELIMETER +getChoice(person.myAttributes.get(UtilsTS.sim)) +
					Utils.COLUMN_DELIMETER + person.myAttributes.get(UtilsTS.weigth);
			myOutputFileWriter.WriteToFile(newLine);
		}
		myOutputFileWriter.CloseFile();
	}
	
	public void applyModel(String outputPath) throws IOException{
		for(BiogemeAgent person: myPopulationSample){
			ArrayList<Integer> choiceSet = person.processChoiceSet();
			//System.out.println(choiceSet);
			person.applyModel(choiceSet);
		}
		
		myOutputFileWriter.OpenFile(outputPath);
		String headers = "Observed choice, Simulated choice";
		myOutputFileWriter.WriteToFile(headers);
		for(BiogemeAgent person: myPopulationSample){
			String newLine = getChoice(person.myAttributes.get(UtilsTS.alternative)) + 
					Utils.COLUMN_DELIMETER +getChoice(person.myAttributes.get(UtilsTS.sim)) +
					Utils.COLUMN_DELIMETER + person.myAttributes.get(UtilsTS.weigth);
			myOutputFileWriter.WriteToFile(newLine);
		}
		myOutputFileWriter.CloseFile();
	}
	
	private String getChoice(String string) {
		// TODO Auto-generated method stub
		for(BiogemeChoice temp: biogemeGenerator.choiceIndex){
			if(temp.biogeme_id == Integer.parseInt(string)){
				return temp.getConstantName();
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
    		while((line=myInputDataReader.myFileReader.readLine())!= null)
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
	
	public void printHypothesis(String path) throws IOException{
		OutputFileWritter tempWriter = new OutputFileWritter();
		tempWriter.OpenFile(path);
		for(BiogemeHypothesis h : modelHypothesis){
			tempWriter.WriteToFile(h.toString());
		}
		tempWriter.CloseFile();
	}
	
}
