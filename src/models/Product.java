package models;

import java.io.Serializable;
import java.util.Set;

public class Product implements Serializable
{
	private static final long serialVersionUID = -7747364358294375695L;

	private Ward ward;
	private String name;
	private final Brand brand;
	private float qtyPerItem;
	private final MeasureUnit measureUnit;
	private float price;
	private final String image;
	private final Type type;
	private final Set<Feature> features;
	private int qtyAvailable;

	public Product(Ward ward, String name, Brand brand, float qtyPerItem, MeasureUnit measureUnit, float price, String image, Type type, Set<Feature> features, int qtyAvailable)
	{
		this.ward = ward;
		this.name = name;
		this.brand = brand;
		this.qtyPerItem = qtyPerItem;
		this.measureUnit = measureUnit;
		this.price = price;
		this.image = image;
		this.type = type;
		this.features = features;
		this.qtyAvailable = qtyAvailable;
	}

	public Ward getWard()
	{
		return ward;
	}

	public void setWard(Ward ward)
	{
		this.ward = ward;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Brand getBrand()
	{
		return brand;
	}

	public float getQtyPerItem()
	{
		return qtyPerItem;
	}

	public void setQtyPerItem(float qtyPerItem)
	{
		this.qtyPerItem = qtyPerItem;
	}

	public MeasureUnit getMeasureUnit()
	{
		return measureUnit;
	}

	public float getPrice()
	{
		return price;
	}

	public void setPrice(float price)
	{
		this.price = price;
	}

	public String getImage()
	{
		if (image.contains("res")) return image.split("res")[1].replace("\\", "/");
		else return image.replace("\\", "/");
	}

	public Type getType()
	{
		return type;
	}

	public Set<Feature> getFeatures()
	{
		return features;
	}

	public int getQtyAvailable()
	{
		return qtyAvailable;
	}

	public void setQtyAvailable(int qtyAvailable)
	{
		this.qtyAvailable = qtyAvailable;
	}
}