package com.gprovs.earnestly;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

class CommentId {

    @Exclude
    public String CommentId;

    public <T extends CommentId> T withId(@NonNull final String id) {
        this.CommentId = id;
        return (T) this;
    }
}
