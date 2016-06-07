package com.fasterxml.jackson.dataformat.javaprop;

import java.io.IOException;
import java.util.Properties;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JavaType;
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

    /**
     * Convenience method that "writes" given `value` as properties
     * in given {@link Properties} object.
     */
    public void writeValue(Properties targetProps, Object value) throws IOException
    {
        if (targetProps == null) {
            throw new IllegalArgumentException("Can not pass null Properties as target");
        }
        JavaPropsGenerator g = ((JavaPropsFactory) getFactory())
                .createGenerator(targetProps);
        writeValue(g, value);
        g.close();
    }

    public void writeValue(Properties targetProps, Object value, JavaPropsSchema schema)
            throws IOException
    {
        if (targetProps == null) {
            throw new IllegalArgumentException("Can not pass null Properties as target");
        }
        JavaPropsGenerator g = ((JavaPropsFactory) getFactory())
                .createGenerator(targetProps);
        if (schema != null) {
            g.setSchema(schema);
        }
        writeValue(g, value);
        g.close();
    }
    
    /**
     * Convenience method that serializes given value but so that results are
     * stored in a newly constructed {@link Properties}. Functionally equivalent
     * to serializing in a File and reading contents into {@link Properties}.
     * 
     */
    public Properties writeValueAsProperties(Object value)
        throws IOException
    {
        Properties props = new Properties();
        writeValue(props, value);
        return props;
    }

    public Properties writeValueAsProperties(Object value, JavaPropsSchema schema)
        throws IOException
    {
        Properties props = new Properties();
        writeValue(props, value, schema);
        return props;
    }
    
    /*
    /**********************************************************
    /* Schema support methods?
    /**********************************************************
     */

    // do we have any actually?
}
