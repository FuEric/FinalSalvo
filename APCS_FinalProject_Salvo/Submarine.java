/**
 * Implements class of Submarine.
 * 
 * @author  Eric Fu
 * @author  Rohit Sriram
 * @version 5/24/2017
 * @author  Period - 3
 * @author  Assignment - APCS Final Project Salvo
 *
 * @author Sources - N/A
 */    
public class Submarine extends Ship
{
	/**
	 * Constant member field: length of the ship
	 */
	protected static final int LENGTH = 3;
	
	/**
	 * Construct a submarine.
	 * 
	 * @param horizontal boolean flag indicating if orientation of the ship is 
	 *                   horizontal
	 * @param row		 row number of ship position
	 * @param col        column number of ship position
	 */
	public Submarine(boolean horizontal, int row, int col)
	{
		super(SUBMARINE, horizontal, row, col);
	}

	/**
	 * Gets ship type name.
	 * 
	 * @return String of ship type name
	 */
	@Override
	public String getTypeName()
	{
		return "Submarine";
	}

}
