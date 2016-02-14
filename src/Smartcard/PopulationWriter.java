/**
 * 
 */
package Smartcard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import ActivityChoiceModel.BiogemeAgent;
import ActivityChoiceModel.BiogemeChoice;
import ActivityChoiceModel.BiogemeSimulator;
import ActivityChoiceModel.UtilsTS;
import Utils.OutputFileWritter;
import Utils.Utils;

/**
 * @author Antoine
 *
 */
public class PopulationWriter extends OutputFileWritter {
	
	
	ArrayList<BiogemeAgent> myPopulation = new ArrayList<BiogemeAgent>();
	
	public void writeSimulationResults(String outputPath, ArrayList<BiogemeAgent> myPopulation) throws IOException {
		this.myPopulation = myPopulation;
		OpenFile(outputPath);
		printHeaders();
		Iterator<BiogemeAgent> it = myPopulation.iterator();
		while(it.hasNext()){
			BiogemeAgent currAgent = it.next();
			printAgent(currAgent);
		}
		CloseFile();
	}
	
	private void printAgent(BiogemeAgent currAgent) throws IOException {
		// TODO Auto-generated method stub
		String newLine = new String();
		for(String header: currAgent.myAttributes.keySet()){
			newLine += currAgent.myAttributes.get(header) + Utils.COLUMN_DELIMETER;
		}
		newLine += getChoice(currAgent.myAttributes.get(UtilsTS.alternative)) + 
				Utils.COLUMN_DELIMETER +getChoice(currAgent.myAttributes.get(UtilsTS.sim)) ;
		WriteToFile(newLine);
	}

	private void printHeaders() throws IOException {
		// TODO Auto-generated method stub
		String headers = new String();
		for(String header: myPopulation.get(0).myAttributes.keySet()){
			headers += header + Utils.COLUMN_DELIMETER;
		}
		headers += headers + UtilsTS.alternative + "_DEF" + Utils.COLUMN_DELIMETER + UtilsTS.sim + "_DEF";
		WriteToFile(headers);
	}
	
	private String getChoice(String string) {
		// TODO Auto-generated method stub
		for(BiogemeChoice temp: BiogemeSimulator.myCtrlGen.choiceIndex){
			if(temp.biogeme_id == Integer.parseInt(string)){
				return temp.getConstantName();
			}
		}
		return null;
	}
}
