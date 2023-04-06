package com.example.ServerSocket;


import java.io.Serializable;
import java.util.List;

public class Message implements Serializable {
    private int id;
    private String type;
    private String data;
    private List<String> collection;

    public Message() {
    }

    public Message(int id, String type, String data, List<String> collection) {
        this.id = id;
        this.type = type;
        this.data = data;
        this.collection = collection;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List<String> getCollection() {
        return collection;
    }

    public void setCollection(List<String> collection) {
        this.collection = collection;
    }
}
