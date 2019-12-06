
/*
 * Coin object
 */
public class Coin extends MapObject{
	
	public int points;

	public Coin(int x, int y) {
		super(x, y, Map.TILE_WIDTH, false, 1);
		points = 5;
		setSprite("smallcoin.png");
	}
}
