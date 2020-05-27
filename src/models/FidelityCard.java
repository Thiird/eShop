package models;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

public class FidelityCard implements Serializable
{
	private static final long serialVersionUID = -7527580350538754026L;

	private final int ID;
	private final Date releaseDate;
	private int points = 0;

	public FidelityCard(int ID)
	{
		this.ID = ID;
		releaseDate = new Date();
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
		return ID ^ releaseDate.hashCode() ^ points;
	}

	private static final Comparator<Customer> getComparatorByID()
	{
		return new Comparator<Customer>()
		{
			@Override
			public int compare(Customer u1, Customer u2)
			{
				return u1.getFidelityCard().getID() - u2.getFidelityCard().getID();
			}
		};
	}
}