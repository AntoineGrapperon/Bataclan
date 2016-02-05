package Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.sun.java_cup.internal.runtime.Scanner;

import SimulationObjects.Person;


public  class  ConfigFile 
{
	
	public static ArrayList<KeyValPair> AttributeDefinitions = new ArrayList<KeyValPair>();
	public static ArrayList<KeyValPair> AttributeDefinitionsImportance = new ArrayList<KeyValPair>();
	public static ArrayList<String> AttributesImportanceNames = new ArrayList<String>();
	public static String GetAgentTyp;
	public static ArrayList<String> AttributesPaths = new ArrayList<String>();
	public static ArrayList<String> AttributesPathsImportance = new ArrayList<String>();
	public static ArrayList<String> AttributesWithModel = new ArrayList <String>();
	public static ArrayList<Integer> NumberOfCategories = new ArrayList <Integer>();
	public static ArrayList<String> TypeOfConditionals = new ArrayList<String>();
	public static ArrayList<String> TypeOfConditionalsImportance = new ArrayList<String>();
	public static ArrayList<String> rankOfConditionals = new ArrayList<String>();
	public static int NumberOfAttributesWithCount=-1;
	public static int NumberOfAttributesWithModel=-1;
	public static String ZonalFile;
	
	public static void resetConfigFile(){
		  AttributeDefinitions = new ArrayList<KeyValPair>();
		 AttributeDefinitionsImportance = new ArrayList<KeyValPair>();
		 AttributesImportanceNames = new ArrayList<String>();
		  AttributesPaths = new ArrayList<String>();
		  AttributesPathsImportance = new ArrayList<String>();
		 AttributesWithModel = new ArrayList <String>();
		  NumberOfCategories = new ArrayList <Integer>();
		 TypeOfConditionals = new ArrayList<String>();
		  TypeOfConditionalsImportance = new ArrayList<String>();
		 rankOfConditionals = new ArrayList<String>();
		  NumberOfAttributesWithCount=-1;
		  NumberOfAttributesWithModel=-1;
		 
	}
		
	public  static void Initialize(String config_file) throws IOException
	{
		InitializeMyAttribute(config_file);
		GetPaths(config_file) ;
	}
	
	public  static void InitializeImportance(String config_file) throws IOException
	{
		InitializeMyAttributeImportance(config_file);
		GetPathsAndRanks(config_file) ;
	}
	
	public static void InitializeMyAttribute(String config_file) throws IOException
	{
		InputDataReader currReader = new InputDataReader();
		currReader.OpenFile(config_file);
		ArrayList <String> currConfig = currReader.StoreLineByLine();
		
	 	for (int i=7; i < currConfig.size(); i++)
	 	{
	 		int d = currConfig.get(i).indexOf("(");
	 		int f = currConfig.get(i).indexOf(")");
	 		String sousChaine = currConfig.get(i).substring(d+1,f);
			String[] resultat = sousChaine.split(",");
			
	 	 	KeyValPair myAttribute = new KeyValPair();
	 		
	 		myAttribute.category = currConfig.get(i).substring(0,d);
	 		myAttribute.value = resultat.length ;
	 		
	 		AttributeDefinitions.add(myAttribute);
	 	}
			GetAgentTyp = currConfig.get(5);
	 		currReader.CloseFile();
	 }

public static void InitializeMyAttributeImportance(String config_file) throws IOException
{
	InputDataReader currReader = new InputDataReader();
	currReader.OpenFile(config_file);
	ArrayList <String> currConfig = currReader.StoreLineByLine();
	String[] list;
 	for (int i=7; i < currConfig.size(); i++)
 	{
 		list = currConfig.get(i).split("=");
 		if(list[3].equals("main")){
 			int d = currConfig.get(i).indexOf("(");
 	 		int f = currConfig.get(i).indexOf(")");
 	 		String sousChaine = currConfig.get(i).substring(d+1,f);
 			String[] resultat = sousChaine.split(",");
 			
 	 	 	KeyValPair myAttribute = new KeyValPair();
 	 		
 	 		myAttribute.category = currConfig.get(i).substring(0,d);
 	 		myAttribute.value = resultat.length ;
 	 		AttributeDefinitions.add(myAttribute);
 	 		
 	 	}
 		else if(list[3].equals("importance")){
			int d = currConfig.get(i).indexOf("(");
	 		int f = currConfig.get(i).indexOf(")");
	 		String sousChaine = currConfig.get(i).substring(d+1,f);
			String[] resultat = sousChaine.split(",");
			
	 	 	KeyValPair myAttribute = new KeyValPair();
	 		
	 		myAttribute.category = currConfig.get(i).substring(0,d);
	 		myAttribute.value = resultat.length ;
	 		AttributesImportanceNames.add(currConfig.get(i).substring(0,d));
	 		
	 		AttributeDefinitionsImportance.add(myAttribute);
	 	}
 			GetAgentTyp = currConfig.get(5);
 	 		currReader.CloseFile();
 		}
 		
 }

public static void GetPaths(String config_file) throws IOException
{
	InputDataReader currReader = new InputDataReader();
	currReader.OpenFile(config_file);
	ArrayList <String> currConfig = currReader.StoreLineByLine();
		
	for (int i=7; i < currConfig.size(); i++)
	{
		int g = currConfig.get(i).indexOf("=");
		int h =currConfig.get(i).indexOf(")");
		String path = currConfig.get(i).substring(g+1);
		String type = currConfig.get(i).substring(h+2,g);
		AttributesPaths.add(path);
		TypeOfConditionals.add(type);
	}

	int k = currConfig.get(3).indexOf("=");
	ZonalFile = currConfig.get(3).substring(k+1);
	currReader.CloseFile();
	currReader.CloseFile();
		
	for (int i=0; i<TypeOfConditionals.size();i++)
	{
		if (TypeOfConditionals.get(i)== "count")
		{
			if (NumberOfAttributesWithCount == -1)
			{
				NumberOfAttributesWithCount = 1;
			}
			else
			{
				NumberOfAttributesWithCount += NumberOfAttributesWithCount;
			}
		}
	}
	
	NumberOfAttributesWithModel = TypeOfConditionals.size() - NumberOfAttributesWithCount;
}

public static void GetPathsAndRanks(String config_file) throws IOException
{
	InputDataReader currReader = new InputDataReader();
	currReader.OpenFile(config_file);
	ArrayList <String> currConfig = currReader.StoreLineByLine();
	String tmp;
	String[] list;
		
	for (int i=7; i < currConfig.size(); i++)
	{	
		list = currConfig.get(i).split("=");
 		if(list[3].equals("main")){
 			AttributesPaths.add(list[2]);
 			TypeOfConditionals.add(list[1]);
 			rankOfConditionals.add(list[3]);
 		}
 		if(list[3].equals("importance")){
 			AttributesPathsImportance.add(list[2]);
 			TypeOfConditionalsImportance.add(list[1]);
 			rankOfConditionals.add(list[3]);
 		}
	}
	int k = currConfig.get(3).indexOf("=");
	ZonalFile = currConfig.get(3).substring(k+1);
	currReader.CloseFile();
	currReader.CloseFile();
		
	for (int i=0; i<TypeOfConditionals.size();i++)
	{
		if (TypeOfConditionals.get(i)== "count" && rankOfConditionals.get(i) == "main")
		{
			if (NumberOfAttributesWithCount == -1)
			{
				NumberOfAttributesWithCount = 1;
			}
			else
			{
				NumberOfAttributesWithCount += NumberOfAttributesWithCount;
			}
		}
	}
	
	NumberOfAttributesWithModel = TypeOfConditionals.size() - NumberOfAttributesWithCount;
}

	public static String fmt(double d)
	{
	    if(d == (long) d)
	        return String.format("%d",(long)d);
	    else
	        return String.format("%s",d);
	}

	public static void updateCounters(Person agent, HashMap<String, DiscreteMarginalDistribution> marginalCounters) {
		// TODO Auto-generated method stub
		Iterator it = agent.myAttributes.iterator();
		while(it.hasNext()){
			KeyValPair k = (KeyValPair)it.next();
			if(marginalCounters.containsKey(k.category)){
				marginalCounters.get(k.category).update(ConfigFile.fmt(k.value));
			}
			else{
				//System.out.println("littleproblem");
			}
			
		}
	}

}

