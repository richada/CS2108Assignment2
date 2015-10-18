package Evaluation;

import java.io.File;
import java.util.ArrayList;

public class AveragePrecision {
	 private static final int k = 20;
	    /**
	     * Please implement the evaluation function by yourselves.
	     */
	    public double getAveragePrecision(String queryfile, ArrayList<String> resultnames){
	    	Precision precision = new Precision();
	    	
	    	String category = queryfile.replace(".wav", "").replaceAll("[^a-zA-Z]","");
	    	double average = 0.0;
	    	
	    	for (int i = 0; i < k; i ++){
	    		ArrayList<String> cutoffk = new ArrayList<String>();
	    		for(int j=0; j<i+1; j++){
	    			cutoffk.add(resultnames.get(j));
	    		}
	    		double pk = precision.getPrecision(queryfile,cutoffk);
	    		double rk = 0;
	    		String category1 = resultnames.get(i).replace(".wav", "").replaceAll("[^a-zA-Z]","");
	    		if (category.equals(category1)){
	    			rk = 1;
	    		}
	    		average += pk * rk;
	    	}
//	    	int relevantCount = 0;
//	    	
//	    	for (int i = 0; i < resultnames.size(); i++) {
//	    		String filename = resultnames.get(i);
//	    		String category1 = filename.replace(".wav", "").replaceAll("[^a-zA-Z]","");
//	    		if (category.equals(category1)){
//	    			relevantCount ++;
//	    		}
//	   		}
//	    	System.out.println(average + "\t" + relevantCount);
//	    	average = average/(double)relevantCount;
	    	return average/(double)20;
	    }

}
