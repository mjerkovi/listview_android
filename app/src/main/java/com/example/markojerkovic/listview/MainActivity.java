package com.example.markojerkovic.listview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import android.view.View;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import android.widget.ArrayAdapter;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = "listview";
    RequestQueue queue;

    private class ListElement {
        private String title;
        private String subtitle;
        private String url;

        ListElement() {};

        ListElement(String tl, String stl, String ul) {
            title = tl;
            subtitle = stl;
            url = ul;
        }
    }

    private ArrayList<ListElement> aList;

    private class MyAdapter extends ArrayAdapter<ListElement> {
        int resource;
        Context context;

        public MyAdapter(Context _context, int _resource, List<ListElement> items) {
            super(_context, _resource, items);
            resource = _resource;
            context = _context;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LinearLayout newView;

            ListElement w = getItem(position);

            // Inflate a new view if necessary.
            if (convertView == null) {
                newView = new LinearLayout(getContext());
                LayoutInflater vi = (LayoutInflater)
                        getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                vi.inflate(resource,  newView, true);
            } else {
                newView = (LinearLayout) convertView;
            }

            TextView title_view = (TextView) newView.findViewById(R.id.textView_title);
            TextView sub_view = (TextView) newView.findViewById(R.id.textView_subtitle);

            title_view.setText(w.title);
            if (w.subtitle == null) {
                sub_view.setText("");
            } else {
                sub_view.setText(w.subtitle);
            }

            // need to add a click listener that has an intent for a webview that goes to the url.
            // Also displays a toast when the list element is pressed.
            newView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int duration = Toast.LENGTH_SHORT;
                    ListElement itm = getItem(position);
                    Toast toast = Toast.makeText(context, itm.url, duration);
                    toast.show();
                    Intent intent = new Intent(MainActivity.this, WebActivity.class);
                    intent.putExtra("URL", itm.url);
                    startActivity(intent);
                }
            });

            return newView;
        }

    }

    private MyAdapter aa;

    public void getSites() {
        String url = "https://luca-ucsc-teaching-backend.appspot.com/hw4/get_news_sites";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray responseArr = response.getJSONArray("news_sites");
                            Log.d(LOG_TAG, "In try block: " + responseArr.toString());
                            for(int i = 0;i <responseArr.length(); ++i){
                                // If the title or url is null then dont add it to the aList
                                // because it shouldn't be displayed in the list.
                                if (responseArr.getJSONObject(i).isNull("title") ||
                                        responseArr.getJSONObject(i).isNull("url")) {
                                    // Do nothing
                                }
                                else {
                                    String temp_title;
                                    String temp_sub;
                                    String temp_url;
                                    temp_title = responseArr.getJSONObject(i).getString("title");
                                    temp_url = responseArr.getJSONObject(i).getString("url");
                                    // If the subtitle is "null" then set the subtitle in the aList
                                    // to null, not the string "null"
                                    if (responseArr.getJSONObject(i).isNull("subtitle")) {
                                        temp_sub = null;
                                    }
                                    else {
                                        temp_sub = responseArr.getJSONObject(i).getString("subtitle");
                                    }
                                    aList.add(new ListElement(temp_title, temp_sub, temp_url));
                                }
                            }
                            aa.notifyDataSetChanged();
                        } catch (Exception e) {
                            Log.d(LOG_TAG, "Bad response");
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d(LOG_TAG, error.toString());
                    }
                });
        queue.add(jsObjRequest);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queue = Volley.newRequestQueue(this);

        aList = new ArrayList<ListElement>();
        aa = new MyAdapter(this, R.layout.list_element, aList);
        ListView myListView = (ListView) findViewById(R.id.site_list);
        myListView.setAdapter(aa);
        aa.notifyDataSetChanged();
        getSites();
    }

    public void clickRefresh (View v) {
        aList.clear();
        getSites();
    }
}
