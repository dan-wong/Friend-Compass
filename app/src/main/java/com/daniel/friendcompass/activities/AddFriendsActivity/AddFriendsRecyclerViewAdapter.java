package com.daniel.friendcompass.activities.AddFriendsActivity;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.daniel.friendcompass.R;
import com.daniel.friendcompass.models.User;
import com.daniel.friendcompass.userrepository.UserRepository;

import java.util.List;

public class AddFriendsRecyclerViewAdapter extends RecyclerView.Adapter<AddFriendsRecyclerViewAdapter.AddFriendsViewHolder> {
    private static final String TAG = AddFriendsRecyclerViewAdapter.class.getSimpleName();

    public List<User> usersList;

    public AddFriendsRecyclerViewAdapter(List<User> usersList) {
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public AddFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.add_friend_card, parent, false);
        return new AddFriendsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final AddFriendsViewHolder holder, int position) {
        final User user = usersList.get(position);
        holder.nameTextView.setText(user.getName());
        holder.selectFriendCheckBox.setChecked(UserRepository.getInstance().getCurrentUser().getTrustedUsers().contains(user.getUid()));

        holder.selectFriendCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                userChecked(user, holder.selectFriendCheckBox.isChecked());
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.selectFriendCheckBox.setChecked(!holder.selectFriendCheckBox.isChecked());
                userChecked(user, holder.selectFriendCheckBox.isChecked());
            }
        });
    }

    private void userChecked(User user, boolean checked) {
        if (checked) {
            UserRepository.getInstance().getCurrentUser().addTrustedUser(user);
        } else {
            UserRepository.getInstance().getCurrentUser().removeTrustedUser(user);
        }
    }

    @Override
    public int getItemCount() {
        return usersList == null ? 0 : usersList.size();
    }

    public class AddFriendsViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public CheckBox selectFriendCheckBox;
        public CardView cardView;

        AddFriendsViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            selectFriendCheckBox = itemView.findViewById(R.id.selectFriendCheckBox);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
