/**
 * 
 */
package ActivityChoiceModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import Utils.InputDataReader;
import Utils.OutputFileWritter;
import Utils.Utils;

/**
 * @author Antoine
 *
 */
public class DataManager {

	InputDataReader myInputDataReader = new InputDataReader();
	OutputFileWritter myOutputFileWriter = new OutputFileWritter();
	protected HashMap<String, ArrayList<String>> myData = new HashMap<String, ArrayList<String>>();
	String path = new String();
	
	public void initialize(String path) throws IOException{
		this.path = path;
		myInputDataReader.OpenFile(path);
		String[] temp;
		temp=path.split(".csv");
		storeData();
		myInputDataReader.CloseFile();
	}
	
	public void createOuputFile(){
		String[] temp;
		temp=path.split(".csv");
		myOutputFileWriter.OpenFile(temp[0] + "_prepared.csv");
	}
	
	public void storeData() throws IOException
    {
    	 ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
    	 data = getData();
    	 
    	 ArrayList<String> headers = data.get(0);
    	 for(int i =0; i < headers.size(); i++){
    		 myData.put(headers.get(i), new ArrayList<String>());
    	 }

    	 for (int i=1; i<data.size(); i++)
    	 {
    			for (int j=0; j<data.get(i).size();j++)
    			{
    				myData.get(headers.get(j)).add(data.get(i).get(j));
    			}
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
    			scanner.useDelimiter(Utils.COLUMN_DELIMETER);

    				while (scanner.hasNext())
    				{
    					String dat = scanner.next();
    					data.get(i).add(dat);
    				}
    				i++;
    		}
    	return data;
    }
	
	public ArrayList<ArrayList<String>> getData(int n) throws IOException
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
    				if(i>n){
    					break;
    				}
    		}
    	return data;
    }
	
	public void printData() throws IOException {

		String headers= new String();
		for (String key : myData.keySet()) {
			headers+= key + Utils.COLUMN_DELIMETER;
		}
		headers = headers.substring(0, headers.length()-1);
		myOutputFileWriter.WriteToFile(headers);
		
		for(int i=0; i < myData.get(UtilsTS.id).size(); i++){
			String line = new String();
			for(String key: myData.keySet()){
				line += myData.get(key).get(i) + Utils.COLUMN_DELIMETER;
			}
			line = line.substring(0, line.length()-1);
			myOutputFileWriter.WriteToFile(line);
		}
		myOutputFileWriter.CloseFile();
	}
	
	public void printColumns(ArrayList<String> toPrint) throws IOException {

		String headers= new String();
		Iterator<String> it = toPrint.iterator();
		while(it.hasNext()){
			String head = it.next();
			headers+= head + Utils.COLUMN_DELIMETER;
		}
		headers = headers.substring(0, headers.length()-1);
		myOutputFileWriter.WriteToFile(headers);
		
		for(int i=0; i < myData.get(UtilsTS.id).size()-1; i++){
			if(myData.get(UtilsTS.pDebut).get(i).equals("1")){
				String line = new String();
				Iterator<String> it2 = toPrint.iterator();
				while(it2.hasNext()){
					String head = it2.next();
					try{
						line += myData.get(head).get(i) + Utils.COLUMN_DELIMETER;
					}
					catch(NullPointerException ex){
						System.out.println("null pointer exception : " + head);
					}
				}
				line = line.substring(0, line.length()-1);
				myOutputFileWriter.WriteToFile(line);
			}
			else{
			}
		}
		myOutputFileWriter.CloseFile();
	}
	
	public void selectAndPrint(){
		ArrayList<String> headers = new ArrayList<String>();
		
		try {
			printColumns(headers);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<Object> createSubSamples(int numberOfCores){

	    //creates sub group of zone that can be processed using multithreading to accelerate computation
	    int subSampleSize = myData.get(UtilsTS.id).size()/(numberOfCores);
	    ArrayList<Object> subSamples = new ArrayList<Object>();
	    int idxCore = 0;
	    
	    for (int j = 0; j<numberOfCores; j++){
	    	HashMap<String, Object> currSubSample = new HashMap<String, Object>();
	    	for (String key: myData.keySet()){
	    		currSubSample.put(key, new ArrayList<Object>());
	    	}
	    	subSamples.add(currSubSample);
	    }
	    
	    System.out.println(myData.get(UtilsTS.id).size());
	    for(int k = 0; k < myData.get(UtilsTS.id).size()-1; k++){
	    	if((k>=idxCore*subSampleSize && k < (idxCore+1)*subSampleSize)|| (k>=idxCore*subSampleSize && idxCore == numberOfCores-1)){// || (k>=i*subSampleSize && i == numberOfCores-1)
	    		for (String key: myData.keySet()){
	    			//System.out.println(key + "  "+ idxCore + "  " + k);
	    			try{

		    			((HashMap<String, ArrayList<Object>>) subSamples.get(idxCore)).get(key).add(myData.get(key).get(k));
	    			}
	    			catch(IndexOutOfBoundsException e){
	    				System.out.println(key + "  "+ idxCore + "  " + k);
	    				System.out.println(e);
	    			}
		    	}
	    	}
	    	else{
	    		idxCore++;
	    	}
	    }
	    for (int j = 0; j<numberOfCores; j++){
	    	int size = ((HashMap<String, ArrayList<Object>>) subSamples.get(j)).get(UtilsTS.id).size();
	    	System.out.println("sample " + j + ": " + size);	
	    }
	    return subSamples;
	}
}
