package nl.topicus.overheid.javafactorybot.test.factory

import nl.topicus.overheid.javafactorybot.AbstractModelFactory
import nl.topicus.overheid.javafactorybot.test.model.Comment
import nl.topicus.overheid.javafactorybot.test.model.User

import java.util.concurrent.TimeUnit

@Singleton
class CommentFactory extends AbstractModelFactory<Comment> {
    Map<String, Closure> fields = [
//            article     : belongsTo(ArticleFactory.instance),
//            author      : hasOne(UserFactory.instance),
            content     : attribute { faker.lorem().paragraph() },
            creationDate: attribute { faker.date().past(20, TimeUnit.DAYS) }
    ]
}
