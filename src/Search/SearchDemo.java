package Search;

import Feature.MagnitudeSpectrum;
import Feature.Energy;
import Feature.MFCC;
import Feature.ZeroCrossing;

import SignalProcess.WaveIO;
import Distance.Cosine;
import Tool.SortHashMapByValue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

/**
 * Created by workshop on 9/18/2015.
 */
public class SearchDemo {
    /**
     * Please replace the 'trainPath' with the specific path of train set in your PC.
     */
    protected final static String trainPath = "data/input/train/";


    /***
     * Get the feature of train set via the specific feature extraction method, and write it into offline file for efficiency;
     * Please modify this function, select or combine the methods (in the Package named 'Feature') to extract feature, such as Zero-Crossing, Energy, Magnitude-
     * Spectrum and MFCC by yourself.
     * @return the map of training features, Key is the name of file, Value is the array/vector of features.
     */
    public static HashMap<String,double[]> trainFeatureList(){
        File trainFolder = new File(trainPath);
        File[] trainList = trainFolder.listFiles();

        HashMap<String, double[]> msFeatureList = new HashMap<>();
        HashMap<String, double[]> zcFeatureList = new HashMap<>();
        HashMap<String, double[]> enFeatureList = new HashMap<>();
        HashMap<String, double[]> mfcFeatureList = new HashMap<>();
     
        
        try {

            FileWriter msfw = new FileWriter("data/feature/msFeature.txt");
            FileWriter zcfw = new FileWriter("data/feature/zcFeature.txt");
            FileWriter enfw = new FileWriter("data/feature/enFeature.txt");
            FileWriter mfcfw = new FileWriter("data/feature/mfcFeature.txt");

            for (int i = 0; i < trainList.length; i++) {
                WaveIO waveIO = new WaveIO();
                short[] signal = waveIO.readWave(trainList[i].getAbsolutePath());

                /**
                 * Example of extracting feature via MagnitudeSpectrum, modify it by yourself.
                 */
                MagnitudeSpectrum ms = new MagnitudeSpectrum();
                Energy en = new Energy();
                MFCC mfc = new MFCC();
                ZeroCrossing zc = new ZeroCrossing();
                
                double[][] mfcStep1 = mfc.process(signal);
                
                double[] msFeature = ms.getFeature(signal);
                double[] zcFeature = zc.getFeature(signal);
                double[] mfcFeature = mfc.getMeanFeature();
                double[] enFeature = en.getFeature(signal);

                /**
                 * Write the extracted feature into offline file;
                 */
                msFeatureList.put(trainList[i].getName(), msFeature);
                zcFeatureList.put(trainList[i].getName(), msFeature);
                enFeatureList.put(trainList[i].getName(), msFeature);
                mfcFeatureList.put(trainList[i].getName(), msFeature);

                String msLine = trainList[i].getName() + "\t";
                String zcLine = trainList[i].getName() + "\t";
                String enLine = trainList[i].getName() + "\t";
                String mfcLine = trainList[i].getName() + "\t";
                
                for (double f: msFeature){
                    msLine += f + "\t";
                }
                msfw.append(msLine+"\n");
                
                for (double f: zcFeature){
                    zcLine += f + "\t";
                }
                zcfw.append(zcLine+"\n");
                
                for (double f: enFeature){
                    enLine += f + "\t";
                }
                enfw.append(enLine+"\n");
                
                for (double f: mfcFeature){
                    mfcLine += f + "\t";
                }
                mfcfw.append(mfcLine+"\n");

                System.out.println("@=========@" + i);
            }
            msfw.close();
            zcfw.close();
            enfw.close();
            mfcfw.close();
            
        }catch (Exception e){
            e.printStackTrace();
        }
        try {


        }catch (Exception e){
            e.printStackTrace();
        }


        return msFeatureList;
    }

    /***
     * Get the distances between features of the selected query audio and ones of the train set;
     * Please modify this function, select or combine the suitable and feasible methods (in the package named 'Distance') to calculate the distance,
     * such as CityBlock, Cosine and Euclidean by yourself.
     * @param query the selected query audio file;
     * @return the top 20 similar audio files;
     */
    public ArrayList<String> resultList(String query, String feature){
        WaveIO waveIO = new WaveIO();

        short[] inputSignal = waveIO.readWave(query);
        MagnitudeSpectrum ms = new MagnitudeSpectrum();
        double[] msFeatureQ = ms.getFeature(inputSignal);
        HashMap<String, Double> simList = new HashMap<String, Double>();

        /**
         * Example of calculating the distance via Cosine Similarity, modify it by yourself please.
         */
        Cosine cosine = new Cosine();

        /**
         * Load the offline file of features (the result of function 'trainFeatureList()'), modify it by yourself please;
         */
        HashMap<String, double[]> trainFeatureList = readFeature(feature);

//        System.out.println(trainFeatureList.size() + "=====");
        for (Map.Entry f: trainFeatureList.entrySet()){
            simList.put((String)f.getKey(), cosine.getDistance(msFeatureQ, (double[]) f.getValue()));
        }

        SortHashMapByValue sortHM = new SortHashMapByValue(20);
        ArrayList<String> result = sortHM.sort(simList);

        String out = query + ":";
        for(int j = 0; j < result.size(); j++){
            out += "\t" + result.get(j);
        }

        System.out.println(out);
        return result;
    }

    /**
     * Load the offline file of features (the result of function 'trainFeatureList()');
     * @param featurePath the path of offline file including the features of training set.
     * @return the map of training features, Key is the name of file, Value is the array/vector of features.
     */
    private HashMap<String, double[]> readFeature(String featurePath){
        HashMap<String, double[]> fList = new HashMap<>();
        try{
            FileReader fr = new FileReader(featurePath);
            BufferedReader br = new BufferedReader(fr);

            String line = br.readLine();
            while(line != null){

                String[] split = line.trim().split("\t");
                if (split.length < 2)
                    continue;
                double[] fs = new double[split.length - 1];
                for (int i = 1; i < split.length; i ++){
                    fs[i-1] = Double.valueOf(split[i]);
                }

                fList.put(split[0], fs);

                line = br.readLine();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return fList;
    }

    public static void main(String[] args){
        //SearchDemo searchDemo = new SearchDemo();
        /**
         * Example of searching, selecting 'bus2.wav' as query;
         */
       // searchDemo.resultList("data/input/test/bus2.wav");
    	
    	HashMap<String,double[]> feature = trainFeatureList();
    }
}
