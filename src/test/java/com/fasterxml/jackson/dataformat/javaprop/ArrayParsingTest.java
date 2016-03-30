package com.fasterxml.jackson.dataformat.javaprop;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.javaprop.ModuleTestBase.Points;

public class ArrayParsingTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperForProps();

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
            InputStream in = new ByteArrayInputStream(input.getBytes("ISO-8859-1"));
            return mapper.readValue(in, type);
        }
        return mapper.readValue(new StringReader(input), type);
    }
}
