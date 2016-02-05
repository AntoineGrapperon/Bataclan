/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Utils;

import java.io.*;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author XPS 1645
 */
public class ConditionalDataReader extends FileManager
{
    public BufferedReader myFileReader;
    LineNumberReader reader;

    @Override
    public void OpenFile(String fileName)
    {
        //myFileReader = new BufferedReader(new StringReader(fileName));
        //[AG] I rewrite this function that didn't work (wring version is above, commented line)
        FileReader fstream = null;
        try
        {
        	fstream = new FileReader(fileName);
            myFileReader = new BufferedReader(fstream);
    		reader = new LineNumberReader(myFileReader);
           
         } 
        catch (IOException ex)
        {
        	Logger.getLogger(OutputFileWritter.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
  //this function expect the input data to be in format:
    /*
     * name of category, number of alternatives  ex: 
     * departure hour, 3
     * last dep hour, 3
     * nActivities, 4
     */
    /*public HashMap<String, Integer> getTripChainAlternatives() throws IOException{
    	HashMap<String, Integer> answer = new HashMap<String, Integer>();
    	String strTok;
    	while ((strTok = myFileReader.readLine()) != null){
    		String[] tok = strTok.split(Utils.COLUMN_DELIMETER);
    		answer.put(tok[0], Integer.parseInt(tok[1].trim()));
        }
    	return answer;
    }*/

    @Override
    public void CloseFile() throws IOException
    {
        myFileReader.close();
    }

    public String GetNextRow() throws IOException
    {
        return myFileReader.readLine();
    }
}
