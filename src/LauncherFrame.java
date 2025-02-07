
import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;

/*
 * Launcher JFrame
 */
public class LauncherFrame extends JFrame{

 JFrame thisFrame;
 JPanel cardPanel;
 OptionsPanel optionsPanel;
 CardLayout cardLayout;  // CardLayout for switching between panels
 boolean musicEnabled;
 int mapNum;

 public LauncherFrame() { 
  super("Start Screen");
  this.thisFrame = this;

  //Configure the window
  this.setSize(1280,720);    
  this.setLocationRelativeTo(null); //start the frame in the center of the screen
  this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
  this.setResizable (false);
  
  // Intialize cardLayout
  cardPanel = new JPanel();
  cardLayout = new CardLayout();
  cardPanel.setLayout(cardLayout);
  
  // Main Menu panel
  JPanel menuPanel = new JPanel();
  menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
  menuPanel.setBackground(Color.BLACK);
  JLabel titleLabel = new JLabel("SNEAK 100");
  titleLabel.setForeground(Color.WHITE);
  titleLabel.setFont(new Font("Gill Sans MT", Font.PLAIN, 100));
  titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
  titleLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
  menuPanel.add(Box.createRigidArea(new Dimension(0,150)));
  menuPanel.add(titleLabel);
  menuPanel.add(Box.createRigidArea(new Dimension(0,60)));
  
  addButton("Start", menuPanel);
  addButton("How to Play", menuPanel);
  addButton("Options", menuPanel);
  addButton("Quit", menuPanel);
  
  cardLayout.show(cardPanel, "Menu");
 
  // Add panels to cardlayout panel
  cardPanel.add(menuPanel, "Menu");
  cardPanel.add(new TutorialPanel(), "Tutorial");
  optionsPanel = new OptionsPanel();
  cardPanel.add(optionsPanel, "Options");
  this.add(cardPanel);
  this.setVisible(true);
 }
 
 /*
  * Add a button to a container with a listener
  * @param text of the button
  * @param panel to add to
  */
 private void addButton(String text, Container panel) {
  panel.add(Box.createRigidArea(new Dimension(0,30)));
  JButton button = new JButton(text);
  button.setBackground(Color.BLACK);
  button.setForeground(Color.WHITE);
  button.setFont(new Font("Gill Sans MT", Font.PLAIN, 20));
  button.addActionListener(new ButtonListener(text));
  button.setFocusPainted(false);
  button.setAlignmentX(Component.CENTER_ALIGNMENT);
  panel.add(button);
 }

 // Main method
 public static void main(String[] args) {
  new LauncherFrame();
 }
 
 // ~~~~~ INNER CLASSES ~~~~~
 /*
  *  Tutorial panel
  */
 class TutorialPanel extends JPanel{
  
  BufferedImage tutImage;
  
  public TutorialPanel() {
   setBackground(new Color(0,true));
   // Load tutorial image
   try {
    tutImage = ImageIO.read(new File("assets/tutorial.png"));
    this.add(Box.createRigidArea(new Dimension(0,1300)));
    addButton("Return", this);
   } catch (IOException e) {
    System.out.println("error loading image: tutorial.png");
    e.printStackTrace();
   }
  }
  
  // Overrides paintcomponent so that button can be drawn over the tut image
  @Override
  public void paintComponent(Graphics g) {
   g.drawImage(tutImage, 0, 0, getWidth(), getHeight(), this);
   super.paintComponent(g);
  }
 }
 
 /*
  * Options panel
  */
 class OptionsPanel extends JPanel{
  
  BufferedImage mapImage;
  JButton togMusic;
  JButton togMap;
  
  public OptionsPanel() {
   musicEnabled = true;
   mapNum = 0;
   setBackground(Color.BLACK);
   setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
   addButton("Return", this);
   add(Box.createRigidArea(new Dimension(0,50)));
   togMusic = new JButton("Toggle Music: ON");
   togMusic.setBackground(Color.BLACK);
   togMusic.setForeground(Color.WHITE);
   togMusic.setFont(new Font("Gill Sans MT", Font.PLAIN, 20));
   togMusic.addActionListener(new ButtonListener("togglemusic"));
   togMusic.setFocusPainted(false);
   togMusic.setAlignmentX(Component.CENTER_ALIGNMENT);
   add(togMusic);
   
   add(Box.createRigidArea(new Dimension(0,30)));
   togMap = new JButton("Toggle Map: Map #0");
   togMap.setBackground(Color.BLACK);
   togMap.setForeground(Color.WHITE);
   togMap.setFont(new Font("Gill Sans MT", Font.PLAIN, 20));
   togMap.addActionListener(new ButtonListener("togglemap"));
   togMap.setFocusPainted(false);
   togMap.setAlignmentX(Component.CENTER_ALIGNMENT);
   add(togMap);   
  }
  
  /*
   * Toggle an option
   */
  public void toggleOption(String opt) {
   if(opt.equals("togglemusic")) {
    if(musicEnabled) {
     musicEnabled =  false;
     togMusic.setText("Toggle Music: OFF");
    } else {
     musicEnabled = true;
     togMusic.setText("Toggle Music: ON");
    }
   } else if(opt.equals("togglemap")) {
    mapNum = (mapNum + 1) % 2;
    togMap.setText("Toggle Map: Map #" + mapNum);
   }
  }
  // Overrides paintcomponent to draw picture
  @Override
  public void paintComponent(Graphics g) {
   super.paintComponent(g);
   // Load map image
   try {
    mapImage = ImageIO.read(new File("assets/map" + mapNum + ".png"));
   } catch (IOException e) {
    System.out.println("error loading image: map" + mapNum + ".png");
    e.printStackTrace();
   }
   g.drawImage(mapImage, thisFrame.getWidth()/2 - mapImage.getWidth()/4,
     300, mapImage.getWidth()/2, mapImage.getHeight()/2, this);
   repaint();
  }
 }

 //This is an inner class that is used to detect a button press
 class ButtonListener implements ActionListener {
  
  String action;
  
  ButtonListener(String a){
   super();
   action = a;
  }
  
  public void actionPerformed(ActionEvent event)  {  
   switch(action) {
   case "Start" :
    System.out.println("Starting game");
    thisFrame.dispose();
    new GameFrame(musicEnabled, mapNum);
    break;
   case "How to Play" :
    cardLayout.show(cardPanel, "Tutorial");
    break;
   case "Return" :
    cardLayout.show(cardPanel, "Menu");
    break;
   case "Options" :
    cardLayout.show(cardPanel, "Options");
    break;
   case "Quit" :
    thisFrame.dispose();
    break;
   case "togglemusic" :
    optionsPanel.toggleOption("togglemusic");
    break;
   case "togglemap" :
    optionsPanel.toggleOption("togglemap");
    break;
   }
  }

 }

}