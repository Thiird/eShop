package models;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ProductProperty
{
	private Product product;
	private ImageView imageView;
	private SimpleStringProperty imagePath;
	private SimpleObjectProperty <Ward> ward;
	private SimpleStringProperty name;
	private SimpleObjectProperty <Brand> brand;
	private SimpleFloatProperty qtyPerItem;
	private SimpleFloatProperty price;
	private SimpleObjectProperty <Type> type;
	private SimpleBooleanProperty bio;
	private SimpleBooleanProperty glutenFree;
	private SimpleBooleanProperty madeInItaly;
	private SimpleBooleanProperty milkFree;
	private SimpleIntegerProperty qtyAvailable;
	private SimpleIntegerProperty cartQuantity;
	private SimpleFloatProperty totalPrice;

	public ProductProperty( Product product, Integer cartQuantity )
	{
		this.product = product;
		imageView = new ImageView(new Image(getClass().getResourceAsStream(product.getImage())));
		imagePath = new SimpleStringProperty(product.getImage());
		ward = new SimpleObjectProperty <>(product.getWard());
		name = new SimpleStringProperty(product.getName());
		brand = new SimpleObjectProperty <>(product.getBrand());
		qtyPerItem = new SimpleFloatProperty(product.getQtyPerItem());
		price = new SimpleFloatProperty(product.getPrice());
		type = new SimpleObjectProperty <>(product.getType());
		bio = product.getFeatures().contains(Feature.BIO) ? new SimpleBooleanProperty(Boolean.TRUE)
				: new SimpleBooleanProperty(Boolean.FALSE);
		glutenFree = product.getFeatures().contains(Feature.GLUTEN_FREE) ? new SimpleBooleanProperty(Boolean.TRUE)
				: new SimpleBooleanProperty(Boolean.FALSE);
		madeInItaly = product.getFeatures().contains(Feature.MADE_IN_ITALY) ? new SimpleBooleanProperty(Boolean.TRUE)
				: new SimpleBooleanProperty(Boolean.FALSE);
		milkFree = product.getFeatures().contains(Feature.MILK_FREE) ? new SimpleBooleanProperty(Boolean.TRUE)
				: new SimpleBooleanProperty(Boolean.FALSE);
		qtyAvailable = new SimpleIntegerProperty(product.getQtyAvailable());
		this.cartQuantity = new SimpleIntegerProperty(cartQuantity);
		totalPrice = new SimpleFloatProperty (cartQuantity * product.getPrice());
	}

	public Product getProduct()
	{
		return product;
	}

	public ImageView getImageView()
	{
		return imageView;
	}

	public String getImagePath()
	{
		return imagePath.get();
	}

	public Ward getWard()
	{
		return ward.get();
	}

	public String getName()
	{
		return name.get();
	}

	public void setName(String name)
	{
		this.name.set(name);
	}

	public Brand getBrand()
	{
		return brand.get();
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

	public Type getType()
	{
		return type.get();
	}

	public Boolean isBio()
	{
		return bio.get();
	}

	public Boolean isGlutenFree()
	{
		return glutenFree.get();
	}

	public Boolean isMadeInItaly()
	{
		return madeInItaly.get();
	}

	public Boolean isMilkFree()
	{
		return milkFree.get();
	}

	public int getQtyAvailable()
	{
		return qtyAvailable.get();
	}

	public void setQtyAvailable(int qtyAvailable)
	{
		this.qtyAvailable.set(qtyAvailable);
	}

	public int getCartQuantity()
	{
		return cartQuantity.get();
	}

	public void setCartQuantity(int cartQuantity)
	{
		this.cartQuantity.set(cartQuantity);
	}
	
	public float getTotalPrice()
	{
		return totalPrice.get();
	}
}