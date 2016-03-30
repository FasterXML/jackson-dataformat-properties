package com.fasterxml.jackson.dataformat.javaprop;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
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
    /* Extended read methods
    /**********************************************************
     */

    public <T> T readValue(Properties props, Class<T> valueType)
        throws IOException
    {
        return readValue(getFactory().createParser(props), valueType);
    }

    public <T> T readValue(Properties props, JavaType valueType)
        throws IOException
    {
        return readValue(getFactory().createParser(props), valueType);
    }

    /*
    /**********************************************************
    /* Extended write methods
    /**********************************************************
     */

    // do we have any actually?

    /*
    /**********************************************************
    /* Schema support methods?
    /**********************************************************
     */

    // do we have any actually?
}
