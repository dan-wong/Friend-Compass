package com.daniel.friendcompass.userrepository;

import android.arch.lifecycle.MutableLiveData;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.daniel.friendcompass.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepository {
    private static final String TAG = UserRepository.class.getSimpleName();

    private static UserRepository instance;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MutableLiveData<List<User>> users;
    private User selectedUser = new User(
            "Auckland",
            -36.844178,
            174.767738,
            System.currentTimeMillis()
    );

    private UserRepository() {
    }

    public static UserRepository getInstance() {
        if (instance == null) instance = new UserRepository();
        return instance;
    }

    public void initialiseRequiredData() {
        if (users == null) getUsers();
    }

    public User getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(User user) {
        this.selectedUser = user;
    }

    public MutableLiveData<List<User>> getUsers() {
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful())
                            Log.d(TAG, "Error getting documents: ", task.getException());

                        List<User> newUsers = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();

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

        if (users == null) users = new MutableLiveData<>();
        return users;
    }

    public void getUser(String id, final UserRepositoryCallback callback) {
        DocumentReference docRef = db.collection("users").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        callback.userDataReceived(document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "GET failed with ", task.getException());
                }
            }
        });
    }

    public void createUser(String firstname, String lastname) {
        Map<String, Object> user = new HashMap<>();
        user.put("first_name", firstname);
        user.put("last_name", lastname);

        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void updateUserLocation(String id, Location location) {
        Map<String, Object> data = new HashMap<>();
        data.put("location", location.getLatitude() + "," + location.getLongitude());
        data.put("timestamp", location.getTime());

        db.collection("users").document(id).set(data, SetOptions.merge());
    }
}
