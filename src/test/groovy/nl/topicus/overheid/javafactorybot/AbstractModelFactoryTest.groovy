package nl.topicus.overheid.javafactorybot

import nl.topicus.overheid.javafactorybot.test.factory.ArticleFactory
import nl.topicus.overheid.javafactorybot.test.factory.CommentFactory
import nl.topicus.overheid.javafactorybot.test.factory.UserFactory
import nl.topicus.overheid.javafactorybot.test.model.Article
import nl.topicus.overheid.javafactorybot.test.model.Comment
import nl.topicus.overheid.javafactorybot.test.model.User
import spock.lang.Specification

class AbstractModelFactoryTest extends Specification {

    def "it fills the attributes when no build parameters are specified"() {
        when:
        Article article = ArticleFactory.instance.build()

        then:
        article.title != null
        article.content != null
        article.creationDate != null
        article.author != null
        article.author instanceof User
        article.comments != null
        article.comments.size() == 3
        article.comments.each {
            assert it instanceof Comment
        }
    }

    def "derp"() {
        expect:
        def user = UserFactory.instance.build()
        user
    }

}
