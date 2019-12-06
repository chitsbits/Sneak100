
/*
 * Hiding spot object
 */
public class HidingSpot extends MapObject {
	
	public boolean playerInside;
	public String type;
	
	public HidingSpot(String t, int x, int y) {
		super(x, y, Map.TILE_WIDTH, true, 0.5);
		type = t;
		setSprite(type + ".png");
		setCollidable(true);
	}
	
	public void playerEnter(Player p) {
		playerInside = true;
		setCollidable(false);
	}
	
	public void playerExit(Player p) {
		playerInside = false;
		setCollidable(true);
	}

}
