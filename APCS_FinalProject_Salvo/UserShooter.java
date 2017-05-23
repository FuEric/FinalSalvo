import java.util.*;

import info.gridworld.grid.*;
 
public class UserShooter extends Player
{
   
    public UserShooter(int numShotsPerTurn)
    {
        super(Player.USER, numShotsPerTurn);
        //setMessage( "Target five tiles to shoot at. Hits will be marked red, "
        //    + "misses will be marked gray." );
    }
     
    @Override
    public List<Location> chooseFiringTargets()
    {
    	// User selects firing targets by click on target grids,
    	// return currently selected firing targets
    	return firingTargets;
    }
    
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
