package com.fasterxml.jackson.dataformat.javaprop.util;

import com.fasterxml.jackson.core.*;
//import com.fasterxml.jackson.core.json.JsonWriteContext;

public class JPropWriteContext
    extends JsonStreamContext
{
    public final static int STATUS_OK_AS_IS = 0;
    public final static int STATUS_OK_AFTER_COMMA = 1;
    public final static int STATUS_OK_AFTER_COLON = 2;
    public final static int STATUS_OK_AFTER_SPACE = 3; // in root context
    public final static int STATUS_EXPECT_VALUE = 4;
    public final static int STATUS_EXPECT_NAME = 5;

    /**
     * Parent context for this context; null for root context.
     */
    protected final JPropWriteContext _parent;

    /*
    /**********************************************************
    /* Simple instance reuse slots; speed up things
    /* a bit (10-15%) for docs with lots of small
    /* arrays/objects
    /**********************************************************
     */

    protected JPropWriteContext _child = null;
    
    /*
    /**********************************************************
    /* Location/state information (minus source reference)
    /**********************************************************
     */
    
    /**
     * Value that is being serialized and caused this context to be created;
     * typically a POJO or container type.
     */
    protected Object _currentValue;

    /**
     * Marker used to indicate that we just received a name, and
     * now expect a value
     */
    protected boolean _gotName;
    
    /**
     * Name of the field of which value is to be parsed; only
     * used for OBJECT contexts
     */
    protected String _currentName;
    
    protected int _basePathLength;

    /*
    /**********************************************************
    /* Life-cycle
    /**********************************************************
     */
    
    protected JPropWriteContext(int type, JPropWriteContext parent,
            int basePathLength)
    {
        super();
        _type = type;
        _parent = parent;
        _basePathLength = basePathLength;
    }

    private void reset(int type, int basePathLength) {
        _type = type;
        _basePathLength = basePathLength;
    }
    
    // // // Factory methods

    public static JPropWriteContext createRootContext() {
        return new JPropWriteContext(TYPE_ROOT, null, 0);
    }

    public JPropWriteContext createChildArrayContext(int basePathLength) {
        JPropWriteContext ctxt = _child;
        if (ctxt == null) {
            _child = ctxt = new JPropWriteContext(TYPE_ARRAY, this, basePathLength);
            return ctxt;
        }
        ctxt.reset(TYPE_ARRAY, basePathLength);
        return ctxt;
    }

    public JPropWriteContext createChildObjectContext(int basePathLength) {
        JPropWriteContext ctxt = _child;
        if (ctxt == null) {
            _child = ctxt = new JPropWriteContext(TYPE_OBJECT, this, basePathLength);
            return ctxt;
        }
        ctxt.reset(TYPE_OBJECT, basePathLength);
        return ctxt;
    }

    /*
    /**********************************************************
    /* State changes
    /**********************************************************
     */

    public int writeFieldName(String name) throws JsonProcessingException {
        if (_gotName) {
            return STATUS_EXPECT_VALUE;
        }
        _gotName = true;
        _currentName = name;
        return (_index < 0) ? STATUS_OK_AS_IS : STATUS_OK_AFTER_COMMA;
    }

    public int writeValue() {
        // Most likely, object:
        if (_type == TYPE_OBJECT) {
            if (!_gotName) {
                return STATUS_EXPECT_NAME;
            }
            _gotName = false;
            ++_index;
            return STATUS_OK_AFTER_COLON;
        }

        // Ok, array?
        if (_type == TYPE_ARRAY) {
            int ix = _index;
            ++_index;
            return (ix < 0) ? STATUS_OK_AS_IS : STATUS_OK_AFTER_COMMA;
        }
        
        // Nope, root context
        // No commas within root context, but need space
        ++_index;
        return (_index == 0) ? STATUS_OK_AS_IS : STATUS_OK_AFTER_SPACE;
    }
    
    /*
    /**********************************************************
    /* Simple accessors, mutators
    /**********************************************************
     */
    
    @Override
    public final JPropWriteContext getParent() { return _parent; }
    
    @Override
    public String getCurrentName() {
        return _currentName;
    }

    @Override
    public Object getCurrentValue() {
        return _currentValue;
    }

    @Override
    public void setCurrentValue(Object v) {
        _currentValue = v;
    }

    public boolean notArray() { return _type != TYPE_ARRAY; }
    
    public StringBuilder appendDesc(StringBuilder sb) {
        if (_parent != null) {
            sb = _parent.appendDesc(sb);
        }
        sb.append('/');
        switch (_type) {
        case TYPE_OBJECT:
            if (_currentName != null) {
                sb.append(_currentName);
            }
            break;
        case TYPE_ARRAY:
            sb.append(getCurrentIndex());
            break;
        case TYPE_ROOT:
        }
        return sb;
    }
    
    // // // Overridden standard methods
    
    /**
     * Overridden to provide developer JsonPointer representation
     * of the context.
     */
    @Override
    public final String toString() {
        return appendDesc(new StringBuilder(64)).toString();
    }
}
