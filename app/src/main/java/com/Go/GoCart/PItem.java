package com.Go.GoCart;

import java.util.ArrayList;
import java.util.List;

public class PItem {

    private String name, cost, site, link, image;
    private Float rating;

    public PItem(String mName, String mCost, String mSite, String mLink, String mImage, Float mRating) {
        name = mName;
        cost = mCost;
        site = mSite;
        link = mLink;
        image = mImage;
        rating = mRating;
    }

    public Float getRating() {
        return rating;
    }

    public String getName() {
        return name;
    }

    public String getCost() {
        return cost;
    }

    public String getSite() {
        return site;
    }

    public String getLink() {
        return link;
    }

    public String getImage() {
        return image;
    }

    public static List<String> getAllNames(List<PItem> items) {
        List<String> newNames = new ArrayList<>();
        for (PItem i : items) {
            newNames.add(i.getName());
        }
        return newNames;
    }

    public static List<String> getAllCosts(List<PItem> items) {
        List<String> newNames = new ArrayList<>();
        for (PItem i : items) {
            newNames.add(i.getCost());
        }
        return newNames;
    }

    public static List<String> getAllSites(List<PItem> items) {
        List<String> newNames = new ArrayList<>();
        for (PItem i : items) {
            newNames.add(i.getSite());
        }
        return newNames;
    }

    public static List<String> getAllLinks(List<PItem> items) {
        List<String> newNames = new ArrayList<>();
        for (PItem i : items) {
            newNames.add(i.getLink());
        }
        return newNames;
    }

    public static List<String> getAllImages(List<PItem> items) {
        List<String> newNames = new ArrayList<>();
        for (PItem i : items) {
            newNames.add(i.getImage());
        }
        return newNames;
    }

    public static List<Float> getAllRatings(List<PItem> items) {
        List<Float> newNames = new ArrayList<>();
        for (PItem i : items) {
            newNames.add(i.getRating());
        }
        return newNames;
    }

}
