package com.karimoore.android.blocspot.Api.Model;

/**
 * Created by kari on 2/23/16.
 */
public abstract class Model {
    private final long rowId;

    public Model(long rowId) {
        this.rowId = rowId;
    }

    public long getRowId() {

        return rowId;
    }
}
