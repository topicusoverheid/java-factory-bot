# java-factory-bot

[![Build Status](https://travis-ci.org/topicusoverheid/java-factory-bot.svg?branch=master)](https://travis-ci.org/topicusoverheid/java-factory-bot)

A library for creating objects as test data, with support for persisting the objects in a database.

Using factories, creating default sane test objects is simple, while individual attibutes can easily be tweaked.
Combining these with [java-faker](https://github.com/DiUS/java-faker) allows to boost your tests, or seed your database
for demo and testing purposes.

This library is inspired by [factory_bot](https://github.com/thoughtbot/factory_bot), a popular gem for ruby.

## Example

Given a model for an `Article` and a `User` (getters and setters are omitted):

```java
@Data
public class Article {
    private String title;
    private String content;
    private Date creationDate;
    private User author;
}

@Data
public class User {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
}
```

we can define factories like

```groovy
class ArticleFactory extends Factory<Article> {
    Map<String, Attribute> attributes = [
            title       : value { faker.lorem().sentence() },
            content     : value { faker.lorem().paragraph() },
            creationDate: value { faker.date().past(20, TimeUnit.DAYS) },
            author      : hasOne(UserFactory)
    ]
}

class UserFactory extends Factory<User> {
    Map<String, Attribute> attributes = [
            username : value { faker.name().username() },
            firstName: value { faker.name().firstName() },
            lastName : value { faker.name().lastName() },
            email    : value { "${get("firstName")}.${get("lastName")}@example.com" }
    ]
}
```

and create objects using

```java
Article article = new ArticleFactory().build()
```

which generates an article with default random but sane attributes. Individual attributes or relations can be overriden
by passing them in a map:

```groovy
Article article = new ArticleFactory().build([title: "Foo", user: [username: "johndoe"]])
```

For documentation and more examples, visit the [wiki](https://github.com/topicusoverheid/java-factory-bot/wiki).

## Installation

### Maven

Add the following to your dependencies:

    <dependency>
        <groupId>nl.topicus.overheid</groupId>
        <artifactId>java-factory-bot</artifactId>
        <version>0.2.0</version>
        <scope>test</scope>
    </dependency>

### Gradle

Add the following line to the dependency section of `build.gradle`

    compile "nl.topicus.overheid:java-factory-bot:0.2.0"

## Building

Execute `./mvnw install` to build and test the library.

### Source and javadoc

To generate jars containing the source and javadoc, enable the `source` profile. Example:

    ./mvnw install -P source

### Deploy

To deploy the library to maven central, enable the `release` and `source` profiles and perform the `deploy` goal:

    ./mvnw clean install deploy -P source -P release

## Licence

This library is released under the Apache 2.0 licence, which you can find [here](LICENSE).