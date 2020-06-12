package main;

import java.util.Map;
import controllers.Controller;
import models.Employee;
import models.User;

public class CreateEmployee extends Controller
{
	public static void main(String[] args)
	{
		Map <String,User> users = getUsers();

		//Create employee account
		  
		 Employee employee = new Employee("8", "8", "8", "8", "8", "8", "8", "8", 0, null);
		 users.put(employee.getEmail(), employee);
		 

		//Logout all the users
		for ( String string : users.keySet() )
		{
			users.get(string).setLoggedIn(false);
		}
		
		

		setUsers(users);
	}
}