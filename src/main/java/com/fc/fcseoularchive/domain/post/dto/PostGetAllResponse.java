package com.fc.fcseoularchive.domain.post.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PostGetAllResponse {

    private final List<PostResponse> posts;

    private final int win;

    private final int draw;

    private final int lose;

    private final int count;

    public PostGetAllResponse(List<PostResponse> posts, int win, int draw, int lose, int count) {
        this.posts = posts;
        this.win = win;
        this.draw = draw;
        this.lose = lose;
        this.count = count;
    }
}
