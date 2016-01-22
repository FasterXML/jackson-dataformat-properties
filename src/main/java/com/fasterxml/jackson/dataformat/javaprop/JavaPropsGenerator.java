package com.fasterxml.jackson.dataformat.javaprop;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.base.GeneratorBase;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.json.JsonWriteContext;

public class JavaPropsGenerator  extends GeneratorBase
{
    protected final static long MIN_INT_AS_LONG = (long) Integer.MIN_VALUE;
    protected final static long MAX_INT_AS_LONG = (long) Integer.MAX_VALUE;

    /*
    /**********************************************************
    /* Configuration
    /**********************************************************
     */

    final protected IOContext _ioContext;

    /*
    /**********************************************************
    /* Life-cycle
    /**********************************************************
     */
    
    public JavaPropsGenerator(IOContext ctxt, int stdFeatures,
            ObjectCodec codec)
    {
        super(stdFeatures, codec);
        _ioContext = ctxt;
    }

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }
    /*
    /**********************************************************
    /* Overridden methods, configuration
    /**********************************************************
     */

    // // No way to indent
    
    @Override
    public JsonGenerator useDefaultPrettyPrinter() {
        // could alternatively throw exception but let it fly for now
        return this;
    }

    @Override
    public JsonGenerator setPrettyPrinter(PrettyPrinter pp) {
        // could alternatively throw exception but let it fly for now
        return this;
    }

    /*
    @Override
    public Object getOutputTarget() {
        return _writer;
    }

    @Override
    public int getOutputBuffered() {
        return -1;
    }
    */

    @Override
    public boolean canUseSchema(FormatSchema schema) {
        // !!! TODO
        return false;
    }
    
    //@Override public void setSchema(FormatSchema schema)
    
    // No Format Features yet
/*
    
    @Override
    public int getFormatFeatures() {
        return _formatFeatures;
    }

    @Override
    public JsonGenerator overrideFormatFeatures(int values, int mask) { }
*/

    /*
    /**********************************************************
    /* Overridden methods: low-level I/O
    /**********************************************************
     */

    @Override
    public void flush() throws IOException {
        // TODO Auto-generated method stub
        
    }
    
    /*
    /**********************************************************************
    /* Overridden methods; writing field names
    /**********************************************************************
     */

    @Override
    public void writeFieldName(String name) throws IOException
    {
        if (_writeContext.writeFieldName(name) == JsonWriteContext.STATUS_EXPECT_VALUE) {
            _reportError("Can not write a field name, expecting a value");
        }
        // TODO: actual addition of name
    }

    /*
    /**********************************************************
    /* Public API: structural output
    /**********************************************************
     */

    @Override
    public void writeStartObject() throws IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void writeEndObject() throws IOException {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void writeStartArray() throws IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void writeEndArray() throws IOException {
        // TODO Auto-generated method stub
        
    }

    /*
    /**********************************************************
    /* Output method implementations, textual
    /**********************************************************
     */

    @Override
    public void writeString(String text) throws IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void writeString(char[] text, int offset, int len)
        throws IOException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void writeRawUTF8String(byte[] text, int offset, int length)
        throws IOException
    {
        _reportUnsupportedOperation();
    }

    @Override
    public void writeUTF8String(byte[] text, int offset, int length)
        throws IOException
    {
        _reportUnsupportedOperation();
    }

    /*
    /**********************************************************
    /* Output method implementations, unprocessed ("raw")
    /**********************************************************
     */

    @Override
    public void writeRaw(String text) throws IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void writeRaw(String text, int offset, int len) throws IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void writeRaw(char[] text, int offset, int len) throws IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void writeRaw(char c) throws IOException
    {
        // TODO Auto-generated method stub
        
    }

    /*
    /**********************************************************
    /* Output method implementations, base64-encoded binary
    /**********************************************************
     */
    
    @Override
    public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len)
            throws IOException
    {
        if (data == null) {
            writeNull();
            return;
        }
        _verifyValueWrite("write Binary value");
        // ok, better just Base64 encode as a String...
        if (offset > 0 || (offset+len) != data.length) {
            data = Arrays.copyOfRange(data, offset, offset+len);
        }
        String encoded = b64variant.encode(data);

        // !!! TODO: write
    }

    /*
    /**********************************************************
    /* Output method implementations, scalars
    /**********************************************************
     */

    @Override
    public void writeBoolean(boolean state) throws IOException
    {
        _verifyValueWrite("write boolean value");
        // !!! TODO: actual write
    }

    @Override
    public void writeNumber(int i) throws IOException
    {
        _verifyValueWrite("write number");
        // !!! TODO: actual write
    }

    @Override
    public void writeNumber(long l) throws IOException
    {
        // First: maybe 32 bits is enough?
        if (l <= MAX_INT_AS_LONG && l >= MIN_INT_AS_LONG) {
            writeNumber((int) l);
            return;
        }
        _verifyValueWrite("write number");
        // !!! TODO: actual write
    }

    @Override
    public void writeNumber(BigInteger v) throws IOException
    {
        if (v == null) {
            writeNull();
            return;
        }
        _verifyValueWrite("write number");
        // !!! TODO: actual write
    }
    
    @Override
    public void writeNumber(double d) throws IOException
    {
        _verifyValueWrite("write number");
        // !!! TODO: actual write
    }    

    @Override
    public void writeNumber(float f) throws IOException
    {
        _verifyValueWrite("write number");
        // !!! TODO: actual write
    }

    @Override
    public void writeNumber(BigDecimal dec) throws IOException
    {
        if (dec == null) {
            writeNull();
            return;
        }
        _verifyValueWrite("write number");
        String str = isEnabled(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN) ? dec.toPlainString() : dec.toString();
        // !!! TODO: actual write
    }

    @Override
    public void writeNumber(String encodedValue) throws IOException,JsonGenerationException, UnsupportedOperationException
    {
        if (encodedValue == null) {
            writeNull();
            return;
        }
        _verifyValueWrite("write number");
        // !!! TODO: actual write
    }

    @Override
    public void writeNull() throws IOException
    {
        _verifyValueWrite("write null value");
        // !!! TODO: actual write
    }

    /*
    /**********************************************************
    /* Implementations for methods from base class
    /**********************************************************
     */

    @Override
    protected void _releaseBuffers() {
        // no buffers to release
    }

    @Override
    protected void _verifyValueWrite(String typeMsg) throws IOException {
        int status = _writeContext.writeValue();
        if (status == JsonWriteContext.STATUS_EXPECT_NAME) {
            _reportError("Can not "+typeMsg+", expecting field name");
        }
    }
}
