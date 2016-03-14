/*
 * Copyright (c) 2016.  Lukasz Fiszer
 */

package com.uznamska.lukas.mynotes.splashscreen;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.uznamska.lukas.mynotes.MainActivity;
import com.uznamska.lukas.mynotes.R;

//import com.google.android.gms.auth.api.Auth;
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.api.GoogleApiClient;
//
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
//import com.google.android.gms.auth.api.signin.GoogleSignInResult;

public class SplashActivity extends FragmentActivity implements
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener
{

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            ScreenSlideFragment fragment = new ScreenSlideFragment();
            fragment.setPosition(position);
            return fragment;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    private static final long SPLASH_TIME = 3000; //3 seconds
    private static final String TAG = "Notes:SinginSplash";
    Handler mHandler;
    Runnable mJumpRunnable;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private static final int NUM_PAGES = 5;


    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;


    private void jump(GoogleSignInAccount acc) {
        //it is safe to use this code even if you
        //do not intend to allow users to skip the splash
        if(isFinishing()) {
            return;
        }
        //startActivity(new Intent(this, MainActivity.class));
        updateUI(true, acc);
        finish();
    }

    private void updateUI(boolean signedIn, GoogleSignInAccount acc) {
        if (signedIn) {
           // findViewById(R.id.sign_in_button).setVisibility(View.GONE);
           // findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
            Intent main = new Intent(this, MainActivity.class);
            main.putExtra("displayname", acc.getDisplayName());
            main.putExtra("imageurl", acc.getPhotoUrl().toString());
            main.putExtra("userid", acc.getId());
            main.putExtra("email", acc.getEmail());
            startActivity(main);
            finish();
        } else {
           // findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            //findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }
    static int i = 0;
    Runnable ViewPagerVisibleScroll= new Runnable() {
        @Override
        public void run() {
            if(i <= mPagerAdapter.getCount()-1)
            {
                mPager.setCurrentItem(i, true);
                mHandler.postDelayed(this, 5000);
                i++;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
             .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
              .build();


        try{
            setContentView(R.layout.video_view);

            // Instantiate a ViewPager and a PagerAdapter.
            mPager = (ViewPager) findViewById(R.id.pager);
            mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
            mPager.setAdapter(mPagerAdapter);

            VideoView videoHolder = (VideoView) findViewById(R.id.videoViewRelative);
            // Button listeners
            findViewById(R.id.sign_in_button).setOnClickListener(this);
            // VideoView videoHolder = new VideoView(this);

            //setContentView(videoHolder);
//            Uri video = Uri.parse("android.resource://" + getPackageName() + "/"
//                    + R.raw.splash);

            //videoHolder.setMediaController(new MediaController(this));
            //videoHolder.requestFocus();
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            Log.d("SRAFG", size.x + " " + size.y);
//            videoHolder.setVideoURI(video);
//            videoHolder.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//
//                public void onCompletion(MediaPlayer mp) {
//                    jump();
//                }
//
//            });
            mHandler = new Handler();
            i = 0;
            mHandler.post(ViewPagerVisibleScroll);
            videoHolder.start();
        } catch(Exception ex) {
            jump(null);
        }
    }

    @Override
    public void onResume() {
        Log.d(TAG, "On resume");
        super.onResume();
        i =0;
       // mHandler.post(ViewPagerVisibleScroll);

    }

    @Override
    public void onStart() {
        Log.d(TAG, "On start");
        super.onStart();
        i = 0;
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        }
    }

    @Override
    public void onRestart() {
        Log.d(TAG,"On restart");
        super.onRestart();
    }


    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
       // jump();
        return true;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String idToken = acct.getIdToken();
            Log.d(TAG, acct.getDisplayName() + " has logged to google " + acct.getId() + " get token " + idToken);

            // TODO(user): send token to server and validate server-side

           // mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
          jump(acct);
        } else {
            // Signed out, show unauthenticated UI.
            //updateUI(false);
        }
    }
}
