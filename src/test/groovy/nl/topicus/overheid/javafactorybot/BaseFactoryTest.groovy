package nl.topicus.overheid.javafactorybot

import nl.topicus.overheid.javafactorybot.test.factory.ArticleFactory
import nl.topicus.overheid.javafactorybot.test.model.Article
import nl.topicus.overheid.javafactorybot.test.model.User
import spock.lang.Specification
import spock.lang.Subject

class BaseFactoryTest extends Specification {
    @Subject
    ArticleFactory factory = new ArticleFactory()

    def "it fills the attributes when no overrides are given"() {
        when:
        Article article = new ArticleFactory().build()

        then: "defined attributes with default are filled"
        article.title != null
        article.content != null
        article.creationDate != null

        and: "defined attributes with default null are null"
        article.summary == null

        and: "relations are set"
        article.author != null
        article.author instanceof User
        article.comments != null
        article.comments.size() == 0

        and: "nondefined attributes are not filled"
        article.slug == null
    }

    def "it uses overrides"() {
        when:
        Article article = factory.build([title: "foo", slug: "derp"])

        then: "overrides are used"
        article.title == "foo"
        article.slug == "derp"

        and: "remaining attributes are filled as default"
        article.content != null
        article.creationDate != null
        article.summary == null
        article.author != null
        article.author instanceof User
        article.comments != null
        article.comments.size() == 0
    }

    def "when creating the create context is used"() {
        given:
        FactoryContext createContext = Mock(FactoryContext)
        FactoryManager.instance.createContext = createContext

        when:
        Article article = new ArticleFactory().create()

        then:
        1 * createContext.persist(_ as User) >> { User a -> a }
        1 * createContext.persist(_ as Article) >> { Article a -> a }
        article != null

        then: "defined attributes with default are filled"
        article.title != null
        article.content != null
        article.creationDate != null

        and: "defined attributes with default null are null"
        article.summary == null

        and: "relations are set"
        article.author != null
        article.author instanceof User
        article.comments != null
        article.comments.size() == 0
    }
}
