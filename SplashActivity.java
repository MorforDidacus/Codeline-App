package com.chitechma;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends Activity {

    private TextView welcomeText, loadingDots, footerText;
    private ImageView splashLogo;
    private final Handler handler = new Handler();
    private final String welcomeMessage = "Dreamtech Solutions";
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Set status bar color to dark green
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#006400")); // Dark green
        }

        // Set background color for the splash screen
        getWindow().getDecorView().setBackgroundColor(Color.parseColor("#ffffff")); // Dark green background

        splashLogo = findViewById(R.id.splashLogo);
        welcomeText = findViewById(R.id.welcomeText);
        loadingDots = findViewById(R.id.loadingDots);
        footerText = findViewById(R.id.footerText);

        // Logo Animation (Fade and Scale)
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(1500);
        Animation scaleUp = new ScaleAnimation(0.5f, 1.5f, 0.5f, 1.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleUp.setDuration(1500);
        splashLogo.startAnimation(fadeIn);
        splashLogo.startAnimation(scaleUp);

        // Loading Dots Animation
        animateLoadingDots();

        // Welcome Text Fade In
        Animation fadeTextIn = new AlphaAnimation(0, 1);
        fadeTextIn.setDuration(2000);
        welcomeText.startAnimation(fadeTextIn);

        // Start the text color change animation
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animateTextColor();
            }
        }, 500);

        // Footer Text (Example text at the bottom)
        footerText.setText("Powered by DreamTech");
        footerText.setTextColor(Color.BLACK);
        footerText.setTextSize(16); // You can adjust the size as needed

        // Move to MainActivity after some time
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);  // Stay for 2 seconds
    }

    private void animateLoadingDots() {
        final String dots = ".";
        final int[] delay = {500, 1000, 1500, 2000};  // Delay for each dot

        // Animate each dot sequentially
        for (int i = 0; i < delay.length; i++) {
            final int index = i;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (index == 0) {
                        loadingDots.setText(".");
                    } else if (index == 1) {
                        loadingDots.setText("..");
                    } else if (index == 2) {
                        loadingDots.setText("...");
                    } else {
                        loadingDots.setText("....");
                    }
                }
            }, delay[i]);
        }

        // After last dot animation, reset the dots and restart the animation
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingDots.setText("");
                animateLoadingDots();  // Restart the animation
            }
        }, 2500);  // Reset after 2.5 seconds
    }

    private void animateTextColor() {
        if (currentIndex >= welcomeMessage.length()) {
            currentIndex = 0;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    animateTextColor();
                }
            }, 300);
            return;
        }

        StringBuilder coloredText = new StringBuilder();
        for (int i = 0; i < welcomeMessage.length(); i++) {
            if (i == currentIndex) {
                coloredText.append("<font color='#00b4d4'>").append(welcomeMessage.charAt(i)).append("</font>");
            } else {
                coloredText.append(welcomeMessage.charAt(i));
            }
        }

        welcomeText.setText(android.text.Html.fromHtml(coloredText.toString()));
        currentIndex++;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animateTextColor();
            }
        }, 150);
    }
}