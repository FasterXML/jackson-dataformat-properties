package com.fasterxml.jackson.dataformat.javaprop;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ArrayGenerationTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperForProps();

    public void testPointListSimple() throws Exception
    {
        String props = MAPPER.writeValueAsString(
                new Points
                        (new Point(1, 2), new Point(3, 4), new Point(5, 6)));
        assertEquals("p.1.x=1\n"
                +"p.1.y=2\n"
                +"p.2.x=3\n"
                +"p.2.y=4\n"
                +"p.3.x=5\n"
                +"p.3.y=6\n"
                ,props);
    }

    public void testPointListWithIndex() throws Exception
    {
        JavaPropsSchema schema = JavaPropsSchema.emptySchema()
                .withWriteIndexUsingMarkers(true);
        String props = MAPPER.writer(schema)
                .writeValueAsString(
                new Points
                        (new Point(1, 2), new Point(3, 4), new Point(5, 6)));
        assertEquals("p[1].x=1\n"
                +"p[1].y=2\n"
                +"p[2].x=3\n"
                +"p[2].y=4\n"
                +"p[3].x=5\n"
                +"p[3].y=6\n"
                ,props);
    }
}
