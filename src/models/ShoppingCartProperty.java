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
	private SimpleObjectProperty <State> state;
	private SimpleIntegerProperty points;

	public ShoppingCartProperty( ShoppingCart shoppingCart )
	{
		ID = new SimpleIntegerProperty(shoppingCart.getID());
		expectedDate = new SimpleObjectProperty <Date>(shoppingCart.getExpectedDate());
		customerEmail = new SimpleStringProperty(shoppingCart.getCustomer().getEmail());
		totalPrice = new SimpleFloatProperty(shoppingCart.getTotalPrice());
		paymentMethod = new SimpleObjectProperty <PaymentMethod>(shoppingCart.getPaymentMethod());
		state = new SimpleObjectProperty <State>(findState(shoppingCart.getExpectedDate()));
		points = new SimpleIntegerProperty(shoppingCart.getPoints());
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

	public State getState()
	{
		return state.get();
	}

	public void setState(State state)
	{
		this.state.set(state);
	}

	private State findState(Date expectedDate)
	{
		Date now = new Date();

		if ( now.after(expectedDate) )
			return State.DELIVERED;
		else
		{
			long hoursBetween = (expectedDate.getTime() - now.getTime()) / 3600000;

			if ( hoursBetween <= 24 )
				return State.PREPARING;
			else
				return State.CONFIRMED;
		}
	}

	public int getPoints()
	{
		return points.get();
	}

	public void setPoints(int points)
	{
		this.points.set(points);
	}
}