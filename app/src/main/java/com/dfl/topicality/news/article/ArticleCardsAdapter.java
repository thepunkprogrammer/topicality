package com.dfl.topicality.news.article;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.dfl.topicality.ImageLoader;
import com.dfl.topicality.R;
import com.dfl.topicality.database.DatabaseArticle;

/**
 * Created by loureiro on 29-01-2018.
 */

public class ArticleCardsAdapter extends ArrayAdapter<DatabaseArticle> {

    private ImageLoader imageLoader;

    ArticleCardsAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        imageLoader = new ImageLoader(getContext());
    }

    @NonNull
    @Override
    public View getView(int position, View contentView, @NonNull ViewGroup parent) {
        ArticleCardViewHolder articleCardViewHolder;

        if (contentView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            contentView = inflater.inflate(R.layout.card_content, parent, false);
            articleCardViewHolder = new ArticleCardViewHolder(contentView);
            contentView.setTag(articleCardViewHolder);
        } else {
            articleCardViewHolder = (ArticleCardViewHolder) contentView.getTag();
        }

        DatabaseArticle article = getItem(position);

        if (article != null) {
            articleCardViewHolder.setTitle(article.getTitle());
            articleCardViewHolder.setUrlToImage(imageLoader, article.getUrlToImage());
            articleCardViewHolder.setIsViewedIcon(article.getIsViewed() == 1);
        }

        return contentView;
    }
}
