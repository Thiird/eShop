package controllers;

import java.time.Duration;
import java.time.Instant;
import java.util.Calendar;
import java.util.Map;
import java.util.Random;
import models.User;

public class CreateEmployee extends Controller
{
	public static void main(String[] args)
	{
		Map <String,User> employees = getUsers();

		/*
		 * if ( employees == null ) employees = new HashMap <String,User>();
		 * 
		 * Employee employee = new Employee("8", "8", "8", "8", "8", "8", "8", "8", 0,
		 * null);
		 * 
		 * employees.put(employee.getEmail(), employee);
		 */

		/*
		 * for ( String string : employees.keySet() ) {
		 * employees.get(string).setLoggedIn(false); }
		 * 
		 * setUsers(employees);
		 */

		for ( int i = 0; i < (new Random().nextInt((5 - 2) + 1) + 2); i++ )
		{

			Instant deliveryInstant = Instant.now().plus(Duration.ofDays(i));

			int hour = new Random().nextInt((18 - 9) + 1) + 9;
			int half = new Random().nextInt((1 - 0) + 1) + 1;

			System.out.println(
					deliveryInstant.toString().substring(0, 10) + " | " + hour + ":" + (half == 1 ? "30" : "00"));

			String[] a = deliveryInstant.toString().substring(0, 10).split("-");

			Calendar calendar = Calendar.getInstance();

			calendar.set(Calendar.YEAR, Integer.parseInt(a[0]));
			calendar.set(Calendar.MONTH, Integer.parseInt(a[1].replace("0", "")));
			calendar.set(Calendar.DATE, Integer.parseInt(a[2].replace("0", "")));
			calendar.set(Calendar.HOUR_OF_DAY, hour);
			calendar.set(Calendar.MINUTE, Integer.parseInt((half == 1 ? "30" : "00")));
			calendar.set(Calendar.SECOND, 0);

			// System.out.println(calendar);

			System.out.println(calendar.getTime());
			System.out.println("=============================");

		}
	}
}