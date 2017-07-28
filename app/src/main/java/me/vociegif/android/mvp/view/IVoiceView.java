package me.vociegif.android.mvp.view;

import me.vociegif.android.mvp.AbsView;
import me.vociegif.android.mvp.vo.StickerAdapt2Result;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public interface IVoiceView extends AbsView {
    void beginMergeService();
    void showMergingDialog();
    void bindVoiceData(StickerAdapt2Result stickerAdapt2Result);
}
