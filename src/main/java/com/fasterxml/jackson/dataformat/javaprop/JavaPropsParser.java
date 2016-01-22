package com.fasterxml.jackson.dataformat.javaprop;

import java.io.IOException;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.base.ParserBase;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;

public class JavaPropsParser extends ParserBase
{
    /*
    /**********************************************************
    /* Configuration
    /**********************************************************
     */

    /**
     * Codec used for data binding when (if) requested.
     */
    protected ObjectCodec _objectCodec;

    /*
    /**********************************************************
    /* Life-cycle
    /**********************************************************
     */

    public JavaPropsParser(IOContext ctxt, int parserFeatures,
            ObjectCodec codec)
    {
        super(ctxt, parserFeatures);
        _objectCodec = codec;
    }

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }

    /*
    /**********************************************************
    /* Publc API overrides
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
    public boolean canUseSchema(FormatSchema schema) {
        return schema instanceof JavaPropsSchema;
    }
    
    /*
    /**********************************************************
    /* Main parsing API
    /**********************************************************
     */

    @Override
    public JsonToken nextToken() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getText() throws IOException {
        // TODO Auto-generated method stub
        return null;
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
