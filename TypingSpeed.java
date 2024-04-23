import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;



// abstract class
abstract class TypingEventHandler implements KeyListener 
{
    abstract void keyTypedEvent(KeyEvent e);

    @Override
    public void keyTyped(KeyEvent e) 
    {
        keyTypedEvent(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}



// Class for typing events and prompt label
class TypingListener extends TypingEventHandler 
{
    private JLabel promptLabel;
    private String predefinedText;

    public TypingListener(JLabel promptLabel, String predefinedText) 
    {
        this.promptLabel = promptLabel;
        this.predefinedText = predefinedText;
    }

    @Override
    void keyTypedEvent(KeyEvent e) 
    {
        char typedChar = e.getKeyChar();
        String prompt = promptLabel.getText();
        if (prompt.length() > 0 && prompt.charAt(0) == typedChar) 
        {
            prompt = prompt.substring(1);
            promptLabel.setText(prompt);
        }
    }
}



// Abstract class
abstract class TypingTimer extends Thread 
{
    protected boolean running = true;

    abstract void stopTimer();
}


class TypingSpeedTimer extends TypingTimer 
{
    private long startTime;
    private JLabel timeLabel;

    public TypingSpeedTimer(JLabel timeLabel) 
    {
        this.timeLabel = timeLabel;
    }

    @Override
    public void run() 
    {
        startTime = System.currentTimeMillis();
        while (running) 
        {
            long elapsedTime = System.currentTimeMillis() - startTime;
            double minutes = (double) elapsedTime / (1000 * 60);
            timeLabel.setText(String.format("Time taken: %.2f minutes", minutes));
            try 
            {
                Thread.sleep(1000); // Update every second
            } 
            catch (InterruptedException e) 
            {
                e.printStackTrace();
            }
        }
    }

    void stopTimer() 
    {
        running = false;
    }
}



public class TypingSpeed extends Frame implements ActionListener 
{
    private JLabel welcomeLabel, textLabel, promptLabel, timeLabel, resultLabel, accuracyLabel;
    private JTextArea inputArea;
    private JButton startButton, stopButton;
    private String predefinedText = "Can you type faster than me?";
    private int correctCount = 0;
    private int totalCount = 0;
    private TypingSpeedTimer typingTimer;

    public void TypingSpeedCalculator() 
    {
        setTitle("Typing Speed Calculator");
        setSize(800, 500); 
        setLayout(null);

        welcomeLabel = new JLabel("Welcome to Typing Speed Calculator!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setBounds(200, 30, 400, 30); 
        add(welcomeLabel);

        textLabel = new JLabel("Predefined Text:");
        textLabel.setBounds(50, 70, 100, 20);
        add(textLabel);

        promptLabel = new JLabel(predefinedText);
        promptLabel.setBounds(50, 90, 700, 20); 
        add(promptLabel);

        inputArea = new JTextArea();
        inputArea.setBounds(50, 120, 700, 200); 
        inputArea.setBorder(new LineBorder(Color.BLUE, 2));
        add(inputArea);

        startButton = new JButton("Start");
        startButton.setBounds(50, 350, 100, 30); 
        startButton.addActionListener(this);
        add(startButton);

        stopButton = new JButton("Stop");
        stopButton.setBounds(200, 350, 100, 30);
        stopButton.addActionListener(this);
        stopButton.setEnabled(false);
        add(stopButton);

        timeLabel = new JLabel("");
        timeLabel.setBounds(50, 400, 200, 20); 
        add(timeLabel);

        resultLabel = new JLabel("");
        resultLabel.setBounds(300, 400, 200, 20); 
        add(resultLabel);

        accuracyLabel = new JLabel("");
        accuracyLabel.setBounds(550, 400, 200, 20); // Adjusted position
        add(accuracyLabel);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }
    private long startTime;
    @Override
    public void actionPerformed(ActionEvent e) 
    {
        if (e.getSource() == startButton) {
            try {
                typingTimer = new TypingSpeedTimer(timeLabel);
                startTime = System.currentTimeMillis();
                typingTimer.start();
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
                inputArea.setEditable(true);
                inputArea.addKeyListener(new TypingListener(promptLabel, predefinedText));
            } 
            catch (IllegalStateException ex) 
            {
                System.err.println("Timer thread already started.");
            }
        } 
        else if (e.getSource() == stopButton) 
        {
            if (typingTimer != null) 
            {
                typingTimer.stopTimer();
            }
            stopButton.setEnabled(false);
            inputArea.setEditable(false);
            calculateSpeed();
        }
    }

    private void calculateSpeed() 
    {
        if (typingTimer == null) 
        {
            return;
        }
        long elapsedTime = System.currentTimeMillis() - startTime;
        double minutes = (double) elapsedTime / (1000 * 60);
        String inputText = inputArea.getText();
        String[] predefinedWords = predefinedText.split("\\s+");
        String[] inputWords = inputText.split("\\s+");
        
        // Accuracy calculation
        correctCount = 0;
        totalCount = Math.min(predefinedWords.length, inputWords.length);
        for (int i = 0; i < totalCount; i++) 
        {
            if (predefinedWords[i].equals(inputWords[i])) 
            {
                correctCount++;
            }
        }
        double accuracy = ((double) correctCount / totalCount) * 100;

        int wordCount = inputWords.length;
        double typingSpeed = wordCount / minutes;
        resultLabel.setText(String.format("Typing speed: %.2f words per min", typingSpeed));
        accuracyLabel.setText(String.format("Accuracy: %.2f%%", accuracy));
        startButton.setEnabled(true);
    }

    public static void main(String[] args) 
    {
        TypingSpeed tsc = new TypingSpeed();
        tsc.TypingSpeedCalculator();
    }
}
