package com.toppers.mycontacts;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.dd.processbutton.iml.ActionProcessButton;
import com.toppers.mycontacts.sample.DrawableProvider;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class DBContactsActivity extends AppCompatActivity {

    private static final int HIGHLIGHT_COLOR = 0x999be6ff;

    ProgressDialog pDialog;

    ArrayList<HashMap<String, String>> productsList;

    JSONParser jsonParser = new JSONParser();

    private static String url_getdb_contacts = "http://contacts.16mb.com/getmydbcontacts.php";
    SharedPreferences preferences;
    //private static String url_all_products = "api.openweathermap.org/data/2.5/weather?q=London,uk";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "products";
    private static final String TAG_NAME = "name";
    private static final String TAG_PHONE = "phone";

    // products JSONArray
    JSONArray products = null;

    // list of data items
    public List<ListData> mDataList = Arrays.asList(new ListData("Iron Man","batman"));
    public ListView mListView;

    // declare the color generator and drawable builder
    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private TextDrawable.IBuilder mDrawableBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        productsList = new ArrayList<HashMap<String, String>>();
        mListView = (ListView)findViewById(R.id.list);
        new SyncData().execute();

        mDrawableBuilder = TextDrawable.builder()
                .beginConfig()
                .withBorder(4)
                .endConfig()
                .roundRect(10);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + productsList.get(position).get("phone")));
                startActivity(intent);
            }
        });

    }

    private class SampleAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDataList.size();
        }

        @Override
        public ListData getItem(int position) {
            return mDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(DBContactsActivity.this, R.layout.list_item_db, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ListData item = getItem(position);

            // provide support for selected state
            updateCheckedState(holder, item);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // when the image is clicked, update the selected state
                    ListData data = getItem(position);
                    data.setChecked(!data.isChecked);
                    updateCheckedState(holder, data);
                }
            });
            holder.textView.setText(item.data);
            holder.textView2.setText(item.data2);
            return convertView;
        }

        private void updateCheckedState(ViewHolder holder, ListData item) {
            if (item.isChecked) {
                holder.imageView.setImageDrawable(mDrawableBuilder.build(" ", 0xff616161));
                holder.view.setBackgroundColor(HIGHLIGHT_COLOR);
                holder.checkIcon.setVisibility(View.VISIBLE);
            }
            else {
                TextDrawable drawable = mDrawableBuilder.build(String.valueOf(item.data.charAt(0)), mColorGenerator.getColor(item.data));
                holder.imageView.setImageDrawable(drawable);
                holder.view.setBackgroundColor(Color.TRANSPARENT);
                holder.checkIcon.setVisibility(View.GONE);
            }
        }
    }

    private static class ViewHolder {

        private View view;

        private ImageView imageView;

        private TextView textView;

        private TextView textView2;

        private ImageView checkIcon;

        private ViewHolder(View view) {
            this.view = view;
            imageView = (ImageView) view.findViewById(R.id.imageView);
            textView = (TextView) view.findViewById(R.id.textView);
            textView2 = (TextView) view.findViewById(R.id.textView2);
            checkIcon = (ImageView) view.findViewById(R.id.check_icon);
        }
    }

    private static class ListData {

        private String data;
        private String data2;

        private boolean isChecked;

        public ListData(String data, String data2) {
            this.data = data;
            this.data2 = data2;
        }

        public void setChecked(boolean isChecked) {
            this.isChecked = isChecked;
        }
    }

    class SyncData extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DBContactsActivity.this);
            pDialog.setMessage("Loading product details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Getting product details in background thread
         */
        protected String doInBackground(String... params1) {

            // updating UI from Background Thread

                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    String phone = preferences.getString("contact", "");
                    params.add(new BasicNameValuePair("phone", phone));

                    // getting JSON Object
                    // Note that create product url accepts POST method
                    JSONObject json = jsonParser.makeHttpRequest(url_getdb_contacts,
                            "POST", params);

                    Log.d("Create Response", json.toString());

                    try {
                        int success = json.getInt(TAG_SUCCESS);

                        if (success == 1) {
                            products = json.getJSONArray(TAG_PRODUCTS);
                            //list[] = new String[products.length()];
                            // looping through All Products
                            for (int i = 0; i < products.length(); i++) {
                                JSONObject c = products.getJSONObject(i);

                                // Storing each json item in variable
                                String name = c.getString("name");
                                String phon = c.getString("phone");

                                // creating new HashMap
                                HashMap<String, String> map = new HashMap<String, String>();

                                // adding each child node to HashMap key => value
                                map.put("name", name);
                                map.put("phone", phon);

                                // adding HashList to ArrayList
                                productsList.add(map);

                            }
                            Log.d("Create Response", "lolo");

                        } else {

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once got all details
            pDialog.dismiss();
            call();
        }
    }

    public void call() {

        SimpleAdapter simpleAdapter = new SimpleAdapter((Context)this, productsList, R.layout.contact_list, new String[]{"name", "phone",
                "photo"}, new int[]{R.id.name, R.id.phone, R.id.photo});
        this.mListView.setAdapter((ListAdapter)simpleAdapter);
        simpleAdapter.notifyDataSetChanged();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.my_contacts) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            return true;
        }
        if (id == R.id.logout) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("key", 0);
            editor.apply();
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}