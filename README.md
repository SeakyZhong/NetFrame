# NetFrame
【Android】
一款轻量级，非侵入式，可高度自由扩展订制任意功能的http请求全自动托管框架。

基于android X + Retrofit2 + okhttp3 + Rxjava2  
如果你的项目是分模块的  
可以直接把netframe拷贝到网络模块中   
或者就用它作为一个module   
如果你的项目是组件化的  
把netframe集成到基础组件或者网络组件里  
如果你的项目还是原始类型  
netframe作为一个单独的package就行  
框架说明和用法，都在代码注释里  
至于发生在网络请求过程中涉及的线程管理切换  
生命周期同步，销毁取消请求，订阅解绑之类的事情  
框架全都封装好了  
导入到自己项目后  
关注HttpResponse和ServerResultFunction两个类  
根据自己的后台接口格式  
改一下状态码和返回数据的格式即可  
  
用过的都说好....

