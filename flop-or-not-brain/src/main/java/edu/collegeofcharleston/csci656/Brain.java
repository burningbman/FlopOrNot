package edu.collegeofcharleston.csci656;

import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;
import com.amazonaws.services.dynamodbv2.document.Item;

public class Brain {
	public static JSONObject calculateMovieRating(String director, String[] actors, int budget) {
		/*
		 * Begin 
		 * 2: Function rate_movie: Calls the function get_user_input( ) and stores the user input 
		 * 3: Open the historical data set csv file 
		 * 4: Iterate through  each row of the historical data set 
		 * 5: experience_factor( ) calculates the  number of hits are equivalent to the number of movies done by cast so far 
		 * 6: dir_imdb_score, a1_imdb_score, a2_imdb_score are three lists which store the  imdb scores of all the movies done
		 * by the actors and director of the movie  title which needs success prediction
		 * ----not using-----> 7: dir_high_imdb_score, a1_high_imdb_score, a2_high_imdb_score are lists which  store the imdb
		 * rates of the same director and actors respectively but the difference being the only those imdb scores are stored
		 * that are above 7.8.//  Thus great artist factor comes into play. imdb_weight( ) calculates the weights
		 * 
		 * 8: fame_rating( ) calculates the fam_factor by taking the number of voted
		 * users, number of critic reviews, number of user reviews, cast facebook likes,
		 * movie facebook likes together and normalizing it on a scale of {0.0...1.0}.
		 * 9: profit( ) calculates Profit = Gross - Budget, for each movie in the
		 * historical data set this is calculated 
		 * 10: profit_scale ( ) scales down the profit to a range of {0.0...1.0}.
		 * 11: Set A= max(profit) , B=min(profit)
		 * 12: Set: profit=(0.1 + (profit-A)*(1.0-0.1)/(B-A))
		 * 13: Set imdb_factor for each actor and the directors to (average_score/10.0)*imdb_weight
		 * 14: Set imdb_rate to average of imdb factors of each artist
		 * 15: Set experience_rate to average to experience_factors of actors and director
		 * 16: movie_rating equals to average of all above calculated factors multiplied by respective weights and
		 * scaled up to a value between 1-10
		 * 17: Suggest ( ) generates suggestions according to the user input movie.
		 * 18: Display suggestions
		 * 19: Display movie_rating
		 */

		List<Item> a1MovieList = DatabaseUtil.getMoviesForActor(actors[0]);
		List<Item> a2MovieList = DatabaseUtil.getMoviesForActor(actors[1]);
		List<Item> dirMovieList = DatabaseUtil.getMoviesForDirector(director);

		// line 5 pseudo code: TOTAL EXPERIENCE:
		int a1Experience = a1MovieList.size();
		int a2Experience =a2MovieList.size();
		int totalExperience =  a1Experience+ a2Experience;
		double avgExperience = (double) totalExperience / 2;

		// line 6 pseudo code: IMDB SCORES
		List<Double> dirIMDBScore = listOfIMDBScores(dirMovieList);
		List<Double> a1IMDBScore = listOfIMDBScores(a1MovieList);
		List<Double> a2IMDBScore = listOfIMDBScores(a2MovieList);

		double avgIMDBScores = averageIMDBScore(dirIMDBScore, a1IMDBScore, a2IMDBScore);

		// line 8 of pseudo code: POPULARITY
		float dirPopularity = popularityOfPerson(director);
		float a1Popularity = popularityOfPerson(actors[0]);
		float a2Popularity = popularityOfPerson(actors[1]);

		double avgPopularity = (dirPopularity + a1Popularity + a2Popularity) / 3;

		// line 9 pseudo code: PROFITS
		List<Double> dirBudgetList = listOfMovieBudgets(dirMovieList);
		List<Double> a1BudgetList = listOfMovieBudgets(a1MovieList);
		List<Double> a2BudgetList = listOfMovieBudgets(a2MovieList);

		double avgBudget = averageBudget(dirBudgetList, a1BudgetList, a2BudgetList);

		double rating = getRating(avgExperience, avgIMDBScores, avgPopularity, avgBudget, budget);

		return jsonRating(rating);

	}

	@SuppressWarnings("unchecked")
	private static JSONObject jsonRating(double rating) {
		JSONObject json = new JSONObject();

		if (rating >= 0 && rating <= 2.9) {
			json.put("wordRating", "FLOP");
			json.put("numberRating", rating);
			return json;
		}
		else if (rating > 2.9 && rating <= 4.9) {
			json.put("wordRating", "BAD");
			json.put("numberRating", rating);
			return json;
		}
		else if (rating > 4.9 && rating <= 5.9) {
			json.put("wordRating", "WATCHABLE");
			json.put("numberRating", rating);
			return json;
		}
		else if (rating > 5.9 && rating <= 6.9) {
			json.put("wordRating", "DECENT");
			json.put("numberRating", rating);
			return json;

		}
		else if (rating > 6.9 && rating <= 7.9) {
			json.put("wordRating", "GOOD");
			json.put("numberRating", rating);
			return json;
		}
		else if (rating > 7.9 && rating <= 8.9) {
			json.put("wordRating", "GREAT");
			json.put("numberRating", rating);
			return json;
		} else {
			json.put("wordRating", "AMAZING");
			json.put("numberRating", rating);
			return json;
		}
	}

	/*
	 * listOfIMDBScores(String person, String role) returns a list of imdb scores of
	 * the movies that the person was in.
	 */

	public static List<Double> listOfIMDBScores(List<Item> movieList) {
		List<Double> imdbScoreList = new ArrayList<>();
		List<Item> moviesToRemove = new ArrayList<>();

		for (int i = 0; i < movieList.size(); i++) {
			Item movie = movieList.get(i);
			try {
				imdbScoreList.add(movie.getDouble("imdbRating"));
			} catch (Exception e) {
				moviesToRemove.add(movie);
			}
		}

		// remove the movies that had an issue
		movieList.removeAll(moviesToRemove);

		return imdbScoreList;
	}

	/*
	 * averageIMDBScore returns the average imdb score of the director and two
	 * actors
	 */
	public static double averageIMDBScore(List<Double> dirIMDBScore, List<Double> a1imdbScore,
			List<Double> a2imdbScore) {
		int movieCount = 0;
		int score = 0;
		for (int i = 0; i < dirIMDBScore.size(); i++) {
			score += dirIMDBScore.get(i);
			movieCount++;
		}
		for (int i = 0; i < a1imdbScore.size(); i++) {
			score += a1imdbScore.get(i);
			movieCount++;
		}
		for (int i = 0; i < a2imdbScore.size(); i++) {
			score += a2imdbScore.get(i);
			movieCount++;
		}

		double avgScore = score / movieCount;

		return avgScore;
	}

	/*
	 * popularityOfPerson() returns a Double that determines the person's
	 * popularity/fame level
	 */
	public static float popularityOfPerson(String name) {
		// actually get popularity getPerson (string name) returns Item;

		Float popularity = DatabaseUtil.getPerson(name).getFloat("popularity");

		return popularity;
	}

	/*
	 * listOfMovieProfits returns a list of Doubles that are the profits of the
	 * movies from either the director or actor
	 */
	public static List<Double> listOfMovieBudgets(List<Item> movieList) {
		List<Double> budgetList = new ArrayList<>();
		for (int i = 0; i < movieList.size(); i++) {
			
			Double budget = movieList.get(i).getDouble("budget");

			budgetList.add(budget);
		}

		return budgetList;
	}

	/*
	 * averageProfit returns the average of the profits of the director, and actors
	 */
	public static double averageBudget(List<Double> dirList, List<Double> a1List, List<Double> a2List) {
		int movieCount = 0;
		int profit = 0;
		for (int i = 0; i < dirList.size(); i++) {
			profit += dirList.get(i);
			movieCount++;
		}
		for (int i = 0; i < a1List.size(); i++) {
			profit += a1List.get(i);
			movieCount++;
		}
		for (int i = 0; i < a2List.size(); i++) {
			profit += a2List.get(i);
			movieCount++;
		}

		double avgProfit = profit / movieCount;
		return avgProfit;
	}

	/*
	 * getRating gives the final rating score to return to controller
	 */
	private static double getRating(double avgExperience, double avgIMDBScores, double avgPopularity, double avgBudget,
			int budgetProvided) {
		double experienceWeight = 0.13;
		double IMDBWeight = 0.25;
		double popularityWeight = 0.55; // #1
		double budgetWeight = 0.08;

		double expScaled = avgExperience/23; // highest number in DB
		double IMDBScaled = avgIMDBScores/10;
		double popScaled = avgPopularity/10; // actor popularity
		double budScaled = budgetProvided/avgBudget;

		if (expScaled > 1)
			expScaled = 1;
		if (IMDBScaled > 1)
			IMDBScaled = 1;
		if (popScaled > 1)
			popScaled = 1;
		if (budScaled > 1)
			budScaled = 1;

		double rating = ((expScaled * experienceWeight) + (IMDBScaled * IMDBWeight) + (popScaled * popularityWeight)
				+ (budScaled * budgetWeight)) * 10;

		return rating;
	}
}
