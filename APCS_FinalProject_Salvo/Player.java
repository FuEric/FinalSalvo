import java.util.*;

import info.gridworld.grid.*;

/**
 * Implements an abstract base class Player.
 * 
 * @author  Eric Fu
 * @author  Rohit Sriram
 * @version 5/25/2017
 * @author  Period - 3
 * @author  Assignment - APCS Final Project Salvo
 *
 * @author Sources - N/A
 */  
public abstract class Player
{
	/**
	 * Constant member field: player type USER
	 */
	public static final int USER = 0;
	
	/**
	 * Constant member field: player type AI
	 */
	public static final int AI   = 1;
	
	/**
	 * Constant member field: default number of shots per turn
	 */
	protected static final int DEFAULT_NUM_SHOTS = 5;

	/**
	 * Member field: type of player
	 */
	private int type;
	
	/**
	 * Member field: number of shots per turn
	 */
	protected int numShots;
	
	/**
	 * Member field: list of firing target locations
	 */
	protected List<Location> firingTargets;
	
	/**
	 * Member field: list of locations where opponent's ships are hit
	 */
	protected List<Location> shotList;
	
	/**
	 * Member field: list of locations where opponent's ships are not hit
	 */
	protected List<Location> missList;

	/**
	 * Constructs a player.
	 * 
	 * @param type player type
	 * 
	 * @param numShotsPerTurn number of shots per turn
	 */
    public Player(int type, int numShotsPerTurn)
    {
    	this.type     = type;
    	this.numShots = numShotsPerTurn;
    	
    	firingTargets = new ArrayList<Location>();
        shotList      = new ArrayList<Location>();
        missList      = new ArrayList<Location>();    	
    }
    
    /**
     * Chooses list of firing target locations.
     * 
     * @return list of firing target locations
     */
    abstract public List<Location> chooseFiringTargets();
    
    /**
     * Observes which and where opponent's ships are hit.
     * 
     * @param hitList  list of locations where opponent's ships are hit
     * @param shipTypes list of types of opponent's ships which are hit
     */
    abstract public void observeHits(List<Location> hitList, 
    				                 List<Integer> shipTypes);
    
    /**
     * Gets player type.
     * 
     * @return player type
     */
    public int getType() 
    {
    	return type;
    }
    
	/**
	 * Checks if firing targets are ready.
	 * 
	 * @return true if firing targets are ready, false otherwise
	 */
    public boolean isFiringTargetsReady() 
    {
    	return firingTargets.size() >= numShots;
    }
    
    /**
     * Adds a firing target.
     * 
     * @param loc location of the firing target
     */
    public void addFiringTarget(Location loc)
    {
    	firingTargets.add(loc);
    }
    
    /**
     * Gets list of locations where opponent's ships are hit.
     * 
     * @return list of locations where opponent's ships are hit
     */
    public List<Location> getShotList()
    {
    	return shotList;
    }
    
    /**
     * Gets list of locations where opponent's ships are not hit
     * @return list of locations where opponent's ships are not hit
     */
    public List<Location> getMissList()
    {
    	return missList;
    }
    
    /**
     * Gets list of firing target locations.
     * @return list of firing target locations
     */
    public List<Location> getFiringTargets()
    {
    	return firingTargets;
    }
}
