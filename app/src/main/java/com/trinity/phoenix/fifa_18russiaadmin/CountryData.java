package com.trinity.phoenix.fifa_18russiaadmin;

public class CountryData {
    String url;
    String countryname;

    public CountryData() {
    }

    public CountryData(String url, String countryname) {
        this.url = url;
        this.countryname = countryname;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCountryname() {
        return countryname;
    }

    public void setCountryname(String countryname) {
        this.countryname = countryname;
    }
}
