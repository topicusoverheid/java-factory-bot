package nl.topicus.overheid.javafactorybot.test.factory

import nl.topicus.overheid.javafactorybot.Factory
import nl.topicus.overheid.javafactorybot.definition.Attribute
import nl.topicus.overheid.javafactorybot.definition.Trait
import nl.topicus.overheid.javafactorybot.test.model.Article

import java.util.concurrent.TimeUnit

class ArticleFactory extends Factory<Article> {
    Map<String, Attribute> attributes = [
            title       : value { faker.lorem().sentence() },
            content     : value { faker.lorem().paragraph() },
            creationDate: value { faker.date().past(20, TimeUnit.DAYS) },
            summary     : value { null },
            author      : hasOne(UserFactory),
            comments    : hasMany(CommentFactory, afterBuild: true, defaultOverrides: { Article it -> [article: it] })
    ]

    Map<String, Trait> traits = [
            withComments: new WithCommentsTrait()
    ]

    class WithCommentsTrait extends Trait<Article> {
        Map<String, Attribute> attributes = [
                comments: hasMany(CommentFactory, amount: 3)
        ]
    }
}