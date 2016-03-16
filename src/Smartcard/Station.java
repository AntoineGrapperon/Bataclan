/**
 * 
 */
package Smartcard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import ActivityChoiceModel.BiogemeAgent;

/**
 * @author Antoine
 *
 */
public class Station {
	int myId;
	/**
	 * X coordinate of the position of the station in the NAD83 UTM ZONE 18N coordinate system.
	 */
	double x;
	/**
	 * Y coordinate of the position of the station in the NAD83 UTM ZONE 18N coordinate system.
	 */
	double y;
	
	public double getDistance(Station station) {
		// TODO Auto-generated method stub
		return Math.sqrt(Math.pow((x - station.x),2) + Math.pow((y-station.y), 2));
	}

	public ArrayList<Smartcard> getSmartcards() {
		// TODO Auto-generated method stub
		ArrayList<Smartcard> localSmartcards = new ArrayList<Smartcard>();
		Iterator<Smartcard> itS = PublicTransitSystem.mySmartcards.iterator();
		while(itS.hasNext()){
			Smartcard tempS = itS.next();
			if(tempS.stationId == myId){
				if(!tempS.isDistributed){
					localSmartcards.add(tempS);
				}
			}
		}
		return localSmartcards;
	}

	public ArrayList<BiogemeAgent> getLocalPopulation() {
		// TODO Auto-generated method stub
		ArrayList<BiogemeAgent> localPopulation = new ArrayList<BiogemeAgent>();
		Iterator<BiogemeAgent> itA = PublicTransitSystem.myPopulation.iterator();
		while(itA.hasNext()){
			BiogemeAgent currA = itA.next();
			if(!currA.isDistributed){
				double agentZone = Double.parseDouble(currA.myAttributes.get(UtilsSM.zoneId));
				
				if(PublicTransitSystem.geoDico.containsKey(agentZone)){
					ArrayList<Integer> closeStation = PublicTransitSystem.geoDico.get(agentZone);
					if(closeStation.contains(myId)){
						localPopulation.add(currA);
					}
				}
			}
		}
		return localPopulation;
	}
}
