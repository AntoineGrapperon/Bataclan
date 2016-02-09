/**
 * 
 */
package Smartcard;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Antoine
 *
 */
public class Smartcard {

	int id;
	protected HashMap<String, ArrayList<String>> myData = new HashMap<String, ArrayList<String>>();
	
	public void setId(int id) {
		// TODO Auto-generated method stub
		this.id = id;
	}
}
