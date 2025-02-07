import java.awt.*;
import java.awt.Graphics;
import java.util.LinkedList;
import java.awt.geom.Line2D;

/*
 * Enemy class
 */
public class Enemy extends Character {

 private int sourceX, sourceY;  // X, Y coordinate of the enemy's tile
 private int destinationX, destinationY;  // X, Y coordinate of the destination's tile
 private int speedFactor;
 private int randomTime; // Random time between changing position while patrolling
 private final int FOVRADIUS = 250;
 private final int HEARING_RADIUS = 180;
 private Clock2 updateSearchTimer;  // Timer used to consistently update the enemy's path
 private Clock2 roamTimer; // Timer used to create delay between runs of path finding
 private Clock2 loseSuspicionTimer;  // Timer for returning to normal after being alerted
 private Clock2 footstepTimer;
 private String route;  // String representation of the enemy's movement route
 private String lookingDirection;  // Direction the enemy is facing
 public boolean alerted;
 public boolean isSuspicious;
 public boolean isDead;
 public Line2D lineToPlayer;  // Invisible line drawn from enemy to player
 
 public Enemy(int y, int x) {
  super(x + CHARC_WIDTH/4, y + CHARC_WIDTH/4, Character.CHARC_WIDTH);
  setSprite("enemy_right.png");
  lookingDirection = "right";
  route = "";
  speedFactor = 120;
  destinationX = sourceX;
  destinationY = sourceY;
  sourceX = (int)(realX + CHARC_WIDTH/2) / 60;
  sourceY = (int)(realY + CHARC_WIDTH/2) / 60;
  randomTime = (int)(Math.random() * 5) + 4;
  alerted = false;
  isSuspicious = false;
  lineToPlayer = new Line2D.Double();
  roamTimer = new Clock2();
  updateSearchTimer = new Clock2();
 }
 
 /* Checks if an enemy can hear player
  * @param player
  * @return True if player is making noise, not hiding, and within distance
  */
 private boolean canHearPlayer(Player p) {
  return p.isMoving && !p.isSneaking && !p.isHiding && getLineDistance(lineToPlayer) < HEARING_RADIUS;
 }
 
 /* Checks if enemy can see player
  * @param player
  * @param map
  * @return True if LOS to player is unobstructed, looking direction is toward player, etc.
  */
 private boolean canSeePlayer(Player p, Map localMap) {
  // If enemy is in alert mode, the looking direction will not matter
  if(alerted) {
   for(MapObject[] row : localMap.getMapArr()) {
    for(MapObject tile : row) {
     if(tile.isCollidable() && lineToPlayer.intersects(tile.getRect())) {
      return false;
     }
    }
   }
   return true;
  } else {
   for(MapObject[] row : localMap.getMapArr()) {
    for(MapObject tile : row) {
     if(tile.isCollidable() && lineToPlayer.intersects(tile.getRect())) {
      return false;
     }
    }
   }
   if(p.isHiding) {
    return false;
   } else return getLineDistance(lineToPlayer) < FOVRADIUS &&
     ((lookingDirection == "right" && realX < p.realX) ||
       (lookingDirection == "left" && realX > p.realX) ||
       (lookingDirection == "up" && realY > p.realY) ||
       (lookingDirection == "down" && realY < p.realY));
  }
 }
 
 /* Generate a valid random location on the map for the enemy to move to
  * @param localMap
  * @return a valid Point
  */
 private Point genRandomLocation(Map localMap) {
  int x, y;
  do {
   x = sourceX + (int)(Math.random() * 10) - 5;
   y = sourceY + (int)(Math.random() * 10) - 5;
  } while(x < 0 || x >= localMap.X_TILES || y < 0 || y >= localMap.Y_TILES ||
    localMap.getTile(y, x).isCollidable() || localMap.getTile(y, x) instanceof HidingSpot);
  return new Point(x,y);
 }
 
 /*
  * Update the enemy's information
  * @param map
  * @param player
  * @param elapsedTime
  */
 public void update(Map localMap, Player p, double elapsedTime) {
  if(!isDead) {
   
   // Footstep sound
   if(xSpeed == 0 && ySpeed == 0) {
    footstepTimer = null;
   }
   if(footstepTimer != null && getLineDistance(lineToPlayer) < 420) {
    footstepTimer.update();
    if(footstepTimer.getElapsedTime() / 1000 > 0.4) {
     if(canSeePlayer(p, localMap)) {
      new SoundEffect("enemyfootstep.wav");
     } else {
      new SoundEffect("enemyfootstepquiet.wav");
     }
     footstepTimer = null;
    }
   }

   // Update X & Y for its pathfinding, timers
   sourceX = (int)(realX + CHARC_WIDTH/2) / 60;
   sourceY = (int)(realY + CHARC_WIDTH/2) / 60;
   roamTimer.update();
   updateSearchTimer.update();
   lineToPlayer.setLine(realX + CHARC_WIDTH/2, realY + CHARC_WIDTH/2,
     p.realX + CHARC_WIDTH/2, p.realY + CHARC_WIDTH/2);
   
   // Update timer for losing suspicion
   if(loseSuspicionTimer != null) {
    loseSuspicionTimer.update();
    if(loseSuspicionTimer.getElapsedTime() / 1000 > 6) {
     removeAlert();
     removeSuspicious();
    }
   }

   // Update the destination based on whether enemy is alerted, suspicious, or in patrol mode
   // If not alerted or suspicious, move around at random invervals
   if(!(alerted || isSuspicious) && roamTimer.getElapsedTime() / 1000 > randomTime) {
    roamTimer = new Clock2();
    Point randomLoc;
    int i = 0;
    // Keep generating locations until a reachable one is found (unless enemy is trapped, in which the gen will stop)
    do {
     i++;
     randomLoc = genRandomLocation(localMap);
     destinationX = randomLoc.x;
     destinationY = randomLoc.y;
    } while(pathFind(localMap, sourceX, sourceY, destinationX, destinationY).equals("") && i < 1000);
    randomTime = (int)(Math.random() * 8) + 4;
   }

   // Proc alert mode or suspicious mode
   if(canSeePlayer(p, localMap)) {
    alert(p);
    isSuspicious = true;
    loseSuspicionTimer = new Clock2();
    destinationX = (int)(p.realX + CHARC_WIDTH/2) / 60; // Destination update exists both here and in the alert/makesuspcious
    destinationY = (int)(p.realY + CHARC_WIDTH/2) / 60; // method because outside calls need to update the destination once (i.e. gunshot)
   } else if(alerted) {
    removeAlert();
    makeSuspicious(p);
   } else if(sourceX == destinationX && sourceY == destinationY && alerted) {
    removeAlert();
   } else if(canHearPlayer(p) && !pathFind(localMap, sourceX, sourceY, (int)(p.realX + CHARC_WIDTH/2) / 60,
     (int)(p.realY + CHARC_WIDTH/2) / 60).equals("")) {
    makeSuspicious(p);
    destinationX = (int)(p.realX + CHARC_WIDTH/2) / 60;
    destinationY = (int)(p.realY + CHARC_WIDTH/2) / 60;
   } else if(sourceX == destinationX && sourceY == destinationY && isSuspicious) {
    removeSuspicious();
   }
   
   // Refresh the the route to destination - only every 0.2 seconds to prevent unnecessary slowage
   if(updateSearchTimer.getElapsedTime() / 1000 > 0.2) {
    route = pathFind(localMap, sourceX, sourceY, destinationX, destinationY);
    updateSearchTimer = new Clock2();
   }
   
   // Have enemy move along its given route
   followPath(p, localMap);
  }
 }
 
 /*
  * Make enemy alerted
  * @param player
  */
 public void alert(Player p) {
  if(!alerted && !isDead) {
    new SoundEffect("alert.wav");
   alerted = true;
   speedFactor = 140;
   loseSuspicionTimer = new Clock2();
   destinationX = (int)(p.realX + CHARC_WIDTH/2) / 60;
   destinationY = (int)(p.realY + CHARC_WIDTH/2) / 60;
  }
 }
 
 /*
  * Remove alerted status
  */
 public void removeAlert() {
  if(alerted) {
   alerted = false;
   speedFactor = 120;
   roamTimer = new Clock2();
   loseSuspicionTimer = null;
  }
 }
 
 /*
  * Make enemy suspicious
  * @param player
  */
 public void makeSuspicious(Player p) {
  if(!isSuspicious && !isDead) {
   new SoundEffect("huh.wav");
   isSuspicious = true;
   loseSuspicionTimer = new Clock2();
   destinationX = (int)(p.realX + CHARC_WIDTH/2) / 60;
   destinationY = (int)(p.realY + CHARC_WIDTH/2) / 60;
  }
 }
 
 /*
  * remove suspicious status
  */
 public void removeSuspicious() {
  new SoundEffect("giveup.wav");
  isSuspicious = false;
  roamTimer = new Clock2();
  loseSuspicionTimer = null;
 }
 
 /*
  * Set enemy's state to dead
  */
 public void kill() {
  isDead = true;
  alerted = false;
  speedFactor = 0;
 }
 
 /*
  * Breadth-first path finding method
  * @param map
  * @param starting X
  * @param starting Y
  * @param destination X
  * @param destination Y
  */
 private String pathFind(Map localMap, int currentX, int currentY, int destX, int destY) {  
  
  // Store search information in LinkedLists
  LinkedList<Integer> xQueue = new LinkedList<Integer>();
  LinkedList<Integer> yQueue = new LinkedList<Integer>();
  LinkedList<String> path = new LinkedList<String>();
  boolean[][] visited = new boolean[localMap.Y_TILES][localMap.X_TILES];
 
  // Add the starting location first
  xQueue.add(currentX);
  yQueue.add(currentY);
  path.add("");
  
  int i = 0; // Counter to stop searches that are too long
  while(true) {
   i++;
   if(xQueue.isEmpty() || i > 100000) {  // Stop the search if no path found or path is too long
    isSuspicious = false;
    return "";  // Return empty string if no path found (movement will be interpreted as no movement)
   }
   
   // Pop from the queue and check its validity
   int x = xQueue.pop();
   int y = yQueue.pop();
   String dir = path.pop();   
   visited[y][x] = true;
   
   // Return route if destination path is found
   if(x == destX && y == destY) {
    return dir;
   }
   
   // Add next tiles to the queues
   else if(x >= 0 && x < localMap.X_TILES &&
     y >= 0 && y < localMap.Y_TILES &&
     !localMap.getTile(y,x).isCollidable()) {

    // Pathfind as normal, unless there is a hiding spot, in which only pathfind if alerted
    if(localMap.getTile(y,x) instanceof HidingSpot) {
     if(alerted) {
      if(!visited[y][x+1]) {
       xQueue.add(x + 1);
       yQueue.add(y);
       path.add(dir + "r");
      }
      if(!visited[y][x-1]) {
       xQueue.add(x - 1);
       yQueue.add(y);
       path.add(dir + "l");
      }
      if(!visited[y-1][x]) {
       xQueue.add(x);
       yQueue.add(y - 1);
       path.add(dir + "u");
      }
      if(!visited[y+1][x]) {
       xQueue.add(x);
       yQueue.add(y + 1);
       path.add(dir + "d");
      }
     }
    } else {
     if(!visited[y][x+1]) {
      xQueue.add(x + 1);
      yQueue.add(y);
      path.add(dir + "r");
     }
     if(!visited[y][x-1]) {
      xQueue.add(x - 1);
      yQueue.add(y);
      path.add(dir + "l");
     }
     if(!visited[y-1][x]) {
      xQueue.add(x);
      yQueue.add(y - 1);
      path.add(dir + "u");
     }
     if(!visited[y+1][x]) {
      xQueue.add(x);
      yQueue.add(y + 1);
      path.add(dir + "d");
     }
    }
   }
  }
 }
 
 /*
  *  Set the enemy's speed acoording its pathed route
  *  @param player
  *  @param map
  */
 private void followPath(Player p, Map localMap) {
  if(route.length() == 0) {
   if(alerted && canSeePlayer(p, localMap)) {
    if(realX < p.realX) {
     xSpeed = (1);
     lookingDirection = "right";
     setSprite("enemy_right.png");
    } else {
     xSpeed = (-1);
     lookingDirection = "left";
     setSprite("enemy_left.png");
    }
    if(realY < p.realY) {
     ySpeed = (1);
     lookingDirection = "down";
     setSprite("enemy_down.png");
    } else{
     ySpeed = (-1);
     lookingDirection = "up";
     setSprite("enemy_up.png");
    }
   } else {
    xSpeed = (0);
    ySpeed = (0);
   }
  } else if(route.substring(0,1).equals("r")){
   xSpeed = (1);
   ySpeed = (0);
   lookingDirection = "right";
   setSprite("enemy_right.png");
   if(footstepTimer == null) {
    footstepTimer = new Clock2();
   }
  } else if(route.substring(0,1).equals("l")) {
   xSpeed = (-1);
   ySpeed = (0);
   lookingDirection = "left";
   setSprite("enemy_left.png");
   if(footstepTimer == null) {
    footstepTimer = new Clock2();
   }
  } else if(route.substring(0,1).equals("u")) {
   ySpeed = -1;
   xSpeed = 0;
   lookingDirection = "up";
   setSprite("enemy_up.png");
   if(footstepTimer == null) {
    footstepTimer = new Clock2();
   }
  } else if(route.substring(0,1).equals("d")) {
   ySpeed = 1;
   xSpeed = 0;
   lookingDirection = "down";
   setSprite("enemy_down.png");
   if(footstepTimer == null) {
    footstepTimer = new Clock2();
   }
  } else {
   xSpeed = 0;
   ySpeed = 0;
  }
 }
 
 public boolean collidingWithPlayer(Player p) {
  return getRect().intersects(p.getRect());
 }

 public void moveX(double elapsedTime) {
  realX = (realX + xSpeed * elapsedTime * speedFactor);
  getRect().x = (int)realX;
 }
 
 public void moveY(double elapsedTime) {
  realY = (realY + ySpeed * elapsedTime * speedFactor);
  getRect().y = (int)realY;
 }
 
 /*
  * Draw method for enemy's colored status indicator
  * @param graphics object
  * @param player
  */
 @Override
 public void draw(Graphics g, Player p) {
  if(alerted) {
   g.setColor(Color.RED);
  } else if(isDead){
   g.setColor(Color.GRAY);
  } else if(isSuspicious){
   g.setColor(Color.YELLOW);
  } else {
   g.setColor(Color.BLUE);
  }
  super.draw(g,p);
 }
 
 /*
  * Get length of a line
  * @param Line2D  lineto measure
  * @return double  distance
  */
 public static double getLineDistance(Line2D line) {
  return Math.sqrt(Math.pow(line.getX1() - line.getX2(), 2) + Math.pow(line.getY1() - line.getY2(),2));
 }
}
