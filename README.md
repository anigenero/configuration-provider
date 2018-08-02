# configuration-provider

[![Build Status](https://travis-ci.org/anigenero/configuration-provider.svg?branch=master)](https://travis-ci.org/anigenero/configuration-provider)

```java
class Person {
    
    @Inject
    @Configuration("person.name")
    private String name;
    
    @Inject
    @Configuration("person.age")
    private Integer age;
    
}
```