import java.awt.*;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;

public class Player extends Character {
	
	public Weapon currentWeapon;
	public boolean isHiding;
	public boolean isSneaking;
	public boolean isMoving;
	public boolean isDead;
	public int speedFactor;
	public int score;
	private double rotation;
	
	public Player(Map map) {
		super(map.playerSpawnX + 10, map.playerSpawnY + 10, CHARC_WIDTH);
		setSprite("player.png");
		isHiding = false;
		isDead = false;
		currentWeapon = null;
		speedFactor = 220;
		score = 0;
	}

	public void moveX(double elapsedTime) {
		realX = realX + xSpeed * elapsedTime * speedFactor;
		getRect().x = (int)realX;
	}
	
	public void moveY(double elapsedTime) {
		realY = realY + ySpeed * elapsedTime * speedFactor;
		getRect().y = (int)realY;
	}
	
	public void moveBack(String dir) {
		if(dir.equals("y")) {
			realY = prevY;
			getRect().y = (int)realY;
		} else if(dir.equals("x")) {
			realX = prevX;
			getRect().x = (int)realX;
		}
	}
	
	public void draw(Graphics g) {
		g.setColor(Color.GREEN);
		g.fillRect(GameFrame.WINDOW_WIDTH/2 - CHARC_WIDTH/2,
				GameFrame.WINDOW_HEIGHT/2 - CHARC_WIDTH/2,	
				CHARC_WIDTH, CHARC_WIDTH);
	}
	
	public void drawSprite(Graphics g, JPanel panel, double cursorAngle) {
		rotation = cursorAngle;
		Graphics2D g2d = (Graphics2D) g.create();
		AffineTransform origXform = g2d.getTransform();
		AffineTransform newXform = (AffineTransform)(origXform.clone());
		newXform.rotate(rotation, GameFrame.WINDOW_WIDTH/2, GameFrame.WINDOW_HEIGHT/2);
		g2d.setTransform(newXform);
		int x = (GameFrame.WINDOW_WIDTH - getSprite().getWidth(panel))/2;
		int y = (GameFrame.WINDOW_HEIGHT - getSprite().getHeight(panel))/2;
		g2d.drawImage(getSprite(), x, y, panel);
		g2d.setTransform(origXform);
		g2d.dispose();
	}
	
	public void sneak() {
		isSneaking = true;
		speedFactor = 100;
	}
	
	public void unSneak() {
		isSneaking = false;
		speedFactor = 220;
	}
}
