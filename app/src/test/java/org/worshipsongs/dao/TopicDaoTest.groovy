package org.worshipsongs.dao

import spock.lang.Specification

/**
 *  Author : Madasamy
 *  Version : 4.x
 */
class TopicDaoTest extends Specification
{
    def topicDao = new TopicDao();

    def "Parse tamil topic name"()
    {
        given:
        def name = "Foo={இடைவிடா நன்றி உமக்குத்தான}"

        when:
        def result = topicDao.parseTamilTopicName(name)

        then:
        result == "இடைவிடா நன்றி உமக்குத்தான"
    }

    def "Parse tamil topic name from default"()
    {
        given:
        def name = "Foo"

        when:
        def result = topicDao.parseTamilTopicName(name)

        then:
        result == "Foo"
    }

    def "Parse tamil topic name from null"()
    {
        setup:
        def result = topicDao.parseTamilTopicName(null)

        expect:
        result == ""
    }

    def "Parse tamil topic name from empty"()
    {
        setup:
        def result = topicDao.parseTamilTopicName("")

        expect:
        result == ""
    }

    def "Parse default name"()
    {
        given:
        def name = "Foo"

        when:
        def result = topicDao.parseTamilTopicName(name)

        then:
        result == "Foo"
    }

    def "Parse default name when tamil name not defined "()
    {
        given:
        def name = "Foo bar "

        when:
        def result = topicDao.parseDefaultName(name)

        then:
        result == "Foo bar "
    }

    def "Parse default topic name from null"()
    {
        setup:
        def result = topicDao.parseDefaultName(null)

        expect:
        result == ""
    }

    def "Parse default topic name from empty"()
    {
        setup:
        def result = topicDao.parseDefaultName(null)

        expect:
        result == ""
    }
}
