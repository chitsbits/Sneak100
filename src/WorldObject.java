import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

/*
 * WorldObject abstract class, defines all tangible objects in the game world
 */
public abstract class WorldObject {
	
	public double realX, realY;  // Real x and y coordinate (as opposed to the x,y for drawing)
	private Rectangle boundingBox;  // Collision box
	public boolean collidable;
	private int width;
	private BufferedImage sprite;

	public WorldObject(int x, int y, int width) {
		this.realX = x;
		this.realY = y;
		this.width = width;
		boundingBox = new Rectangle(x, y, width+1, width+1);
	}
	
	/*
	 * Default draw (used by objects without sprites, i.e. background)
	 */
	public void draw(Graphics g, Player p) {
		g.fillRect((int)(getDrawnX(p)),
				(int)(getDrawnY(p)),
				width, width);
	}
	
	/*
	 *  Draws sprite to the graphics object
	 *  @param g  graphics object to draw to
	 *  @param p  player being relatively drawn to
	 */
	public void drawSprite(Graphics g, Player p) {
		// Only draw sprites that are visible on the screen
		if(Math.abs(this.realX - p.realX) < GameFrame.WINDOW_WIDTH/2 + Map.TILE_WIDTH &&
				Math.abs(this.realY - p.realY) < GameFrame.WINDOW_HEIGHT/2 + Map.TILE_WIDTH) {
			g.drawImage(sprite, getDrawnX(p), getDrawnY(p), null);
		}
	}

	/* 
	 * Load sprite
	 * @param filename  filename of the image, including file extension
	 */
	public void setSprite(String filename) {
		try {
			sprite = ImageIO.read(new File("assets/sprites/" + filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("error loading sprite: " + filename);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("error loading sprite: " + filename);		
		}
	}

	/*
	 * Load a subimage from a spritesheet
	 * @param filename  spritesheet filename
	 * @param row  row to access
	 * @param col  col to access
	 */
	public void setSpriteSheet(String filename, int row, int col) {
		setSprite(filename);
		sprite = ((BufferedImage)(sprite)).getSubimage(col * Map.TILE_WIDTH, row * Map.TILE_WIDTH, Map.TILE_WIDTH, Map.TILE_WIDTH);
	}

	/*
	 *  @return the X position of the image to be drawn in the game (centered around player)
	 */
	public int getDrawnX(Player p) {
		return (int)(realX - p.realX + GameFrame.WINDOW_WIDTH/2 - Character.CHARC_WIDTH/2);
	}
	
	/*
	 *  @return  the Y position of the image to be drawn in the game (centered around player)
	 */
	public int getDrawnY(Player p) {
		return (int)(realY - p.realY + GameFrame.WINDOW_HEIGHT/2 - Character.CHARC_WIDTH/2);
	}
	
	public boolean isCollidable() {
		return collidable;
	}
	
	public BufferedImage getSprite() {
		return sprite;
	}
	
	public Rectangle getRect() {
		return boundingBox;
	}
	
	public void setCollidable(boolean set) {
		collidable = set;
	}

}
