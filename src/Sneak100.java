import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;


// Game class
public class Sneak100 {
	
	public boolean gameOver;
	public boolean exitReached;
	public Player player;
	public Map map;
	public boolean[] inputArr;
	public MapObject currentInteractingTile;
	public Line2D cursorLine;
	private double cursorAngle;
	private final int INTERACTION_RADIUS = 80;
	private List<Enemy> enemyList;
	private List<Bullet> bulletList;
	private SoundEffect interactionSfx;
	private HidingSpot currentBush;
	public Clock2 gameTime;
	private Clock2 footstepTimer;
	
	// Constructor
	public Sneak100(boolean musicEnabled, int mapNum) {
				
		map = new Map(mapNum);
		player = new Player(map);
		inputArr = new boolean[6];
		cursorLine = new Line2D.Double();
		enemyList = new ArrayList<Enemy>();
		bulletList = new ArrayList<Bullet>();
		currentBush = null;
		gameTime = new Clock2();
		
		if(musicEnabled) {
			SoundEffect.playMusic("override.wav");
		}
		
		// Loop through map and spawn enemies
		for(int i = 0; i < map.Y_TILES; i++) {
			for(int j = 0; j < map.X_TILES; j++) {
				if(map.getTile(i,j).isEnemySpawnPoint) {
					enemyList.add(new Enemy(i * Map.TILE_WIDTH,j * Map.TILE_WIDTH));
				}
			}
		}
	}
	
	// Method that updates the game world
	public void update(double elapsedTime, Point cursor) {

		// Update in-game timer
		gameTime.update();
		
		// Update timer for footstep sfx
		if(footstepTimer != null) {
			footstepTimer.update();
			if(player.isSneaking || player.isHiding) {
				if(footstepTimer.getElapsedTime() / 1000 > 0.6) {
					new SoundEffect("footstepquiet.wav");
					footstepTimer = null;
				}
			} else if(footstepTimer.getElapsedTime() / 1000 > 0.4) {
				new SoundEffect("footstep.wav");
				footstepTimer = null;
			}
		}
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// Consider input events
		if(inputArr[0]) { // W
			player.ySpeed = -1;
			player.isMoving = true;
			if(footstepTimer == null) {
				footstepTimer = new Clock2();
			}
		} else if(inputArr[2]) {  // S
			player.ySpeed = 1;
			player.isMoving = true;
			if(footstepTimer == null) {
				footstepTimer = new Clock2();
			}
		} else {
			player.ySpeed = 0;
			player.isMoving = false;
		}
		
		if(inputArr[1]) {  // A
			player.xSpeed = -1;
			player.isMoving = true;
			if(footstepTimer == null) {
				footstepTimer = new Clock2();
			}
			
		} else if(inputArr[3]) {  // D
			player.xSpeed = 1;
			player.isMoving = true;
			if(footstepTimer == null) {
				footstepTimer = new Clock2();
			}
		} else {
			player.xSpeed = 0;
			player.isMoving = false;
		}
		if(player.xSpeed == 0 && player.ySpeed == 0) {
			footstepTimer = null;
		}
		
		if(inputArr[4]) {  // SHIFT
			player.sneak();
		} else {
			player.unSneak();
		}
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				
		//Update line from player to cursor
		cursorLine.setLine((int)(player.realX + Character.CHARC_WIDTH/2),
				(int)(player.realY + Character.CHARC_WIDTH/2),
				cursor.x + player.realX - GameFrame.WINDOW_WIDTH/2 + Character.CHARC_WIDTH/2,
				cursor.y + player.realY - GameFrame.WINDOW_HEIGHT/2 + Character.CHARC_WIDTH/2);
		cursorAngle = Math.atan2(cursor.y - GameFrame.WINDOW_HEIGHT/2,
				cursor.x - GameFrame.WINDOW_WIDTH/2);
				
		// Update timer for the tile being interacted with
		if(currentInteractingTile != null) {
			if(currentInteractingTile.updateInteractionClock()) {
				tileInteraction();
			} 
		}
		if(currentInteractingTile != null) {
			if(getDistance(currentInteractingTile) > INTERACTION_RADIUS) {
				endAction();
			}
		}
		
		// Special checks for bushes/hidingspots: if the player exits, then make the bush collidable again
		if(currentBush != null) {
			if(getDistance(currentBush) <= Map.TILE_WIDTH) {
				currentBush.playerEnter(player);
				player.speedFactor = 100;
				player.isHiding = true;
			} else {
				if(currentBush.playerInside && !player.getRect().intersects(currentBush.getRect())) {
					new SoundEffect(currentBush.type + "exit.wav");
					currentBush.playerExit(player);
					player.isHiding = false;
					currentBush = null;
				}
			}
		}
		
		// Check for bullet collision with enemies
		for(int i = 0; i < bulletList.size(); i++) {
			if(checkEnemyCollision(bulletList.get(i)) != null) {
				new SoundEffect("enemydeath.wav");
				checkEnemyCollision(bulletList.get(i)).kill();
				bulletList.remove(i);
				i--;
			// Check bullet collision with walls
			} else if(checkMapObjCollision(bulletList.get(i))) {
				new SoundEffect("bulletimpact.wav");
				bulletList.remove(i);
				i--;
			}
		}

		// Move the player
		player.updatePrevCoords();
		player.moveX(elapsedTime);
		if(checkMapObjCollision(player)) {
			player.moveBack("x");
		}
		player.moveY(elapsedTime);
		if(checkMapObjCollision(player)) {
			player.moveBack("y");
		}
		
		// Update bullets
		for(Bullet bullet : bulletList) {
			bullet.move(elapsedTime);
		}
		
		// Update Enemies
		for(Enemy ent : enemyList) {
			ent.update(map, player, elapsedTime);
			ent.updatePrevCoords();
			ent.moveX(elapsedTime);
			ent.moveY(elapsedTime);
			// Check for collision
			if(ent.alerted && ent.collidingWithPlayer(player)) {
				gameOver = true;
			}
		}
		
		// Collision with small coins
		for(int i = 0; i < map.Y_TILES; i++) {
			for(int j = 0; j < map.X_TILES; j++) {
				if(map.getTile(i, j) instanceof Coin && map.getTile(i,j).getRect().intersects(player.getRect())) {
					new SoundEffect("coinpickup.wav");
					player.score += ((Coin)(map.getTile(i, j))).points;
					map.deleteTile(i,j);
				}
			}
		}
	}
	// ~~~ UPDATE METHOD END ~~~~
	
	public void shoot() {
		if(player.currentWeapon != null) {
			int originX = (int)(player.realX + Character.CHARC_WIDTH/2);
			int originY = (int)(player.realY + Character.CHARC_WIDTH/2);
			Weapon weap = player.currentWeapon;
			
			if(weap.getType().equals("pistol")) {
				new SoundEffect(("gunshot1.wav"));
				bulletList.add(new Bullet(originX, originY, cursorAngle, 2000));
				
				for(Enemy enemy : enemyList) {  // Make all enemies within a radius suspicious
					if(Enemy.getLineDistance(enemy.lineToPlayer) < 350) {
						enemy.makeSuspicious(player);
					}
				}
			}
			else if(weap.getType().equals("shotgun")) {
				new SoundEffect(("gunshot2.wav"));
				bulletList.add(new Bullet(originX, originY, cursorAngle + Math.toRadians(5), 1800));
				bulletList.add(new Bullet(originX, originY, cursorAngle + Math.toRadians(9), 2050));
				bulletList.add(new Bullet(originX, originY, cursorAngle + Math.toRadians(-4), 1940));
				bulletList.add(new Bullet(originX, originY, cursorAngle + Math.toRadians(-7), 2100));
				bulletList.add(new Bullet(originX, originY, cursorAngle + Math.toRadians(11), 2100));
				
				for(Enemy enemy : enemyList) {  // Make all enemies within a radius suspicious
					if(Enemy.getLineDistance(enemy.lineToPlayer) < 420) {
						enemy.makeSuspicious(player);
					}
				}
			} 
			else if(weap.getType().equals("tranquilizer")) {  // Tranquilizer is silent and won't alert enemies
				new SoundEffect(("gunshot3.wav"));
				bulletList.add(new Bullet(originX, originY, cursorAngle, 2000));
			}
			weap.useAmmo();
			if(weap.getAmmo() == 0) {
				 player.currentWeapon = null;
			}
		}
	}

	// Method that is called if a MapObject's interaction is proc'ed
	public void tileInteraction() {
		if(currentInteractingTile instanceof LargeCoin) {
			new SoundEffect("coinpickup.wav");
			player.score += ((LargeCoin)(currentInteractingTile)).points;
			map.deleteTile(currentInteractingTile);
		}
		else if(currentInteractingTile instanceof Safe && !((Safe)(currentInteractingTile)).opened) {
			new SoundEffect("coinpickup.wav");
			player.score += ((Safe)(currentInteractingTile)).points;
			((Safe)(currentInteractingTile)).unlock();
		}
		else if(currentInteractingTile instanceof Door && ((Door)(currentInteractingTile)).locked) {
			((Door)(currentInteractingTile)).unlock();
		}
		else if(currentInteractingTile instanceof Weapon) {
			new SoundEffect("equipgun.wav");
			player.currentWeapon = (Weapon)(currentInteractingTile);
			map.deleteTile(currentInteractingTile);
		}
		else if(currentInteractingTile instanceof HidingSpot) {
			currentBush = (HidingSpot)(currentInteractingTile);
		}
		else if(currentInteractingTile instanceof Exit) {
			exitReached = true;
			gameOver = true;
		}
		currentInteractingTile = null;
	}
	
	// Check for a worldObject's collision with MapObjects
	public boolean checkMapObjCollision(WorldObject worldObj) {
		for(int i = 0; i < map.X_TILES; i++) {
			for(int j = 0; j < map.X_TILES; j++) {
				if(map.getTile(i,j).isCollidable()) {
					if(map.getTile(i,j).getRect().intersects(worldObj.getRect())) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	// Check for a WorldObject's collision with an enemy
	public Enemy checkEnemyCollision(WorldObject worldObj) {
		for(Enemy enemy : enemyList) {
			if(worldObj.getRect().intersects(enemy.getRect())) {
				return enemy;
			}
		}
		return null;
	}

	// Returns the amount of obstructions between cursor and player
	public int checkCursorLineOfSight() {
		int count = 0;
		for(int i = 0; i < map.Y_TILES; i++) {
			for(int j = 0; j < map.X_TILES; j++) {
				if(map.getTile(i,j).isCollidable() && cursorLine.intersects(map.getTile(i,j).getRect())){
					count++;
				}
			}
		}
		return count;
	}
	
	// Returns the distance between the player and a given MapObject (from center to center)
	public double getDistance(MapObject tile) {
		return Math.sqrt(
				Math.pow((tile.realX + Map.TILE_WIDTH/2) - (player.realX + Character.CHARC_WIDTH/2), 2) +
				Math.pow((tile.realY + Map.TILE_WIDTH/2) - (player.realY + Character.CHARC_WIDTH/2), 2));
	}
	
	// Check whether the mouse is on a valid tile, and start the interaction
	public void checkAction(Point cursor) {
		if(!player.isDead && !player.isHiding) {
			for(int i = 0; i < map.Y_TILES; i++) {
				for(int j = 0; j < map.X_TILES; j++) {
					
					MapObject tile = map.getTile(i,j);
					Rectangle drawnRect = new Rectangle((int)(tile.getDrawnX(player)),
							(int)(tile.getDrawnY(player)), Map.TILE_WIDTH, Map.TILE_WIDTH);
					if(tile.interactable && drawnRect.contains(cursor)) {
						if((tile.isCollidable() && checkCursorLineOfSight() == 1)
								|| (!tile.isCollidable() && checkCursorLineOfSight() == 0)) {
							if(tile != currentInteractingTile) {
								endAction();
								currentInteractingTile = tile;
								currentInteractingTile.startInteraction();

								//Play audio cues
								if(tile instanceof Door || tile instanceof Safe) {
									interactionSfx = new SoundEffect("unlock.wav");
								} else if(tile instanceof Weapon) {
									interactionSfx = new SoundEffect("guninteract.wav");
								} else if(tile instanceof HidingSpot) {
									if(((HidingSpot)(tile)).type.equals("bush")) {
										interactionSfx = new SoundEffect("bushinteract.wav");
									} else if(((HidingSpot)(tile)).type.equals("vent")) {
										interactionSfx = new SoundEffect("ventinteract.wav");
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	// End the interaction of the current interacting tile
	public void endAction() {
		if(currentInteractingTile != null) {
			currentInteractingTile.endInteraction();
			if(interactionSfx != null) {
				interactionSfx.endSound();
			}
		}
		interactionSfx = null;
		currentInteractingTile = null;
	}
	
	public void draw(Graphics g, JPanel panel) {
		map.drawSprites(g, player);	
		
		for(Bullet bullet : bulletList) {
			bullet.draw(g,player);
		}
		for(Enemy ent : enemyList) {
			ent.draw(g, player);
			ent.drawSprite(g, player);
		}
		player.drawSprite(g, panel, cursorAngle);
		
		if(gameOver) {
			g.setColor(new Color(0,0,0,200));
			g.fillRect(100, 100, 1080, 520);
			g.setFont(new Font("Gill Sans MT", Font.PLAIN, 70));
			g.setColor(Color.WHITE);
			if(exitReached) {
				g.drawString("EXIT REACHED", GameFrame.WINDOW_WIDTH/2 - 250, GameFrame.WINDOW_HEIGHT/2);
			} else {
				g.drawString("GAME OVER", GameFrame.WINDOW_WIDTH/2 - 200, GameFrame.WINDOW_HEIGHT/2);
			}
			g.setFont(new Font("Gill Sans MT", Font.PLAIN, 30));
			g.drawString("SCORE: " + Integer.toString(player.score), GameFrame.WINDOW_WIDTH/2 - 70, 
					GameFrame.WINDOW_HEIGHT/2 + 60);
		}
	}
}
