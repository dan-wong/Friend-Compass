package com.daniel.friendcompass.userstore;

import java.util.Map;

public interface UserStoreCallback {
    void userDataReceived(Map<String, Object> data);
}
