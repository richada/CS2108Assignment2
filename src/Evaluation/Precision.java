package Evaluation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by workshop on 9/18/2015.
 */
public class Precision {
    private static final int k = 20;
    /**
     * Please implement the evaluation function by yourselves.
     */
    public double getPrecision(String queryfile, ArrayList<String> resultnames){
    	int count = 0;
    	String category = queryfile.replace(".wav", "").replaceAll("[^a-zA-Z]","");
    	for (int i = 0; i < resultnames.size(); i ++){
    		String category1 = resultnames.get(i).replace(".wav", "").replaceAll("[^a-zA-Z]","");
    		if (category.equals(category1)){
    			count ++;
    		}
    	}
    	return count/(double)resultnames.size();
    }
    
}
