
/*
 * LargeCoin object
 */
public class LargeCoin extends MapObject{
	
	public int points;

	public LargeCoin(int x, int y) {
		super(x, y, Map.TILE_WIDTH, true, 1);
		points = 10;
		setSprite("largecoin.png");
	}
}
