package com.fasterxml.jackson.dataformat.javaprop.failing;

import java.io.*;
import java.math.BigDecimal;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.io.SerializedString;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.dataformat.javaprop.JavaPropsFactory;
import com.fasterxml.jackson.dataformat.javaprop.ModuleTestBase;

public class SimpleStreamingTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperForProps();

    private final JavaPropsFactory F = new JavaPropsFactory();

    public void testStreamingGeneration() throws Exception
    {
        StringWriter strw = new StringWriter();
        JsonGenerator gen = F.createGenerator(strw);

        Object target = gen.getOutputTarget();
        assertTrue(target instanceof Writer);
        
        gen.writeStartObject();
        gen.writeBooleanField("flagTrue", true);
        gen.writeBooleanField("flagFalse", false);
        gen.writeNullField("null");
        gen.writeNumberField("long", 10L);
        gen.writeNumberField("int", 10);
        gen.writeNumberField("double", 0.25);
        gen.writeNumberField("float", 0.5f);
        gen.writeNumberField("decimal", BigDecimal.valueOf(0.125));

        gen.writeFieldName("arr");
        gen.writeStartArray();
        
        JsonStreamContext ctxt = gen.getOutputContext();
        String path = ctxt.toString();

        // 30-Mar-2016, tatu: Will fail until we can override accessor in 2.8
        //   (Was final in 2.7 and before)
//        assertTrue(ctxt instanceof JPropWriteContext);

        assertEquals("/arr", path);
        
        gen.writeEndArray();

        gen.writeRaw("# comment!");
        gen.writeRaw(new SerializedString("# comment!"));
        gen.writeEndObject();
        assertFalse(gen.isClosed());
        gen.flush();
        gen.close();

        String props = strw.toString();

        // Plus read back for fun
        Map<?,?> stuff = MAPPER.readValue(props, Map.class);
        assertEquals("10", stuff.get("long"));
    }
}
