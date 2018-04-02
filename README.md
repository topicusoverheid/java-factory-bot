# java-factory-bot

A library for creating objects as test data. 
Using factories, creating default sane test objects is simple, while individual attibutes can easily be tweaked.
Combining these with [java-faker](https://github.com/DiUS/java-faker) allows to boost your tests, or seed your database
for demo and testing purposes.

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
    @Override
    Map<String, Attribute> getAttributes() {
        [
                title       : attribute { faker.lorem().sentence() },
                content     : attribute { faker.lorem().paragraph() },
                creationDate: attribute { faker.date().past(20, TimeUnit.DAYS) },
                author      : hasOne(UserFactory)
        ]
    }
}

class UserFactory extends Factory<User> {
    @Override
    Map<String, Attribute> getAttributes() {
        [
                username : attribute { faker.name().username() },
                firstName: attribute { faker.name().firstName() },
                lastName : attribute { faker.name().lastName() },
                email    : attribute { "${get("firstName")}.${get("lastName")}@example.com" }
        ]
    }
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

~~For documentation, view the wiki.~~ TODO

## Installation

**Note:** java-factory-bot is not yet available on maven central. If you which to use it, please consider cloning an building locally.

### Maven

Add the following to your dependencies:

    <dependency>
        <groupId>nl.topicus.overheid</groupId>
        <artifactId>java-factory-bot</artifactId>
        <version>0.1.0</version>
        <scope>test</scope>
    </dependency>

### Gradle

Add the following line to the dependency section of `build.gradle`

    compile "nl.topicus.overheid:java-factory-bot:0.1.0"

## Building

Execute `./mvnw install` to build and test the library.

## Licence

This library is released under the Apache 2.0 licence, which you can find [here](LICENSE).