package com.daniel.friendcompass.activities.UserActivity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.daniel.friendcompass.util.DividerItemDecoration;
import com.daniel.friendcompass.R;
import com.daniel.friendcompass.models.User;
import com.daniel.friendcompass.activities.UserActivity.adapter.UserRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserActivity extends AppCompatActivity {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private List<User> users;
    private UserRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ButterKnife.bind(this);

        users = new ArrayList<>();
        prepareUsers();
        adapter = new UserRecyclerViewAdapter(users);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getDrawable(R.drawable.drawable_divider)));
        recyclerView.setAdapter(adapter);
    }

    private void prepareUsers() {
        User user = new User("Daniel Wong");
        users.add(user);

        user = new User("Sharon Wong");
        users.add(user);

        user = new User("John Doe");
        users.add(user);
    }
}
