package models;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

public class ShoppingCart implements Serializable
{
	private static final long serialVersionUID = 1L;
	private final int ID;
	private Date expectedDate;
	private HashMap<Product, Integer> products;
	private final Customer customer;
	private float totalPrice;
	private PaymentMethod paymentMethod;

	public ShoppingCart(Customer customer, int id)
	{
		this.customer = customer;
		products = new HashMap<Product, Integer>();
		ID = id;
	}

	public void addProduct(Product p, int qtyToAdd)
	{
		if (!products.containsKey(p))
		{
			products.put(p, qtyToAdd);
		}
		else
		{
			products.put(p, products.get(p) + qtyToAdd);
		}
	}

	public boolean containsProduct(Product p)
	{
		return products.containsKey(p);
	}

	public int getID()
	{
		return ID;
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
}