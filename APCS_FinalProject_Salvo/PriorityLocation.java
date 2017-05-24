import java.util.Comparator;

import info.gridworld.grid.Location;

/**
 * Implements class of PriorityLocation.
 * 
 * @author  Eric Fu
 * @author  Rohit Sriram
 * @version 5/24/2017
 * @author  Period - 3
 * @author  Assignment - APCS Final Project Salvo
 *
 * @author Sources - N/A
 */   
public class PriorityLocation extends Location
                           implements Comparator<PriorityLocation>
{
	/**
	 * Member field: priority of the location
	 */
	private int priority;
	
	/**
	 * Constructs an instance of PriorityLocation.
	 * 
	 * @param row row number of the location
	 * @param col column number of the location
	 * @param priority priority of the location
	 */
	public PriorityLocation(int row, int col, int priority)
	{
		super(row, col);
		
		this.priority = priority;
	}
	
	/**
	 * Constructs an instance of PriorityLocation.
	 * 
	 * @param loc location
	 * @param priority priority of the location
	 */
	public PriorityLocation(Location loc, int priority) 
	{
		this(loc.getRow(), loc.getCol(), priority);
	}
	
	/**
	 * Gets priority of the location.
	 * 
	 * @return priority of the location
	 */
	public int getPriority()
	{
		return priority;
	}

    /**
     * Compares its two arguments for order. 
     * 
     * @param o1 the first PriorityLocation
     * @param o2 the second PriorityLocation
     * 
     * @return a negative integer, zero, or a positive integer as the first 
     *          argument is less than, equal to, or greater than the second.
     */
	@Override
	public int compare(PriorityLocation o1, PriorityLocation o2)
	{  // a negative integer, zero, or a positive integer as the first argument 
       // is less than, equal to, or greater than the second.
		return o2.priority - o1.priority;
		
		// java.util.PriorityQueue is a min queue, so the order is o2 - o1.
		// The larger o1.priority is, the lesser o2.priority - o1.priority, and
		// the higher the priority in java.util.PriorityQueue
	}

	/**
	 * Compares this object with the specified object for order.
	 * 
	 * @param loc an object
     * 
     * @return a negative integer, zero, or a positive integer as this object is 
     *         less than, equal to, or greater than the specified object
	 */
	@Override
	public int compareTo(Object loc) {
		return  ((PriorityLocation)loc).priority - this.priority;
	}
}
