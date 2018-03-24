package com.chat.seecolove.agora;

import android.util.Log;

import com.chat.seecolove.widget.CustomProgressDialog;


import io.agora.rtc.IRtcEngineEventHandler;

/**
 *
 * A handler activity act as a bridge to take callbacks from @MessageHandler.
 * Subclasses should override these key methods.
 */
public class BaseEngineEventHandlerActivity extends BaseActivity {
    protected CustomProgressDialog progressDialog;
    protected void showProgress(int resID) {
        if (progressDialog != null) {
            progressDialog.cancel();
        }
        progressDialog = new CustomProgressDialog(this, getResources()
                .getString(resID));
        progressDialog.show();
    }

    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        Log.i("aa",uid+"=====onJoinChannelSuccess======"+channel);
    }

    public void onRejoinChannelSuccess(String channel, int uid, int elapsed) {
        Log.i("aa",uid+"=====onRejoinChannelSuccess======"+channel);
    }

    public void onError(int err) {
        Log.i("aa","=====onError======");
    }

    public void onCameraReady() {
        Log.i("aa","=====onCameraReady======");
    }

    public void onAudioQuality(int uid, int quality, short delay, short lost) {
        Log.i("aa",uid+"=====onAudioQuality======");
    }

    public void onAudioTransportQuality(int uid, short delay, short lost) {
        Log.i("aa",uid+"=====onAudioTransportQuality======");
    }

    public void onVideoTransportQuality(int uid, short delay, short lost) {
        Log.i("aa",uid+"=====onVideoTransportQuality======");
    }

    public void onLeaveChannel(IRtcEngineEventHandler.RtcStats stats) {
        Log.i("aa","=====onLeaveChannel======");
    }

    public void onUpdateSessionStats(IRtcEngineEventHandler.RtcStats stats) {
        Log.i("aa","=====onUpdateSessionStats======");
    }

    public void onRecap(byte[] recap) {
        Log.i("aa","=====onRecap======");
    }

    public void onAudioVolumeIndication(IRtcEngineEventHandler.AudioVolumeInfo[] speakers, int totalVolume) {
        Log.i("aa","=====onAudioVolumeIndication======");

    }

    public void onNetworkQuality(int quality) {
        Log.i("aa","=====onNetworkQuality======");
    }

    public void onUserJoined(int uid, int elapsed) {
        Log.i("aa","=====onUserJoined======");
    }

    public void onUserOffline(int uid) {
        Log.i("aa","=====onUserOffline======");
    }

    public void onUserMuteAudio(int uid, boolean muted) {
        Log.i("aa","=====onUserMuteAudio======");
    }

    public void onUserMuteVideo(int uid, boolean muted) {
        Log.i("aa","=====onUserMuteVideo======");
    }

    public void onAudioRecorderException(int nLastTimeStamp) {
        Log.i("aa","=====onAudioRecorderException======");
    }

    public void onRemoteVideoStat(int uid, int frameCount, int delay, int receivedBytes) {
        Log.i("aa","=====onRemoteVideoStat======");
    }

    public void onLocalVideoStat(int sentBytes, int sentFrames) {
        Log.i("aa","=====onLocalVideoStat======");
    }

    public void onFirstRemoteVideoFrame(int uid, int width, int height, int elapsed) {
        Log.i("aa","=====onFirstRemoteVideoFrame======");
    }

    public void onFirstLocalVideoFrame(int width, int height, int elapsed) {
        Log.i("aa","=====onFirstLocalVideoFrame======");
    }

    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
        Log.i("aa","=====onFirstRemoteVideoDecoded======");
    }

    public void onConnectionLost() {
        Log.i("aa","=====onConnectionLost======");
    }

    public void onMediaEngineEvent(int code) {
        Log.i("aa","=====onMediaEngineEvent======");
    }

}
