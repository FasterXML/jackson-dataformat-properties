package com.fasterxml.jackson.dataformat.javaprop.util;

import java.util.*;

/**
 * Value in an ordered tree presentation built from an arbitrarily ordered
 * set of flat input values. Since either index- OR name-based access is to
 * be supported (similar to, say, Javascript objects) -- but only one, not both --
 * storage is bit of a hybrid. In addition, branches may also have values.
 * So, code does bit coercion as necessary, trying to maintain something
 * consistent and usable at all times, without failure.
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

    protected boolean _hasContents = false;
    
    public JPropNode setValue(String v) {
        // should we care about overwrite?
        _value = v;
        return this;
    }

    public JPropNode addByIndex(int index) {
        // if we already have named entries, coerce into name
        if (_byName != null) {
            return addByName(String.valueOf(index));
        }
        _hasContents = true;
        if (_byIndex == null) {
            _byIndex = new LinkedHashMap<>();
        }
        Integer key = Integer.valueOf(index);
        JPropNode n = _byIndex.get(key);
        if (n == null) {
            n = new JPropNode();
            _byIndex.put(key, n);
        }
        return n;
    }
    
    public JPropNode addByName(String name) {
        // if former index entries, first coerce them
        _hasContents = true;
        if (_byIndex != null) {
            for (Map.Entry<Integer, JPropNode> entry : _byIndex.entrySet()) {
                _byName.put(entry.getKey().toString(), entry.getValue());
            }
            _byIndex = null;
        }
        if (_byName == null) {
            _byName = new LinkedHashMap<>();
        } else {
            JPropNode old = _byName.get(name);
            if (old != null) {
                return old;
            }
        }
        JPropNode result = new JPropNode();
        _byName.put(name, result);
        return result;
    }

    public boolean isLeaf() {
        return !_hasContents && (_value != null);
    }

    public boolean isArray() {
        return _byIndex != null;
    }

    public String getValue() {
        return _value;
    }

    public Iterator<JPropNode> arrayContents() {
        return _byIndex.values().iterator();
    }

    /**
     * Child entries accessed with String property name, if any.
     */
    public Iterator<Map.Entry<String, JPropNode>> objectContents() {
        return _byName.entrySet().iterator();
    }
}
