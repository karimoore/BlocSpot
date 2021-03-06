package com.karimoore.android.blocspot;

import android.graphics.Color;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.karimoore.android.blocspot.Api.Model.Category;
import com.karimoore.android.blocspot.Api.Model.Point;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kari on 2/10/16.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private static final String TAG = "MyAdapter";


    public static interface Delegate {
       //public void onItemClicked(ItemAdapter itemAdapter, RssItem rssItem);
        public void onLongClick(int rowId);
        public void onNoteChanged(int rowId, String note);
        public void onVisitedChanged(int rowId, boolean visited);
        public void hideKeyboard(View v);
    }


    public Delegate getDelegate() {
        if (delegate == null){
            return null;
        }
        return delegate.get();
    }

    public void setDelegate(Delegate delegate) {
        this.delegate = new WeakReference<Delegate>(delegate);
    }

    private WeakReference<Delegate> delegate;
    private List<Point> mDataset = new ArrayList<Point>();  // array of points of interest
    private List<Category> adapterCategories = new ArrayList<>();  // list of categories


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder
                                    implements View.OnLongClickListener,
                                                View.OnClickListener{
        private static final String TAG = "MyViewHolder";
        // each data item is just a string in this case
        public TextView mNameTextView;
        public EditText mNoteEditText;
        public Button mDoneButton;
        public ImageButton visitedButton; // also has a style
        public ImageButton menuButton;
        int rowId;

        public MyViewHolder(View v) {
            super(v);
            mNameTextView = (TextView) v.findViewById(R.id.info_name);
            mNoteEditText = (EditText) v.findViewById(R.id.info_note);
            mDoneButton = (Button) v.findViewById(R.id.edit_txt_done_button);
            visitedButton = (ImageButton) v.findViewById(R.id.visited_image_button);
           menuButton = (ImageButton) v.findViewById(R.id.item_menu_button);
            v.setOnClickListener(this);
            v.setOnLongClickListener(this);
            v.setLongClickable(true);
            mNoteEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                // if focused is on editing note show the "done button
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus == false)
                        mDoneButton.setVisibility(View.GONE);
                    else
                        mDoneButton.setVisibility(View.VISIBLE);
                    Log.d(TAG, "focus changed for " + getAdapterPosition());
                }
            });

            mDoneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getDelegate() != null) {
                        getDelegate().onNoteChanged(rowId, mNoteEditText.getText().toString());
                        getDelegate().hideKeyboard(v);
                    }
                }
            });
            visitedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Clicked on visited");

                }
            });


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


        @Override
        public boolean onLongClick(View v) {
            Log.d(TAG,  "LONG Clicked on Item in List @ position: " + getAdapterPosition() + " at rowId of: "+ rowId);
            Log.d(TAG, "Allow user to change the category for this item or add a new one.");
/*
            CategoryFilterDialog newFragment = new CategoryFilterDialog();
            newFragment.show(getSupportFragmentManager(), "category");
*/
            //newFragment.setFilterResultsListener(this);
            if (getDelegate() != null)
                getDelegate().onLongClick(rowId);
            return true;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(List<Point> myDataset, List<Category> categories) {
        mDataset.addAll(myDataset);
        adapterCategories.addAll(categories);
    }

    public void update(List<Point> points){
        mDataset.clear();
        mDataset.addAll(points);
        notifyDataSetChanged();
    }
    public void updateCategories(List<Category> categories){
        adapterCategories.clear();
        adapterCategories.addAll(categories);
        //notifyDataSetChanged();
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
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final int pos = position; //remember this position for OnClick later

        // - get element from your dataset at this position
        // - replace the contents of the view with that element


        // keep row id of the DB
        holder.rowId = (int) mDataset.get(position).getRowId();

        //----------------------------
        // name field
        holder.mNameTextView.setText(mDataset.get(position).getName()); // connect to datasource here

        // get note field
        holder.mNoteEditText.setText(mDataset.get(position).getNote());



        //// get the category information
        long catId = mDataset.get(position).getCatId();
        final int backgroundColor;
        if (catId == -1) {
            // no category assigned yet, may have a YELP item
            backgroundColor = Color.BLUE;

        }
        else {
            backgroundColor = adapterCategories.get((int) (catId-1)).getBackgroundColor();
        }
        // given catId - get color

        //----------------------------
        // Category Button that works like a checkbox
        final int resource;
        if (mDataset.get(position).isVisited()) {

            resource = R.drawable.checkmark;
        } else {
//            resource = android.R.color.transparent;
           resource = backgroundColor;
        }

        holder.visitedButton.setBackgroundColor(backgroundColor);
        holder.visitedButton.setImageResource(resource); // error when passed a color resource id, instead of drawable
        holder.visitedButton.setTag(mDataset.get(position));
        holder.visitedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton ib = (ImageButton)v;
                Point point = (Point) v.getTag();
                mDataset.get(pos).setVisited(!mDataset.get(pos).isVisited());  // set dataSet to what user has  ??NEED to UPDATE DB
                if (mDataset.get(pos).isVisited()) {
                    holder.visitedButton.setBackgroundColor(backgroundColor);
                    holder.visitedButton.setImageResource(R.drawable.checkmark);
                } else {
                    holder.visitedButton.setImageResource(backgroundColor); //needs a resource id of drawable not color.
                }
                holder.visitedButton.setTag(mDataset.get(pos));

                getDelegate().onVisitedChanged(pos+1, mDataset.get(pos).isVisited());
                Log.d(TAG, "Clicked Button for :" + " " + point.getName() + "is visited: " + mDataset.get(pos).isVisited());
            }
        });

//--------------------------------------
// Menu
        holder.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu menu = new PopupMenu(v.getContext(), v);
                menu.setOnMenuItemClickListener (new PopupMenu.OnMenuItemClickListener ()
                {
                    @Override
                    public boolean onMenuItemClick (MenuItem item)
                    {
                        int id = item.getItemId();
                        switch (id)
                        {
                            case R.id.item_1:
                                Log.d(TAG, "DO Item 1 Action! for item number " + position ); break;
                            case R.id.item_2:
                                Log.d(TAG, "DO Item 2 Action! for item number " + position ); break;
                        }
                        return true;
                    }
                });
                menu.inflate (R.menu.item_menu);
                menu.show();

            }
        });
/*
        Crime crime = mCrimes.get(pos);
        holder.bindCrime(crime);
         final int pos = position;

*/
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
