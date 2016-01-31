package com.fasterxml.jackson.dataformat.javaprop;

import com.fasterxml.jackson.core.FormatSchema;
import com.fasterxml.jackson.dataformat.javaprop.util.Markers;

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

    protected final static Markers DEFAULT_INDEX_MARKER = new Markers("[", "]");

    protected final static JavaPropsSchema EMPTY = new JavaPropsSchema();
    
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
    /* Formatting constants for input and output
    /**********************************************************************
     */

    /**
     * Default path separator to use for hierarchic paths, if any; empty
     * String may be used to indicate that no hierarchy should be inferred
     * using a simple separator (although index markers may still be used,
     * if defined).
     */
    protected String _pathSeparator = ".";

    /**
     * Default start marker for index access, if any; empty String may be used
     * to indicate no marker-based index detection should be made.
     *<p>
     * Default value of "[" is usually combined with end marker of "]" to allow
     * C/Java-style bracket notation, like "settings.path[1]".
     */
    protected Markers _indexMarker = DEFAULT_INDEX_MARKER;
    
    /*
    /**********************************************************************
    /* Formatting constants for input(-only)
    /**********************************************************************
     */

    /**
     * Whether 'simple' index-notation is supported for path segments or not:
     * simple meaning that if a path segment is a textual representation of
     * a non-negative integer value with length of 9 or less (that is, up to
     * but no including one billion), it will be considered index, not property
     * name.
     *<p>
     * Note that this settings does NOT control whether "start/end marker" indicated
     * indexes are enabled or not; those depend on {@link #_indexStartMarker} and
     * {@link #_indexEndMarker}.
     *<p>
     * Default value is <code>true</code>, "plain" index segments are
     * supported.
     */
    protected boolean _parseSimpleIndexes = true;

    /*
    /**********************************************************************
    /* Formatting constants for output(-only)
    /**********************************************************************
     */

    /**
     * Whether array-element paths are written using start/end markers
     * (see {@link #_indexStartMarker}, {@link #_indexEndMarker}) or
     * "simple" index number: if set to <code>true</code> AND markers
     * are specified as non-empty Strings, will use sequence of
     *<pre>
     *   startMarker index endMarker
     *</pre>
     * to include index in path; otherwise will simply use textual representation
     * of the index number as path segment, prefixed by path separator as necessary.
     */
    protected boolean _writeIndexUsingMarkers;

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

    /**
     * Optional header to prepend before any other output: typically a
     * comment section or so. Note that contents here are
     * <b>NOT modified in any way</b>, meaning that any comment indicators
     * (leading '#' or '!') and linefeeds MUST be specified by caller.
     */
    protected String _header = "";
    
    /*
    /**********************************************************************
    /* Public API, FormatSchema
    /**********************************************************************
     */

    @Override
    public String getSchemaType() {
        return "JavaProps";
    }

    public static JavaPropsSchema emptySchema() {
        return EMPTY;
    }

    /*
    /**********************************************************************
    /* Public API, extended, properties
    /**********************************************************************
     */

    public int firstArrayOffset() {
        return _firstArrayOffset;
    }

    public String header() {
        return _header;
    }

    public Markers indexMarker() {
        return _indexMarker;
    }

    public String lineEnding() {
        return _lineEnding;
    }

    public String lineIndentation() {
        return _lineIndentation;
    }

    public String lineKeyValueSeparator() {
        return _lineKeyValueSeparator;
    }

    public boolean parseSimpleIndexes() {
        return _parseSimpleIndexes;
    }
    
    public String pathSeparator() {
        return _pathSeparator;
    }

    public boolean writeIndexUsingMarkers() {
        return _writeIndexUsingMarkers && (_indexMarker != null);
    }
}
