package com.fasterxml.jackson.dataformat.javaprop.util;

/**
 * Simple value class for encapsulating a pair of start and end markers;
 * initially needed for index markers (like "[" and "]").
 */
public class Markers
{
    protected final String _start, _end;

    public Markers(String start, String end) {
        _start = start;
        _end = end;
    }

    public String getStart() {
        return _start;
    }

    public String getEnd() {
        return _end;
    }

//    public StringBuilder appendIn(String)
}
