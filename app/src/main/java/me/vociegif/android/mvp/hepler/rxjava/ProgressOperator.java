package me.vociegif.android.mvp.hepler.rxjava;




import me.vociegif.android.mvp.hepler.ProgressBarHelper;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public class ProgressOperator<T extends Object>
        implements Observable.Operator<T, T> {

    @Override
    public Subscriber<? super T> call(Subscriber<? super T> subscriber) {
        return new Subscriber<T>() {
            @Override
            public void onCompleted() {
                if (!subscriber.isUnsubscribed()) {
                    ProgressBarHelper.getInstance(false).dismiss();
                    subscriber.onCompleted();
                }
            }

            @Override
            public void onError(Throwable e) {
                if (!subscriber.isUnsubscribed()) {
                    ProgressBarHelper.getInstance(false).dismiss();
                    subscriber.onError(e);
                }
            }

            @Override
            public void onNext(T data) {
                if (!subscriber.isUnsubscribed()) {

                    ProgressBarHelper.getInstance(false).dismiss();
                    subscriber.onNext(data);
                }
            }
        };
    }
}
