package com.jhson.imageload.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import com.jhson.imageload.R;
import com.jhson.imageload.adpater.ImageAdapter;
import com.jhson.imageload.connection.CacheHttpConnection;
import com.jhson.imageload.model.ImageModel;
import com.jhson.imageload.parser.pull.ImageXmlPullParser;
import com.jhson.imageload.task.BaseAsyncTask;
import com.jhson.imageload.task.ImageParsingTask;

import java.util.List;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button domBtn = (Button) findViewById(R.id.dom_btn);
        Button pullBtn = (Button) findViewById(R.id.pull_btn);

        domBtn.setOnClickListener(onClickListener);
        pullBtn.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch (v.getId()){
                case R.id.dom_btn:
                    intent = new Intent(MainActivity.this, DomParserActivity.class);
                    break;
                case R.id.pull_btn:
                    intent = new Intent(MainActivity.this, PullParserActivity.class);
                    break;
            }
            startActivity(intent);

        }
    };
}
