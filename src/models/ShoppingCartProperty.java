package models;

import java.util.Date;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class ShoppingCartProperty
{
	private SimpleIntegerProperty ID;
	private SimpleObjectProperty <Date> expectedDate;
	private SimpleStringProperty customerEmail;
	private SimpleFloatProperty totalPrice;
	private SimpleObjectProperty <PaymentMethod> paymentMethod;

	public ShoppingCartProperty( ShoppingCart shoppingCart )
	{
		ID = new SimpleIntegerProperty(shoppingCart.getID());
		expectedDate = new SimpleObjectProperty <Date>(shoppingCart.getExpectedDate());
		customerEmail = new SimpleStringProperty(shoppingCart.getCustomer().getEmail());
		totalPrice = new SimpleFloatProperty(shoppingCart.getTotalPrice());
		paymentMethod = new SimpleObjectProperty <PaymentMethod>(shoppingCart.getPaymentMethod());
	}

	public int getID()
	{
		return ID.get();
	}

	public void setID(int ID)
	{
		this.ID.set(ID);
	}

	public Date getExpectedDate()
	{
		return expectedDate.get();
	}

	public void setExpectedDate(Date expectedDate)
	{
		this.expectedDate.set(expectedDate);
	}

	public String getCustomerEmail()
	{
		return customerEmail.get();
	}

	public void setCustomerEmail(String customerEmail)
	{
		this.customerEmail.set(customerEmail);
	}

	public float getTotalPrice()
	{
		return totalPrice.get();
	}

	public void setTotalPrice(float totalPrice)
	{
		this.totalPrice.set(totalPrice);
	}

	public PaymentMethod getPaymentMethod()
	{
		return paymentMethod.get();
	}

	public void setPaymentMethod(PaymentMethod paymentMethod)
	{
		this.paymentMethod.set(paymentMethod);
	}
}