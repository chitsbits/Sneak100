
/*
 * Door object
 */
public class Door extends MapObject{
	
	public boolean locked;

	public Door(int x, int y) {
		super(x, y, Map.TILE_WIDTH, true, 2);
		setCollidable(true);
		locked = true;
		setSprite("door_locked.png");
	}
	
	public void unlock() {
		locked = false;
		setCollidable(false);
		interactable = false;
		setSprite("door_opened.png");
	}
}
