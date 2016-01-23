package com.fasterxml.jackson.dataformat.javaprop.util;

import com.fasterxml.jackson.dataformat.javaprop.JavaPropsSchema;

/**
 * Helper class used for splitting a flattened property key into
 * nested/structured path that can be used to traverse and/or define
 * hierarchic structure.
 */
public class JPropPathSplitter
{
    protected final JavaPropsSchema _schema;
    
    public JPropPathSplitter(JavaPropsSchema schema)
    {
        _schema = schema;
    }

    /*
    public static void main(String[] args) throws Exception
    {
        String p1 = Pattern.quote("[");
        String p2 = Pattern.quote("->");

        Pattern p = Pattern.compile("("+p1+")|("+p2+")");

        Matcher m = p.matcher("path.and[2]->foo");
        System.out.println("Pattern == "+p);
        if (!m.find()) {
            throw new Error("Not found!");
        }
        System.out.println("Matches: main="+m.group(0)+" , 1='"+m.group(1)+"' vs 2='"+m.group(2)+"'");
        System.out.println(" starts: 1="+m.start(1)+", 2="+m.start(2)+" vs 0 = "+m.start());
    }
    */
}
