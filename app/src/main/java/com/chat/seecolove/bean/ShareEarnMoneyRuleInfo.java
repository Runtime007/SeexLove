package com.chat.seecolove.bean;

/**
 * 分享赚钱——规则详情
 */
public class ShareEarnMoneyRuleInfo {

    private String order;
    private String ruleNumber;//奖励title顺序
    private String rule; //规则title
    private String ruleInfo; //规则详情
    private boolean arrowStatue; // 箭头状态，false:向下，true:向上

    public ShareEarnMoneyRuleInfo(String order, String ruleNumber, String rule, String ruleInfo) {
        this.order = order;
        this.ruleNumber = ruleNumber;
        this.rule = rule;
        this.ruleInfo = ruleInfo;
    }

    public String getOrder() {
        return order;

    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getRuleNumber() {
        return ruleNumber;
    }

    public void setRuleNumber(String ruleNumber) {
        this.ruleNumber = ruleNumber;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getRuleInfo() {
        return ruleInfo;
    }

    public void setRuleInfo(String ruleInfo) {
        this.ruleInfo = ruleInfo;
    }

    public boolean isArrowStatue() {
        return arrowStatue;
    }

    public void setArrowStatue(boolean arrowStatue) {
        this.arrowStatue = arrowStatue;
    }
}
