
/*
 * Safe object
 */
public class Safe extends MapObject{
	
	public int points;
	public boolean opened;

	public Safe(int x, int y) {
		super(x, y, Map.TILE_WIDTH, true, 5);
		setCollidable(true);
		points = 20;
		opened = false;
		setSprite("safe_locked.png");
	}
	
	public void unlock() {
		opened = true;
		setSprite("safe_opened.png");
		interactable = false;
	}
}
