package Player;
import Search.SearchDemo;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import ExtraFeature.IdentifyProtocolV1;
import ExtraFeature.JavaSoundRecorder;
import ExtraFeature.PseudoRF;

/**
 * Created by workshop on 9/18/2015.
 */
public class SoundEffectDemo extends JFrame implements ActionListener{

    JPanel contentPane;
    JPanel checkBoxPanel = new JPanel(new GridLayout(0,2));
    JButton openButton, searchButton, queryButton, recordButton, songButton, rfButton;
    JFileChooser fileChooser;
    JCheckBox MSButton, ZCButton, ENButton, MFCButton;
    JLabel songNameLabel;


    File queryAudio = null;
    int resultSize = 20;
    /**
     * If need, please replace the 'querySet' with specific path of test set of audio files in your PC.
     */
    String querySet = "data/input/";
    /**
     * Please Replace the 'basePath' with specific path of train set of audio files in your PC.
     */
    String basePath = "data/input/train/";


    JButton[] resultButton = new JButton[resultSize];
    JLabel [] resultLabels = new JLabel[resultSize];
    ArrayList<String> resultFiles = new ArrayList<String>();

    // Constructor
    public SoundEffectDemo() {
        // Pre-load all the sound files
        queryAudio = null;
        SoundEffect.volume = SoundEffect.Volume.LOW;  // un-mute
        int x=150,y=30;
        
        // Set up UI components;
        openButton = new JButton("Select an audio clip...");
        //openButton.setPreferredSize(new Dimension(x,y));
        openButton.addActionListener(this);

        String tempName = "";
        
        recordButton = new JButton("Record a 10s clip...");
        recordButton.setPreferredSize(new Dimension(x,y));
        recordButton.addActionListener(this);
        
        songButton = new JButton("Music Recognition");
        //songButton.setPreferredSize(new Dimension(x,y));
        songButton.addActionListener(this);
        
        queryButton = new JButton("Current Audio:"+tempName);
        //queryButton.setPreferredSize(new Dimension(x,y));
        queryButton.addActionListener(this);

        searchButton = new JButton("Search");
        //searchButton.setPreferredSize(new Dimension(x,y));
        searchButton.addActionListener(this);
        
        rfButton = new JButton("Relevance Feedback");
        rfButton.addActionListener(this);
        
        MSButton = new JCheckBox("Magnitude Spectrum Match");
        MSButton.addActionListener(this);
        
        ZCButton = new JCheckBox("Zero Crossing Match");
        ZCButton.addActionListener(this);
        
        ENButton = new JCheckBox("Energy Match");
        ENButton.addActionListener(this);
        
        MFCButton = new JCheckBox("MFCC Match");
        MFCButton.addActionListener(this);
          
        songNameLabel = new JLabel();
        songNameLabel.setVisible(false);
        
        JPanel queryPanel = new JPanel();
        JPanel extraFeaturePanel = new JPanel();
        JPanel featurePanel = new JPanel(new GridLayout(0,1));
        JPanel panelWithName = new JPanel(new GridLayout(1,0));
        
        queryPanel.add(openButton);
        queryPanel.add(queryButton);
        queryPanel.add(searchButton);
        extraFeaturePanel.add(recordButton);
        extraFeaturePanel.add(songButton);
        extraFeaturePanel.add(rfButton);
        extraFeaturePanel.add(songNameLabel);
        featurePanel.add(queryPanel);
        featurePanel.add(extraFeaturePanel);
        checkBoxPanel.add(MSButton);
        checkBoxPanel.add(ZCButton);
        checkBoxPanel.add(ENButton);
        checkBoxPanel.add(MFCButton);
//        panelWithName.add(checkBoxPanel);
//        panelWithName.add(songNameLabel);
        

        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new GridLayout(0, 4, 30, 30));

        for (int i = 0; i < resultLabels.length; i ++){
            resultLabels[i] = new JLabel();

            resultButton[i] = new JButton(resultLabels[i].getText());

            resultButton[i].addActionListener(this);

            resultButton[i].setVisible(false);
            resultPanel.add(resultLabels[i]);
            resultPanel.add(resultButton[i]);
        }


        resultPanel.setBorder(BorderFactory.createEmptyBorder(30,16,10,16));

        contentPane = (JPanel)this.getContentPane();
        setSize(800,900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        contentPane.add(featurePanel, BorderLayout.PAGE_START);
        contentPane.add(checkBoxPanel,BorderLayout.PAGE_END);
        contentPane.add(resultPanel, BorderLayout.CENTER);

        contentPane.setVisible(true);
        setVisible(true);

    }

    public void actionPerformed(ActionEvent e){
        if (e.getSource() == openButton){
            if (fileChooser == null) {
                fileChooser = new JFileChooser(querySet);

                fileChooser.addChoosableFileFilter(new AudioFilter());
                fileChooser.setAcceptAllFileFilterUsed(false);
            }
            int returnVal = fileChooser.showOpenDialog(SoundEffectDemo.this);

            if (returnVal == JFileChooser.APPROVE_OPTION){
                queryAudio = fileChooser.getSelectedFile();
            }

            fileChooser.setSelectedFile(null);

            queryButton.setText(queryAudio.getName());

            fileChooser.setSelectedFile(null);

        }else if (e.getSource() == searchButton){
        	String msC = "0", zcC = "0", enC = "0", mfcC = "0";
        	String checkBit = "";
            SearchDemo searchDemo = new SearchDemo();
            if(MSButton.isSelected()){
            	msC = "1";
            }
            
            if(ZCButton.isSelected()){
				zcC = "1";       	
			}
			
			if(ENButton.isSelected()){
				enC = "1";
			}
			
			if(MFCButton.isSelected()){
				mfcC = "1";
			}
			
			checkBit = msC+zcC+enC+mfcC;
			resultFiles = searchDemo.resultList(queryAudio.getAbsolutePath(), checkBit);
            for (int i = 0; i < resultFiles.size(); i ++){
                resultLabels[i].setText(resultFiles.get(i));
                resultButton[i].setText(resultFiles.get(i));
                resultButton[i].setVisible(true);
            }

        }else if (e.getSource() == queryButton){
            new SoundEffect(queryAudio.getAbsolutePath()).play();
            
        }else if (e.getSource() == songButton){
        	try {
				String songName = IdentifyProtocolV1.getSong();
				songNameLabel.setText("Title: "+songName);
				songNameLabel.setVisible(true);
				//System.out.println(songName);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            
        }else if (e.getSource() == recordButton){
            try {
				String recordFile = JavaSoundRecorder.record();
				queryAudio = new File(recordFile);
				queryButton.setText(queryAudio.getName());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }else if (e.getSource() == rfButton){
        	SearchDemo searchDemo = new SearchDemo();
            String queryName = queryAudio.getName();
            String checkBit = "";
            String msC="0",zcC="0",enC="0",mfcC="0";
            if(MSButton.isSelected()){
            	msC = "1";
            }
            
            if(ZCButton.isSelected()){
				zcC = "1";       	
			}
			
			if(ENButton.isSelected()){
				enC = "1";
			}
			
			if(MFCButton.isSelected()){
				mfcC = "1";
			}
			checkBit = msC + zcC + enC + mfcC;
			
            PseudoRF rf = new PseudoRF();
            ArrayList<String> rfQueries = rf.getRFqueries(queryName, resultFiles);
            ArrayList<Double> weightList = rf.getWeightList(rfQueries.size());
            resultFiles = searchDemo.giveRF(rfQueries,weightList,checkBit);
            
            for (int i = 0; i < resultFiles.size(); i ++){
                resultLabels[i].setText(resultFiles.get(i));
                resultButton[i].setText(resultFiles.get(i));
                resultButton[i].setVisible(true);
            }
            
        }
        else {
            for (int i = 0; i < resultSize; i ++){
                if (e.getSource() == resultButton[i]){
                    String filePath = basePath+resultFiles.get(i);
                    new SoundEffect(filePath).play();
                    break;
                }
            }
        }
    }

    public static void main(String[] args) {
        new SoundEffectDemo();
    }
}
