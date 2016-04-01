package com.fasterxml.jackson.dataformat.javaprop.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.dataformat.javaprop.JavaPropsSchema;

/**
 * Helper class used for splitting a flattened property key into
 * nested/structured path that can be used to traverse and/or define
 * hierarchic structure.
 */
public abstract class JPropPathSplitter
{
    protected final boolean _useSimpleIndex;

    protected JPropPathSplitter(boolean useSimpleIndex) {
        _useSimpleIndex = useSimpleIndex;
    }
    
    public static JPropPathSplitter create(JavaPropsSchema schema)
    {
        // will never be null
        String sep = schema.pathSeparator();
        final Markers indexMarker = schema.indexMarker();

        // First: index marker in use?
        if (indexMarker == null) { // nope, only path separator, if anything
            // and if no separator, can use bogus "splitter":
            if (sep.isEmpty()) {
                return NonSplitting.instance;
            }
            // otherwise it's still quite simple
            return new PathOnlySplitter(sep, schema.parseSimpleIndexes());
        }
        // Yes, got index marker to use. But separator?
        if (sep.isEmpty()) {
            return new IndexOnlySplitter(schema.parseSimpleIndexes(), indexMarker);
        }
        return new IndexSplitter(sep, schema.parseSimpleIndexes(),
                indexMarker);
    }

    /**
     * Main access method for splitting key into one or more segments and using
     * segmentation to add the String value as a node in its proper location.
     * 
     * @return Newly added node
     */
    public abstract JPropNode splitAndAdd(JPropNode parent,
            String key, String value);

    /*
    /**********************************************************************
    /* Helper methods for implementations
    /**********************************************************************
     */

    protected JPropNode _addSegment(JPropNode parent, String segment)
    {
        if (_useSimpleIndex) {
            int ix = _asInt(segment);
            if (ix >= 0) {
                return parent.addByIndex(ix);
            }
        }
        return parent.addByName(segment);
    }

    protected int _asInt(String segment) {
        final int len = segment.length();
        if (len > 0) {
            for (int i = 0; i < len; ++i) {
                char c = segment.charAt(i);
                if ((c > '9') || (c < '0')) {
                    return -1;
                }
            }
            // Fine; is simple int indeed (possibly with leading zeroes)
            if (len <= 9) {
                return Integer.parseInt(segment);
            }
            if (len == 10) {
                long l = Long.parseLong(segment);
                if (l <= Integer.MAX_VALUE) {
                    return (int) l;
                }
            }
            // Out of bound, return as String (or, throw exception?)
        }
        return -1;
    }
    
    /*
    /**********************************************************************
    /* Implementations
    /**********************************************************************
     */

    /**
     * "No-op" implementation that does no splitting and simply adds entries
     * as is.
     */
    public static class NonSplitting extends JPropPathSplitter
    {
        public final static NonSplitting instance = new NonSplitting();

        private NonSplitting() { super(false); }
        
        @Override
        public JPropNode splitAndAdd(JPropNode parent,
                String key, String value)
        {
            return parent.addByName(key).setValue(value);
        }
    }

    /**
     * Simple variant where we only have path separator, and optional "segment
     * is index iff value is integer number"
     */
    public static class PathOnlySplitter extends JPropPathSplitter
    {
        protected final String _pathSeparator;
        protected final int _pathSeparatorLength;

        public PathOnlySplitter(String pathSeparator, boolean useIndex)
        {
            super(useIndex);
            _pathSeparator = pathSeparator;
            _pathSeparatorLength = pathSeparator.length();
        }

        @Override
        public JPropNode splitAndAdd(JPropNode parent,
                String key, String value)
        {
            JPropNode curr = parent;
            int start = 0;
            final int keyLen = key.length();

            while (true) {
                int ix = key.indexOf(_pathSeparator, start);
                if (ix < 0) {
                    break;
                }
                if (ix > start) { // segment before separator
                    String segment = key.substring(start, ix);
                    curr = _addSegment(curr, segment);
                }
                start = ix + _pathSeparatorLength;
                if (start == key.length()) {
                    break;
                }
            }

            if (start < keyLen) {
                if (start > 0) {
                    key = key.substring(start);
                }
                curr = _addSegment(curr, key);
            }
            curr.setValue(value);
            return curr;
        }
    }

    /**
     * Special variant that does not use path separator, but does allow
     * index indicator, at the end of path.
     */
    public static class IndexOnlySplitter extends JPropPathSplitter
    {
        protected final Pattern _indexMatch;
        
        public IndexOnlySplitter(boolean useSimpleIndex,
                Markers indexMarker)
        {
            super(useSimpleIndex);
            _indexMatch = Pattern.compile(String.format("(.*)%s(\\d{1,9})%s$",
                    Pattern.quote(indexMarker.getStart()),
                    Pattern.quote(indexMarker.getEnd())));
        }

        @Override
        public JPropNode splitAndAdd(JPropNode parent,
                String key, String value)
        {
            Matcher m = _indexMatch.matcher(key);
            // short-cut for common case of no index:
            if (!m.matches()) {
                return _addSegment(parent, key).setValue(value);
            }
            // otherwise we need recursion as we "peel" away layers
            return _splitMore(parent, m.group(1), m.group(2))
                    .setValue(value);
        }

        protected JPropNode _splitMore(JPropNode parent, String prefix, String indexStr)
        {
            int ix = Integer.parseInt(indexStr);
            Matcher m = _indexMatch.matcher(prefix);
            if (!m.matches()) {
                parent = _addSegment(parent, prefix);
            } else {
                parent = _splitMore(parent, m.group(1), m.group(2));
            }
            return parent.addByIndex(ix);
        }
    }

    public static class IndexSplitter extends JPropPathSplitter
    {
        protected final Pattern _indexMatch;
        
        public IndexSplitter(String pathSeparator, boolean useSimpleIndex,
                Markers indexMarker)
        {
            super(useSimpleIndex);
            _indexMatch = Pattern.compile(String.format
                    ("(%s)|(%s(\\d{1,9})%s)",
                            Pattern.quote(pathSeparator),
                            Pattern.quote(indexMarker.getStart()),
                            Pattern.quote(indexMarker.getEnd())));
        }

        @Override
        public JPropNode splitAndAdd(JPropNode parent,
                String key, String value)
        {
            Matcher m = _indexMatch.matcher(key);
            int start = 0;

            while (m.find()) {
                // which match did we get? Either path separator (1), or index (2)
                int ix = m.start(1);

                if (ix >= 0) { // path separator...
                    if (ix > start) {
                        String segment = key.substring(start, ix);
                        parent = _addSegment(parent, segment);
                    }
                    start = m.end(1);
                    continue;
                }
                // no, index marker, with contents
                ix = m.start(2);
                if (ix > start) {
                    String segment = key.substring(start, ix);
                    parent = _addSegment(parent, segment);
                }
                start = m.end(2);
                ix = Integer.parseInt(m.group(3));
                parent = parent.addByIndex(ix);
            }
            if (start < key.length()) {
                String segment = (start == 0) ? key : key.substring(start);
                parent = _addSegment(parent, segment);
            }
            return parent.setValue(value);
        }
    }

    /*
    public static void main(String[] args) throws Exception
    {
        String p1 = Pattern.quote("[");
        String p2 = Pattern.quote("->");

        Pattern p = Pattern.compile("("+p1+")|("+p2+")");

        Matcher m = p.matcher("path.and[2]->foo");
        System.out.println("Pattern == "+p);
        if (!m.find()) {
            throw new Error("Not found!");
        }
        System.out.println("Matches: main="+m.group(0)+" , 1='"+m.group(1)+"' vs 2='"+m.group(2)+"'");
        System.out.println(" starts: 1="+m.start(1)+", 2="+m.start(2)+" vs 0 = "+m.start());
    }
    */
}
