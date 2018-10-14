package edu.collegeofcharleston.csci656;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.amazonaws.services.dynamodbv2.document.Item;

public class DatabaseUtilTest {
	@Ignore
	public void testGettingMoviesForDirector() {
		List<Item> movies = DatabaseUtil.getMoviesForDirector("George Lucas");
		assertEquals(5, movies.size());
		
		// check to make sure all objects are movies
		for (Item movie : movies) {
			assertTrue(movie.getString("itemId").startsWith("movie-"));
			assertTrue(movie.getString("relatedItemId").startsWith("movie-"));
		}
	}

	@Test
	public void testFakeMovieList() {
		List<Item> movies = DatabaseUtil.getFakeMovieList(3);
		Item movie = movies.get(0);
		assertNotNull(movie.getInt("budget"));
		assertNotNull(movie.getBoolean("adult"));
		assertNotNull(movie.getString("original_title"));
		assertNotNull(movie.getFloat("popularity"));
		System.out.println(movies);
	}

	@Test
	public void testGettingMoviesForActor() {
		List<Item> movies = DatabaseUtil.getMoviesForActor("Harrison Ford");
		assertEquals(14, movies.size());
		
		// check to make sure all objects are movies
		for (Item movie : movies) {
			assertTrue(movie.getString("itemId").startsWith("movie-"));
			assertTrue(movie.getString("relatedItemId").startsWith("movie-"));
		}
	}
}
