package june.second.lunchmatchmaker.Item;

import android.graphics.Bitmap;

public class Place {

    String name;
    String category;
    String priceCategory;
    String microReview;
    String totalReviewCount;
    double x;
    double y;
    String imageSrc;

    Bitmap bitmapPlacePhoto;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriceCategory() {
        return priceCategory;
    }

    public void setPriceCategory(String priceCategory) {
        this.priceCategory = priceCategory;
    }

    public String getMicroReview() {
        return microReview;
    }

    public void setMicroReview(String microReview) {
        this.microReview = microReview;
    }

    public String getTotalReviewCount() {
        return totalReviewCount;
    }

    public void setTotalReviewCount(String totalReviewCount) {
        this.totalReviewCount = totalReviewCount;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }


    public String getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }


    public Bitmap getBitmapPlacePhoto() {
        return bitmapPlacePhoto;
    }

    public void setBitmapPlacePhoto(Bitmap bitmapPlacePhoto) {
        this.bitmapPlacePhoto = bitmapPlacePhoto;
    }


}
