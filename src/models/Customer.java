package models;

import java.io.Serializable;

public class Customer extends User implements Serializable
{
	private static final long serialVersionUID = 6831011522310593231L;

	private FidelityCard fidelityCard;
	private PaymentMethod preferredPaymentMethod;

	public Customer( String name, String surname, String address, String CAP, String city, String phone, String email,
			String password, FidelityCard fidelityCard, PaymentMethod preferredPaymentMethod )
	{
		super(name, surname, address, CAP, city, phone, email, password);

		this.fidelityCard = fidelityCard;
		this.preferredPaymentMethod = preferredPaymentMethod;
	}

	public FidelityCard getFidelityCard()
	{
		return fidelityCard;
	}

	public PaymentMethod getPreferredPaymentMethod()
	{
		return preferredPaymentMethod;
	}

	public void setFidelityCard(FidelityCard fidelityCard)
	{
		this.fidelityCard = fidelityCard;
	}

	public void setPreferredPaymentMethod(PaymentMethod preferredPaymentMethod)
	{
		this.preferredPaymentMethod = preferredPaymentMethod;
	}

	@Override
	public int hashCode()
	{
		return super.hashCode() ^ 31415;
	}
}