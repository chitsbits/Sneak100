
/*
 * Background object
 */
public class Background extends MapObject {

	public Background(int x, int y) {
		super(x, y, Map.TILE_WIDTH, false, 0);
		setCollidable(false);
		setSprite("floor.png");
	}

}
