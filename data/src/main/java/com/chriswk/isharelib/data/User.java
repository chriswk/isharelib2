package com.chriswk.isharelib.data;

import org.neo4j.graphdb.Direction;
import org.neo4j.helpers.collection.IteratorUtil;
import org.springframework.data.neo4j.annotation.*;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collection;
import java.util.Set;

public class User {
    @GraphId
    Long nodeId;

    public static final String FRIEND = "FRIEND";
    public static final String RATED = "RATED";

    @Indexed(unique = true)
    String login;

    String name;
    String password;
    String info;
    private Roles[] roles;

    public User() {
    }

    public User(String login, String name, String password, Roles... roles) {
        this.login = login;
        this.name = name;
        this.password = password;
        this.roles = roles;
    }

    public String encode(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

    @RelatedToVia(type = RATED)
    @Fetch
    Iterable<Rating> ratings;

    @RelatedTo(type = RATED)
    Set<Movie> favorites;

    @RelatedTo(type = FRIEND, direction = Direction.BOTH)
    @Fetch Set<User> friends;

    public void addFriend(User friend) {
        this.friends.add(friend);
    }

    public Rating rate(Neo4jOperations template, Movie movie, int stars, String comment) {
        final Rating rating = template.createRelationshipBetween(this, movie, Rating.class, RATED, false).rate(stars, comment);
        return template.save(rating);
    }

    public Collection<Rating> getRatings() {
        return IteratorUtil.asCollection(ratings);
    }

    @Override
    public String toString() {
        return String.format("%s (%s) [%s]", name, login, nodeId);
    }

    public String getName() {
        return name;
    }

    public Set<User> getFriends() {
        return friends;
    }

    public Roles[] getRole() {
        return roles;
    }


    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getInfo() {
        return info;
    }
    public void setInfo(String info) {
        this.info = info;
    }

    public void updatePassword(String old, String newPass1, String newPass2) {
        if (!password.equals(encode(old))) throw new IllegalArgumentException("Existing Password invalid");
        if (!newPass1.equals(newPass2)) throw new IllegalArgumentException("New Passwords don't match");
        this.password = encode(newPass1);
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFriend(User other) {
        return other!=null && getFriends().contains(other);
    }

    public enum Roles implements GrantedAuthority {
        ROLE_USER, ROLE_ADMIN;

        @Override
        public String getAuthority() {
            return name();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;
        if (nodeId == null) return super.equals(o);
        return nodeId.equals(user.nodeId);

    }

    public Long getId() {
        return nodeId;
    }

    @Override
    public int hashCode() {

        return nodeId != null ? nodeId.hashCode() : super.hashCode();
    }
}
