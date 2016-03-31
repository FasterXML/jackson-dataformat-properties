package com.fasterxml.jackson.dataformat.javaprop;

import com.fasterxml.jackson.databind.*;

public class SimpleGenerationTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperForProps();

    public void testSimpleEmployee() throws Exception
    {
        String props = MAPPER.writeValueAsString(
                new FiveMinuteUser("Bob", "Palmer", true, Gender.MALE,
                        new byte[] { 1, 2, 3, 4 }));
        assertEquals("firstName=Bob\n"
                +"lastName=Palmer\n"
                +"gender=MALE\n"
                +"verified=true\n"
                +"userImage=AQIDBA==\n"
                ,props);
    }

    public void testSimpleRectangle() throws Exception
    {
        String props = MAPPER.writeValueAsString(
                new Rectangle(new Point(1, -2), new Point(5, 10)));
        assertEquals("topLeft.x=1\n"
                +"topLeft.y=-2\n"
                +"bottomRight.x=5\n"
                +"bottomRight.y=10\n"
                ,props);
    }

    public void testRectangleWithCustomKeyValueSeparator() throws Exception
    {
        JavaPropsSchema schema = JavaPropsSchema.emptySchema()
                .withKeyValueSeparator(": ");
        String props = MAPPER.writer(schema).writeValueAsString(
                new Rectangle(new Point(1, -2), new Point(5, 10)));
        assertEquals("topLeft.x: 1\n"
                +"topLeft.y: -2\n"
                +"bottomRight.x: 5\n"
                +"bottomRight.y: 10\n"
                ,props);
    }
}
