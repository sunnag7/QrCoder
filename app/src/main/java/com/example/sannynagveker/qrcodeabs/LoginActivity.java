package com.example.sannynagveker.qrcodeabs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    EditText txtUsr = null, txtPass = null;
    Button mButtonSubmit = null;
    SharedPreferences sharedpreferences;
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedpreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        txtUsr = (EditText) findViewById(R.id.editText);
        txtPass =(EditText) findViewById(R.id.editText2);
        mButtonSubmit = (Button) findViewById(R.id.button);
        spinner = (ProgressBar)findViewById(R.id.progressBar1);

        mButtonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!txtUsr.getText().toString().equals("")&& !txtPass.getText().toString().equals("")){

                   postLogin();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),
                            " Invalid UserName or Password !!",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void postLogin() {
        spinner.setVisibility(View.VISIBLE);
        AndroidNetworking.post("http://www.abs-projects.co.uk/inventory/login.json")
            .addBodyParameter("usr_username", txtUsr.getText().toString())
            .addBodyParameter("usr_password", txtPass.getText().toString())
           // .setTag("test")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    spinner.setVisibility(View.GONE);
                    Log.d("in login",
                            ""+response.toString() );
                    try {
                        if (response.get("status").equals("Success")){
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            JSONObject k = response.getJSONObject("data");
                            editor.putString("token_id", k.getString("token") );
                            editor.apply();
                            Intent i = new Intent(getApplicationContext(), LandActivity.class);
                            startActivity(i);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onError(ANError error) {
                    // handle error
                    spinner.setVisibility(View.GONE);
                }
            });

    }

}
