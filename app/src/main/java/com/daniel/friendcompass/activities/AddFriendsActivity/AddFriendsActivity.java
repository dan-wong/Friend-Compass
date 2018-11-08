package com.daniel.friendcompass.activities.AddFriendsActivity;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.daniel.friendcompass.R;
import com.daniel.friendcompass.misc.DividerItemDecoration;
import com.daniel.friendcompass.models.User;
import com.daniel.friendcompass.userrepository.UserRepository;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddFriendsActivity extends AppCompatActivity {
    @BindView(R.id.recyclerView) RecyclerView recyclerView;

    private AddFriendsRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friends);
        ButterKnife.bind(this);

        setTitle("Add Friends");

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getDrawable(R.drawable.drawable_divider)));

        final Observer<List<User>> usersObserver = new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                adapter = getNewAdapter(users);
                recyclerView.setAdapter(adapter);
            }
        };

        UserRepository.getInstance().getUsers().observe(this, usersObserver);
    }

    private AddFriendsRecyclerViewAdapter getNewAdapter(List<User> users) {
        return new AddFriendsRecyclerViewAdapter(users);
    }
}
