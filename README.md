# Open for Java

This is a port of https://www.npmjs.com/package/open / https://github.com/sindresorhus/open to Java

## Why?

Java lacks a sensible way of opening URLs and applications in a cross platform way. The built in jawa.awt.Desktop only works on some platfors and has some known issues e.g. when the process is run as headless but you still want to use open.

## Usage

Add the dependency, e.g. if you are using Maven:

```xml
<dependency>
    <groupId>com.vaadin</groupId>
    <artifactId>open</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

Then call one of the static methods in the `Open` class, e.g.

```java
Open.open("https://github.com/");
```
opens the URL in the default browser

```java
Open.open("https://github.com/", App.FIREFOX);
```
opens the URL in the default browser
