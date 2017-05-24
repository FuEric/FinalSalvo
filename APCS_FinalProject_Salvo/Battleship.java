/**
 * Implements class of Battleship.
 * 
 * @author  Eric Fu
 * @author  Rohit Sriram
 * @version 5/24/2017
 * @author  Period - 3
 * @author  Assignment - APCS Final Project Salvo
 *
 * @author Sources - N/A
 */    
public class Battleship extends Ship
{
	/**
	 * Constant member field: length of the ship
	 */
	protected static final int LENGTH = 4;
	
	/**
	 * Construct a battleship.
	 * 
	 * @param horizontal boolean flag indicating if orientation of the ship is 
	 *                   horizontal
	 * @param row		 row number of ship position
	 * @param col        column number of ship position
	 */
	public Battleship(boolean horizontal, int row, int col)
	{
		super(BATTLESHIP, horizontal, row, col);
	}

	/**
	 * Gets ship type name.
	 * 
	 * @return String of ship type name
	 */
	@Override
	public String getTypeName()
	{
		return "Battleship";
	}
}
