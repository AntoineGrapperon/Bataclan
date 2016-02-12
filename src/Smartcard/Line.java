/**
 * 
 */
package Smartcard;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This represents a transportation line. A different class instance should be created for each direction.
 * @author Antoine
 *
 */
public class Line {
	int myId;
	/**
	 * This HashMap is structured as follow: the key can be either 0 or 1 depending on the line direction.
	 * The ArrayList<Station> is the stations that are served by the bus line in the given direction.
	 */
	HashMap<Integer,ArrayList<Station>> myStations = new HashMap<Integer,ArrayList<Station>>();
	
	public double getDistance(Station origin, Station nextOrigin){
			double distance = 1000000;
			double xA = nextOrigin.x;
			double yA = nextOrigin.y;
			int currDirection = getDirection(origin);
			
			for(Station station: myStations.get(currDirection)){
				double xB = station.x;
				double yB = station.y;
				double currDist = Math.pow(xA-xB,2)+Math.pow(yA-yB, 2);
				if(currDist < distance){
					distance = currDist;
				}
			}
			distance = Math.sqrt(distance);
		return distance;
	}
	
	public int getDirection(Station station){
		if(contains(station)){
			for(int i : myStations.keySet()){
				if(myStations.get(i).contains(station)){
					return i;
				}
			}
		}
		return 10;
	}
	
	public boolean contains(Station station){
		for(int i: myStations.keySet()){
			if(myStations.get(i).contains(station)){
				return true;
			}
		}
		System.out.println("--there was probably a problem in function Line.contains");
		return false;
	}

}
