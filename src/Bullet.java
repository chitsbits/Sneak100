import java.awt.Color;
import java.awt.Graphics;

/*
 * Bullet object
 */
public class Bullet extends WorldObject{
	
	private int velocity;
	private double angle;

	public Bullet(int x, int y, double a, int v) {
		super(x, y, 5);
		velocity = v;
		angle = a;
	}
	
	public void move(double elapsedTime) {
		realX = realX + elapsedTime * velocity * Math.cos(angle);
		getRect().x = (int) realX;
		realY = realY + elapsedTime * velocity * Math.sin(angle);
		getRect().y = (int) realY;
	}
	
	@Override
	public void draw(Graphics g, Player p) {
		g.setColor(Color.YELLOW);
		super.draw(g, p);
	}
}
