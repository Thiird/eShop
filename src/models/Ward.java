package models;

import java.io.Serializable;

public enum Ward implements Serializable
{
	ALL, BAKERY, DESSERTS, DRINKS, INGREDIENTS, FRUITS_AND_VEGETABLES, MEAT_AND_FISH;

	public String[] valuesAsString()
	{
		Ward[] values = Ward.values();
		String valuesAsString[] = new String[values.length];
		int i = 0;
		
		for ( Ward ward : values )
			valuesAsString[i++] = ward.toString();
	
		return valuesAsString;
	}
}