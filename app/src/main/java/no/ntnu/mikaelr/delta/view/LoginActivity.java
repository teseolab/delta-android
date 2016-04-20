package no.ntnu.mikaelr.delta.view;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.presenter.LoginPresenterImpl;
import no.ntnu.mikaelr.delta.presenter.signature.LoginPresenter;
import no.ntnu.mikaelr.delta.util.SharedPrefsUtil;
import no.ntnu.mikaelr.delta.view.signature.LoginView;

public class LoginActivity extends AppCompatActivity implements LoginView, View.OnClickListener {

    private LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.presenter = new LoginPresenterImpl(this);

        if (SharedPrefsUtil.getInstance().getCookie().equals("")) {

            setContentView(R.layout.activity_login);

            AppCompatButton loginButton = (AppCompatButton) findViewById(R.id.login);
            ColorStateList csl = ColorStateList.valueOf(0xff26A69A);
            loginButton.setSupportBackgroundTintList(csl);
            loginButton.setOnClickListener(this);

//            ToolbarUtil.initializeToolbar(this, R.drawable.ic_close_white_24dp, "Logg inn");

        } else {
            presenter.goToMainActivity();
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
            EditText username = (EditText) findViewById(R.id.username);
            EditText password = (EditText) findViewById(R.id.password);
            presenter.login(username.getText().toString(), password.getText().toString());
        }
    }
}
