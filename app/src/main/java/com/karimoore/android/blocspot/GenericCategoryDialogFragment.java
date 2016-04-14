package com.karimoore.android.blocspot;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.karimoore.android.blocspot.Api.DataSource;
import com.karimoore.android.blocspot.Api.Model.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kari on 4/7/16.
 */
public class GenericCategoryDialogFragment extends DialogFragment {

    private static final String TAG = "GenericCategoryDialog";

    private List<Category> allCategories = new ArrayList<Category>();
    AlertDialog.Builder mainDialog;
    LayoutInflater inflater;
    View bodyView;
    View titleView;
    protected List<CheckBox> chkBoxIdList;

    private TextView titleTextView;



    public GenericCategoryDialogFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BlocSpotApplication.getSharedDataSource().fetchAllCategories(new DataSource.Callback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> categories) {
                allCategories.addAll(categories);
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
        mainDialog = new AlertDialog.Builder(getContext());

        inflater = getActivity().getLayoutInflater();
        // inflate the body of dialog
        bodyView = inflater.inflate(R.layout.category_checkbox_dialog, null);

        // inflate the titlebar
        titleView=inflater.inflate(R.layout.category_titlebar, null);
        titleTextView = (TextView) titleView.findViewById(R.id.category_title_text_view);

        mainDialog.setCustomTitle(titleView);
        populateCategories();
        mainDialog.setView(bodyView);
        mainDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "Cancel dialog -do nothing");
            }
        });
    }

    public void setTitle(String title) {
        titleTextView.setText(title);
    }

    public void populateCategories() {
        chkBoxIdList = new ArrayList();

        for (int row = 0; row < allCategories.size(); row++) {
            // create a horizontal row
            createRow(row, (int) allCategories.get(row).getBackgroundColor(), allCategories.get(row).getName());

        }
    }

    public void createRow(int row, int color, String name){
        if (row == -1) {
            //adding by user to end of list
            row = allCategories.size() - 1;
        }
        LinearLayout ll = new LinearLayout(getContext());
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        // set background color to the category color
        ll.setBackgroundColor(color);

        TextView tv = new TextView(getContext());
        tv.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                100)); // make text bigger
        tv.setText(name);
        tv.setBackgroundColor(color);

        tv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 4f));
        tv.setTag(row); // save the position in list

        ll.addView(tv);

        CheckBox chkBox = new CheckBox(getContext());
        chkBox.setButtonDrawable(R.drawable.category_button_selector);
        //chkBox.setBackgroundColor(Color.RED);
        chkBox.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
        chkBox.setTag(row);
        chkBox.setId(row);

        chkBoxIdList.add(chkBox);  // set on click listener in child

/*
        chkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Checkbox for item: ..." + v.getTag() + " has state..." + ((CheckBox) v).isChecked());
                allCategories.get((Integer) v.getTag()).setIsFilter(((CheckBox) v).isChecked());
            }
        });
*/

        ll.addView(chkBox);
        ((ViewGroup) bodyView.findViewById(R.id.category_item_ll)).addView(ll);
        Log.d(TAG, "Added Category: " + name + " List is now size: " + allCategories.size());


    }

    public void addCategoryToList(Category category){
        allCategories.add(category);
        Log.d(TAG, "We now have " + allCategories.size() + " categories in our list");
    }

    public List<String> categoriesToFilter(){
        List<String> filterResult = new ArrayList<>();
        // get the state of the checkboxes
        // iterate through local filters - send back results to make db query
        for (int i = 0; i < allCategories.size(); i++) {
            if (allCategories.get(i).isFilter()) {
                filterResult.add(String.valueOf(allCategories.get(i).getRowId()));
            }
        }
        return filterResult;
    }
}
