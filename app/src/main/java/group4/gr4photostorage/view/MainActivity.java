package group4.gr4photostorage.view;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import group4.gr4photostorage.Application;
import group4.gr4photostorage.R;
import group4.gr4photostorage.helper.AP;

public class MainActivity extends GoogleBaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //nullable if maybe there is no view
    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    LinearLayout headerContainer;
    CircleImageView imgAvatar;
    TextView tvName;
    Button updateProfile;

    private boolean mResolvingError;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mGoogleApiClient.connect();
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        headerContainer = (LinearLayout) headerView.findViewById(R.id.header_container);
        imgAvatar = (CircleImageView) headerView.findViewById(R.id.imageView);
        tvName = (TextView) headerView.findViewById(R.id.tv_name);

        updateProfile = (Button) headerView.findViewById(R.id.btn_update_profile);
        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Application.me = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                setupView();
            }
        });
    }

    private void setupView() {
        Person.Cover cover = Application.me.getCover();
        if (cover!=null &&cover.hasCoverPhoto()) {
            Glide.with(this)
                    .load(cover.getCoverPhoto().getUrl())
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>(400, 400) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                            Drawable d = new BitmapDrawable(getResources(), resource);
                            headerContainer.setBackgroundDrawable(d);
                        }
                    });
        }
        Glide.with(this).load(Application.me.getImage().getUrl()).asBitmap().into(imgAvatar);
        tvName.setText(Application.me.getDisplayName());
    }

    @OnClick(R.id.ic_menu)
    public void openDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.openDrawer(GravityCompat.END);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.about) {
            // Handle
        } else if (id == R.id.logout) {
            AP.clearPrefs(this);
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (mResolvingError)
            return;
        else if (connectionResult.hasResolution()) {
            try {
                mResolvingError = true;
                connectionResult.startResolutionForResult(this, 1);
            } catch (IntentSender.SendIntentException e) {
                mGoogleApiClient.connect();
            }
        } else {
            mResolvingError = true;
            Toast.makeText(this, getString(R.string.please_check_connection), Toast.LENGTH_LONG).show();
        }
    }

}
