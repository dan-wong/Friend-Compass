package com.daniel.friendcompass.userrepository;

import android.arch.lifecycle.MutableLiveData;
import android.location.Location;
import android.util.Log;

import com.daniel.friendcompass.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class UserRepository {
    private static final String TAG = UserRepository.class.getSimpleName();

    private static UserRepository instance;
    private static final User defaultUser = new User(
            "default",
            "Auckland",
            -36.844178,
            174.767738,
            System.currentTimeMillis()
    );

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private MutableLiveData<List<User>> users;
    private MutableLiveData<User> selectedUser;

    private UserRepository() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public static UserRepository getInstance() {
        if (instance == null) instance = new UserRepository();
        return instance;
    }

    public void initialiseRequiredData() {
        if (users != null) return;

        getUsers();
        db.collection("users")
                .orderBy("name")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null || queryDocumentSnapshots == null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        List<User> newUsers = new ArrayList<>();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            if (document.getId().equals(auth.getCurrentUser().getUid())) continue;
                            Map<String, Object> data = document.getData();

                            if (data == null) continue;

                            User user;
                            if (data.containsKey("location")) {
                                GeoPoint geoPoint = (GeoPoint) data.get("location");
                                Date date = (Date) data.get("timestamp");

                                user = new User(
                                        document.getId(),
                                        String.valueOf(data.get("name")),
                                        geoPoint.getLatitude(),
                                        geoPoint.getLongitude(),
                                        date.getTime()
                                );
                            } else {
                                user = new User(
                                        String.valueOf(data.get("name"))
                                );
                            }
                            newUsers.add(user);
                        }
                        users.setValue(newUsers);
                    }
                });
    }

    public void resetUsersList() {
        users = null;
    }

    public MutableLiveData<User> getSelectedUser() {
        if (selectedUser == null) {
            selectedUser = new MutableLiveData<>();
            selectedUser.setValue(defaultUser);
        }
        return selectedUser;
    }

    public void setSelectedUser(User user) {
        db.collection("users").document(user.getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) return;
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            Map<String, Object> data = documentSnapshot.getData();

                            if (data == null) return;
                            GeoPoint geoPoint = (GeoPoint) data.get("location");
                            Date date = (Date) data.get("timestamp");

                            User user = new User(
                                    documentSnapshot.getId(),
                                    String.valueOf(data.get("name")),
                                    geoPoint.getLatitude(),
                                    geoPoint.getLongitude(),
                                    date.getTime()
                            );

                            selectedUser.setValue(user);
                        }
                    }
                });
    }

    public MutableLiveData<List<User>> getUsers() {
        if (users == null) users = new MutableLiveData<>();
        return users;
    }

    public void createNewUser(FirebaseUser user) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", user.getDisplayName());
        userData.put("email", user.getEmail());

        db.collection("users")
                .document(user.getUid())
                .set(userData, SetOptions.merge());
    }

    public void updateUserLocation(Location location) {
        DocumentReference docRef = db.collection("users")
                .document(auth.getCurrentUser().getUid());

        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        docRef.update(
                "location", geoPoint,
                "timestamp", new Date(location.getTime())
        );
    }
}
