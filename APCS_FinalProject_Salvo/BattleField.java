import java.util.*;
import java.awt.Color;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;


import info.gridworld.grid.*;
import info.gridworld.world.World;
  
/**
 * Implements the Salvo version of Battleship. BattleField is a grid world of tiles,  
 * and contains 2 players: a user and an AI player, and each player has a fleet of 
 * 5 ships. At the start of the game, ships of each fleet are automatically and 
 * randomly placed. AI's ships are invisible, and user's ship are visible. 
 * Before the game starts, the user has the opportunity to re-arrange his/her 
 * ship positions by clicking on a ship, and and using arrow (UP, DOWN, LEFT, RIGHT)  
 * keys to move it. A game is started by clicking on the Step button. The Run button 
 * should not be clicked at any time. After game starts, each player will take turn  
 * to choose 5 targets to fire. User chooses firing targets by clicking on upper 
 * 10 by 10 grid. The game ends when a player sinks all opponent's ships.  
 * 
 * @author  Eric Fu
 * @author  Rohit Sriram
 * @version 5/24/2017
 * @author  Period - 3
 * @author  Assignment - APCS Final Project Salvo
 *
 * @author Sources - N/A
 */ 
public class BattleField extends World<Tile>
{
	/**
	 * Constant member field: number of rows on each player's side
	 */
	public static final int NUM_ROWS = 10;

	/**
	 * Constant member field: number of columns on each player's side
	 */
	public static final int NUM_COLS = 10;
	
	/**
	 * Constant member field: number of black rows used as boundary between upper and
	 * lower grid
	 */	
	public static final int NUM_BOUNDARY_ROWS = 2;
	
	/**
	 * Member field: 2D array to store 2 fleet of ships for 2 players 
	 */
	private Ship[][] fleet = new Ship[2][Ship.NUM_SHIPS];
	

	/**
	 * Constant member field: initial game phase
	 */
	public static final int GAME_PHASE_INIT    = 0;

	/**
	 * Constant member field: running game phase
	 */
	public static final int GAME_PHASE_RUNNING = 1;

	/**
	 * Constant member field: ended game phase
	 */
	public static final int GAME_PHASE_ENDED   = 2;
	
	/**
	 * Member field: game phase
	 */
	private int gamePhase = GAME_PHASE_INIT;
	
	/**
	 * Member field: current turn (i.e., which player)
	 */
	private int turn;
	
    /**
     * Member field: an instance of Random object
     */
	private Random random;
	
    /**
     * Member field: user player
     */
	private UserShooter user;
	
    /**
     * Member field: AI player
     */
	private AIShooter   ai;
	
	/**
	 * Constructs a BattleField.
	 */
	public BattleField()
	{
		super(new BoundedGrid<Tile>(NUM_ROWS * 2 + NUM_BOUNDARY_ROWS, NUM_COLS));
		
		random = new Random(new Date().getTime());
		turn = random.nextInt() % 2; //Player.USER; 
		
		user = new UserShooter(Player.DEFAULT_NUM_SHOTS);
		ai   = new AIShooter(Player.DEFAULT_NUM_SHOTS);
		
		gamePhase = GAME_PHASE_INIT; 
		
		// populate boundary rows between target grid above and ocean grid below
		populateBoundaryRows();
				
		populateFleet();
		
		showUserFleet();  // show user's fleet in ocean grid below
		
		this.setMessage("The first turn is " + 
		                (turn == Player.USER ? "yours" : "AI's") + "." ); 
	}
	
	/**
	 * Gets phase of the game.
	 * 
	 * @return phase of the game
	 */
	public int getGamePhase()
	{
		return gamePhase;
	}
	
	/**
	 * Performs one step. This method is called when the user clicks on the 
	 * step button, or when run mode has been activated by clicking the run button. 
	 * 
	 */
	@Override
	public void step() 
	{	// Step or Run button clicked
		if (gamePhase > GAME_PHASE_RUNNING)
		{
			return;
		}
		
		gamePhase = GAME_PHASE_RUNNING;
		
		Player player = turn == Player.USER ? user : ai;
		
		player.chooseFiringTargets();
		
		if (!player.isFiringTargetsReady())
		{
			try 
			{   // wait for player to click/select targets
				Thread.yield();  // not working as intended here, so don't click on 
				                 // Run button. Use Step button only
				Thread.sleep(3000); // in ms
				return;
			}
			catch(Exception e)
			{
				// just ignore it
			}
		}
		else // player has enough firing targets
		{
			List<Location> targets = player.chooseFiringTargets();
			
			List<Location> hitList = new ArrayList<Location>();
			List<Integer> shipTypes = new ArrayList<Integer>();
			
			int opponent = (turn + 1) % 2;
			
			for (Location target : targets)
			{
				int row = target.getRow();
				int col = target.getCol();
				
				for (int shipNo = 0; shipNo < Ship.NUM_SHIPS; shipNo++)
				{
					if (fleet[opponent][shipNo] != null) 
					{
						if (fleet[opponent][shipNo].occupies(row, col))
						{
							fleet[opponent][shipNo].takeHitAt(row, col);
							hitList.add(target);
							shipTypes.add(fleet[opponent][shipNo].getType());
						}
					}
				}
			}
			
			player.observeHits(hitList, shipTypes);
		    refreshGrids();
		    
			boolean won = isWholeFleetSunken(opponent);
			if (won)
			{
				this.setMessage((turn == Player.USER ? "You" : "AI") + " won!" ); 
				
				gamePhase = GAME_PHASE_ENDED;
			}
			else 
			{
				turn = (turn + 1) % 2; //Player.USER; //(turn + 1) % 2;
				
				this.setMessage("It's " + (turn == Player.USER ? "your" : "AI's") + 
								" turn." ); 
				
				if (turn == Player.AI)
				{
					step(); // AI can execute the next step immediately
				}
			}
		}
	}
	
	/**
	 * Acts when a location is clicked. This method is called when the user clicks 
	 * on a location in the WorldFrame. 
	 * 
	 * @param loc  the grid location that the user selected
	 * 
	 * @return true if the world consumes the click, or false if the GUI should 
	 *         invoke the Location->Edit menu action
	 */
	@Override
	public boolean locationClicked(Location loc)
	{
		boolean clickConsumed = true; // always true to avoid built-in behavior
		
		if (gamePhase == GAME_PHASE_INIT)
		{
			int selectedShip = getSelectedUserShip(loc);
			//clickConsumed = selectedShip >= 0;
		}
		else if (gamePhase == GAME_PHASE_RUNNING)
		{
			int col = loc.getCol();
			int row = loc.getRow();
			
			if (turn == Player.USER && row < NUM_ROWS) // clicked target grid
			{
				if (!user.getMissList().contains(loc) && 
				    !user.getShotList().contains(loc) &&
				    !user.isFiringTargetsReady())
				{
					user.addFiringTarget(loc);
					refreshUserTargtGrid();
				}
				
				if (user.isFiringTargetsReady())
				{
					step();
				}
				
			}
		}
		
		return clickConsumed;
	}

	/**
	 * Acts when a key was pressed. 
	 * 
	 * @param description the string describing the key
	 * @param loc the selected location in the grid at the time the key was pressed 
	 * 
	 * @return true if the world consumes the key press, false if the GUI should 
	 *         consume it.
	 */
	@Override
	public boolean keyPressed(String description, Location loc)
	{
		int selectedShip = getSelectedUserShip(loc);
		
		KeyStroke key = KeyStroke.getKeyStroke(description);
		int keyCode = key.getKeyCode();
		
		if (selectedShip >= 0 && 
			(keyCode == KeyEvent.VK_UP   ||
			 keyCode == KeyEvent.VK_DOWN ||
			 keyCode == KeyEvent.VK_LEFT ||
			 keyCode == KeyEvent.VK_RIGHT ) ) {

			tryMoveSelectedShip(selectedShip, keyCode);
		}
		
		return false;
	}
	
	/**
	 * Tries to move the currently selected ship.
	 * 
	 * @param selectedShip ship number of the selected ship
	 * @param keyCode  key code
	 * 
	 * @return true if the ship is moved, false otherwise
	 */
	private boolean tryMoveSelectedShip(int selectedShip, int keyCode)
	{
		boolean moved = false;
		
		int row = fleet[Player.USER][selectedShip].getHeadRow();
		int col = fleet[Player.USER][selectedShip].getHeadCol();
		
		switch (keyCode)
		{
			case KeyEvent.VK_UP:   row--; break;
			case KeyEvent.VK_DOWN: row++; break;
			case KeyEvent.VK_LEFT: col--; break;
			default:               col++; break;
		}
		
		if (isLocationGoodForShip(Player.USER, 
						fleet[Player.USER][selectedShip].getLength(), 
				        fleet[Player.USER][selectedShip].isHorizontal(), 
				        row, col, selectedShip))
		{
			fleet[Player.USER][selectedShip].moveTo(row, col);
			refreshUserFleetOnGrid();
			moved = true;
		}
		
		return moved;
	}
		
	/**
	 * Gets ship number of the user's ship at the specified location.
	 * 
	 * @param loc location
	 * 
	 * @return ship number of the user's ship at the specified location
	 */
	private int getSelectedUserShip(Location loc) 
	{
		return getShipLocatedAt(Player.USER, loc);
	}
	
	/**
	 * Gets ship number of the specified player side at the specified location.
	 * 
	 * @param side player side
	 * @param loc location
	 * 
	 * @return ship number of the specified player side at the specified location
	 */
	private int getShipLocatedAt(int side, Location loc) 
	{
		int selected = -1;
		int firstRowOffset = side==Player.USER ? (NUM_ROWS + NUM_BOUNDARY_ROWS) : 0;
		int col = loc.getCol();
		int row = loc.getRow() - firstRowOffset;
		
		for (int shipNo=0; row >= 0 && selected < 0 && shipNo < Ship.NUM_SHIPS; 
						                                                   shipNo++)
		{
			if (fleet[side][shipNo] != null) 
			{
				if (fleet[side][shipNo].occupies(row, col)) 
				{
					selected = shipNo;
				}
			}
		}
		
		return selected;
	}

	/**
	 * Creates a ship.
	 * 
	 * @param shipNum  ship number
	 * @param horizontal flag indicating if orientation of ship is horizontal
	 * @param row row number of the ship location
	 * @param col column number of the ship location
	 * 
	 * @return an instance of ship
	 */
	private Ship createShip(int shipNum, boolean horizontal, int row, int col)
	{
		switch(shipNum) {
			case Ship.CARRIER:  	return new Carrier   (horizontal, row, col);
			case Ship.BATTLESHIP:  	return new Battleship(horizontal, row, col);
			case Ship.CRUISER:  	return new Cruiser   (horizontal, row, col);
			case Ship.SUBMARINE:	return new Submarine (horizontal, row, col);
			default:            	return new Destroyer (horizontal, row, col);
		}
	}
	
	/**
	 * Automatically and randomly populates fleet of ships for each player.
	 */
	private void populateFleet() 
	{	
		for (int side = Player.USER; side <= Player.AI; side++) 
		{
			for (int shipNo = 0; shipNo < Ship.NUM_SHIPS; shipNo++)
			{
				int len = Ship.getShipLength(shipNo);
				boolean horizontal = random.nextBoolean();
				
				boolean placed = false;
				while (!placed) {
					int row = random.nextInt(NUM_ROWS);
					int col = random.nextInt(NUM_COLS);
					
					if (isLocationGoodForShip(side, len, horizontal, row, col, -1)) 
					{
						fleet[side][shipNo] = createShip(shipNo, horizontal, 
										                 row, col);
						placed = true;
					}	
				}
			}
		}
	}

	/**
	 * Checks if the specified location is good to place a ship.
	 * 
	 * @param side  player side
	 * @param shipLength length of the ship
	 * @param horizontal flag indicating if orientation of ship is horizontal
	 * @param row row number of the ship location
	 * @param col column number of the ship location
	 * @param skipShipNo ship number of the ship to skip when checking location 
	 *                   conflict. It's needed in the case of trying to move a ship.
	 * 
	 * @return true if the specified location is good to place a ship, 
	 *         false otherwise
	 */
	private boolean isLocationGoodForShip(int side, int shipLength, 
					      boolean horizontal, int row, int col, int skipShipNo) {
		boolean good = 0 <= row && row <= NUM_ROWS && 
					   0 <= col && col <= NUM_COLS;
		int r, c;
		
		for (int i=0; good && i < shipLength; i++) 
		{
			if (horizontal) 
			{
				r = row;
				c = col + i;
			}
			else
			{
				r = row + i;
				c = col;
			}
			
			if (r >= NUM_ROWS || c >= NUM_COLS) 
			{   // out of boundary
				good = false;
			}
			else // within boundary
			{	 // check if the position is already occupied
				good &= !isPositionOccupied(side, r, c, skipShipNo);
			}
		}
		
		return good;
	}
	
	/**
	 * Checks if the specified location is occupied.
	 *  
	 * @param side player side
	 * @param row row number of the ship location
	 * @param col column number of the ship location
	 * @param skipShipNo ship number of the ship to skip when checking location 
	 *                   conflict. It's needed in the case of trying to move a ship.
	 *                   
	 * @return true if the specified location is occupied, false otherwise
	 */
	private boolean isPositionOccupied(int side, int row, int col, int skipShipNo)
	{
		boolean occupied = false;
		
		for (int shipNo = 0; !occupied && shipNo < Ship.NUM_SHIPS; shipNo++)
		{
			if (shipNo == skipShipNo)
			{   // skip checking this ship when trying to move it
				continue;
			}
			else if (fleet[side][shipNo] != null) 
			{
				occupied = fleet[side][shipNo].occupies(row, col);
			}
		}
		
		return occupied;
	}
	
	/**
	 * Checks if the whole fleet of the specified side is sunken.
	 * 
	 * @param side player side
	 * 
	 * @return true if the whole fleet of the specified side is sunken,
	 *         false otherwise
	 */
	private boolean isWholeFleetSunken(int side) 
	{
		boolean allSunken = true;
		
		for (int shipNo = 0; allSunken && shipNo < Ship.NUM_SHIPS; shipNo++)
		{	
			allSunken &= fleet[side][shipNo].sunken();
		}
		
		return allSunken;
	}
	
	/**
	 * Populates boundary rows between target grid above and ocean grid below.
	 */
	private void populateBoundaryRows() 
	{
		Grid<Tile> grid = this.getGrid();
		
		Tile boundaryTile = new Tile(Color.black);
		
		for (int r = 0; r < NUM_BOUNDARY_ROWS; r++)
		{
			for (int c = 0; c < NUM_COLS; c++)
			{
				Location loc = new Location(NUM_ROWS + r, c);
				grid.put(loc, boundaryTile);
			}
		}	
	}
	
	/**
	 * Shows ships of the user's fleet.
	 */
	private void showUserFleet() 
	{
		Grid<Tile> grid = this.getGrid();
		
		int firstRowOffset = NUM_ROWS + NUM_BOUNDARY_ROWS;
		
		Tile shipTile = new Tile(Color.blue);
		Tile hitTile  = new Tile(Color.red);
		Tile missTile = new Tile(Color.gray);
		
		List<Location> missList = ai.getMissList();
		
		for (Location loc : missList)
		{
			Location shiftedLoc = new Location(firstRowOffset + loc.getRow(), 
							                   loc.getCol());
			if (grid.isValid(shiftedLoc))
			{
				grid.put(shiftedLoc, missTile);
			}
		}
		
		for (int r = 0; r < NUM_ROWS; r++)
		{
			for (int c = 0; c < NUM_COLS; c++)
			{
				if (isPositionOccupied(Player.USER, r, c, -1)) {				
					Location loc = new Location(firstRowOffset + r, c);
					int selectedShip = getShipLocatedAt(Player.USER, loc);
					
					boolean isHit = fleet[Player.USER][selectedShip].isHitAt(r, c);
					Tile tile = isHit ? hitTile : shipTile;	
					grid.put(loc, tile);
				}
			}
		}	
	}
	
	/**
	 * Shows the user's target grid.
	 */
	private void showUserTargtGrid() 
	{
		Grid<Tile> grid = this.getGrid();
		
		Tile selectedTile = new Tile(Color.orange);
		List<Location> firingTargets = user.getFiringTargets();
		
		for (Location loc : firingTargets)
		{
			grid.put(loc, selectedTile);
		}
		
		Tile hitTile  = new Tile(Color.red);	
		List<Location> shotList = user.getShotList();
		
		for (Location loc : shotList)
		{
			grid.put(loc, hitTile);
		}
		
		Tile missTile = new Tile(Color.gray);
		List<Location> missList = user.getMissList();
		
		for (Location loc : missList)
		{
			grid.put(loc, missTile);
		}
	}
	
	/**
	 * Refreshes the user's target grid.
	 */
	private void refreshUserTargtGrid()
	{
		clearGrid(Player.AI);
		showUserTargtGrid() ;
	}
	
	/** 
	 * Clears the grid of the specified side.
	 * 
	 * @param side player side
	 */
	private void clearGrid(int side) 
	{
		Grid<Tile> grid = this.getGrid();
		
		int firstRowOffset = side==Player.USER ? (NUM_ROWS + NUM_BOUNDARY_ROWS) : 0;
		
		for (int r = 0; r < NUM_ROWS; r++)
		{
			for (int c = 0; c < NUM_COLS; c++)
			{			
				Location loc = new Location(firstRowOffset + r, c);
				grid.remove(loc);
			}
		}			
	}
	
	/**
	 * Refresher the user's fleet on the grid.
	 */
	private void refreshUserFleetOnGrid()
	{
		clearGrid(Player.USER);
		showUserFleet() ;
	}
	
	/**
	 * Refreshes all grids.
	 */
	private void refreshGrids()
	{
		refreshUserFleetOnGrid();
		refreshUserTargtGrid();
	}
	
	/**
	 * Main program.
	 * 
	 * @param args command-line arguments
	 */
	public static void main(String[] args)
	{
        BattleField battleField = new BattleField();
        battleField.show();
	}

}
