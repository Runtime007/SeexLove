package com.chat.seecolove.tools;

/**
 * Created by 建成 on 2017-10-25.
 */

public interface IHttpAsyncAttatcher<B, S, F> {
    public void onStart(B param);
    public void onSuccess(S data);
    public void onFail(F data);
}
