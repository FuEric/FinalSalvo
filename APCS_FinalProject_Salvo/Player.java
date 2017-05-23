import java.util.*;

import info.gridworld.grid.*;
 
public abstract class Player
{
	public static final int USER = 0;
	public static final int AI   = 1;
	
	protected static final int DEFAULT_NUM_SHOTS = 5;

	private int type;
	
	protected int numShots;
	protected List<Location> firingTargets;
	protected List<Location> shotList;
	protected List<Location> missList;

    public Player(int type, int numShotsPerTurn)
    {
    	this.type     = type;
    	this.numShots = numShotsPerTurn;
    	
    	firingTargets = new ArrayList<Location>();
        shotList      = new ArrayList<Location>();
        missList      = new ArrayList<Location>();    	
    }
    
    abstract public List<Location> chooseFiringTargets();
    abstract public void observeHits(List<Location> hitList, 
    				                 List<Integer> shipTypes);
    
    public int getType() 
    {
    	return type;
    }
    
    public boolean isFiringTargetsReady() 
    {
    	return firingTargets.size() >= numShots;
    }
    
    public void addFiringTarget(Location loc)
    {
    	firingTargets.add(loc);
    }
    
    public List<Location> getShotList()
    {
    	return shotList;
    }
    
    public List<Location> getMissList()
    {
    	return missList;
    }
    
    public List<Location> getFiringTargets()
    {
    	return firingTargets;
    }
}
