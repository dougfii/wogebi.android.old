package com.dougfii.android.core.entity;

/**
 * Created by momo on 15/11/1.
 */
public class ResultEntity<T extends BaseEntity> extends BaseEntity {
    private int code = 0;    // 返回代码
    private String msg = "";    // 返回消息
    private T data = null; // 返回数据

    public ResultEntity() {
        super();
    }

    public ResultEntity(int code, String msg, T data) {
        super();
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}