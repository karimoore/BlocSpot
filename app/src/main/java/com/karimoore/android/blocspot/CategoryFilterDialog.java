package com.karimoore.android.blocspot;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kari on 3/14/16.
 */

//Constants-----------

public class CategoryFilterDialog extends GenericCategoryDialogFragment {
    private static final String TAG = "CategoryFilterDialog";

    ImageButton titleImageButton;

    //List<String> filters;

    private FilterResultsListener listener;

    // 1. Defines the listener interface with a method passing back which categoryId's that define the filter.
    public interface FilterResultsListener {
        void getFilterResults(List<String> whichCategories);
    }
    // Assign the listener implementing events interface that will receive the events
    public void setFilterResultsListener(FilterResultsListener listener) {
        this.listener = listener;
    }

    public CategoryFilterDialog() {
        this.listener = null;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "OnCreate");
        // remove the add button during a filter
        titleImageButton = (ImageButton) titleView.findViewById(R.id.category_titlebar_add_bn);
        titleImageButton.setVisibility(View.GONE);

        //filters = new ArrayList<>(chkBoxIdList.size());
        for (int i = 0; i < chkBoxIdList.size(); i++) {
            ((CheckBox) chkBoxIdList.get(i).findViewById(chkBoxIdList.get(i).getId())).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Checkbox for item: ..." + v.getTag() + " has state..." + ((CheckBox) v).isChecked());
                   // allCategories.get((Integer) v.getTag()).setIsFilter(((CheckBox) v).isChecked());

                }
            });
        }
        // update the title
        mainDialog.setPositiveButton("Filter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "Show the newly filtered items in map/list");
                List<String> filterCategories = new ArrayList<String>();
                for (int i = 0; i < chkBoxIdList.size(); i++) {
                    if (((CheckBox) chkBoxIdList.get(i).findViewById(chkBoxIdList.get(i).getId())).isChecked()) {
                        filterCategories.add(String.valueOf(i + 1));  // index of 0 has row id of 1

                    }
                }

                if (listener != null)
                    listener.getFilterResults(filterCategories);
                // bundle this up so its accessible from activity
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        return mainDialog.create();

    }
}

/*


    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        // compare the local categories to the database categories and update the database with
        // the newly added/deleted categories
        if (listChanged) {
            Toast.makeText(getContext(), "List has changed, so Update the database with new list of categories", Toast.LENGTH_SHORT).show();
            listChanged = false;
        }
    }

    public Dialog createTheDialog(List<Category> currentCategories) {
        mainDialog.setPositiveButton("Perform Filter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
        mainDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(), "no filter", Toast.LENGTH_SHORT).show();
            }
        });

        // Create the AlertDialog object and return it
        return mainDialog.create();

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        BlocSpotApplication.getSharedDataSource().fetchAllCategories(new DataSource.Callback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> categories) {
                allCategories.addAll(categories);
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
        if (getTag().equals("filterCategory")) {
            addFuntion = false; // performing filter function
            title = getResources().getString(R.string.cat_filter_title);
            positiveAction = getResources().getString(R.string.cat_filter_positive_action);
        } else {
            title = getResources().getString(R.string.cat_add_title);
            positiveAction = getResources().getString(R.string.cat_add_positive_action);
        }
        return createTheDialog(allCategories);
*/
/*
        builder.setMessage(R.string.dialog_fire_missiles)
                .setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
*//*

                }




        //LayoutInflater inflater = getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
*/
/*
        View view = inflater.inflate(R.layout.popup_dialog, null);
        description = (TextView) view.findViewById(R.id.description);
        image = (ImageView) view.findViewById(R.id.popup_image);
        description.setText(markersAndObjects.get(marker.getId()).getDescription());
        image.setImageResource(markersAndObjects.get(marker.getId()).getImageID());



        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.show();
*//*


//    }*//*


*/
/*

// change color dynamically
    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
            if(radioButton.getText().equals("yes")) {
                radioButton.setBackgroundColor(Color.GREEN);
            } else {
                radioButton.setBackgroundColor(Color.RED);
            }
        }
    });
*//*

}
*/
