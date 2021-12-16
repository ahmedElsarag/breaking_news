package com.example.breakingnews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.breakingnews.adapter.Adapter;
import com.example.breakingnews.api.ApiClient;
import com.example.breakingnews.api.ApiInterface;
import com.example.breakingnews.databinding.ActivityMainBinding;
import com.example.breakingnews.model.Article;
import com.example.breakingnews.model.News;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, Adapter.OnItemClickListener {

    private List<Article> articles;
    private Adapter adapter;
    ConstVariables constV = new ConstVariables();
    LinearLayoutManager linearLayoutManager;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        binding.swipeRefreshLayout.setOnRefreshListener(this);
        binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        intiateRecyclerView();

        loadGson("");
    }

    //using retrofit and loading gson data
    public void loadGson(final String keyword){
        binding.swipeRefreshLayout.setRefreshing(true);
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        String country = Utils.getCountry();
        String language = Utils.getLanguage();

        Call<News> call;

        if(keyword.length()>0)
            call = apiInterface.getNewsSearch(keyword,language,"publishedAt",constV.API_KEY);
        else
            call = apiInterface.getNews(country,constV.API_KEY);

        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                if(response.isSuccessful()&&response.body().getArticle() !=null){
                    articles = response.body().getArticle();
                    adapter = new Adapter(articles,MainActivity.this,MainActivity.this);
                    binding.recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    binding.swipeRefreshLayout.setRefreshing(false);

                }else {
                    Toast.makeText(MainActivity.this,"error getting data",Toast.LENGTH_LONG);
                    binding.swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    // search menu item implementation and action
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search latest news....");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query.length()>2)
                    loadGson(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                loadGson(newText);
                return false;
            }
        });
        searchItem.getIcon().setVisible(false,false);
        return true;
    }



    // swipe refresh action listener method
    @Override
    public void onRefresh() {
        onLoadingSwipeRefresh("");
    }

    public void onLoadingSwipeRefresh(final String keyword){

        binding.swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        loadGson(keyword);
                        binding.swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );
    }

    private void intiateRecyclerView(){
        linearLayoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerView.setNestedScrollingEnabled(false);
    }

    @Override
    public void onItemClick(View v , int position) {

        ImageView imageView = v.findViewById(R.id.img);
        TextView textView = v.findViewById(R.id.author);
        Pair<View, String> p1 = Pair.create((View) imageView , "img");
        Pair<View, String> p2 = Pair.create((View) textView , "author");

        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,p1,p2);

        Intent intent = new Intent(this,DetailsActivity.class);
        intent.putExtra(constV.AUTHOR,articles.get(position).getAuthor());
        intent.putExtra(constV.DATE,articles.get(position).getPublishedAt());
        intent.putExtra(constV.TITLE,articles.get(position).getTitle());
        intent.putExtra(constV.SOURCE,articles.get(position).getSource().getName());
        intent.putExtra(constV.IMG,articles.get(position).getUrlToImage());
        intent.putExtra(constV.CONTENT,articles.get(position).getContent());
        intent.putExtra(constV.URL,articles.get(position).getUrl());
        startActivity(intent,activityOptions.toBundle());
    }

}
