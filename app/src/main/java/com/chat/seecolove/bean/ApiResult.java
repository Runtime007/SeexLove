package com.chat.seecolove.bean;

/**
 * Created by 建成 on 2017-10-26.
 */

public class ApiResult<D, A> {


    /**
     * 返回的状态值 0:失败 1:成功 2:未登录 4:余额不足 5：没有返佣的订单
     */
    private int resultCode = 0;
    /**
     * 返回的信息描述 如：用户不存在 rd 为1时可为空
     */
    private String resultMessage = "";
    /**
     * 页面总数
     */
    private int totalPage;

    /**
     * 卖家总数
     */
    private int totalSeller;
    /**
     * 当前页面数
     */
    private int currentPage;
    /**
     * 数据集 list Map obj 等
     */
    private D dataCollection;
    /**
     * 新加一个额外参数,用于传递额外值
     */
    private A attribute;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public D getDataCollection() {
        return dataCollection;
    }

    public void setDataCollection(D dataCollection) {
        this.dataCollection = dataCollection;
    }

    public int getTotalSeller() {
        return totalSeller;
    }

    public void setTotalSeller(int totalSeller) {
        this.totalSeller = totalSeller;
    }

    public A getAttribute() {
        return attribute;
    }

    public void setAttribute(A attribute) {
        this.attribute = attribute;
    }

}
