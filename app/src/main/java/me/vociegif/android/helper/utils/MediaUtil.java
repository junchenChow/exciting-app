package me.vociegif.android.helper.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.view.View;

import java.io.IOException;

public class MediaUtil {
    private static MediaPlayer player = null;
    private static View v_playVoice;

    public static boolean isPlayingVoice() {
        if (player != null && player.isPlaying()) {
            return true;
        }
        return false;
    }

    public static void playNewShake(Context context){
        playVoice(context, "shake_sound_male.mp3");
    }
    /**
     * 播放音频文件
     *
     * @param ctx
     */

    public static void playVoice(Context ctx, String name) {
        if (MediaUtil.isPlayingVoice()) {
            MediaUtil.stopPlayVoice();
        }
        if (player != null && player.isPlaying()) {
            if (v_playVoice != null) {
                AnimationDrawable ad = ((AnimationDrawable) v_playVoice.getBackground());ad.stop();
            }
            player.release();
            player = null;
            v_playVoice = null;
            return;
        }
        try {
            player = new MediaPlayer();
            AssetFileDescriptor fileDescriptor = ctx.getAssets().openFd(name);
            player.setDataSource(fileDescriptor.getFileDescriptor(),
                    fileDescriptor.getStartOffset(),
                    fileDescriptor.getLength());
            player.prepare();
            player.start();
            player.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    player.release();
                    player = null;
                    if (v_playVoice != null) {
                        try {
                            AnimationDrawable animationDrawable = ((AnimationDrawable) v_playVoice
                                    .getBackground());
                            animationDrawable.stop();
                            animationDrawable.selectDrawable(0);
                            v_playVoice = null;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 结束播放音频
     */
    public static void stopPlayVoice() {
        if (player != null && player.isPlaying()) {
            player.release();
            player = null;
        }
        if (v_playVoice != null) {
            try {
                ((AnimationDrawable) v_playVoice.getBackground()).stop();
                ((AnimationDrawable) v_playVoice.getBackground())
                        .selectDrawable(0);
                v_playVoice = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 注意
            // ：此处try\catch用来修复BUG——播放语音时，滑动观看交流信息再点击语音会卡死。若是播放时，来回滑动过多，也有几率造成卡死。
        }
    }

}
