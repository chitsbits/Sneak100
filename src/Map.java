
import java.awt.*;
import java.io.*; 
import java.util.Scanner;

// Class representing the game world.
// Contains the tile grid map.
class Map {
	
	public final int X_TILES;  // Number of tiles
	public final int Y_TILES;
	public final int WIDTH;
	public final int HEIGHT;
	
	public static final int TILE_WIDTH = 60;
	
	private MapObject[][] mapArr;
	private File mapFile;
	private Scanner fileScanner;
	
	public int playerSpawnX, playerSpawnY;
	
	public Map(int mapNum) {
		
		loadMap(mapNum);  // Load Map 
		
		// First 2 lines of file will give width and height
		X_TILES = fileScanner.nextInt();
		Y_TILES = fileScanner.nextInt();
		WIDTH = X_TILES * TILE_WIDTH;
		HEIGHT = Y_TILES * TILE_WIDTH;
		mapArr = new MapObject[Y_TILES][X_TILES];
		
		// First loop, load all elements except for walls
		for(int i = 0; i < Y_TILES; i++) {
			for(int j = 0; j < X_TILES; j++) {
				String current = fileScanner.next();
				
				// Replace this with switch statement
				
				if(current.equals("#")) {
					mapArr[i][j] = new Wall("X",j * TILE_WIDTH,i * TILE_WIDTH);
				}else if(current.equals("o")) {
					mapArr[i][j] = new Coin(j * TILE_WIDTH,i * TILE_WIDTH);
				}else if(current.equals("&")) {
					mapArr[i][j] = new LargeCoin(j * TILE_WIDTH,i * TILE_WIDTH);
				}else if(current.equals("$")) {
					mapArr[i][j] = new Safe(j * TILE_WIDTH,i * TILE_WIDTH);
				}else if(current.equals("B")){
					mapArr[i][j] = new HidingSpot("bush", j * TILE_WIDTH,i * TILE_WIDTH);
				}else if(current.equals("V")){
					mapArr[i][j] = new HidingSpot("vent", j * TILE_WIDTH,i * TILE_WIDTH);
				}else if(current.equals("D")) {
					mapArr[i][j] = new Door(j * TILE_WIDTH,i * TILE_WIDTH);	
				}else if(current.equals(".")) {
					mapArr[i][j] = new Background(j * TILE_WIDTH,i * TILE_WIDTH);
				}else if(current.equals("T")) {
					mapArr[i][j] = new Weapon("tranquilizer",j * TILE_WIDTH,i * TILE_WIDTH);
				}else if(current.equals("S")) {
					mapArr[i][j] = new Weapon("shotgun",j * TILE_WIDTH,i * TILE_WIDTH);
				}else if(current.equals("P")) {
					mapArr[i][j] = new Weapon("pistol",j * TILE_WIDTH,i * TILE_WIDTH);
				}else if(current.equals("+")) {
					mapArr[i][j] = new Exit(j * TILE_WIDTH,i * TILE_WIDTH);
				}else if(current.equals("E")) {
					mapArr[i][j] = new Background(j * TILE_WIDTH, i * TILE_WIDTH);
					mapArr[i][j].isEnemySpawnPoint = true;
				}else if(current.equals("@")) {
					mapArr[i][j] = new Background(j * TILE_WIDTH, i * TILE_WIDTH);
					playerSpawnX = j * TILE_WIDTH;
					playerSpawnY = i * TILE_WIDTH;
				}
			}
		}
		
		// Second loop: update walls to use proper sprite
		loadMap(mapNum);
		fileScanner.nextInt();
		fileScanner.nextInt();
		for(int i = 0; i < Y_TILES; i++) {
			for(int j = 0; j < X_TILES; j++) {
				String current = fileScanner.next();
				if(current.equals("#")) {
					MapObject[] adjacent = new MapObject[4];
					if(i > 0) {  // Up
						adjacent[0] = mapArr[i-1][j];
					} if(j < X_TILES-1) {  // Right
						adjacent[1] = mapArr[i][j+1];
					} if(i < Y_TILES-1) {  // Down
						adjacent[2] = mapArr[i+1][j];
					} if(j > 0) {  // Left
						adjacent[3] = mapArr[i][j-1];
					}
					
					if(adjacent[0] instanceof Wall && adjacent[1] instanceof Wall && adjacent[2] instanceof Wall && adjacent[3] instanceof Wall) {
						mapArr[i][j] = new Wall("X", j * TILE_WIDTH, i * TILE_WIDTH);
					} // T shapes
					else if(adjacent[0] instanceof Wall && adjacent[1] instanceof Wall && adjacent[2] instanceof Wall) {
						mapArr[i][j] = new Wall("T3", j * TILE_WIDTH, i * TILE_WIDTH);
					} else if(adjacent[0] instanceof Wall && adjacent[1] instanceof Wall && adjacent[3] instanceof Wall) {
						mapArr[i][j] = new Wall("T4", j * TILE_WIDTH, i * TILE_WIDTH);
					} else if(adjacent[0] instanceof Wall && adjacent[2] instanceof Wall && adjacent[3] instanceof Wall) {
						mapArr[i][j] = new Wall("T1", j * TILE_WIDTH, i * TILE_WIDTH);
					} else if(adjacent[2] instanceof Wall && adjacent[1] instanceof Wall && adjacent[3] instanceof Wall) {
						mapArr[i][j] = new Wall("T2", j * TILE_WIDTH, i * TILE_WIDTH);
					} // L shapes
					else if(adjacent[0] instanceof Wall && adjacent[1] instanceof Wall) {
						mapArr[i][j] = new Wall("L4", j * TILE_WIDTH, i * TILE_WIDTH);
					} else if(adjacent[2] instanceof Wall && adjacent[1] instanceof Wall) {
						mapArr[i][j] = new Wall("L3", j * TILE_WIDTH, i * TILE_WIDTH);
					} else if(adjacent[2] instanceof Wall && adjacent[3] instanceof Wall) {
						mapArr[i][j] = new Wall("L2", j * TILE_WIDTH, i * TILE_WIDTH);
					} else if(adjacent[0] instanceof Wall && adjacent[3] instanceof Wall) {
						mapArr[i][j] = new Wall("L1", j * TILE_WIDTH, i * TILE_WIDTH);
					}
					else if(adjacent[0] instanceof Wall && adjacent[2] instanceof Wall) {
						mapArr[i][j] = new Wall("vertical", j * TILE_WIDTH, i * TILE_WIDTH);
					} else if(adjacent[1] instanceof Wall && adjacent[3] instanceof Wall) {
						mapArr[i][j] = new Wall("horizontal", j * TILE_WIDTH, i * TILE_WIDTH);
					}
					else if(adjacent[0] instanceof Wall) {
						mapArr[i][j] = new Wall("U1", j * TILE_WIDTH, i * TILE_WIDTH);
					} else if(adjacent[1] instanceof Wall) {
						mapArr[i][j] = new Wall("U4", j * TILE_WIDTH, i * TILE_WIDTH);
					} else if(adjacent[2] instanceof Wall) {
						mapArr[i][j] = new Wall("U3", j * TILE_WIDTH, i * TILE_WIDTH);
					} else if(adjacent[3] instanceof Wall) {
						mapArr[i][j] = new Wall("U2", j * TILE_WIDTH, i * TILE_WIDTH);
					}
					else {
						mapArr[i][j] = new Wall("O", j * TILE_WIDTH, i * TILE_WIDTH);
					}
				}
			}
		}
		
	}
	
	// Load map from assets folder
	private void loadMap(int mapNum) {
		
		try {
			mapFile = new File("assets/map" + mapNum + ".txt");
			fileScanner = new Scanner(mapFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void drawSprites(Graphics g, Player p) {
		for(int i = 0; i < Y_TILES; i++) {
			for(int j = 0; j < X_TILES; j++) {
				mapArr[i][j].drawSprite(g, p);

			}
		}		
	}
	
	public void deleteTile(int row, int col) {
		mapArr[row][col] = new Background(col * TILE_WIDTH, row * TILE_WIDTH);
	}
	
	public void deleteTile(MapObject toDelete) {
		for(int i = 0; i < Y_TILES; i++) {
			for(int j = 0; j < X_TILES; j++) {
				if(mapArr[i][j] == toDelete) {
					mapArr[i][j] = new Background(j * TILE_WIDTH, i * TILE_WIDTH);
				}
			}
		}
	}
	
	public MapObject getTile(int row, int col) {
		return mapArr[row][col];
	}
	
	public MapObject[][] getMapArr() {
		return mapArr;
	}
}
