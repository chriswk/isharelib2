package com.chriswk.isharelib.data;

import org.neo4j.helpers.collection.IteratorUtil;
import org.springframework.data.neo4j.annotation.*;
import org.springframework.data.neo4j.support.index.IndexType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static org.neo4j.graphdb.Direction.OUTGOING;
import static org.neo4j.graphdb.Direction.INCOMING;

@NodeEntity
public class Movie {
    @GraphId
    Long nodeId;

    @Indexed(unique = true)
    Long tmdbId;

    @Indexed(indexType = IndexType.FULLTEXT, indexName = "titleSearch")
    String title;

    String description;

    @RelatedTo(type = "GENRE", direction = OUTGOING)
    Set<Genre> genres;

    @RelatedTo(type = "RECORDED_IN", direction = OUTGOING)
    Set<Country> countries;

    @RelatedTo(type="DIRECTED", elementClass = Person.class, direction = INCOMING)
    Set<Person> directors;

    @RelatedTo(type="ACTS_IN", direction = INCOMING)
    Set<Person> actors;

    @RelatedTo(type = "SPOKEN", direction = OUTGOING)
    Set<Language> languages = new HashSet<>();

    @RelatedToVia(type = "ACTS_IN", direction = INCOMING)
    Iterable<Role> roles;

    @RelatedToVia(type = "RATED", direction = INCOMING)
    @Fetch Iterable<Rating> ratings;

    private String language;
    private String imdbId;
    private String tagline;
    private LocalDate releaseDate;
    private Integer runtime;
    private String homepage;
    private String trailer;
    private String studio;
    private Integer version;
    private LocalDate lastModified;
    private String imageUrl;


    public Movie() {}

    public Movie(Long tmdbId, String title) {
        this.tmdbId = tmdbId;
        this.title = title;
    }

    public Collection<Person> getActors() {
        return actors;
    }

    public Collection<Role> getRoles() {
        return IteratorUtil.asCollection(roles);
    }

    public Long getTmdbId() {
        return tmdbId;
    }

    public Long getNodeId() {
        return nodeId;
    }

    public String getTitle() {
        return title;
    }

    public Set<Country> getCountries() {
        return countries;
    }

    public Optional<Integer> getYear() {
        return Optional.ofNullable(releaseDate).map((d) -> d.getYear());
    }

    public String toString() {
        return String.format("%s (%s) [%s]", title, releaseDate, tmdbId);
    }

    public String getDescription() {
        return description;
    }

    public double getStars() {
        return IteratorUtil.asCollection(ratings)
            .stream()
            .mapToInt((rating) -> rating.getStars())
            .average().orElse(0d);
    }

    public Collection<Rating> getRatings() {
        Iterable<Rating> allRatings = ratings;
        return allRatings == null ? Collections.<Rating>emptyList() : IteratorUtil.asCollection(allRatings);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    public void setStudio(String studio) {
        this.studio = studio;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setLastModified(LocalDate lastModified) {
        this.lastModified = lastModified;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLanguage() {
        return language;
    }

    public String getImdbId() {
        return imdbId;
    }

    public String getTagline() {
        return tagline;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public Integer getRuntime() {
        return runtime;
    }

    public String getHomepage() {
        return homepage;
    }

    public String getTrailer() {
        return trailer;
    }

    public String getStudio() {
        return studio;
    }

    public Integer getVersion() {
        return version;
    }

    public LocalDate getLastModified() {
        return lastModified;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getYoutubeId() {
        String trailerUrl = trailer;
        if (trailerUrl==null || !trailerUrl.contains("youtu")) return null;
        String[] parts = trailerUrl.split("[=/]");
        int numberOfParts = parts.length;
        return numberOfParts > 0 ? parts[numberOfParts-1] : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Movie movie = (Movie) o;
        if (nodeId == null) return super.equals(o);
        return nodeId.equals(movie.nodeId);

    }

    @Override
    public int hashCode() {
        return nodeId != null ? nodeId.hashCode() : super.hashCode();
    }

    public Set<Person> getDirectors() {
        return directors;
    }

    public Set<Genre> getGenres() {
        return genres;
    }

    public Set<Language> getLanguages() {
        return languages;
    }

    public void addLanguage(Language language) {
        if (this.languages == null) {
            this.languages = new HashSet<Language>();
        }
        this.languages.add(language);
    }
}
