package com.daniel.friendcompass.userrepository;

import com.daniel.friendcompass.models.User;

import java.util.List;
import java.util.Map;

public interface UserRepositoryCallback {
    void userDataReceived(Map<String, Object> data);

    void userListReceived(List<User> users);
}
