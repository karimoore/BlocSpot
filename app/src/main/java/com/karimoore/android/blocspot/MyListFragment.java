package com.karimoore.android.blocspot;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.karimoore.android.blocspot.Api.Model.Category;
import com.karimoore.android.blocspot.Api.Model.Point;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kari on 2/8/16.
 */
public class MyListFragment extends Fragment implements MyAdapter.Delegate {

    public static interface Delegate {
        public void onItemLongClicked(int rowId);
        //public void onItemContracted(RssItemListFragment rssItemListFragment, RssItem rssItem);
    }
    private WeakReference<Delegate> delegate;

    private RecyclerView mRecyclerView;
    //private RecyclerView.Adapter mAdapter;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    public static List<Point> listPoints = new ArrayList<>();
    public static List<Category> listCategories = new ArrayList<>();
    public static final MyListFragment newInstance(List<Point> points, List<Category> categories)
    {
        listPoints.addAll(points);
        listCategories.addAll(categories);
        MyListFragment f = new MyListFragment();
        Bundle localBundle = new Bundle(2);
        localBundle.putSerializable("POINTS", (Serializable) listPoints);
        localBundle.putSerializable("CATEGORY", (Serializable) listCategories);
        f.setArguments(localBundle);
        return f;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity = getActivity();
        delegate = new WeakReference<Delegate>((Delegate) activity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list,container,false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)

        mAdapter = new MyAdapter(listPoints, listCategories);
        mAdapter.setDelegate(this);
        mRecyclerView.setAdapter(mAdapter);

        return v;
    }



    public void update(List<Point> points) {
        //Clear existing items
        listPoints.clear();
        listPoints.addAll(points);
        ((MyAdapter)mAdapter).update(points);  // TO DO (retest)this crashes when SC from map to list??
    }

    public void updateCategories(List<Category> categories){
        listCategories.clear();
        listCategories.addAll(categories);
        mAdapter.updateCategories(categories);
    }

    //=---------------------MyAdapter.Delegate--------------
    @Override
    public void onLongClick(int rowId) {
        delegate.get().onItemLongClicked(rowId);


    }
    //--------------------------------------------------------
}
