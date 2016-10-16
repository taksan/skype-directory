package org.taksan;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import spark.Spark;

public class SkypeDirectory {
    private static Directory groups;
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
        
	public static void main(String[] args) throws JsonSyntaxException, IOException {
	    JsonTransformer toJson = new JsonTransformer();
	    groups = Directory.loadStoredGroups();
	    
	    Spark.webSocket("/wsocket", NotificationCenterHandler.class);
	    
        get("/groups/", (req, res) -> 
            groups, 
            toJson);
	    
		get("/groups/:threadId", (req, res) -> 
		    groups.get(req.params(":threadId")), 
		    toJson);
		
        post("/groups/", "application/json", (req, res) -> 
            groups.put(parseEntry(req.body())), 
            toJson);
        
        put("/groups/:threadId", "application/json", (req, res) -> 
            groups.updateKey(req.params(":threadId"), parseEntry(req.body())), 
            toJson);
        
        delete("/groups/:threadId", (req, res) -> 
            groups.remove(req.params(":threadId")), 
            toJson);
    }

    private static SkypeEntry parseEntry(String json) {
        return gson.fromJson(json, SkypeEntry.class);
    }
}
