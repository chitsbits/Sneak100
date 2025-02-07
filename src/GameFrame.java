
import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DecimalFormat;


/*
 * JFrame of the game
 */
class GameFrame extends JFrame{

 public Sneak100 game;
 public static final int WINDOW_WIDTH = 1280;
 public static final int WINDOW_HEIGHT = 720;
 
 /* Constructor
  * @param musicEnabled  setting specified by the launcher frame
  * @param mapNum  setting specified by the launcher frame
  */
 public GameFrame(boolean musicEnabled, int mapNum) {
  super();
  setTitle("Sneak 100");
  setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
  setResizable(false);
  setLocationRelativeTo(null);
  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  getContentPane().add(new GamePanel(musicEnabled, mapNum));
  pack();
  setVisible(true);
 }
 
 public void quitGame() {
  dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
 }

 // ~~~ INNER CLASSES ~~~~
 /*
  * Main JPanel of the game
  */
 class GamePanel extends JPanel implements KeyListener, MouseListener {
  
  private FrameRate frameRate;
  private Clock clock;
  private Point cursor;
  
  public GamePanel(boolean musicEnabled, int mapNum) {
   
   // Initalize game
   game = new Sneak100(musicEnabled, mapNum);
   
   setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
   addKeyListener(this);
   addMouseListener(this);
   setFocusable(true);
   requestFocusInWindow();
   setBackground(Color.DARK_GRAY);
   setLayout(null);
   
   // Add HUD panels
   GunPanel gunPanel = new GunPanel();   
   add(gunPanel);
   InfoPanel infoPanel = new InfoPanel();
   add(infoPanel);
   
   frameRate = new FrameRate();
   clock = new Clock();
   cursor = new Point(); 
  }
  
  /*
   *  Main game loop method, updates game and draws all elements
   */
  @Override
  public void paintComponent(Graphics g) {
   super.paintComponent(g);
   setDoubleBuffered(true); 
   
   // Update calls
   clock.update();
   frameRate.update();
   
   cursor.x = MouseInfo.getPointerInfo().getLocation().x - getLocationOnScreen().x;
   cursor.y = MouseInfo.getPointerInfo().getLocation().y - getLocationOnScreen().y;
   
   // Update game
   if(!game.gameOver) {
    game.update(clock.getDeltaTime(), cursor);
   }
   
   // Draw calls
   game.draw(g, this);
   frameRate.draw(g, 10, 10);
   
   repaint();
  }
  
  @Override
  public void keyPressed(KeyEvent e) {
   
   switch(e.getKeyCode()) {
   case KeyEvent.VK_W :
    game.inputArr[0] = true;
    break;
   case KeyEvent.VK_A :
    game.inputArr[1] = true;
    break;
   case KeyEvent.VK_S :
    game.inputArr[2] = true;
    break;
   case KeyEvent.VK_D :
    game.inputArr[3] = true;
    break;
   case KeyEvent.VK_SHIFT :
    game.inputArr[4] = true;
    break;
   case KeyEvent.VK_E :
    game.checkAction(cursor);
   }
  }

  @Override
  public void keyReleased(KeyEvent e) {
   
   switch(e.getKeyCode()) {
   case KeyEvent.VK_W :
    game.inputArr[0] = false;
    break;
   case KeyEvent.VK_A :
    game.inputArr[1] = false;
    break;
   case KeyEvent.VK_S :
    game.inputArr[2] = false;
    break;
   case KeyEvent.VK_D :
    game.inputArr[3] = false;
    break;
   case KeyEvent.VK_SHIFT :
    game.inputArr[4] = false;
    break;
   case KeyEvent.VK_E :
    game.endAction();
    break;
   case KeyEvent.VK_ESCAPE :
    quitGame();
   }
  }
  
  @Override
  public void mousePressed(MouseEvent e) {
   game.shoot();
  }

  // Unused listener interface methods
  @Override
  public void keyTyped(KeyEvent e) {
  }
  @Override
  public void mouseClicked(MouseEvent e) {
  }
  @Override
  public void mouseEntered(MouseEvent e) {
  }
  @Override
  public void mouseExited(MouseEvent e) {
  }
  @Override
  public void mouseReleased(MouseEvent e) {
  }
 }
 
 /*
  * JPanel for gun information
  */
 class GunPanel extends JPanel {
  
  BufferedImage image;
  JLabel ammoLabel;
  
  public GunPanel() {
   setLayout(null);
   setBounds(980, 550, 400, 120);   
   setBackground(new Color(0,0,0,0));
   
   ammoLabel = new JLabel();
   ammoLabel.setFont(new Font("Gill Sans MT", Font.PLAIN, 20));
   ammoLabel.setForeground(Color.WHITE);
   ammoLabel.setLocation(70,10);
   ammoLabel.setSize(100,100);
   add(ammoLabel);
  }
  
  public void paintComponent(Graphics g) {
   super.paintComponent(g);
   setDoubleBuffered(true);
   Weapon weapon = game.player.currentWeapon;
   if(weapon != null) {
    setBackground(new Color(255,0,0,200));
    try {
     image = ImageIO.read(new File("assets/sprites/" + weapon.getType() + "transparent.png"));
     g.drawImage(image, 190, 15, 100, 100, null);
    } catch (FileNotFoundException e) {
     e.printStackTrace();
     System.out.println("error loading sprite: " + weapon.getType() + "transparent.png");
    } catch (IOException e) {
     e.printStackTrace();
     System.out.println("error loading sprite: " + weapon.getType() + "transparent.png");  
    }
    ammoLabel.setText("AMMO: " + weapon.getAmmo());
   } else {
    setBackground(new Color(0,0,0,0));
    ammoLabel.setText("");
   }
   repaint();
  }
 }
 
 /*
  * JPanel for HUD info
  */
 class InfoPanel extends JPanel {
  
  JLabel gameTime;
  JLabel score;
  DecimalFormat df = new DecimalFormat("##.##");
  
  public InfoPanel() {
   setLayout(null);
   setBounds(0, 10, 250, 70);   
   setBackground(new Color(255,0,0,200));
   
   gameTime = new JLabel();
   gameTime.setFont(new Font("Gill Sans MT", Font.PLAIN, 20));
   gameTime.setForeground(Color.WHITE);
   gameTime.setBounds(10, 10, 240, 20);
   add(gameTime);
   
   score = new JLabel();
   score.setFont(new Font("Gill Sans MT", Font.PLAIN, 20));
   score.setForeground(Color.WHITE);
   score.setBounds(10, 35, 240, 20);
   add(score);
  }
  
  public void paintComponent(Graphics g) {
   super.paintComponent(g);
   setDoubleBuffered(true);
   
   gameTime.setText("TIME: " + df.format(game.gameTime.getElapsedTime() / 1000));
   score.setText("SCORE: " + game.player.score);
   
   repaint();
  }
 }
}