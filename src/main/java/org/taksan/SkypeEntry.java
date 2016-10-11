package org.taksan;

public class SkypeEntry implements Comparable{
    public String name;
    public String gid;
    
    public SkypeEntry(String name, String gid) {
        this.name = name;
        this.gid = gid;
    }

    @Override
    public int compareTo(Object o) {
        return name.compareTo(((SkypeEntry)o).name);
    }
}