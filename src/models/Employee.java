package models;

import java.io.Serializable;

public class Employee extends User implements Serializable
{
	private static final long serialVersionUID = 5308368866453433054L;

	private final int ID;
	private Role role;

	public Employee(String name, String surname, String address, String CAP, String city, String phone, String email, String password, int ID, Role role)
	{
		super(name, surname, address, CAP, city, phone, email, password);

		this.ID = ID;
		this.role = role;
	}

	public int getID()
	{
		return ID;
	}

	public Role getRole()
	{
		return role;
	}

	@Override
	public int hashCode()
	{
		return super.hashCode() ^ ID;
	}
}