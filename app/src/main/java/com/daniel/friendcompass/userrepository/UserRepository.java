package com.daniel.friendcompass.userrepository;

import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.daniel.friendcompass.models.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class UserRepository {
    private static final String TAG = UserRepository.class.getSimpleName();

    private static UserRepository instance;
    private static final User defaultUser = new User(
            "Auckland",
            -36.844178,
            174.767738,
            System.currentTimeMillis()
    );

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MutableLiveData<List<User>> users;
    private MutableLiveData<User> selectedUser;

    private UserRepository() { }

    public static UserRepository getInstance() {
        if (instance == null) instance = new UserRepository();
        return instance;
    }

    public void initialiseRequiredData() {
        if (users != null) return;

        getUsers();
        db.collection("users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null || queryDocumentSnapshots == null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        List<User> newUsers = new ArrayList<>();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            Map<String, Object> data = document.getData();

                            if (data == null) continue;

                            User user;
                            if (data.containsKey("latitude")) {
                                user = new User(
                                        String.valueOf(data.get("name")),
                                        (double) data.get("latitude"),
                                        (double) data.get("longitude"),
                                        (long) data.get("timestamp")
                                );
                            } else {
                                user = new User(
                                        String.valueOf(data.get("name"))
                                );
                            }
                            newUsers.add(user);
                        }
                        Collections.sort(newUsers);
                        users.setValue(newUsers);
                    }
                });
    }

    public MutableLiveData<User> getSelectedUser() {
        if (selectedUser == null) {
            selectedUser = new MutableLiveData<>();
            selectedUser.setValue(defaultUser);
        }
        return selectedUser;
    }

    public void setSelectedUser(User user) {
        selectedUser.setValue(user);
    }

    public MutableLiveData<List<User>> getUsers() {
        if (users == null) users = new MutableLiveData<>();
        return users;
    }
}
