package edu.collegeofcharleston.csci656;

import org.junit.Test;

public class BrainTest {
	@Test
	public void calculateMovieRatingTest(){	
		String[] actors = {"Zac Efron", "Seth Rogen"};
		System.out.println(Brain.calculateMovieRating("Michael Bay", actors, 180000000));
	}

}
