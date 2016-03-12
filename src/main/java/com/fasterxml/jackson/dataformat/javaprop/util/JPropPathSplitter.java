package com.fasterxml.jackson.dataformat.javaprop.util;

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
        String sep = schema.pathSeparator();
        if (sep == null) {
            sep = "";
        }
        
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
        // Yes, got index marker to use. But how about separator?
        if (sep.isEmpty()) { // no separator, just index. Unusual...
            
        } else { // both separator and index
            
        }

        // !!! TBI
        throw new IllegalStateException("Handling of Indexes not yet supported");
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
            for (int i = 1; i < len; ++i) {
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
                int ix = key.indexOf(_pathSeparator);
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
