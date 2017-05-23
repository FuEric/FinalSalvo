import java.util.*;
import java.awt.Color;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;


import info.gridworld.grid.*;
import info.gridworld.world.World;
 
public class BattleField extends World<Tile>
{
	public static final int NUM_ROWS = 10;
	public static final int NUM_COLS = 10;
	public static final int NUM_BOUNDARY_ROWS = 2;
	
	private Ship[][] fleet = new Ship[2][Ship.NUM_SHIPS];
	
	private static final int GAME_PHASE_INIT    = 0;
	private static final int GAME_PHASE_RUNNING = 1;
	private static final int GAME_PHASE_ENDED   = 2;
	private int gamePhase;
	private int turn;
	private Random random;
	private UserShooter user;
	private AIShooter   ai;
	
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
				Thread.yield();  // not working as intended here, so don't click on Run button
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
		
	private int getSelectedUserShip(Location loc) 
	{
		return getShipLocatedAt(Player.USER, loc);
	}
	
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
	
	private boolean isWholeFleetSunken(int side) 
	{
		boolean allSunken = true;
		
		for (int shipNo = 0; allSunken && shipNo < Ship.NUM_SHIPS; shipNo++)
		{	
			allSunken &= fleet[side][shipNo].sunken();
		}
		
		return allSunken;
	}
	
	// populate boundary rows between target grid above and ocean grid below
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
	
	private void refreshUserTargtGrid()
	{
		clearGrid(Player.AI);
		showUserTargtGrid() ;
	}
	
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
	
	private void refreshUserFleetOnGrid()
	{
		clearGrid(Player.USER);
		showUserFleet() ;
	}
	
	private void refreshGrids()
	{
		refreshUserFleetOnGrid();
		refreshUserTargtGrid();
	}
	
	public static void main(String[] args)
	{
        BattleField battleField = new BattleField();
        battleField.show();
	}

}
