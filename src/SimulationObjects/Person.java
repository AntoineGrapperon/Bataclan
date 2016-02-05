package SimulationObjects;

import Samplers.MetropolisHasting.KeyValDoublePair;
import SimulationObjects.AgentAttributes.*;
import Utils.KeyValPair;
import Utils.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.InternalFrameEvent;

import Utils.ConditionalDataReader;
import Utils.ConfigFile;
import Utils.DiscreteCondDistribution;
import Utils.InputDataReader;

/*
 * created by: b farooq, poly montreal
 * on: 22 october, 2013
 * last edited by: b farooq, poly montreal
 * on: 22 october, 2013
 * summary: 
 * comments:
 */

public class Person extends SimulationObject implements Serializable
{
	
	private static int idCounter = 0;
    private String myZoneID;
	
        public String GetZoneID()
        {
            return myZoneID;
        }
        public void SetZoneID(String id)
        {
            myZoneID = id;   
        }
     
        public Person() throws IOException        
        {
            myID = ++idCounter;
            
            Random r = new Random();
            ArrayList <KeyValPair> ConfigArray = ConfigFile.AttributeDefinitions;
            myAttributes = new ArrayList <KeyValPair>(ConfigArray.size());
                        
            for(int i=0; i<ConfigArray.size(); i++)
            {
	            int randvalue = r.nextInt((int)ConfigArray.get(i).value -1);
	            KeyValPair currAttribute = new KeyValPair();
	            
	            currAttribute.value = randvalue;
	            
	            currAttribute.category = ConfigArray.get(i).category;
	            
	            myAttributes.add(currAttribute);
            }
        }
         
        
        public Person(String currZone) throws IOException
        {
            myZoneID = currZone;
            myID = ++idCounter;
            
            Random r = new Random();
            ArrayList <KeyValPair> ConfigArray = ConfigFile.AttributeDefinitions;
            myAttributes = new ArrayList <KeyValPair>(ConfigArray.size());
                        
            for(int i=0; i<ConfigArray.size(); i++)
            {
            	int randvalue = r.nextInt((int)ConfigArray.get(i).value -1);
            
            	KeyValPair currAttribute = new KeyValPair();

            	currAttribute.value = randvalue;
            	currAttribute.category = ConfigArray.get(i).category;

            	myAttributes.add(currAttribute);

            }
        }
        
        public Person(String pathToSeeds, boolean test) throws IOException
        {
            
            //myID = ++idCounter;
        	myID = Utils.personId;
        	Utils.personId +=1;
            
            Random r = new Random();
            ArrayList <KeyValPair> ConfigArray = ConfigFile.AttributeDefinitions;
            myAttributes = new ArrayList <KeyValPair>(ConfigArray.size());
            
            ConditionalDataReader myDataReader = new ConditionalDataReader();
            myDataReader.OpenFile(pathToSeeds);
            String currRow = null;
            int rand = r.nextInt(Utils.numberOfSeeds)+32;
            int i=0;
            String headers = myDataReader.GetNextRow();
            String[] headersList = headers.split(",");
            while(i < rand){
            	currRow = myDataReader.GetNextRow();
            	i++;
            }
            //System.out.println(currRow);
            String[] currRowList = currRow.split(",");
            
            for(int j = 0; j<ConfigFile.AttributeDefinitions.size(); j++){
            	KeyValPair currAttribute = new KeyValPair();
            	currAttribute.category = headersList[j];
            	currAttribute.value = Double.parseDouble(currRowList[j]);
            	myAttributes.add(currAttribute);
            }
            
            Iterator it = myAttributes.iterator();
            while(it.hasNext()){
            	KeyValPair k = (KeyValPair)it.next();
            	//System.out.println(k.category + "   " +k.value);
            }
            
           
        }
	
    @Override
	public String GetNewJointKey(String baseDim) 
    {           	        
        String jointKey = null;
		try
		{				
			for (int i=0; i<myAttributes.size(); i++)
			{
				if (!((String) myAttributes.get(i).category).equals(baseDim)) {
					//System.out.println(baseDim + "  " + (String) myAttributes.get(i).category );
					if(jointKey==null)
					{
						jointKey = Integer.toString((int) myAttributes.get(i).value);
					}
					else
					{
						jointKey += Utils.CONDITIONAL_DELIMITER + Integer.toString((int) myAttributes.get(i).value);
						if((Integer.toString((int) myAttributes.get(i).value) != null || Integer.toString((int) myAttributes.get(i).value) != "")){
							
						}
						else{
							System.out.println((Integer.toString((int) myAttributes.get(i).value)) + " " + myAttributes.get(i).value);
						}
					}				    	
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(jointKey.contains("--")){
			System.out.println("--wrong key : " + jointKey);
		}
		
		
        return jointKey;
        }
    
    

    @Override
        public SimulationObject CreateNewCopy(String baseDim, int baseDimVal)
        {
    		   		
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos;
            try {
                oos = new ObjectOutputStream(bos);
                oos.writeObject(this);
                oos.flush();
                oos.close();
                bos.close();
                byte [] byteData = bos.toByteArray();

                ByteArrayInputStream bais = new ByteArrayInputStream(byteData);
                try
                
                {
                    Person myCopy = (Person)new ObjectInputStream(bais).readObject();
                  
                            
                    for (int i=0; i<myAttributes.size(); i++) {
                    	if (baseDim.equals(myAttributes.get(i).category))
                    		myCopy.myAttributes.get(i).value = baseDimVal;
                    }
                    
                    return myCopy;
                   
                
                }catch (ClassNotFoundException ex) {
                    Logger.getLogger(Household.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (IOException ex) {
                Logger.getLogger(Household.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
}
