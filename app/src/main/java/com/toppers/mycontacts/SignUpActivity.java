package com.toppers.mycontacts;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.toppers.mycontacts.utils.ProgressGenerator;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class SignUpActivity extends Activity implements ProgressGenerator.OnCompleteListener {


    JSONParser jsonParser = new JSONParser();

    private static String url_login = "http://contacts.16mb.com/signup.php";
    SharedPreferences preferences;
    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    EditText editEmail,editPassword,editPhone;
    ActionProcessButton btnSignIn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_sign_up);

        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);
        editPhone = (EditText) findViewById(R.id.editPhone);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final ProgressGenerator progressGenerator = new ProgressGenerator(this);
        btnSignIn = (ActionProcessButton) findViewById(R.id.btnSignIn);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressGenerator.start(btnSignIn);
                btnSignIn.setEnabled(false);
                editEmail.setEnabled(false);
                editPassword.setEnabled(false);
                new Login().execute();
            }
        });
    }

    @Override
    public void onComplete() {
        Toast.makeText(this, "Loading Complete", Toast.LENGTH_SHORT).show();
        btnSignIn.setEnabled(true);
        editEmail.setEnabled(true);
        editPassword.setEnabled(true);
    }

    class Login extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            btnSignIn.setMode(ActionProcessButton.Mode.ENDLESS);
        }

        /**
         * Getting product details in background thread
         * */
        protected String doInBackground(String... params) {

            Thread t = new Thread(new Runnable() {
                public void run() {

                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("name", editEmail.getText().toString()));
                    params.add(new BasicNameValuePair("password", editPassword.getText().toString()));
                    params.add(new BasicNameValuePair("phone", editPhone.getText().toString()));

                    // getting JSON Object
                    // Note that create product url accepts POST method
                    JSONObject json = jsonParser.makeHttpRequest(url_login,
                            "POST", params);

                    //Log.d("Create Response", json.toString());

                    try {
                        int success = json.getInt(TAG_SUCCESS);

                        if (success == 1) {
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putInt("key", 1);
                            editor.putString("contact",editPhone.getText().toString());
                            editor.apply();
                            Intent intent = new Intent(SignUpActivity.this, DBContactsActivity.class);
                            startActivity(intent);
                            finish();
                        } else {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
            t.start();
            return "";
        }



        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once got all details
            // pDialog.dismiss();
        }
    }


}

