package com.group24.interactivediary;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/*
 * Improved version of:
 * https://gist.github.com/nesquena/d09dc68ff07e845cc622
 *
 * More info on:
 * https://github.com/codepath/android_guides/wiki/Endless-Scrolling-with-AdapterViews-and-RecyclerView
 */

public class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {
    private static final int VISIBLE_THRESHOLD = 5;
    private static final int STARTING_PAGE_INDEX = 0;

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private final int visibleThreshold;
    // The current offset index of data you have loaded
    private int currentPage = 0;
    // The total number of items in the data-set after the last load
    private int previousTotalItemCount = 0;
    // True if we are still waiting for the last set of data to load.
    private boolean loading = true;

    private final RecyclerView.LayoutManager layoutManager;
    private final LastVisibleItemPositionFinder lastVisibleItemPositionFinder;

    private final LoadMoreListener loadMoreListener;

    public interface LoadMoreListener {
        // Defines the process for actually loading more data based on page
        void onLoadMore(int page, int totalItemsCount, RecyclerView view);
    }

    private interface LastVisibleItemPositionFinder {
        int find();
    }

    @SuppressWarnings("unused")
    public EndlessRecyclerViewScrollListener(LinearLayoutManager layoutManager, LoadMoreListener loadMoreListener) {
        this.layoutManager = layoutManager;
        this.visibleThreshold = VISIBLE_THRESHOLD;
        this.lastVisibleItemPositionFinder = layoutManager::findLastVisibleItemPosition;
        this.loadMoreListener = loadMoreListener;
    }

    @SuppressWarnings("unused")
    public EndlessRecyclerViewScrollListener(GridLayoutManager layoutManager, LoadMoreListener loadMoreListener) {
        this.layoutManager = layoutManager;
        this.visibleThreshold = VISIBLE_THRESHOLD * layoutManager.getSpanCount();
        this.lastVisibleItemPositionFinder = layoutManager::findLastVisibleItemPosition;
        this.loadMoreListener = loadMoreListener;
    }

    @SuppressWarnings("unused")
    public EndlessRecyclerViewScrollListener(StaggeredGridLayoutManager layoutManager, LoadMoreListener loadMoreListener) {
        this.layoutManager = layoutManager;
        this.visibleThreshold = VISIBLE_THRESHOLD * layoutManager.getSpanCount();
        this.lastVisibleItemPositionFinder = () -> {
            final int[] lastVisibleItemPositions = layoutManager.findLastVisibleItemPositions(null);
            // get maximum element within the list
            return getLastVisibleItem(lastVisibleItemPositions);
        };
        this.loadMoreListener = loadMoreListener;
    }

    // This happens many times a second during a scroll, so be wary of the code you place here.
    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.
    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {
        final int lastVisibleItemPosition = lastVisibleItemPositionFinder.find();
        final int totalItemCount = layoutManager.getItemCount();

        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = STARTING_PAGE_INDEX;
            this.previousTotalItemCount = totalItemCount;

            if (totalItemCount == 0) {
                this.loading = true;
            }
        }

        // If it’s still loading, we check to see if the data-set count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item count.
        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false;
            previousTotalItemCount = totalItemCount;
        }

        // If it isn’t currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        // threshold should reflect how many total columns there are too
        if (!loading && (lastVisibleItemPosition + visibleThreshold) > totalItemCount) {
            currentPage++;
            loadMoreListener.onLoadMore(currentPage, totalItemCount, view);
            loading = true;
        }
    }

    private int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int maxSize = 0;

        for (int i = 0; i < lastVisibleItemPositions.length; i++) {
            if (i == 0 || lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i];
            }
        }

        return maxSize;
    }

    /**
     * Call this method whenever performing new searches
     */
    @SuppressWarnings("unused")
    public void resetState() {
        this.currentPage = STARTING_PAGE_INDEX;
        this.previousTotalItemCount = 0;
        this.loading = true;
    }

}
