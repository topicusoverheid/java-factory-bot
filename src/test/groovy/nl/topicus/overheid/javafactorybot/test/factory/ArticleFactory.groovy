package nl.topicus.overheid.javafactorybot.test.factory

import nl.topicus.overheid.javafactorybot.Factory
import nl.topicus.overheid.javafactorybot.definition.Attribute
import nl.topicus.overheid.javafactorybot.test.model.Article

import java.util.concurrent.TimeUnit

@Singleton
class ArticleFactory extends Factory<Article> {
    def definition(){
        a([
                title       : attribute { faker.lorem().sentence() },
                content     : attribute { faker.lorem().paragraphs(3) },
                creationDate: attribute { faker.date().past(20, TimeUnit.DAYS) },
                author      : hasOne(UserFactory.instance, firstName: 'Jan'),
                comments    : hasMany(CommentFactory.instance, 3, article: null)
        ])
        after {
            build {
                def article = delegate
                comments.each{it.article = article}
            }
        }
    }

    def a(Map<String, Attribute> attributes){

    }

    def after(@DelegatesTo(FactoryHooks) Closure closure){

    }


    @Lazy Map<String, Attribute> attributes = [
            title       : attribute { faker.lorem().sentence() },
            content     : attribute { faker.lorem().paragraphs(3) },
            creationDate: attribute { faker.date().past(20, TimeUnit.DAYS) },
            author      : hasOne(UserFactory.instance, firstName: 'Jan'),
            comments    : hasMany(CommentFactory.instance, 3, article: null)
    ]

    @Override
    Article onAfterBuild(Article article) {
        article.comments.each {it.article = article}
        article
    }
}

class FactoryHooks {
    def build(@DelegatesTo(value=Article, strategy = Closure.DELEGATE_ONLY) Closure closure) {

    }
}