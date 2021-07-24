package com.acorn.test.unitydemo4.bean;

import java.io.Serializable;

/**
 * Author:yu
 * Date: 2019/4/26
 * Description:
 **/
public class ResultObject<T> implements Serializable {
    private static final long serialVersionUID = 5213230387175987834L;

    public int code;
    public String msg;
    public T data;

    @Override
    public String toString() {
        return "LzyResponse{\n" +//
                "\tcode=" + code + "\n" +//
                "\tmsg='" + msg + "\'\n" +//
                "\tdata=" + data + "\n" +//
                '}';
    }
}
