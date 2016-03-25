package com.fasterxml.jackson.dataformat.javaprop;

import java.io.*;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SimpleParsingTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperForProps();

    public void testSimpleNonNested() throws Exception {
        _testSimpleNonNested(false);
        _testSimpleNonNested(true);
    }

    private void _testSimpleNonNested(boolean useBytes) throws Exception
    {
        final String INPUT = "firstName=Bob\n"
                +"lastName=Palmer\n"
                +"gender=MALE\n"
                +"verified=true\n"
                +"userImage=AQIDBA==\n";
        FiveMinuteUser result = _mapFrom(MAPPER, INPUT, FiveMinuteUser.class, useBytes);
        assertEquals(Gender.MALE, result.getGender());
        assertEquals(4, result.getUserImage().length);
    }

    public void testSimpleRectangle() throws Exception {
        _testSimpleRectangle(false);
        _testSimpleRectangle(true);
    }

    private void _testSimpleRectangle(boolean useBytes) throws Exception
    {
        final String INPUT = "topLeft.x=1\n"
                +"topLeft.y=-2\n"
                +"bottomRight.x=5\n"
                +"bottomRight.y=10\n";
        Rectangle result = _mapFrom(MAPPER, INPUT, Rectangle.class, useBytes);
        assertEquals(5, result.bottomRight.x);
        assertEquals(10, result.bottomRight.y);
    }

    public void testPointList() throws Exception
    {
        _testPointList(false);
        _testPointList(true);
    }

    private void _testPointList(boolean useBytes) throws Exception
    {
        final String INPUT = "p.1.x=1\n"
                +"p.1.y=2\n"
                +"p.2.x=3\n"
                +"p.2.y=4\n"
                +"p.3.x=5\n"
                +"p.3.y=6\n"
                ;
        Points result = _mapFrom(MAPPER, INPUT, Points.class, useBytes);
        assertNotNull(result);
        assertNotNull(result.p);
//System.err.println("As JSON: "+new ObjectMapper().writeValueAsString(result));        
        assertEquals(3, result.p.size());
        assertEquals(1, result.p.get(0).x);
        assertEquals(2, result.p.get(0).y);
        assertEquals(3, result.p.get(1).x);
        assertEquals(4, result.p.get(1).y);
        assertEquals(5, result.p.get(2).x);
        assertEquals(6, result.p.get(2).y);
    }

    protected <T> T _mapFrom(ObjectMapper mapper, String input, Class<T> type,
            boolean useBytes)
        throws IOException
    {
        if (useBytes) {
            return _mapFromBytes(mapper, input, type);
        }
        return _mapFromString(mapper, input, type);
    }
    
    protected <T> T _mapFromString(ObjectMapper mapper, String input, Class<T> type)
        throws IOException
    {
        return mapper.readValue(new StringReader(input), type);
    }

    protected <T> T _mapFromBytes(ObjectMapper mapper, String input, Class<T> type)
        throws IOException
    {
        InputStream in = new ByteArrayInputStream(input.getBytes("ISO-8859-1"));
        return mapper.readValue(in, type);
    }
}
