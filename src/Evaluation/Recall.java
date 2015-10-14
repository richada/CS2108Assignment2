package Evaluation;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by workshop on 10/1/2015.
 */
public class Recall {
    private static final int k = 20;
    /**
     * Please implement the evaluation function by yourselves.
     */
    public double getRecall(String queryfile, ArrayList<String> resultnames){
    	int relevantCount = 0;
    	int retrievedCount = 0;
    	String category = queryfile.replace(".wav", "").replaceAll("[^a-zA-Z]","");
    	for (int i = 0; i < k; i ++){
    		String category1 = resultnames.get(i).replace(".wav", "").replaceAll("[^a-zA-Z]","");
    		if (category.equals(category1)){
    			retrievedCount ++;
    		}
    	}
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
    	return retrievedCount/(double)relevantCount;
    }
}
