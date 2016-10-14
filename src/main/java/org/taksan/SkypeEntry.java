package org.taksan;

public class SkypeEntry implements Comparable{
    public String name;
    public String gid;
    public String threadId;
    
    public SkypeEntry(String threadId, String name, String gid) {
        this.threadId = threadId;
        this.name = name;
        this.gid = gid;
    }

    @Override
    public int compareTo(Object o) {
        return name.compareTo(((SkypeEntry)o).name);
    }
}