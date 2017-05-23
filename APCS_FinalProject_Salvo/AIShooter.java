import java.util.*;

import info.gridworld.grid.*;
 
public class AIShooter extends Player
{
    private PriorityQueue<PriorityLocation> priorityQueue;
    
    private int[] shipHitCount = {0, 0, 0, 0, 0}; // # of times a ship being hit
    
    private Random random;
       
    public AIShooter(int numShotsPerTurn)
    {
        super(Player.AI, numShotsPerTurn);
        
        priorityQueue = new PriorityQueue<PriorityLocation>(20, 
        				                             new PriorityLocation(0, 0, 0));
        
        random = new Random(new Date().getTime());    
    }
    
    public boolean tileAdjacent(Location l)
    {
        if (shotList.contains( l.getAdjacentLocation( Location.NORTH )) ||
            shotList.contains( l.getAdjacentLocation( Location.SOUTH )) ||
            shotList.contains( l.getAdjacentLocation( Location.EAST )) ||
            shotList.contains( l.getAdjacentLocation( Location.WEST )))
        {
            return true;
        }
        
        return false;
    }

    public Location hunt() //randomly shoots around map to hit player ship. skips adjacent spots already fired upon
    {
        int row = random.nextInt(BattleField.NUM_ROWS);
        int col = random.nextInt(BattleField.NUM_COLS);
        
        Location loc = new Location(row, col);
        
        if (shotList.contains(loc) || missList.contains(loc) 
        				           || tileAdjacent(loc)
           )
        {   // need improve here
        	// towards the end, there are too few remaining white tiles,
        	// and random pick cannot reach these white tiles, and caused 
        	// stock overflow
            return hunt();
        }
        
        return loc;
    }
    
    private void enqueLocation(Location loc, int priority)
    {
     	int row = loc.getRow();
    	int col = loc.getCol();
    	
    	boolean valid = 0 <= row && row < BattleField.NUM_ROWS &&
    					0 <= col && col < BattleField.NUM_COLS;
    	
    	if (!valid) 
    	{
    		return;
    	}
   
       	// check if loc is already contained in priorityQueue
    	// if yes, increase priority at this location
    	List<Location> reOcurringLocations = new ArrayList<Location>();
    	
    	Iterator<PriorityLocation> it = priorityQueue.iterator();
    	while (it.hasNext()) 
    	{
    		PriorityLocation exitingLoc = it.next();
    		if (exitingLoc.getRow() == row && exitingLoc.getCol() == col)
    		{
    			priority += exitingLoc.getPriority();
    			
    			reOcurringLocations.add(exitingLoc);
    		}
    	}
    	
    	// remove the existing/duplicated location(s)
    	for (Location reOcurred : reOcurringLocations)
    	{
    		priorityQueue.remove(reOcurred);
    	}
    	
    	priorityQueue.add(new PriorityLocation(loc, priority));
    }

    @Override
    public List<Location> chooseFiringTargets()
    {
    	firingTargets.clear();
    	
    	// select from priorityQueue first
    	for (int i = 0; !priorityQueue.isEmpty() && i < numShots; i++)
    	{
    		firingTargets.add(priorityQueue.poll());
    	}
 
    	// hunt more targets if necessary
    	for (int i = firingTargets.size(); i < numShots; i++)
    	{
    		firingTargets.add(hunt());
    	}

    	return firingTargets;
    }
    
    @Override
    public void observeHits(List<Location> hitList, List<Integer> shipTypes)
    {
    	for (int i = 0; i < hitList.size(); i++)
    	{
    		Location loc = hitList.get(i);
    		int shipType = shipTypes.get(i);
    		
    		shipHitCount[shipType]++;
    		
    		firingTargets.remove(loc);
    		
    		int priority = Ship.getShipLength(shipType) - shipHitCount[shipType];
    		// Here the larger the value, the higher the priority
    		
    		enqueLocation(loc.getAdjacentLocation(Location.NORTH), priority);
    		enqueLocation(loc.getAdjacentLocation(Location.SOUTH), priority);
    		enqueLocation(loc.getAdjacentLocation(Location.EAST), priority);
    		enqueLocation(loc.getAdjacentLocation(Location.WEST), priority);
    	}
    	
    	shotList.addAll(hitList);
    	
    	missList.addAll(firingTargets); // add remaining firing targets to missList
    	
    	firingTargets.clear();
    }
}
