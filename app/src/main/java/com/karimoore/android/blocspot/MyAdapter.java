package com.karimoore.android.blocspot;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.karimoore.android.blocspot.Api.Model.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kari on 2/10/16.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private static final String TAG = "MyAdapter";
    private List<Point> mDataset = new ArrayList<Point>();  // array of points of interest

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder
                                    implements View.OnClickListener{
        private static final String TAG = "MyViewHolder";
        // each data item is just a string in this case
        public TextView mTextView;
        public MyViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.info_text);
            v.setOnClickListener(this);
        }
        // call some bindData(data); called from adapter in the onBindViewHolder
/*
        public void bindCrime(Crime crime) {
            mCrime = crime;
            mSolvedCheckBox.setChecked(crime.isSolved());
        }
*/
        @Override
        public void onClick(View v) {
            Log.d(TAG,  "Clicked on Item in List @ position: " + getAdapterPosition());

/*
            if (mCrime != null) {
                Intent i = CrimeActivity.getIntent(v.getContext(), mCrime);
                startActivity(i);
            }
*/
        }


    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(List<Point> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_card_view, parent, false);
        // set the view's size, margins, paddings and layout parameters

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(mDataset.get(position).getName() + ", " +
                                mDataset.get(position).getLatitude() + ", " +
                                mDataset.get(position).getLongitude()); // connect to datasource here
/*
        Crime crime = mCrimes.get(pos);
        holder.bindCrime(crime);
*/
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
