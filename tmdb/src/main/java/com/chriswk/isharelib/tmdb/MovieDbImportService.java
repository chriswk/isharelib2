package com.chriswk.isharelib.tmdb;

import com.chriswk.isharelib.data.Language;
import com.chriswk.isharelib.data.Movie;
import com.chriswk.isharelib.repository.MovieRepository;
import com.chriswk.isharelib.repository.PersonRepository;
import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.model.MovieDb;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class MovieDbImportService {
    private static final Logger LOGGER = LogManager.getLogger(MovieDbImportService.class);

    @Autowired
    private TheMovieDbApi movieDbApi;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private Neo4jTemplate template;

    List<Movie> importMovies(final boolean getCast, Collection<Long> ids) {
        return ids.stream()
           .map(id -> findOrSaveMovie(id))
           .map(movie -> fetchTmdbMovie(movie, getCast))
           .collect(Collectors.toList());
    }

    @Transactional
    public Movie findOrSaveMovie(final Long tmdbId) {
        return Optional.ofNullable(movieRepository.findByTmdbId(tmdbId)).orElseGet(() -> {
            Movie m = new Movie(tmdbId, "");
            return movieRepository.save(m);
        });
    }

    @Transactional
    public Movie fetchTmdbMovie(Movie outMovie, boolean getCast) {
        try {
            MovieDb tmdbMovie = movieDbApi.getMovieInfo(outMovie.getTmdbId().intValue(), "en");
            outMovie.setHomepage(tmdbMovie.getHomepage());
            outMovie.setImdbId(tmdbMovie.getImdbID());
            tmdbMovie.getSpokenLanguages().stream()
                    .map(l -> new Language(l.getIsoCode(), l.getName()))
                    .forEach(l -> outMovie.addLanguage(l));
            outMovie.setReleaseDate(LocalDate.parse(tmdbMovie.getReleaseDate()));
            outMovie.setTagline(tmdbMovie.getTagline());
            outMovie.setRuntime(tmdbMovie.getRuntime());
            LOGGER.info("Saving {}", outMovie);
            return movieRepository.save(outMovie);
        } catch (MovieDbException movieEx) {
            LOGGER.error("Excception while fetching movie: {}", outMovie, movieEx);
        }
        return outMovie;
    }
}
