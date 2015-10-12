package Distance;

/**
 * Created by workshop on 9/18/2015.
 */
public class CityBlock {
    public double getDistance(double[] query1, double[] query2){
        double distance;
    	
    	if (query1.length != query2.length){
            System.err.println("The dimension of the two vectors does not match!");

            System.exit(1);
        }

        double distanceSum = 0.0;
        double range = 0.0;
        double maximum = 0.0;
        double minimum = 0.0;

        for (int i = 0; i < query1.length; i++){
            distanceSum += Math.abs(query1[i]-query2[i]);
        }
        for (int i = 0; i < query1.length; i++){
            if (query1[i] > maximum){
            	maximum = query1[i];
            }
            if (query2[i] > maximum){
            	maximum = query2[i];
            }
        }
        minimum = maximum;
        for (int i = 0; i < query1.length; i++){
            if (query1[i] < minimum){
            	minimum = query1[i];
            }
            if (query2[i] < minimum){
            	minimum = query2[i];
            }
        }
        
        range = maximum - minimum;
        
        if (range != 0.0){
        	distance = distanceSum/range/query1.length;
        }
        else {
        	distance = 0.0;
        }

        return distance;
    }
}
