EnhancedFloaty
===============

一个支持拖曳、自动靠边、拖放调整大小的悬浮窗。

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
            compile 'com.github.hyb1996:node-android-lib:1.0.14'
    }
```

### Usage

通过ResizableFloatyService的startService方法启动悬浮窗，并提供ViewSupplier来指定悬浮窗的View。为了保证ViewSupplier的可序列化，ViewSupplier必须是静态内部类或静态代码块的匿名类。
```
private static void startService(Context ctx) {
    ResizableFloatyService.startService(ctx, new ResizableFloatyService.ViewSupplier() {
        @Override
        public View inflateCollapsedView(Context context) {
            return View.inflate(context, R.layout.floating_window_collapsed, null);
        }

        @Override
        public View inflateExpandedView(Context context) {
            return View.inflate(context, R.layout.floating_window_expanded, null);
        }

        @Override
        public View getResizerView(View expandedView) {
            return expandedView.findViewById(R.id.resizer);
        }
    });
}
```

### License

MIT