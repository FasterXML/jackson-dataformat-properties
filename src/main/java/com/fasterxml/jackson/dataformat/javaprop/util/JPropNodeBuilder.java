package com.fasterxml.jackson.dataformat.javaprop.util;

import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.dataformat.javaprop.*;

public class JPropNodeBuilder
{
    protected final JavaPropsSchema _schema;

    protected final JPropPathSplitter _splitter;

    protected final JPropNode _root;
    
    protected JPropNodeBuilder(JavaPropsSchema schema)
    {
        _schema = schema;
        _root = new JPropNode();
        _splitter = new JPropPathSplitter(schema);
    }

    public static JPropNode build(JavaPropsSchema schema,
            Properties props)
    {
        return new JPropNodeBuilder(schema).build(props);
    }

    protected JPropNode build(Properties props)
    {
        for (Map.Entry<?,?> entry : props.entrySet()) {
            // these should be Strings; but due to possible "compromised" properties,
            // let's play safe, coerce if and as necessary
            String key = String.valueOf(entry.getKey());
            String value = String.valueOf(entry.getValue());

            addNode(key, value);
        }
        return _root;
    }

    protected void addNode(String key, String value)
    {
    }
}
