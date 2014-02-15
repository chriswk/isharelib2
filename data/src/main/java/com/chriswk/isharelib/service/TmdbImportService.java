package com.chriswk.isharelib.service;

import com.chriswk.isharelib.data.Language;
import com.chriswk.isharelib.data.Movie;
import com.chriswk.isharelib.data.Person;
import com.chriswk.isharelib.data.Role;
import com.chriswk.isharelib.repository.MovieRepository;
import com.chriswk.isharelib.repository.PersonRepository;
import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.model.MovieDb;
import com.omertron.themoviedbapi.model.PersonCredit;
import com.omertron.themoviedbapi.model.PersonType;
import com.omertron.themoviedbapi.results.TmdbResultsList;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TmdbImportService {
    private static final Logger LOGGER = LogManager.getLogger(TmdbImportService.class);

    @Autowired
    private TheMovieDbApi movieDbApi;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private Neo4jTemplate template;

    List<Movie> importMovie(final Long id, final boolean getCast) {
        return importMovies(Arrays.asList(id), getCast);
    }

    List<Movie> importMovies(Collection<Long> ids, final boolean getCast) {
        return ids.stream()
                .map(id -> findOrCreateMovie(id))
                .map(movie -> fetchTmdbMovie(movie, getCast))
                .collect(Collectors.toList());
    }

    @Transactional
    public Movie findOrCreateMovie(final Long tmdbId) {
        return Optional.ofNullable(movieRepository.findByTmdbId(tmdbId)).orElseGet(() -> {
            Movie m = new Movie(tmdbId, "");
            return movieRepository.save(m);
        });
    }

    @Transactional
    public Movie fetchTmdbMovie(Movie outMovie, boolean getCast) {
        try {
            MovieDb tmdbMovie = movieDbApi.getMovieInfo(outMovie.getTmdbId().intValue(), "en");
            transfromFromTmdbMovie(outMovie, tmdbMovie);
            LOGGER.info("Saving {}", outMovie);
            outMovie = movieRepository.save(outMovie);
            if (getCast) {
                importCast(outMovie);
            }
        } catch (MovieDbException movieEx) {
            LOGGER.error("Excception while fetching movie: {}", outMovie, movieEx);
        }
        return outMovie;
    }

    private void transfromFromTmdbMovie(Movie outMovie, MovieDb tmdbMovie) {
        outMovie.setHomepage(tmdbMovie.getHomepage());
        outMovie.setImdbId(tmdbMovie.getImdbID());
        outMovie.addLanguages(tmdbMovie.getSpokenLanguages()
                .stream()
                .map(l -> new Language(l.getIsoCode(), l.getName()))
                .collect(Collectors.toList()));
        outMovie.setReleaseDate(LocalDate.parse(tmdbMovie.getReleaseDate()));
        outMovie.setTagline(tmdbMovie.getTagline());
        outMovie.setRuntime(tmdbMovie.getRuntime());
    }

    private void importCast(Movie outMovie) {
        try {
            TmdbResultsList<com.omertron.themoviedbapi.model.Person> cast =
                    movieDbApi.getMovieCasts(outMovie.getTmdbId().intValue());
            cast.getResults()
                    .stream()
                    .forEach(tmdbPerson -> {
                        Person p = findOrSavePerson(Long.valueOf(tmdbPerson.getId()));
                        if (tmdbPerson.getPersonType() == PersonType.CAST) {
                            Role role = p.playedIn(outMovie, tmdbPerson.getCharacter());
                            template.save(role);
                        } else if (tmdbPerson.getPersonType() == PersonType.CREW) {
                            if (tmdbPerson.getJob().equals("Director")) {
                                p.directed(outMovie);
                            }
                        }
                        personRepository.save(p);
                    });
        } catch (MovieDbException e) {
            e.printStackTrace();
        }
    }

    List<Person> importPerson(final Long id) {
        return importPeople(Arrays.asList(id));
    }

    private List<Person> importPeople(List<Long> ids) {
        return ids.stream()
                .map(id -> findOrSavePerson(id))
                .map(person -> fetchTmdbPerson(person))
                .collect(Collectors.toList());
    }

    private Person fetchTmdbPerson(Person person) {
        try {
            com.omertron.themoviedbapi.model.Person personInfo = movieDbApi.getPersonInfo(person.getTmdbId().intValue());
            transformFromTmdbPerson(person, personInfo);
            person = personRepository.save(person);
        } catch (MovieDbException e) {
            LOGGER.error("Excception while fetching movie: {}", person, e);
        }
        return person;
    }

    private void transformFromTmdbPerson(Person person, com.omertron.themoviedbapi.model.Person personInfo) {
        person.setAka(personInfo.getAka());
        person.setBiography(personInfo.getBiography());
        try {

            if (StringUtils.isNotEmpty(personInfo.getBirthday())) {
                person.setBirthday(LocalDate.parse(personInfo.getBirthday()));
            }
            if (StringUtils.isNotEmpty(personInfo.getDeathday())) {
                person.setDeathDay(LocalDate.parse(personInfo.getDeathday()));
            }
        } catch (DateTimeParseException dt) {
            LOGGER.error("exception while parsing {}", personInfo.getBirthday(), dt);
        }
        person.setBirthplace(personInfo.getBirthplace());
        person.setName(personInfo.getName());
        person.setProfileImageUrl(personInfo.getProfilePath());

        person.setImdbId(personInfo.getImdbId());
    }

    private Person findOrSavePerson(final Long id) {
        return Optional.ofNullable(personRepository.findByTmdbId(id)).orElseGet(() -> {
            Person p = new Person(id);
            return personRepository.save(p);
        });
    }

    private void buildHistory(Person person) {
        try {
            TmdbResultsList<PersonCredit> credits = movieDbApi.getPersonCredits(person.getTmdbId().intValue());
            credits.getResults().forEach(credit -> addCredit(person, credit));
        } catch (MovieDbException e) {
            LOGGER.error("Exception while fetching credits for {}", person, e);
        }
    }

    @Transactional
    private void addCredit(Person person, PersonCredit credit) {
        Movie movie = findOrCreateMovie(Long.valueOf(credit.getMovieId()));
        movie.setTitle(credit.getMovieTitle());
        switch (credit.getPersonType()) {
            case CAST:
                addRole(person, movie, credit);
                break;
            case CREW:
                if (credit.getJob().equals("Director")) {
                    addDirected(person, movie);
                }
                break;
            default:
                break;
        }
    }

    private void addRole(Person person, Movie movie, PersonCredit credit) {
        Role role = person.playedIn(movie, credit.getCharacter());
        template.save(role);
    }

    @Transactional
    private void addDirected(Person person, Movie movie) {
        person.directed(movie);
        personRepository.save(person);
    }


}
