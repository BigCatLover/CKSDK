package com.game.sdk.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.game.sdk.util.MResource;
import com.game.sdk.util.ResourceUtils;

/**
 * Created by zhanglei on 2017/6/18.
 */

public class CkAgreeActivity extends Activity {

    private TextView title;
    private TextView content;
    private TextView content_title;
    private ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(MResource.getIdByName(this,MResource.LAYOUT,"ck_agreenment_activity"));
        setupUI();
    }

    private void setupUI() {
        title = (TextView) findViewById(MResource.getIdByName(this, "R.id.lyg_agreen_title"));
        content = (TextView) findViewById(MResource.getIdByName(this, "R.id.lyg_agreen_content"));
        content_title = (TextView) findViewById(MResource.getIdByName(this, "R.id.title_content"));
        back= (ImageView) findViewById(MResource.getIdByName(this,"R.id.lyg_sdk_iv_return"));
        title.setText(ResourceUtils.getString(this,"ck_protocol"));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        content_title.setText(ResourceUtils.getString(this,"ck_protocol_title"));
        content.setText(ResourceUtils.getString(this,"ck_protocol_content"));
    }
}
