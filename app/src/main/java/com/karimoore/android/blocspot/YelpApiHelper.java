package com.karimoore.android.blocspot;

import android.location.Location;
import android.util.Log;

import com.karimoore.android.blocspot.Api.Model.YelpDataObject;
import com.karimoore.android.blocspot.Api.Model.YelpPoint;
import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by kari on 4/15/16.
 */
public class YelpApiHelper {
    private static final String TAG = "YelpApiHelper";

    private String query;
    private YelpAPIFactory apiFactory;
    private YelpAPI yelpAPI;
    private Location location;


    public void setYelpHelperListener(YelpHelperListener yelpHelperListener) {
        this.yelpHelperListener = yelpHelperListener;
    }

    YelpHelperListener yelpHelperListener;
    public interface YelpHelperListener {
        void updateYelpPoints(List<YelpPoint> points);
        void errorOnYelp();
    }

    public YelpApiHelper(String query, Location location) {
        this.yelpHelperListener = null;
        this.query = query;
        this.location = location;
    }

    // This YELP search call is already asynch by nature
    /*
    private class YelpTask extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... params) {
            setupYelpAPI();
            search();
            return null;
        }
    }
*/
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void performSearchTask() {

        //new YelpTask().execute();
        setupYelpAPI();
        search();
    }

    public void setupYelpAPI() {
        //open yelp search box
        Log.d(TAG, "Search for points of interest in yelp");
        apiFactory = new YelpAPIFactory("pGkbXZie7A3zdkLXDIajMQ", "oU4AYlOVKCP2otdKmGQ--tu46PQ", "On9-Q_jz5nPITjADJEUSN9T65wYRqnBt", "GxDyjZUWOok4KIoHI-Rc7F4Tp18");
        yelpAPI = apiFactory.createAPI();

    }

    public void search() {
        Map<String, String> params = new HashMap<>();

        // general params
        params.put("term", query);
        params.put("limit", "3");


        Log.d(TAG, "Searching "+ query + " in location " + location.getLatitude() + "," + location.getLongitude() );
        // coordinates
        CoordinateOptions coordinate = CoordinateOptions.builder()
                .latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        Call<SearchResponse> call = yelpAPI.search(coordinate, params);
        //Response<SearchResponse> response = call.execute();

        //Call<SearchResponse> call = yelpAPI.search("Chicago", params);

        Callback<SearchResponse> searchResponseCallback = new Callback<SearchResponse>() {
                @Override
                public void onResponse(Response<SearchResponse> response, Retrofit retrofit) {
                    SearchResponse searchResponse = response.body();
                    Log.d(TAG, "Yelp has returned " + searchResponse.total()+ " results");

                    YelpDataObject yelpDataObject = new YelpDataObject();
                    List<YelpPoint> yelpPoints = new ArrayList<YelpPoint>();
                    yelpPoints.addAll(yelpDataObject.populate(response.body()));
                    yelpHelperListener.updateYelpPoints(yelpPoints);

                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d(TAG, "Error on Yelp Search: " + t.toString());
                    yelpHelperListener.errorOnYelp();

                }
            };
            call.enqueue(searchResponseCallback);

    }
}
