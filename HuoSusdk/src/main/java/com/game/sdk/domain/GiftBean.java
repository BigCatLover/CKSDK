package com.game.sdk.domain;

/**
 * Created by zhanglei on 2017/7/25.
 */

public class GiftBean {
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getGiftId() {
        return giftId;
    }

    public void setGiftId(String giftId) {
        this.giftId = giftId;
    }

    private int status;
    private String content;
    private String gameId;
    private String giftId;

    public int getRemain() {
        return remain;
    }

    public void setRemain(int remain) {
        this.remain = remain;
    }

    private int remain;
}
