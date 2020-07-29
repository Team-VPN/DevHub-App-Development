package com.teamvpn.devhub.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


public class PicassoMarker implements Target {
    Marker mMarker;

    PicassoMarker(Marker marker) {
        mMarker = marker;
    }

    @Override
    public int hashCode() {
        return mMarker.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof PicassoMarker) {
            Marker marker = ((PicassoMarker) o).mMarker;
            return mMarker.equals(marker);
        } else {
            return false;
        }
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        int height = 100;
        int width = 100;
        Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, width, height, false);
        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
        mMarker.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
    }

    @Override
    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

    }



    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
}