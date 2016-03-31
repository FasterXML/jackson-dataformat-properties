package com.fasterxml.jackson.dataformat.javaprop;

import java.util.*;

/**
 * Tests for extended functionality to work with JDK `Properties`
 * Object
 */
public class PropertiesSupportTest extends ModuleTestBase
{
    private final JavaPropsMapper MAPPER = mapperForProps();

    public void testSimpleEmployee() throws Exception
    {
        Properties props = new Properties();
        props.put("a.b", "14");
        props.put("x", "foo");
        Map<?,?> result = MAPPER.readValue(props, Map.class);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("foo", result.get("x"));
        Object ob = result.get("a");
        assertNotNull(ob);
        assertTrue(ob instanceof Map<?,?>);
        Map<?,?> m2 = (Map<?,?>) ob;
        assertEquals(1, m2.size());
        assertEquals("14", m2.get("b"));
    }
}
