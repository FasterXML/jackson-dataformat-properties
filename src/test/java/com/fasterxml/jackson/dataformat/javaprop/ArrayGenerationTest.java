package com.fasterxml.jackson.dataformat.javaprop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.javaprop.util.Markers;

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
                .withWriteIndexUsingMarkers(true)
                .withFirstArrayOffset(3);
        String props = MAPPER.writer(schema)
                .writeValueAsString(
                new Points
                        (new Point(1, 2), new Point(3, 4), new Point(5, 6)));
        assertEquals("p[3].x=1\n"
                +"p[3].y=2\n"
                +"p[4].x=3\n"
                +"p[4].y=4\n"
                +"p[5].x=5\n"
                +"p[5].y=6\n"
                ,props);
    }

    public void testPointListWithCustomMarkers() throws Exception
    {
        JavaPropsSchema schema = JavaPropsSchema.emptySchema()
                .withWriteIndexUsingMarkers(true)
                .withIndexMarker(Markers.create("<<", ">>"))
                ;
        String props = MAPPER.writer(schema)
                .writeValueAsString(
                new Points(new Point(1, 2), new Point(3, 4)));
        assertEquals("p<<1>>.x=1\n"
                +"p<<1>>.y=2\n"
                +"p<<2>>.x=3\n"
                +"p<<2>>.y=4\n"
                ,props);
    }
}
