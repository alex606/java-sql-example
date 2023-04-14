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
    private final MongoDatabase users;

    private final String isCheckedOut = "isCheckedOut";
    private final String checkedOutBy = "checkedOutBy";
    private final String lastCheckoutDate = "lastCheckoutDate";

    public MongoDbPersistenceService() {
        this.mongoClient = MongoClients.create(
                "mongodb+srv://alexwu606:" + System.getenv("dbPassword") + "@cluster0.oqyciog.mongodb.net/?retryWrites=true&w=majority"
        );
        this.mongoDatabase = mongoClient.getDatabase("LIBRARY_DATABASE");
        this.users = mongoClient.getDatabase("LIBRARY_USERS");
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

    public List<String> checkOutItem(Document filter, String customer, Message message) {

        Document documentToFind = getLibraryCollection().find(filter).first();

        if (documentToFind == null) {
            message.setErrorMessage("The library item does not exist");
            System.out.println("The library item does not exist");
            return getAllItems();
        }

        if (documentToFind.containsKey(isCheckedOut) && documentToFind.getBoolean(isCheckedOut)) {
            message.setErrorMessage("The item is currently checked out by: " + documentToFind.getString(checkedOutBy));
            System.out.println("The item is currently checked out by: " + documentToFind.getString(checkedOutBy));
            return getAllItems();
        }

        Document update = new Document("$set", new Document()
                .append(isCheckedOut, true)
                .append(checkedOutBy, customer)
                .append(lastCheckoutDate, new Date())
        );
        getLibraryCollection().updateOne(documentToFind, update);

        Document user = getLibraryUsers().find(new Document("name", customer)).first();
        if (user == null) {
            getLibraryUsers().insertOne(new Document()
                    .append("name", customer)
                    .append("checkedOut", List.of(documentToFind.getString("title")))
            );
        } else {
            Document updateUser = new Document(
                    "$push", new Document("checkedOut", documentToFind.getString("title"))
            );
            getLibraryUsers().updateOne(user, updateUser);
        }
        return getAllItems();
    }

    public List<String> checkInItem(Document filter, String libraryCustomer, Message message) {

        Document documentToFind = getLibraryCollection().find(filter).first();

        if (documentToFind == null) {
            message.setErrorMessage("The library item does not exist");
            System.out.println("The library item does not exist");
            return getAllItems();
        }

        if (documentToFind.containsKey(isCheckedOut) && !documentToFind.getBoolean(isCheckedOut)) {
            message.setErrorMessage("The item is currently already checked in");
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

        Document user = new Document("name", libraryCustomer);
        Document updateUser = new Document(
                "$pull", new Document("checkedOut", documentToFind.getString("title"))
        );
        getLibraryUsers().updateOne(user, updateUser);

        return getAllItems();
    }

    private MongoCollection<Document> getLibraryCollection() {
        return this.mongoDatabase.getCollection("libraryItems");
    }

    private MongoCollection<Document> getLibraryUsers() {
        return this.users.getCollection("users");
    }
}
