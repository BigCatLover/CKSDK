package com.game.sdk.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.game.sdk.adapter.GiftAdapter;
import com.game.sdk.adapter.MessageAdapter;
import com.game.sdk.domain.GiftBean;
import com.game.sdk.domain.MessageBean;
import com.game.sdk.util.MResource;

import java.util.ArrayList;
import java.util.List;

public class FloatDialogActivity extends Activity implements View.OnClickListener{
    private int type;//1 消息； 2 礼包
    private ListView listView;
    private TextView detail_title,detail_content,title;
    private ImageView detailClose,close;
    private RelativeLayout message_ll;
    private RelativeLayout detail_ll;
    private MessageAdapter messageAdapter;
    private GiftAdapter giftAdapter;
    private List<MessageBean> messageList = new ArrayList<>();
    private List<GiftBean> giiftList = new ArrayList<>();
    private boolean msgCloseFlag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        intiData();
        type = intent.getIntExtra("type",0);
        if(type == 1){
            setupMessage();
        }else if(type == 2){
            setupGift();
        }
    }

    private void intiData(){
        MessageBean bean = new MessageBean();
        bean.setContent("content111");
        bean.setTitle("title 1");
        bean.setStatus(0);
        messageList.add(bean);
        MessageBean bean1 = new MessageBean();
        bean1.setContent("content2222");
        bean1.setTitle("title 2");
        bean1.setStatus(1);
        messageList.add(bean1);
        MessageBean bean2 = new MessageBean();
        bean2.setContent("content3333");
        bean2.setTitle("title 3");
        bean2.setStatus(0);
        messageList.add(bean2);

        GiftBean bean3 = new GiftBean();
        bean3.setStatus(0);
        bean3.setContent("gift11");
        bean3.setRemain(0);
        giiftList.add(bean3);
        GiftBean bean4 = new GiftBean();
        bean4.setStatus(1);
        bean4.setContent("gift22");
        bean4.setRemain(0);
        giiftList.add(bean4);
        GiftBean bean5 = new GiftBean();
        bean5.setStatus(0);
        bean5.setContent("gift33");
        bean5.setRemain(4);
        giiftList.add(bean5);
        GiftBean bean6 = new GiftBean();
        bean6.setStatus(1);
        bean6.setContent("gift44");
        bean6.setRemain(5);
        giiftList.add(bean6);
    }

    private void setupMessage(){
        setContentView(MResource.getIdByName(getApplication(), "layout", "ck_include_dialog_message"));
        listView = (ListView) findViewById(MResource.getIdByName(this, "R.id.list_view"));
        message_ll = (RelativeLayout) findViewById(MResource.getIdByName(this, "R.id.message_ll"));
        detail_ll = (RelativeLayout) findViewById(MResource.getIdByName(this, "R.id.detail_ll"));
        detail_ll.setVisibility(View.INVISIBLE);
        message_ll.setVisibility(View.VISIBLE);
        detail_title = (TextView)findViewById(MResource.getIdByName(this, "R.id.title"));
        detail_content = (TextView)findViewById(MResource.getIdByName(this, "R.id.content"));
        close = (ImageView) findViewById(MResource.getIdByName(this, "R.id.close"));
        detailClose = (ImageView)findViewById(MResource.getIdByName(this, "R.id.detail_close"));
        close.setOnClickListener(this);
        detailClose.setOnClickListener(this);
        messageAdapter = new MessageAdapter(this, messageList, new MessageAdapter.ItemClickListener() {
            @Override
            public void onClick(int position) {
                msgCloseFlag=true;
                detail_title.setText(messageList.get(position).getTitle());
                detail_content.setText(messageList.get(position).getContent());
                detail_ll.setVisibility(View.VISIBLE);
                message_ll.setVisibility(View.INVISIBLE);
            }
        });
        listView.setAdapter(messageAdapter);
    }

    private void setupGift(){
        setContentView(MResource.getIdByName(getApplication(), "layout", "ck_include_dialog_gift"));
        listView = (ListView) findViewById(MResource.getIdByName(this, "R.id.list_view"));
        title = (TextView)findViewById(MResource.getIdByName(this, "R.id.title"));
        title.setText("礼包福利");
        close = (ImageView) findViewById(MResource.getIdByName(this, "R.id.close"));
        close.setOnClickListener(this);
        giftAdapter = new GiftAdapter(this,giiftList);
        listView.setAdapter(giftAdapter);
    }

    @Override
    public void onBackPressed() {
       if(msgCloseFlag){
           msgCloseFlag=false;
           detail_ll.setVisibility(View.INVISIBLE);
           message_ll.setVisibility(View.VISIBLE);
       }else{
           this.finish();
       }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==close.getId()){
            this.finish();
        }else if(v.getId()==detailClose.getId()){
            detail_ll.setVisibility(View.INVISIBLE);
            message_ll.setVisibility(View.VISIBLE);
        }
    }



    public static void start(Context context, int type) {
        Intent starter = new Intent(context, FloatDialogActivity.class);
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_NO_ANIMATION);
        starter.putExtra("type",type);
        context.startActivity(starter);
    }
}
