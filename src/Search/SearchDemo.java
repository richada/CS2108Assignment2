package Search;

import Feature.MagnitudeSpectrum;
import Feature.Energy;
import Feature.MFCC;
import Feature.ZeroCrossing;

import Evaluation.Precision;
import Evaluation.Recall;
import Evaluation.AveragePrecision;

import SignalProcess.WaveIO;
import Distance.Cosine;
import Distance.CityBlock;
import Distance.Euclidean;
import Tool.SortHashMapByValue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

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
        
        String queryCategory = query.substring(query.lastIndexOf("\\")+1).split(".wav")[0].replaceAll("[^a-zA-Z]","");
        
        switch(queryCategory){
        case "bus":
        	msW = 0.7;
        	zcW = 0.1;
        	enW = 0;
        	mfcW = 0.2;
        	break;
        	
        case "busystreet":
        	msW = 0.35;
        	zcW = 0.1;
        	enW = 0.2;
        	mfcW = 0.35;
        	break;
        	
        case "office":
        	msW = 0.4;
        	zcW = 0.07;
        	enW = 0.08;
        	mfcW = 0.45;
        	break;
        	
        case "openairmarket":
        	msW = 0.5;
        	zcW = 0.08;
        	enW = 0.02;
        	mfcW = 0.4;
        	break;
        	
        case "park":
        	msW = 0.45;
        	zcW = 0.07;
        	enW = 0.03;
        	mfcW = 0.45;
        	break;
        	
        case "quietstreet":
        	msW = 0.43;
        	zcW = 0;
        	enW = 0.1;
        	mfcW = 0.47;
        	break;
        	
        case "restaurant":
        	msW = 0.45;
        	zcW = 0.1;
        	enW = 0;
        	mfcW = 0.45;
        	break;
        	
        case "supermarket":
        	msW = 0.45;
        	zcW = 0.05;
        	enW = 0.1;
        	mfcW = 0.4;
        	break;
        	
        case "tube":
        	msW = 0.3;
        	zcW = 0.05;
        	enW = 0.05;
        	mfcW = 0.6;
        	break;
        	
        case "tubestation":
        	msW = 0.13;
        	zcW = 0.12;
        	enW = 0.05;
        	mfcW = 0.7;
        	break;
        	
        default:
        	System.out.println("No Such Case!\n");
        	break;
        }
        	
        System.out.println("MS Weight: " + msW + "\nZC Weight: " + zcW + "\nEN Weight: " + enW + "\nMFCC Weight: " + mfcW + "\n");
        
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
        			mfcV = cosine.getDistance(mfcFeatureQ, (double[]) f.getValue());
        			if(simList.containsKey((String)f.getKey())){
        				originV = simList.get((String)f.getKey());
        				simList.put((String)f.getKey(), (originV + mfcV * mfcW));
        			}else
        				simList.put((String)f.getKey(), mfcV * mfcW);
        		}
            }
        

        SortHashMapByValue sortHM = new SortHashMapByValue(20);
        ArrayList<String> result = sortHM.sort(simList);
        
        //Evaluation Part
        Precision pre = new Precision();
        Recall rec = new Recall();
        AveragePrecision ap = new AveragePrecision();
        
        String queryName = query.substring(query.lastIndexOf("\\")+1);
        double recallV = rec.getRecall(queryName, result);
        double precisionV = pre.getPrecision(queryName, result);
        double averagePrecisionV = ap.getAveragePrecision(queryName, result);
        double F1Score = (2 * precisionV * recallV) /(precisionV + recallV);
       
        System.out.println("Recall: "+recallV+"\nPrecision: "+precisionV+"\nAverage Precision: "+averagePrecisionV+"\nF1 Score: "+F1Score+"\n");
        return result;
    }
    
    
    public ArrayList<String> giveRF(ArrayList<String> rfQueries, ArrayList<Double> weightList, String checkBit){
    	String inputPath1 = "data/input/query/";
    	String inputPath = "data/input/train/";
    	HashMap<String,Double> simList = new HashMap<String,Double>();
    	
        MagnitudeSpectrum ms = new MagnitudeSpectrum();
        Energy en = new Energy();
        MFCC mfc = new MFCC();
        ZeroCrossing zc = new ZeroCrossing();
        double msW=0.452,zcW=0.077,enW=0.067,mfcW=0.404;
        
        String msFeature = "data/feature/msFeature.txt";
        String zcFeature = "data/feature/zcFeature.txt";
        String enFeature = "data/feature/enFeature.txt";
        String mfcFeature = "data/feature/mfcFeature.txt";
        
		
        HashMap<String, double[]> msTrainFeatureList = readFeature(msFeature);
        HashMap<String, double[]> zcTrainFeatureList = readFeature(zcFeature);
        HashMap<String, double[]> enTrainFeatureList = readFeature(enFeature);
        HashMap<String, double[]> mfcTrainFeatureList = readFeature(mfcFeature);
        
        Cosine cosine = new Cosine();
        CityBlock cb = new CityBlock();
        Euclidean ed = new Euclidean();
        
        String queryCategory = rfQueries.get(0).split(".wav")[0].replaceAll("[^a-zA-Z]","");
        
        switch(queryCategory){
        case "bus":
        	msW = 0.7;
        	zcW = 0.1;
        	enW = 0;
        	mfcW = 0.2;
        	break;
        	
        case "busystreet":
        	msW = 0.35;
        	zcW = 0.1;
        	enW = 0.2;
        	mfcW = 0.35;
        	break;
        	
        case "office":
        	msW = 0.4;
        	zcW = 0.07;
        	enW = 0.08;
        	mfcW = 0.45;
        	break;
        	
        case "openairmarket":
        	msW = 0.5;
        	zcW = 0.08;
        	enW = 0.02;
        	mfcW = 0.4;
        	break;
        	
        case "park":
        	msW = 0.45;
        	zcW = 0.07;
        	enW = 0.03;
        	mfcW = 0.45;
        	break;
        	
        case "quietstreet":
        	msW = 0.43;
        	zcW = 0;
        	enW = 0.1;
        	mfcW = 0.47;
        	break;
        	
        case "restaurant":
        	msW = 0.45;
        	zcW = 0.1;
        	enW = 0;
        	mfcW = 0.45;
        	break;
        	
        case "supermarket":
        	msW = 0.45;
        	zcW = 0.05;
        	enW = 0.1;
        	mfcW = 0.4;
        	break;
        	
        case "tube":
        	msW = 0.3;
        	zcW = 0.05;
        	enW = 0.05;
        	mfcW = 0.6;
        	break;
        	
        case "tubestation":
        	msW = 0.13;
        	zcW = 0.12;
        	enW = 0.05;
        	mfcW = 0.7;
        	break;
        	
        default:
        	System.out.println("No Such Case!\n");
        	break;
        }
             
    	for(int i=0; i<rfQueries.size(); i++){
    		String fileName = "";
    		if(i==0)
    			fileName = inputPath1 + rfQueries.get(i);
    		else
    			fileName = inputPath + rfQueries.get(i);
    		WaveIO waveIO = new WaveIO();
            short[] inputSignal = waveIO.readWave(fileName);
            double[] msFeatureQ = null,zcFeatureQ=null,enFeatureQ=null,mfcFeatureQ=null;
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

            double msV=0,zcV=0,enV=0,mfcV=0;
        	double originV=0,finalV=0;
        	
//            CheckBit 1: MS
//        	  CheckBit 2: ZC
//        	  CheckBit 3: EN
//        	  CheckBit 4: MFCC

           	if(checkBit.charAt(0) == '1'){
           		for (Map.Entry f: msTrainFeatureList.entrySet()){
           			msV = cosine.getDistance(msFeatureQ, (double[]) f.getValue());
           			if(simList.containsKey((String)f.getKey())){
           				originV = simList.get((String)f.getKey());
           				simList.put((String)f.getKey(), (originV + msV * msW * weightList.get(i)));
           			}else
           				simList.put((String)f.getKey(), msV * msW * weightList.get(i));
           		}
           	}
           	
            if(checkBit.charAt(1) == '1'){
               	for (Map.Entry f: zcTrainFeatureList.entrySet()){
           			zcV = ed.getDistance(zcFeatureQ, (double[]) f.getValue());
           			if(simList.containsKey((String)f.getKey())){
           				originV = simList.get((String)f.getKey());
           				simList.put((String)f.getKey(), (originV + zcV * zcW * weightList.get(i)));
           			}else
           				simList.put((String)f.getKey(), zcV * zcW * weightList.get(i));
           		}
            }
                
            if(checkBit.charAt(2) == '1'){
               	for (Map.Entry f: enTrainFeatureList.entrySet()){
           			enV = ed.getDistance(enFeatureQ, (double[]) f.getValue());
           			if(simList.containsKey((String)f.getKey())){
           				originV = simList.get((String)f.getKey());
           				simList.put((String)f.getKey(), (originV + enV * enW * weightList.get(i)));
           			}else
           				simList.put((String)f.getKey(), enV * enW * weightList.get(i));
           		}
            }
                
            if(checkBit.charAt(3) == '1'){
               	for (Map.Entry f: mfcTrainFeatureList.entrySet()){
           			mfcV = cosine.getDistance(mfcFeatureQ, (double[]) f.getValue());
           			if(simList.containsKey((String)f.getKey())){
           				originV = simList.get((String)f.getKey());
           				simList.put((String)f.getKey(), (originV + mfcV * mfcW * weightList.get(i)));
           			}else
           				simList.put((String)f.getKey(), mfcV * mfcW * weightList.get(i));
           		}
            }
            
    	}
    	
    	SortHashMapByValue sortHM = new SortHashMapByValue(20);
        ArrayList<String> result = sortHM.sort(simList);
        
        Precision pre = new Precision();
        Recall rec = new Recall();
        AveragePrecision ap = new AveragePrecision();
        
        String queryName = rfQueries.get(0);
        double recallV = rec.getRecall(queryName, result);
        double precisionV = pre.getPrecision(queryName, result);
        double averagePrecisionV = ap.getAveragePrecision(queryName, result);
        double F1Score = (2 * precisionV * recallV) /(precisionV + recallV);

        System.out.println("After RF:\nRecall: "+recallV+"\nPrecision: "+precisionV+"\nAverage Precision: "+averagePrecisionV+"\nF1 Score: "+F1Score+"\n");
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
        
        String msResultP = "data/output/msResult.txt";
        String zcResultP = "data/output/zcResult.txt";
        String enResultP = "data/output/enResult.txt";
        String mfcResultP = "data/output/mfcResult.txt";
        
        String msSResultP = "data/output/msSResult.txt";
        String zcSResultP = "data/output/zcSResult.txt";
        String enSResultP = "data/output/enSResult.txt";
        String mfcSResultP = "data/output/mfcSResult.txt";
    	
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
        
        FileWriter msfw = new FileWriter(msResultP);
        FileWriter zcfw = new FileWriter(zcResultP);
        FileWriter enfw = new FileWriter(enResultP);
        FileWriter mfcfw = new FileWriter(mfcResultP);
        
        FileWriter mssfw = new FileWriter(msSResultP);
        FileWriter zcsfw = new FileWriter(zcSResultP);
        FileWriter ensfw = new FileWriter(enSResultP);
        FileWriter mfcsfw = new FileWriter(mfcSResultP);
    
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
            
            HashMap<String, Double> sortedResult = new HashMap<String, Double>();
            
            String msLine = (String)f1.getKey() + " Precision:" + String.valueOf(precisionV) + "\n";
            
//            for(int i=0; i<msResult.size(); i++){
//            	msLine += msResult.get(i) + "\t";
//            }
            msfw.append(msLine);
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
            String zcLine = (String)f1.getKey() + " Precision:" + String.valueOf(precisionV) + "\n";
            
//            for(int i=0; i<zcResult.size(); i++){
//            	zcLine += zcResult.get(i) + "\t";
//            }
            zcfw.append(zcLine);
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
            String enLine = (String)f1.getKey() + " Precision:" + String.valueOf(precisionV) + "\n";
            
//            for(int i=0; i<enResult.size(); i++){
//            	enLine += enResult.get(i) + "\t";
//            }
            enfw.append(enLine);
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
            String mfcLine = (String)f1.getKey() + " Precision:" + String.valueOf(precisionV) + "\n";
            
//            for(int i=0; i<mfcResult.size(); i++){
//            	mfcLine += mfcResult.get(i) + "\t";
//            }
            mfcfw.append(mfcLine);
            mfcList.clear();
        }
        
        msfw.close();
        zcfw.close();
        enfw.close();
        mfcfw.close();
        
        FileReader msfr = new FileReader(msResultP);
        FileReader zcfr = new FileReader(zcResultP);
        FileReader enfr = new FileReader(enResultP);
        FileReader mfcfr = new FileReader(mfcResultP);
        
        BufferedReader msbr = new BufferedReader(msfr);
        BufferedReader zcbr = new BufferedReader(zcfr);
        BufferedReader enbr = new BufferedReader(enfr);
        BufferedReader mfcbr = new BufferedReader(mfcfr);
        
        HashMap<String, String> msSResult = new HashMap<String,String>();
        HashMap<String, String> zcSResult = new HashMap<String,String>();
        HashMap<String, String> enSResult = new HashMap<String,String>();
        HashMap<String, String> mfcSResult = new HashMap<String,String>();
        
        for(int i=0; i<100; i++){
        	String msL = msbr.readLine();
        	String zcL = zcbr.readLine();
        	String enL = enbr.readLine();
        	String mfcL = mfcbr.readLine();
        	
        	msSResult.put(msL.split(" ")[0], msL.split(" ")[1]);
        	zcSResult.put(zcL.split(" ")[0], zcL.split(" ")[1]);
        	enSResult.put(enL.split(" ")[0], enL.split(" ")[1]);
        	mfcSResult.put(mfcL.split(" ")[0], mfcL.split(" ")[1]);
        }
        
        TreeMap<String, String>msFResult = new TreeMap<String,String>(msSResult);
        TreeMap<String, String>zcFResult = new TreeMap<String,String>(zcSResult);
        TreeMap<String, String>enFResult = new TreeMap<String,String>(enSResult);
        TreeMap<String, String>mfcFResult = new TreeMap<String,String>(mfcSResult);
        
        int counter = 1;
        ArrayList<Double> average = new ArrayList<Double>();
        
        for(Map.Entry m: msFResult.entrySet()){
        	String output = m.getKey() + " " + m.getValue() + "\n";
        	average.add(Double.parseDouble(((String) m.getValue()).split(":")[1]));
        	if(counter % 10 == 0){
        		double sum = 0;
        		for(int i=0; i<10; i++){
        			sum += average.get(i);
        		}
        		double averageP = sum / 10.0;
        		output += "Average: " + String.valueOf(averageP) + "\n\n";
        		average.clear();
        	}
        	counter++;
        	mssfw.append(output);
        }
        
        counter = 1;
        for(Map.Entry m: zcFResult.entrySet()){
        	String output = m.getKey() + " " + m.getValue() + "\n";
        	average.add(Double.parseDouble(((String) m.getValue()).split(":")[1]));
        	if(counter % 10 == 0){
        		double sum = 0;
        		for(int i=0; i<10; i++){
        			sum += average.get(i);
        		}
        		double averageP = sum / 10.0;
        		output += "Average: " + String.valueOf(averageP) + "\n\n";
        		average.clear();
        	}
        	counter++;
        	zcsfw.append(output);
        }
        
        counter = 1;
        for(Map.Entry m: enFResult.entrySet()){
        	String output = m.getKey() + " " + m.getValue() + "\n";
        	average.add(Double.parseDouble(((String) m.getValue()).split(":")[1]));
        	if(counter % 10 == 0){
        		double sum = 0;
        		for(int i=0; i<10; i++){
        			sum += average.get(i);
        		}
        		double averageP = sum / 10.0;
        		output += "Average: " + String.valueOf(averageP) + "\n\n";
        		average.clear();
        	}
        	counter++;
        	ensfw.append(output);
        }
        
        counter = 1;
        for(Map.Entry m: mfcFResult.entrySet()){
        	String output = m.getKey() + " " + m.getValue() + "\n";
        	average.add(Double.parseDouble(((String) m.getValue()).split(":")[1]));
        	if(counter % 10 == 0){
        		double sum = 0;
        		for(int i=0; i<10; i++){
        			sum += average.get(i);
        		}
        		double averageP = sum / 10.0;
        		output += "Average: " + String.valueOf(averageP) + "\n\n";
        		average.clear();
        	}
        	counter++;
        	mfcsfw.append(output);
        }
        
        msbr.close();
        mssfw.close();
        msfr.close();
                                                                                                                         
        zcbr.close();
        zcsfw.close();
        zcfr.close();
        
        enbr.close();
        ensfw.close();
        enfr.close();
        
        mfcbr.close();
        mfcsfw.close();
        mfcfr.close();
    }

    public static void testWeight() throws IOException{
    	String msFeature = "data/feature/msFeature.txt";
        String zcFeature = "data/feature/zcFeature.txt";
        String enFeature = "data/feature/enFeature.txt";
        String mfcFeature = "data/feature/mfcFeature.txt";
        
        String msQFeature = "data/feature/msQFeature.txt";
        String zcQFeature = "data/feature/zcQFeature.txt";
        String enQFeature = "data/feature/enQFeature.txt";
        String mfcQFeature = "data/feature/mfcQFeature.txt";
        
        String weightResultP = "data/output/weightResult.txt";
    	
    	HashMap<String, double[]> msTrainFeatureList = readFeature(msFeature);
        HashMap<String, double[]> zcTrainFeatureList = readFeature(zcFeature);
        HashMap<String, double[]> enTrainFeatureList = readFeature(enFeature);
        HashMap<String, double[]> mfcTrainFeatureList = readFeature(mfcFeature);
        
        HashMap<String, double[]> msQueryFeatureList = readFeature(msQFeature);
        HashMap<String, double[]> zcQueryFeatureList = readFeature(zcQFeature);
        HashMap<String, double[]> enQueryFeatureList = readFeature(enQFeature);
        HashMap<String, double[]> mfcQueryFeatureList = readFeature(mfcQFeature);
        
        HashMap<String, Double> outputList = new HashMap<String, Double>();
        
        FileWriter fw = new FileWriter(weightResultP);
    
        Cosine cosine = new Cosine();
        Euclidean ed = new Euclidean();
        
        Precision pre = new Precision();
        
        double msV=0,zcV=0,enV=0,mfcV=0;
        
        ArrayList<String> queryName = new ArrayList<String>();
        for (Map.Entry f1: msQueryFeatureList.entrySet()){
        	queryName.add((String)f1.getKey());
        }
        Collections.sort(queryName);
        
        for (int k=0; k<100; k++){
        	String query = queryName.get(k);
        	System.out.println(query.split(".wav")[0]);
        	String queryCategory = query.split(".wav")[0].replaceAll("[^a-zA-Z]","");
        	System.out.println(queryCategory);
        	double msL=0,msH=0,zcL=0,zcH=0,enL=0,enH=0;
      
        	switch(queryCategory){
            case "bus":
            	msL=0.4;
            	msH=0.5;
            	zcL=0.05;
            	zcH=0.1;
            	enL=0.01;
            	enH=0.01;
            	break;
            	
            case "busystreet":
            	msL=0.3;
            	msH=0.35;
            	zcL=0.01;
            	zcH=0.05;
            	enL=0.3;
            	enH=0.35;
            	break;
            	
            case "office":
            	msL=0.3;
            	msH=0.4;
            	zcL=0.05;
            	zcH=0.15;
            	enL=0.05;
            	enH=0.15;
            	break;
            	
            case "openairmarket":
            	msL=0.4;
            	msH=0.5;
            	zcL=0.05;
            	zcH=0.1;
            	enL=0.01;
            	enH=0.05;
            	break;
            	
            case "park":
            	msL=0.4;
            	msH=0.45;
            	zcL=0.05;
            	zcH=0.1;
            	enL=0.01;
            	enH=0.05;
            	break;
            	
            case "quietstreet":
            	msL=0.4;
            	msH=0.45;
            	zcL=0.01;
            	zcH=0.05;
            	enL=0.05;
            	enH=0.1;
            	break;
            	
            case "restaurant":
            	msL=0.4;
            	msH=0.45;
            	zcL=0.05;
            	zcH=0.1;
            	enL=0.01;
            	enH=0.05;
            	break;
            	
            case "supermarket":
            	msL=0.45;
            	msH=0.5;
            	zcL=0.01;
            	zcH=0.05;
            	enL=0.05;
            	enH=0.1;
            	break;
            	
            case "tube":
            	msL=0.35;
            	msH=0.4;
            	zcL=0.01;
            	zcH=0.1;
            	enL=0.01;
            	enH=0.1;
            	break;
            	
            case "tubestation":
            	msL=0.4;
            	msH=0.45;
            	zcL=0.01;
            	zcH=0.1;
            	enL=0.01;
            	enH=0.1;
            	break;
            	
            default:
            	System.out.println("No Such Case!\n");
            	break;
            }
        	
        	for(double msW = msL; msW <= msH; msW += 0.01){
        		for(double zcW = zcL; zcW <= zcH; zcW += 0.01){
        			for(double enW = enL; enW <= enH; enW += 0.01){
        				double mfcW = 1.0-msW-zcW-enW;
        				double originV = 0;		
				        for (Map.Entry f2: msTrainFeatureList.entrySet()){		
				    		msV = cosine.getDistance(msQueryFeatureList.get(query), (double[]) f2.getValue());
				    		if(outputList.containsKey((String)f2.getKey())){
			       				originV = outputList.get((String)f2.getKey());
			       				outputList.put((String)f2.getKey(), (originV + msV * msW));
			       			}else
			       				outputList.put((String)f2.getKey(), msV * msW);
			        	}
			        	for (Map.Entry f2: zcTrainFeatureList.entrySet()){
			    			zcV = ed.getDistance(zcQueryFeatureList.get(query), (double[]) f2.getValue());
			    			if(outputList.containsKey((String)f2.getKey())){
		        				originV = outputList.get((String)f2.getKey());
		        				outputList.put((String)f2.getKey(), (originV + zcV * zcW));
		        			}else
		        				outputList.put((String)f2.getKey(), zcV * zcW);
			        	}
			        	for (Map.Entry f2: enTrainFeatureList.entrySet()){
			    			enV = ed.getDistance(enQueryFeatureList.get(query), (double[]) f2.getValue());
			    			if(outputList.containsKey((String)f2.getKey())){
		        				originV = outputList.get((String)f2.getKey());
		        				outputList.put((String)f2.getKey(), (originV + enV * enW));
		        			}else
		        				outputList.put((String)f2.getKey(), enV * enW);
			        	}
			        	for (Map.Entry f2: mfcTrainFeatureList.entrySet()){
			    			mfcV = ed.getDistance(mfcQueryFeatureList.get(query), (double[]) f2.getValue());
			    			if(outputList.containsKey((String)f2.getKey())){
		        				originV = outputList.get((String)f2.getKey());
		        				outputList.put((String)f2.getKey(), (originV + mfcV * mfcW));
		        			}else
		        				outputList.put((String)f2.getKey(), mfcV * mfcW);
			        	}
			        	
			        	
			        	SortHashMapByValue sortHM = new SortHashMapByValue(20);
			            ArrayList<String> msResult = sortHM.sort(outputList);
			            
			            double precisionV = pre.getPrecision(query, msResult);
			            
			            
			            if(precisionV < 0.8)
			            	continue;
			            
			            String resultLine = query + "\n\tmsW:" + String.valueOf(msW) + 
			            		"\tzcW:" + String.valueOf(zcW) + "\tenW:" + String.valueOf(enV) + "\tmfcW:" + String.valueOf(mfcV) +
			            		"\n\t Precision:" + String.valueOf(precisionV) + "\n";
			            
			//            for(int i=0; i<msResult.size(); i++){
			//            	msLine += msResult.get(i) + "\t";
			//            }
			            outputList.clear();   
			            fw.append(resultLine);			        
        			}
        		}
        	}
        }
        fw.close();
    }                                                                                       
    
    public static void main(String[] args) throws IOException{
        //SearchDemo searchDemo = new SearchDemo();
        /**
         * Example of searching, selecting 'bus2.wav' as query;
         */
       // searchDemo.resultList("data/input/test/bus2.wav");
    	
    	//HashMap<String,double[]> feature = trainFeatureList();
    	//testRun();
    	//testWeight();
    }
}
