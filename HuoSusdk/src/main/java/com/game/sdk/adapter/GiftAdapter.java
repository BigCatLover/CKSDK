package com.game.sdk.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.game.sdk.dialog.GiftApplyDialogUtil;
import com.game.sdk.domain.AdBean;
import com.game.sdk.domain.GiftBean;
import com.game.sdk.util.MResource;
import com.game.sdk.util.ResourceUtils;

import java.net.FileNameMap;
import java.util.List;

/**
 * Created by zhanglei on 2017/7/25.
 */

public class GiftAdapter extends BaseAdapter {
    Context context;
    List<GiftBean> datas;
    private LayoutInflater listContainer;
    public GiftAdapter(Context context, List<GiftBean> listItems) {
        this.context = context;
        this.datas = listItems;
        listContainer = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //自定义视图
        ListItemView listItemView = null;
        if (convertView == null) {
            listItemView = new ListItemView();
            //获取list_item布局文件的视图
            convertView = listContainer.inflate(MResource.getIdByName(context,MResource.LAYOUT,"ck_float_gift_adapter"), null);
            //获取控件对象
            listItemView.btn = (TextView) convertView.findViewById(MResource.getIdByName(context, "R.id.get"));
            listItemView.content = (TextView)convertView.findViewById(MResource.getIdByName(context, "R.id.content"));
            //设置控件集到convertView
            convertView.setTag(listItemView);
        }else {
            listItemView = (ListItemView)convertView.getTag();
        }

        if(datas.get(position).getRemain()>0){
            if(datas.get(position).getStatus()==0){
                setBtnStatus(listItemView.btn,listItemView.btn,0);
            }else{
                setBtnStatus(listItemView.btn,listItemView.btn,1);
            }
        }else{
            if(datas.get(position).getStatus()==1){
                setBtnStatus(listItemView.btn,listItemView.btn,1);
            }else{
                setBtnStatus(listItemView.btn,listItemView.btn,2);
            }
        }
        listItemView.content.setText(datas.get(position).getContent());

        return convertView;
    }

    //  status 0 领取；1  查看礼包码 2 已领完
    private void setBtnStatus(final TextView view,final TextView btn,int status){
        if(status == 0){
            view.setText("领取");
            view.setBackgroundResource(ResourceUtils.getDrawableId(context, "ck_btn_get"));
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //todo 通知web领取了礼包
                    GiftApplyDialogUtil.showApplyDialog(context,"12345",1);
                    setBtnStatus(view,btn,1);
                }
            });
        }else if(status == 1){
            view.setText("查看礼包码");
            view.setBackgroundResource(ResourceUtils.getDrawableId(context, "ck_btn_hasget"));
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GiftApplyDialogUtil.showApplyDialog(context,"12345",0);
                }
            });
        }else if(status == 2){
            view.setText("已领完");
            view.setBackgroundResource(ResourceUtils.getDrawableId(context, "ck_btn_hasget"));
        }

    }


    public final class ListItemView{
        public TextView btn;
        public TextView content;
    }
}
