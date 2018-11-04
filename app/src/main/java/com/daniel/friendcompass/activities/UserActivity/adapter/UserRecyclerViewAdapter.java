package com.daniel.friendcompass.activities.UserActivity.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.daniel.friendcompass.R;
import com.daniel.friendcompass.models.User;

import java.util.List;

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.UserViewHolder> {
    private List<User> users;

    public UserRecyclerViewAdapter(List<User> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_card, parent, false);
        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        final User user = users.get(position);
        holder.nameTextview.setText(user.getName());
        holder.lastUpdatedTextView.setText("Woohoo");
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), user.getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextview;
        public TextView lastUpdatedTextView;
        public CardView cardView;

        public UserViewHolder(View itemView) {
            super(itemView);
            nameTextview = itemView.findViewById(R.id.nameTextView);
            lastUpdatedTextView = itemView.findViewById(R.id.lastUpdatedTextView);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }


}
