package org.taksan;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import spark.Spark;

public class SkypeDirectory {
    public static Map<String, SkypeEntry> groups = new HashMap<>();
        
	public static void main(String[] args) throws JsonSyntaxException, IOException {
	    Spark.webSocket("/wsocket", NotificationCenterHandler.class);
	    Gson gson = new GsonBuilder().setPrettyPrinting().create();
	    
	    File file = new File("directory.json");
	    if (file.exists()) {
	        Type typeOfHashMap = new TypeToken<Map<String, SkypeEntry>>() { }.getType();
	        groups = gson.fromJson(FileUtils.readFileToString(file), typeOfHashMap);
	    }
	    
	    get("/groups/", (req, res) -> {
	        return (groups);
        }, new JsonTransformer());
	    
		get("/groups/:threadId", (req, res) -> {
		    if (groups.get(req.params(":threadId")) == null)
		        return new Object();
		    return groups.get(req.params(":threadId"));
		}, new JsonTransformer());
		
        post("/groups/", "application/json", (req, res) -> {
            SkypeEntry entry = gson.fromJson(req.body(), SkypeEntry.class);
            groups.put(entry.threadId, entry);
            save();
            NotificationCenterHandler.notifyGroupAdded(entry);
            
            return entry;
        }, new JsonTransformer());
        
        put("/groups/:threadId", "application/json", (req, res) -> {
            SkypeEntry data = gson.fromJson(req.body(), SkypeEntry.class);
            SkypeEntry updatedEntry = groups.get(req.params(":threadId"));
            if (updatedEntry != null)
                updatedEntry.name = data.name;
            save();
            NotificationCenterHandler.notifyGroupUpdated(updatedEntry);
            return "";
        });
        
        delete("/groups/:threadId", (req, res) -> {
            SkypeEntry removedEntry = groups.get(req.params(":threadId"));
            groups.remove(req.params(":threadId"));
            save();
            NotificationCenterHandler.notifyGroupRemoved(removedEntry);
            return "";
        }, new JsonTransformer());
    }
	
	private static void save() {
	    Gson gson = new GsonBuilder().setPrettyPrinting().create();
	    String json = gson.toJson(groups);
	    
	    try {
            FileUtils.writeStringToFile(new File("directory.json"), json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
	}
}
