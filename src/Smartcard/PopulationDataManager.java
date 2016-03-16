/**
 * 
 */
package Smartcard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import ActivityChoiceModel.BiogemeAgent;
import ActivityChoiceModel.DataManager;
import ActivityChoiceModel.UtilsTS;
import Utils.Utils;

/**
 * @author Antoine
 *
 */
public class PopulationDataManager extends DataManager {
	
	ArrayList<BiogemeAgent> myPopulation = new ArrayList<BiogemeAgent>();

	public ArrayList<BiogemeAgent> getAgents(String path) throws IOException{
		initialize(path);
		createAgents();
		setIndex();
		return myPopulation;
	}

	private void setIndex() {
		// TODO Auto-generated method stub
		int count = 0;
		for(BiogemeAgent ag: myPopulation){
			ag.myAttributes.put(UtilsSM.agentId, Integer.toString(count));
			count++;
		}
	}

	private void createAgents() {
		// TODO Auto-generated method stub
		for(int i = 0; i < myData.get(UtilsSM.zoneId).size(); i++){
			if(i%10000 == 0){
				System.out.println("agents loaded : " +i);
			}
			BiogemeAgent newAgent = new BiogemeAgent();
			for(String key: myData.keySet()){
				String value = myData.get(key).get(i);
				newAgent.myAttributes.put(key, value);
			}
			myPopulation.add(newAgent);	
		}
	}
}
