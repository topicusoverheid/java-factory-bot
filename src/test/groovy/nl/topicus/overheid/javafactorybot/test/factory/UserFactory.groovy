package nl.topicus.overheid.javafactorybot.test.factory

import nl.topicus.overheid.javafactorybot.AbstractModelFactory
import nl.topicus.overheid.javafactorybot.test.model.Article
import nl.topicus.overheid.javafactorybot.test.model.User

import java.util.concurrent.TimeUnit

@Singleton
class UserFactory extends AbstractModelFactory<User> {
    Map<String, Closure> fields = [
            username : attribute { faker.name().username() },
            firstName: attribute { faker.name().firstName() },
            lastName : attribute { faker.name().lastName() },
            email    : attribute { faker.internet().emailAddress() }
    ]
}
