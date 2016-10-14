package org.taksan;

public class EntryEvent extends SkypeEntry{
    public String operation;
    public EntryEvent(SkypeEntry entry) {
        super(entry.threadId, entry.name, entry.gid);
    }
}