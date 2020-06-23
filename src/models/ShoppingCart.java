package models;

import java.io.Serializable;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;

public class ShoppingCart implements Serializable
{
	private static final long serialVersionUID = 1205121062336564634L;
	private int ID;
	private Date expectedDate;
	private HashMap <Product,Integer> products;
	private final Customer customer;
	private float totalPrice = 0f;
	DecimalFormat df = new DecimalFormat("##.00"); // Round to two decimal places
	private PaymentMethod paymentMethod;
	private int points; // Points gained after checkout

	public ShoppingCart( Customer customer )
	{
		this.customer = customer;

		products = new HashMap <Product,Integer>();

		df.setRoundingMode(RoundingMode.DOWN);
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

		totalPrice = Float
				.parseFloat(String
						.format("%.2f",
								Float.parseFloat(
										Float.toString(totalPrice + (p.getPrice() * qtyToAdd)).replace(",", ".")))
						.replace(",", "."));
	}

	public void recalculateTotalPrice()
	{// Recalculates total price, needed after product quantity change in
		// shoppingCart view

		totalPrice = 0f;

		for ( Product p : products.keySet() )
		{
			totalPrice += Float.parseFloat(df.format(p.getPrice() * products.get(p)).replace(",", "."));
		}

		totalPrice = Float.parseFloat(String
				.format("%.2f", Float.parseFloat(Float.toString(totalPrice).replace(",", "."))).replace(",", "."));
	}

	public boolean containsProduct(Product p)
	{
		return products.containsKey(p);
	}

	public boolean removeProduct(Product p)
	{
		if ( products.containsKey(p) )
		{
			products.remove(p);
			return true;
		}

		return false;
	}

	public void clear()
	{
		products.clear();
		totalPrice = 0f;
		// ID will be reset (set) during payment
	}

	public void setID(int id)
	{
		this.ID = id;
	}

	public int getID()
	{
		return ID;
	}

	public int getPoints()
	{
		return points;
	}

	public void setPoints()
	{
		points = (int) getTotalPrice();
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

	public void setPaymentMethod(PaymentMethod paymentMethod)
	{
		this.paymentMethod = paymentMethod;
	}

	public HashMap <Product,Integer> getProducts()
	{
		return products;
	}

	public void setProducts(HashMap <Product,Integer> products)
	{
		this.products = products;
	}
}