 
public class Destroyer extends Ship
{
	protected static final int LENGTH = 2;
	
	public Destroyer(boolean horizontal, int row, int col)
	{
		super(DESTROYER, horizontal, row, col);
	}

	@Override
	public String getTypeName()
	{
		return "Destroyer";
	}
}
