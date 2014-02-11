package com.chriswk.isharelib.data;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.springframework.data.neo4j.annotation.RelatedToVia;
import org.springframework.data.neo4j.support.index.IndexType;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Person {
    @GraphId
    Long nodeId;

    @Indexed(unique = true)
    Long tmdbId;


    @Indexed(indexType = IndexType.FULLTEXT, indexName = "people")
    String name;

    private LocalDate birthday;

    private String birthplace;
    private String biography;
    private Integer version;
    private LocalDate lastModified;
    private String profileImageUrl;


    @RelatedTo(elementClass = Movie.class, type = "DIRECTED")
    private Set<Movie> directedMovies = new HashSet<>();

    @RelatedToVia
    Collection<Role> roles;

    public Person(Long tmdbId, String name) {
        this.tmdbId = tmdbId;
        this.name = name;
    }

    public Person(Long tmdbId) {
        this.tmdbId = tmdbId;
    }
    protected Person() {
    }

    public Long getTmdbId() {
        return tmdbId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("%s [%s]", name, tmdbId);
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public void setBirthplace(String birthplace) {
        this.birthplace = birthplace;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setLastModified(LocalDate lastModified) {
        this.lastModified = lastModified;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public String getBirthplace() {
        return birthplace;
    }

    public String getBiography() {
        return biography;
    }

    public Integer getVersion() {
        return version;
    }

    public LocalDate getLastModified() {
        return lastModified;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public Role playedIn(Movie movie, String roleName) {
        final Role role = new Role(movie, this, roleName);
        if (roles == null) {
            roles = new HashSet<>();
        }
        roles.add(role);
        return role;
    }

    public Set<Movie> getDirectedMovies() {
        return directedMovies;
    }

    public void directed(Movie movie) {
        this.directedMovies.add(movie);
    }

    public Iterable<Role> getRoles() {
        return roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;
        if (nodeId == null) return super.equals(o);
        return nodeId.equals(person.nodeId);

    }

    @Override
    public int hashCode() {
        return nodeId != null ? nodeId.hashCode() : super.hashCode();
    }
}
