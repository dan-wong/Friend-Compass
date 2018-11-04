package com.daniel.friendcompass.userstore;

import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class UserStore {
    private static final String TAG = UserStore.class.getSimpleName();

    private static final UserStore instance = new UserStore();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private UserStore() {
    }

    public static UserStore getInstance() {
        return instance;
    }

    public void getUser(String id, final UserStoreCallback callback) {
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
