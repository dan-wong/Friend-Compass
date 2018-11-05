package com.daniel.friendcompass.activities.UserActivity;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.daniel.friendcompass.R;
import com.daniel.friendcompass.activities.UserActivity.adapter.UserRecyclerViewAdapter;
import com.daniel.friendcompass.models.User;
import com.daniel.friendcompass.userrepository.UserRepository;
import com.daniel.friendcompass.util.DividerItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserActivity extends AppCompatActivity implements UserRecyclerViewAdapter.UserActivityListener {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private UserRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ButterKnife.bind(this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getDrawable(R.drawable.drawable_divider)));

        final Observer<List<User>> usersObserver = new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                adapter = getNewAdapter(users);
                recyclerView.setAdapter(adapter);
                progressBar.setVisibility(View.INVISIBLE);
            }
        };

        UserRepository.getInstance().getUsers().observe(this, usersObserver);
    }

    private UserRecyclerViewAdapter getNewAdapter(List<User> users) {
        return new UserRecyclerViewAdapter(this, users, this);
    }

    @Override
    public void userSelected(User user) {
        UserRepository.getInstance().setSelectedUser(user);
        finish();
    }
}