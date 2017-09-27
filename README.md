# exciting-app
🎉 A speech-input-shake synthesis GIF small app.

Use Retrofit2 & RxJava, Dagger2 together with a Clean Architecture.

## Core frame
```
gif(packge)
    gif_vo(packge)
    ├──GifBean.java
    ├──GifMinutes.java
    helper(packge)
    ├──AnimatedGifEncoder.java
    ├──LZWEncoder.java
    ├──NeuQuant.java
├──GifMergeHelper.java
├──MergeFactory.java
├──MergeService.java
```
## Synthetic Usage
#### Step 1(Use java)
```java
static void encodeForJava(List<GifMinutes> pic, String newPic) {
        try {
            float progress;
            AnimatedGifEncoder e = new AnimatedGifEncoder();
            e.setTransparent(Color.TRANSPARENT);
            e.setRepeat(-1);
            e.start(newPic);
            for (int i = 0, len = pic.size(); i < len; i++) {
                e.setDelay(pic.get(i).getDelay());
                e.setPostX(pic.get(i).getPostX());
                e.setPostY(pic.get(i).getPostY());
                e.addFrame(pic.get(i).getBitmap());
                progress = 60 + 40f * i / len;
                EventBus.getDefault().post(new EventMergeProgress(progress));
            }
            e.finish();

            for (GifMinutes aPic : pic) {
                if (null != aPic.getBitmap() && !aPic.getBitmap().isRecycled())
                    aPic.getBitmap().recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
```

#### Step 2(Use C)
```java
    static {
        System.loadLibrary("gifflen");
    }
    /**
     * 初始化
     *
     * @param gifName    gif图片路径
     * @param w          生成Gif图片默认宽
     * @param h          生成Gif图片默认高
     * @param numColors  色彩,默认256
     * @param quality    图片质量,默认100
     * @param frameDelay 播放间隔
     * @return
     */
    public native int Init(String gifName, int w, int h, int numColors, int quality, int frameDelay);

    public native int AddFrame(int[] pixels);

    public native void Close();
    
    
    /**
     * 使用C语言合成Gif图片
     *
     * @param fileName 待合成Gif图片路径
     * @param bitmaps  图片集合
     * @param delay    Gif图片播放延迟时间
     * @return true 合成成功<br>
     * false 合成失败
     */
    public static boolean encode(String fileName, Bitmap[] bitmaps, int delay) {
        if (bitmaps == null || bitmaps.length == 0) {
            throw new NullPointerException("Bitmaps should have content!!!");
        }
        try {
            int width = bitmaps[0].getWidth();
            int height = bitmaps[0].getHeight();

            if (Init(fileName, width, height, 256, 100, delay) != 0) {
                Log.e(TAG, "GifUtil init failed");
                return false;
            }

            for (Bitmap bp : bitmaps) {
                int pixels[] = new int[width * height];
                bp.getPixels(pixels, 0, width, 0, 0, width, height);
                AddFrame(pixels);
            }
            Close();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Encode error", e);
        }
        return false;
    }

```

## About this project
以前做的一个小项目, 算是属于一个简单的工具类型的app。<br/>
可以使用语音、文字输入、或者直接摇一摇, 获取到的图片资源都是自动匹配的。<br/>
<br/>
里面主要的东西也就是gif的合成和直接静态图片生成分享, gif合成底层是用的java备选也有用c 具体可以直接看代码。<br/>
有朋友需要此类型需求的可以参考参考, 小小的把代码框架整理了一下, 开源出来仅做学习交流用处.

## Thanks to the open source project

* [RxJava](https://github.com/ReactiveX/RxJava)
* [RxAndroid](https://github.com/ReactiveX/RxAndroid)
* [RxLifecycle](https://github.com/trello/RxLifecycle)
* [okhttp](https://github.com/square/okhttp)
* [retrofit](https://github.com/square/retrofit)
* [android-gif-drawable](https://github.com/koral--/android-gif-drawable)
