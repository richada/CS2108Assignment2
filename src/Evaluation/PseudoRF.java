package Evaluation;

import java.util.ArrayList;

public class PseudoRF {
	public ArrayList<String> getRFqueries(String queryfile, ArrayList<String> resultnames){
		ArrayList<String> newQueryFiles = new ArrayList<String>();
		String category = queryfile.replace(".wav", "").replaceAll("[^a-zA-Z]","");
		for (int i = 0; i < resultnames.size(); i ++){
			String category1 = resultnames.get(i).replace(".wav", "").replaceAll("[^a-zA-Z]","");
			if (category.equals(category1)){
				newQueryFiles.add(resultnames.get(i));
			}
			else {
				break;
			}
		}
		return newQueryFiles;
	}
	public ArrayList<Double> computeWeightList(int length){
		ArrayList<Double> weight = new ArrayList<Double>();
		double w = 1;
		for(int i = 0; i < length - 1; i ++){
			weight.add(w*0.5);
			w *= 0.5;
		}
		weight.add(w * 2);
		return weight;
	}
	
	

	
}
	
