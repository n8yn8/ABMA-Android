package com.n8yn8.abma.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;
import com.n8yn8.abma.BuildConfig;
import com.n8yn8.abma.R;
import com.n8yn8.abma.model.backendless.DbManager;

/**
 * Created by Nate on 3/2/17.
 */

public class LoginDialog extends LinearLayout {

    TextInputLayout confirmInput, passInput, emailInput;
    CheckBox newAccountCheckbox;
    ProgressBar progressBar;
    EditText confirmPassEditText, passEditText, emailEditText;
    Button loginButton;

    OnLoginSuccess loginCallback;

    public LoginDialog(Context context) {
        super(context);
        intiViews();
    }

    public LoginDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        intiViews();
    }

    public LoginDialog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        intiViews();
    }

    @TargetApi(21)
    public LoginDialog(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        intiViews();
    }

    private void intiViews() {
        inflate(getContext(), R.layout.dialog_login, this);

        progressBar = findViewById(R.id.progressBar);

        confirmPassEditText = findViewById(R.id.confirm_edit_text);
        confirmInput = findViewById(R.id.confirmTextInput);

        passEditText = findViewById(R.id.pass_edit_text);
        passInput = findViewById(R.id.passwordInput);

        emailEditText = findViewById(R.id.email_edit_text);
        emailInput = findViewById(R.id.emalInput);

        if (BuildConfig.DEBUG) {
            confirmPassEditText.setText("saigon00"); //TODO: remove
            passEditText.setText("saigon00");
            emailEditText.setText("thelostonefound@gmail.com");
        }

        newAccountCheckbox = (CheckBox) findViewById(R.id.newAccountCheckBox);
        newAccountCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                updateConfirmVisibility();
            }
        });

        loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                updateError(null);
                updateWorking(true);
                String password = passEditText.getText().toString();
                if (TextUtils.isEmpty(password)) {
                    passInput.setError("Password can't be empty");
                    return;
                } else {
                    passInput.setError(null);
                }
                if (newAccountCheckbox.isChecked()) {
                    String confirmPass = confirmPassEditText.getText().toString();
                    if (!password.equals(confirmPass)) {
                        confirmInput.setError("Password doesn't match");
                        return;
                    } else {
                        confirmInput.setError(null);
                    }
                }

                String email = emailEditText.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    emailInput.setError("Email can't be empty");
                    return;
                } else {
                    emailInput.setError(null);
                }

                DbManager.OnLoginResponse callback = new DbManager.OnLoginResponse() {
                    @Override
                    public void onLogin(@Nullable String error) {
                        updateWorking(false);
                        if (error == null) {
                            if (loginCallback != null) {
                                loginCallback.loginSuccess();
                            }
                        } else {
                            updateError(error);
                        }
                    }
                };

                if (newAccountCheckbox.isChecked()) {
                    DbManager.getInstance().register(email, password, callback);
                } else {
                    DbManager.getInstance().login(email, password, callback);
                }
            }
        });

        updateWorking(false);
        updateConfirmVisibility();

    }

    private void updateError(@Nullable String error) {
        TextView errorTextView = (TextView) findViewById(R.id.errorTextView);
        if (error != null) {
            errorTextView.setVisibility(VISIBLE);
            errorTextView.setText("Error: " + error);
        } else {
            errorTextView.setVisibility(GONE);
        }
    }

    private void updateWorking(boolean isWorking) {
        progressBar.setVisibility(isWorking ? VISIBLE : GONE);
        newAccountCheckbox.setEnabled(!isWorking);
        emailEditText.setEnabled(!isWorking);
        passEditText.setEnabled(!isWorking);
        confirmPassEditText.setEnabled(!isWorking);
        loginButton.setEnabled(!isWorking);
    }

    private void updateConfirmVisibility() {
        confirmInput.setVisibility(newAccountCheckbox.isChecked() ? VISIBLE : GONE);
    }

    public interface OnLoginSuccess {
        void loginSuccess();
    }

    public void setCallback(OnLoginSuccess callback) {
        this.loginCallback = callback;
    }
}
