import java.awt.Graphics;

/*
 * Character abstract class, extended by Player and Enemy
 */
public abstract class Character extends WorldObject{
	
	public double prevX, prevY;
	public double xSpeed, ySpeed;
	public static final int CHARC_WIDTH = 40;

	public Character(int x, int y, int width) {
		super(x, y, width);
	}
	
	// Draw method
	public void draw(Graphics g, Player p) {
		g.fillRect((int)(getDrawnX(p)) + 3,
				(int)(getDrawnY(p)) + 3,
				CHARC_WIDTH-7, CHARC_WIDTH-7);
	}
	
	public void updatePrevCoords() {
		prevX = realX;
		prevY = realY;
	}	
}
