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
	    		ArrayList<String> cutoffk = (ArrayList<String>) resultnames.subList(0, i + 1);
	    		double pk = precision.getPrecision(queryfile,cutoffk);
	    		double rk = 0;
	    		String category1 = resultnames.get(i).replace(".wav", "").replaceAll("[^a-zA-Z]","");
	    		if (category.equals(category1)){
	    			rk = 1;
	    		}
	    		average += pk * rk;
	    	}
	    	int relevantCount = 0;
	    	File folder = new File("data/input/train");
	    	File[] listOfFiles = folder.listFiles();

	    	for (int i = 0; i < listOfFiles.length; i++) {
	    		if ((listOfFiles[i].isFile())) {
	    			String filename = listOfFiles[i].getName();
	    			String category1 = filename.replace(".wav", "").replaceAll("[^a-zA-Z]","");
	    			if (category.equals(category1)){
	    				relevantCount ++;
	    				}
	    			}
	    		}
	    	average = average/(double)relevantCount;
	    	return average;
	    }

}
