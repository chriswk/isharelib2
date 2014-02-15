package com.chriswk.isharelib.domain;

import com.chriswk.isharelib.data.*;
import com.chriswk.isharelib.repository.MovieRepository;
import com.chriswk.isharelib.repository.PersonRepository;
import com.chriswk.isharelib.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/isharelib-data-test-context.xml"})
@Transactional
public class DomainTests {

    @Autowired
    protected MovieRepository movieRepository;
    @Autowired
    protected UserRepository userRepository;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    Neo4jTemplate template;

    @Test
    public void actorCanPlayARoleInAMovie() {
        Person tomHanks = template.save(new Person(1L, "Tom Hanks"));
        Movie forestGump = template.save(new Movie(1L, "Forrest Gump"));

        Role role = tomHanks.playedIn(forestGump, "Forrest");
        template.save(role);

        Movie foundForestGump = this.movieRepository.findByTmdbId(1L);

        assertEquals("created and looked up movie equal", forestGump, foundForestGump);
        Role firstRole = foundForestGump.getRoles().iterator().next();
        assertEquals("role forrest",role, firstRole);
        assertEquals("role forrest","Forrest", firstRole.getName());
    }



    @Test
    public void canFindMovieByTitleQuery() {
        Movie forestGump = template.save(new Movie(1L, "Forrest Gump"));
        Iterator<Movie> queryResults = movieRepository.findAllByQuery("titleSearch", "title", "Forre*").iterator();
        assertTrue("found movie by query",queryResults.hasNext());
        Movie foundMovie = queryResults.next();
        assertEquals("created and looked up movie equal", forestGump, foundMovie);
        assertFalse("found only one movie by query", queryResults.hasNext());
    }

    @Test
    public void userCanRateMovie() {
        Movie movie = template.save(new Movie(1L, "Forrest Gump"));
        User user = template.save(new User("ich", "Micha", "password"));
        Rating awesome = user.rate(template,movie, 5, "Awesome");

        user = userRepository.findByPropertyValue("login", "ich");
        movie = movieRepository.findByTmdbId(1L);
        Rating rating = user.getRatings().iterator().next();
        assertEquals(awesome,rating);
        assertEquals("Awesome",rating.getComment());
        assertEquals(5,rating.getStars());
        assertEquals(5,movie.getStars(),0);
    }
    @Test
    public void canFindUserByLogin() {
        User user = template.save(new User("ich", "Micha", "password"));
        User foundUser = userRepository.findByPropertyValue("login", "ich");
        assertEquals(user, foundUser);
    }
}
