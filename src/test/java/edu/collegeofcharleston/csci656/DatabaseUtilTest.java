package edu.collegeofcharleston.csci656;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import org.junit.Ignore;
import org.junit.Test;

import com.amazonaws.services.dynamodbv2.document.Item;

public class DatabaseUtilTest {
	@Test
	@Ignore
	public void testGettingMoviesForDirector() {
		ArrayList<Item> movies = DatabaseUtil.getMoviesForDirector("George Lucas");
		assertEquals(5, movies.size());
		for (Item movie : movies) {
			System.out.println(movie.get("original_title"));
		}
	}
	
	@Test
	public void testFakeMovieList() {
		ArrayList <Item> movies = DatabaseUtil.getFakeMovieArray(3);
		Item movie = movies.get(0);
		assertNotNull(movie.getInt("budget"));
		assertNotNull(movie.getBoolean("adult"));
		assertNotNull(movie.getString("original_title"));
		assertNotNull(movie.getFloat("popularity"));
		System.out.println(movies);
	}
	
	@Test
	@Ignore
	public void testGettingMoviesForActor() {
		ArrayList<Item> movies = DatabaseUtil.getMoviesForActor("Harrison Ford");
		assertEquals(14, movies.size());
	}
}
