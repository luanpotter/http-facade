# http-facade

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/xyz.luan/http-facade/badge.svg)](https://maven-badges.herokuapp.com/maven-central/xyz.luan/http-facade)
[![GitHub release](https://img.shields.io/github/release/luanpotter/http-facade.svg)](https://github.com/luanpotter/http-facade/releases)
[![License](https://img.shields.io/github/license/luanpotter/http-facade.svg)](https://opensource.org/licenses/MIT)

My take on a simple HTTP Façade for easy request making.

Zero-dependency, unobtrusive, Java 7/Google App Engine-ready API for some pretty HTTP handling.

## Maven

```xml
<dependency>
    <groupId>xyz.luan</groupId>
    <artifactId>http-facade</artifactId>
    <version>2.3.0</version>
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

Parse URL's, set form and query params, handle cookies, authentication (built-in non-platform-dependent Base64 enc/dec), and more.

For problems with SSL requests and Java outdated CA certificate repositories, see [here](doc/SSL.md).

## Mocking Requests

If you want to mock a request in order to always retrieve a specific response, instead of actually making the request, you can use MockedHttpFacade and MockedResponse.

Simply inject/instantiate a mocked service to do the request; see the following example:

```java

    class MyService {

        public void sendRequest(String url) {
            getFacade(url).get();
        }

        public HttpFacade getFacade(String url) {
            return new HttpFacade(url);
        }
    }

    class MockMyService extends MyService {
        @Override
        public HttpFacade getFacade(String url) {
            MockedResponse.build().withStatus(200).withContent("{ name : mock }");
            return new MockedHttpFacade(url).mockResponse(mock);
        }
    }

```
