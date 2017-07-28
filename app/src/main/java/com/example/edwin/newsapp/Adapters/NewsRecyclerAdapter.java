package com.example.edwin.newsapp.Adapters;

/**
 * Created by Edwin on 6/23/2017.
 */
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.example.edwin.newsapp.Models.NewsItem;
import com.example.edwin.newsapp.R;
import java.util.ArrayList;


public class NewsRecyclerAdapter extends RecyclerView.Adapter<NewsRecyclerAdapter.AdapterViewHolder> {


    private ArrayList<NewsItem> mNewsData;
    private final NewsAdapterOnClickHandler mClickHandler;
    Context context;

    public interface NewsAdapterOnClickHandler {
        void onClick(String url);
    }

    public NewsRecyclerAdapter(NewsAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class AdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        public final TextView title;
        public final TextView description;
        public final TextView time;
        public final ImageView thumb;

        public AdapterViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            title = (TextView) view.findViewById(R.id.tv_title);
            description = (TextView) view.findViewById(R.id.tv_description);
            time = (TextView) view.findViewById(R.id.tv_time);
            thumb = (ImageView) view.findViewById(R.id.thumb);
        }
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(mNewsData.get(adapterPosition).getUrl());
        }

    }


    @Override
    public AdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.news_recycler_layout;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdapterViewHolder adapterViewHolder, int position) {
        Glide.with(context).load(mNewsData.get(position).getUrlToImage())
                .into(adapterViewHolder.thumb);
        adapterViewHolder.title.setText(mNewsData.get(position).getTitle());
        adapterViewHolder.time.setText(mNewsData.get(position).getAuthor() + "    " + mNewsData.get(position).getPublishedAt());
        adapterViewHolder.description.setText(mNewsData.get(position).getDescription());

    }


    @Override
    public int getItemCount() {
        if (null == mNewsData) return 0;
        return mNewsData.size();
    }

    public void setNewsData(ArrayList<NewsItem> newsData) {
        mNewsData = newsData;
        notifyDataSetChanged();
    }
}