EnhancedFloaty
===============

一个支持拖曳、自动靠边、拖放调整大小、多窗口的悬浮窗。

效果图：

![screen-capture1](https://raw.githubusercontent.com/hyb1996/EnhancedFloaty/master/screen-captures/ss01.png)

### Dependency

#### Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:
```
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
```

#### Step 2. Add the dependency
```
    dependencies {
            compile 'com.github.hyb1996:EnhancedFloaty:0.13'
    }
```

### Usage

通过`context.startService(new Intent(context, FloatyService.class));`启动悬浮窗服务。 
通过`FloatyService.addWindow(window);`来添加悬浮窗。 
例如`FloatyService.addWindow(new ResizableFloatyWindow(new SampleFloaty()));`


更多用法参见Sample。

### License

MIT