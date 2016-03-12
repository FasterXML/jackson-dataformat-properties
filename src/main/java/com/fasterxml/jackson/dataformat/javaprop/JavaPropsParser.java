package com.fasterxml.jackson.dataformat.javaprop;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Properties;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.base.ParserBase;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.dataformat.javaprop.util.JPropNode;
import com.fasterxml.jackson.dataformat.javaprop.util.JPropNodeBuilder;
import com.fasterxml.jackson.dataformat.javaprop.util.JPropReadContext;

public class JavaPropsParser extends ParserBase
{
    protected final static JavaPropsSchema DEFAULT_SCHEMA = new JavaPropsSchema();
    
    /*
    /**********************************************************
    /* Configuration
    /**********************************************************
     */

    /**
     * Codec used for data binding when (if) requested.
     */
    protected ObjectCodec _objectCodec;

    /**
     * Although most massaging is done later, caller may be interested in the
     * ultimate source.
     */
    protected final Object _inputSource;

    /**
     * Actual {@link java.util.Properties} that were parsed and handed to us
     * for further processing.
     */
    protected final Properties _sourceProperties;
    
    /**
     * Schema we use for parsing Properties into structure of some kind.
     */
    protected JavaPropsSchema _schema;

    /*
    /**********************************************************
    /* Parsing state
    /**********************************************************
     */
    
    protected JPropReadContext _readContext;

    /**
     * State flag we need due to lazily constructing {@link #_readContext}.
     */
    protected boolean _converted;
    
    /*
    /**********************************************************
    /* Life-cycle
    /**********************************************************
     */

    public JavaPropsParser(IOContext ctxt, Object inputSource,
            int parserFeatures, ObjectCodec codec, Properties sourceProps)
    {
        super(ctxt, parserFeatures);
        _objectCodec = codec;
        _inputSource = inputSource;
        _sourceProperties = sourceProps;
        
    }

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }

    @Override
    public void setSchema(FormatSchema schema)
    {
        if (schema instanceof JavaPropsSchema) {
            _schema = (JavaPropsSchema) schema;
        } else {
            super.setSchema(schema);
        }
    }

    @Override
    public JavaPropsSchema getSchema() {
        return _schema;
    }
    
    @Override
    public int releaseBuffered(OutputStream out) throws IOException {
        // we do not take byte-based input, so base impl would be fine
        return -1;
    }

    @Override
    public int releaseBuffered(Writer w) throws IOException {
        // current implementation delegates to JDK `Properties, so we don't ever
        // see the input so:
        return -1;
    }
    
    /*
    /**********************************************************
    /* Public API overrides
    /**********************************************************
     */
    
    @Override
    public ObjectCodec getCodec() {
        return _objectCodec;
    }

    @Override
    public void setCodec(ObjectCodec c) {
        _objectCodec = c;
    }

    @Override
    public Object getInputSource() {
        return _inputSource;
    }
    
    /*
    /**********************************************************
    /* Overrides: capability introspection methods
    /**********************************************************
     */
    
    @Override
    public boolean canUseSchema(FormatSchema schema) {
        return schema instanceof JavaPropsSchema;
    }

    @Override
    public boolean requiresCustomCodec() { return false;}

    @Override
    public boolean canReadObjectId() { return false; }

    @Override
    public boolean canReadTypeId() { return false; }
    
    /*
    /**********************************************************
    /* Main parsing API
    /**********************************************************
     */

    @Override
    public JsonToken nextToken() throws IOException {
        if (_readContext == null) {
            if (_converted) {
                return null;
            }
            _converted = true;
            JPropNode root = JPropNodeBuilder.build(_schema, _sourceProperties);
            _readContext = JPropReadContext.create(root);
        }

        while ((_currToken = _readContext.nextToken()) == null) {
            _readContext = _readContext.nextContext();
            if (_readContext == null) { // end of content
                return null;
            }
        }
        return _currToken;
    }

    @Override
    public String getText() throws IOException {
        JsonToken t = _currToken;
        if (t == JsonToken.VALUE_STRING) {
            return _readContext.getCurrentText();
        }
        if (t == JsonToken.FIELD_NAME) {
            return _readContext.getCurrentName();
        }
        // shouldn't have non-String scalar values so:
        return (t == null) ? null : t.asString();
    }

    @Override
    public char[] getTextCharacters() throws IOException {
        String text = getText();
        return (text == null) ? null : text.toCharArray();
    }

    @Override
    public int getTextLength() throws IOException {
        String text = getText();
        return (text == null) ? 0 : text.length();
    }

    @Override
    public int getTextOffset() throws IOException {
        return 0;
    }

    // TODO: can remove from 2.8 or so (base impl added in 2.7.1)
    @SuppressWarnings("resource")
    @Override
    public byte[] getBinaryValue(Base64Variant variant) throws IOException
    {
        if (_binaryValue == null) {
            if (_currToken != JsonToken.VALUE_STRING) {
                _reportError("Current token ("+_currToken+") not VALUE_STRING, can not access as binary");
            }
            ByteArrayBuilder builder = _getByteArrayBuilder();
            _decodeBase64(getText(), builder, variant);
            _binaryValue = builder.toByteArray();
        }
        return _binaryValue;
    }

    /*
    /**********************************************************
    /* Implementations of abstract helper methods
    /**********************************************************
     */

    @Override
    protected boolean loadMore() throws IOException {
        _reportUnsupportedOperation();
        return false;
    }
    
    @Override
    protected void _finishString() throws IOException {
        _reportUnsupportedOperation();
    }

    @Override
    protected void _closeInput() throws IOException {
        // nothing to do here
    }
}
