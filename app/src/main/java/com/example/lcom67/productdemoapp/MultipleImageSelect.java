package com.example.lcom67.productdemoapp;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class MultipleImageSelect extends AppCompatActivity {

    ImageView image ;
    private int[] imageArray;
    private int currentIndex;
    private int startIndex;
    private int endIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_image_select);

        image = (ImageView) findViewById(R.id.imageselect);
        imageArray = new int[5];
        imageArray[0] = R.drawable.profile_icon;
        imageArray[1] = R.drawable.profile_image;
        imageArray[2] = R.drawable.profile_icon;
        imageArray[3] = R.drawable.profile_image;
        imageArray[4] = R.drawable.profile_icon;

        startIndex = 0;
        endIndex = 4;
        nextImage();

    }

    public void nextImage(){
        image.setImageResource(imageArray[currentIndex]);
        Animation rotateimage = AnimationUtils.loadAnimation(this, R.anim.custom_anim);
        image.startAnimation(rotateimage);
        currentIndex++;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(currentIndex>endIndex){
                    currentIndex--;
                    previousImage();
                }else{
                    nextImage();
                }

            }
        },1000); // here 1000(1 second) interval to change from current  to next image

    }

    public void previousImage(){
        image.setImageResource(imageArray[currentIndex]);
        Animation rotateimage = AnimationUtils.loadAnimation(this, R.anim.custom_anim);
        image.startAnimation(rotateimage);
        currentIndex--;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(currentIndex<startIndex){
                    currentIndex++;
                    nextImage();
                }else{
                    previousImage(); // here 1000(1 second) interval to change from current  to previous image
                }
            }
        },1000);

    }
}
