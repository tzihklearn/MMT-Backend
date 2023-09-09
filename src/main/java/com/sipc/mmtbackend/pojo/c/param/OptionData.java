package com.sipc.mmtbackend.pojo.c.param;

/**
 * @Author yuleng
 * @Date 2022/7/24 20:29
 * @Version 1.0
 */
public class OptionData {

    private String a;
    private String b;
    private String c;
    private String d;

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    @Override
    public String toString() {
        if (this.c == null) {
            return a + '&' + b;
        }
        if (this.d == null) {
            return a + '&' + b + '&' + c;
        }
        return a + '&' + b + '&' + c + '&' + d;
    }
}
