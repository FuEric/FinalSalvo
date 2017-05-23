 
public class Battleship extends Ship
{
	protected static final int LENGTH = 4;
	
	public Battleship(boolean horizontal, int row, int col)
	{
		super(BATTLESHIP, horizontal, row, col);
	}

	@Override
	public String getTypeName()
	{
		return "Battleship";
	}
}
