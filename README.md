# 控件版本引起闪退
epson眼镜为android4.0.4
textclock控件必须在android4.2以上版本使用，安装到epson眼镜上会闪退

# 控件布局太大超过眼镜的分辨率，也会引起闪退

# gradle plugin升级引起闪退
将gradle plugin从2.0.0升级到2.2.3，编译出的apk，在手机上运行是正常的，但在低版本的epson眼镜上运行会闪退
最终分析出：gradle2.2.0的编译没有兼容到安卓低较版本的覆盖安装，将gradle plugin版本改回2.0.0就行。

//TODO 测试