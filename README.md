# http-facade

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/xyz.luan/http-facade/badge.svg)](https://maven-badges.herokuapp.com/maven-central/xyz.luan/http-facade)
[![GitHub release](https://img.shields.io/github/release/luanpotter/http-facade.svg)](https://github.com/luanpotter/http-facade/releases)
[![License](https://img.shields.io/github/license/luanpotter/http-facade.svg)](https://opensource.org/licenses/MIT)

My take on a simple HTTP Façade for easy request making.

## Maven

```xml
<dependency>
    <groupId>xyz.luan</groupId>
    <artifactId>http-facade</artifactId>
    <version>1.5.1</version>
</dependency>
```

## Examples

Simple get:

```java
    Response r = new HttpFacade("www.google.com").get();
    r.status() // 200
    r.content() // <html>...
```

More complex request:

```java
    new HttpFacade("luan.xyz/api/people")
        .header("key", "value")
        .body("{ id: 42, name: \"Luan\" }")
    .post();
```
