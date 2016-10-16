package org.taksan;

import static spark.Spark.after;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import spark.Request;
import spark.Spark;

public class SkypeDirectory {
    private static Map<String, SkypeEntry> groups = new ConcurrentHashMap<>();
    private static Gson gson;
        
	public static void main(String[] args) throws JsonSyntaxException, IOException {
	    Spark.webSocket("/wsocket", NotificationCenterHandler.class);
	    gson = new GsonBuilder().setPrettyPrinting().create();
	    
	    loadStoredGroups();
	    
	    get("/groups/", (req, res) -> groups, new JsonTransformer());
	    
		get("/groups/:threadId", (req, res) -> groups.get(req.params(":threadId")), new JsonTransformer());
		
        post("/groups/", "application/json", (req, res) -> {
            SkypeEntry entry = gson.fromJson(req.body(), SkypeEntry.class);
            groups.put(entry.threadId, entry);
            NotificationCenterHandler.notifyGroupAdded(entry);
            
            return entry;
        }, new JsonTransformer());
        
        put("/groups/:threadId", "application/json", (req, res) -> {
            SkypeEntry data = gson.fromJson(req.body(), SkypeEntry.class);
            SkypeEntry updatedEntry = groups.get(req.params(":threadId"));
            if (updatedEntry != null)
                updatedEntry.name = data.name;
            NotificationCenterHandler.notifyGroupUpdated(updatedEntry);
            return "";
        });
        
        delete("/groups/:threadId", (req, res) -> {
            SkypeEntry removedEntry = groups.get(req.params(":threadId"));
            groups.remove(req.params(":threadId"));
            NotificationCenterHandler.notifyGroupRemoved(removedEntry);
            return "";
        }, new JsonTransformer());
        
        after((req,res) -> save(req));
    }
	
    private static void loadStoredGroups() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File("directory.json");
	    if (file.exists()) {
	        Type typeOfHashMap = new TypeToken<Map<String, SkypeEntry>>() { }.getType();
	        groups = gson.fromJson(FileUtils.readFileToString(file), typeOfHashMap);
	    }
    }
	
	private static void save(Request req) {
	    if (req.requestMethod().equals("GET")) 
	        return;
	    
	    Gson gson = new GsonBuilder().setPrettyPrinting().create();
	    String json = gson.toJson(groups);
	    
	    try {
            FileUtils.writeStringToFile(new File("directory.json"), json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
	}
}
