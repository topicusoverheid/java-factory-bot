package nl.topicus.overheid.javafactorybot.test.factory

import nl.topicus.overheid.javafactorybot.Factory
import nl.topicus.overheid.javafactorybot.definition.Attribute
import nl.topicus.overheid.javafactorybot.test.model.Comment

import java.util.concurrent.TimeUnit

class CommentFactory extends Factory<Comment> {
    def init() {
        attributes([
                article     : hasOne(ArticleFactory),
                author      : hasOne(UserFactory),
                content     : attribute { faker.lorem().paragraph() },
                creationDate: attribute { faker.date().past(20, TimeUnit.DAYS) }
        ])
    }
}
