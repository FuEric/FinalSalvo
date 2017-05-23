import java.util.Comparator;

import info.gridworld.grid.Location;
 
public class PriorityLocation extends Location
                           implements Comparator<PriorityLocation>
{
	private int priority;
	
	public PriorityLocation(int r, int c, int priority)
	{
		super(r, c);
		
		this.priority = priority;
	}
	
	public PriorityLocation(Location loc, int priority) 
	{
		this(loc.getRow(), loc.getCol(), priority);
	}
	
	public int getPriority()
	{
		return priority;
	}

	@Override
	public int compare(PriorityLocation o1, PriorityLocation o2)
	{  // a negative integer, zero, or a positive integer as the first argument 
       // is less than, equal to, or greater than the second.
		return o2.priority - o1.priority;
		
		// java.util.PriorityQueue is a min queue, so the order is o2 - o1
		// the larger o1.priority is, the lesser o2.priority - o1.priority, and
		// the higher the priority in java.util.PriorityQueue
	}

	@Override
	public int compareTo(Object loc) {
		return  ((PriorityLocation)loc).priority - this.priority;
	}
}
