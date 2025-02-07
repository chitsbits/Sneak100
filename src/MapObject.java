import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Line2D;

/*
 * MapObject class, defines objects that stay static on the map (i.e. not characters)
 */
public abstract class MapObject extends WorldObject{
 
 private Clock2 interactionClock;  // Clock to time user's input
 public boolean interactable;
 private double useTime;  // Time in seconds it takes to proc the interaction
 public boolean isEnemySpawnPoint;

 /*
  * Constructor
  * @param x coord
  * @param y coord
  * @param width
  * @param interactable
  * @param use time
  */
 public MapObject(int x, int y, int width, boolean interact, double use) {
  super(x, y, width);
  interactable = interact;
  useTime = use;
  interactionClock = null;
  isEnemySpawnPoint = false;
 }
 
 /*
  * Begin an interaction
  */
 public void startInteraction() {
  if(interactionClock == null) {
   interactionClock = new Clock2();
  }
 }
 
 // Updates the interaction clock. Returns true if timer is done, false otherwise
 public boolean updateInteractionClock() {
  if(interactionClock != null) {
   interactionClock.update();
   if(interactionClock.getElapsedTime() / 1000 >= useTime) {
    endInteraction();
    return true;
   }
  }
  return false;
 }
 
 // End interaction
 public void endInteraction() {
  interactionClock = null;
 }
 
 /*
  * Overriden to draw green progress bar for interactions
  */
 @Override
 public void drawSprite(Graphics g, Player p) {
  super.drawSprite(g, p);
  // Draw green progres bar
  if(interactionClock != null && interactable) {
   g.setColor(Color.GREEN);
   g.fillRect((int)(getDrawnX(p)) + 10, (int)(getDrawnY(p)) + 45,
     (int)(40 * ((interactionClock.getElapsedTime() / 1000) / useTime)), 5);
   g.setColor(Color.WHITE);
   g.drawRect((int)(getDrawnX(p)) + 10, (int)(getDrawnY(p)) + 45, 40, 5);
  }
 }
 
 // @return length of a line
 public static double getLineDistance(Line2D line) {
  return Math.sqrt(Math.pow(line.getX1() - line.getX2(), 2) + Math.pow(line.getY1() - line.getY2(),2));
 }
}
