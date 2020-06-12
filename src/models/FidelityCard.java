package models;

import java.io.Serializable;
import java.util.Date;

public class FidelityCard implements Serializable
{
	private static final long serialVersionUID = -7527580350538754026L;

	private final int ID;
	private final Date releaseDate;
	private int points;

	public FidelityCard( int ID )
	{
		this.ID = ID;
		points = 0;
		releaseDate = new Date();
	}

	public void addPoints(int toAdd)
	{
		this.points += toAdd;
	}

	public int getID()
	{
		return ID;
	}

	public Date getReleaseDate()
	{
		return releaseDate;
	}

	public int getPoints()
	{
		return points;
	}

	@Override
	public int hashCode()
	{
		return ID ^ points;
	}
}