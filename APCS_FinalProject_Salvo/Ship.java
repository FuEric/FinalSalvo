/** 
 */
public abstract class Ship
{	
	public static final int NUM_SHIPS = 5;
	
	public static final int CARRIER    = 0;
	public static final int BATTLESHIP = 1;
	public static final int CRUISER    = 2;
	public static final int SUBMARINE  = 3;
	public static final int DESTROYER  = 4;
	
	private int type;
	private int length = 1;
	private boolean horizontal = false;
	private int headRow;
	private int headCol;
	
	private boolean[] segHit;
	
	public static final int MAX_SHIP_LENGTH = 5;
	private static final int[] shipLength = {Carrier.LENGTH, Battleship.LENGTH, 
		                        Cruiser.LENGTH, Submarine.LENGTH, Destroyer.LENGTH};
	
	abstract public String getTypeName();
	
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
	
	public int getType()
	{
		return type;
	}
	
	public static int getShipLength(int shipType) 
	{
		return (0 <= shipType && shipType < NUM_SHIPS) ? shipLength[shipType] : 0;
	}
	
	public void moveTo(int row, int col)
	{
		this.headRow = row;
		this.headCol = col;		
	}
	
	public boolean sunken()
	{
		boolean allHit = true;
		
		for (int segment = 0; allHit && segment < length; segment++) 
		{
			allHit &= segHit[segment];
		}
		
		return allHit;
	}
	
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
	
	public void takeHitAt(int row, int col) 
	{
		if (!occupies(row, col) )
		{
			return;
		}
		
		int segment = horizontal ? (col - headCol) : (row - headRow);
	
		segHit[segment] = true;
	}
	
	
	public boolean isHitAt(int row, int col) 
	{
		if (!occupies(row, col) )
		{
			return false;
		}
		
		int segment = horizontal ? (col - headCol) : (row - headRow);
	
		return segHit[segment];
	}


	public int getLength()
	{
		return length;
	}
	
	public int getHeadRow()
	{
		return headRow;
	}
	
	private int getTailRow()
	{
		return headRow + length - 1;
	}	
	
	public int getHeadCol()
	{
		return headCol;
	}
	
	private int getTailCol()
	{
		return headCol + length - 1;
	}
	
	public boolean isHorizontal()
	{
		return horizontal;
	}
}