package models;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ProductProperty
{
	private ImageView imageView;
	private SimpleStringProperty imagePath;
	private SimpleObjectProperty<Ward> ward;
	private SimpleStringProperty name;
	private SimpleFloatProperty qtyPerItem;
	private SimpleFloatProperty price;
	private SimpleIntegerProperty qtyAvailable;

	public ProductProperty(Product product)
	{
		imageView = new ImageView(new Image(getClass().getResourceAsStream(product.getImage())));
		imagePath = new SimpleStringProperty(product.getImage());
		ward = new SimpleObjectProperty<>(product.getWard());
		name = new SimpleStringProperty(product.getName());
		qtyPerItem = new SimpleFloatProperty(product.getQtyPerItem());
		price = new SimpleFloatProperty(product.getPrice());
		qtyAvailable = new SimpleIntegerProperty(product.getQtyAvailable());
	}

	public ImageView getImageView()
	{
		return imageView;
	}

	public void setImageView(ImageView imageView)
	{
		this.imageView = imageView;
	}

	public String getImagePath()
	{
		return imagePath.get();
	}

	public void setImagePath(String imagePath)
	{
		this.imagePath.set(imagePath);
	}

	public Ward getWard()
	{
		return ward.get();
	}

	public void setWard(Ward ward)
	{
		this.ward.set(ward);
	}

	public String getName()
	{
		return name.get();
	}

	public void setName(String name)
	{
		this.name.set(name);
	}

	public float getQtyPerItem()
	{
		return qtyPerItem.get();
	}

	public void setQtyPerItem(float qtyPerItem)
	{
		this.qtyPerItem.set(qtyPerItem);
	}

	public float getPrice()
	{
		return price.get();
	}

	public void setPrice(float price)
	{
		this.price.set(price);
	}

	public int getQtyAvailable()
	{
		return qtyAvailable.get();
	}

	public void setQtyAvailable(int qtyAvailable)
	{
		this.qtyAvailable.set(qtyAvailable);
	}
}