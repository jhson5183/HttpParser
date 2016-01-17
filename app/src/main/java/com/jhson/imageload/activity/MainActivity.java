package com.jhson.imageload.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import com.jhson.imageload.R;
import com.jhson.imageload.adpater.ImageAdapter;
import com.jhson.imageload.connection.CacheHttpConnection;
import com.jhson.imageload.fragment.PullParserFragment;
import com.jhson.imageload.fragment.SaxParserFragment;
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
            switch (v.getId()){
                case R.id.dom_btn:
                    addFragment(getFragment(SaxParserFragment.class));
                    break;
                case R.id.pull_btn:
                    addFragment(getFragment(PullParserFragment.class));
                    break;
            }

        }
    };

    private void addFragment(Fragment fragment){
        if(fragment == null){
            return ;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
//        if(fragmentManager.findFragmentByTag(fragment.getClass().getSimpleName()) != null){
//            return ;
//        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content_fragment, fragment, fragment.getClass().getSimpleName());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private Fragment getFragment(Class<?> cls){
        Fragment fragment = null;

        try {
            fragment = (Fragment)cls.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return fragment;
    }
}
