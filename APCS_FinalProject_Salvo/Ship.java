/**
 * Implements an abstract base class Ship.
 * 
 * @author  Eric Fu
 * @author  Rohit Sriram
 * @version 5/24/2017
 * @author  Period - 3
 * @author  Assignment - APCS Final Project Salvo
 *
 * @author Sources - N/A
 */  
public abstract class Ship
{	
	/**
	 * Constant member field: # of ships per player
	 */
	public static final int NUM_SHIPS = 5;

	/**
	 * Constant member fields: types of ships per player
	 */
	public static final int CARRIER    = 0;
	public static final int BATTLESHIP = 1;
	public static final int CRUISER    = 2;
	public static final int SUBMARINE  = 3;
	public static final int DESTROYER  = 4;
	
	/**
	 * Member field: type of ship
	 */
	private int type;
	
	/**
	 * Member field: length of ship
	 */
	private int length = 1;
	
	/**
	 * Member field: orientation of ship
	 */
	private boolean horizontal = false;
	
	/**
	 * Member field: row # of head of ship
	 */
	private int headRow;
	
	/**
	 * Member field: column # of head of ship
	 */
	private int headCol;
	
	/**
	 * Member field: array of booleans to record if a ship segment is hit 
	 */
	private boolean[] segHit;
	
	/**
	 * Constant member field: maximum length among all ships 
	 */
	public static final int MAX_SHIP_LENGTH = 5;
	
	
	/**
	 * Constant member field: array of ship lengths
	 */	
	private static final int[] shipLength = {Carrier.LENGTH, Battleship.LENGTH, 
		                        Cruiser.LENGTH, Submarine.LENGTH, Destroyer.LENGTH};
	
	/**
	 * Gets ship type name.
	 * 
	 * @return String of ship type name
	 */
	abstract public String getTypeName();
	
	/**
	 * Constructs a ship.
	 * 
	 * @param shipType   int of ship type
	 * @param horizontal boolean flag indicating if orientation of the ship is 
	 *                   horizontal
	 * @param row		 row number of ship position
	 * @param col        column number of ship position
	 */
	public Ship(int shipType, boolean horizontal, int row, int col)
	{
        this.type       = shipType;
		this.horizontal = horizontal;
		this.headRow    = row;
		this.headCol    = col;
		
		this.length = getShipLength(shipType);
		
		segHit = new boolean[this.length];
		for (int i=0; i < this.length; i++) 
		{
			segHit[i] = false;
		}
	}
	
	/**
	 * Gets ship type.
	 * 
	 * @return  ship type
	 */
	public int getType()
	{
		return type;
	}
	
	/**
	 * Gets ship length.
	 * 
	 * @param shipType ship type
	 * @return ship length
	 */
	public static int getShipLength(int shipType) 
	{
		return (0 <= shipType && shipType < NUM_SHIPS) ? shipLength[shipType] : 0;
	}
	
	/**
	 * Moves the ship to specified new location.
	 * 
	 * @param row row number
	 * @param col column number
	 */
	public void moveTo(int row, int col)
	{
		this.headRow = row;
		this.headCol = col;		
	}
	
	/**
	 * Checks if the ship is sunken.
	 * 
	 * @return true if the ship is sunken, false otherwise
	 */
	public boolean sunken()
	{
		boolean allHit = true;
		
		for (int segment = 0; allHit && segment < length; segment++) 
		{
			allHit &= segHit[segment];
		}
		
		return allHit;
	}
	
	/**
	 * Checks if the ship occupies the specified location.
	 * 
	 * @param row row number of the location
	 * @param col column number of the location
	 * 
	 * @return true if the ship occupies the specified location, false otherwise
	 */
	public boolean occupies(int row, int col) 
	{
		if (horizontal) 
		{
			return headRow == row && (headCol <= col && col <= getTailCol());
			
		}
		else 
		{
			return headCol == col && (headRow <= row && row <= getTailRow());
		}	
	}
	
	/**
	 * Takes a hit at the specified location.
	 * 
	 * @param row row number of the location
	 * @param col column number of the location
	 */
	public void takeHitAt(int row, int col) 
	{
		if (!occupies(row, col) )
		{
			return;
		}
		
		int segment = horizontal ? (col - headCol) : (row - headRow);
	
		segHit[segment] = true;
	}
	
	/**
	 * Checks if the ship is hit at the specified location.
	 *
	 * @param row row number of the location
	 * @param col column number of the location
	 * 
	 * @return true if the ship is hit at the specified location, false otherwise
	 */
	public boolean isHitAt(int row, int col) 
	{
		if (!occupies(row, col) )
		{
			return false;
		}
		
		int segment = horizontal ? (col - headCol) : (row - headRow);
	
		return segHit[segment];
	}

    /**
     * Gets length of the ship.
     * 
     * @return length of the ship
     */
	public int getLength()
	{
		return length;
	}
	
	/**
	 * Gets row number of head of the ship.
	 * 
	 * @return row number of head of the ship.
	 */
	public int getHeadRow()
	{
		return headRow;
	}
	
	/**
	 * Gets row number of tail of the ship.
	 * 
	 * @return row number of tail of the ship.
	 */	
	private int getTailRow()
	{
		return headRow + length - 1;
	}	
	
	/**
	 * Gets column number of head of the ship.
	 * 
	 * @return column number of head of the ship.
	 */
	public int getHeadCol()
	{
		return headCol;
	}
	
	/**
	 * Gets column number of tail of the ship.
	 * 
	 * @return column number of tail of the ship.
	 */	
	private int getTailCol()
	{
		return headCol + length - 1;
	}
	
	/**
	 * Checks if orientation of the ship is horizontal.
	 * 
	 * @return true if orientation of the ship is horizontal, false otherwise
	 */
	public boolean isHorizontal()
	{
		return horizontal;
	}
}