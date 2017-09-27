# exciting-app
🎉 A speech-input-shake synthesis GIF small app.

Use Retrofit2 & RxJava, Dagger2 together with a Clean Architecture.

## Core frame
```
gif(packge)
  gif_vo
    GifBean
    GifMinutes
  helper
    AnimatedGifEncoder
    LZWEncoder
    NeuQuant
  GifMergeHelper
  MergeFactory
  MergeService
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
