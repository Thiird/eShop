package controllers;

import java.util.HashMap;
import java.util.Map;
import models.Employee;
import models.User;

public class CreateEmployee extends Controller <Employee>
{
	public static void main(String[] args)
	{
		Map <String,User> employees = getUsers();

		if ( employees == null )
			employees = new HashMap <String,User>();

		Employee employee = new Employee("8", "8", "8", "8", "8", "8", "8", "8", 0, null);

		employees.put(employee.getEmail(), employee);

		setUsers(employees);
	}
}