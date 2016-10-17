package org.taksan;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@SuppressWarnings("serial")
public class Directory extends HashMap<String, SkypeEntry>{
    public static Directory loadStoredGroups() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File("directory.json");
        if (file.exists()) 
            return gson.fromJson(FileUtils.readFileToString(file), Directory.class);
        return new Directory();
    }
    
    public synchronized SkypeEntry put(SkypeEntry value) {
        if (value.threadId.equals("")) 
            throw new RuntimeException("threadId cannot be empty");
        
        SkypeEntry entry = super.put(value.threadId, value);
        save();
        NotificationCenterHandler.notifyGroupAdded(get(value.threadId));
        return entry;
    }
    
    public synchronized SkypeEntry updateKey(String key, SkypeEntry entry) {
        SkypeEntry updatedEntry = get(key);
        if (updatedEntry == null) return null;
        
        updatedEntry.name = entry.name;
        updatedEntry.gid  = entry.gid;
        save();
        NotificationCenterHandler.notifyGroupUpdated(updatedEntry);
        
        return updatedEntry;
    }
    
    @Override
    public synchronized SkypeEntry remove(Object key) {
        SkypeEntry entry = super.remove(key);
        save();
        NotificationCenterHandler.notifyGroupRemoved(entry);
        return entry;
    }
    
    private void save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(this);
        
        try {
            FileUtils.writeStringToFile(new File("directory.json"), json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
