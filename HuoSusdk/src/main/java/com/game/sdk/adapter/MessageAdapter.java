package com.game.sdk.adapter;

import android.content.Context;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.game.sdk.domain.MessageBean;
import com.game.sdk.util.MResource;
import com.game.sdk.util.ResourceUtils;

import java.util.List;

/**
 * Created by zhanglei on 2017/7/25.
 */

public class MessageAdapter extends BaseAdapter {
    Context context;
    List<MessageBean> datas;
    private LayoutInflater listContainer;
    private ItemClickListener listener;

    public MessageAdapter(Context context, List<MessageBean> listItems, ItemClickListener listener) {
        this.context = context;
        this.datas = listItems;
        this.listener = listener;
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
        final int selectID = position;
        //自定义视图
        ListItemView listItemView = null;
        if (convertView == null) {
            listItemView = new ListItemView();
            //获取list_item布局文件的视图
            convertView = listContainer.inflate(MResource.getIdByName(context, MResource.LAYOUT,"ck_float_message_adapter"), null);
            //获取控件对象
            listItemView.title = (TextView) convertView.findViewById(MResource.getIdByName(context, "R.id.title"));
            //设置控件集到convertView
            convertView.setTag(listItemView);
        } else {
            listItemView = (ListItemView) convertView.getTag();
        }

        listItemView.title.setText(datas.get(position).getTitle());
        setClickListener(listItemView.title, position);

        if (datas.get(position).getStatus() == 1) {
            TextPaint paint = listItemView.title.getPaint();
            paint.setFakeBoldText(false);
//            listItemView.title.setTextColor(ResourceUtils.getColorId(context,"text_black"));
        } else {
            TextPaint paint = listItemView.title.getPaint();
            paint.setFakeBoldText(true);
//            listItemView.title.setTextColor(ResourceUtils.getColorId(context,"ck_black"));
        }

        return convertView;
    }

    private void setClickListener(final TextView textView, final int position) {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(position);
                }
                TextPaint paint = textView.getPaint();
                paint.setFakeBoldText(false);
                textView.setTextColor(ResourceUtils.getColor(context, "ck_text_black"));
            }
        });
    }


    public final class ListItemView {
        public TextView title;
    }

    public interface ItemClickListener {
        void onClick(int position);
    }
}
