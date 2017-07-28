package me.vociegif.android.event;

import me.vociegif.android.mvp.vo.VersionUpdateEntity;

import java.io.Serializable;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public class EventVersionUpdate implements Serializable {
    private VersionUpdateEntity versionResult;

    public EventVersionUpdate(VersionUpdateEntity versionResult) {
        this.versionResult = versionResult;
    }


    public VersionUpdateEntity getVersionResult() {
        return versionResult;
    }

}
