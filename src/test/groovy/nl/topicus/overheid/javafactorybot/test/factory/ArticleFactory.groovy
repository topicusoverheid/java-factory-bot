package nl.topicus.overheid.javafactorybot.test.factory

import nl.topicus.overheid.javafactorybot.Factory
import nl.topicus.overheid.javafactorybot.test.model.Article

import java.util.concurrent.TimeUnit

class ArticleFactory extends Factory<Article> {
    def init() {
        attributes([
                title       : attribute { faker.lorem().sentence() },
                content     : attribute { faker.lorem().paragraph() },
                creationDate: attribute { faker.date().past(20, TimeUnit.DAYS) },
                author      : hasOne(UserFactory),
                comments    : hasMany(CommentFactory)
        ])

        // Hooks
        afterBuild { Article article -> article.comments.each { it.article = article } }
        afterAttributes { Map<String, Object> attrs -> attrs.put("title", "foo") }

        // Traits
        this.trait "withComments", {
            attributes([
                    comments: hasMany(CommentFactory, 3, article: null as Article)
            ])
            afterBuild { Article article -> article.comments.each { it.article = article } }
        }
    }
}