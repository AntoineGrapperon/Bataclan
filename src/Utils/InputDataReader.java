package Utils;

import SimulationObjects.SimulationObject;
import SimulationObjects.SpatialZone;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Scanner;


/**
 *
 * @author XPS 1645
 */

public class InputDataReader extends FileManager
{
	 public BufferedReader myFileReader;
     LineNumberReader reader;
     ArrayList conditionalNames;
     public static int nbOfAttWithModels;
     public static int maxNumOfCategories;
     public static ArrayList<ArrayList<String>> CategoriesOfAttributesWithModel;
     public static ArrayList<String> AttributesWithModel;
     public InputDataReader()
{
	conditionalNames = new ArrayList();
}

@Override
public void OpenFile(String fileName)
{
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


@Override
public void CloseFile() throws IOException
{
	myFileReader.close();
}
    
public ArrayList<String> StoreLineByLine() throws IOException
{
	String str;
    ArrayList<String> Line = new ArrayList<String>();
    	
    while ((str = myFileReader.readLine()) != null) 
    {
    	Line.add(str);
    }
    myFileReader.close();
    AttributesWithModel = new ArrayList<String>();
    AttributesWithModel = Line;
    return Line;
 }

        // Should be called before fillCollection1
        public void GetConditionalList() throws IOException
        {
            String strTok = myFileReader.readLine();
            String[] strs = strTok.split(",");
            for (int i = 1; i < strs.length; i++)
            {
                conditionalNames.add(strs[i]);
            }
        }
        
        @SuppressWarnings("resource")
		public ArrayList<ArrayList> GetZonalData() throws IOException
        {
        	String line=null;
        	Scanner scanner = null;
        	ArrayList<ArrayList> ZonalData = new ArrayList<ArrayList>();

        		int i=0;
        		while((line=myFileReader.readLine())!= null)
        		{
        			ZonalData.add(new ArrayList());
        			scanner = new Scanner(line);
        			scanner.useDelimiter(",");

        				while (scanner.hasNext())
        				{
        					String data = scanner.next();
        					ZonalData.get(i).add(data);
        				}
        				i++;
        		}
        	
        	return ZonalData;
        }

        // For zone by zone information (conditionals as columns)
        public String FillCollection1(DiscreteCondDistribution currColl) throws IOException
        {
            String strTok = myFileReader.readLine();
            if (strTok != null)
            {
                String[] strColl = strTok.split(",");
                for (int i = 0; i < conditionalNames.size(); i++)
                {
                    String currCond = (String) conditionalNames.get(i);
                    String[] currDimVal = currCond.split(Utils.CATEGORY_DELIMITER);

                    currColl.AddValue(currDimVal[0], currDimVal[1],
                                        Integer.parseInt(strColl[i+1]));
                }
                return strColl[0];
            }
            return "";
        }

        // For 1 values for all zones (conditionals as rows)
        public void FillCollection2(DiscreteCondDistribution currColl) throws IOException
        {
            String strTok = myFileReader.readLine();
            while ((strTok = myFileReader.readLine()) != null)
            {
                String[] strToken = strTok.split(",");
                if(!strToken[1].trim().equals("null")){
                	if(Integer.parseInt(strToken[1].trim())!= 0){
                		int j = strToken[0].indexOf(
                                Utils.CATEGORY_DELIMITER);
                        String catName = strToken[0].substring(0, j);
                        //System.out.println(catName);
                        //[AG] in the case where there is only 1 attribute, the substring is trying to extract a non existing portion of sentence, I  added a if/else structure to fix that
                        String condName;
                        if(strToken[0].substring(j+1).length() == 1){condName = strToken[0].substring(j);}
                        else{
                        	condName = strToken[0].substring(j + 1,
                        			strToken[0].length()); 
                        	/*if(condName.contains("--")){
                        		System.out.println("--wrong key format : " + condName);
                        	}*/
                        }
                        
                        /*string[] strToken = strTok.Split(',');
                        //int j =  strToken[0].IndexOf(
                        //        Utils.Constants.CATEGORY_DELIMITER);
                        string catName = strTok.Substring(0,1);
                        string condName = strTok.Substring(2, strTok.LastIndexOf(',')-2);*/

                        currColl.AddValue(catName, condName,
                                Double.parseDouble(strToken[1]));
                	}
                }
                
            }
        }
        
        public void FillCollection2(DiscreteCondDistribution currColl, DiscreteMarginalDistribution currTarget) throws IOException
        {
            String strTok = myFileReader.readLine();
            while ((strTok = myFileReader.readLine()) != null)
            {
                String[] strToken = strTok.split(",");
                if(!strToken[1].trim().equals("null")){
                	if(Integer.parseInt(strToken[1].trim())!= 0){
                		int j = strToken[0].indexOf(
                                Utils.CATEGORY_DELIMITER);
                        String catName = strToken[0].substring(0, j);
                        //System.out.println(catName);
                        //[AG] in the case where there is only 1 attribute, the substring is trying to extract a non existing portion of sentence, I  added a if/else structure to fix that
                        String condName;
                        if(strToken[0].substring(j+1).length() == 1){condName = strToken[0].substring(j);}
                        else{
                        	condName = strToken[0].substring(j + 1,
                        			strToken[0].length()); 
                        	/*if(condName.contains("--")){
                        		System.out.println("--wrong key format : " + condName);
                        	}*/
                        }
                        
                        /*string[] strToken = strTok.Split(',');
                        //int j =  strToken[0].IndexOf(
                        //        Utils.Constants.CATEGORY_DELIMITER);
                        string catName = strTok.Substring(0,1);
                        string condName = strTok.Substring(2, strTok.LastIndexOf(',')-2);*/

                        currColl.AddValue(catName, condName,
                                Double.parseDouble(strToken[1]));
                        currTarget.CumulValue(catName,Double.parseDouble(strToken[1]));
                        //System.out.println(catName + "  " +strToken[1]);
                        //System.out.println(currTarget.GetDimensionName());
                        
                	}
                }
                
            }
        }
        
        public void FillCollection3(ModelDistribution currColl, int modelnumber, SpatialZone curZ) throws IOException
        {
        	String stri;

        	ArrayList<String> Line = new ArrayList<String>();

            while (!(stri = reader.readLine()).equals("#CATEGORIES")) 
            {
            	if (reader.getLineNumber()> 1)
        		{
        		Line.add(stri);
        		}          	
            }
            
        	for (int i=1; i < Line.size(); i++)
         	{
         		String myAttribute = Line.get(i);
         		Line.add(myAttribute);
         	}
        	AttributesWithModel = new ArrayList<String>();
        	AttributesWithModel = Line;
        	nbOfAttWithModels = Line.size();
        	
        	String str;
        	ArrayList<String> CategoryLines = new ArrayList<String>();
        	while (!(str = reader.readLine()).equals("#UTILITY_FUNCTIONS"))
        	{
        		while (reader.getLineNumber()> (Line.size()+2))
        		{
        		CategoryLines.add(str);
        		break;
        		}
        	}
        	
        	ArrayList<Integer> NumberOfCategories = new ArrayList <Integer>();
        	ArrayList <Integer> CummNumberOfCategories = new ArrayList <Integer>();
        	
        	CategoriesOfAttributesWithModel = new ArrayList<ArrayList<String>>();
        	CategoriesOfAttributesWithModel.add(new ArrayList<String>());
        	for (int i=0; i < CategoryLines.size(); i++)
         	{	
        		String[] resultat = CategoryLines.get(i).split(",");
        		int a = 0;
        		a += resultat.length;
        		CummNumberOfCategories.add(0);
        		CummNumberOfCategories.add(a);
        		
        		for (int j=0; j<resultat.length ; j++)
        		{
        			CategoriesOfAttributesWithModel.get(i).add(resultat[j]);
        			NumberOfCategories.add(resultat.length);
        		}
         	}
        	
        	
        	ArrayList<String> UtilityLines = new ArrayList<String>();
        	
        	while ((str = reader.readLine()) != null)
        	{	
        		while ((reader.getLineNumber()> (2*Line.size()) +3 + CummNumberOfCategories.get(modelnumber)) && (reader.getLineNumber()< (2*Line.size()) +3 + CummNumberOfCategories.get(modelnumber) + NumberOfCategories.get(modelnumber)+1))
        		{
        			UtilityLines.add(str);
        			break;
        		}
        	}
        	
        	int maxCateg = 0;
        	
        	for ( int i = 0; i < NumberOfCategories.size(); i++)
        	{
        	    if ( NumberOfCategories.get(i) > maxCateg)
        	    {
        	    	maxCateg = NumberOfCategories.get(i);
        	    }
        	    maxNumOfCategories = maxCateg;
        	}
        	
        	int[] CoefSize = new int [UtilityLines.size()];
        	
        	for (int i=0;  i < UtilityLines.size(); i++)
        	{
        		int a = UtilityLines.get(i).indexOf("=");
        		String sousChaine = UtilityLines.get(i).substring(a+1);
        		String[] sousousChaine = sousChaine.split("\\+"); 
        		
        		CoefSize[i] =sousousChaine.length;	
        	}
        	
        	int max = 0;
        	
        	for ( int i = 0; i < CoefSize.length; i++)
        	{
        	    if ( CoefSize[i] > max)
        	    {
        	      max = CoefSize[i];
        	    }
        	}
        
        	double [][] UtilityCoefForAttributes = new double [UtilityLines.size()][max];
        	String [][] UtilityVariablesForAttributes = new String [UtilityLines.size()][max];
        	
        	for (int i=0;  i < UtilityLines.size(); i++)
        	{
        		int a = UtilityLines.get(i).indexOf("=");
        		String sousChaine = UtilityLines.get(i).substring(a+1);
        		String[] sousousChaine = sousChaine.split("\\+"); 
        		
        		for (int j=0; j<sousousChaine.length; j++)
        		{	
        			int b = sousousChaine[j].indexOf("*");
        			String sousousousChaine = sousousChaine[j].substring(0,b);
        			String sousousChaine1 = sousousChaine[j].substring(b+1);
        			UtilityCoefForAttributes[i][j] = Double.parseDouble(sousousousChaine);
        			UtilityVariablesForAttributes[i][j] = sousousChaine1;
        		}
        	}
        	
        	double [][] UtilityVariablesForAttributes1 = new double [UtilityLines.size()][max];
        	
        	for (int i=0; i<UtilityVariablesForAttributes.length; i++)
        	{ 
        		for (int j=0; j<UtilityVariablesForAttributes[i].length; j++)
        		{
        			if (UtilityVariablesForAttributes[i][j] != null)
        			{
        				for (int k=0; k<ConfigFile.AttributeDefinitions.size();k++)
        				{
        					if (UtilityVariablesForAttributes[i][j].endsWith(ConfigFile.AttributeDefinitions.get(k).category))
        					{
        						UtilityVariablesForAttributes1[i][j] = ConfigFile.AttributeDefinitions.get(k).value;
        					}
        				}
        				
        				for (int h=0; h<curZ.myAttributes.size();h++)
        				{
        					if (UtilityVariablesForAttributes[i][j].endsWith(curZ.myAttributes.get(h).category))
        					{
        						UtilityVariablesForAttributes1[i][j] = curZ.myAttributes.get(h).value;
        					}
        				}        				
        			
        			}
        			else UtilityVariablesForAttributes1[i][j]=0;
        		}
        		}		
        	
       
        	double [] UtilityValues = new double [maxCateg];
        	
        		for (int i=0; i<maxCateg; i++)
        		{
        			for( int j=0; j<UtilityLines.size(); j++, i++)
        			{
        				double a = -1;
        				for (int k=0; k<max; k++)
        				{
        					if (a == -1)
        					{
        						a = UtilityCoefForAttributes[j][k]*UtilityVariablesForAttributes1[j][k];
        					}
        					else
        					{
        						a += UtilityCoefForAttributes[j][k]*UtilityVariablesForAttributes1[j][k]; ;
        					}
        				}
        				UtilityValues[i]=a;
        			}			
        		}    
        		currColl.AddUtility(UtilityValues, modelnumber);
        }

        public boolean LoadZonalPopulationPool(ArrayList currPopPool) throws IOException
        {
            String strTok;
            int i = 0;
            while ((strTok = myFileReader.readLine()) != null
                    && i < Utils.POOL_COUNT)
            {
                currPopPool.add(strTok);
                i++;
            }
            if (i == Utils.POOL_COUNT || i > 0)
            {
                return true;
            }

            return false;
        }

        public boolean LoadZonalPopulationPoolByType(ArrayList currPopPool, String type) throws IOException
        {
            String strTok;
            int i = 0;
            while ((strTok = myFileReader.readLine()) != null
                    && i < Utils.POOL_COUNT)
            {
                String [] xStrs = strTok.split(",");
                if( xStrs[9].equals(type))
                {
                    strTok = xStrs[3] + "," + xStrs[4] + "," +
                            xStrs[5] + "," + xStrs[6] + "," +
                            xStrs[7] + "," + xStrs[8] + "," +
                            xStrs[9];
                    currPopPool.add(strTok);
                }
                i++;
            }
            if (i == Utils.POOL_COUNT || i > 0)
            {
                return true;
            }

            return false;
        }

        public void FillControlTotals(HashMap<String, Object> currTable) throws IOException
        {
            String strTok;
            myFileReader.readLine();
            while ((strTok = myFileReader.readLine()) != null)
            {
                String[] strToken = strTok.split(",");
                if (!currTable.containsKey(strToken[0]))
                {
                    currTable.put(strToken[0], Integer.parseInt(strToken[2]));
                }
            }
        }

        public void FillControlTotalsByDwellType(HashMap<String, Object> currTable) throws IOException
        {
            String strTok;
            myFileReader.readLine();
            while ((strTok = myFileReader.readLine()) != null)
            {
                String[] strToken = strTok.split(",");
                if (!currTable.containsKey(strToken[0]))
                {
                    currTable.put(strToken[0], strToken);
                }
            }
        }

        public void FillZonalData(HashMap<String, Object> currTable) throws IOException
        {
        	 ArrayList<ArrayList> currZonalData = new ArrayList<ArrayList>();
        	 currZonalData = GetZonalData();

        	 for (int i=0; i<currZonalData.size()-1; i++)
        	 {
        		 	SpatialZone curZ = new SpatialZone("Importance");
        			
        			for (int j=0; j<currZonalData.get(i).size();j++)
        			{
        				try{
        					//System.out.println( i + "  " + j);
            				KeyValPair myZonalData = new KeyValPair();
            				myZonalData.category = (String) currZonalData.get(0).get(j);
            				myZonalData.value = (double) Double.parseDouble((String) currZonalData.get(i+1).get(j));
            				curZ.myAttributes.add(myZonalData);
        				}
        				catch(IndexOutOfBoundsException e){
        					System.out.println("le i+1: " + i + "le j: " + j);
        				}
        			}
        			currTable.put((String) currZonalData.get(i+1).get(0),curZ);
        	 }
        }
        
        
}