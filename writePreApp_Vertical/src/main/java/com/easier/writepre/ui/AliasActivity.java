package com.easier.writepre.ui;

import android.content.Intent;
import android.os.Bundle;

import com.easier.writepre.R;
import com.easier.writepre.mainview.SocialMainView;

public class AliasActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alias);

        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra(MainActivity.MAIN_TAB_INDEX, MainActivity.TYPE_TWO);
        intent.putExtra(SocialMainView.TAB_INDEX, SocialMainView.TAB_MicroExhibition);
        startActivity(intent);
        finish();
    }
}
