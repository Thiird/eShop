package models;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

public class ShoppingCart implements Serializable
{
	private static final long serialVersionUID = 1205121062336564634L;
	private int ID;
	private Date expectedDate;
	private HashMap <Product,Integer> products;
	private final Customer customer;
	private float totalPrice;
	private PaymentMethod paymentMethod;

	public ShoppingCart( Customer customer, int ID )
	{
		this.customer = customer;
		this.ID = ID;

		products = new HashMap <Product,Integer>();
	}

	public void addProduct(Product p, int qtyToAdd)
	{
		if ( !products.containsKey(p) )
		{
			products.put(p, qtyToAdd);
		}
		else
		{
			products.put(p, products.get(p) + qtyToAdd);
		}
	}

	public void setTotalPrice(int totalPrice)
	{
		this.totalPrice = totalPrice;
	}

	public boolean containsProduct(Product p)
	{
		return products.containsKey(p);
	}

	public int getID()
	{
		return ID;
	}

	public void setExpectedDate(Date expectedDate)
	{
		this.expectedDate = expectedDate;
	}

	public Date getExpectedDate()
	{
		return expectedDate;
	}

	public Customer getCustomer()
	{
		return customer;
	}

	public float getTotalPrice()
	{
		return totalPrice;
	}

	public PaymentMethod getPaymentMethod()
	{
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod pm)
	{
		this.paymentMethod = pm;
	}
}