package no.ntnu.mikaelr.delta.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.presenter.LoginPresenterImpl;
import no.ntnu.mikaelr.delta.presenter.signature.LoginPresenter;
import no.ntnu.mikaelr.delta.util.SharedPrefsUtil;
import no.ntnu.mikaelr.delta.view.signature.LoginView;

public class LoginActivity extends AppCompatActivity implements LoginView, View.OnClickListener {

    private LoginPresenter presenter;

    private EditText username;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.presenter = new LoginPresenterImpl(this);

        boolean userIsLoggedIn = !SharedPrefsUtil.getInstance().getCookie().equals("");

        if (userIsLoggedIn) {
            presenter.goToMainActivity();
        } else {
            setContentView(R.layout.activity_login);

            username = (EditText) findViewById(R.id.username);
            password = (EditText) findViewById(R.id.password);

            initializeButton();
            setUsernameFromRegistration();

            // Makes the keyboard appear
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    // PRIVATE METHODS -------------------------------------------------------------------------------------------------

    private void initializeButton() {
        AppCompatButton loginButton = (AppCompatButton) findViewById(R.id.login);
        ColorStateList csl = ColorStateList.valueOf(0xff26A69A);
        loginButton.setSupportBackgroundTintList(csl);
        loginButton.setOnClickListener(this);
    }

    private void setUsernameFromRegistration() {
        String usernameFromRegister = getIntent().getStringExtra("username");
        if (usernameFromRegister != null) {
            username.setText(usernameFromRegister);
        }
    }

    // INTERFACE METHODS -----------------------------------------------------------------------------------------------

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // LISTENERS -------------------------------------------------------------------------------------------------------

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login) {
            presenter.login(username.getText().toString(), password.getText().toString());

            // Makes the keyboard disappear
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(username.getWindowToken(), 0);
        }
    }

    public void goToRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
