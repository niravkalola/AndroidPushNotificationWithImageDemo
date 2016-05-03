package com.nkdroid.pushnotification;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.GsonBuilder;
import com.nkdroid.pushnotification.model.JSONresponse;
import com.nkdroid.pushnotification.util.GetServiceCall;
import com.nkdroid.pushnotification.util.PrefUtils;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {


    private GoogleCloudMessaging gcm;
    private String regid;
    private String PROJECT_NUMBER = "ADD_YOUR_PROJECT_NUMER_HERE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (PrefUtils.getNotificationId(MainActivity.this).length() == 0) {
            getRegId();
        }

    }

    public void getRegId() {


        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(MainActivity.this);
                    }
                    regid = gcm.register(PROJECT_NUMBER);
                    Log.e("GCM ID :", regid);
                    if (regid == null || regid == "") {
                        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                        alert.setTitle(getString(R.string.ERROR));
                        alert.setMessage(getString(R.string.INTERNALSERVERERROR));
                        alert.setPositiveButton(getString(R.string.TRYAGIN), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getRegId();
                                dialog.dismiss();
                            }
                        });
                        alert.setNegativeButton(getString(R.string.EXIT), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                MainActivity.this.finish();
                            }
                        });
                        alert.show();
                    } else {
                        Log.e("registration id:", regid);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                                progressDialog.setMessage("Registering Device...");
                                progressDialog.show();
                                new GetServiceCall("http://www.nkdroidsolutions.com/push_demo/add_registration_id.php?registration_id=" + regid, GetServiceCall.TYPE_JSONOBJECT) {

                                    @Override
                                    public void response(String response) {

                                        progressDialog.dismiss();
                                        JSONresponse jsoNresponse = new GsonBuilder().create().fromJson(response, JSONresponse.class);
                                        if (jsoNresponse.getStatus() == 1) {
                                            PrefUtils.setNotificationId(regid, MainActivity.this);
                                            Toast.makeText(MainActivity.this, jsoNresponse.getMessage(), Toast.LENGTH_LONG).show();
                                        } else if (jsoNresponse.getStatus() == 2) {
                                            PrefUtils.setNotificationId(regid, MainActivity.this);
                                            Toast.makeText(MainActivity.this, jsoNresponse.getMessage(), Toast.LENGTH_LONG).show();
                                        } else if (jsoNresponse.getStatus() == 0) {
                                            Toast.makeText(MainActivity.this, jsoNresponse.getMessage(), Toast.LENGTH_LONG).show();
                                        }

                                    }

                                    @Override
                                    public void error(VolleyError error) {
                                        progressDialog.dismiss();
                                        error.printStackTrace();
                                    }

                                }.call();
                            }
                        });


                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    Log.e("error", ex.toString());
                }
                return null;
            }
        }.execute();
    } // end of getRegId

}
