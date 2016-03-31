package com.fasterxml.jackson.dataformat.javaprop;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SimpleParsingTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperForProps();

    private final ObjectMapper JSON_MAPPER = new ObjectMapper();
    
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

    public void testNonSplittingParsing() throws Exception {
        JavaPropsSchema schema = JavaPropsSchema.emptySchema()
                .withoutPathSeparator();
        Map<?,?> result = MAPPER.readerFor(Map.class)
                .with(schema)
                .readValue("a.b.c = 3");
        assertEquals("{\"a.b.c\":\"3\"}", JSON_MAPPER.writeValueAsString(result));
    }
}
