package com.toppers.mycontacts;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import java.util.List;
import java.util.TreeSet;


public class MainActivity extends AppCompatActivity {

    private static final int HIGHLIGHT_COLOR = 0x999be6ff;

    ArrayList<HashMap<String, String>> contactsList = new ArrayList<>();
    private ListView mListView;

    JSONParser jsonParser = new JSONParser();

    SharedPreferences preferences;

    private static String url_create_product = "http://contacts.16mb.com/uploadmycontacts.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";


    // list of data items
    public List<ListData> mDataList = Arrays.asList(
            new ListData("Iron Man"),
            new ListData("Captain America")
    );

    // declare the color generator and drawable builder
    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private TextDrawable.IBuilder mDrawableBuilder;
    ActionProcessButton upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        int type = DrawableProvider.SAMPLE_ROUND_RECT_BORDER;
        Cursor cursor = this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        ListData datalist[] = new ListData[cursor.getCount()];
        String list[] = new String[cursor.getCount()];
        int j=0;
        while (cursor.moveToNext()) {
            String string = cursor.getString(cursor.getColumnIndex("display_name"));
            String string2 = cursor.getString(cursor.getColumnIndex("data1"));

            list[j] = string;
            j++;
            HashMap hashMap = new HashMap();
            hashMap.put("name", string);
            hashMap.put("phone", string2);
            contactsList.add(hashMap);
        }
        System.out.println(cursor.getCount() );
        List<String> list1 = Arrays.asList(list);
        TreeSet<String> unique = new TreeSet<String>(list1);
        String[] result = new String[unique.size()];
        unique.toArray(result);
        Arrays.sort(result);
        upload = (ActionProcessButton) findViewById(R.id.btnUpload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SyncData().execute();
            }
        });

       /* ArrayList<HashMap<String, String>> arrayList = contactsList;
        Comparator<HashMap<String, String>> comparator = new Comparator<HashMap<String, String>>(){

            public int compare(HashMap<String, String> hashMap, HashMap<String, String> hashMap2) {
                return (hashMap.get("name")).compareTo((String)hashMap2.get((Object)"name"));
            }
        };
        Collections.sort(arrayList, (Comparator)comparator);*/
        System.out.println(result.length);
        for(int i=0;i<result.length;i++)
            datalist[i] = new ListData(result[i]);
        mDataList = Arrays.asList(datalist);

        // initialize the builder based on the "TYPE"
        switch (DrawableProvider.SAMPLE_ROUND) {
            case DrawableProvider.SAMPLE_RECT:
                mDrawableBuilder = TextDrawable.builder()
                        .rect();
                break;
            case DrawableProvider.SAMPLE_ROUND_RECT:
                mDrawableBuilder = TextDrawable.builder()
                        .roundRect(10);
                break;
            case DrawableProvider.SAMPLE_ROUND:
                mDrawableBuilder = TextDrawable.builder()
                        .round();
                break;
            case DrawableProvider.SAMPLE_RECT_BORDER:
                mDrawableBuilder = TextDrawable.builder()
                        .beginConfig()
                        .withBorder(4)
                        .endConfig()
                        .rect();
                break;
            case DrawableProvider.SAMPLE_ROUND_RECT_BORDER:
                mDrawableBuilder = TextDrawable.builder()
                        .beginConfig()
                        .withBorder(4)
                        .endConfig()
                        .roundRect(10);
                break;
            case DrawableProvider.SAMPLE_ROUND_BORDER:
                mDrawableBuilder = TextDrawable.builder()
                        .beginConfig()
                        .withBorder(4)
                        .endConfig()
                        .round();
                break;
        }

        // init the list view and its adapter
      /*  ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new SampleAdapter());*/
    }

   /* public void cont(){
        int i=0;
        Cursor cursor = this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            String string = cursor.getString(cursor.getColumnIndex("display_name"));
            String string2 = cursor.getString(cursor.getColumnIndex("data1"));
            datalist[i] = new ListData(string);
            i++;
        }
        mDataList = Arrays.asList(datalist);
    }*/


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
                convertView = View.inflate(MainActivity.this, R.layout.list_item_layout, null);
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

        private ImageView checkIcon;

        private ViewHolder(View view) {
            this.view = view;
            imageView = (ImageView) view.findViewById(R.id.imageView);
            textView = (TextView) view.findViewById(R.id.textView);
            checkIcon = (ImageView) view.findViewById(R.id.check_icon);
        }
    }

    private static class ListData {

        private String data;

        private boolean isChecked;

        public ListData(String data) {
            this.data = data;
        }

        public void setChecked(boolean isChecked) {
            this.isChecked = isChecked;
        }
    }

    class SyncData extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        /**
         * Getting product details in background thread
         * */
        protected String doInBackground(String... params) {

            Thread t = new Thread(new Runnable() {
                public void run() {

                    JSONArray jsonArray = new JSONArray();
                    try {
                        for (int i = 0; i < contactsList.size(); i++) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("name", contactsList.get(i).get("name"));
                            jsonObject.put("phone", contactsList.get(i).get("phone"));
                            jsonArray.put(jsonObject);
                        }
                    }catch (Exception e){}
                    String cont = preferences.getString("contact","");
                    Log.d("name",cont);
                    // getting JSON Object
                    // Note that create product url accepts POST method
                    Log.d("",jsonArray.toString());
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("table", cont));
                    params.add(new BasicNameValuePair("contacts", jsonArray.toString()));
                    JSONObject json = jsonParser.makeHttpRequest(url_create_product,
                            "POST", params);

                    //Log.d("Create Response", json.toString());

                   /* try {
                        int success = json.getInt(TAG_SUCCESS);

                        if (success == 1) {
                            Log.d("print",json.getString("message")+"\nrathish");
                        } else {
                            Log.d("print",json.getString("message")+"\nrathish");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/

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

        }
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
        if (id == R.id.cloud_contacts) {
            Intent i = new Intent(this, DBContactsActivity.class);
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