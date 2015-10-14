package Player;
import Search.SearchDemo;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

/**
 * Created by workshop on 9/18/2015.
 */
public class SoundEffectDemo extends JFrame implements ActionListener{

    JPanel contentPane;
    JPanel checkBoxPanel = new JPanel(new GridLayout(0,2));
    JButton openButton, searchButton, queryButton;
    JFileChooser fileChooser;
    JCheckBox MSButton, ZCButton, ENButton, MFCButton;


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

        // Set up UI components;
        openButton = new JButton("Select an audio clip...");
        openButton.addActionListener(this);

        String tempName = "";

        queryButton = new JButton("Current Audio:"+tempName);
        queryButton.addActionListener(this);

        searchButton = new JButton("Search");
        searchButton.addActionListener(this);
        
        MSButton = new JCheckBox("Magnitude Spectrum Match");
        MSButton.addActionListener(this);
        
        ZCButton = new JCheckBox("Zero Crossing Match");
        ZCButton.addActionListener(this);
        
        ENButton = new JCheckBox("Energy Match");
        ENButton.addActionListener(this);
        
        MFCButton = new JCheckBox("MFCC Match");
        MFCButton.addActionListener(this);

        checkBoxPanel.add(MSButton);

        checkBoxPanel.add(ZCButton);

        checkBoxPanel.add(ENButton);

        checkBoxPanel.add(MFCButton);
        
        JPanel queryPanel = new JPanel();
        queryPanel.add(openButton);
        queryPanel.add(queryButton);
        queryPanel.add(searchButton);
        

        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new GridLayout(0, 4, 60, 60));

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

        contentPane.add(queryPanel, BorderLayout.PAGE_START);
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
        }else {
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
