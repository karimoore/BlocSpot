package com.karimoore.android.blocspot;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.karimoore.android.blocspot.Api.DataSource;
import com.karimoore.android.blocspot.Api.Model.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kari on 2/8/16.
 */
public class MyListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<Point> currentPoints = new ArrayList<Point>();

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

        // create a fake array of strings
        BlocSpotApplication.getSharedDataSource().fetchAllPoints(new DataSource.Callback<List<Point>>() {
            @Override
            public void onSuccess(List<Point> points) {
                currentPoints = points;
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
        //String[] fakeData = new String[]{"Willis Tower", "Art Institute", "Wrigley", "Soldiers Field", "501 Boardman", "The bean"};
        mAdapter = new MyAdapter(currentPoints);
        mRecyclerView.setAdapter(mAdapter);

        return v;
    }


}
