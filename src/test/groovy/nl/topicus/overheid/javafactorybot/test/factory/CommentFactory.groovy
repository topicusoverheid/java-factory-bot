package nl.topicus.overheid.javafactorybot.test.factory

import nl.topicus.overheid.javafactorybot.Factory
import nl.topicus.overheid.javafactorybot.definition.Attribute
import nl.topicus.overheid.javafactorybot.test.model.Comment

import java.util.concurrent.TimeUnit

@Singleton
class CommentFactory extends Factory<Comment> {
    @Lazy Map<String, Attribute> attributes = [
            article     : hasOne(ArticleFactory.instance),
            author      : hasOne(UserFactory.instance),
            content     : attribute { faker.lorem().paragraph() },
            creationDate: attribute { faker.date().past(20, TimeUnit.DAYS) }
    ]
}
