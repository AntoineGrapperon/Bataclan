
package SimulationObjects;

import Utils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.sun.java_cup.internal.runtime.Scanner;

/*
 * created by: b farooq, poly montreal
 * on: 22 october, 2013
 * last edited by: b farooq, poly montreal
 * on: 22 october, 2013
 * summary: 
 * comments:
 */

public class SpatialZone extends SimulationObject
{
    //private double myPerUntChosen;
    //public void SetPercUnitsChosen(double val)
    //{
    //    myPerUntChosen = val;
    //}
    public double GetApartmentPercent()
    {
        return (double) myDwellMarginal.GetValue("3")
                / ((double)myDwellMarginal.GetValue("0")
                + (double)myDwellMarginal.GetValue("1")
                + (double) myDwellMarginal.GetValue("2")
                + (double)myDwellMarginal.GetValue("3"));
    }

    ///////////////////////////////////
    // For Household Synthesis

    //public DiscreteCondDistribution censusPersonConditionals;

    public ModelDistribution modelUnivDegConditionals;
    public ModelDistribution modelIncConditionals;
    public ModelDistribution modelCarsConditionals;
    public ModelDistribution modelDwellConditionals;

    public DiscreteMarginalDistribution myCarsMarginal;
    public DiscreteMarginalDistribution myDwellMarginal;
    public DiscreteMarginalDistribution myDwellMarginalCounts;
    public DiscreteMarginalDistribution myPersonMarginal;
    ///////////////////////////////////

    ///////////////////////////////////
    // For Person Synthesis
    public DiscreteMarginalDistribution myHhldSize2Marginal;
    public DiscreteMarginalDistribution mySexMarginal;
    public DiscreteMarginalDistribution myAgeMarginal;
    public DiscreteMarginalDistribution myEducationMarginal;

    //public HashMap <String, Object> myAttributesDiscConditional = new HashMap <String, Object>();
    public ArrayList<DiscreteCondDistribution> myAttributesDiscConditional = new ArrayList<DiscreteCondDistribution>();
    public ArrayList<ModelDistribution> myAttributesModel = new ArrayList<ModelDistribution>();

    
    ///////////////////////////////////

    public SpatialZone()
    {
        ///////////////////////////////////
        // For Household Synthesis

        //censusPersonConditionals = new DiscreteCondDistribution();

        modelIncConditionals = new ModelDistribution();
        modelIncConditionals.SetDimensionName("IncomeLevel");
        modelUnivDegConditionals = new ModelDistribution();
        modelUnivDegConditionals.SetDimensionName("NumWithUnivDeg");
        modelDwellConditionals = new ModelDistribution();
        modelDwellConditionals.SetDimensionName("DwellingType");
        modelCarsConditionals = new ModelDistribution();
        modelCarsConditionals.SetDimensionName("NumOfCars");

        myDwellMarginal = new DiscreteMarginalDistribution();
        myDwellMarginal.SetDimensionName("DwellingType");

        myDwellMarginalCounts = new DiscreteMarginalDistribution();
        myDwellMarginalCounts.SetDimensionName("DwellingType");

        myCarsMarginal = new DiscreteMarginalDistribution();
        myCarsMarginal.SetDimensionName("NumOfCars");

        myPersonMarginal = new DiscreteMarginalDistribution();
        myPersonMarginal.SetDimensionName("HouseholdSize");
        ///////////////////////////////////

        ///////////////////////////////////
        // For Person Synthesis
        myHhldSize2Marginal = new DiscreteMarginalDistribution();
        myHhldSize2Marginal.SetDimensionName("HouseholdSize2");

        mySexMarginal = new DiscreteMarginalDistribution();
        mySexMarginal.SetDimensionName("Sex");

        myAgeMarginal = new DiscreteMarginalDistribution();
        myAgeMarginal.SetDimensionName("MaritalStatus");

        myEducationMarginal = new DiscreteMarginalDistribution();
        myEducationMarginal.SetDimensionName("EducationLevel");
        

    	int numberofmodels = 1;
    	int numberofcounts = 1;
    	
        for (int i=0; i<ConfigFile.AttributeDefinitions.size(); i++)
        {	
        	if (ConfigFile.TypeOfConditionals.get(i).equals("count"))
        	{ 
        		numberofcounts += numberofcounts;
        		
        		DiscreteCondDistribution myAttributeDiscConditional = new DiscreteCondDistribution();
        		myAttributeDiscConditional.SetDimensionName(ConfigFile.AttributeDefinitions.get(i-numberofmodels+1).category);
        		myAttributesDiscConditional.add(myAttributeDiscConditional);
        		
        	}
        	
        	else if (ConfigFile.TypeOfConditionals.get(i).equals("model"))
        	{
        		numberofmodels += numberofmodels;
        		
        		ModelDistribution myAttributeModel = new ModelDistribution();
            	myAttributeModel.SetDimensionName(ConfigFile.AttributeDefinitions.get(i-numberofcounts+1).category);
            	myAttributesModel.add(myAttributeModel);
        	}    		
        }
        	
        	myAttributes = new ArrayList <KeyValPair>();	
 }
    
    public HashMap<String, DiscreteMarginalDistribution> marginalCounters = new HashMap<String, DiscreteMarginalDistribution>();
    public HashMap<String, DiscreteMarginalDistribution> marginalTargets = new HashMap<String, DiscreteMarginalDistribution>();
    
    public SpatialZone(String type)
    {
        // For Person Synthesis

    	int numberofmodels = 1;
    	int numberofcounts = 1;
    	
        for (int i=0; i<ConfigFile.AttributeDefinitionsImportance.size(); i++)
        {	
        	if (ConfigFile.TypeOfConditionalsImportance.get(i).equals("count"))
        	{ 
        		numberofcounts += numberofcounts;
        		
        		DiscreteCondDistribution myAttributeDiscConditional = new DiscreteCondDistribution();
        		myAttributeDiscConditional.SetDimensionName(ConfigFile.AttributeDefinitionsImportance.get(i-numberofmodels+1).category);
        		myAttributesDiscConditional.add(myAttributeDiscConditional);
        		
        		//System.out.println(ConfigFile.AttributeDefinitionsImportance.get(i-numberofmodels+1).category);
        		DiscreteMarginalDistribution currCounter = new DiscreteMarginalDistribution();
        		currCounter.SetDimensionName(ConfigFile.AttributeDefinitionsImportance.get(i-numberofmodels+1).category);
        		marginalCounters.put(ConfigFile.AttributeDefinitionsImportance.get(i-numberofmodels+1).category, currCounter);
        		
        		DiscreteMarginalDistribution currTarget = new DiscreteMarginalDistribution();
        		currTarget.SetDimensionName(ConfigFile.AttributeDefinitionsImportance.get(i-numberofmodels+1).category);
        		marginalTargets.put(ConfigFile.AttributeDefinitionsImportance.get(i-numberofmodels+1).category, currTarget);
        		
        		/*for(int j= 0; j< ConfigFile.AttributeDefinitionsImportance.get(i).value ; j++){
        			
        		}*/
        	}
        	
        	else if (ConfigFile.TypeOfConditionalsImportance.get(i).equals("model"))
        	{
        		numberofmodels += numberofmodels;
        		
        		ModelDistribution myAttributeModel = new ModelDistribution();
            	myAttributeModel.SetDimensionName(ConfigFile.AttributeDefinitions.get(i-numberofcounts+1).category);
            	myAttributesModel.add(myAttributeModel);
        	}    		
        }
        	
        	myAttributes = new ArrayList <KeyValPair>();	
 }

    private double averageIncome;

    public double GetAverageIncome()
    {
        return averageIncome;
    }

    public void SetAverageIncome(double avgInc)
    {
        averageIncome = avgInc;
    }
    private double percentHighEducated;

    public double GetPercentHighEducated()
    {
        return percentHighEducated;
    }

    public void SetPercentHighEducated(double perHighEdu)
    {
        percentHighEducated = perHighEdu;
    }

    private KeyValPair hhldControlTotal;
        
    public void SetHhldControlTotal(String key, int val)
    {
        hhldControlTotal.category = key;
        hhldControlTotal.value = val;
    }

    public ArrayList GetDataHhldCollectionsList()
    {
        ArrayList currColl = new ArrayList();

        currColl.add((ConditionalDistribution)modelIncConditionals);
        currColl.add((ConditionalDistribution)modelUnivDegConditionals);
        currColl.add((ConditionalDistribution)modelDwellConditionals);
        currColl.add((ConditionalDistribution)modelCarsConditionals);

        return currColl;
    }

    public ArrayList GetPersonDataCollectionsList()
    {
    	ArrayList<ConditionalDistribution> currColl = new ArrayList<ConditionalDistribution>();
    	
    	int numberofmodels = 1;
    	int numberofcounts = 1;
    	
    	for (int i=0; i<ConfigFile.AttributeDefinitions.size(); i++)
    	{
    		if (ConfigFile.TypeOfConditionals.get(i).equals("count"))
    		{
    				numberofcounts += numberofcounts;
    			
    				currColl.add((ConditionalDistribution) myAttributesDiscConditional.get(i-numberofmodels+1));
    		}
        	
    		else if (ConfigFile.TypeOfConditionals.get(i).equals("model"))
    		{
    				numberofmodels += numberofmodels;
    				currColl.add((ConditionalDistribution) myAttributesModel.get(i-numberofcounts+1));
    		}
    	}
    	   return currColl;
    }
    
    public ArrayList GetZonalCondCollections()
    {
    	ArrayList<ConditionalDistribution> currColl = new ArrayList<ConditionalDistribution>();
    	
    	int numberofmodels = 1;
    	int numberofcounts = 1;
    	
    	for (int i=0; i<ConfigFile.AttributeDefinitionsImportance.size(); i++)
    	{
    		if (ConfigFile.TypeOfConditionalsImportance.get(i).equals("count"))
    		{
    				numberofcounts += numberofcounts;
    			
    				currColl.add((ConditionalDistribution) myAttributesDiscConditional.get(i-numberofmodels+1));
    		}
        	
    		else if (ConfigFile.TypeOfConditionalsImportance.get(i).equals("model"))
    		{
    				numberofmodels += numberofmodels;
    				currColl.add((ConditionalDistribution) myAttributesModel.get(i-numberofcounts+1));
    		}
    	}
    	   return currColl;
    }

    public DiscreteMarginalDistribution GetHousholdSizeDist()
    {
        return myPersonMarginal;
    }

    public int GetNumHhldWOneCar()
    {
        return (int) myCarsMarginal.GetValue("1");
    }

    public int GetNumHhldWTwoCar()
    {
        return (int) myCarsMarginal.GetValue("2");
    }

    public int GetNumHhldWThreeCar()
    {
        return (int) myCarsMarginal.GetValue("3");
    }

    public double GetPercentHhldWOneCar()
    {
        double sum = myCarsMarginal.GetValue("1")
                    + myCarsMarginal.GetValue("2")
                    + myCarsMarginal.GetValue("3");

        if (sum > 0.00)
        {
            return myCarsMarginal.GetValue("1") / sum;
        }
        return 0.00;
    }

    public double GetPercentHhldWTwoCar()
    {
        double sum = myCarsMarginal.GetValue("1")
                    + myCarsMarginal.GetValue("2")
                    + myCarsMarginal.GetValue("3");

        if (sum > 0.00)
        {
            return myCarsMarginal.GetValue("2") / sum;
        }    
        return 0.00;
    }

    public double GetPercentHhldWThreeCar()
    {
        double sum = myCarsMarginal.GetValue("1")
                    + myCarsMarginal.GetValue("2")
                    + myCarsMarginal.GetValue("3");

        if (sum > 0.00)
        {
            return myCarsMarginal.GetValue("3") / sum;
        }
        return 0.00;
    }

    public DiscreteMarginalDistribution GetCarMarginal()
    {
        return myCarsMarginal;
    }

    public DiscreteMarginalDistribution GetDwellingMarginals()
    {
        return myDwellMarginal;
    }

    public DiscreteMarginalDistribution GetDwellingMarginalsByCount()
    {
        return myDwellMarginalCounts;
    }

    public DiscreteMarginalDistribution GetPersonHhldSizeMarginal()
    {
        return myHhldSize2Marginal;
    }

    public DiscreteMarginalDistribution GetPersonSexMarginal()
    {
        return mySexMarginal;
    }

    public DiscreteMarginalDistribution GetPersonAgeMarginal()
    {
        return myAgeMarginal;
    }

    public DiscreteMarginalDistribution GetPersonEduMarginal()
    {
        return myEducationMarginal;
    }

	/*public void updateCounters(Person agent) {
		// TODO Auto-generated method stub
		Iterator it = agent.myAttributes.iterator();
		while(it.hasNext()){
			KeyValPair k = (KeyValPair)it.next();
			if(marginalCounters.containsKey(k.category)){
				marginalCounters.get(k.category).update(ConfigFile.fmt(k.value));
			}
			else{
				System.out.println("little problem");
			}
			
		}
	}*/
    

   /* public void printLocalMarginalFittingAnalysis(int localPool){
    	
    	OutputFileWritter outputFile = new OutputFileWritter();
    	try{
    		outputFile.OpenFile(Utils.DATA_DIR + "data\\" + ConfigFile.fmt(myAttributes.get(0).value) + "\\marginalCheck.csv");
    		
    		String config = "Marginal checking for " + ConfigFile.fmt(myAttributes.get(0).value);
    		outputFile.WriteToFile(config);
    		config = "Warmup iterations: " + Utils.COLUMN_DELIMETER + Utils.WARMUP_ITERATIONS;
    		outputFile.WriteToFile(config);
    		config = "Skip iterations:" + Utils.COLUMN_DELIMETER + Utils.SKIP_ITERATIONS;
    		outputFile.WriteToFile(config);
    		config = "Pool count:" + Utils.COLUMN_DELIMETER + localPool;
    		outputFile.WriteToFile(config);

    		for(int i = 0; i < ConfigFile.AttributeDefinitionsImportance.size(); i++){
    			String headers = myAttributesDiscConditional.get(i).GetDimensionName();
    			for(int j = 0; j < ConfigFile.AttributeDefinitionsImportance.get(i).value; j++){
    				headers = headers + Utils.COLUMN_DELIMETER  + Integer.toString(j);	
    			}
    			outputFile.WriteToFile(headers);
    			String target = "Distribution target";
    			String result = "Number generated";
    			
    			double stdDev = 0;
    			double absErr = 0;
    			double popTot = 0;
    			
    			for(int j = 0; j < ConfigFile.AttributeDefinitionsImportance.get(i).value; j++){
    				//System.out.println();
    				String tempCat = myAttributesDiscConditional.get(i).GetDimensionName();
    				target = target + Utils.COLUMN_DELIMETER + marginalTargets.get(tempCat).GetValue(ConfigFile.fmt(j));
    				result = result + Utils.COLUMN_DELIMETER + marginalCounters.get(tempCat).GetValue(ConfigFile.fmt(j));
    				
    				stdDev = stdDev + (marginalTargets.get(tempCat).GetValue(ConfigFile.fmt(j))-marginalCounters.get(tempCat).GetValue(ConfigFile.fmt(j)))
    						*(marginalTargets.get(tempCat).GetValue(ConfigFile.fmt(j))-marginalCounters.get(tempCat).GetValue(ConfigFile.fmt(j)));
    				absErr = absErr + Math.abs(marginalTargets.get(tempCat).GetValue(ConfigFile.fmt(j))-marginalCounters.get(tempCat).GetValue(ConfigFile.fmt(j)));
    				popTot = popTot + marginalTargets.get(tempCat).GetValue(ConfigFile.fmt(j));
    			
    			}
    			
    			stdDev = stdDev/popTot;
    			outputFile.WriteToFile(target);
    			outputFile.WriteToFile(result);
    		}
    		
        	outputFile.CloseFile();
    	}
    	catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
     
    }*/
    
    public String getLocalStatistics(int localPool){
    	
    	
    	String statAnalysis = ConfigFile.fmt(myAttributes.get(0).value) + Utils.COLUMN_DELIMETER + myAttributes.get(1).value;
    	for(int i = 0; i < ConfigFile.AttributeDefinitionsImportance.size(); i++){
			
			double stdDev = 0;
			double absErr = 0;
			double popTot = 0;
			double relativeErr = 0;
			
			for(int j = 0; j < ConfigFile.AttributeDefinitionsImportance.get(i).value; j++){
				//System.out.println();
				String tempCat = myAttributesDiscConditional.get(i).GetDimensionName();
				
				stdDev = stdDev + (marginalTargets.get(tempCat).GetValue(ConfigFile.fmt(j))-marginalCounters.get(tempCat).GetValue(ConfigFile.fmt(j)))
						*(marginalTargets.get(tempCat).GetValue(ConfigFile.fmt(j))-marginalCounters.get(tempCat).GetValue(ConfigFile.fmt(j)));
				absErr = absErr + Math.abs(marginalTargets.get(tempCat).GetValue(ConfigFile.fmt(j))-marginalCounters.get(tempCat).GetValue(ConfigFile.fmt(j)));
				popTot = popTot + marginalTargets.get(tempCat).GetValue(ConfigFile.fmt(j));
			
			}
			
			stdDev = stdDev/popTot;
			relativeErr = absErr/popTot;
			
			statAnalysis = statAnalysis	+ Utils.COLUMN_DELIMETER + stdDev + Utils.COLUMN_DELIMETER + absErr + Utils.COLUMN_DELIMETER + relativeErr;
		}
    	return statAnalysis;
    }
    
    public String getSRMSEstatistic(){
    	String stat = new String();
    	for(int i = 0; i < ConfigFile.AttributeDefinitionsImportance.size(); i++){
    		String tempCat = myAttributesDiscConditional.get(i).GetDimensionName();
    		double num = 0;
        	double den = 0;
    		for(int j = 0; j < ConfigFile.AttributeDefinitionsImportance.get(i).value; j++){
    			double catTarget = marginalTargets.get(tempCat).GetValue(ConfigFile.fmt(j));
				double catResult = marginalCounters.get(tempCat).GetValue(ConfigFile.fmt(j));
    			num += (catResult - catTarget) * (catResult - catTarget);
    			den += catTarget;
    		}
    		num = Math.sqrt(num/den);
    		den = den/den;
    		num = num/den;
    		stat = stat + Utils.COLUMN_DELIMETER + num;
    	}
    	return stat;
    }
    
    /**
     * The Total Absolute Error is defined as the absolute difference between estimated probability rates for observed and created population.
     * Source: Ballas, D., Clarke, G., & Turton, I. (1999, July). Exploring microsimulation methodologies for the estimation of household attributes. In 4th International Conference on GeoComputation, Mary Washington College, Virginia, USA.
     * @return
     */
    public String getTotalAbsoluteError(){
    	String  stat = new String();
    	for(int i = 0; i < ConfigFile.AttributeDefinitionsImportance.size(); i++){
    		String tempCat = myAttributesDiscConditional.get(i).GetDimensionName();
    		for(int j = 0; j < ConfigFile.AttributeDefinitionsImportance.get(i).value; j++){
    			double target = marginalTargets.get(tempCat).GetValue(ConfigFile.fmt(j));
            	double result = marginalCounters.get(tempCat).GetValue(ConfigFile.fmt(j));
            	if(result!=0){
        			stat += Utils.COLUMN_DELIMETER + Math.abs(target-result);
            	}
            	else{
            		stat += Utils.COLUMN_DELIMETER + " ";
            	}
    		}
    	}
    	return stat;
    }
    
    /**
     * The Santardised Absolute Error is the sum of the absolute differences between estimated and observed counts divided by total population.
     * Source : Huang, Z., & Williamson, P. (2001). A comparison of synthetic reconstruction and combinatorial optimisation approaches to the creation of small-area microdata. Department of Geography, University of Liverpool.
     * @return
     */
    public String getStandardizedAbsoluteError(){
    	String  stat = new String();
    	for(int i = 0; i < ConfigFile.AttributeDefinitionsImportance.size(); i++){
    		String tempCat = myAttributesDiscConditional.get(i).GetDimensionName();
    		for(int j = 0; j < ConfigFile.AttributeDefinitionsImportance.get(i).value; j++){
    			double target = marginalTargets.get(tempCat).GetValue(ConfigFile.fmt(j));
            	double result = marginalCounters.get(tempCat).GetValue(ConfigFile.fmt(j));
            	if(result!=0){
        			stat += Utils.COLUMN_DELIMETER + Math.abs(target-result)/result;
            	}
            	else{
            		stat += Utils.COLUMN_DELIMETER + " ";
            	}
    		}
    	}
    	return stat;
    	
    	/*String  stat = new String();
    	for(int i = 0; i < ConfigFile.AttributeDefinitionsImportance.size(); i++){
    		String tempCat = myAttributesDiscConditional.get(i).GetDimensionName();
    		double pop = 0;
    		double absDiff = 0;
    		for(int j = 0; j < ConfigFile.AttributeDefinitionsImportance.get(i).value; j++){
    			double target = marginalTargets.get(tempCat).GetValue(ConfigFile.fmt(j));
            	double result = marginalCounters.get(tempCat).GetValue(ConfigFile.fmt(j));
    			absDiff = Math.abs(target-result);
    			pop += result;
    		}
    		stat += Utils.COLUMN_DELIMETER + absDiff/pop;
    	}
    	return stat;*/
    }
    
    
    public String printLocalMarginalFittingAnalysis(int localPool){
    	
    	OutputFileWritter outputFile = new OutputFileWritter();
    	String statAnalysis = ConfigFile.fmt(myAttributes.get(0).value) + Utils.COLUMN_DELIMETER + myAttributes.get(1).value;
    	try{
    		outputFile.OpenFile(Utils.DATA_DIR + "data\\" + ConfigFile.fmt(myAttributes.get(0).value) + "\\marginalCheck.csv");
    		
    		String config = "Marginal checking for " + ConfigFile.fmt(myAttributes.get(0).value);
    		outputFile.WriteToFile(config);
    		config = "Warmup iterations: " + Utils.COLUMN_DELIMETER + Utils.WARMUP_ITERATIONS;
    		outputFile.WriteToFile(config);
    		config = "Skip iterations:" + Utils.COLUMN_DELIMETER + Utils.SKIP_ITERATIONS;
    		outputFile.WriteToFile(config);
    		config = "Pool count:" + Utils.COLUMN_DELIMETER + localPool;
    		outputFile.WriteToFile(config);
    		
    		
    		
    		for(int i = 0; i < ConfigFile.AttributeDefinitionsImportance.size(); i++){
    			String headers = myAttributesDiscConditional.get(i).GetDimensionName();
    			for(int j = 0; j < ConfigFile.AttributeDefinitionsImportance.get(i).value; j++){
    				headers = headers + Utils.COLUMN_DELIMETER  + Integer.toString(j);	
    			}
    			outputFile.WriteToFile(headers);
    			String target = "Distribution target";
    			String result = "Number generated";
    			
    			double stdDev = 0;
    			double absErr = 0;
    			double popTot = 0;
    			double relativeErr = 0;
    			
    			for(int j = 0; j < ConfigFile.AttributeDefinitionsImportance.get(i).value; j++){
    				//System.out.println();
    				String tempCat = myAttributesDiscConditional.get(i).GetDimensionName();
    				target = target + Utils.COLUMN_DELIMETER + marginalTargets.get(tempCat).GetValue(ConfigFile.fmt(j));
    				result = result + Utils.COLUMN_DELIMETER + marginalCounters.get(tempCat).GetValue(ConfigFile.fmt(j));
    				
    				stdDev = stdDev + (marginalTargets.get(tempCat).GetValue(ConfigFile.fmt(j))-marginalCounters.get(tempCat).GetValue(ConfigFile.fmt(j)))
    						*(marginalTargets.get(tempCat).GetValue(ConfigFile.fmt(j))-marginalCounters.get(tempCat).GetValue(ConfigFile.fmt(j)));
    				absErr = absErr + Math.abs(marginalTargets.get(tempCat).GetValue(ConfigFile.fmt(j))-marginalCounters.get(tempCat).GetValue(ConfigFile.fmt(j)));
    				popTot = popTot + marginalTargets.get(tempCat).GetValue(ConfigFile.fmt(j));
    			
    			}
    			
    			stdDev = Math.sqrt(stdDev/popTot);
    			relativeErr = absErr/popTot;
    			
    			statAnalysis = statAnalysis	+ Utils.COLUMN_DELIMETER 
    					+ stdDev + Utils.COLUMN_DELIMETER 
    					+ absErr + Utils.COLUMN_DELIMETER 
    					+ relativeErr;

    			outputFile.WriteToFile(target);
    			outputFile.WriteToFile(result);
    		}
    		
        	outputFile.CloseFile();
        	
    	}
    	catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
    	return statAnalysis;
     
    }

	public void updateStatisticsDatasets(int zoneId, HashMap stdDev, HashMap absErr) {
		// TODO Auto-generated method stub
		System.out.println(zoneId + "  " + myAttributes.get(0).value);
		
		/*String headers = "zoneId" + Utils.COLUMN_DELIMETER;
		
		for(int i = 0; i < ConfigFile.AttributeDefinitions.size(); i++){
			headers = Utils.COLUMN_DELIMETER + myAttributesDiscConditional.get(i).GetDimensionName() + "_StdDev" + Utils.COLUMN_DELIMETER + myAttributesDiscConditional.get(i).GetDimensionName() + "_AbsErr" ;
		}*/
		
		long currStdDev = 0;
		stdDev.put(zoneId,currStdDev);
	}

	public String printLocalPopulation(int value) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTargets() {
		// TODO Auto-generated method stub
		String  stat = new String();
    	for(int i = 0; i < ConfigFile.AttributeDefinitionsImportance.size(); i++){
    		String tempCat = myAttributesDiscConditional.get(i).GetDimensionName();
    		for(int j = 0; j < ConfigFile.AttributeDefinitionsImportance.get(i).value; j++){
    			double target = marginalTargets.get(tempCat).GetValue(ConfigFile.fmt(j));
        		stat += Utils.COLUMN_DELIMETER + target;
    		}
    	}
    	return stat;
	}
	
	public String getResultStats() {
		// TODO Auto-generated method stub
		String  stat = new String();
    	for(int i = 0; i < ConfigFile.AttributeDefinitionsImportance.size(); i++){
    		String tempCat = myAttributesDiscConditional.get(i).GetDimensionName();
    		for(int j = 0; j < ConfigFile.AttributeDefinitionsImportance.get(i).value; j++){
    			double result = marginalCounters.get(tempCat).GetValue(ConfigFile.fmt(j));
        		stat += Utils.COLUMN_DELIMETER + result;
    		}
    	}
    	return stat;
	}
	
	
    
}
