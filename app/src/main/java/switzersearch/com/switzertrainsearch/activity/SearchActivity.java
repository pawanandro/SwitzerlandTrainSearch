package switzersearch.com.switzertrainsearch.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import switzersearch.com.switzertrainsearch.R;
import switzersearch.com.switzertrainsearch.data.DBHelper;
import switzersearch.com.switzertrainsearch.model.Prognosis;
import switzersearch.com.switzertrainsearch.model.Station;
import switzersearch.com.switzertrainsearch.model.coordinate;
import switzersearch.com.switzertrainsearch.model.from;

public class SearchActivity extends AppCompatActivity {
    private static String url = "http://transport.opendata.ch/v1/connections?";
    private EditText mEtFrom, mEtTo;
    private Button mSubmit,mkFavourite;
    private TextView mTvDepart, mTvArr;
    private Gson mGson;
    private DBHelper mDbHelper;
    private SQLiteDatabase dbWrite;
    private String ConnectionResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initialize();
        setTitle("Search For Your Trains");
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callSearch();
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                if (inputManager != null) {
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });

        mkFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToFavouriteList();
            }
        });
    }

    private void initialize()
    {
        mEtFrom = findViewById(R.id.etFrom);
        mEtTo = findViewById(R.id.etTo);
        mSubmit = findViewById(R.id.submit);
        mkFavourite = findViewById(R.id.mkFavourite);
        mTvArr = findViewById(R.id.tvArrival);
        mTvDepart = findViewById(R.id.tvDepart);

    }


    private void callSearch()
    {
        if(!(mEtFrom.getText().toString().equals("")&& mEtTo.getText().toString().equals(""))  )
        {
            callNetWorkOperation();

        }
        else {
            Toast.makeText(this, "from or To address cann't be empty", Toast.LENGTH_SHORT).show();

        }
    }

    private void callNetWorkOperation()
    {
        mGson =  new Gson();

        String dt=url + "from=" + mEtFrom.getText().toString() + "&to=" + mEtTo.getText().toString();
        System.out.println(dt);
        //JsonObjectRequest jsonObjectRequest = new

        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, url + "from=" + mEtFrom.getText().toString() + "&to=" + mEtTo.getText().toString(), null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        ConnectionResult=response.toString();
                        from fromObj = new from();
                        from toObj = new from();
                        coordinate coordinate1 = new coordinate();
                        Prognosis prognosis = new Prognosis();
                        Station station = new Station();
                        try {
                            fromObj = mGson.fromJson(response.getJSONArray("connections").getJSONObject(0).getJSONObject("from").toString(), from.class);
                            //obj.setDeparture(response.getJSONArray("connections").getJSONObject(0).getString("departure"));
                            prognosis = mGson.fromJson(response.getJSONArray("connections").getJSONObject(0).getJSONObject("from").getJSONObject("prognosis").toString(), Prognosis.class);
                            coordinate1 = mGson.fromJson(response.getJSONArray("connections").getJSONObject(0).getJSONObject("from").getJSONObject("station").getJSONObject("coordinate").toString(), coordinate.class);
                            station = mGson.fromJson(response.getJSONArray("connections").getJSONObject(0).getJSONObject("from").getJSONObject("station").toString(), Station.class);
                            fromObj.setCoordinate1(coordinate1);
                            fromObj.setStation(station);
                            fromObj.setPrognosis(prognosis);


                            toObj = mGson.fromJson(response.getJSONArray("connections").getJSONObject(0).getJSONObject("to").toString(), from.class);
                            //obj.setDeparture(response.getJSONArray("connections").getJSONObject(0).getString("departure"));
                            prognosis = mGson.fromJson(response.getJSONArray("connections").getJSONObject(0).getJSONObject("to").getJSONObject("prognosis").toString(), Prognosis.class);
                            coordinate1 = mGson.fromJson(response.getJSONArray("connections").getJSONObject(0).getJSONObject("to").getJSONObject("station").getJSONObject("coordinate").toString(), coordinate.class);
                            station = mGson.fromJson(response.getJSONArray("connections").getJSONObject(0).getJSONObject("to").getJSONObject("station").toString(), Station.class);
                            toObj.setCoordinate1(coordinate1);
                            toObj.setStation(station);
                            toObj.setPrognosis(prognosis);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String mDepart= null;
                        String mArr= null;
                        if (fromObj.getStation()!=null || toObj.getStation()!=null) {
                            mDepart = "Platform: "+fromObj.getPlatform()+"\n\n Station Name: "+fromObj.getStation().getName()+"\n\n Departure Time: "+ timeStampToDate(fromObj.getDepartureTimestamp());
                            mArr = "Platform: "+toObj.getPlatform()+"\n\n Station Name: "+toObj.getStation().getName()+"\n\n  Arrival Time: "+ timeStampToDate(toObj.getArrivalTimestamp());
                            mDepart=mDepart.replace("null","Not Available");
                            mArr=mArr.replace("null","Not Available");
                            mTvDepart.setText(mDepart);
                            mTvArr.setText(mArr);
                        }
                        else{
                            Toast.makeText(SearchActivity.this, "Please enter Station names correctly", Toast.LENGTH_SHORT).show();

                        }

                        //System.out.println("from:"+fromObj.getDepartureTimestamp()+"\n station:"+fromObj.getStation().getName()+"\n Coordinate:"+fromObj.getCoordinate1().getType()+"\n Prgnosis:"+fromObj.getPrognosis().getDeparture());
                        //System.out.println("To:"+toObj.getArrivalTimestamp()+"\n station:"+toObj.getStation().getName()+"\n Coordinate:"+toObj.getCoordinate1().getType()+"\n Prgnosis:"+toObj.getPrognosis().getDeparture());

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);

        // AppContoller.getInstance().addToRequestQueue(jsonArrayRequest);
    }

    private String timeStampToDate(Long ts)
    {
        Timestamp timestamp = new Timestamp(ts);
        Date dt = new Date(timestamp.getTime());
        String date=dt.toString().replace("GMT+5:30 1970","");
        return dt.toString();
    }


    private void addToFavouriteList()
    {
        mDbHelper = new DBHelper(this);
        dbWrite = mDbHelper.getWritableDatabase();
        if(ConnectionResult!=null)
            DBHelper.writeToSQLDB(dbWrite, ConnectionResult);
        dbWrite.close();
    }

    public String getLocalTime(String OurDate) {
        String formattedTime = null;
        try {
            System.out.println("OurDate:"+OurDate);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat output = new SimpleDateFormat("EEEE, dd MMM yyyy");
            Date d = null;
            try {
                d = sdf.parse(OurDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            formattedTime = output.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formattedTime;
    }
}