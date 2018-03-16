package com.game.sdk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.game.sdk.domain.AdBean;
import com.game.sdk.util.MResource;
import com.game.sdk.util.ResourceUtils;

import java.util.List;

/**
 * Created by zhanglei on 2017/7/8.
 */

public class AdTitleAdapter extends BaseAdapter {
    Context context;
    List<AdBean> datas;
    int mSelect = 0;   //选中项
    private LayoutInflater listContainer;
    public AdTitleAdapter(Context context, List<AdBean> listItems) {
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

    public void changeSelected(int positon){ //刷新方法
        if(positon != mSelect){
            mSelect = positon;
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int selectID = position;
        //自定义视图
        ListItemView  listItemView = null;
        if (convertView == null) {
            listItemView = new ListItemView();
            //获取list_item布局文件的视图
            convertView = listContainer.inflate(MResource.getIdByName(context,MResource.LAYOUT,"ck_ad_title_adapter"), null);
            //获取控件对象
            listItemView.btn_bg = (RelativeLayout)convertView.findViewById(MResource.getIdByName(context, "R.id.ck_title_bg"));
            listItemView.title = (TextView)convertView.findViewById(MResource.getIdByName(context, "R.id.title"));
            listItemView.lable_ll = (LinearLayout)convertView.findViewById(MResource.getIdByName(context, "R.id.label_layout"));
            //设置控件集到convertView
            convertView.setTag(listItemView);
        }else {
            listItemView = (ListItemView)convertView.getTag();
        }

        listItemView.title.setText(datas.get(position).getName());
        setLable(listItemView.lable_ll,datas.get(position).getLab());
        if(mSelect==position){
            listItemView.btn_bg.setBackgroundResource(MResource.getIdByName(context, "R.drawable.ck_title_selected"));  //选中项背景
        }else{
            listItemView.btn_bg.setBackgroundResource(MResource.getIdByName(context, "R.drawable.ck_title_unselected"));  //其他项背景
        }

        return convertView;
    }

    public void setLable(LinearLayout view,String lable){
        view.removeAllViews();
        ImageView iv = (ImageView) LayoutInflater.from(context).
                inflate(MResource.getIdByName(context,MResource.LAYOUT,"ck_ad_label"), view, false);
        if(lable.equals("限时")){
            iv.setImageResource(ResourceUtils.getDrawableId(context, "ck_label_limittime"));
        }
        if(lable.equals("福利")){
            iv.setImageResource(ResourceUtils.getDrawableId(context, "ck_label_fuli"));
        }
        if(lable.equals("公告")){
            iv.setImageResource(ResourceUtils.getDrawableId(context, "ck_label_announcement"));
        }
        if(lable.equals("火爆")){
            iv.setImageResource(ResourceUtils.getDrawableId(context, "ck_label_hot"));
        }
        if(lable.equals("最新")){
            iv.setImageResource(ResourceUtils.getDrawableId(context, "ck_label_new"));
        }
        if(lable.equals("推荐")){
            iv.setImageResource(ResourceUtils.getDrawableId(context, "ck_label_limittime"));
        }
        if(lable.equals("活动")){
            iv.setImageResource(ResourceUtils.getDrawableId(context, "ck_label_activity"));
        }
        view.addView(iv);
    }

public final class ListItemView{
    public LinearLayout lable_ll;
    public TextView title;
    public RelativeLayout btn_bg;
}
}
