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
	private SimpleObjectProperty<Ward> ward;
	private SimpleStringProperty name;
	private SimpleObjectProperty <Brand> brand;
	private SimpleFloatProperty qtyPerItem;
	private SimpleFloatProperty price;
	private SimpleObjectProperty <Type> type;
	private SimpleBooleanProperty madeInItaly;
	private SimpleIntegerProperty qtyAvailable;
	private SimpleIntegerProperty cartQuantity;
	
	public ProductProperty(Product product, Integer cartQuantity)
	{
		this.product = product;
		imageView = new ImageView(new Image(getClass().getResourceAsStream(product.getImage())));
		imagePath = new SimpleStringProperty(product.getImage());
		ward = new SimpleObjectProperty<>(product.getWard());
		name = new SimpleStringProperty(product.getName());
		brand = new SimpleObjectProperty <> (product.getBrand());
		qtyPerItem = new SimpleFloatProperty(product.getQtyPerItem());
		price = new SimpleFloatProperty(product.getPrice());
		qtyAvailable = new SimpleIntegerProperty(product.getQtyAvailable());
		type = new SimpleObjectProperty <>(product.getType());
		this.cartQuantity = new SimpleIntegerProperty(cartQuantity);
		if ( product.getFeatures().contains(Feature.MADE_IN_ITALY))
			madeInItaly = new SimpleBooleanProperty(Boolean.TRUE);
		else
			madeInItaly = new SimpleBooleanProperty(Boolean.FALSE);
	}

	public Product getProduct()
	{
		return product;
	}
	
	public void setProduct(Product product)
	{
		this.product = product;
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
	
	public Brand getBrand()
	{
		return brand.get();
	}

	public void setBrand(Brand brand)
	{
		this.brand.set(brand);
	}
	
	public Type getType()
	{
		return type.get();
	}

	public void setType(Type type)
	{
		this.type.set(type);
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
	
	public Boolean getMadeInItaly()
	{
		return madeInItaly.get();
	}
	
	public void setMadeInItaly ( Boolean madeInItaly )
	{
		this.madeInItaly.set(madeInItaly);
	}
	
	public int getCartQuantity()
	{
		return cartQuantity.get();
	}

	public void setCartQuantity(int cartQuantity)
	{
		this.cartQuantity.set(cartQuantity);
	}
}