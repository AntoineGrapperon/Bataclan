/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Utils;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ActivityChoiceModel.UtilsTS;

//    // This should substitute as the only Key Value pair
//    public struct KeyValPair1
//    {
//        public string category;
//        public uint value;
//    }
public class ConditionalGenerator
{
    ConditionalDataReader myDataReader;
    OutputFileWritter myDataWriter;
    ArrayList myDimensionNames;
    HashMap<String, Object> myCondCollection;
    ConditionalDataReader myZonalDataReader;

    public ConditionalGenerator()
    {
    	myZonalDataReader = new ConditionalDataReader();
        myDataReader = new ConditionalDataReader();
        myDataWriter = new OutputFileWritter();
        myCondCollection = new HashMap();
        myDimensionNames = new ArrayList();
    }

    /*public boolean GenerateConditionals(String inputDataFile, String inputDescFile ) throws IOException
    {
        myDataReader.OpenFile(inputDataFile);
	String currRow = null;
        currRow = myDataReader.GetNextRow();
        if (currRow != null)
        {
            SetDimensions(inputDescFile);
            System.out.println("--dimensions setted");
        }
        else
        {
            return false;
        }
        currRow = myDataReader.GetNextRow();

        for (int i = 0; i < myDimensionNames.size(); i++)
        {
            CreateCategoryCombinations(i);
            System.out.println("--category combinations created");
            System.gc();
        }
        while(currRow != null )
        {
            WriteNextConditional(currRow);
            currRow = myDataReader.GetNextRow();
        }

        for (Map.Entry<String, Object> currTable : myCondCollection.entrySet())
        {
            //myDataWriter.OpenFile(Utils.DATA_DIR + "Census" + currTable.getKey() + ".csv");
        	//myDataWriter.OpenFile(Utils.DATA_DIR+"data\\ImportanceSamplingConditionals\\" + "NHS" + currTable.getKey() + ".csv");
        	myDataWriter.OpenFile(Utils.DATA_DIR+"data\\ImportanceSamplingConditionals\\" + "NHS.txt");
            myDataWriter.WriteToFile("Conditional,Count");

            if(currTable.getValue() instanceof HashMap)
            {
                HashMap<String, Object> curr = (HashMap)currTable.getValue();
                for (Map.Entry<String, Object> currPair : curr.entrySet())
                {
                    myDataWriter.WriteToFile(currPair.getKey() + "," + currPair.getValue().toString());
                }
            }
            myDataWriter.CloseFile();
        }
	
        myDataReader.CloseFile();
        return true;
    }*/

    private void SetDimensions(String inputDescFile ) throws IOException
    {
        ConditionalDataReader descReader = new ConditionalDataReader();
        descReader.OpenFile(inputDescFile);
        String dimStr = descReader.GetNextRow();
        dimStr.trim();
        int counter=0;
        while (dimStr != null)
        {
        	counter++;
        	//System.out.println(counter);
            String[] currTok = dimStr.split(Utils.COLUMN_DELIMETER);
            myCondCollection.put(currTok[0], new HashMap<String, Object>());
            ArrayList curCats = new ArrayList();
                
            for (String curCat: currTok)
            {
                if (curCat != null)
                {
                    curCats.add(curCat);
                    //System.out.println(curCat);
                }
                else{
                	System.out.println("problem when charging dimension categories");
                }
            }
                
            myDimensionNames.add(curCats);
            dimStr = descReader.GetNextRow();
        }
            
        descReader.CloseFile();
    }

    private void CreateCategoryCombinations(int idx)
    {
        int dimCnt = 1;
        String curDimNm = myDimensionNames.get(idx).toString();
        System.out.println(curDimNm);

        for(Iterator<ArrayList> curDim = myDimensionNames.iterator(); curDim.hasNext();)
        {
        	ArrayList next = curDim.next();
            dimCnt *= ((next).size()-1);
        }

       String[] combStr = new String[dimCnt];
       int offset = 0;
       for(int i = 1; i< ((ArrayList)myDimensionNames.get(idx)).size(); i++)
       {
           for (int j = 0; j < dimCnt / (((ArrayList)myDimensionNames.get(idx)).size()-1); j++)
           {
               if( !((ArrayList)myDimensionNames.get(idx)).get(i).toString().equals("") ){
            	   combStr[j + offset] = ((ArrayList)myDimensionNames.get(idx)).get(i).toString() + Utils.CATEGORY_DELIMITER;
               }
               else{
            	   System.out.println("--there is a problem with " +((ArrayList)myDimensionNames.get(idx)).get(i).toString() );
               }
           } 
           offset += dimCnt / (((ArrayList)myDimensionNames.get(idx)).size() - 1);
       }
     
       offset = dimCnt /(((ArrayList)myDimensionNames.get(idx)).size() - 1);

       for (int i = 0; i < myDimensionNames.size(); i++)
       {
           if(i != idx)
           {
               AppendDimensions(combStr, i, offset);
               offset /= (((ArrayList)myDimensionNames.get(i)).size() - 1);
           }
       }
       
       //The commented line below was not working, it was calling the table [age,0,1,2] instead of the name "age"
       ArrayList test = (ArrayList)myDimensionNames.get(idx);
       //System.out.println(test.get(0));
       //System.out.println((ArrayList)myDimensionNames.get(idx).get(0));
       HashMap<String, Object> currDimColl = (HashMap<String, Object>) myCondCollection.get(test.get(0));
       //System.out.println(currDimColl); 
       //int counter = 0;
       
       for (String curStr: combStr)
       {
           //counter++;
    	   LightKeyValPair currKey = new LightKeyValPair();
           currKey.category = curStr.substring(0,curStr.length()-1);
           //System.out.println(currKey.category);
           currDimColl.put(currKey.category, (Object)currKey);
           //this if function is to insure that the RAM is not filled by the garbage collector
           /*if(counter == 100000){
        	   System.out.println("--clearing garbage collector");
        	   System.gc();
        	   counter = 0;
           }*/
       }
       myCondCollection.put(((String)test.get(0)), currDimColl);
    }
        
    private void AppendDimensions(String[] stringCol, int currDim, int offset)
    {
        ArrayList currDimL = (ArrayList)myDimensionNames.get(currDim);
        int DimOff = offset / (currDimL.size() - 1);
        int repeat = stringCol.length / offset;
        int cursor = 0;
        for (int i = 0; i < repeat; i++)
        {
            for (int j = 1; j < currDimL.size(); j++)
            {
                for (int k = 0; k < DimOff; k++)
                {
                    stringCol[cursor] += currDimL.get(j).toString() + Utils.CONDITIONAL_DELIMITER;
                    cursor++;
                }
            }
        }
    }
    
    /*private void WriteNextConditional(String currStr)
    {
        String[] currVal = currStr.split(Utils.COLUMN_DELIMETER);
			
        for(int i = 0; i < currVal.length; i++)
        {
            HashMap<String, Object> currDimColl = (HashMap<String, Object>) myCondCollection.get(myDimensionNames.get(i).toString());
            String currCondNm = currVal[i] + Utils.CATEGORY_DELIMITER;
            for (int j = 0; j < currVal.length; j++)
            {
                if (i != j)
                {
                    currCondNm = currCondNm + currVal[j] + Utils.CONDITIONAL_DELIMITER;
                }
            }
            currCondNm = currCondNm.substring(0, currCondNm.length() - 1);
            if (currDimColl.containsKey(currCondNm))
            {
                LightKeyValPair mycurVal = (LightKeyValPair) currDimColl.get(currCondNm);
                mycurVal.value++;
                currDimColl.put(currCondNm, mycurVal);
            }
            else
            {
                LightKeyValPair curPair = new LightKeyValPair();
                curPair.value = 1;
                curPair.category = currCondNm;
                //currDimColl.Add(currCondNm, curPair);
            }
        }
    }*/
    
    /*private void CreateCategoryCombinations(int idx)
    {
        int dimCnt = 1;
        String curDimNm = myDimensionNames.get(idx).toString();
        System.out.println(curDimNm);

        for(Iterator<ArrayList> curDim = myDimensionNames.iterator(); curDim.hasNext();)
        {
        	ArrayList next = curDim.next();
            dimCnt *= ((next).size()-1);
        }

       String[] combStr = new String[dimCnt];
       int offset = 0;
       for(int i = 1; i< ((ArrayList)myDimensionNames.get(idx)).size(); i++)
       {
           for (int j = 0; j < dimCnt / (((ArrayList)myDimensionNames.get(idx)).size()-1); j++)
           {
               combStr[j + offset] = ((ArrayList)myDimensionNames.get(idx)).get(i).toString() + Utils.CATEGORY_DELIMITER;
           }
           System.gc();     
           offset += dimCnt / (((ArrayList)myDimensionNames.get(idx)).size() - 1);
       }
     
       offset = dimCnt /(((ArrayList)myDimensionNames.get(idx)).size() - 1);

       for (int i = 0; i < myDimensionNames.size(); i++)
       {
           if(i != idx)
           {
               AppendDimensions(combStr, i, offset);
               offset /= (((ArrayList)myDimensionNames.get(i)).size() - 1);
           }
       }
       System.gc();
       
       //The commented line below was not working, it was calling the table [age,0,1,2] instead of the name "age"
       ArrayList test = (ArrayList)myDimensionNames.get(idx);
       System.out.println(test.get(0));
       //System.out.println((ArrayList)myDimensionNames.get(idx).get(0));
       HashMap<String, Object> currDimColl = (HashMap<String, Object>) myCondCollection.get(test.get(0));
       System.out.println(currDimColl); 
       //int counter = 0;
       
       for (String curStr: combStr)
       {
           //counter++;
    	   KeyValPair currKey = new KeyValPair();
           currKey.category = curStr.substring(0,curStr.length()-1);
           //System.out.println(currKey.category);
           currDimColl.put(currKey.category, (Object)currKey);
           
           //this if function is to insure that the RAM is not filled by the garbage collector
           /*if(counter == 100000){
        	   System.out.println("--clearing garbage collector");
        	   System.gc();
        	   counter = 0;
           }
       }
       System.gc();
    }*/
    
    public boolean GenerateConditionalsStepByStep(String inputDataFile, String inputDescFile, String inputZonalFile, String destPath ) throws IOException
    {
    	//these data required to copy paste CMA information to DA level (projecting information)
    	//myZonalDataReader.OpenFile(inputZonalFile);
    	
    	
        myDataReader.OpenFile(inputDataFile);
        String currRow = null;
        currRow = myDataReader.GetNextRow();
        if (currRow != null)
        {
            SetDimensions(inputDescFile);
            System.out.println("--dimensions setted");
        }
        else
        {
            return false;
        }
        myDataReader.CloseFile();
        

        for (int i = 0; i < myDimensionNames.size(); i++)
        {
        	myDataReader.OpenFile(inputDataFile);
            currRow = myDataReader.GetNextRow();
            currRow = myDataReader.GetNextRow();
            String pathToSource = null;;
            if(UtilsTS.city.equals("Montreal")){
            	pathToSource = Utils.DATA_DIR +"\\data\\"+ destPath + "\\" + ((ArrayList) myDimensionNames.get(i)).get(0) + ".txt";
            }
            else if (UtilsTS.city.equals("Gatineau")){
            	pathToSource = destPath + "\\" + ((ArrayList) myDimensionNames.get(i)).get(0) + ".txt";
            }
        	myDataWriter.OpenFile(pathToSource);
            myDataWriter.WriteToFile("Conditional,Count");
            CreateCategoryCombinations(i);
            System.out.println("--category combinations created");
            while(currRow != null )
            {
                WriteNextConditionalOneCategoryAtATime(currRow, i);
                currRow = myDataReader.GetNextRow();
            }
            
            HashMap<String, Object> currTable = (HashMap<String, Object>) myCondCollection.get(((ArrayList)myDimensionNames.get(i)).get(0));
            for (Map.Entry<String, Object> currPair : currTable.entrySet())
            {
            	//System.out.println(((LightKeyValPair)(currPair.getValue())).value);
                myDataWriter.WriteToFile(currPair.getKey() + "," + Integer.toString((((LightKeyValPair)(currPair.getValue())).value)));
            }
            
            myCondCollection.put((String)(((ArrayList)myDimensionNames.get(i)).get(0)), null);
            
            myDataWriter.CloseFile();
            myDataReader.CloseFile();
            System.gc();
            
            //this code was an attempt to duplicate all the data in each folder, but it is just making memory requirement explode...
            /*currRow = myZonalDataReader.GetNextRow();
            String pathToDest;
            String[] list;
           
            while(currRow!= null){
            	currRow = myZonalDataReader.GetNextRow();
            	list = currRow.split(Utils.COLUMN_DELIMETER);
            	pathToDest = "D:\\Recherche\\CharlieWorkspace\\PopSynz\\data\\" + list[0] + "\\" + sourceType + ((ArrayList) myDimensionNames.get(i)).get(0) + ".csv";
            	copy(pathToSource,pathToDest);
        	}*/
        } 
        return true;
    }
    
    private void WriteNextConditionalOneCategoryAtATime(String currStr, int idx)
    {
        String[] currVal = currStr.split(Utils.COLUMN_DELIMETER);
			
        //for(int i = 0; i < currVal.length; i++)
        //{
            HashMap<String, Object> currDimColl = (HashMap<String, Object>) myCondCollection.get(((ArrayList)myDimensionNames.get(idx)).get(0));
            String currCondNm = currVal[idx] + Utils.CATEGORY_DELIMITER;
            for (int j = 0; j < currVal.length; j++)
            {
                if (idx != j)
                {
                    currCondNm = currCondNm + currVal[j] + Utils.CONDITIONAL_DELIMITER;
                }
            }
            currCondNm = currCondNm.substring(0, currCondNm.length() - 1);
            //System.out.println(currCondNm);
            //System.out.println(currDimColl);
            if (currDimColl.containsKey(currCondNm))
            {
            	
                LightKeyValPair mycurVal = (LightKeyValPair) currDimColl.get(currCondNm);
                mycurVal.value++;
                currDimColl.put(currCondNm, mycurVal);
                //System.out.println(mycurVal.value);
            }
            else
            {
            	//System.out.println("no");
                LightKeyValPair curPair = new LightKeyValPair();
                curPair.value = 1;
                curPair.category = currCondNm;
                currDimColl.put(currCondNm, curPair);
            }
        //}
    }
    
    private void copy(String source, String dest){
    	InputStream is = null;
    	OutputStream os = null;
    	File of = new File(dest);
    	byte[] buffer = new byte[1024];
    	int length;
    	//be careful, here it is "raw" copying, if the file already exists, it will no delete and replace it but rather append new data at the end.
    	try{
    		if(!of.exists()){
        		of.createNewFile();
        	}
    		is = new FileInputStream(source);
    		os = new FileOutputStream(of,false);
    		while((length = is.read(buffer))>0){
    			os.write(buffer, 0, length);
    		}
    		is.close();
			os.close();
    	}
    	catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
    }
}
