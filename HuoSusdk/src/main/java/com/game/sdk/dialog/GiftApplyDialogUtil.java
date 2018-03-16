package com.game.sdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.game.sdk.log.T;
import com.game.sdk.util.BaseAppUtil;
import com.game.sdk.util.MResource;

/**
 * Created by liu hong liang on 2016/10/11.
 */

public class GiftApplyDialogUtil {
    public static void showApplyDialog(final Context context, final String codeContent, int type) {
        View contentView = LayoutInflater.from(context).inflate(MResource.getIdByName(context, MResource.LAYOUT,"ck_check_gift_code_dialog"), null);
        LinearLayout title_ll = (LinearLayout) contentView.findViewById(MResource.getIdByName(context, "R.id.title_ll"));
        TextView title = (TextView) contentView.findViewById(MResource.getIdByName(context, "R.id.tv_title"));
        title.setText("领取成功");
        title_ll.setVisibility(View.VISIBLE);
        TextView code = (TextView) contentView.findViewById(MResource.getIdByName(context, "R.id.tv_codeContent"));
        TextView tvCopy = (TextView) contentView.findViewById(MResource.getIdByName(context, "R.id.tv_copy"));
        TextView tvHint = (TextView) contentView.findViewById(MResource.getIdByName(context, "R.id.tv_hint"));
        TextView tvCodeName = (TextView) contentView.findViewById(MResource.getIdByName(context, "R.id.tv_codeName"));
        String content = "礼包领取成功,请在有效期间，在游戏内使用。";
        tvHint.setText(content);
        code.setText(codeContent);
        tvCodeName.setText("礼包码");
        tvCopy.setText("复制");

        tvCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseAppUtil.copyToSystem(context, codeContent);
                T.s(context, "复制成功");
            }
        });
        final Dialog dialog = new Dialog(context, MResource.getIdByName(context, "R.style.lyg_sdk_dialog_bg_style"));

        dialog.setContentView(contentView);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

}
