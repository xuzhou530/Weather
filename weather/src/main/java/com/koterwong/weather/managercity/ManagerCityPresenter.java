package com.koterwong.weather.managercity;

import com.koterwong.weather.base.BaseApplication;
import com.koterwong.weather.beans.WeatherBean;
import com.koterwong.weather.commons.SavedCityDBManager;
import com.koterwong.weather.weather.WeatherJsonUtil;

import java.util.List;

/**
 * Author：Koterwong，Data：2016/4/29.
 * Description:
 */
public class ManagerCityPresenter {

    private ManagerCityView mView;

    public ManagerCityPresenter(ManagerCityView mView) {
        this.mView = mView;
    }

    public List<String> querySavedCityList(){
       return SavedCityDBManager.getInstance(BaseApplication.getApplication()).queryCities();
    }

    public void deleteCity(String cityName){
        SavedCityDBManager.getInstance(BaseApplication.getApplication()).deleteCity(cityName);
    }

    public WeatherBean.NowBean querySimpleWeather(String city){
        WeatherBean weatherBean = WeatherJsonUtil.getLocWeatherBean(city);
        return weatherBean.now;
    }

}