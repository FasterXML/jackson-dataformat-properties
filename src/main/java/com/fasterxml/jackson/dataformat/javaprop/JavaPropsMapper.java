package com.fasterxml.jackson.dataformat.javaprop;

import com.fasterxml.jackson.core.Version;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JavaPropsMapper extends ObjectMapper
{
    private static final long serialVersionUID = 1L;

    /*
    /**********************************************************
    /* Life-cycle
    /**********************************************************
     */

    public JavaPropsMapper() {
        this(new JavaPropsFactory());
    }

    public JavaPropsMapper(JavaPropsFactory f) {
        super(f);
    }

    protected JavaPropsMapper(JavaPropsMapper src) {
        super(src);
    }
    
    @Override
    public JavaPropsMapper copy()
    {
        _checkInvalidCopy(JavaPropsMapper.class);
        return new JavaPropsMapper(this);
    }

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }

    @Override
    public JavaPropsFactory getFactory() {
        return (JavaPropsFactory) _jsonFactory;
    }

    /*
    /**********************************************************
    /* Schema access
    /**********************************************************
     */
}
