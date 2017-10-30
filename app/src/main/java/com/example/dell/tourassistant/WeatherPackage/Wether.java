
package com.example.dell.tourassistant.WeatherPackage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Wether {

    @SerializedName("data")
    @Expose
    private List<Datum> data = null;
    @SerializedName("count")
    @Expose
    private Integer count;

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

}
