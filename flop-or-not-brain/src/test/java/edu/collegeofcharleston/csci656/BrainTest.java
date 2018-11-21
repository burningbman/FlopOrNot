package edu.collegeofcharleston.csci656;

import org.junit.Test;

public class BrainTest {
	@Test
	public void calculateMovieRatingTest(){	
		String[] actors = {"Tom Cruise", "Morgan Freeman"};
		System.out.println(Brain.calculateMovieRating("Steven Spielberg", actors, 120000000));
	}

}