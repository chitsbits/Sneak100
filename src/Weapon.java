
/*
 * Weapon object
 */
public class Weapon extends MapObject{
	
	private String type;
	private int ammo;

	public Weapon(String t, int x, int y) {
		super(x, y, Map.TILE_WIDTH, true, 2.3);
		type = t;
		if(type.equals("pistol")) {
			ammo = 3;
		} else if(type.equals("shotgun")) {
			ammo = 2;
		} else if(type.equals("tranquilizer")) {
			ammo = 2;
		}
		if(type.equals("pistol")) {
			setSprite("pistol.png");
		} else if(type.equals("shotgun")) {
			setSprite("shotgun.png");
		} else if(type.equals("tranquilizer")) {
			setSprite("tranquilizer.png");
		}
	}
	
	public void useAmmo() {
		ammo -= 1;
	}
	
	public String getType() {
		return type;
	}
	
	public int getAmmo() {
		return ammo;
	}
}
