import java.util.*;

import info.gridworld.grid.*;
  
/**
 * Implements class of UserShooter.
 * 
 * @author  Eric Fu
 * @author  Rohit Sriram
 * @version 5/24/2017
 * @author  Period - 3
 * @author  Assignment - APCS Final Project Salvo
 *
 * @author Sources - N/A
 */ 
public class UserShooter extends Player
{
   /**
    * Constructs a UserShooter.
    * 
    * @param numShotsPerTurn number of shots per turn
    */
    public UserShooter(int numShotsPerTurn)
    {
        super(Player.USER, numShotsPerTurn);
    }
     
    /**
     * Chooses list of firing target locations.
     * 
     * @return list of firing target locations
     */
    @Override
    public List<Location> chooseFiringTargets()
    {
    	// User selects firing targets by click on target grids,
    	// return currently selected firing targets
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
    	for (Location loc : hitList)
    	{
     		firingTargets.remove(loc);
    	}
    	
    	shotList.addAll(hitList);
    	
    	missList.addAll(firingTargets); // add remaining firing targets to missList
    	
    	firingTargets.clear();
    }
}
