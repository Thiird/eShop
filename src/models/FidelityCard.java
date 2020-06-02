package models;

import java.io.Serializable;
import java.util.Date;

public class FidelityCard implements Serializable
{
	private static final long serialVersionUID = -7527580350538754026L;

	private final int ID;
	private final Date releaseDate;
	private int points;
	private boolean enabled;

	public FidelityCard(int ID, boolean enabled)
	{
		this.ID = ID;
		releaseDate = new Date();
		points = 0;
		this.enabled = enabled;
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
	
	public boolean getEnabled()
	{
		return enabled;
	}

	@Override
	public int hashCode()
	{
		return ID ^ releaseDate.hashCode() ^ points;
	}
}