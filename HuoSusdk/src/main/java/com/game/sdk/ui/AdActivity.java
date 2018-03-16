package com.game.sdk.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.game.sdk.CKGameManager;
import com.game.sdk.adapter.AdTitleAdapter;
import com.game.sdk.util.MResource;
import com.game.sdk.util.ResourceUtils;

public class AdActivity extends Activity {
    ImageView close;
    RelativeLayout title_ll;
    TextView title;
    TextView checkDetail;
    ImageView imageView;
    ListView listView;
    AdTitleAdapter adapter;
    int currentPosition=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        int count = getIntent().getIntExtra("count",0);
        if(count<=1){
            setContentView(MResource.getIdByName(this,MResource.LAYOUT,"ck_ad_dialog_single"));
        }else{
            setContentView(MResource.getIdByName(this,MResource.LAYOUT,"ck_ad_dialog_more"));
        }
        setupUI(count);
    }
    private void setupUI(final int count){
        if(count<=1){
            close = (ImageView) findViewById(MResource.getIdByName(this, "R.id.close"));
            title_ll = (RelativeLayout)findViewById(MResource.getIdByName(this, "R.id.title_ll"));
            imageView = (ImageView) findViewById(MResource.getIdByName(this, "R.id.url_single"));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(count==1){
                        finish();

                    }else {
                        finish();
                    }
                }
            });
            title = (TextView) findViewById(MResource.getIdByName(this, "R.id.title"));
            if(count==1){
                title.setText(CKGameManager.getAdList().get(0).getName());
                loadImage(CKGameManager.getAdList().get(0).getImg(),imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AdWebActivity.start(AdActivity.this, CKGameManager.getAdList().get(0).getUrl());
                    }
                });
            }else {
                imageView.setImageResource(ResourceUtils.getDrawableId(this,"ck_ad_default"));
                title.setText("捞月游戏中心");
            }

        }else {
            close = (ImageView) findViewById(MResource.getIdByName(this, "R.id.close1"));
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            title = (TextView) findViewById(MResource.getIdByName(this, "R.id.title"));
            imageView = (ImageView) findViewById(MResource.getIdByName(this, "R.id.image_more"));
            listView = (ListView)findViewById(MResource.getIdByName(this, "R.id.title_recycleview"));
            checkDetail = (TextView) findViewById(MResource.getIdByName(this, "R.id.check_detail"));
            checkDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AdWebActivity.start(AdActivity.this, CKGameManager.getAdList().get(currentPosition).getUrl());
                }
            });

            loadImage(CKGameManager.getAdList().get(0).getImg(),imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AdWebActivity.start(AdActivity.this, CKGameManager.getAdList().get(0).getUrl());
                }
            });
            adapter = new AdTitleAdapter(this, CKGameManager.getInstance().getAdList());
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    adapter.changeSelected(position);//刷新
                    currentPosition=position;
                    loadImage(CKGameManager.getAdList().get(position).getImg(),imageView);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AdWebActivity.start(AdActivity.this, CKGameManager.getAdList().get(position).getUrl());
                        }
                    });

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
       return;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CKGameManager.getInstance().showFloatView();
    }
    private void loadImage(final String url, ImageView imageView){
        if(url==null||url.isEmpty()){
            Glide.with(this).load(ResourceUtils.getDrawableId(this,"ck_image_bg"))
                    .error(ResourceUtils.getDrawableId(this,"ck_image_bg"))
                    .placeholder(ResourceUtils.getDrawableId(this,"ck_image_bg")).into(imageView);
            return;
        }
        Glide.with(this).load(url).error(ResourceUtils.getDrawableId(this,"ck_image_bg"))
                .placeholder(ResourceUtils.getDrawableId(this,"ck_image_bg")).into(imageView);
    }

    public static void start(Activity activity, int count) {
        Intent starter = new Intent(activity, AdActivity.class);
        starter.putExtra("count",count);
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(starter);
        activity.finish();
    }
}
