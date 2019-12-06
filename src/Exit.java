
public class Exit extends MapObject{

	public Exit(int x, int y) {
		super(x, y, Map.TILE_WIDTH, true, 3);
		setSprite("exit.png");
	}
}