package com.fasterxml.jackson.dataformat.javaprop;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.*;

public class SimpleGenerationTest extends ModuleTestBase
{
    @JsonPropertyOrder({ "topLeft", "bottomRight" })
    static class Rectangle {
        public Point topLeft;
        public Point bottomRight;

        public Rectangle(Point p1, Point p2) {
            topLeft = p1;
            bottomRight = p2;
        }
    }

    @JsonPropertyOrder({ "x", "y" })
    static class Point {
        public int x, y;
        
        public Point(int x0, int y0) {
            x = x0;
            y = y0;
        }
    }

    static class Points {
        public List<Point> p;

        public Points(Point... p0) {
            p = Arrays.asList(p0);
        }
    }
    
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

    public void testPointList() throws Exception
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
}
