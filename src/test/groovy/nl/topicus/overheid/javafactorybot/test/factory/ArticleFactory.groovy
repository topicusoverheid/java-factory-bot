package nl.topicus.overheid.javafactorybot.test.factory

import nl.topicus.overheid.javafactorybot.Factory
import nl.topicus.overheid.javafactorybot.definition.Attribute
import nl.topicus.overheid.javafactorybot.test.model.Article

import java.util.concurrent.TimeUnit

@Singleton
class ArticleFactory extends Factory<Article> {
    @Lazy Map<String, Attribute> attributes = [
            title       : attribute { faker.lorem().sentence() },
            content     : attribute { faker.lorem().paragraphs(3) },
            creationDate: attribute { faker.date().past(20, TimeUnit.DAYS) },
            author      : hasOne(UserFactory.instance, firstName: 'Jan'),
            comments    : hasMany(CommentFactory.instance, 3, article: null)
    ]
}
