 
public class Submarine extends Ship
{
	protected static final int LENGTH = 3;
	
	public Submarine(boolean horizontal, int row, int col)
	{
		super(SUBMARINE, horizontal, row, col);
	}

	@Override
	public String getTypeName()
	{
		return "Submarine";
	}

}
