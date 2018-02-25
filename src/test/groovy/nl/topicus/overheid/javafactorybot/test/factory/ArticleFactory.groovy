package nl.topicus.overheid.javafactorybot.test.factory

import nl.topicus.overheid.javafactorybot.AbstractModelFactory
import nl.topicus.overheid.javafactorybot.test.model.Article

import java.util.concurrent.TimeUnit

@Singleton
class ArticleFactory extends AbstractModelFactory<Article> {
    Map<String, Closure> fields = [
            title       : attribute { faker.lorem().sentence() },
            content     : attribute { faker.lorem().paragraphs(3) },
            creationDate: attribute { faker.date().past(20, TimeUnit.DAYS) },
            author      : hasOne(UserFactory.instance),
            comments    : hasMany(CommentFactory.instance, 3),
    ]
}
