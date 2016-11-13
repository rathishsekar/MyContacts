package com.toppers.mycontacts;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class ContactActivity extends AppCompatActivity {
    //Contact con;
    ArrayList<HashMap<String, String>> contactsList;
    EditText content;
    //DatabaseHandler db;
    ListView list;
    private ProgressDialog pDialog;
    private Button post;
    String value;

    public ContactActivity() {
        ArrayList arrayList;
        this.contactsList = arrayList = new ArrayList();
    }

    public void call() {
        SimpleAdapter simpleAdapter = new SimpleAdapter((Context)this, this.contactsList, R.layout.contact_list, new String[]{"name", "phone",
        "photo"}, new int[]{R.id.name, R.id.phone, R.id.photo});
        this.list.setAdapter((ListAdapter)simpleAdapter);
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.setContentView(R.layout.activity_contacts);
        this.list = (ListView)this.findViewById(R.id.list);
        Cursor cursor = this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            String string = cursor.getString(cursor.getColumnIndex("display_name"));
            String string2 = cursor.getString(cursor.getColumnIndex("data1"));
            String string3 = cursor.getString(cursor.getColumnIndex("photo_uri"));
            HashMap hashMap = new HashMap();
            hashMap.put((Object)"name", (Object)string);
            Log.d((String) "phone", (String) string2);
            hashMap.put((Object) "phone", (Object) string2);
            hashMap.put("photo", string3);
            this.contactsList.add(hashMap);
        }
        ArrayList<HashMap<String, String>> arrayList = this.contactsList;
        Comparator<HashMap<String, String>> comparator = new Comparator<HashMap<String, String>>(){

            public int compare(HashMap<String, String> hashMap, HashMap<String, String> hashMap2) {
                return ((String)hashMap.get((Object)"name")).compareTo((String)hashMap2.get((Object)"name"));
            }
        };
        Collections.sort(arrayList, (Comparator)comparator);
        Bundle bundle2 = this.getIntent().getExtras();
        if (bundle2 != null) {
            this.value = bundle2.getString("id");
        }
        this.call();
    }

   /* public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(2131492864, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int n = menuItem.getItemId();
        if (n == 2131230811) {
            return true;
        }
        if (n == 2131230813) {
            Intent intent = new Intent(this.getApplicationContext(), (Class)Profile.class);
            intent.putExtra("id", this.value);
            this.startActivity(intent);
        }
        if (n == 2131230814) {
            this.con = this.db.getContact(1);
            this.db.deleteContact(this.con);
            Intent intent = new Intent((Context)this, (Class)LoginActivity.class);
            this.startActivity(intent);
            this.finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }*/

}

