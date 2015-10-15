package Search;

import Feature.MagnitudeSpectrum;
import Feature.Energy;
import Feature.MFCC;
import Feature.ZeroCrossing;

import Evaluation.Precision;
import Evaluation.Recall;

import SignalProcess.WaveIO;
import Distance.Cosine;
import Distance.CityBlock;
import Distance.Euclidean;
import Tool.SortHashMapByValue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by workshop on 9/18/2015.
 */
public class SearchDemo {
    /**
     * Please replace the 'trainPath' with the specific path of train set in your PC.
     */
    protected final static String trainPath = "data/input/train/";
    protected final static String queryPath = "data/input/query/";


    /***
     * Get the feature of train set via the specific feature extraction method, and write it into offline file for efficiency;
     * Please modify this function, select or combine the methods (in the Package named 'Feature') to extract feature, such as Zero-Crossing, Energy, Magnitude-
     * Spectrum and MFCC by yourself.
     * @return the map of training features, Key is the name of file, Value is the array/vector of features.
     */
    public static HashMap<String,double[]> trainFeatureList(){
        File trainFolder = new File(trainPath);
        File queryFolder = new File(queryPath);

        File[] trainList = trainFolder.listFiles();
        File[] queryList = queryFolder.listFiles();

        HashMap<String, double[]> msFeatureList = new HashMap<>();
        HashMap<String, double[]> zcFeatureList = new HashMap<>();
        HashMap<String, double[]> enFeatureList = new HashMap<>();
        HashMap<String, double[]> mfcFeatureList = new HashMap<>();
     
        
        try {

            FileWriter msfw = new FileWriter("data/feature/msQFeature.txt");
            FileWriter zcfw = new FileWriter("data/feature/zcQFeature.txt");
            FileWriter enfw = new FileWriter("data/feature/enQFeature.txt");
            FileWriter mfcfw = new FileWriter("data/feature/mfcQFeature.txt");

            for (int i = 0; i < queryList.length; i++) {
                WaveIO waveIO = new WaveIO();
                short[] signal = waveIO.readWave(queryList[i].getAbsolutePath());

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
                msFeatureList.put(queryList[i].getName(), msFeature);
                zcFeatureList.put(queryList[i].getName(), zcFeature);
                enFeatureList.put(queryList[i].getName(), enFeature);
                mfcFeatureList.put(queryList[i].getName(), mfcFeature);

                String msLine = queryList[i].getName() + "\t";
                String zcLine = queryList[i].getName() + "\t";
                String enLine = queryList[i].getName() + "\t";
                String mfcLine = queryList[i].getName() + "\t";
                
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
    public ArrayList<String> resultList(String query, String checkBit){
        WaveIO waveIO = new WaveIO();
        short[] inputSignal = waveIO.readWave(query);
        double[] msFeatureQ = null,zcFeatureQ=null,enFeatureQ=null,mfcFeatureQ=null;
        MagnitudeSpectrum ms = new MagnitudeSpectrum();
        Energy en = new Energy();
        MFCC mfc = new MFCC();
        ZeroCrossing zc = new ZeroCrossing();
        double msW=0.452,zcW=0.077,enW=0.067,mfcW=0.404;
        String msFeature = "data/feature/msFeature.txt";
        String zcFeature = "data/feature/zcFeature.txt";
        String enFeature = "data/feature/enFeature.txt";
        String mfcFeature = "data/feature/mfcFeature.txt";
        System.out.println(query);
        
        if(checkBit.charAt(0) == '1')
        	msFeatureQ = ms.getFeature(inputSignal);
        if(checkBit.charAt(1) == '1')
        	zcFeatureQ = zc.getFeature(inputSignal);
        if(checkBit.charAt(2) == '1')
        	enFeatureQ = en.getFeature(inputSignal);
        if(checkBit.charAt(3) == '1'){
        	double[][] mfcFeatureQ1 = mfc.process(inputSignal);
        	mfcFeatureQ = mfc.getMeanFeature();
        }
        
        HashMap<String, Double> simList = new HashMap<String, Double>();

        /**
         * Example of calculating the distance via Cosine Similarity, modify it by yourself please.
         */
        Cosine cosine = new Cosine();
        CityBlock cb = new CityBlock();
        Euclidean ed = new Euclidean();

        /**
         * Load the offline file of features (the result of function 'trainFeatureList()'), modify it by yourself please;
         */
        HashMap<String, double[]> msTrainFeatureList = readFeature(msFeature);
        HashMap<String, double[]> zcTrainFeatureList = readFeature(zcFeature);
        HashMap<String, double[]> enTrainFeatureList = readFeature(enFeature);
        HashMap<String, double[]> mfcTrainFeatureList = readFeature(mfcFeature);
        double msV=0,zcV=0,enV=0,mfcV=0;
    	double originV=0,finalV=0;
    	
//        CheckBit 1: MS
//    	  CheckBit 2: ZC
//    	  CheckBit 3: EN
//    	  CheckBit 4: MFCC

        	if(checkBit.charAt(0) == '1'){
        		for (Map.Entry f: msTrainFeatureList.entrySet()){
        			msV = cosine.getDistance(msFeatureQ, (double[]) f.getValue());
        			if(simList.containsKey((String)f.getKey())){
        				originV = simList.get((String)f.getKey());
        				simList.put((String)f.getKey(), (originV + msV * msW));
        			}else
        				simList.put((String)f.getKey(), msV * msW);
        		}
        	}
        	
            if(checkBit.charAt(1) == '1'){
            	for (Map.Entry f: zcTrainFeatureList.entrySet()){
        			zcV = ed.getDistance(zcFeatureQ, (double[]) f.getValue());
        			if(simList.containsKey((String)f.getKey())){
        				originV = simList.get((String)f.getKey());
        				simList.put((String)f.getKey(), (originV + zcV * zcW));
        			}else
        				simList.put((String)f.getKey(), zcV * zcW);
        		}
            }
            
            if(checkBit.charAt(2) == '1'){
            	for (Map.Entry f: enTrainFeatureList.entrySet()){
        			enV = ed.getDistance(enFeatureQ, (double[]) f.getValue());
        			if(simList.containsKey((String)f.getKey())){
        				originV = simList.get((String)f.getKey());
        				simList.put((String)f.getKey(), (originV + enV * enW));
        			}else
        				simList.put((String)f.getKey(), enV * enW);
        		}
            }
            
            if(checkBit.charAt(3) == '1'){
            	for (Map.Entry f: mfcTrainFeatureList.entrySet()){
        			mfcV = ed.getDistance(mfcFeatureQ, (double[]) f.getValue());
        			if(simList.containsKey((String)f.getKey())){
        				originV = simList.get((String)f.getKey());
        				simList.put((String)f.getKey(), (originV + mfcV * mfcW));
        			}else
        				simList.put((String)f.getKey(), mfcV * mfcW);
        		}
            }
        

        SortHashMapByValue sortHM = new SortHashMapByValue(20);
        ArrayList<String> result = sortHM.sort(simList);

        String out = query + ":";
        for(int j = 0; j < result.size(); j++){
            out += "\t" + result.get(j);
        }
        
        
        //Evaluation Part
        Precision pre = new Precision();
        Recall rec = new Recall();
        String queryName = query.substring(query.lastIndexOf("\\")+1);
        System.out.println(queryName);
        double recallV = rec.getRecall(queryName, result);
        double precisionV = pre.getPrecision(queryName, result);
        double F1Score = (2 * precisionV * recallV) /(precisionV + recallV);
       
        System.out.println(out);
        System.out.println("Recall: "+recallV+"\n"+"Precision: "+precisionV+"\n"+"F1 Score: "+F1Score);
        return result;
    }

    /**
     * Load the offline file of features (the result of function 'trainFeatureList()');
     * @param featurePath the path of offline file including the features of training set.
     * @return the map of training features, Key is the name of file, Value is the array/vector of features.
     */
    private static HashMap<String, double[]> readFeature(String featurePath){
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

    public static void testRun() throws IOException{
    	String msFeature = "data/feature/msFeature.txt";
        String zcFeature = "data/feature/zcFeature.txt";
        String enFeature = "data/feature/enFeature.txt";
        String mfcFeature = "data/feature/mfcFeature.txt";
        
        String msQFeature = "data/feature/msQFeature.txt";
        String zcQFeature = "data/feature/zcQFeature.txt";
        String enQFeature = "data/feature/enQFeature.txt";
        String mfcQFeature = "data/feature/mfcQFeature.txt";
    	
    	HashMap<String, double[]> msTrainFeatureList = readFeature(msFeature);
        HashMap<String, double[]> zcTrainFeatureList = readFeature(zcFeature);
        HashMap<String, double[]> enTrainFeatureList = readFeature(enFeature);
        HashMap<String, double[]> mfcTrainFeatureList = readFeature(mfcFeature);
        
        HashMap<String, double[]> msQueryFeatureList = readFeature(msQFeature);
        HashMap<String, double[]> zcQueryFeatureList = readFeature(zcQFeature);
        HashMap<String, double[]> enQueryFeatureList = readFeature(enQFeature);
        HashMap<String, double[]> mfcQueryFeatureList = readFeature(mfcQFeature);
        
        HashMap<String, Double> msList = new HashMap<String, Double>();
        HashMap<String, Double> zcList = new HashMap<String, Double>();
        HashMap<String, Double> enList = new HashMap<String, Double>();
        HashMap<String, Double> mfcList = new HashMap<String, Double>();
        
        FileWriter msfw = new FileWriter("data/output/msResult.txt");
        FileWriter zcfw = new FileWriter("data/output/zcResult.txt");
        FileWriter enfw = new FileWriter("data/output/enResult.txt");
        FileWriter mfcfw = new FileWriter("data/output/mfcResult.txt");
    
        Cosine cosine = new Cosine();
        CityBlock cb = new CityBlock();
        Euclidean ed = new Euclidean();
        
        Precision pre = new Precision();
        
        double msV=0,zcV=0,enV=0,mfcV=0;
        
        for (Map.Entry f1: msQueryFeatureList.entrySet()){
        	for (Map.Entry f2: msTrainFeatureList.entrySet()){
    			msV = cosine.getDistance((double[]) f1.getValue(), (double[]) f2.getValue());
    			msList.put((String)f2.getKey(), msV);
        	}
        	SortHashMapByValue sortHM = new SortHashMapByValue(20);
            ArrayList<String> msResult = sortHM.sort(msList);
            
            double precisionV = pre.getPrecision((String)f1.getKey(), msResult);
            String msLine = (String)f1.getKey() + ":\t Precision: " + String.valueOf(precisionV) + "\n";
            
//            for(int i=0; i<msResult.size(); i++){
//            	msLine += msResult.get(i) + "\t";
//            }
            msfw.append(msLine + "\n");
            msList.clear();
        }
        
        for (Map.Entry f1: zcQueryFeatureList.entrySet()){
        	for (Map.Entry f2: zcTrainFeatureList.entrySet()){
    			zcV = ed.getDistance((double[]) f1.getValue(), (double[]) f2.getValue());
    			zcList.put((String)f2.getKey(), zcV);
        	}
        	SortHashMapByValue sortHM = new SortHashMapByValue(20);
            ArrayList<String> zcResult = sortHM.sort(zcList);
            double precisionV = pre.getPrecision((String)f1.getKey(), zcResult);
            String zcLine = (String)f1.getKey() + ":\t Precision: " + String.valueOf(precisionV) + "\n";
            
//            for(int i=0; i<zcResult.size(); i++){
//            	zcLine += zcResult.get(i) + "\t";
//            }
            zcfw.append(zcLine + "\n");
            zcList.clear();
        }
        
        for (Map.Entry f1: enQueryFeatureList.entrySet()){
        	for (Map.Entry f2: enTrainFeatureList.entrySet()){
    			enV = ed.getDistance((double[]) f1.getValue(), (double[]) f2.getValue());
    			enList.put((String)f2.getKey(), enV);
        	}
        	SortHashMapByValue sortHM = new SortHashMapByValue(20);
            ArrayList<String> enResult = sortHM.sort(enList);
            double precisionV = pre.getPrecision((String)f1.getKey(), enResult);
            String enLine = (String)f1.getKey() + ":\t Precision: " + String.valueOf(precisionV) + "\n";
            
//            for(int i=0; i<enResult.size(); i++){
//            	enLine += enResult.get(i) + "\t";
//            }
            enfw.append(enLine + "\n");
            enList.clear();
        }
        
        for (Map.Entry f1: mfcQueryFeatureList.entrySet()){
        	for (Map.Entry f2: mfcTrainFeatureList.entrySet()){
    			mfcV = ed.getDistance((double[]) f1.getValue(), (double[]) f2.getValue());
    			mfcList.put((String)f2.getKey(), mfcV);
        	}
        	SortHashMapByValue sortHM = new SortHashMapByValue(20);
            ArrayList<String> mfcResult = sortHM.sort(mfcList);
            double precisionV = pre.getPrecision((String)f1.getKey(), mfcResult);
            String mfcLine = (String)f1.getKey() + ":\t Precision: " + String.valueOf(precisionV) + "\n";
            
//            for(int i=0; i<mfcResult.size(); i++){
//            	mfcLine += mfcResult.get(i) + "\t";
//            }
            mfcfw.append(mfcLine + "\n");
            mfcList.clear();
        }
        
        msfw.close();
        zcfw.close();
        enfw.close();
        mfcfw.close();

    }
    
    public static void main(String[] args) throws IOException{
        //SearchDemo searchDemo = new SearchDemo();
        /**
         * Example of searching, selecting 'bus2.wav' as query;
         */
       // searchDemo.resultList("data/input/test/bus2.wav");
    	
    	//HashMap<String,double[]> feature = trainFeatureList();
    	testRun();
    }
}
