import java.util.*;

import org.junit.*;
import static org.junit.Assert.*;
import junit.framework.JUnit4TestAdapter;

import info.gridworld.grid.Location;

/**
 * Implements a Salvo JUnit test program.
 * 
 * @author  Eric Fu
 * @author  Rohit Sriram
 * @version 5/24/2017
 * @author  Period - 3
 * @author  Assignment - APCS Final Project Salvo
 *
 * @author Sources - N/A
 */  
public class JUTestSalvo
{

    /**
     * Tests ship constructor.
     */
    @Test
    public void testShipConstructor()
    {
    	int row = 0, col = 2;
    	boolean horizontal = true;
    	Ship ship = new Battleship(horizontal, row, col);
    	
    	assertNotNull(ship);
    }

	/**
	 * Tests ship getters.
	 */
	@Test
	public void testShipGetters()
	{
		int row = 0, col = 2;
		boolean horizontal = true;
		Ship ship = new Battleship(horizontal, row, col);
    	
    	assertEquals("<<getType - failed>>", Ship.BATTLESHIP, ship.getType());
    	
       	assertEquals("<<getTypeName - failed>>", "Battleship", ship.getTypeName());
   	
    	assertEquals("<<getShipLength - failed>>", Battleship.LENGTH, 
    					                         Ship.getShipLength(ship.getType()));
       	
    	assertEquals("<<getLength - failed>>", Battleship.LENGTH, ship.getLength());
       	
    	assertEquals("<<getHeadRow - failed>>", row, ship.getHeadRow());
      	
    	assertEquals("<<getHeadCol - failed>>", col, ship.getHeadCol());
     	
    	assertEquals("<<isHorizontal - failed>>", horizontal, ship.isHorizontal());
    }
    

	/**
	 * Tests other ship APIs.
	 */
	@Test
	public void testShipAPIs()
	{
		int row = 0, col = 2;
		boolean horizontal = true;
		Ship ship = new Battleship(horizontal, row, col);
    	
		row++; col++;
		ship.moveTo(row, col);
		
		assertEquals("<<moveTo - failed>>", row, ship.getHeadRow());	
    	assertEquals("<<moveTo - failed>>", col, ship.getHeadCol());
    	
    	assertFalse("<<sunken - failed>>", ship.sunken());
    	
    	int col1 = col + 1;
       	assertTrue("<<occupies - failed>>", ship.occupies(row, col1));
       	
       	ship.takeHitAt(row, col1);
      	assertTrue("<<takeHitAt/isHitAt - failed>>", ship.isHitAt(row, col1));
	}
	
	/**
	 * Tests Carrier APIs.
	 */
	@Test
	public void testCarrierAPIs()
	{
		int row = 0, col = 2;
		boolean horizontal = true;
		Ship ship = new Carrier(horizontal, row, col);
    	
    	assertEquals("<<getType - failed>>", Ship.CARRIER, ship.getType());
    	
       	assertEquals("<<getTypeName - failed>>", "Carrier", ship.getTypeName());
     	
    	assertEquals("<<getLength - failed>>", Carrier.LENGTH, ship.getLength());
	}

	/**
	 * Tests Battleship APIs.
	 */
	@Test
	public void testBattleshipAPIs()
	{
		int row = 0, col = 2;
		boolean horizontal = true;
		Ship ship = new Battleship(horizontal, row, col);
    	
    	assertEquals("<<getType - failed>>", Ship.BATTLESHIP, ship.getType());
    	
       	assertEquals("<<getTypeName - failed>>", "Battleship", ship.getTypeName());
     	
    	assertEquals("<<getLength - failed>>", Battleship.LENGTH, ship.getLength());
	}
	
	/**
	 * Tests Cruiser APIs.
	 */
	@Test
	public void testCruiserAPIs()
	{
		int row = 0, col = 2;
		boolean horizontal = true;
		Ship ship = new Cruiser(horizontal, row, col);
    	
    	assertEquals("<<getType - failed>>", Ship.CRUISER, ship.getType());
    	
       	assertEquals("<<getTypeName - failed>>", "Cruiser", ship.getTypeName());
     	
    	assertEquals("<<getLength - failed>>", Cruiser.LENGTH, ship.getLength());
	}
	
	/**
	 * Tests Submarine APIs.
	 */
	@Test
	public void testSubmarineAPIs()
	{
		int row = 0, col = 2;
		boolean horizontal = true;
		Ship ship = new Submarine(horizontal, row, col);
    	
    	assertEquals("<<getType - failed>>", Ship.SUBMARINE, ship.getType());
    	
       	assertEquals("<<getTypeName - failed>>", "Submarine", ship.getTypeName());
     	
    	assertEquals("<<getLength - failed>>", Submarine.LENGTH, ship.getLength());
	}

	
	/**
	 * Tests Destroyer APIs.
	 */
	@Test
	public void testDestroyerAPIs()
	{
		int row = 0, col = 2;
		boolean horizontal = true;
		Ship ship = new Destroyer(horizontal, row, col);
    	
    	assertEquals("<<getType - failed>>", Ship.DESTROYER, ship.getType());
    	
       	assertEquals("<<getTypeName - failed>>", "Destroyer", ship.getTypeName());
     	
    	assertEquals("<<getLength - failed>>", Destroyer.LENGTH, ship.getLength());
	}

	/**
	 * Tests Player APIs.
	 */
	@Test
	public void testPlayerAPIs()
	{
		Player player = new UserShooter(Player.DEFAULT_NUM_SHOTS);

    	assertEquals("<<getType - failed>>", Player.USER, player.getType());
    	
    	assertFalse("<<isFiringTargetsReady - failed>>", 
    					                             player.isFiringTargetsReady());
    	
    	int row = 0, col = 2;
    	for (int i = 0; i < Player.DEFAULT_NUM_SHOTS; i++)
    	{
    		player.addFiringTarget(new Location(row, col + i));
    	}
    	assertTrue("<<isFiringTargetsReady - failed>>", 
    					                             player.isFiringTargetsReady());
    	
    	assertEquals("<<getFiringTargets - failed>>", player.DEFAULT_NUM_SHOTS, 
    					                          player.getFiringTargets().size());
      	
    	assertEquals("<<getShotList - failed>>", 0, player.getShotList().size());
     	
    	assertEquals("<<getMissList - failed>>", 0, player.getMissList().size());
   	}

	/**
	 * Tests UserShooter APIs.
	 */
	@Test
	public void testUserShooterAPIs()
	{
		Player player = new UserShooter(Player.DEFAULT_NUM_SHOTS);

    	assertEquals("<<getType - failed>>", Player.USER, player.getType());
    	
    	assertFalse("<<isFiringTargetsReady - failed>>", 
    					                             player.isFiringTargetsReady());
    	
    	int row = 0, col = 2;
    	for (int i = 0; i < Player.DEFAULT_NUM_SHOTS; i++)
    	{
    		player.addFiringTarget(new Location(row, col + i));
    	}
    	assertTrue("<<isFiringTargetsReady - failed>>", 
    					                             player.isFiringTargetsReady());
    	
    	assertEquals("<<getFiringTargets - failed>>", player.DEFAULT_NUM_SHOTS, 
    					                          player.getFiringTargets().size());
       	
    	assertEquals("<<chooseFiringTargets - failed>>", player.DEFAULT_NUM_SHOTS, 
    					                        player.chooseFiringTargets().size());
      	
    	assertEquals("<<getShotList - failed>>", 0, player.getShotList().size());
     	
    	assertEquals("<<getMissList - failed>>", 0, player.getMissList().size());
    	
     	assertTrue("<<isFiringTargetsReady - failed>>", 
    					                             player.isFiringTargetsReady());

    	player.observeHits(new ArrayList<Location>(), new ArrayList<Integer>());
    	
       	assertEquals("<<observeHits - failed>>", 0, 
       					                          player.getFiringTargets().size());
       	
    	assertEquals("<<getMissList - failed>>", player.DEFAULT_NUM_SHOTS,
    					                               player.getMissList().size());
	}

	/**
	 * Tests AIShooter APIs.
	 */
	@Test
	public void testAIShooterAPIs()
	{
		Player player = new AIShooter(Player.DEFAULT_NUM_SHOTS);

    	assertEquals("<<getType - failed>>", Player.AI, player.getType());
    	
    	assertFalse("<<isFiringTargetsReady - failed>>", 
    					                             player.isFiringTargetsReady());
     	
    	assertEquals("<<chooseFiringTargets - failed>>", player.DEFAULT_NUM_SHOTS, 
    					                        player.chooseFiringTargets().size());
       	
    	assertEquals("<<getFiringTargets - failed>>", player.DEFAULT_NUM_SHOTS, 
    					                          player.getFiringTargets().size());
        	
    	assertEquals("<<getShotList - failed>>", 0, player.getShotList().size());
     	
    	assertEquals("<<getMissList - failed>>", 0, player.getMissList().size());
      	
     	assertTrue("<<isFiringTargetsReady - failed>>", 
    					                             player.isFiringTargetsReady());

    	player.observeHits(new ArrayList<Location>(), new ArrayList<Integer>());
    	
       	assertEquals("<<observeHits - failed>>", 0, 
       					                          player.getFiringTargets().size());
    	
    	assertEquals("<<getMissList - failed>>", player.DEFAULT_NUM_SHOTS,
    					                               player.getMissList().size());

	}

	/**
	 * Tests BattleField APIs.
	 */
	@Test
	public void testBattleFieldAPIs()
	{
		BattleField battleField = new BattleField();
		
		assertNotNull(battleField);
	 	
       	assertEquals("<<getGamePhase - failed>>", BattleField.GAME_PHASE_INIT, 
       					                          battleField.getGamePhase());
	}

	/**
	 * Tests PriorityLocation APIs.
	 */
	@Test
	public void testPriorityLocationAPIs()
	{
		int row = 0, col = 2;
		int priority1 = 1, priority2 = 2;
		
		PriorityLocation loc1 = new PriorityLocation(row, col,  priority1);
		PriorityLocation loc2 = new PriorityLocation(row, col+2, priority2);
		PriorityLocation loc3 = new PriorityLocation(row, col+3, priority1);
		
    	assertEquals("<<getPriority - failed>>", priority1,  loc1.getPriority());

    	assertTrue("<<compare - failed>>", loc1.compare(loc1,  loc2) > 0);
    	assertTrue("<<compare - failed>>", loc1.compare(loc2,  loc1) < 0);
       	assertTrue("<<compare - failed>>", loc1.compare(loc1,  loc3) == 0);
       	
    	assertTrue("<<compareTo - failed>>", loc1.compareTo(loc2) > 0);
       	assertTrue("<<compareTo - failed>>", loc2.compareTo(loc1) < 0);
      	assertTrue("<<compareTo - failed>>", loc1.compareTo(loc3) == 0);
	}
	
	
    /**
     * Creates test suite.
     * 
     * @return test suite
     */
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter( JUTestSalvo.class );
    }

    /**
     * Executes JUnit test of Salvo 
     * @param args  command-line arguments (not used)
     */
    public static void main( String args[] )
    {
        org.junit.runner.JUnitCore.main( "JUTestSalvo" );
    }

}
