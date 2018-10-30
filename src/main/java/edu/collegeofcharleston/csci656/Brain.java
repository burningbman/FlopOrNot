package edu.collegeofcharleston.csci656;

import java.util.List;

import com.amazonaws.services.dynamodbv2.document.Item;

public class Brain {
	public static int calculateMovieRating(String director, String[] actors, int budget) {

		/* 
		 * Begin
		2: Function rate_movie: Calls the function get_user_input( ) and stores the user input
		3: Open the historical data set csv file
		4: Iterate through each row of the historical data set
		5: experience_factor( ) calculates the number of hits are equivalent to the number of movies done by cast
		so far
			6: dir_imdb_score, a1_imdb_score, a2_imdb_score are three lists which store the imdb scores of all the
		movies done by the actors and director of the movie title which needs success prediction
//		7: dir_high_imdb_score, a1_high_imdb_score, a2_high_imdb_score are lists which store the imdb rates
		of the same director and actors respectively but the difference being the only those imdb scores are
		stored that are above 7.8.// Thus great artist factor comes into play. imdb_weight( ) calculates the
		weights
		
		8: fame_rating( ) calculates the fam_factor by taking the number of voted users, number of critic reviews,
		number of user reviews, cast facebook likes, movie facebook likes together and normalizing it on a
		scale of {0.0...1.0}
		9: profit( ) calculates Profit = Gross - Budget, for each movie in the historical data set this is calculated
		10: profit_scale ( ) scales down the profit to a range of {0.0...1.0}.
		11: Set A= max(profit) , B=min(profit)
		12: Set: profit=(0.1 + (profit-A)*(1.0-0.1)/(B-A))
		13: Set imdb_factor for each actor and the directors to (average_score/10.0)*imdb_weight
		14: Set imdb_rate to average of imdb factors of each artist
		15: Set experience_rate to average to experience_factors of actors and director
		16: movie_rating equals to average of all above calculated factors multiplied by respective weights and
		scaled up to a value between 1-10
		17: Suggest ( ) generates suggestions according to the user input movie.
		18: Display suggestions
		19: Display movie_rating
		*/
		
		//calculate total experience: line 5 in pseudo code
		int totalExperience=0;
		for(int i =0; i<actors.length;i++){
			totalExperience += totalExperience + experienceFactor(actors[i]);
		}
		
		//line 6 pseudo code
		List<Double> dir_high_imdb_score = listOfhighestIMDBScores(director,"Director");
		List<Double> a1_high_imdb_score = listOfhighestIMDBScores(actors[0], "Actor");
		List<Double> a2_high_imdb_score = listOfhighestIMDBScores(actors[1], "Actor");
		
		
		
		return 12;
	}
	/*
	 * experienceFactor(String actor)
	 * returns how many movies an actor has been in
	 */
	
	public static int experienceFactor(String actor){
		//List<Item> actorsMovieList = DatabaseUtil.getMoviesForActor(actor);
		
		int randNum = (int)(Math.random() * 10); //for testing
		
		List<Item> actorsMovieList =  DatabaseUtil.getFakeMovieList(randNum);//for testing
		return actorsMovieList.size();
	}
	
	/*
	 * listOfIMDBScores(String person, String role)
	 * returns a list of imdb scores of the movies that the person was in. 
	 */
	
	public static List<Double> listOfhighestIMDBScores(String person, String role){
		List<Double> imdbScoreList = null;
		List<Item> movieList=null;
		int randNum = (int)(Math.random() * 10); //for testing
		if(role=="Director"){
			//movieList =  DatabaseUtil.getMoviesForDirector(person);
			movieList =  DatabaseUtil.getFakeMovieList(randNum);//for testing
		}else{
			//movieList =  DatabaseUtil.getMoviesForActor(actor);					
			movieList =  DatabaseUtil.getFakeMovieList(randNum); //for testing
		}
		for(int i=0; i<movieList.size();i++){
			if((Double) movieList.get(i).get("imdbRating")>=7.8)
				imdbScoreList.add((Double) movieList.get(i).get("imdbRating"));
		}
		return imdbScoreList;
	}
	
	
}
