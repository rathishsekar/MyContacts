package com.toppers.mycontacts.widget;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.dd.processbutton.iml.ActionProcessButton;
import com.toppers.mycontacts.R;

/**
 * Created by RATHISH on 05-Nov-15.
 */
public class StartActivity extends AppCompatActivity{

    ActionProcessButton upload,mycontacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        upload = (ActionProcessButton) findViewById(R.id.btnUpload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
