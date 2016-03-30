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

## Simple POJO serialization

Given our simple POJO definition of

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
String props = mapper.writeValueAsString(user);
```

we could have following contents in String `props`:

```
gender=M
name.first=Bob
name.last=Palmer
verified=true
userImage=AQIDBA==
```

and could similarly read it back with:

```java
User result = mapper.readValue(props, User.class);
```

