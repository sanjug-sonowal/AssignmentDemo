package com.example.a1stzoomassignment.View;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.a1stzoomassignment.Model.Repository;
import com.example.a1stzoomassignment.R;
import com.example.a1stzoomassignment.ViewModel.RepositoryAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RepoScreenFragment extends Fragment {


    private static final String TAG = "GitHubRepositoryViewModel";
    private static final String BASE_URL = "https://api.github.com/repos/";

    private String USERNAME;
    private String REPONAME;

    private RecyclerView recyclerView;
    private List<Repository> repositoryList;
    private List<String> newRepoList;


    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private RecyclerView repositoryRecyclerView;
    private RepositoryAdapter repositoryAdapter;

    LinearLayout emptyRepoScreen;


    private Context context;

    public RepoScreenFragment() {
    }

    public static RepoScreenFragment newInstance(String username, String repoName) {
        RepoScreenFragment fragment = new RepoScreenFragment();
        Bundle args = new Bundle();
        args.putString(username, username);
        args.putString(repoName, repoName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        repositoryList = new ArrayList<>();




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_repo_screen, container, false);

        Button btnAddRepo = rootView.findViewById(R.id.btnAddRepo);
        repositoryRecyclerView = rootView.findViewById(R.id.repositoryRecyclerView);

        repositoryList = new ArrayList<>();

        repositoryAdapter = new RepositoryAdapter(repositoryList);

        repositoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        repositoryRecyclerView.setAdapter(repositoryAdapter);

        emptyRepoScreen = (LinearLayout) rootView.findViewById(R.id.emptyRepoScreen);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("mypref", MODE_PRIVATE);
        String storedRepositoryJson = sharedPreferences.getString("repository", "");

        if (!storedRepositoryJson.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Repository>>() {}.getType();
            List<Repository> storedRepositoryList = gson.fromJson(storedRepositoryJson, type);

            repositoryList.addAll(storedRepositoryList);
            repositoryAdapter.notifyDataSetChanged();

            // Hide the emptyRepoScreen if data is present
            emptyRepoScreen.setVisibility(View.GONE);
        } else {
            // Show the emptyRepoScreen if no data is present
            emptyRepoScreen.setVisibility(View.VISIBLE);
        }
        fetchRepository("username", "repoName");

        btnAddRepo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomDialog();
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }


    public void fetchRepository(String username, String repoName) {
        String url = BASE_URL + username + "/" + repoName;
        String apiToken = "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";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/vnd.github+json")
                .addHeader("Authorization", "Bearer "+apiToken)
                .addHeader("X-GitHub-Api-Version", "2022-11-28")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    Gson gson = new Gson();
                    Repository repository = gson.fromJson(json, Repository.class);

                    Log.d(TAG, "Repository: " + repository.getName());

                    SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("mypref", MODE_PRIVATE);
                    String storedRepositoryJson = sharedPreferences.getString("repository", "");

                    List<Repository> repositoryListFetched;
                    if (!storedRepositoryJson.isEmpty()) {
                        Type type = new TypeToken<List<Repository>>() {}.getType();
                        repositoryListFetched = gson.fromJson(storedRepositoryJson, type);
                        emptyRepoScreen.setVisibility(View.GONE);

                        boolean isDuplicate = false;
                        for (Repository storedRepository : repositoryListFetched) {
                            if (storedRepository.getName().equals(repository.getName())) {
                                isDuplicate = true;
                                break;
                            }
                        }

                        if (isDuplicate) {
                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(requireContext(), "Repository already exists.", Toast.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        }
                    } else {
                        repositoryListFetched = new ArrayList<>();
                        emptyRepoScreen.setVisibility(View.VISIBLE);
                    }

                    repositoryListFetched.add(repository);

                    String updatedRepositoryJson = gson.toJson(repositoryListFetched);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("repository", updatedRepositoryJson);
                    editor.apply();

                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String storedRepositoryJson = sharedPreferences.getString("repository", "");
                            if (!storedRepositoryJson.isEmpty()) {
                                emptyRepoScreen.setVisibility(View.GONE);
                            }

                            repositoryList.add(repository);

                            String updatedRepositoryJson = gson.toJson(repositoryList);

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("repository", updatedRepositoryJson);
                            editor.apply();

                            repositoryAdapter.notifyDataSetChanged();
                        }
                    });

                } else {
                    Log.e(TAG, "Error: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Request failed: " + e.getMessage());
            }
        });

    }



    public void showBottomDialog() {
        final Dialog dialog = new Dialog(requireActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);

        EditText edt_repoName = dialog.findViewById(R.id.repoName);
        EditText repoOwner = dialog.findViewById(R.id.repoOwner);

        LinearLayout liveLayout = dialog.findViewById(R.id.layoutLive);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        Button btnAdd = dialog.findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernameText = repoOwner.getText().toString();
                String repoNameText = edt_repoName.getText().toString();

                String username = usernameText.trim();
                String repoName = repoNameText.trim();

                if (!username.isEmpty() && !repoName.isEmpty()) {
                    fetchRepository(username, repoName);
                } else {
                    Toast.makeText(requireActivity(), "Please enter a valid username and repository name", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

        dialog.show();

        liveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(requireActivity(), "Go live is Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}
