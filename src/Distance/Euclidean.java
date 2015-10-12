package Distance;

/**
 * Created by workshop on 9/18/2015.
 */
public class Euclidean {

    public double getDistance(double[] query1, double[] query2){
    	double distance;
        
    	if (query1.length != query2.length){
            System.err.println("The dimension of the two vectors does not match!");

            System.exit(1);
        }
    	double[] difference = new double[query1.length];
    	for (int i = 0; i < query1.length; i ++){
    		difference[i] = query1[i] - query2[i];
    	}
    	
    	distance = 0.5 * getVariance(difference)/(getVariance(query1)-getVariance(query2));
    	return distance;
    }
    double getMean(double[] array)
    {
        double sum = 0.0;
        int size = array.length;
        for(double a : array)
            sum += a;
        return sum/size;
    }

    double getVariance(double[] array)
    {
        double mean = getMean(array);
        double temp = 0;
        int size = array.length;
        for(double a :array)
            temp += (mean-a)*(mean-a);
        return temp/size;
    }
}