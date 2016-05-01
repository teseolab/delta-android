package no.ntnu.mikaelr.delta.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.presenter.RegisterPresenterImpl;
import no.ntnu.mikaelr.delta.presenter.signature.RegisterPresenter;
import no.ntnu.mikaelr.delta.view.signature.RegisterView;

public class RegisterActivity extends AppCompatActivity implements RegisterView, View.OnClickListener {

    private RegisterPresenter presenter;

    // ACTIVITY METHODS ------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.presenter = new RegisterPresenterImpl(this);

        AppCompatButton loginButton = (AppCompatButton) findViewById(R.id.register);
        ColorStateList csl = ColorStateList.valueOf(0xff26A69A);
        loginButton.setSupportBackgroundTintList(csl);
        loginButton.setOnClickListener(this);

        ToolbarUtil.initializeToolbar(this, R.drawable.ic_close_white_24dp, "Registrer bruker");

        // Makes the keyboard appear
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    // LISTENERS -------------------------------------------------------------------------------------------------------

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.register) {

            EditText username = (EditText) findViewById(R.id.username);
            EditText password = (EditText) findViewById(R.id.password);
            EditText passwordCheck = (EditText) findViewById(R.id.password_check);
            EditText registerCode = (EditText) findViewById(R.id.register_code);

            presenter.register(username.getText().toString(), password.getText().toString(), passwordCheck.getText().toString(), registerCode.getText().toString());

            // Makes the keyboard disappear
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(username.getWindowToken(), 0);

        }
    }

    // INTERFACE METHODS -----------------------------------------------------------------------------------------------

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
