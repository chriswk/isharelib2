package com.chriswk.isharelib.service;

import java.util.Arrays;
import java.util.List;

import com.chriswk.isharelib.data.Movie;
import com.chriswk.isharelib.data.Person;
import com.chriswk.isharelib.repository.MovieRepository;
import com.chriswk.isharelib.repository.PersonRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/isharelib-data-test-context.xml"})
@Transactional
public class TmdbImportServiceTest {
    @Autowired
    TmdbImportService importService;

    @Autowired
    MovieRepository movieRepository;

    @Autowired
    PersonRepository personRepository;

    @Test
    public void testImportMovie() throws Exception {
        List<Movie> movie = importService.importMovie(2L, false);
        assertThat("Should only have one movie", movie.size(), is(1));
        assertThat("Id of movie should be 2", movie.get(0).getTmdbId(), is(2L));
    }

    @Test
    public void testImportMovieTwice() throws Exception {
        List<Movie> movies = importService.importMovies(Arrays.asList(2L, 2L), false);
        final Movie foundMovie = movieRepository.findByTmdbId(2L);
        assertEquals("movie-id", movies.get(0), foundMovie);
    }

    @Test
    public void testImportPerson() throws Exception {
        long personId = 105955L;
        List<Person> actors = importService.importPerson(personId);
        assertThat(actors.size(), is(1));
        Person actor = actors.get(0);
        assertThat(actor.getTmdbId(), is(personId));
        assertThat("person-name",actor.getName(), is("George M. Williamson"));
        final Person foundActor = personRepository.findByTmdbId(personId);
        assertEquals(actor, foundActor);
    }

    @Test
    public void shouldImportMovieWithTwoDirectors() throws Exception {
        List<Movie> movies = importService.importMovie(603L, true);
        assertThat(movies.size(), is(1));
        Movie importedMovie = movies.get(0);
        Movie foundMovie = movieRepository.findByTmdbId(importedMovie.getTmdbId());

        assertThat(foundMovie.getDirectors().size(), is(2));
    }
}
