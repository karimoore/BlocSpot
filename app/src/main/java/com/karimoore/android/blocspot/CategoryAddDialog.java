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
import android.widget.CheckBox;
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


    private AddCategoryListener listener;
    private int rowId; //of the POI to be changed.

    // 1. Defines the listener interface with a method passing back which categoryId's that define the filter.
    public interface AddCategoryListener {
        void newCategoryForPoint(int  categoryId, int pointId);
        void newCategoryAddedByUser();
    }
    // Assign the listener implementing events interface that will receive the events
    public void setChangeCategoryListener(AddCategoryListener listener, int rowId) {
        this.listener = listener;
        this.rowId = rowId;
    }

    public CategoryAddDialog() {
        this.listener = null;

    }

    @Override
    public void setupDialog(Dialog dialog, int style) {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "OnCreate");
        // update the title

        for (int i = 0; i < chkBoxIdList.size(); i++) {
            ((CheckBox) chkBoxIdList.get(i).findViewById(chkBoxIdList.get(i).getId())).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Checkbox for item: ..." + v.getTag() + " has state..." + ((CheckBox) v).isChecked());

                    // only one checkbox can be checked
                    // clear all other checkboxes
                    for (int i = 0; i < chkBoxIdList.size(); i++) {
                        if (i != (int)v.getTag()) {
                            // clear check
                            ((CheckBox)chkBoxIdList.get(i).findViewById(chkBoxIdList.get(i).getId())).setChecked(false);
                        }
                    }
                    // allCategories.get((Integer) v.getTag()).setIsFilter(((CheckBox) v).isChecked());

                }
            });
        }

        mainDialog.setPositiveButton("Assign Category", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "Update this point of interest to new category");
                // get the current checked category and send it to MianActivity to be updated in DB

                listener.newCategoryForPoint(getCheckedCategory(), rowId);
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

                        listener.newCategoryAddedByUser();

                        // add the category row to the View
                        createRow(-1, Color.MAGENTA, newCategory);
                        int addedCheckbox = chkBoxIdList.size() - 1;
                        ((CheckBox) chkBoxIdList.get(addedCheckbox).findViewById(chkBoxIdList.get(addedCheckbox).getId())).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d(TAG, "Checkbox for item: ..." + v.getTag() + " has state..." + ((CheckBox) v).isChecked());

                                // only one checkbox can be checked
                                // clear all other checkboxes
                                for (int i = 0; i < chkBoxIdList.size(); i++) {
                                    if (i != (int)v.getTag()) {
                                        // clear check
                                        ((CheckBox)chkBoxIdList.get(i).findViewById(chkBoxIdList.get(i).getId())).setChecked(false);
                                    }
                                }
                                // allCategories.get((Integer) v.getTag()).setIsFilter(((CheckBox) v).isChecked());

                            }
                        });

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

    public int getCheckedCategory(){
        for (int i = 0; i < chkBoxIdList.size(); i++){
            CheckBox chkbox = (CheckBox)chkBoxIdList.get(i).findViewById(  chkBoxIdList.get(i).getId() );
            if (chkbox.isChecked()){
                return (chkBoxIdList.get(i).getId()) +1;  // rowId to the category to be assigned

            }
        }
        return -1;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        return mainDialog.create();

    }
}
