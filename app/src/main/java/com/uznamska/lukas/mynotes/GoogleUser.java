/*
 * Copyright (c) 2016.  Lukasz Fiszer
 */

package com.uznamska.lukas.mynotes;


import android.util.Log;

class GoogleUser {
    private final String mNameSurname;
    private final String mImageUrl;
    private final String mUserId;
    private final String mEmail;

    public GoogleUser(UserBuilder builder) {
        this.mNameSurname = builder.mNameSurname;
        this.mUserId = builder.mUserId;
        this.mImageUrl = builder.mImageUrl;
        this.mEmail = builder.mEmail;
    }

    public String getNameSurname() {
        return mNameSurname;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getUserId() {
        return mUserId;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public static class UserBuilder {
        private final  String mNameSurname;
        private String mImageUrl;
        private String mUserId;
        private String mEmail;

        public UserBuilder(String mNameSurname) {
            this.mNameSurname = mNameSurname;
        }

        UserBuilder userId(String id){
            this.mUserId = id;
            return this;
        }

        UserBuilder imageUrl(String url){
            this.mImageUrl = url;
            return this;
        }

        UserBuilder email(String email) {
            Log.d("GGG", "FFFF000");
            this.mEmail = email;
            return this;
        }

        GoogleUser build() {
            return  new GoogleUser(this);
        }


    }
}

