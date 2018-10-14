package edu.collegeofcharleston.csci656;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.BatchGetItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableKeysAndAttributes;
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
	
	public static List<Item> getFakeMovieList(int count) {
		ArrayList<Item> fakeMovies = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			fakeMovies.add(getFakeMovie(i));
		}
		return fakeMovies;
	}
	
	private static List<Item> getMoviesById(ArrayList<String> ids) {
		TableKeysAndAttributes attrs = new TableKeysAndAttributes(flopOrNotTable.getTableName());
		for (String id : ids) {
			attrs.addPrimaryKey(new PrimaryKey().addComponent("itemId", id).addComponent("relatedItemId", id));
		}		
		
		BatchGetItemSpec spec = new BatchGetItemSpec().withTableKeyAndAttributes(attrs);
		BatchGetItemOutcome outcome = db.batchGetItem(spec);
		Map<String, List<Item>> map = outcome.getTableItems();
		
		return map.get(flopOrNotTable.getTableName());
	}

	private static List<Item> getMoviesByPerson(String role, String id) {
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
			return getMoviesById(ids);
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

	private static List<Item> getMoviesByNameAndRole(String name, String role) {
		String id = getPersonIdByName(name);
		if (id != null) {
			return getMoviesByPerson(role, id);
		} else {
			return new ArrayList<Item>();
		}
	}

	public static List<Item> getMoviesForActor(String name) {
		return getMoviesByNameAndRole(name, "Actor");
	}

	public static List<Item> getMoviesForDirector(String name) {
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
