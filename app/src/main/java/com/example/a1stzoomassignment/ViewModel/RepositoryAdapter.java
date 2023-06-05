package com.example.a1stzoomassignment.ViewModel;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.a1stzoomassignment.Model.Repository;
import com.example.a1stzoomassignment.R;

import java.util.List;

public class RepositoryAdapter extends RecyclerView.Adapter<RepositoryAdapter.RepositoryViewHolder> {
    private List<Repository> repositoryList;

    public RepositoryAdapter(List<Repository> repositoryList) {
        this.repositoryList = repositoryList;
    }


    @NonNull
    @Override
    public RepositoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardplaceholder, parent, false);
        return new RepositoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RepositoryViewHolder holder, int position) {
        Repository repository = repositoryList.get(position);
        holder.bind(repository);
    }

    @Override
    public int getItemCount() {
        return repositoryList.size();
    }

    public static class RepositoryViewHolder extends RecyclerView.ViewHolder {
        private TextView repoNameTextView;
        private TextView repoOwnerTextView;

        private ImageView repositryAvatar;
        private ImageButton btnShare;

        private CardView Repocard;



        public RepositoryViewHolder(@NonNull View itemView) {
            super(itemView);
            repoNameTextView = itemView.findViewById(R.id.DescriptionText);
            repoOwnerTextView = itemView.findViewById(R.id.repoName);

            repositryAvatar = itemView.findViewById(R.id.profileImage);

            btnShare = itemView.findViewById(R.id.shareButton);

            Repocard = itemView.findViewById(R.id.Repocard);

        }

        public void bind(final Repository repository) {
            Glide.with(itemView.getContext())
                    .load(repository.getOwner().getAvatar_url())
                    .into(repositryAvatar);
            repoNameTextView.setText(repository.getDescription());
            repoOwnerTextView.setText(repository.getName());

            Repocard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = repository.getHtml_url();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    itemView.getContext().startActivity(intent);
                }
            });

            btnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String gitLink = "Git Link: " + repository.getHtml_url();
                    String ownerName = "Owner Name: " + repository.getOwner().getLogin();
                    String shareText = gitLink + "\n" + ownerName;

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                    itemView.getContext().startActivity(Intent.createChooser(shareIntent, "Share via"));
                }
            });
        }

    }
}
