# configuration-provider

[![Build Status](https://travis-ci.org/anigenero/configuration-provider.svg?branch=master)](https://travis-ci.org/anigenero/configuration-provider)
[![codecov](https://codecov.io/gh/anigenero/configuration-provider/branch/master/graph/badge.svg)](https://codecov.io/gh/anigenero/configuration-provider)

### Setup ###
In your build.gradle, specify the repo and dependencies
```groovy
repositories {
    jcenter()
}

dependencies {
    compile group: 'com.anigenero.cdi.config', name: 'configuration-producer', version: '1.0.+'
}
```

Create an _application.properties_ in your resource folder
```properties
person.name=Nick Fury
person.age=45
```

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

```java
class Person {
    
    @Inject
    public Person(@Configuration("person.name") String name, @Configuration("person.age") Integer age) {
        // ...
    }
    
}
```