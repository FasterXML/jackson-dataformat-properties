package com.fasterxml.jackson.dataformat.javaprop;

import com.fasterxml.jackson.core.FormatSchema;

/**
 * Simple {@link FormatSchema} sub-type that defines properties of
 * a CSV document to read or write.
 * Properties supported currently are:
 *<ul>
 *   TODO
 *</ul>
 */
public class JavaPropsSchema
    implements FormatSchema,
    java.io.Serializable
{
    private static final long serialVersionUID = 1L; // 2.5

    /*
    /**********************************************************************
    /* Simple numeric properties
    /**********************************************************************
     */

    /**
     * Specifies index number used when writing the first array entry (which
     * in Java has index of 0). After this initial value, additional elements
     * will have consecutive values, incremented by 1.
     * Note that this setting has no effect on reading: input indexes are only
     * used for sorting values, and their exact values have no meaning.
     *<p>
     * Default value is 1.
     */
    protected int _firstArrayOffset = 1;

    /*
    /**********************************************************************
    /* Formatting constants for input(-only)
    /**********************************************************************
     */

    /**
     * Whether index-notation is supported for path segments or not.
     *<p>
     * Default value is <code>true</code>, "plain" index segments are
     * supported.
     */
    protected boolean _parseIndexes = false;
    
    /*
    /**********************************************************************
    /* Formatting constants for output(-only)
    /**********************************************************************
     */
    
    /**
     * String prepended before key value, as possible indentation
     */
    protected String _lineIndentation = "";

    /**
     * String added between key and value; needs to include the "equals character"
     * (either '=' or ':', both allowed by Java Properties specification), may
     * also include white before and/or after "equals character".
     * Default value is a single '=' character with no white spaces around
     */
    protected String _lineKeyValueSeparator = "=";
    
    /**
     * String added after value, including at least one linefeed.
     * Default value is the 'Unix linefeed'.
     */
    protected String _lineEnding = "\n";
    
    /*
    /**********************************************************************
    /* Public API, FormatSchema
    /**********************************************************************
     */

    @Override
    public String getSchemaType() {
        return "JavaProps";
    }

    /*
    /**********************************************************************
    /* Public API, extended, properties
    /**********************************************************************
     */
}
