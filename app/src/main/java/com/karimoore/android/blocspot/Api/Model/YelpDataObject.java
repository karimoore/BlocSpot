package com.karimoore.android.blocspot.Api.Model;

import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kari on 4/18/16.
 */
public class YelpDataObject  {

    private String displayAddress;

    private List<YelpPoint> listOfYelpPoints = new ArrayList<YelpPoint>();

    public String getDisplayAddress() {
        return displayAddress;
    }
    public List<YelpPoint> populate(SearchResponse data) {

        ArrayList<Business> businesses = data.businesses();

        for (int i = 0; i < businesses.size(); i++) {

            String displayAddress = "";
            for (int addCount = 0; addCount < businesses.get(i).location().displayAddress().size(); addCount++) {
                displayAddress += businesses.get(i).location().displayAddress().get(addCount);
            }
            String str = businesses.get(i).snippetText();

            YelpPoint point = new YelpPoint(-1,
                    businesses.get(i).name(),
                    businesses.get(i).location().coordinate().latitude(),
                    businesses.get(i).location().coordinate().longitude(),
                    displayAddress,
                    false, -1);
            listOfYelpPoints.add(point);
        }
        return listOfYelpPoints;

    }


}
