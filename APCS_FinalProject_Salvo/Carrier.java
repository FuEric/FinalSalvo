 
public class Carrier extends Ship
{
	protected static final int LENGTH = 5;
	
	public Carrier(boolean horizontal, int row, int col)
	{
		super(CARRIER, horizontal, row, col);
	}

	@Override
	public String getTypeName()
	{
		return "Carrier";
	}

}
