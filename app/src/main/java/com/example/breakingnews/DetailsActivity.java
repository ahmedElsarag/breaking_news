package com.example.breakingnews;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.breakingnews.databinding.ActivityDetailsBinding;

public class DetailsActivity extends AppCompatActivity {
    String url, author, date, title, source, content, img;
    ConstVariables constV = new ConstVariables();
    private ActivityDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_details);


        Intent intent = getIntent();
        author = intent.getStringExtra(constV.AUTHOR);
        date = intent.getStringExtra(constV.DATE);
        title = intent.getStringExtra(constV.TITLE);
        source = intent.getStringExtra(constV.SOURCE);
        content = intent.getStringExtra(constV.CONTENT);
        img = intent.getStringExtra(constV.IMG);
        url = intent.getStringExtra(constV.URL);

        String[] dateArr = Utils.DateFormat(date).split(" ");

        binding.author.setText(author);
        binding.titl.setText(title);
        binding.day.setText(dateArr[1]);
        binding.month.setText(dateArr[2]);
        binding.source.setText(source);
        binding.content.setText(content);
        binding.time.setText(Utils.DateToTimeFormat(date));


        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(Utils.getRandomDrawbleColor());
        requestOptions.error(Utils.getRandomDrawbleColor());
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.centerCrop();

        Glide.with(this)
                .load(img)
                .apply(requestOptions)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.img);
    }

    public void openBrowser(View view) {

        Intent implicit = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(implicit);
    }
}
