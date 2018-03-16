package com.game.sdk.util;

import com.game.sdk.domain.WebLoadAssert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liu hong liang on 2016/11/1.
 */

public class WebLoadByAssertUtil {
    private static List<WebLoadAssert> webLoadAssertList;
    static {
        webLoadAssertList=new ArrayList<>();
        webLoadAssertList.add(new WebLoadAssert("image/png","alipay.png"));
        webLoadAssertList.add(new WebLoadAssert("image/png","unionpay.png"));
        webLoadAssertList.add(new WebLoadAssert("image/png","wxpay.png"));
        webLoadAssertList.add(new WebLoadAssert("application/x-javascript","fastclick.js"));
        webLoadAssertList.add(new WebLoadAssert("application/x-javascript","payment.js"));
        webLoadAssertList.add(new WebLoadAssert("text/css","payment.css"));
    }

    public static List<WebLoadAssert> getWebLoadAssertList() {
        return webLoadAssertList;
    }
}
