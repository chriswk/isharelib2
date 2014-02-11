package com.chriswk.isharelib.data;

import org.springframework.data.neo4j.annotation.ResultColumn;

public interface MovieRecommendation {
    @ResultColumn("otherMovie")
    Movie getMovie();

    @ResultColumn("rating")
    int getRating();
}
