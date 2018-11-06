package com.daniel.friendcompass.activities.UserActivity.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daniel.friendcompass.R;
import com.daniel.friendcompass.models.User;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.List;

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.UserViewHolder> {
    private List<User> usersList;
    private UserActivityListener listener;

    public UserRecyclerViewAdapter(List<User> usersList, UserActivityListener listener) {
        this.usersList = usersList;
        this.listener = listener;
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
        final User user = usersList.get(position);
        holder.nameTextView.setText(user.getName());
        holder.locationTextView.setText(user.getAddress());

        if (user.getTimestamp() == 0) {
            holder.lastUpdatedTextView.setText(R.string.no_location_found);
        } else {
            holder.lastUpdatedTextView.setText(String.format("Last updated %s", new PrettyTime().format(new Date(user.getTimestamp()))));
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.userSelected(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList == null ? 0 : usersList.size();
    }

    public interface UserActivityListener {
        void userSelected(User user);
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView locationTextView;
        public TextView lastUpdatedTextView;
        public CardView cardView;

        UserViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            lastUpdatedTextView = itemView.findViewById(R.id.lastUpdatedTextView);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
