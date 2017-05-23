 
public class Cruiser extends Ship
{
	protected static final int LENGTH = 3;
	
	public Cruiser(boolean horizontal, int row, int col)
	{
		super(CRUISER, horizontal, row, col);
	}

	@Override
	public String getTypeName()
	{
		return "Cruiser";
	}
}
