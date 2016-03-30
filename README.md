## Overview

[Jackson](/FasterXML/jackson) (Java) data format module that supports reading and writing 
[Java Properties](https://en.wikipedia.org/wiki/.properties) files,
using naming convention to determine implied structure (by default
assuming dotted notation, but configurable from non-nested to other separators).

## Status

While experimental, currently works well enough to pass not only basic unit tests but also
standard [Jackson dataformat benchmark](https://github.com/FasterXML/jackson-benchmarks).
The goal is to make 2.7 the first official release.


## Maven dependency

To use this extension on Maven-based projects, use following dependency:

```xml
<dependency>
  <groupId>com.fasterxml.jackson.dataformat</groupId>
  <artifactId>jackson-dataformat-properties</artifactId>
  <version>2.7.3-SNAPSHOT</version>
</dependency>
```

# Usage

Basic usage is by using `JavaPropsFactory` in places where you would usually use `JsonFactory`;
or, for convenience, `JavaPropsMapper` instead of plain `ObjectMapper`.

```java
JavaPropsMapper mapper = new JavaPropsMapper();
// and then read/write data as usual
SomeType value = ...;
String props = mapper.writeValueAsBytes(value);
// or
mapper.writeValue(new File("stuff.properties", value);
SomeType otherValue = mapper.readValue(props, SomeType.class);
```

## Basics of conversion

Since default `java.util.Properties` can read "flat" key/value entries in,
what is the big deal here?

Most properties files actually use an implied structure by using a naming convention;
most commonly by using period ('.') as logical path separator. So you may have something like:

```
title=Home Page
site.host=localhost
site.port=8080
```

to group related properties together. This implied structure could easily be made actual explicit Structure:
for example, we could consider following Class definitions

```java
public class WebPage {
  public String title;
  public Site site;
}
static class Site {
  public String host;
  public int port;
}
```

So what this module does is to convert "flattened" properties keys into hierarchic structure
and back; and by doing this allows Jackson databinding to bind such properties content into
full hierarcic Object model.

## Simple POJO serialization

Let's consider another simple POJO definition of:

```java
public class User {
    public Gender gender;
    public Name name; // nested object
    public boolean verified;
    public byte[] userImage;
}
static class Name {
    public String first, last;
}
enum Gender { M, F, O; }
```

and code

```java
User user = new User(Gender.M, new Name("Bob", "Palmer"),
   true, new byte[] { 1, 2, 3, 4 });
String propStr = mapper.writeValueAsString(user);
```

and with that, we could have following contents in String `propStr`:

```
gender=M
name.first=Bob
name.last=Palmer
verified=true
userImage=AQIDBA==
```

## Simple POJO deserialization

Given a String of `propStr`, we could easily read contents back as a POJO with:

```java
User result = mapper.readValue(props, User.class);
```

and veirfy that contents are as expected.


## Basic array handling

Although path notation usually assumes that path segments (pieces between separators,
comma by default) are to be considered logical POJO properties (no pun intended), or fields,
there is default handling (which may be disabled) to infer index values from path segments
that are simple numbers. This means that default handling for Properties content like:

```
boxes.1.x = 5
boxes.1.y = 6
boxes.2.x = -5
boxes.2.y = 15
```

could be easily read into following POJO:

```java
public class Container {
  public List<Box> boxes;
}
static class Box {
  public int x, y;
}
```

with

```java
Container c = mapper.readValue(propsFile, Boxes.class);
```

Similarly when writing out Properties content, default inclusion mechanism is to use "simple"
indexes, and to start with index `1`. Note that most of these aspects are configurable; and
note also that when reading, absolute value of index is not used as-is but only to indicate
ordering of entries: that is, gaps in logical numbering do not indicate `null` values in resulting
arrays and `List`s.

# Using "Any" properties

TO BE WRITTEN

# Customizing handling with `JavaPropsSchema`

TO BE WRITTEN



