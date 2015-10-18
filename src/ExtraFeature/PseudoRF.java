package Evaluation;

import java.util.ArrayList;

public class PseudoRF {
	public ArrayList<String> getRFqueries(String queryfile, ArrayList<String> resultnames){
		ArrayList<String> newQueryFiles = new ArrayList<String>();
		Precision precision = new Precision();
		String category = queryfile.replace(".wav", "").replaceAll("[^a-zA-Z]","");
		if (precision.getPrecision(queryfile, resultnames) > 0.25){
			for (int i = 0; i < resultnames.size(); i ++){
				String category1 = resultnames.get(i).replace(".wav", "").replaceAll("[^a-zA-Z]","");
	    		if (category.equals(category1)){
	    			newQueryFiles.add(resultnames.get(i));
	    		}
	    		if (newQueryFiles.size() == 5){
	    			break;
	    		}
			}
			
		}
		return newQueryFiles;
	}

	
}
	
