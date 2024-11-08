import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JFileChooser;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import static java.nio.file.StandardOpenOption.CREATE;
public class TagExtractorFrame extends JFrame
{
    private JPanel mainPanel, titlePanel, filePanel, displayPanel;
    private JLabel titleLabel;
    private JButton fileButton, quitButton;
    private JScrollPane displayScroll;
    private JTextArea displayText;
    private ActionListener quit = new quitListener();
    private ActionListener file = new fileListener();
    private JFileChooser chooser;
    TagExtractorFrame()
    {
        setTitle("Tag Extractor");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainPanel = new JPanel();
        titlePanel = new JPanel();
        filePanel = new JPanel();
        displayPanel = new JPanel();
        titleLabel = new JLabel("Tag Extractor");
        displayText = new JTextArea(30,60);
        displayScroll = new JScrollPane(displayText);
        chooser = new JFileChooser();
        fileButton = new JButton("Choose File");
        fileButton.setFont(new Font("Serif", Font.PLAIN, 25));
        fileButton.addActionListener(file);
        quitButton = new JButton("Quit");
        quitButton.setFont(new Font("Serif", Font.PLAIN, 25));
        quitButton.addActionListener(quit);
        add(mainPanel);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(displayPanel, BorderLayout.CENTER);
        mainPanel.add(filePanel, BorderLayout.SOUTH);
        titlePanel.add(titleLabel);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 40));
        displayPanel.add(displayScroll);
        displayText.setFont(new Font("Serif", Font.PLAIN, 20));
        filePanel.add(fileButton);
        filePanel.add(quitButton);
    }
    private class fileListener implements ActionListener
    {
        public void actionPerformed(ActionEvent AE)
        {
            try
            {
                File docImport;
                File noiseImport = new File(System.getProperty("user.dir") + "\\English Stop Words.txt");
                File workingDirectory = new File(System.getProperty("user.dir"));
                chooser.setCurrentDirectory(workingDirectory);
                if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
                {
                    docImport = chooser.getSelectedFile();
                    Path docFile = docImport.toPath();
                    Path noiseFile = noiseImport.toPath();
                    displayText.setText("");
                    displayText.append(docImport.getName());
                    displayText.append("\n\n---------------------------------------------------\n\n");
                    Map<String, Integer> docWords = new TreeMap<>();
                    Set<String> noiseWords = new TreeSet<>();
                    InputStream docIn = new BufferedInputStream(Files.newInputStream(docFile, CREATE));
                    BufferedReader docReader = new BufferedReader(new InputStreamReader(docIn));
                    InputStream noiseIn = new BufferedInputStream(Files.newInputStream(noiseFile, CREATE));
                    BufferedReader noiseReader = new BufferedReader(new InputStreamReader(noiseIn));
                    while(noiseReader.ready())
                    {
                        for(String word : noiseReader.readLine().split("[^a-zA-Z]+"))
                        {
                            noiseWords.add(word.toLowerCase());
                        }
                    }
                    noiseReader.close();
                    while(docReader.ready())
                    {
                        for(String docWord : docReader.readLine().split("[^a-zA-Z]+"))
                        {
                            if ((!(noiseWords.contains(docWord.toLowerCase()))) && (!(docWord.equals("")))){
                                Integer count = docWords.get(docWord);
                                if (count == null)
                                {
                                    count = 1;
                                }
                                else
                                {
                                    count++;
                                }
                                docWords.put(docWord.toLowerCase(), count);
                            }
                        }
                    }
                    docReader.close();
                    noiseReader.close();
                    for (String key : docWords.keySet())
                    {
                        displayText.append(String.format("  %-20s %-20s\n", key, docWords.get(key)));
                    }
                }
                else
                {
                    displayText.append("Failed to choose a file to process");
                    displayText.append("Run the program again!");
                }
            }
            catch (FileNotFoundException e)
            {
                System.out.println("File not found!!!");
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    private class quitListener implements ActionListener
    {
        public void actionPerformed(ActionEvent AE)
        {
            System.exit(0);
        }
    }
}