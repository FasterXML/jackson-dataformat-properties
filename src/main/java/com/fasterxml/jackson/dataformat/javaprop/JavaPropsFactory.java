package com.fasterxml.jackson.dataformat.javaprop;

import java.io.*;
import java.net.URL;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.format.InputAccessor;
import com.fasterxml.jackson.core.format.MatchStrength;
import com.fasterxml.jackson.core.io.IOContext;

public class JavaPropsFactory extends JsonFactory
{
    private static final long serialVersionUID = 1L;

    public final static String FORMAT_NAME_JAVA_PROPERTIES = "java_properties";
    
    /*
    /**********************************************************
    /* Factory construction, configuration
    /**********************************************************
     */
    
    public JavaPropsFactory() { }

    public JavaPropsFactory(ObjectCodec codec) {
        super(codec);
    }

    protected JavaPropsFactory(JavaPropsFactory src, ObjectCodec oc)
    {
        super(src, oc);
    }

    @Override
    public JavaPropsFactory copy()
    {
        _checkInvalidCopy(JavaPropsFactory.class);
        return new JavaPropsFactory(this, null);
    }

    /*                                                                                       
    /**********************************************************                              
    /* Versioned                                                                             
    /**********************************************************                              
     */

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }
    
    /*
    /**********************************************************
    /* Format detection functionality
    /**********************************************************
     */
    
    @Override
    public String getFormatName() {
        return FORMAT_NAME_JAVA_PROPERTIES;
    }
    
    /**
     * Sub-classes need to override this method
     */
    @Override
    public MatchStrength hasFormat(InputAccessor acc) throws IOException
    {
        // TODO, if possible... probably isn't?
        return MatchStrength.INCONCLUSIVE;
    }

    /*
    /**********************************************************
    /* Capability introspection
    /**********************************************************
     */

    // Not positional
    @Override
    public boolean requiresPropertyOrdering() {
        return false;
    }

    // Can not handle raw binary data
    @Override
    public boolean canHandleBinaryNatively() {
        return true;
    }

    // Not using char[] internally
    @Override
    public boolean canUseCharArrays() { return false; }

    // No format-specific configuration, yet:
/*    
    @Override
    public Class<? extends FormatFeature> getFormatReadFeatureType() {
        return null;
    }

    @Override
    public Class<? extends FormatFeature> getFormatWriteFeatureType() {
        return null;
    }
*/

    /*
    /**********************************************************
    /* Overridden parser factory methods
    /**********************************************************
     */

    @Override
    public JavaPropsParser createParser(File f) throws IOException {
        return _createParser(new FileInputStream(f), _createContext(f, true));
    }

    @Override
    public JavaPropsParser createParser(URL url) throws IOException {
        return _createParser(_optimizedStreamFromURL(url), _createContext(url, true));
    }

    @Override
    public JavaPropsParser createParser(InputStream in) throws IOException {
        return _createParser(in, _createContext(in, false));
    }

    @Override
    public JavaPropsParser createParser(byte[] data) throws IOException {
        return _createParser(data, 0, data.length, _createContext(data, true));
    }

    @Override
    public JavaPropsParser createParser(byte[] data, int offset, int len) throws IOException {
        return _createParser(data, offset, len, _createContext(data, true));
    }

    /*
    /**********************************************************
    /* Overridden generator factory methods
    /**********************************************************
     */

    @Override
    public JavaPropsGenerator createGenerator(OutputStream out, JsonEncoding enc) throws IOException {
        IOContext ctxt = _createContext(out, false);
        ctxt.setEncoding(enc);
        out = _decorate(out, ctxt);
        return _createJavaPropsGenerator(ctxt, _generatorFeatures, _objectCodec, out);
    }

    /**
     * Method for constructing {@link JsonGenerator} for generating
     * CBOR-encoded output.
     *<p>
     * Since CBOR format always uses UTF-8 internally, no encoding need
     * to be passed to this method.
     */
    @Override
    public JavaPropsGenerator createGenerator(OutputStream out) throws IOException {
        IOContext ctxt = _createContext(out, false);
        out = _decorate(out, ctxt);
        return _createJavaPropsGenerator(ctxt, _generatorFeatures, _objectCodec, out);
    }

    /*
    /******************************************************
    /* Overridden internal factory methods
    /******************************************************
     */

    @Override
    protected IOContext _createContext(Object srcRef, boolean resourceManaged) {
        return super._createContext(srcRef, resourceManaged);
    }

    @Override
    protected JavaPropsParser _createParser(InputStream in, IOContext ctxt) throws IOException
    {
//        return new JavaPropsParser(ctxt, _parserFeatures, _objectCodec, in, 0, 0, true);
        return null;
    }

    @Override
    protected JsonParser _createParser(Reader r, IOContext ctxt) throws IOException {
//      return new JavaPropsParser(ctxt, _parserFeatures, _objectCodec, null, data, offset, len, false);
      return null;
    }

    @Override
    protected JsonParser _createParser(char[] data, int offset, int len, IOContext ctxt,
            boolean recyclable) throws IOException
    {
//      return new JavaPropsParser(ctxt, _parserFeatures, _objectCodec, null, data, offset, len, false);
      return null;
    }

    @Override
    protected JavaPropsParser _createParser(byte[] data, int offset, int len, IOContext ctxt) throws IOException
    {
//        return new JavaPropsParser(ctxt, _parserFeatures, _objectCodec, null, data, offset, len, false);
        return null;
    }

    @Override
    protected JavaPropsGenerator _createGenerator(Writer out, IOContext ctxt) throws IOException {
        return null;
    }

    @Override
    protected JavaPropsGenerator _createUTF8Generator(OutputStream out, IOContext ctxt) throws IOException {
        return _createJavaPropsGenerator(ctxt, _generatorFeatures, _objectCodec, out);
    }

    @Override
    protected Writer _createWriter(OutputStream out, JsonEncoding enc, IOContext ctxt) throws IOException {
        return null;
    }

    private final JavaPropsGenerator _createJavaPropsGenerator(IOContext ctxt,
            int stdFeat, ObjectCodec codec, OutputStream out) throws IOException
    {
//        return new JavaPropsGenerator(ctxt, stdFeat, _objectCodec, out);
        return null;
    }
}
