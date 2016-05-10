package com.karimoore.android.blocspot;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.karimoore.android.blocspot.Api.DataSource;
import com.karimoore.android.blocspot.Api.Model.Category;
import com.karimoore.android.blocspot.Api.Model.Point;
import com.karimoore.android.blocspot.Api.Model.YelpPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kari on 2/8/16.
 */
public class MainActivity extends AppCompatActivity implements CategoryFilterDialog.FilterResultsListener,
                                                                MyListFragment.Delegate,
                                                                MyMapsFragment.Delegate,
                                                                CategoryAddDialog.AddCategoryListener,
                                                                YelpApiHelper.YelpHelperListener{


    private static final String TAG = "MainActivity";


    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabLayout.Tab listTab;
    ProgressDialog progressDialog;

    private MyMapsFragment mapFragment;
    private MyListFragment listFragment;

    MenuItem mSearchMenuItem;

    protected static List<Point> currentPoints = new ArrayList<Point>();
    protected static List<Category> currentCategories = new ArrayList<Category>();

    private List<YelpPoint> listOfYelpPointsFromSearch = new ArrayList<YelpPoint>();

    public MainActivity() {

    }


    public static List<Point> getCurrentPoints(){
        return currentPoints;
    }
    public static List<Category> getCurrentCategories(){
        return currentCategories;
    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); // allows for progress bar
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        Log.d(TAG, "MainActivity OnCreate()");
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
        }

        // get data on startup-------------------------
        BlocSpotApplication.getSharedDataSource().fetchAllPointsAndCategories(new DataSource.Callback2<List<Point>, List<Category>>() {

            @Override
            public void onSuccess(List<Point> points, List<Category> categories) {
                // add database items to local copy
                currentPoints.addAll(points);
                currentCategories.addAll(categories);
                viewPager = (ViewPager) findViewById(R.id.viewpager);

                setupViewPager(viewPager);
                tabLayout = (TabLayout) findViewById(R.id.tabs);
                tabLayout.setupWithViewPager(viewPager);

                viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        // when switching between map and list -
                        // make sure changes are shown to user.
                        viewPager.setCurrentItem(tab.getPosition());

                        if (tab.getPosition() == 1) { //LIST
                            // disable search icon
                            mSearchMenuItem.setVisible(false);
                            listFragment.update(getCurrentPoints());


                        } else {
                            // enable search icon
                            mSearchMenuItem.setVisible(true);
                            mapFragment.update(getCurrentPoints());

                        }
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });


            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(MainActivity.this, "Did not get the correct points", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        String query;
        super.onNewIntent(intent);
        Log.d(TAG, "MainActivity received a new intent, (expecting search here)");
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {


            query = intent.getStringExtra(SearchManager.QUERY);
            progressDialog = ProgressDialog.show(this, "Search",
                    "Searching YELP...", true);
            Location location = mapFragment.getLastLocation();
            YelpApiHelper yelpApiHelper = new YelpApiHelper(query, location);
            yelpApiHelper.setYelpHelperListener(this);
            yelpApiHelper.performSearchTask();

        }

    }
// ---- YElpApiHelper Interface
    @Override
    public void errorOnYelp() {
        progressDialog.dismiss();
        Toast.makeText(MainActivity.this, "Unable to show search results", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateYelpPoints(List<YelpPoint> points) {
        mapFragment.updateYelpPoints(points);
        progressDialog.dismiss();


    }

    //--------------------------------------


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivity OnResume called.");
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        mapFragment = MyMapsFragment.newInstance(currentPoints, currentCategories);
        listFragment = MyListFragment.newInstance(currentPoints, currentCategories);
        adapter.addFragment(mapFragment, "MAP");
        adapter.addFragment(listFragment, "LIST");
        viewPager.setAdapter(adapter);
        viewPager.getChildAt(0);
        int i = 0;


    }


    //-------------------MyListFragment.Delegate---------------
    @Override
    public void onItemLongClicked(int rowId) {

        Log.d(TAG, "I am in the MainActivity and can make DB updates for the longClick");
        showAssignCategoryDialog(rowId);
    }

    @Override
    public void onItemNoteChanged(int rowId, String note) {
        BlocSpotApplication.getSharedDataSource().updateNoteForPoint(rowId, note);
        BlocSpotApplication.getSharedDataSource().fetchAllPoints(new DataSource.Callback<List<Point>>() {
            @Override
            public void onSuccess(List<Point> points) {
                currentPoints.clear();
                currentPoints.addAll(points);
                mapFragment.update(points);
                listFragment.update(points);
            }

            @Override
            public void onError(String errorMessage) {

            }
        });




    }

    @Override
    public void onItemVisitedChanged(int rowId, boolean visited) {
        BlocSpotApplication.getSharedDataSource().updateVisitedForPoint(rowId, visited);
        BlocSpotApplication.getSharedDataSource().fetchAllPoints(new DataSource.Callback<List<Point>>() {
            @Override
            public void onSuccess(List<Point> points) {
                currentPoints.clear();
                currentPoints.addAll(points);
                mapFragment.update(points);
                listFragment.update(points);
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }

    //-----------------MyMapsFragment.Delegate-------------------------
    @Override
    public void addYelpPoint(Point point) {
        BlocSpotApplication.getSharedDataSource().addPoint(point);
        BlocSpotApplication.getSharedDataSource().fetchAllPoints(new DataSource.Callback<List<Point>>() {
            @Override
            public void onSuccess(List<Point> points) {
                // may still be within the search view, so don't clear the map, yet
                currentPoints.clear();
                currentPoints.addAll(points);
                mapFragment.updateCategories(getCurrentCategories());
                mapFragment.update(points);
                listFragment.updateCategories(getCurrentCategories());
                listFragment.update(points);
            }

            @Override
            public void onError(String errorMessage) {

            }
        });


    }

    //---------------------------------------------------------


    //----------------------------------------------------------------
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get((position));
        }


        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchMenuItem = menu.findItem(R.id.action_search);
        MenuItemCompat.setOnActionExpandListener(mSearchMenuItem,new MenuItemCompat.OnActionExpandListener(){
                                                     @Override
                                                     public boolean onMenuItemActionExpand(MenuItem item) {
                                                         //disable the list view...no functionality yet
                                                         // yelp results only managed in map only
                                                         Log.d(TAG, "disable the list");
                                                         LinearLayout tabStrip = ((LinearLayout)tabLayout.getChildAt(0));
                                                         tabStrip.getChildAt(1).setClickable(false); // disable the LIST

                                                         return true;
                                                     }

                                                     @Override
                                                     public boolean onMenuItemActionCollapse(MenuItem item) {
                                                         LinearLayout tabStrip = ((LinearLayout)tabLayout.getChildAt(0));
                                                         tabStrip.getChildAt(1).setClickable(true); // enable the LIST

                                                         // call update to clear map of search results
                                                         // also need to turn the onclickmarker to false
                                                         mapFragment.update(getCurrentPoints());
                                                         mapFragment.clearSearchListener();
                                                         return true;
                                                     }
                                                 });


        SearchView searchView = (SearchView) MenuItemCompat.getActionView(mSearchMenuItem);

        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.getItemId() == R.id.action_filter){
            // Show Category Dialog
            Toast.makeText(MainActivity.this, "User allowed to perform filter", Toast.LENGTH_SHORT).show();
            showFilterCategoryDialog();

        }

             return super.onOptionsItemSelected(item);
    }


    public void showAssignCategoryDialog(int rowId) {
        CategoryAddDialog newFragment = new CategoryAddDialog();
        newFragment.show(getSupportFragmentManager(), "assignCategory");
        newFragment.setChangeCategoryListener(this, rowId);
    }

    @Override
    public void newCategoryForPoint(int categoryId, int pointId) {
        // update the database for pointID  with a new categoryId
        BlocSpotApplication.getSharedDataSource().updateCategoryForPoint(categoryId, pointId);
        BlocSpotApplication.getSharedDataSource().fetchAllPoints(new DataSource.Callback<List<Point>>() {
            @Override
            public void onSuccess(List<Point> points) {
                currentPoints.clear();
                currentPoints.addAll(points);
                mapFragment.updateCategories(getCurrentCategories());
                mapFragment.update(points);
                listFragment.updateCategories(getCurrentCategories());
                listFragment.update(points);
            }

            @Override
            public void onError(String errorMessage) {

            }
        });

    }

    @Override
    public void newCategoryAddedByUser() {
        BlocSpotApplication.getSharedDataSource().fetchAllCategories(new DataSource.Callback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> categories) {
                currentCategories.clear();
                currentCategories.addAll(categories);
                listFragment.updateCategories(categories);
            }

            @Override
            public void onError(String errorMessage) {

            }
        });

    }

    public void showFilterCategoryDialog() {
        CategoryFilterDialog newFragment = new CategoryFilterDialog();
        newFragment.show(getSupportFragmentManager(), "filterCategory");
        newFragment.setFilterResultsListener(this);
    }
    @Override
    public void getFilterResults(List<String> categoryIds) {
        Toast.makeText(MainActivity.this, "Filter these ids: " + categoryIds.get(0), Toast.LENGTH_SHORT).show();
        BlocSpotApplication.getSharedDataSource().fetchFilteredPoints(categoryIds, new DataSource.Callback<List<Point>>() {
            @Override
            public void onSuccess(List<Point> points) {
                boolean mapVisible = mapFragment.getUserVisibleHint();
                currentPoints.clear();
                currentPoints.addAll(points);
                mapFragment.update(points);
                listFragment.update(points);

            }

            @Override
            public void onError(String errorMessage) {
                Log.d(TAG, "error in filtering: " + errorMessage);
            }
        });
    }



}



