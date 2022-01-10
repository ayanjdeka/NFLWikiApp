package wiki.nfl.ayan.nflwikiapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class NFLWiki extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int RC_SIGN_IN = 100;
    DrawerLayout drawer;
    NavigationView navigationView;

    FragmentTransaction fragmentTransactionForPlayers;
    FragmentManager fragmentManagerForPlayers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nflwiki);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new IntroductionFragment()).commit();
        }




        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(NFLFireBaseHelper.getAccount() == null){
            navigationView.setEnabled(false);
        }


        /*
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);


        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        findViewById(R.id.sign_in_button).setOnClickListener(this);


        updateUI(account);
        */

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nflwiki, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_Players) {

            fragmentManagerForPlayers = getSupportFragmentManager();
            fragmentTransactionForPlayers = fragmentManagerForPlayers.beginTransaction();
            fragmentTransactionForPlayers.replace(R.id.fragment_container,new PlayerFragment());
            fragmentTransactionForPlayers.commit();

        } else if (id == R.id.nav_News) {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new NewsFragment()).commit();

        } else if (id == R.id.nav_Teams) {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new TeamsFragment()).commit();


        } else if (id == R.id.nav_Trivia) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new TriviaFragment()).commit();


        } else if (id == R.id.nav_preferences) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Preferences()).commit();


        } else if(id == R.id.id_PlayerCompare){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ComparePlayersFragment()).commit();

        } else if(id == R.id.nav_signin){
            signOut();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new IntroductionFragment()).commit();

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void signOut() {
        GoogleSignInClient mGoogleSignInClient  = NFLFireBaseHelper.getGoogleSignInClient();
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        NFLFireBaseHelper.setAccount(null);
                    }
                });
    }

}
