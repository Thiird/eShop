package models;

import java.io.Serializable;

public class User implements Serializable
{
	private static final long serialVersionUID = 2938649178329713235L;

	private String name;
	private String surname;
	private String address;
	private String CAP;
	private String city;
	private String phone;
	private final String email;
	private String password;
	private boolean loggedIn = false;

	public User( String name, String surname, String address, String CAP, String city, String phone, String email,
			String password )
	{
		this.name = name;
		this.surname = surname;
		this.address = address;
		this.CAP = CAP;
		this.city = city;
		this.phone = phone;
		this.email = email;
		this.password = password;
	}

	public String getName()
	{
		return name;
	}

	public String getSurname()
	{
		return surname;
	}

	public String getAddress()
	{
		return address;
	}

	public String getCAP()
	{
		return CAP;
	}

	public String getCity()
	{
		return city;
	}

	public String getPhone()
	{
		return phone;
	}

	public String getEmail()
	{
		return email;
	}

	public String getPassword()
	{
		return password;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setSurname(String surname)
	{
		this.surname = surname;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public void setCAP(String CAP)
	{
		this.CAP = CAP;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public boolean getLoggedIn()
	{
		return this.loggedIn;
	}

	public void setLoggedIn(boolean flag)
	{
		this.loggedIn = flag;
	}

	@Override
	public int hashCode()
	{
		return name.hashCode() ^ surname.hashCode() ^ address.hashCode() ^ CAP.hashCode() ^ city.hashCode()
				^ phone.hashCode() ^ email.hashCode() ^ password.hashCode();
	}
}