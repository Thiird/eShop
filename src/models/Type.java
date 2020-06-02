package models;

import java.io.Serializable;

public enum Type implements Serializable
{
	ALL,
	PIZZA,
	CAKE,
	MEAT,
	BREAD,
	BISCUIT,
	FRUIT,
	VEGETABLE,
	OTHERS;
	
	public static final String[] valuesAsString()
	{
		Type[] values = Type.values();
		String valuesAsString[] = new String[values.length];
		int i = 0;
		
		for ( Type type : values )
			valuesAsString[i++] = type.toString();
	
		return valuesAsString;
	}
}