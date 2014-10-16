BitmapFun
=========

介绍
----------
在Google BitmapFun源码的基础上修改而来，主要在以下几个方面：<br />
1、当图片需要缓存在不同的文件夹下时，无法缓存的问题；<br />
2、在手机应用管理中清除缓存数据后，使用报错的问题；<br />
3、增加支持ImageView backgroud属性；<br />
4、增加支持对图片进行圆角处理；<br />
5、一个ImageFetcher实例，支持设置多个加载中的图片；<br />

初始化ImageFetcher
----------
ImageCacheParams cacheParams = new ImageCacheParams(this, "product");<br />
// Set memory cache to 10% of mem class<br />
cacheParams.setMemCacheSizePercent(0.20f);<br />
cacheParams.compressQuality = 90;<br />
// The ImageFetcher takes care of loading images into our ImageView<br />
// children asynchronously<br />
imageFetcher = new ImageFetcher(this, 0);<br />
imageFetcher.setLoadingImage(loadingBmp1, loadingBmp2); //这里是数组参数，可设置多个<br />
imageFetcher.addImageCache(cacheParams);<br />
