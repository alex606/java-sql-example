package com.example.ServerSocket;

import com.mongodb.client.*;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MongoDbPersistenceService {

    private final MongoClient mongoClient;
    private final MongoDatabase mongoDatabase;

    private final String isCheckedOut = "isCheckedOut";
    private final String checkedOutBy = "checkedOutBy";
    private final String lastCheckoutDate = "lastCheckoutDate";

    public MongoDbPersistenceService() {
        this.mongoClient = MongoClients.create(
                "mongodb+srv://alexwu606:" + System.getenv("dbPassword") + "@cluster0.oqyciog.mongodb.net/?retryWrites=true&w=majority"
        );
        this.mongoDatabase = mongoClient.getDatabase("LIBRARY_DATABASE");
    }

    public void insertItem() {
        Document document1 = new Document("id", 1)
                .append("itemType", "book")
                .append("title", "Green Eggs and ham")
                .append("author", "Dr. Suess")
                .append("pages", 17)
                .append("summaryDescription", "A Dr. Suess Classic");

        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("libraryItems");
        mongoCollection.insertOne(document1);
    }

    public void closeConnection() {
        this.mongoClient.close();
    }

    public Document findDocument(Document document) {
        return getLibraryCollection().find(document).first();
    }

    public List<String> getAllItems() {
        MongoCursor<Document> cursor = getLibraryCollection().find().iterator();
        List<String> items = new ArrayList<>();
        try {
            while (cursor.hasNext()) {
                items.add(cursor.next().toJson());
            }
        } finally {
            cursor.close();
        }
        return items;
    }

    public List<String> checkOutItem(Document filter, String customer) {

        Document documentToFind = getLibraryCollection().find(filter).first();

        if (documentToFind == null) {
            System.out.println("The library item does not exist");
            return getAllItems();
        }

        if (documentToFind.containsKey(isCheckedOut) && documentToFind.getBoolean(isCheckedOut)) {
            System.out.println("The item is currently checked out by: " + documentToFind.getString(checkedOutBy));
            return getAllItems();
        }

        Document update = new Document("$set", new Document()
                .append(isCheckedOut, true)
                .append(checkedOutBy, customer)
                .append(lastCheckoutDate, new Date())
        );
        getLibraryCollection().updateOne(documentToFind, update);
        return getAllItems();
    }

    public List<String> checkInItem(Document filter, String libraryCustomer) {

        Document documentToFind = getLibraryCollection().find(filter).first();

        if (documentToFind == null) {
            System.out.println("The library item does not exist");
            return getAllItems();
        }

        if (documentToFind.containsKey(isCheckedOut) && !documentToFind.getBoolean(isCheckedOut)) {
            System.out.println("The item is currently already checked in");
            return getAllItems();
        }

        if (documentToFind.containsKey(checkedOutBy)
                && !Objects.equals(documentToFind.getString(checkedOutBy), libraryCustomer)) {
            System.out.println("The item is currently checked out by a different customer");
            return getAllItems();
        }

        Document update = new Document()
                .append("$set", new Document(isCheckedOut, false))
                .append("$unset", new Document(checkedOutBy, libraryCustomer));

        getLibraryCollection().updateOne(documentToFind, update);
        return getAllItems();
    }

    private MongoCollection<Document> getLibraryCollection() {
        return this.mongoDatabase.getCollection("libraryItems");
    }
}
