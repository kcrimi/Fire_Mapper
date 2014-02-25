
public class Building {
	
	int[] mbr, xVerts, yVerts;
	
	
	/**
	 * @param args
	 */
	public Building(double[] verts)
	{
		xVerts = new int[(verts.length-2)/2];
		yVerts = new int[(verts.length-2)/2];
		int xInd = 0;
		int yInd = 0;
		for (int i = 0; i< (verts.length-2); i++)
		{
			if ((i % 2) ==0)
			{
				xVerts[xInd] = (int) verts[i];
				xInd++;
			}
			else
			{
				yVerts[yInd] = (int) verts[i];
				yInd++;
			}
		}
	}
}
