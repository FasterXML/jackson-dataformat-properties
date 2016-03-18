package com.fasterxml.jackson.dataformat.javaprop;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SimpleParsingTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperForProps();
    
    public void testSimpleNonNested() throws Exception
    {
        final String INPUT = "firstName=Bob\n"
                +"lastName=Palmer\n"
                +"gender=MALE\n"
                +"verified=true\n"
                +"userImage=AQIDBA==\n";
        FiveMinuteUser result = MAPPER.readValue(INPUT, FiveMinuteUser.class);
        assertEquals(Gender.MALE, result.getGender());
        assertEquals(4, result.getUserImage().length);
    }

    public void testSimpleRectangle() throws Exception
    {
        final String INPUT = "topLeft.x=1\n"
                +"topLeft.y=-2\n"
                +"bottomRight.x=5\n"
                +"bottomRight.y=10\n";
        Rectangle result = MAPPER.readValue(INPUT, Rectangle.class);
        assertEquals(5, result.bottomRight.x);
        assertEquals(10, result.bottomRight.y);
    }

    public void testPointList() throws Exception
    {
        final String INPUT = "p.1.x=1\n"
                +"p.1.y=2\n"
                +"p.2.x=3\n"
                +"p.2.y=4\n"
                +"p.3.x=5\n"
                +"p.3.y=6\n"
                ;
        Points result = MAPPER.readValue(INPUT, Points.class);
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
}
