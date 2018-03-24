package com.chat.seecolove.shotimg;

/**
 */
public class YUV {

    public byte[] y;
    public byte[] u;
    public byte[] v;
    public int width = 1;
    public int height = 1;
    public YUV(){
        super();
    }

    public YUV(byte[] y, byte[] u, byte[] v, int width, int height) {
        super();
        this.y = y;
        this.u = u;
        this.v = v;
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {

        return "y:"+getY().length+",u:"+getU().length+",v:"+getV().length+",width:"+getWidth()+",height:"+getHeight();
//        return ",width:"+getWidth()+",height:"+getHeight();
    }

    public byte[] getY() {
        return y;
    }

    public void setY(byte[] y) {
        this.y = y;

    }

    public byte[] getU() {
        return u;
    }

    public void setU(byte[] u) {
        this.u = u;
    }

    public byte[] getV() {
        return v;
    }

    public void setV(byte[] v) {
        this.v = v;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
