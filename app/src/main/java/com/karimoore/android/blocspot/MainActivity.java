package com.karimoore.android.blocspot;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.karimoore.android.blocspot.Api.DataSource;
import com.karimoore.android.blocspot.Api.Model.Category;
import com.karimoore.android.blocspot.Api.Model.Point;
import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by kari on 2/8/16.
 */
public class MainActivity extends AppCompatActivity implements CategoryFilterDialog.FilterResultsListener,
                                                                MyListFragment.Delegate,
                                                                CategoryAddDialog.AddCategoryListener{

    private static final String TAG = "MainActivity";


    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private MyMapsFragment mapFragment;
    private MyListFragment listFragment;

    protected static List<Point> currentPoints = new ArrayList<Point>();
    protected static List<Category> currentCategories = new ArrayList<Category>();


    public static List<Point> getCurrentPoints(){
        return currentPoints;
    }
    public static List<Category> getCurrentCategories(){
        return currentCategories;
    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

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
                            listFragment.update(getCurrentPoints());
                        } else {
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



        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        mapFragment = MyMapsFragment.newInstance(currentPoints, currentCategories);
        listFragment = MyListFragment.newInstance(currentPoints, currentCategories);
        adapter.addFragment(mapFragment, "MAP");
        adapter.addFragment(listFragment, "LIST");
        viewPager.setAdapter(adapter);


    }


    //-------------------MyListFragment.Delegate---------------
    @Override
    public void onItemLongClicked(int rowId) {

        Log.d(TAG, "I am in the MainActivity and can make DB updates for the longClick");
        showAssignCategoryDialog(rowId);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.getItemId() == R.id.action_search)
        {
            //open yelp search box
            Toast.makeText(MainActivity.this, "Search for points of interest in yelp", Toast.LENGTH_SHORT).show();
            YelpAPIFactory apiFactory = new YelpAPIFactory("pGkbXZie7A3zdkLXDIajMQ", "oU4AYlOVKCP2otdKmGQ--tu46PQ", "On9-Q_jz5nPITjADJEUSN9T65wYRqnBt", "GxDyjZUWOok4KIoHI-Rc7F4Tp18");
            YelpAPI yelpAPI = apiFactory.createAPI();
            Map<String, String> params = new HashMap<>();

// general params
            params.put("term", "food");
            params.put("limit", "3");

// locale params
            params.put("lang", "fr");

            Call<SearchResponse> call = yelpAPI.search("Chicago", params);

/*
            // bounding box
            BoundingBoxOptions bounds = BoundingBoxOptions.builder()
                    .swLatitude(37.7577)
                    .swLongitude(-122.4376)
                    .neLatitude(37.785381)
                    .neLongitude(-122.391681).build();
            Call<SearchResponse> call = yelpAPI.search(bounds, params);
            Response<SearchResponse> response = call.execute();

// coordinates
            CoordinateOptions coordinate = CoordinateOptions.builder()
                    .latitude(37.7577)
                    .longitude(-122.4376).build();
            Call<SearchResponse> call = yelpAPI.search(coordinate, params);
            Response<SearchResponse> response = call.execute();
*/

            Callback<SearchResponse> searchResponseCallback = new Callback<SearchResponse>() {
                @Override
                public void onResponse(Response<SearchResponse> response, Retrofit retrofit) {
                    SearchResponse searchResponse = response.body();

                    int totalNumberOfResult = searchResponse.total();  // 3

                    ArrayList<Business> businesses = searchResponse.businesses();
                    String businessName = businesses.get(0).name();  // "JapaCurry Truck"
                    Double rating = businesses.get(0).rating();  // 4.0

                }

                @Override
                public void onFailure(Throwable t) {

                }
            };
            call.enqueue(searchResponseCallback);
        } else if (item.getItemId() == R.id.action_filter){
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



