package com.daniel.friendcompass.models;

import java.util.List;

public class CurrentUser {
    private final String uid;
    private final String name;
    private final List<String> trustedUsers;

    public CurrentUser(String uid, String name, List<String> trustedUsers) {
        this.uid = uid;
        this.name = name;
        this.trustedUsers = trustedUsers;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public List<String> getTrustedUsers() {
        return trustedUsers;
    }

    public void addTrustedUser(User user) {
        if (trustedUsers.contains(user.getUid())) return;
        trustedUsers.add(user.getUid());
    }

    public void removeTrustedUser(User user) {
        trustedUsers.remove(user.getUid());
    }
}
