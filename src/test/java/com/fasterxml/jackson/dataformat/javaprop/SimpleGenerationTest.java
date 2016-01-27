package com.fasterxml.jackson.dataformat.javaprop;

import com.fasterxml.jackson.databind.*;

public class SimpleGenerationTest extends ModuleTestBase
{
    public void testSimpleEmployee() throws Exception
    {
        ObjectMapper mapper = mapperForProps();
        String props = mapper.writeValueAsString(
                new FiveMinuteUser("Bob", "Palmer", true, Gender.MALE,
                        new byte[] { 1, 2, 3, 4 }));
        assertEquals("firstName=Bob\n"
                +"lastName=Palmer\n"
                +"gender=MALE\n"
                +"verified=true\n"
                +"userImage=AQIDBA==\n",
                props);
    }
}
