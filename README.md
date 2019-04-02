# Android路由跳转
通过在Activity添加注解，生成路由表，支持配置拦截器，ActivityResult回调处理，链式调用
可解析路由地址中的参数 以String方式获取，支持全局拦截器，路由成功与否回调
# 配置

在根项目的build.gradle增加以下配置
```
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
在对应模块下的build.gradle中增加以下配置
```
apply plugin: 'kotlin-android'
apply plugin: 'kotlin-kapt'

dependencies {
    implementation 'com.github.Jiaoshichun.HRouter:HrouterApi:1.0'
    kapt 'com.github.Jiaoshichun.HRouter:HRouterComiler:1.0'
    }
```
# 初始化
在自定义的Application中的onCreate方法中调用以下方法
```
 HRouterConfiguration.get().init(this)//初始化方法
                .addRouterRuleCreator(new HRouterRuleCreatorImpl())//传入规则生成器
                .addGlobalHRouterCallBack(new HRouterCallBack())// //全局路由跳转成功失败回调 可选 可在错误方法中对于url已 http开头的跳转到固定的webview界面
                .addGlobalInterceptor(new HRouterInterceptor())//全局拦截器 可选

  
```
支持配置该模块跳转链接的BaseUrl以及生成HRouterRuleCreatorImpl对应的包名
```
@HRouterConfig(baseUrl = "hrouter://", pack = "com.shi.router")
public class MyApplication extends Application {
}
```
**注意：在多模块中使用时，必须为每一个模块配置不同包名生成各自模块的HRouterRuleCreatorImpl**

# 在Activity上添加注解
```
@HRouterInterceptors(Intercept::class)//配置拦截器  Intercept 需要实现HRouterInterceptor 接口
@HRouterRule("hrouter://mainActivity","hrouter://main") //一个activity可配置多个url 但是一个Url只能对应一个Activity
class MainActivity : BaseActivity(){} 
```
# 路由跳转
```
HRouter.create("hrouter://main").open(this)
```
# 处理ActivityResult
需在BaseActivity中增加以下配置
```
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ActivityResultDispatcher.getInstance().dispatchResult(this, requestCode, resultCode, data)
    }
```
```
 HRouter.create(""hrouter://main").setResultCallBack { requestCode, resultCode, data ->
                 //处理ActivityResult结果
               }.open(this)
```
# 权限处理
对需要权限的activity 可以在对应的activity中添加权限注解，在跳转前回进行权限申请 权限未申请通过则会跳转失败
```
@HRouterPermission(Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE, Permission.ACCESS_FINE_LOCATION,
    Permission.ACCESS_COARSE_LOCATION)
```
也可修改默认的权限过程 通过
```
HRouterConfiguration.get().setPermissionProcessor()
```
# 通过Action进行跳转
使用方式
```
            Router.createByAction(MediaStore.ACTION_IMAGE_CAPTURE)
                    .addPermission(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                    )
                    .addExtra(
                        MediaStore.EXTRA_OUTPUT, AndPermission.getFileUri(
                            this, File(Environment.getExternalStorageDirectory(), "hrouter.jpg")
                        )
                    )
                    .requestCode(110)
                    .open(this)
```

