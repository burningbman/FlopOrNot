package edu.collegeofcharleston.csci656;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.BatchGetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;

public class DatabaseUtil {
	private static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
	private static DynamoDB db = new DynamoDB(client);
	private static Table flopOrNotTable = db.getTable("flopOrNot");
	
	public static Item getFakeActor(int id) {
		float popularity = (float) (Math.random() * 16);
		return new Item()
				.withString("itemId", "person-" + id)
				.withString("relatedItemId", "person-" + id)
				.withInt("id", id)
				.withFloat("popularity", popularity)
				.withString("imdb_id", "nm" + id);
	}
	
	public static Item getFakeMovie(int id) {
		float popularity = (float) (Math.random() * 16);
		int budget = (int) (Math.random() * 200000000);
		return new Item()
				.withString("itemId", "movie-" + id)
				.withString("relatedItemId", "movie-" + id)
				.withBoolean("adult", false)
				.withInt("id", id)
				.withFloat("popularity", popularity)
				.withString("imdb_id", "tt" + id)
				.withInt("budget", budget)
				.withString("original_title", "Movie_" + id);
	}
	
	public static ArrayList<Item> getFakeMovieArray(int count) {
		ArrayList<Item> fakeMovies = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			fakeMovies.add(getFakeMovie(i));
		}
		return fakeMovies;
	}
	
	private static ArrayList<Item> getMoviesById(String personId, ArrayList<String> ids) {
		ValueMap valueMap = new ValueMap();
		String query = "itemId = :id0";
		valueMap.put(":id0", ids.get(0));
		for (int i = 1; i < ids.size(); i++) {
			String value = ":id" + i;
			query += "OR itemId = " + value;
			valueMap.put(value, ids.get(i));
		}
//		BatchGetItemSpec spec = new BatchGetItemSpec();
//		spec.
		//db.bat
		
		QuerySpec querySpec = new QuerySpec().withKeyConditionExpression(query)
				.withValueMap(valueMap)
				.withFilterExpression("relatedItemId");
		
		ItemCollection<QueryOutcome> collection = flopOrNotTable.query(querySpec);
		Iterator<Item> iterator = collection.iterator();
		
		while (iterator.hasNext()) {
			Item item = iterator.next();
			String relatedItemId = item.getString("relatedItemId");

		}
		
		return new ArrayList<Item>();
	}

	private static ArrayList<Item> getMoviesByPerson(String role, String id) {
		QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("itemId = :person")
				.withFilterExpression("job = :role")
				.withValueMap(new ValueMap().withString(":person", id).withString(":role", role));

		ItemCollection<QueryOutcome> collection = flopOrNotTable.query(querySpec);
		Iterator<Item> iterator = collection.iterator();

		ArrayList<String> ids = new ArrayList<>();

		while (iterator.hasNext()) {
			Item item = iterator.next();
			String relatedItemId = item.getString("relatedItemId");
			if (relatedItemId != id) {
				ids.add(relatedItemId);
			}
		}

		if (ids.size() > 0) {
			return getMoviesById(id, ids);
		} else {
			return new ArrayList<Item>();
		}
	}

	private static String getPersonIdByName(String name) {
		HashMap<String, String> attrNames = new HashMap<>();
		attrNames.put("#name", "name");

		QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("#name = :name").withMaxResultSize(1)
				.withNameMap(attrNames).withValueMap(new ValueMap().withString(":name", name));

		ItemCollection<QueryOutcome> items = flopOrNotTable.getIndex("name-index").query(querySpec);

		String id = null;
		Iterator<Item> iterator = items.iterator();
		while (iterator.hasNext()) {
			Item item = iterator.next();
			id = item.getString("itemId");
		}
		return id;
	}

	private static ArrayList<Item> getMoviesByNameAndRole(String name, String role) {
		String id = getPersonIdByName(name);
		if (id != null) {
			return getMoviesByPerson(role, id);
		} else {
			return new ArrayList<Item>();
		}
	}

	public static ArrayList<Item> getMoviesForActor(String name) {
		return getMoviesByNameAndRole(name, "Actor");
	}

	public static ArrayList<Item> getMoviesForDirector(String name) {
		return getMoviesByNameAndRole(name, "Director");
	}

	public static void getPersons() {
		ScanSpec scanSpec = new ScanSpec()
				.withFilterExpression("begins_with(itemId, :id) and begins_with(relatedItemId, :id)")
				.withValueMap(new ValueMap().withString(":id", "person-"));

		ItemCollection<ScanOutcome> items = flopOrNotTable.scan(scanSpec);

		Iterator<Item> iterator = items.iterator();
		while (iterator.hasNext()) {
			Item item = iterator.next();
			System.out.println(item.getString("name"));
		}
	}
}
