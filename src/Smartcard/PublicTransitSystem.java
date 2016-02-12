/**
 * 
 */
package Smartcard;

import java.util.HashMap;

/**
 * @author Antoine
 *
 */
public class PublicTransitSystem {
	
	public static HashMap<Integer, Line> myLines;
	public static HashMap<Integer, Station> myStations;
	
	public PublicTransitSystem(){
		
	}
	
	public void createLines(){
		myLines = new HashMap<Integer, Line>();
		myStations = new HashMap<Integer, Station>();
	}
}
