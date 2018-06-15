package com.example.sannynagveker.qrcodeabs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class RequestActivity extends AppCompatActivity {

    TextView txt_id, txt_cond, txt_name, txt_brand, txt_model_no, txt_sr_no, txt_allocated_to,
            txt_designation,txt_cho_des,txt_middle_name,txt_last_name, txt_modified_on, txt_created_on;
    String str_id, str_cond, str_name, str_brand, str_model_no, str_sr_no, str_allocated_to,
            str_designation,str_cho_des,str_middle_name, str_last_name, str_modified_on, str_created_on;
    SharedPreferences sharedpreferences;
    String token ="";
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Details");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        String s = getIntent().getStringExtra("id");
        sharedpreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        token = sharedpreferences.getString("token_id", null);

        fab  = (FloatingActionButton) findViewById(R.id.fab);
        fab.setClickable(false);

        if (token!=null) {
            AndroidNetworking.get("http://www.abs-projects.co.uk/inventory/stockUsage/view.json?reference_code="+s)
                    //.addPathParameter("data", s)
                    //     .addQueryParameter("limit", "3")
                  //  .addHeaders("accept", "application/x-www-form-urlencoded")
                    .addHeaders("Authorization","Bearer " +token)
                    //     .setTag("test")
                    .setPriority(Priority.LOW)
                    .build()
                    .getAsString( new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            // do anything with response
                           // Toast.makeText(getApplicationContext(), "" + response, Toast.LENGTH_LONG).show();
                            setViews(response);
                            
                        }

                        @Override
                        public void onError(ANError error) {
                            // handle error
                            Log.d("error atreq",error.toString());
                        }
                    });

        }
    }

    private void setViews(String response) {
        txt_id=(TextView) findViewById(R.id.textView5);
        txt_cond=(TextView)findViewById(R.id.textView14);
        txt_name=(TextView)findViewById(R.id.textView2);
        txt_brand=(TextView)findViewById(R.id.textView3);
        txt_model_no=(TextView)findViewById(R.id.textView13);
        txt_sr_no=(TextView)findViewById(R.id.textView6);
        txt_allocated_to=(TextView)findViewById(R.id.textView8);
        txt_designation=(TextView)findViewById(R.id.textView11);
        txt_cho_des=(TextView)findViewById(R.id.textView12);
        txt_middle_name=(TextView)findViewById(R.id.textView9);
        txt_last_name=(TextView)findViewById(R.id.textView10);
        txt_created_on=(TextView)findViewById(R.id.textView16);
        txt_modified_on=(TextView)findViewById(R.id.textView18);
        //b1=(TextView)findViewById(R.id.button);

        try {
            JSONObject jsonObj = new JSONObject(response);
            JSONArray jobjArr = jsonObj.getJSONArray("data");

            txt_id.setText(jobjArr.getJSONObject(0).get("sus_stk_no").toString());

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            SimpleDateFormat sd2 = new SimpleDateFormat("dd-MM-yyyy");
            try {

                Date date = formatter.parse((jobjArr.getJSONObject(0).get("sus_created_dt").toString()).replaceAll("Z$", "+0000"));
                Date date2 = formatter.parse((jobjArr.getJSONObject(0).get("sus_modified_dt").toString()).replaceAll("Z$", "+0000"));
                System.out.println(date);

                String newDate = sd2.format(date);
                String newDate2 = sd2.format(date2);

                System.out.println(newDate);
                str_modified_on = newDate;
                str_created_on = newDate2;

                txt_created_on.setText(newDate);
                txt_modified_on.setText(newDate2);

               // System.out.println("time zone : " + TimeZone.getDefault().getID());
               // System.out.println(formatter.format(date));

            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (jobjArr.getJSONObject(0).getJSONObject("prod").get("prod_indv_use").toString().equals("1")){
                txt_cond.setText("Used individually");
            }
            else{
                txt_cond.setText("Used general");
            }
            str_name =jobjArr.getJSONObject(0).getJSONObject("prod").get("prod_name").toString();
            str_brand = jobjArr.getJSONObject(0).getJSONObject("prod").get("prod_model").toString();
            str_model_no = jobjArr.getJSONObject(0).getJSONObject("prod").get("prod_model_no").toString();

            txt_name.setText(str_name);
            txt_brand.setText(str_brand);
            txt_model_no.setText(str_model_no);

            txt_sr_no.setText("Sr. No.:"+jobjArr.getJSONObject(0).getJSONObject("stk").get("stk_serial_no").toString());

            str_allocated_to = jobjArr.getJSONObject(0).getJSONObject("res").get("res_fname").toString();
            str_last_name = jobjArr.getJSONObject(0).getJSONObject("res").get("res_lname").toString();
            str_middle_name = jobjArr.getJSONObject(0).getJSONObject("res").get("res_mname").toString();

            txt_allocated_to.setText(str_allocated_to);
            txt_designation.setText("Designation: "+jobjArr.getJSONObject(0).getJSONObject("res").get("res_designation").toString());
            txt_cho_des.setText(jobjArr.getJSONObject(0).getJSONObject("cho").get("cho_desc").toString());

            txt_middle_name.setText(str_middle_name);
            txt_last_name.setText(str_last_name);
            fab.setClickable(true);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, ""+str_name+" "+str_brand+" "+str_model_no+"\nAssigned to: "+str_allocated_to+" "+str_middle_name+" "+str_last_name);
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), LandActivity.class);
        startActivity(i);
        finish();
    }
}
