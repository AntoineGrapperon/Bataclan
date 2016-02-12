/**
 * 
 */
package Smartcard;

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
}
