import java.util.*;

import info.gridworld.grid.*;
   
/**
 * Implements class AIShooter.
 * 
 * @author  Eric Fu
 * @author  Rohit Sriram
 * @version 5/24/2017
 * @author  Period - 3
 * @author  Assignment - APCS Final Project Salvo
 *
 * @author Sources - N/A
 */
public class AIShooter extends Player
{
	/**
	 * Constant member fields: hunting modes
	 */
	public static int HUNT_MODE_RANDOM                   = 0;
	public static int HUNT_MODE_FULL_SCAN_PRIORITIZATION = 1;
	
	/**
	 * Member field: current hunting mode
	 */
	private int huntMode = HUNT_MODE_FULL_SCAN_PRIORITIZATION;
	
	/**
	 * Member field: a PriorityQueue of PriorityLocation which contains both location
	 *               and priority of the location. Locations with highest priorities 
	 *               will be chosen from the PriorityQueue as next batch of firing 
	 *               target locations.
	 */
    private PriorityQueue<PriorityLocation> priorityQueue;
    
    /**
     * Member field: array of Boolean objects indicating if orientation of each
     *               opponent's ship is horizontal
     */
    private Boolean[] shipHorizontal = { null, null, null, null, null }; 
                      // initialize horizontal of each ship  to unknown
    
    /**
     * Member field: list of hit location list of each opponent's ship
     */
    private ArrayList< ArrayList<Location> > shipHitLoc; 
    
    /**
     * Member field: an instance of Random object
     */
    private Random random;
    
	/**
	 * Constant member field: number of random hunting steps at start of a game
	 */
    private final int NUM_INITIAL_RANDOM_STEPS = 3;
    
    
	/**
	 * Constant member field: interval of number of steps that random hunting mode
	 *                        will be run once
	 */
    private final int RANDOM_HUNT_INTERVAL     = 5;
    
    /**
     * Member field: number of steps the game has run
     */
    private int step = 0;
    
    /**
     * Constructs a AIShooter.
     * 
     * @param numShotsPerTurn number of shots per turn
     */      
    public AIShooter(int numShotsPerTurn) 
    {
    	this(numShotsPerTurn, HUNT_MODE_FULL_SCAN_PRIORITIZATION);
    }
    
    /**
     * Constructs a AIShooter.
     * 
     * @param numShotsPerTurn number of shots per turn
     * @param huntMode  hunting mode
     */
    public AIShooter(int numShotsPerTurn, int huntMode)
    {
        super(Player.AI, numShotsPerTurn);
        
        this.huntMode = huntMode;
        
        priorityQueue = new PriorityQueue<PriorityLocation>(20, 
        				                             new PriorityLocation(0, 0, 0));
        
        random = new Random(new Date().getTime()); 
        
        shipHitLoc = new ArrayList< ArrayList<Location> >(Ship.NUM_SHIPS);
        for (int s = 0; s < Ship.NUM_SHIPS; s++)
        {
        	shipHitLoc.add( new ArrayList<Location>(Ship.MAX_SHIP_LENGTH) );
        }
    }

    /**
     * Chooses list of firing target locations.
     * 
     * @return list of firing target locations
     */
    @Override
    public List<Location> chooseFiringTargets()
    {
    	firingTargets.clear();
		step++;
    		
		if (step <= NUM_INITIAL_RANDOM_STEPS || step % RANDOM_HUNT_INTERVAL == 0 ||
		    huntMode == HUNT_MODE_RANDOM)
		{
			// select from priorityQueue first
	    	for (int i = 0; !priorityQueue.isEmpty() && i < numShots; i++)
	    	{
	    		firingTargets.add(priorityQueue.poll());
	    	}
	 
	    	// hunt more targets if necessary
	    	for (int i = firingTargets.size(); i < numShots; i++)
	    	{
	    		firingTargets.add(huntRandom());
	    	}
		}
		else
		{   
			huntFullScan();
			
	    	for (int i = 0; !priorityQueue.isEmpty() && i < numShots; i++)
	    	{
	    		firingTargets.add(priorityQueue.poll());
	    	}		
		}
		
    	return firingTargets;
    }
    
    /**
     * Observes which and where opponent's ships are hit.
     * 
     * @param hitList  list of locations where opponent's ships are hit
     * @param shipTypes list of types of opponent's ships which are hit
     */
    @Override
    public void observeHits(List<Location> hitList, List<Integer> shipTypes)
    {
    	for (int i = 0; i < hitList.size(); i++)
    	{
    		Location loc = hitList.get(i);
    		int shipNo = shipTypes.get(i);
    		
    		recordHitLocation(shipNo, loc);
    		
    		firingTargets.remove(loc);  // remaining locations will be miss locations
    		
    		if (huntMode == HUNT_MODE_RANDOM)
    		{
	    		int priority = Ship.getShipLength(shipNo) - getShipHitCount(shipNo);
	    		// Here the larger the value, the higher the priority
	    		
	    		enqueLocation(loc.getAdjacentLocation(Location.NORTH), priority);
	    		enqueLocation(loc.getAdjacentLocation(Location.SOUTH), priority);
	    		enqueLocation(loc.getAdjacentLocation(Location.EAST), priority);
	    		enqueLocation(loc.getAdjacentLocation(Location.WEST), priority);
    		}
    	}
     	
    	shotList.addAll(hitList);
    	
    	missList.addAll(firingTargets); // add remaining firing targets to missList
    	
    	firingTargets.clear();
       	
    	tryEliminateUnknownOrientation();
    }
    
    /**
     * Gets number of hits of the specified opponent's ship.
     *  
     * @param shipNo ship number
     * 
     * @return number of hits of the specified opponent's ship
     */
    private int getShipHitCount(int shipNo) 
    {
    	return shipHitLoc.get(shipNo).size();
    }
    
    /**
     * Records location where an opponent's ship was hit.
     * 
     * @param shipNo ship number
     * @param loc location where an opponent's ship was hit 
     */
    private void recordHitLocation(int shipNo, Location loc)
    {
    	int previousHitCount = getShipHitCount(shipNo);
    	
    	if (previousHitCount > 0 && shipHorizontal[shipNo] == null)
    	{   // determine if the ship is horizontal
    		Location loc0 = shipHitLoc.get(shipNo).get(0);
    		shipHorizontal[shipNo] = loc0.getRow() == loc.getRow();
    	}
    	
    	shipHitLoc.get(shipNo).add(loc);
    }
    
    /**
     * Tries to eliminate unknown orientations of opponent's ships by determining if 
     * there is enough room available in horizontal or vertical direction to place
     * a ship.
     */
    private void tryEliminateUnknownOrientation()
    {
    	for (int shipNo = 0; shipNo < Ship.NUM_SHIPS; shipNo++)
    	{  
    		if (shipHorizontal[shipNo] == null && getShipHitCount(shipNo) == 1)
    		{
    			Location hitLoc = shipHitLoc.get(shipNo).get(0);
    			// There should be only only hit for this case
    			
    	    	int horizontalRoom = getHorizontalRoom(hitLoc);
    	    	int verticalRoom   = getVerticalRoom(hitLoc);
    	    	
    	    	int shipLen = Ship.getShipLength(shipNo);
    	    	
    	    	if (horizontalRoom < shipLen) 
    	    	{
    	    		shipHorizontal[shipNo] = Boolean.FALSE;
    	    	}
    	    	else if (verticalRoom < shipLen) 
    			{
    				shipHorizontal[shipNo] = Boolean.TRUE;
    			}
    		}
    	}
    }
 
    /**
     * Puts a location with specified priority into the priority queue. If the 
     * location is already in the queue, new priority of the location will be sum 
     * of the 2 priorities.
     * 
     * @param loc		location
     * @param priority  priority
     */
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
  
    /** 
     * Checks if a specified location is adjacent to a specified list of locations.
     * 
     * @param loc     location 
     * @param locList list of locations
     * @return true if a specified location is adjacent to a specified list of 
     *         locations, false otherwise
     */
    private boolean adjacentTo(Location loc, List<Location> locList)
    {
        if (locList.contains( loc.getAdjacentLocation( Location.NORTH )) ||
			locList.contains( loc.getAdjacentLocation( Location.SOUTH )) ||
			locList.contains( loc.getAdjacentLocation( Location.EAST ))  ||
			locList.contains( loc.getAdjacentLocation( Location.WEST )))
        {
            return true;
        }
        
        return false;
    }
    
    /**
     * Checks if a specified location is adjacent to list of hit locations.
     * 
     * @param loc location 
     * 
     * @return true if a specified location is adjacent to list of hit locations, 
     *         false otherwise
     */
    private boolean adjacentToHitLocation(Location loc)
    {
    	return adjacentTo(loc, shotList);
    }
    
    /**
     * Checks if a specified location is adjacent to list of miss locations.
     * 
     * @param loc location 
     * 
     * @return true if a specified location is adjacent to list of miss locations, 
     *         false otherwise
     */
    private boolean adjacentToMissLocation(Location loc)
    {
    	return adjacentTo(loc, missList);
    }
        
    /**
     * Checks if a specified location is adjacent to list of firing target locations.
     * 
     * @param loc location 
     * 
     * @return true if a specified location is adjacent to list of firing target  
     *         locations, false otherwise
     */
    private boolean adjacentToFiringTarget(Location loc)
    {
    	return adjacentTo(loc, firingTargets);
    }
    
    /**
     * Randomly hunts a location where an opponent's ship might be.
     * 
     * @return location where an opponent's ship might be
     */
    private Location huntRandom() //randomly shoots around map to hit player ship. skips adjacent spots already fired upon
    {
    	Location loc = null;
    	boolean found = false;
    	
    	while (!found) 
    	{   		
	        int row = random.nextInt(BattleField.NUM_ROWS);
	        int col = random.nextInt(BattleField.NUM_COLS);
	        
	        // scan whole row column by column starting from a random column
	        // to ensure that towards the end, when there are too few remaining 
	        // white tiles, we still can locate all remaining white tiles
	        for (int i = 0; !found && i < BattleField.NUM_COLS; i++)
	        {
	        	int c = (col + i) % BattleField.NUM_COLS;
	        	loc = new Location(row, c);
	        }
	        
	        loc = new Location(row, col);
	        
	        found = !(   shotList.contains(loc) 
	        	      || missList.contains(loc) 
 				      || adjacentToHitLocation(loc)
                     );
    	}

        return loc;
    }
    
    /**
     * Checks if the specified location was previously fired upon.
     * 
     * @param loc location
     * 
     * @return true if the specified location was previously fired upon, 
     *         false otherwise
     */
    private boolean isPreviouslyFiredUpon(Location loc)
    {
    	return shotList.contains(loc) || missList.contains(loc);
    }
    
    /**
     * Scans each location previously not being fired upon, evaluate its priority,
     * and add it to priorityQueue.
     */
    private void huntFullScan()
    {
    	priorityQueue.clear();
    	
		for (int row = 0; row < BattleField.NUM_ROWS; row++)
		{
			for (int col = 0; col < BattleField.NUM_COLS; col++)
			{			
				Location loc = new Location(row, col);
				
				if (isPreviouslyFiredUpon(loc))
				{  
					continue; //skip it
				}
				
				int priority = determinePriority(loc);
				enqueLocation(loc, priority);			
			}
		}	
    }
    
    /**
     * Determines priority of the specified location. First a location will be  
     * determined with 100% certainty if it must be or must not be an opponent's ship
     * location, then checks if it's a possible location of an opponent's ship, and
     * finally its priority will be adjusted according its adjacency to hit, miss 
     * and firing locations.
     *  
     * @param loc location
     * 
     * @return priority of the specified location
     */
    private int determinePriority(Location loc)
    {
    	int priority = 0; 	
    	int mustBeshipNo = mustBeShipLocation(loc);
    	
    	if (mustBeshipNo >= 0)
    	{
    		priority += (Ship.getShipLength(mustBeshipNo) - 
    						                     getShipHitCount(mustBeshipNo)) * 40;
    	}
    	else if (mustNotBeShipLocation(loc))
    	{
    		priority -= Ship.MAX_SHIP_LENGTH * 50;
    	}
    	else
    	{
    		List<Integer> prospectShips = maybeShipLocation(loc);
    		
    		for (Integer shipNum : prospectShips)
    		{
    			priority += (Ship.getShipLength(shipNum) - 
    							                      getShipHitCount(shipNum)) * 20;
    		}
    	}
    	
    	//if (adjacentToHitLocation(loc))  { priority -= 1; }
    	
    	if (adjacentToMissLocation(loc)) { priority -= 2; }

    	if (adjacentToFiringTarget(loc)) { priority -= 1; }   	
    	
    	return priority;
    }

    /**
     * Determines a list of opponent's ships which might be at the specified 
     * location.
     * 
     * @param loc location
     * 
     * @return list of opponent's ships which might be at the specified location
     */
    private List<Integer> maybeShipLocation(Location loc)
    {
    	List<Integer> prospectShips = new ArrayList<Integer>(Ship.NUM_SHIPS);
    	
     	int row = loc.getRow();
    	int col = loc.getCol();

    			
    	for (int shipNo = 0; shipNo < Ship.NUM_SHIPS; shipNo++)
    	{
    		int hitCount = getShipHitCount(shipNo);
    		
    		if (hitCount <= 0) { continue; }  //not hit yet, don't know its proximity
    		
    		int remainingNumSegments = Ship.getShipLength(shipNo) - hitCount;
    		
    		if (remainingNumSegments <= 0) { continue; } // sunken already
    		
    		// check if loc is within range of remaining segments of the ship
    		boolean withinRange = false;
    		int minRadius = Integer.MAX_VALUE;
    		boolean cannotBeThisShip = false;
    		
    		for (Location hitLoc : shipHitLoc.get(shipNo))
    		{
    			if (shipHorizontal[shipNo] == null)
    			{
    				int r = hitLoc.getRow();
    				int c = hitLoc.getCol();
    				
    				if (row != r && col != c) 
    				{
    					cannotBeThisShip = true; 
    					break; 
    				}
    				
    				int dist = r == row ? Math.abs(c - col) : Math.abs(r - row);
    				
    				minRadius = Math.min(minRadius, dist);
    			}
    			else if (shipHorizontal[shipNo] == Boolean.TRUE)
    			{
    				if (row != hitLoc.getRow() ) 
    				{ 
    					cannotBeThisShip = true; 
    					break; 
    				} 
    				
    				int dc = Math.abs(hitLoc.getCol() - col); 				
    				minRadius = Math.min(minRadius, dc); 
    			}
    			else // the ship is vertical
    			{
    				if (col != hitLoc.getCol() )  
    				{ 
    					cannotBeThisShip = true; 
    					break; 
    				} 
    				
    				int dr = Math.abs(hitLoc.getRow() - row);
    				minRadius = Math.min(minRadius, dr);
    			}
    		}
    		
    		if (cannotBeThisShip) { continue; }
    		
    		withinRange = minRadius <= remainingNumSegments;
    		if (withinRange)
    		{
    			prospectShips.add(shipNo);
    		}   		
    	}
    	
    	return prospectShips;
    }
    
    /**
     * Determines with 100% certainty a list of opponent's ships which must be at 
     * the specified location. When the specified location is on the same line of
     * an opponent's ship and is between 2 ends of hit positions of the ship, then
     * it must be an opponent's ship location. When there is not enough room 
     * available on far side of hit positions of the ship, and the specified location
     * is too close to the ship on the near side of hit positions of the ship, then
     * it must be an opponent's ship location.
     * 
     * @param loc location
     * 
     * @return list of opponent's ships which might be at the specified location
     */ 
    private int mustBeShipLocation(Location loc)
    {   	
     	int row = loc.getRow();
    	int col = loc.getCol();

    	// starting from prospectShips
    	List<Integer> prospectShips = maybeShipLocation(loc);
    	
    	for (Integer shipNo : prospectShips)
    	{
    		if (shipHorizontal[shipNo] == Boolean.TRUE)
    		{
    			int minCol  = Integer.MAX_VALUE;
    			int maxCol  = Integer.MIN_VALUE;
    			int shipRow = -1;
    			
        		for (Location hitLoc : shipHitLoc.get(shipNo))
        		{
        			minCol = Math.min(minCol, hitLoc.getCol());
        			maxCol = Math.max(maxCol, hitLoc.getCol());
        			
        			shipRow = hitLoc.getRow();
        		}
        		
        		if (row != shipRow) { continue; }
        		
        		if (minCol < col && col < maxCol) 
        		{   // loc is between 2 ends of the ship hit locations
        			// so it must be a ship location
        			return shipNo; 
        		}
        		else 
        		{
        			int remainingNumSideSegments = Ship.getShipLength(shipNo) -
        							                         (maxCol - minCol + 1);
        			if (remainingNumSideSegments <= 0) { continue; }
        			
	        	    if (col < minCol)
	        		{   // loc is on the left side of left end of ship hit locations     	    	
	        			int rightRoom = 0; // max possible room on the right side of 
	        			                   // the right end of the ship hit locations
	        			
	        			for (int c = maxCol + 1; c < BattleField.NUM_COLS; c++)
	        			{
	        				Location loc1 = new Location(row, c);
	        				
	        				if (isPreviouslyFiredUpon(loc1))
	        				{
	        					break;
	        				}
	        				else
	        				{
	        					rightRoom++;
	        				}
	        			}
	        			
	        			// determine min length on the left side of the left end of 
	        			// the ship hit locations      			
	        			int minNumLeftSideSegments = remainingNumSideSegments - 
	        							                                  rightRoom;
	        			if (minNumLeftSideSegments <= 0) { continue; }
	        			
	        			int distance = minCol - col;
	        			
	        			if (distance <= minNumLeftSideSegments)
	        			{
	        				return shipNo; 
	        			}
	        		}
	        	    else
	        	    { // loc is on the right side of right end of ship hit locations     	    	
	        			int leftRoom = 0; // max possible room on the left side of 
	        			                  // the left end of the ship hit locations
	        			
	        			for (int c = minCol - 1; c >= 0; c--)
	        			{
	        				Location loc1 = new Location(row, c);
	        				
	        				if (isPreviouslyFiredUpon(loc1))
	        				{
	        					break;
	        				}
	        				else
	        				{
	        					leftRoom++;
	        				}
	        			}
	        			
	        			// determine min length on the left side of the left end of 
	        			// the ship hit locations      			
	        			int minNumRightSideSegments = remainingNumSideSegments - 
	        							                                    leftRoom;
	        			if (minNumRightSideSegments <= 0) { continue; }
	        			
	        			int distance = col - maxCol;
	        			
	        			if (distance <= minNumRightSideSegments)
	        			{
	        				return shipNo; 
	        			}
	        	    }
        		}
    		}
    		else if (shipHorizontal[shipNo] == Boolean.FALSE)
    		{   // ship is vertical
    			int minRow  = Integer.MAX_VALUE;
    			int maxRow  = Integer.MIN_VALUE;
    			int shipCol = -1;
    			
        		for (Location hitLoc : shipHitLoc.get(shipNo))
        		{
        			minRow = Math.min(minRow, hitLoc.getRow());
        			maxRow = Math.max(maxRow, hitLoc.getRow());
        			
        			shipCol = hitLoc.getCol();
        		}
        		
        		if (col != shipCol) { continue; }
        		
        		if (minRow < row && row < maxRow) 
        		{   // loc is between 2 ends of the ship hit locations
        			// so it must be a ship location
        			return shipNo; 
        		}
        		else 
        		{
        			int remainingNumSideSegments = Ship.getShipLength(shipNo) -
        							                         (maxRow - minRow + 1);
        			if (remainingNumSideSegments <= 0) { continue; }
        			
	        	    if (row < minRow)
	        		{   // loc is on the top of top end of ship hit locations     	    	
	        			int bottomRoom = 0; // max possible room on the bottom of 
	        			                   // bottom side of the ship hit locations
	        			
	        			for (int r = maxRow + 1; r < BattleField.NUM_ROWS; r++)
	        			{
	        				Location loc1 = new Location(r, col);
	        				
	        				if (isPreviouslyFiredUpon(loc1))
	        				{
	        					break;
	        				}
	        				else
	        				{
	        					bottomRoom++;
	        				}
	        			}
	        			
	        			// determine min length on the top side of the top end of 
	        			// the ship hit locations      			
	        			int minNumTopSideSegments = remainingNumSideSegments - 
	        							                                 bottomRoom;
	        			if (minNumTopSideSegments <= 0) { continue; }
	        			
	        			int distance = minRow - row;
	        			
	        			if (distance <= minNumTopSideSegments)
	        			{
	        				return shipNo; 
	        			}
	        		}
	        	    else
	        	    { //loc is on the bottom side of bottom end of ship hit locations     	    	
	        			int topRoom = 0; // max possible room on the top side of 
	        			                  // the top end of the ship hit locations
	        			
	        			for (int r = minRow - 1; r >= 0; r--)
	        			{
	        				Location loc1 = new Location(r, col);
	        				
	        				if (isPreviouslyFiredUpon(loc1))
	        				{
	        					break;
	        				}
	        				else
	        				{
	        					topRoom++;
	        				}
	        			}
	        			
	        			// determine min length on the left side of the left end of 
	        			// the ship hit locations      			
	        			int minNumBottomSideSegments = remainingNumSideSegments - 
	        							                                    topRoom;
	        			if (minNumBottomSideSegments <= 0) { continue; }
	        			
	        			int distance = row - maxRow;
	        			
	        			if (distance <= minNumBottomSideSegments)
	        			{
	        				return shipNo; 
	        			}
	        	    }
        		}
  			
    		}
    	}
    	
    	return -1;
    }
    
    /**
     * Determines with 100% certainty if the specified location must not be an 
     * opponent's ship location. When there is not enough room available around the
     * specified location, a ship cannot be placed there. When proximity of all 
     * opponent's ships are known (i.e., each opponent's ship has been hit at least
     * once), if the specified location is not at the right row or column, or is too
     * far from all opponent's ships, then it must not be an opponent's ship 
     * location. 
     * 
     * @param loc location
     * 
     * @return list of opponent's ships which might be at the specified location
     */ 
    private boolean mustNotBeShipLocation(Location loc)
    {
    	if (mustNotBeShipLocationDueToConfinedSpace(loc))      { return true; }
    	
    	if (mustNotBeShipLocationDueToDistanceToAllShips(loc)) { return true; }
   	
    	return false;
    }
    
    /**
     * Calculates available room (length) containing the specified location in 
     * horizontal direction.
     * 
     * @param loc location
     * 
     * @return available room containing the specified location in horizontal 
     *         direction
     */
    private int getHorizontalRoom(Location loc)
    {
    	int leftRoom = 0, rightRoom = 0;
    	
     	int row = loc.getRow();
    	int col = loc.getCol();
   	
    	for (int c = col + 1; c < BattleField.NUM_COLS; c++)
		{
			Location loc1 = new Location(row, c);
			
			if (missList.contains(loc1))
			{
				break;
			}
			else
			{
				rightRoom++;
			}
		}
    	
    	for (int c = col - 1; c >= 0; c--)
		{
			Location loc1 = new Location(row, c);
			
			if (missList.contains(loc1))
			{
				break;
			}
			else
			{
				leftRoom++;
			}
		}
 	
    	return leftRoom + 1 + rightRoom; // include loc itself in the middle
    }

    /**
     * Calculates available room (length) containing the specified location in 
     * vertical direction.
     * 
     * @param loc location
     * 
     * @return available room containing the specified location in vertical direction
     */
    private int getVerticalRoom(Location loc)
    {
    	int topRoom = 0, bottomRoom = 0;
       	
     	int row = loc.getRow();
    	int col = loc.getCol();
        
		for (int r = row + 1; r < BattleField.NUM_ROWS; r++)
		{
			Location loc1 = new Location(r, col);
			
			if (missList.contains(loc1))
			{
				break;
			}
			else
			{
				bottomRoom++;
			}
		}
		
		for (int r = row - 1; r >= 0; r--)
		{
			Location loc1 = new Location(r, col);
			
			if (missList.contains(loc1))
			{
				break;
			}
			else
			{
				topRoom++;
			}
		}

    	return bottomRoom + 1 + topRoom; // include loc itself in the middle
    }

    /**
     * Determines with 100% certainty if the specified location must not be an 
     * opponent's ship location because there is not enough room available around the
     * specified location. 
     * 
     * @param loc location
     * 
     * @return true if the specified location must not be an opponent's ship 
     *         location, false otherwise
     */
    private boolean mustNotBeShipLocationDueToConfinedSpace(Location loc)
    {    	
    	int minRemainingShipSegments = Integer.MAX_VALUE;
    	for (int shipNo = 0; shipNo < Ship.NUM_SHIPS; shipNo++)
    	{    		
    		int remaining = Ship.getShipLength(shipNo) - getShipHitCount(shipNo);
    		
    		if (remaining > 0)  // skip the ones already sunken 
    		{   
    			minRemainingShipSegments = Math.min(minRemainingShipSegments, 
    							                    remaining);
    		}
    	}
    	
    	int horizontalRoom = getHorizontalRoom(loc);
    	int verticalRoom   = getVerticalRoom(loc);
		
		int maxRoom = Math.max(horizontalRoom, verticalRoom);
		
    	return maxRoom < minRemainingShipSegments;
    }
 
    /**
     * Determines with 100% certainty if the specified location must not be an 
     * opponent's ship location because the specified location is not at the right 
     * row or column, or is too far from all opponent's ships. This is only 
     * applicable when proximity of all opponent's ships are known (i.e., each  
     * opponent's ship has been hit at least once).
     * 
     * @param loc location
     * 
     * @return true if the specified location must not be an opponent's ship 
     *         location, false otherwise
     */

    private boolean mustNotBeShipLocationDueToDistanceToAllShips(Location loc)
    {
    	boolean hitAllShips = true;
    	for (int shipNo = 0; hitAllShips && shipNo < Ship.NUM_SHIPS; shipNo++)
    	{  
    		hitAllShips &= (getShipHitCount(shipNo) > 0);
    	}
    	
    	if (!hitAllShips) { return false; }
    	
    	List<Integer> prospectShips = maybeShipLocation(loc);
    	
    	return prospectShips.size() <= 0;
    }
   
}
