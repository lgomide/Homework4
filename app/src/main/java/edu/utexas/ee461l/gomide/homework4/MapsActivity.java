package edu.utexas.ee461l.gomide.homework4;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public void changeMap(View v){
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    public void changeMap2(View v){
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    }

    public void changeMap3(View v){
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    public void searchMap(View v){
        HttpClient httpclient = new DefaultHttpClient();
        EditText text = (EditText) findViewById(R.id.search);
        String search = text.getText().toString().replace(' ','+');
        String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query="+search+"&key=AIzaSyAJaq4XgLly78-mQOiuYf0PGBzQ7mQZnCQ";
        try {
            HttpResponse response = httpclient.execute(new HttpGet(url));
            HttpEntity httpEntity = response.getEntity();
            String jsonString = EntityUtils.toString(httpEntity);
            JSONObject result = new JSONObject(jsonString);
            JSONArray results = result.getJSONArray("results");
            JSONObject place = results.getJSONObject(0);
            String name = place.getString("name");
            String fullName = place.getString("formatted_address");
            String icon = place.getString("icon");
            Double lat = place.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
            Double lng = place.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
            URL icon_url = new URL(icon);
            HttpURLConnection conn = (HttpURLConnection) icon_url.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            Bitmap bmImg = BitmapFactory.decodeStream(is);
            LatLng placeLatLng = new LatLng(lat,lng);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLatLng, 15));
            mMap.addMarker(new MarkerOptions().position(placeLatLng).title(name).icon(BitmapDescriptorFactory.fromBitmap(bmImg)).snippet(fullName));

        } catch(ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } catch (JSONException e){
            e.printStackTrace();
        }

    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
        }
    }


}
