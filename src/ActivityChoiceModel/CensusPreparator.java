package ActivityChoiceModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import SimulationObjects.Person;
import Smartcard.UtilsSM;
import Smartcard.UtilsST;
import Utils.InputDataReader;
import Utils.OutputFileWritter;
import Utils.Utils;

public class CensusPreparator extends DataManager {
	//InputDataReader myInputDataReader = new InputDataReader();
	//OutputFileWritter myOutputFileWritter = new OutputFileWritter();
	//public HashMap<String, Object> myTravelSurvey;
	//HashMap<String, ArrayList<String>> myData = new HashMap<String, ArrayList<String>>();
	
	
	
	public CensusPreparator(String path){
		myInputDataReader.OpenFile(path);
		//String[] temp;
		//temp=path.split(".csv");
		//myOutputFileWritter.OpenFile(temp[0] + "_prepared.csv");	
	}
	
	public void writeZonalInputFile() throws IOException{
		Utils.COLUMN_DELIMETER = ";";
		storeData(false);
		Utils.COLUMN_DELIMETER = ",";
		FileWriter	writerZonalInputFile = createZonalWriter();
		System.out.println(myData.get(UtilsTS.dauid).size());
		for(int i = 0; i < myData.get(UtilsTS.dauid).size(); i++){
			String dauid = myData.get(UtilsTS.dauid).get(i);
			String pop = myData.get(" Population, 2006 ").get(i);
			writerZonalInputFile.append(dauid + ", " + pop + "\n");
		}
		writerZonalInputFile.close();
	}
	
	public void writeZonalInputFile(int nBatch) throws IOException{
		int count = 0;
		Utils.COLUMN_DELIMETER = ";";
		storeData(false);
		Utils.COLUMN_DELIMETER = ",";
		int subsetSize = myData.get(UtilsTS.dauid).size()/nBatch;
		for(int currN = 0; currN < nBatch; currN++){
			FileWriter	writerZonalInputFile = createZonalWriter(currN);
			for(int i = 0;i < myData.get(UtilsTS.dauid).size();i++){
				if((i>= currN * subsetSize && i < (currN+1)*subsetSize) || (i >= currN * subsetSize && currN == nBatch-1)){
					String dauid = myData.get(UtilsTS.dauid).get(i);
					int populationTarget = Integer.parseInt(myData.get(Utils.population + ", 2006").get(i)) - 
							Integer.parseInt(myData.get("Male, total / 0 to 4 years").get(i)) -
							Integer.parseInt(myData.get("Female, total / 0 to 4 years").get(i)) -
							Integer.parseInt(myData.get("Male, total / 5 to 9 years").get(i)) -
							Integer.parseInt(myData.get("Female, total / 5 to 9 years").get(i)) ;
					String pop = Integer.toString(populationTarget);
					//String pop = myData.get(Utils.population + ", 2006").get(i);
					writerZonalInputFile.append(dauid + ", " + pop + "\n");
					count++;
				}
			}
			/*for(int i = currN * subsetSize; i < Math.min(myData.get(UtilsTS.dauid).size(),(currN+1)* subsetSize); i++){
				
			}*/
			writerZonalInputFile.close();
			
		}	
		System.out.println("-- " + count + " zones where created out of " + myData.get(UtilsTS.dauid).size());
	}
	
	public void writeCtrlFile(int nBatch) throws IOException{
		for(int i = 0; i< nBatch; i++){
			FileWriter writerCtrlFile = createCtrlFileWriter(i);
			writerCtrlFile.append("\r\n" + 
					"\r\n" + 
					" \r\n" + 
					"=zonalFile_Gatineau_auto"+i+".txt\r\n" + 
					"\r\n" + 
					"Person\r\n" + 
					"\r\n" + 
					"age(0,1,2,3,4,5,6) =count=age.txt=main\r\n" + 
					"sex(0,1) =count=sex.txt=main\r\n" + 
					"mStat(0,1,2) =count=mStat.txt=main\r\n" + 
					"nPers(0, 1, 2, 3, 4) =count=nPers.txt=main\r\n" + 
					"inc(0,1,2,3,4,5,6) =count=inc.txt=main\r\n" + 
					"car(0, 1, 2, 3) =count=car.txt=main\r\n" + 
					"occ(0, 1, 2, 3) =count=occ.txt=main\r\n" + 
					"edu(0, 1, 2, 3, 4, 5, 6) =count=edu.txt=main\r\n" + 
					"sex(0,1) =count=Censussex.csv=importance\r\n" + 
					"age(0,1,2,3,4,5,6) =count=Censusage.csv=importance");
			writerCtrlFile.close();
		}
		
		
	}
	
	public void prepareDataColumnStorage() throws IOException{
		Utils.COLUMN_DELIMETER = ";";
		storeData(false);
		Utils.COLUMN_DELIMETER = ",";
		System.out.println("--census data was stored");
		for(int i = 0; i < myData.get(UtilsTS.dauid).size(); i++){
			writeAgeSex(i);
		}
	
	}
	
	
	
	private void writeAgeSex(int i) throws IOException {
		// TODO Auto-generated method stub
		String da = myData.get("dauid").get(i);
		FileWriter	writerAge = createLocalStatWriter(da, "age");
		FileWriter writerSex = createLocalStatWriter(da, "sex");
		
		int ageM0 = 0;int ageM1 = 0;int ageM2 = 0;int ageM3 = 0;int ageM4 = 0;int ageM5 = 0;int ageM6 = 0;
		int ageF0 = 0;int ageF1 = 0;int ageF2 = 0;int ageF3 = 0;int ageF4 = 0;int ageF5 = 0;int ageF6 = 0;
		
		ageM0 = Integer.parseInt(myData.get("Male, total / 15 to 19 years").get(i)) +
				//Integer.parseInt(myData.get("Male, total / 5 to 9 years").get(i)) +
				Integer.parseInt(myData.get("Male, total / 10 to 14 years").get(i));
		
		ageM1 = Integer.parseInt(myData.get("Male, total / 20 to 24 years").get(i));
		
		ageM2 = Integer.parseInt(myData.get("Male, total / 25 to 29 years").get(i)) +
				Integer.parseInt(myData.get("Male, total / 30 to 34 years").get(i));
		
		ageM3 = Integer.parseInt(myData.get("Male, total / 35 to 39 years").get(i)) +
				Integer.parseInt(myData.get("Male, total / 40 to 44 years").get(i));
		
		ageM4 = Integer.parseInt(myData.get("Male, total / 45 to 49 years").get(i)) +
				Integer.parseInt(myData.get("Male, total / 50 to 54 years").get(i));
		
		ageM5 = Integer.parseInt(myData.get("Male, total / 55 to 59 years").get(i)) +
				Integer.parseInt(myData.get("Male, total / 60 to 64 years").get(i));
		
		ageM6 = Integer.parseInt(myData.get("Male, total / 65 to 69 years").get(i)) +
				Integer.parseInt(myData.get("Male, total / 70 to 74 years").get(i)) +
				Integer.parseInt(myData.get("Male, total / 75 to 79 years").get(i)) +
				Integer.parseInt(myData.get("Male, total / 80 to 84 years").get(i)) +
				Integer.parseInt(myData.get("Male, total / 85 years and over").get(i));
		
		ageF0 = Integer.parseInt(myData.get("Female, total / 15 to 19 years").get(i)) +
				//Integer.parseInt(myData.get("Female, total / 5 to 9 years").get(i)) +
				Integer.parseInt(myData.get("Female, total / 10 to 14 years").get(i));
		
		ageF1 = Integer.parseInt(myData.get("Female, total / 20 to 24 years").get(i));
		
		ageF2 = Integer.parseInt(myData.get("Female, total / 25 to 29 years").get(i)) +
				Integer.parseInt(myData.get("Female, total / 30 to 34 years").get(i));
		
		ageF3 = Integer.parseInt(myData.get("Female, total / 35 to 39 years").get(i)) +
				Integer.parseInt(myData.get("Female, total / 40 to 44 years").get(i));
		
		ageF4 = Integer.parseInt(myData.get("Female, total / 45 to 49 years").get(i)) +
				Integer.parseInt(myData.get("Female, total / 50 to 54 years").get(i));
		
		ageF5 = Integer.parseInt(myData.get("Female, total / 55 to 59 years").get(i)) +
				Integer.parseInt(myData.get("Female, total / 60 to 64 years").get(i));
		
		ageF6 = Integer.parseInt(myData.get("Female, total / 65 to 69 years").get(i)) +
				Integer.parseInt(myData.get("Female, total / 70 to 74 years").get(i)) +
				Integer.parseInt(myData.get("Female, total / 75 to 79 years").get(i)) +
				Integer.parseInt(myData.get("Female, total / 80 to 84 years").get(i)) +
				Integer.parseInt(myData.get("Female, total / 85 years and over").get(i));
		
		addConditionals(writerAge, writerSex, ageM0, ageM1, ageM2, ageM3, ageM4, ageM5, ageM6,ageF0, ageF1, ageF2, ageF3, ageF4, ageF5, ageF6 );
		writerAge.flush(); writerSex.flush();
		writerAge.close();	writerSex.close();
	}
	
	public void prepareDataRowStorage() throws IOException{
		storeData(true);
		System.out.println("--census data was stored");
		ArrayList<String> daList = getDAlist();
		int cursor = 0;
		int nLinesDa = getNumberOfDaAttributes();
		for(String da: daList){
			writeAgeSex(da, cursor, nLinesDa);
			cursor+= nLinesDa;
		}		
	}
	
	private int getNumberOfDaAttributes() {
		// TODO Auto-generated method stub
		String temp = myData.get("Geo_Code").get(0);
		int counter = 0;
		for(int i = 0; i < myData.get("Geo_Code").size(); i++){
			if(myData.get("Geo_Code").get(i).equals(temp)){
				counter++;
			}
		}
		System.out.println(counter);
		return counter;
	}

	private void writeAgeSex(String da, int cursor, int nSkip) throws IOException {
		// TODO Auto-generated method stub
		FileWriter	writerAge = createLocalStatWriter(da, "age");
		FileWriter writerSex = createLocalStatWriter(da, "sex");
		
		int ageM0 = 0;int ageM1 = 0;int ageM2 = 0;int ageM3 = 0;int ageM4 = 0;int ageM5 = 0;int ageM6 = 0;
		int ageF0 = 0;int ageF1 = 0;int ageF2 = 0;int ageF3 = 0;int ageF4 = 0;int ageF5 = 0;int ageF6 = 0;
		
		for(int i = cursor; i < cursor + nSkip; i++){
			if(myData.get("Geo_Code").get(i).equals(da)){
				if(myData.get("Male").get(i).equals("")||myData.get("Female").get(i).equals("")){
				}
				else if(
						myData.get("Characteristic").get(i).equals("   5 to 9 years")||
						myData.get("Characteristic").get(i).equals("   10 to 14 years")||
						myData.get("Characteristic").get(i).equals("   15 to 19 years")
						){
					ageM0 += Integer.parseInt(myData.get("Male").get(i));
					ageF0 += Integer.parseInt(myData.get("Female").get(i));
				}
				else if(
						myData.get("Characteristic").get(i).equals("   20 to 24 years")
						){
					ageM1 += Integer.parseInt(myData.get("Male").get(i));
					ageF1 += Integer.parseInt(myData.get("Female").get(i));
				}
				else if(
						myData.get("Characteristic").get(i).equals("   25 to 29 years")||
						myData.get("Characteristic").get(i).equals("   30 to 34 years")
						){
					ageM2 += Integer.parseInt(myData.get("Male").get(i));
					ageF2 += Integer.parseInt(myData.get("Female").get(i));
				}
				else if(
						myData.get("Characteristic").get(i).equals("   35 to 39 years")||
						myData.get("Characteristic").get(i).equals("   40 to 44 years")
						){
					ageM3 += Integer.parseInt(myData.get("Male").get(i));
					ageF3 += Integer.parseInt(myData.get("Female").get(i));
				}
				else if(
						myData.get("Characteristic").get(i).equals("   45 to 49 years")||
						myData.get("Characteristic").get(i).equals("   50 to 54 years")
						){
					ageM4 += Integer.parseInt(myData.get("Male").get(i));
					ageF4 += Integer.parseInt(myData.get("Female").get(i));
				}
				else if(
						myData.get("Characteristic").get(i).equals("   55 to 59 years")||
						myData.get("Characteristic").get(i).equals("   60 to 64 years")
						){
					ageM5 += Integer.parseInt(myData.get("Male").get(i));
					ageF5 += Integer.parseInt(myData.get("Female").get(i));
				}
				else if(
						myData.get("Characteristic").get(i).equals("   65 to 69 years")||
						myData.get("Characteristic").get(i).equals("   70 to 74 years")||
						myData.get("Characteristic").get(i).equals("   75 to 79 years")||
						myData.get("Characteristic").get(i).equals("   80 to 84 years")||
						myData.get("Characteristic").get(i).equals("   85 years and over")
						){
					ageM6 += Integer.parseInt(myData.get("Male").get(i));
					ageF6 += Integer.parseInt(myData.get("Female").get(i));
				}
			}
		}
		//System.out.println(ageM0 + " " + ageM1 + " " + ageM2 + " " + ageM3 + " " + ageM4 + " " + ageM5);
		addConditionals(writerAge, writerSex, ageM0, ageM1, ageM2, ageM3, ageM4, ageM5, ageM6,ageF0, ageF1, ageF2, ageF3, ageF4, ageF5, ageF6 );
		writerAge.flush(); writerSex.flush();
		writerAge.close();	writerSex.close();
		
	}
	
	
	private void addConditionals(FileWriter writerAge, FileWriter writerSex, int ageM0, int ageM1, int ageM2, int ageM3, int ageM4, int ageM5, int ageM6, int ageF0,
			int ageF1, int ageF2, int ageF3, int ageF4, int ageF5, int ageF6) {
		// TODO Auto-generated method stub
		try{
			writerAge.append("0|0-...-...-...-...-...-..." + ", " + ageM0);
			writerAge.append('\n');
			writerAge.append("1|0-...-...-...-...-...-..." + ", " + ageM1);
			writerAge.append('\n');
			writerAge.append("2|0-...-...-...-...-...-..." + ", " + ageM2);
			writerAge.append('\n');
			writerAge.append("3|0-...-...-...-...-...-..." + ", " + ageM3);
			writerAge.append('\n');
			writerAge.append("4|0-...-...-...-...-...-..." + ", " + ageM4);
			writerAge.append('\n');
			writerAge.append("5|0-...-...-...-...-...-..." + ", " + ageM5);
			writerAge.append('\n');
			writerAge.append("6|0-...-...-...-...-...-..." + ", " + ageM6);
			writerAge.append('\n');
			
			writerAge.append("0|1-...-...-...-...-...-..." + ", " + ageF0);
			writerAge.append('\n');
			writerAge.append("1|1-...-...-...-...-...-..." + ", " + ageF1);
			writerAge.append('\n');
			writerAge.append("2|1-...-...-...-...-...-..." + ", " + ageF2);
			writerAge.append('\n');
			writerAge.append("3|1-...-...-...-...-...-..." + ", " + ageF3);
			writerAge.append('\n');
			writerAge.append("4|1-...-...-...-...-...-..." + ", " + ageF4);
			writerAge.append('\n');
			writerAge.append("5|1-...-...-...-...-...-..." + ", " + ageF5);
			writerAge.append('\n');
			writerAge.append("6|1-...-...-...-...-...-..." + ", " + ageF6);
			writerAge.append('\n');
			
			writerSex.append("0|0-...-...-...-...-...-..." + ", " + ageM0);
			writerSex.append('\n');
			writerSex.append("0|1-...-...-...-...-...-..." + ", " + ageM1);
			writerSex.append('\n');
			writerSex.append("0|2-...-...-...-...-...-..." + ", " + ageM2);
			writerSex.append('\n');
			writerSex.append("0|3-...-...-...-...-...-..." + ", " + ageM3);
			writerSex.append('\n');
			writerSex.append("0|4-...-...-...-...-...-..." + ", " + ageM4);
			writerSex.append('\n');
			writerSex.append("0|5-...-...-...-...-...-..." + ", " + ageM5);
			writerSex.append('\n');
			writerSex.append("0|6-...-...-...-...-...-..." + ", " + ageM6);
			writerSex.append('\n');
			
			writerSex.append("1|0-...-...-...-...-...-..." + ", " + ageF0);
			writerSex.append('\n');
			writerSex.append("1|1-...-...-...-...-...-..." + ", " + ageF1);
			writerSex.append('\n');
			writerSex.append("1|2-...-...-...-...-...-..." + ", " + ageF2);
			writerSex.append('\n');
			writerSex.append("1|3-...-...-...-...-...-..." + ", " + ageF3);
			writerSex.append('\n');
			writerSex.append("1|4-...-...-...-...-...-..." + ", " + ageF4);
			writerSex.append('\n');
			writerSex.append("1|5-...-...-...-...-...-..." + ", " + ageF5);
			writerSex.append('\n');
			writerSex.append("1|6-...-...-...-...-...-..." + ", " + ageF6);
			writerSex.append('\n');

		}
		catch(Exception e){
			e.printStackTrace();
			System.err.println(e.getClass().getName()+": "+e.getMessage());
	        System.exit(0);
		}
	}

	public FileWriter createLocalStatWriter(String daId, String attributeName){
		try{
			boolean b = false;
			//File file = new File("..\\data\\" + daId);
			File file = new File(Utils.DATA_DIR + "data\\" + daId);
			if(!file.exists()){b=file.mkdirs();}
			if(!b){System.out.println("--directories were created");}
			
			FileWriter writer = new FileWriter(Utils.DATA_DIR + "data\\" + daId + "\\Census" + attributeName + ".csv");
			//FileWriter writer = new FileWriter("..\\data\\" + daId + "\\Census" + attributeName + ".csv");
			writer.append("Category, Count \n");
			return writer;
		}
		catch(Exception e){	
			e.printStackTrace();
			return null;
		}
	}
	
	public FileWriter createZonalWriter(){
		try{
			boolean b = false;
			//File file = new File("..\\data\\" + daId);
			File file = new File(Utils.DATA_DIR );
			if(!file.exists()){b=file.mkdirs();}
			if(!b){System.out.println("--directories were created");}
			
			FileWriter writer = new FileWriter( Utils.DATA_DIR + "zonalFile_" + UtilsTS.city +"_auto.txt");
			//FileWriter writer = new FileWriter("..\\data\\" + daId + "\\Census" + attributeName + ".csv");
			writer.append("da_uid, total \n");
			return writer;
		}
		catch(Exception e){	
			e.printStackTrace();
			return null;
		}
	}
	
	public FileWriter createZonalWriter(int n){
		try{
			boolean b = false;
			//File file = new File("..\\data\\" + daId);
			File file = new File(Utils.DATA_DIR );
			if(!file.exists()){b=file.mkdirs();}
			//if(!b){System.out.println("--directories were created");}
			if(!b){}
			
			FileWriter writer = new FileWriter( Utils.DATA_DIR + "zonalFile_" + UtilsTS.city +"_auto"+n+".txt");
			//FileWriter writer = new FileWriter("..\\data\\" + daId + "\\Census" + attributeName + ".csv");
			writer.append("da_uid, total \n");
			return writer;
		}
		catch(Exception e){	
			e.printStackTrace();
			return null;
		}
	}
	
	public FileWriter createCtrlFileWriter(int n)	{
		try{
			boolean b = false;
			//File file = new File("..\\data\\" + daId);
			File file = new File(Utils.DATA_DIR + "ctrl\\" );
			if(!file.exists()){b=file.mkdirs();}
			//if(!b){System.out.println("--directories were created");}
			if(!b){}
			
			FileWriter writer = new FileWriter( Utils.DATA_DIR + "ctrl\\config" + n +".txt");
			//FileWriter writer = new FileWriter("..\\data\\" + daId + "\\Census" + attributeName + ".csv");
			return writer;
		}
		catch(Exception e){	
			e.printStackTrace();
			return null;
		}
	}

	public ArrayList<String> getDAlist(){
		ArrayList<String> daList = new ArrayList<String>();
		for(int i = 0; i < myData.get("Geo_Code").size(); i++){
			if(!daList.contains(myData.get("Geo_Code").get(i))){
				daList.add(myData.get("Geo_Code").get(i));
			}
		}
		return daList;
	}
	
	/**
	 * This function is useful for census file whoch attributes are not stored in columns but in rows. This kind of storage creates very long file and they can be filtered by type..
	 * @param filter is true if filter should be used. False otherwise.
	 * @return
	 * @throws IOException
	 */
	public void storeData(boolean filter) throws IOException
    {
		if(filter){
			 ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
	    	 data = getData(true);
	    	 
	    	 ArrayList<String> headers = data.get(0);
	    	 for(int i =0; i < headers.size(); i++){
	    		 myData.put(headers.get(i), new ArrayList<String>());
	    	 }

	    	 for (int i=1; i<data.size(); i++)
	    	 {
	    		 //System.out.println(headers.size() + "  " + data.get(i).size() + "  " + data.get(i).get(data.get(i).size()-1));
	    			for (int j=0; j<headers.size();j++)
	    			{
	    				myData.get(headers.get(j)).add(data.get(i).get(j));
	    			}
	    	 }
	    	 //System.out.println("--census data was stored");
		}
		else{
			 ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
	    	 data = getData();
	    	 
	    	 ArrayList<String> headers = data.get(0);
	    	 for(int i =0; i < headers.size(); i++){
	    		 myData.put(headers.get(i), new ArrayList<String>());
	    	 }

	    	 for (int i=1; i<data.size(); i++)
	    	 {
	    		 //System.out.println(headers.size() + "  " + data.get(i).size() + "  " + data.get(i).get(data.get(i).size()-1));
	    			for (int j=0; j<headers.size();j++)
	    			{
	    				myData.get(headers.get(j)).add(data.get(i).get(j));
	    			}
	    	 }
		}
    	 
    }
	
	
	/**
	 * This function is useful for census file whoch attributes are not stored in columns but in rows. This kind of storage creates very long file and they can be filtered by type..
	 * @param filter
	 * @return
	 * @throws IOException
	 */
	@Deprecated
	public ArrayList<ArrayList<String>> getData(boolean filter) throws IOException
    {
    	String line=null;
    	Scanner scanner = null;
    	ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();

    		int i=0;
    		while((line=myInputDataReader.myFileReader.readLine())!= null)
    		{
    			ArrayList<String> tempLine = new ArrayList<String>();
    			scanner = new Scanner(line);
    			scanner.useDelimiter(Utils.COLUMN_DELIMETER);

    				while (scanner.hasNext())
    				{
    					String dat = scanner.next();
    					tempLine.add(dat);
    				}
    				if(tempLine.get(3).equals("Detailed mother tongue") ||
    						tempLine.get(3).equals("Knowledge of official languages")||
    						tempLine.get(3).equals("First official language spoken")||
    						tempLine.get(3).equals("Detailed language spoken most often at home")||
    						tempLine.get(3).equals("Detailed other language spoken regularly at home")
    						){	
    				}
    				else{
        				i++;
        				//data.get(i).add(dat);
        				data.add(tempLine);	
    				}
    		}
    	return data;
    }
}
