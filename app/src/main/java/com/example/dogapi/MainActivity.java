package com.example.dogapi;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    //stared work 29/1/2020 by ranjitha
    private String URLstring = "https://dog.ceo/api/breeds/image/random";
    private static ProgressDialog mProgressDialog;
    private ListView listView;
    ArrayList<DataModel> dataModelArrayList;
    private ListAdapter listAdapter;
    ImageView imgv1,imgv2;
    Bitmap bm =null;
    int REQUEST_CHECK = 0;
    String imgs;
    String s;
    File imgpath;
    private Cursor c=null;
    private  MyDataBase mdb=null;
    private SQLiteDatabase db=null;
    private static final String DB_NAME = "ImgDb.db";
    public static final int DB_VERSION = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgv1 = (ImageView)this.findViewById(R.id.iv);
        imgv2=(ImageView)this.findViewById(R.id.iv1);
        mdb=new MyDataBase(getApplicationContext(), DB_NAME,null,

                DB_VERSION);

        listView = findViewById(R.id.lv);

        retrieveJSON();

    }

    private void retrieveJSON() {

        showSimpleProgressDialog(this, "Loading...","Fetching Json",false);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLstring,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("strrrrr", ">>" + response);

                        try {

                            JSONObject obj = new JSONObject(response);


                               dataModelArrayList = new ArrayList<>();
                                //JSONArray dataArray  = obj.getJSONArray("message");

                                for (int i = 1; i < obj.length(); i++) {

                                    DataModel playerModel = new DataModel();
                                    //JSONObject dataobj = dataArray.getJSONObject(i);

//                                    playerModel.setName(dataobj.getString("name"));
//                                    playerModel.setCountry(dataobj.getString("country"));
//                                    playerModel.setCity(dataobj.getString("city"));
                                    playerModel.setImgURL(obj.getString("message"));

                                    dataModelArrayList.add(playerModel);

                                }

                                setupListview();



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        requestQueue.add(stringRequest);


    }
    private File getFile() {
        File folder = new File("sdcard/attendence");
        if (!folder.exists()) {
            folder.mkdir();
        }
        imgpath = new File(folder, File.separator +

                Calendar.getInstance().getTime() + ".jpg");


        return imgpath;
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        try{
            s = imgpath.toString();
            Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            e.printStackTrace();

        }
        imgv1.setImageDrawable(Drawable.createFromPath(s));


    }
    public void sand(View view)
    {
        db=mdb.getWritableDatabase();
        ContentValues cv =new ContentValues();
        cv.put("path" ,s);
        db.insert("tableimage", null, cv);
        Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
    }
    public void show (View view) {
        String col[] = {"path"};
        db = mdb.getReadableDatabase();
        c = db.query("tableimage", col, null, null, null, null, null);

        c.moveToLast();

        imgs = c.getString(c.getColumnIndex("message"));
        imgv2.setImageDrawable(Drawable.createFromPath(imgs));
    }
    private void setupListview(){
        removeSimpleProgressDialog();  //will remove progress dialog
        listAdapter = new ListAdapter(this, dataModelArrayList);
        listView.setAdapter(listAdapter);
    }

    public static void removeSimpleProgressDialog() {
        try {
            if (mProgressDialog != null) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();

        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void showSimpleProgressDialog(Context context, String title,
                                                String msg, boolean isCancelable) {
        try {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(context, title, msg);
                mProgressDialog.setCancelable(isCancelable);
            }

            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }

        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();
        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


