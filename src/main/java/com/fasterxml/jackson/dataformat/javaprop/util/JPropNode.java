package com.fasterxml.jackson.dataformat.javaprop.util;

import java.util.*;

/**
 * Value in an ordered tree presentation built from an arbitrarily ordered
 * set of flat input values
 */
public class JPropNode
{
    /**
     * Value for the path, for leaf nodes; usually null for branches.
     * If both children and value exists, typically need to construct
     * bogus value with empty String key.
     */
    protected String _value;

    /**
     * Child entries with integral number index, if any.
     */
    protected Map<Integer, JPropNode> _byIndex;

    /**
     * Child entries accessed with String property name, if any.
     */
    protected Map<String, JPropNode> _byName;
}
