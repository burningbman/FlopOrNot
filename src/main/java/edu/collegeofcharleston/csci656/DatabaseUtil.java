package edu.collegeofcharleston.csci656;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;

public class DatabaseUtil {
	private static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
	private static DynamoDB db = new DynamoDB(client);
	private static Table table = db.getTable("flopOrNot");
	
	private static ArrayList<Item> createArrayFromIterator(Iterator<Item> iterator) {
		ArrayList<Item> itemList = new ArrayList<>();
		
		while(iterator.hasNext()) {
			itemList.add(iterator.next());
		}
		
		return itemList;
	}

	private static ArrayList<Item> getMoviesByPerson(String role, String id) {
		QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("itemId = :person")
				.withFilterExpression("job = :role")
				.withValueMap(new ValueMap().withString(":person", id).withString(":role", role));
		
		ItemCollection <QueryOutcome> collection = table.query(querySpec);

		return createArrayFromIterator(collection.iterator());
	}
	
	private static String getPersonIdByName(String name) {
		HashMap <String, AttributeValue> attrValues = new HashMap<>();
		attrValues.put(":id", new AttributeValue("person-"));
		//attrValues.put(":name", new AttributeValue(name));
		
		HashMap <String, String> attrNames = new HashMap<>();
		attrNames.put("#name", "name");
		
		ScanRequest scanRequest = new ScanRequest().withTableName("flopOrNot")
				.withIndexName("name")
				.withFilterExpression("begins_with(itemId, :id) and begins_with(relatedItemId, :id)")
				.withExpressionAttributeValues(attrValues);
				//.withExpressionAttributeNames(attrNames);
		ScanResult s = client.scan(scanRequest);
		List<Map<String, AttributeValue>> persons = s.getItems();
		if (persons.size() == 0) {
			return null;
		} else {
			return persons.get(0).get("itemId").getS();
		}
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

		ItemCollection<ScanOutcome> items = table.scan(scanSpec);

		Iterator<Item> iterator = items.iterator();
		while (iterator.hasNext()) {
			Item item = iterator.next();
			System.out.println(item.getString("name"));
		}
	}
}
