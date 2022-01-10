package wiki.nfl.ayan.nflwikiapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class IntroductionFragment extends Fragment implements View.OnClickListener {

    View fragmentView;
    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient mGoogleSignInClient = null;
    SignInButton signInButton;
    TextView accountText;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.content_nflwiki, null);

        accountText = fragmentView.findViewById(R.id.id_showAccount);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this.getActivity(), gso);
        NFLFireBaseHelper.setGoogleSignInClient(mGoogleSignInClient); // getting from my helper to keep track on signout
        GoogleSignInAccount account = null;


        signInButton = fragmentView.findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        fragmentView.findViewById(R.id.sign_in_button).setOnClickListener(this);


        return fragmentView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            // ...
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
            String id = account.getId();
            String name = account.getDisplayName();
            String name2 = account.getGivenName();
            System.out.println();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("SOMETAG", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        NFLFireBaseHelper.setAccount(account);
        signInButton.setEnabled(false);
        accountText.setText("Welcome: "+NFLFireBaseHelper.getAccount().getDisplayName());
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this.getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        NFLFireBaseHelper.setAccount(null);
                    }
                });
    }

}
