package com.karimoore.android.blocspot;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.karimoore.android.blocspot.Api.Model.Category;

/**
 * Created by kari on 4/7/16.
 */
public class CategoryAddDialog extends GenericCategoryDialogFragment {
    private static final String TAG = "CategoryAddDialog";
    private ImageButton titleImageButton;

    @Override
    public void setupDialog(Dialog dialog, int style) {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "OnCreate");
        // update the title
        mainDialog.setPositiveButton("Assign Category", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "Update this point of interest to new category");
            }
        });

        setTitle("Assign A Category");
        titleImageButton = (ImageButton) titleView.findViewById(R.id.category_titlebar_add_bn);


        titleImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // user wants to add a category
                Toast.makeText(getContext(), "Add a new category", Toast.LENGTH_SHORT).show();
                // popup add new category dialog
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View addCategoryView = inflater.inflate(R.layout.new_category_dialog, null);

                final EditText editText = (EditText) addCategoryView.findViewById(R.id.new_category_edit_text);

                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Add a New Category: ");
                builder.setView(addCategoryView);
                builder.setPositiveButton("Save Category", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String newCategory = String.valueOf(editText.getText());
                        // TO DO: get random color

                        // add this category to category list
                        Category newlyAddedCategory = new Category(0, newCategory, (long) BitmapDescriptorFactory.HUE_MAGENTA, Color.MAGENTA, false);
                        addCategoryToList(newlyAddedCategory);

                        // make a database call to add Category to the Category Table
                        BlocSpotApplication.getSharedDataSource().addCategory(newlyAddedCategory);

                        // add the category row to the View
                        createRow(-1, Color.MAGENTA, newCategory);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "User canceled addCategory");
                    }
                });
                builder.create();
                builder.show();


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
