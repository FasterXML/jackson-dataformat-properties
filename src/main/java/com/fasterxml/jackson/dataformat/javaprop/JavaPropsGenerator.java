package com.fasterxml.jackson.dataformat.javaprop;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.base.GeneratorBase;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.json.JsonWriteContext;
import com.fasterxml.jackson.dataformat.javaprop.io.JPropEscapes;
import com.fasterxml.jackson.dataformat.javaprop.io.JPropWriteContext;
import com.fasterxml.jackson.dataformat.javaprop.util.Markers;

public class JavaPropsGenerator extends GeneratorBase
{
    // As an optimization we try coalescing short writes into
    // buffer; but pass longer directly.
    final protected static int SHORT_WRITE = 100;

    /**
     * Since our context object does NOT implement standard write context, need
     * to do something like use a placeholder...
     */
    protected final static JsonWriteContext BOGUS_WRITE_CONTEXT = JsonWriteContext.createRootContext(null);
    
    private final static JavaPropsSchema EMPTY_SCHEMA;
    static {
        EMPTY_SCHEMA = JavaPropsSchema.emptySchema();
    }

    /*
    /**********************************************************
    /* Configuration
    /**********************************************************
     */

    final protected IOContext _ioContext;

    /**
     * Underlying {@link Writer} used for output.
     */
    final protected Writer _out;

    /**
     * Definition of columns being written, if available.
     */
    protected JavaPropsSchema _schema = EMPTY_SCHEMA;

    /*
    /**********************************************************
    /* Output state
    /**********************************************************
     */

    /**
     * Current context, in form we can use it (GeneratorBase has
     * untyped reference; left as null)
     */
    protected JPropWriteContext _jpropContext;

    /*
    /**********************************************************
    /* Output buffering
    /**********************************************************
     */

    /**
     * Intermediate buffer in which contents are buffered before
     * being written using {@link #_out}.
     */
    protected char[] _outputBuffer;

    /**
     * Pointer to the next available location in {@link #_outputBuffer}
     */
    protected int _outputTail = 0;

    /**
     * Offset to index after the last valid index in {@link #_outputBuffer}.
     * Typically same as length of the buffer.
     */
    protected final int _outputEnd;

    protected final StringBuilder _basePath = new StringBuilder(50);

    /*
    /**********************************************************
    /* Life-cycle
    /**********************************************************
     */

    public JavaPropsGenerator(IOContext ctxt, Writer out,
            int stdFeatures, ObjectCodec codec)
    {
        super(stdFeatures, codec, BOGUS_WRITE_CONTEXT);
        _ioContext = ctxt;
        _out = out;
        _outputBuffer = ctxt.allocConcatBuffer();
        _outputEnd = _outputBuffer.length;
        _jpropContext = JPropWriteContext.createRootContext();
    }

    @Override
    public Object getCurrentValue() {
        return _jpropContext.getCurrentValue();
    }

    @Override
    public void setCurrentValue(Object v) {
        _jpropContext.setCurrentValue(v);
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

    @Override
    public Object getOutputTarget() {
        return _out;
    }

    /*
    @Override
    public int getOutputBuffered() {
        return -1;
    }
    */

    @Override
    public void setSchema(FormatSchema schema) {
        if (schema instanceof JavaPropsSchema) {
            _schema = (JavaPropsSchema) schema;
            return;
        }
        super.setSchema(schema);
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
    public boolean canWriteObjectId() { return false; }

    @Override
    public boolean canWriteTypeId() { return false; }

    @Override
    public boolean canWriteBinaryNatively() { return false; }

    @Override
    public boolean canOmitFields() { return true; }
    
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
    public void close() throws IOException
    {
        super.close();
        _flushBuffer();
        _outputTail = 0; // just to ensure we don't think there's anything buffered

        if (_out != null) {
            if (_ioContext.isResourceManaged() || isEnabled(Feature.AUTO_CLOSE_TARGET)) {
                _out.close();
            } else if (isEnabled(Feature.FLUSH_PASSED_TO_STREAM)) {
                // If we can't close it, we should at least flush
                _out.flush();
            }
        }
        // Internal buffer(s) generator has can now be released as well
        _releaseBuffers();
    }

    @Override
    public void flush() throws IOException
    {
        _flushBuffer();
        if (_out != null) {
            if (isEnabled(Feature.FLUSH_PASSED_TO_STREAM)) {
                _out.flush();
            }
        }
    }

    /*
    /**********************************************************************
    /* Overridden methods; writing field names
    /**********************************************************************
     */

    @Override
    public void writeFieldName(String name) throws IOException
    {
        if (!_jpropContext.writeFieldName(name)) {
            _reportError("Can not write a field name, expecting a value");
        }
        // Ok; append to base path at this point.
        // First: ensure possibly preceding field name is removed:
        _jpropContext.truncatePath(_basePath);
        if (_basePath.length() > 0) {
            String sep = _schema.pathSeparator();
            if (!sep.isEmpty()) {
                _basePath.append(sep);
            }
        }
        // Note that escaping needs to be applied now...
        
        JPropEscapes.appendKey(_basePath, name);
        // NOTE: we do NOT yet write the key; wait until we have value; just append to path
    }

    /*
    /**********************************************************
    /* Public API: structural output
    /**********************************************************
     */

    @Override
    public void writeStartArray() throws IOException {
        _verifyValueWrite("start an array");
        _jpropContext = _jpropContext.createChildArrayContext(_basePath.length());
    }

    @Override
    public void writeEndArray() throws IOException {
        if (!_jpropContext.inArray()) {
            _reportError("Current context not an ARRAY but "+_jpropContext.getTypeDesc());
        }
        _jpropContext = _jpropContext.getParent();
    }

    @Override
    public void writeStartObject() throws IOException {
        _verifyValueWrite("start an object");
        _jpropContext = _jpropContext.createChildObjectContext(_basePath.length());
    }

    @Override
    public void writeEndObject() throws IOException
    {
        if (!_jpropContext.inObject()) {
            _reportError("Current context not an object but "+_jpropContext.getTypeDesc());
        }
        _jpropContext = _jpropContext.getParent();
    }

    /*
    /**********************************************************
    /* Output method implementations, textual
    /**********************************************************
     */

    @Override
    public void writeString(String text) throws IOException
    {
        if (text == null) {
            writeNull();
            return;
        }
        _verifyValueWrite("write String value");
        _writeEscapedEntry(text);
    }

    @Override
    public void writeString(char[] text, int offset, int len)
        throws IOException
    {
        _verifyValueWrite("write String value");
        _writeEscapedEntry(text, offset, len);
    }

    @Override
    public void writeRawUTF8String(byte[] text, int offset, int len)throws IOException
    {
        _reportUnsupportedOperation();
    }

    @Override
    public void writeUTF8String(byte[] text, int offset, int len) throws IOException
    {
        writeString(new String(text, offset, len, "UTF-8"));
    }

    /*
    /**********************************************************
    /* Output method implementations, unprocessed ("raw")
    /**********************************************************
     */

    @Override
    public void writeRaw(String text) throws IOException {
        _writeRaw(text);
    }

    @Override
    public void writeRaw(String text, int offset, int len) throws IOException {
        _writeRaw(text.substring(offset, offset+len));
    }

    @Override
    public void writeRaw(char[] text, int offset, int len) throws IOException {
        _writeRaw(text, offset, len);
    }

    @Override
    public void writeRaw(char c) throws IOException {
        _writeRaw(c);
    }

    @Override
    public void writeRaw(SerializableString text) throws IOException, JsonGenerationException {
        writeRaw(text.toString());
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
        _writeEscapedEntry(encoded);
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
        _writeUnescapedEntry(state ? "true" : "false");
    }

    @Override
    public void writeNumber(int i) throws IOException
    {
        _verifyValueWrite("write number");
        _writeUnescapedEntry(String.valueOf(i));
    }

    @Override
    public void writeNumber(long l) throws IOException
    {
        _verifyValueWrite("write number");
        _writeUnescapedEntry(String.valueOf(l));
    }

    @Override
    public void writeNumber(BigInteger v) throws IOException
    {
        if (v == null) {
            writeNull();
            return;
        }
        _verifyValueWrite("write number");
        _writeUnescapedEntry(String.valueOf(v));
    }
    
    @Override
    public void writeNumber(double d) throws IOException
    {
        _verifyValueWrite("write number");
        _writeUnescapedEntry(String.valueOf(d));
    }    

    @Override
    public void writeNumber(float f) throws IOException
    {
        _verifyValueWrite("write number");
        _writeUnescapedEntry(String.valueOf(f));
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
        _writeUnescapedEntry(str);
    }

    @Override
    public void writeNumber(String encodedValue) throws IOException
    {
        if (encodedValue == null) {
            writeNull();
            return;
        }
        _verifyValueWrite("write number");
        _writeUnescapedEntry(encodedValue);
    }

    @Override
    public void writeNull() throws IOException
    {
        _verifyValueWrite("write null value");
        _writeUnescapedEntry("");
    }

    /*
    /**********************************************************
    /* Implementations for methods from base class
    /**********************************************************
     */

    @Override
    protected void _releaseBuffers()
    {
        char[] buf = _outputBuffer;
        if (buf != null) {
            _outputBuffer = null;
            _ioContext.releaseConcatBuffer(buf);
        }
    }

    protected void _flushBuffer() throws IOException
    {
        if (_outputTail > 0) {
            _out.write(_outputBuffer, 0, _outputTail);
            _outputTail = 0;
        }
    }

    @Override
    protected void _verifyValueWrite(String typeMsg) throws IOException
    {
        // first, check that name/value cadence works
        if (!_jpropContext.writeValue()) {
            _reportError("Can not "+typeMsg+", expecting field name");
        }
        // and if so, update path if we are in array
        if (_jpropContext.inArray()) {
            // remove possible path remnants from an earlier sibling
            _jpropContext.truncatePath(_basePath);
            int ix = _jpropContext.getCurrentIndex() + _schema.firstArrayOffset();
            if (_schema.writeIndexUsingMarkers()) {
                Markers m = _schema.indexMarker();
                // no leading path separator, if using enclosed indexes
                _basePath.append(m.getStart());
                _basePath.append(ix);
                _basePath.append(m.getEnd());
            } else {
                // leading path separator, if using "simple" index markers
                if (_basePath.length() > 0) {
                    String sep = _schema.pathSeparator();
                    if (!sep.isEmpty()) {
                        _basePath.append(sep);
                    }
                }
                _basePath.append(ix);
            }
        }
    }

    /*
    /**********************************************************
    /* Internal methods; escaping writes
    /**********************************************************
     */
    
    protected void _writeEscapedEntry(String value) throws IOException
    {
        // note: key has been already escaped so:
        _writeRaw(_basePath);
        _writeRaw(_schema.keyValueSeparator());

        _writeEscaped(value);
        _writeLinefeed();
    }

    protected void _writeEscapedEntry(char[] text, int offset, int len) throws IOException
    {
        // note: key has been already escaped so:
        _writeRaw(_basePath);
        _writeRaw(_schema.keyValueSeparator());

        _writeEscaped(text, offset, len);
        _writeLinefeed();
    }

    protected void _writeUnescapedEntry(String value) throws IOException
    {
        // note: key has been already escaped so:
        _writeRaw(_basePath);
        _writeRaw(_schema.keyValueSeparator());

        _writeRaw(value);
        _writeLinefeed();
    }

    protected void _writeEscaped(String value) throws IOException
    {
        StringBuilder sb = JPropEscapes.appendValue(value);
        if (sb == null) {
            _writeRaw(value);
        } else {
            _writeRaw(sb);
        }
    }

    protected void _writeEscaped(char[] text, int offset, int len) throws IOException
    {
        _writeEscaped(new String(text, offset, len));
    }

    protected void _writeLinefeed() throws IOException
    {
        _writeRaw(_schema.lineEnding());
    }

    /*
    /**********************************************************
    /* Internal methods; raw writes
    /**********************************************************
     */

    protected void _writeRaw(char c) throws IOException
    {
        if (_outputTail >= _outputEnd) {
            _flushBuffer();
        }
        _outputBuffer[_outputTail++] = c;
    }
    
    public void _writeRaw(String text) throws IOException
    {
        // Nothing to check, can just output as is
        int len = text.length();
        int room = _outputEnd - _outputTail;

        if (room == 0) {
            _flushBuffer();
            room = _outputEnd - _outputTail;
        }
        // But would it nicely fit in? If yes, it's easy
        if (room >= len) {
            text.getChars(0, len, _outputBuffer, _outputTail);
            _outputTail += len;
        } else {
            _writeRawLong(text);
        }
    }

    public void _writeRaw(StringBuilder text) throws IOException
    {
        // Nothing to check, can just output as is
        int len = text.length();
        int room = _outputEnd - _outputTail;

        if (room == 0) {
            _flushBuffer();
            room = _outputEnd - _outputTail;
        }
        // But would it nicely fit in? If yes, it's easy
        if (room >= len) {
            text.getChars(0, len, _outputBuffer, _outputTail);
            _outputTail += len;
        } else {
            _writeRawLong(text);
        }
    }
    
    public void _writeRaw(String text, int start, int len) throws IOException
    {
        // Nothing to check, can just output as is
        int room = _outputEnd - _outputTail;

        if (room < len) {
            _flushBuffer();
            room = _outputEnd - _outputTail;
        }
        // But would it nicely fit in? If yes, it's easy
        if (room >= len) {
            text.getChars(start, start+len, _outputBuffer, _outputTail);
            _outputTail += len;
        } else {                
            _writeRawLong(text.substring(start, start+len));
        }
    }

    protected void _writeRaw(char[] text, int offset, int len) throws IOException
    {
        // Only worth buffering if it's a short write?
        if (len < SHORT_WRITE) {
            int room = _outputEnd - _outputTail;
            if (len > room) {
                _flushBuffer();
            }
            System.arraycopy(text, offset, _outputBuffer, _outputTail, len);
            _outputTail += len;
            return;
        }
        // Otherwise, better just pass through:
        _flushBuffer();
        _out.write(text, offset, len);
    }

    protected void _writeRawLong(String text) throws IOException
    {
        int room = _outputEnd - _outputTail;
        text.getChars(0, room, _outputBuffer, _outputTail);
        _outputTail += room;
        _flushBuffer();
        int offset = room;
        int len = text.length() - room;

        while (len > _outputEnd) {
            int amount = _outputEnd;
            text.getChars(offset, offset+amount, _outputBuffer, 0);
            _outputTail = amount;
            _flushBuffer();
            offset += amount;
            len -= amount;
        }
        // And last piece (at most length of buffer)
        text.getChars(offset, offset+len, _outputBuffer, 0);
        _outputTail = len;
    }

    protected void _writeRawLong(StringBuilder text) throws IOException
    {
        int room = _outputEnd - _outputTail;
        text.getChars(0, room, _outputBuffer, _outputTail);
        _outputTail += room;
        _flushBuffer();
        int offset = room;
        int len = text.length() - room;

        while (len > _outputEnd) {
            int amount = _outputEnd;
            text.getChars(offset, offset+amount, _outputBuffer, 0);
            _outputTail = amount;
            _flushBuffer();
            offset += amount;
            len -= amount;
        }
        // And last piece (at most length of buffer)
        text.getChars(offset, offset+len, _outputBuffer, 0);
        _outputTail = len;
    }
}
