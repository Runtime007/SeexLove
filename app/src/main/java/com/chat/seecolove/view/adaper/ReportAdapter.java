package com.chat.seecolove.view.adaper;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.ReportItemInfo;

public class ReportAdapter extends BaseRclvAdapter<ReportItemInfo> {

    private boolean origin = true;

    public ReportAdapter(Context context) {
        super(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindContentItemView(RecyclerView.ViewHolder holder, int position) {
        ReportViewHolder thisHolder = (ReportViewHolder)holder;
        thisHolder.report_item_title.setText(mList.get(position).getContent());
        thisHolder.ckb_report.setChecked(mList.get(position).isCheckStatus());

        if (origin){
            thisHolder.report_item_title.setTextColor(Color.parseColor("#454A51"));
        }else{
            if (mList.get(position).isCheckStatus()){
                thisHolder.report_item_title.setTextColor(Color.parseColor("#454A51"));
            }else{
                thisHolder.report_item_title.setTextColor(Color.parseColor("#9C9C9C"));
            }
        }

    }

    public void setOrigin(boolean origin){
        this.origin = origin;
    }

    @Override
    public int getContentItemType(int position) {
        return 0;
    }

    private static class ReportViewHolder extends RecyclerView.ViewHolder {

        private TextView report_item_title;
        private CheckBox ckb_report;

        public ReportViewHolder(View itemView) {
            super(itemView);
            report_item_title = (TextView) itemView.findViewById(R.id.report_item_title);
            ckb_report = (CheckBox) itemView.findViewById(R.id.ckb_report);
        }
    }

}
