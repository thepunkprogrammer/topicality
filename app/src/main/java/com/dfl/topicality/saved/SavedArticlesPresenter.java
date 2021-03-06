package com.dfl.topicality.saved;

import com.dfl.topicality.database.DatabaseArticle;
import com.dfl.topicality.database.DatabaseInteractor;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by loureiro on 31-01-2018.
 */

public class SavedArticlesPresenter implements SavedArticlesContract.Presenter {

    private final SavedArticlesContract.View view;
    private final DatabaseInteractor databaseInteractor;

    private final ArrayList<String> databaseArticleIdsList;
    private final CompositeDisposable compositeDisposable;

    SavedArticlesPresenter(SavedArticlesContract.View view, DatabaseInteractor databaseInteractor) {
        this.view = view;
        this.databaseInteractor = databaseInteractor;

        databaseArticleIdsList = new ArrayList<>();
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void subscribe(SavedArticlesContract.State state) {
        getAllArticles();
    }


    @Override
    public void unsubscribe() {
        compositeDisposable.clear();
    }

    @Override
    public SavedArticlesContract.State getState() {
        return null;
    }

    @Override
    public void getAllArticles() {
        compositeDisposable.add(databaseInteractor.getAllDatabaseArticles()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapIterable(databaseArticles -> databaseArticles)
                .filter(databaseArticle -> !databaseArticleIdsList.contains(databaseArticle.getUrl()))
                .subscribe(databaseArticle -> {
                            if (databaseArticleIdsList.isEmpty()) {
                                view.hideNoBookmarksLayout();
                            }
                            view.addArticle(databaseArticle);
                            databaseArticleIdsList.add(databaseArticle.getUrl());
                        },
                        throwable -> view.showSnackBar(throwable.getMessage())));
    }

    @Override
    public void removeFromSaved(String url, int viewHolderPosition) {
        compositeDisposable.add(databaseInteractor.deleteDatabaseArticleFromSavedWhereUrl(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                            view.removeArticle(viewHolderPosition);
                            databaseArticleIdsList.remove(viewHolderPosition);
                            if (databaseArticleIdsList.isEmpty()) {
                                view.showNoBookmarksLayout();
                            }
                        },
                        throwable -> view.showSnackBar(throwable.getMessage())));
    }

    @Override
    public void upsertFavoriteSourceClicks(String sourceDomain) {
        compositeDisposable.add(databaseInteractor.upsertFavoriteSourcesClicks(sourceDomain)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                        },
                        throwable -> view.showSnackBar(throwable.getMessage())));
    }

    @Override
    public void setArticleAsClicked(String url) {
        ArrayList<String> urls = new ArrayList<>();
        urls.add(url);
        compositeDisposable.add(databaseInteractor.getDatabaseArticleFromUrl(urls)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(databaseArticles -> {
                            for (DatabaseArticle databaseArticle : databaseArticles) {
                                databaseArticle.setIsViewed(1);
                                databaseArticle.setIsClicked(1);
                                addDatabaseArticleToDatabase(databaseArticle);
                            }
                        },
                        throwable -> view.showSnackBar(throwable.getMessage())));
    }

    private void addDatabaseArticleToDatabase(DatabaseArticle databaseArticle) {
        compositeDisposable.add(databaseInteractor.insertAllDatabaseArticles(databaseArticle)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                        },
                        throwable -> view.showSnackBar(throwable.getMessage())));
    }
}
