
/*
 * Wall object
 */
public class Wall extends MapObject{
	
	private String type;

	public Wall(String t, int x, int y) {
		super(x, y, Map.TILE_WIDTH, false, 0);
		setCollidable(true);
		type = t;

		// Set sprite depending on adjacent tiles
		if(type.equals("vertical")) {
			setSpriteSheet("walls.png",0,0);
		} else if(type.equals("horizontal")) {
			setSpriteSheet("walls.png",0,1);
		} else if(type.equals("U1")) {
			setSpriteSheet("walls.png",0,2);
		} else if(type.equals("U2")) {
			setSpriteSheet("walls.png",0,3);
		} else if(type.equals("U3")) {
			setSpriteSheet("walls.png",1,0);
		} else if(type.equals("U4")) {
			setSpriteSheet("walls.png",1,1);
		} else if(type.equals("L1")) {
			setSpriteSheet("walls.png",1,2);
		} else if(type.equals("L2")) {
			setSpriteSheet("walls.png",1,3);
		} else if(type.equals("L3")) {
			setSpriteSheet("walls.png",2,0);
		} else if(type.equals("L4")) {
			setSpriteSheet("walls.png",2,1);
		} else if(type.equals("T1")) {
			setSpriteSheet("walls.png",2,2);
		} else if(type.equals("T2")) {
			setSpriteSheet("walls.png",2,3);
		} else if(type.equals("T3")) {
			setSpriteSheet("walls.png",3,0);
		} else if(type.equals("T4")) {
			setSpriteSheet("walls.png",3,1);
		} else if(type.equals("X")) {
			setSpriteSheet("walls.png",3,2);
		} else if(type.equals("O")) {
			setSpriteSheet("walls.png",3,3);
		}
	}
}
