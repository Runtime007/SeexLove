package com.chat.seecolove.view.adaper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.ShareEarnMoneyRuleInfo;
import com.chat.seecolove.widget.recycleview.BaseRclvAdapter;


public class ShareEarnMoneyAdapter extends BaseRclvAdapter<ShareEarnMoneyRuleInfo> {
    public ShareEarnMoneyAdapter(Context context) {
        super(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_share_earn_money, null);
        return new ShareEarnMoneyHolder(view);
    }

    @Override
    public void onBindContentItemView(RecyclerView.ViewHolder holder, final int position) {
        ShareEarnMoneyHolder thisHolder = (ShareEarnMoneyHolder) holder;
        final ShareEarnMoneyRuleInfo item = mList.get(position);
        if (item.isArrowStatue()) {
            thisHolder.iv_arraow.setImageResource(R.mipmap.icon_shrink);
            thisHolder.tv_award_rule_info.setVisibility(View.VISIBLE);
        } else {
            thisHolder.iv_arraow.setImageResource(R.mipmap.icon_drop_down);
            thisHolder.tv_award_rule_info.setVisibility(View.GONE);
        }

        thisHolder.tv_order.setText(item.getOrder());
        thisHolder.tv_award_num.setText(item.getRuleNumber());
        thisHolder.tv_award_rule.setText(item.getRule());
        thisHolder.tv_award_rule_info.setText(item.getRuleInfo());

        thisHolder.layout_award.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.isArrowStatue()) {
                    item.setArrowStatue(false);
                } else {
                    item.setArrowStatue(true);
                }
                notifyItemChanged(position);
            }
        });

    }

    @Override
    public int getContentItemType(int position) {
        return 0;
    }

    static class ShareEarnMoneyHolder extends RecyclerView.ViewHolder {

        private TextView tv_order;
        private TextView tv_award_num;
        private TextView tv_award_rule;
        private ImageView iv_arraow;
        private TextView tv_award_rule_info;
        private View layout_award;

        public ShareEarnMoneyHolder(View itemView) {
            super(itemView);
            tv_order = (TextView) itemView.findViewById(R.id.tv_order);
            tv_award_num = (TextView) itemView.findViewById(R.id.tv_award_num);
            tv_award_rule = (TextView) itemView.findViewById(R.id.tv_award_rule);
            iv_arraow = (ImageView) itemView.findViewById(R.id.iv_arraow);
            tv_award_rule_info = (TextView) itemView.findViewById(R.id.tv_award_rule_info);
            layout_award = itemView.findViewById(R.id.layout_award);
        }
    }
}
