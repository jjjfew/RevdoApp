package net.testSocket.chat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wilddog.client.AuthData;
import com.wilddog.client.Wilddog;
import com.wilddog.client.WilddogError;

import net.testSocket.R;
import net.testSocket.chat.auth.PasswordActivity;
import net.testSocket.chat.auth.QQOAuthActivity;
import net.testSocket.chat.auth.WeiboOAuthActivity;

public class LoginActivity extends AppCompatActivity {
    private  String dispName;

    private static final String TAG = LoginActivity.class.getSimpleName();

    /* *************************************
     *              GENERAL                *
     ***************************************/
    /* TextView that is used to display information about the logged in user */
    private TextView mLoggedInStatusTextView;

    /* A dialog that is presented until the Wilddog authentication finished. */
    private ProgressDialog mAuthProgressDialog;

    /* A reference to the Wilddog */
    private Wilddog mWilddogRef;

    /* Data from the authenticated user */
    private AuthData mAuthData;

    /* Listener for Wilddog session changes */
    private Wilddog.AuthStateListener mAuthStateListener;

    /* *************************************
     *              PASSWORD               *
     ***************************************/
    private Button mPasswordLoginButton;

    /* *************************************
     *            ANONYMOUSLY              *
     ***************************************/
    private Button mAnonymousLoginButton;

    /* *************************************
     *              WEIBO                  *
     ***************************************/
    private Button mWeiboButton;
    /* *************************************
     *              WEIBO                  *
     ***************************************/
    private Button mQQButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Load the view and display it */
        setContentView(R.layout.activity_login);

        mWilddogRef = new Wilddog(getResources().getString(R.string.wilddog_url));

 /* *************************************
  *               PASSWORD              *
  ***************************************/
        mPasswordLoginButton = (Button) findViewById(R.id.login_with_password);
        mPasswordLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginWithPassword();
            }
        });

 /* *************************************
  *              ANONYMOUSLY            *
  ***************************************/
 /* Load and setup the anonymous login button */
        mAnonymousLoginButton = (Button) findViewById(R.id.login_anonymously);
        mAnonymousLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginAnonymously();
            }
        });

 /* *************************************
  *              weibo                  *
  ***************************************/
 /* Load and setup the anonymous login button */
        mWeiboButton = (Button) findViewById(R.id.login_button_default);
        mWeiboButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, WeiboOAuthActivity.class);
                startActivity(intent);
            }
        });
 /* *************************************
  *              qq                     *
  ***************************************/
 /* Load and setup the anonymous login button */
        mQQButton = (Button) findViewById(R.id.login_with_qq);
        mQQButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, QQOAuthActivity.class);
                startActivity(intent);
            }
        });


 /* *************************************
  *               GENERAL 点认证后才会显示出来             *
  ***************************************/
        mLoggedInStatusTextView = (TextView) findViewById(R.id.login_status);

 /* Setup the progress dialog that is displayed later when authenticating with Wilddog */
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Loading");
        mAuthProgressDialog.setMessage("Authenticating with Wilddog...");
        mAuthProgressDialog.setCancelable(false);
        mAuthProgressDialog.show();

        mAuthStateListener = new Wilddog.AuthStateListener() {  //检测是否已认证
            @Override
            public void onAuthStateChanged(AuthData authData) {
                mAuthProgressDialog.hide();
                setAuthenticatedUser(authData);
            }
        };
        //Check if the user is authenticated with Wilddog already. If this is the case we can set the authenticated
//user and hide hide any login buttons
        mWilddogRef.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* If a user is currently authenticated, display a logout menu */
        if (this.mAuthData != null) {
            getMenuInflater().inflate(R.menu.chat, menu);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Unauthenticate from Wilddog and from providers where necessary.
     */
    private void logout() {
        if (this.mAuthData != null) {
            /* logout of Wilddog */
            mWilddogRef.unauth();
            /* Update authenticated user and show login buttons */
            setAuthenticatedUser(null);
        }
    }

    /**
     * Once a user is logged in, take the mAuthData provided from Wilddog and "use" it.
     */
    private void setAuthenticatedUser(AuthData authData) {
        if (authData != null) {
            /* Hide all the login buttons */
            mPasswordLoginButton.setVisibility(View.GONE);
            mAnonymousLoginButton.setVisibility(View.GONE);
            mWeiboButton.setVisibility(View.GONE);
            mQQButton.setVisibility(View.GONE);
            mLoggedInStatusTextView.setVisibility(View.VISIBLE);
            /* show a provider specific status text */
            String name = null;
            if (authData.getProvider().equals("weibo")
                    || authData.getProvider().equals("qq")) {
                name = (String) authData.getProviderData().get("displayName");
            } else if (authData.getProvider().equals("anonymous")
                    || authData.getProvider().equals("password")) {
                name = authData.getUid();
            } else {
                Log.e(TAG, "Invalid provider: " + authData.getProvider());
            }
            if (name != null) {
                mLoggedInStatusTextView.setText("Logged in as " + name + " (" + authData.getProvider() + ")");
                finish();
                Intent intent=new Intent(LoginActivity.this,ItemListActivity.class);
                intent.putExtra("name",dispName);
                startActivity(intent);
            }
        } else {
            /* No authenticated user show all the login buttons 登出后再次显示按钮*/
            mPasswordLoginButton.setVisibility(View.VISIBLE);
            mAnonymousLoginButton.setVisibility(View.VISIBLE);
            //add
            mWeiboButton.setVisibility(View.VISIBLE);
            mQQButton.setVisibility(View.VISIBLE);
            mLoggedInStatusTextView.setVisibility(View.GONE);
        }
        this.mAuthData = authData;
        /* invalidate options menu to hide/show the logout button */
        supportInvalidateOptionsMenu();
    }

    /**
     * Show errors to users
     */
    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Utility class for authentication results
     */
    private class AuthResultHandler implements Wilddog.AuthResultHandler {

        private final String provider;

        public AuthResultHandler(String provider) {
            this.provider = provider;
        }

        @Override
        public void onAuthenticated(AuthData authData) {
            mAuthProgressDialog.hide();  //隐藏认证过程框
            Log.i(TAG, provider + " auth successful");
            setAuthenticatedUser(authData);
        }

        @Override
        public void onAuthenticationError(WilddogError wilddogError) {
            mAuthProgressDialog.hide();
            showErrorDialog(wilddogError.toString());
        }
    }

    /* ************************************
     *              PASSWORD              *
     **************************************
     */
    public void loginWithPassword() {
        startActivityForResult(new Intent(LoginActivity.this, PasswordActivity.class),1);
    }
    /* ************************************
     *             ANONYMOUSLY            *
     **************************************
     */
    private void loginAnonymously() {
        mAuthProgressDialog.show();
        mWilddogRef.authAnonymously(new AuthResultHandler("anonymous"));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try{
        String email = data.getExtras().getString("email");//得到新Activity 关闭后返回的数据
        String pwd = data.getExtras().getString("pwd");
        Log.i(TAG, email+" "+pwd);
        dispName=email;  //用于聊天时显示名字
        mAuthProgressDialog.show();
        if((email!=null)&&(pwd!=null))
        mWilddogRef.authWithPassword(email, pwd, new AuthResultHandler("password"));
        }
        catch (Exception e){}
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
       // logout();
    }
}
