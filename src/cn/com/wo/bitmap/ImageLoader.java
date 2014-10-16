package cn.com.wo.bitmap;

import android.widget.ImageView;

/**
 * 图片加载器
 * 
 * @author xiaoxh
 */
public class ImageLoader {

	/**
	 * 加载50x50大小的图片，通过url拼接，主要用于加载曲库同步过来的歌曲、专辑等图片。
	 * 
	 * @param imageFetcher
	 * @param url
	 * @param iv
	 */
	public static void loadIn50(ImageFetcher imageFetcher, String url, ImageView iv) {
		loadIn50(imageFetcher, url, iv, 0);
	}
	
	/**
	 * 加载50x50大小的图片，通过url拼接，主要用于加载曲库同步过来的歌曲、专辑等图片。
	 * 
	 * @param imageFetcher
	 * @param url
	 * @param iv
	 */
	public static void loadIn50(ImageFetcher imageFetcher, String url, ImageView iv, int loadingImageIndex) {
		if(!isValidUrl(url))
			return ;
        
		String urls[] = url.split("\\/");
		int length = urls.length;
		int last = length - 1;
		StringBuffer sb = new StringBuffer(length + 8);
		for(int i = 0; i < length; i++) {
			if(i < last)
				sb.append(urls[i]).append("/");
			else 
				sb.append("50/").append(urls[i]);
		}
//		imageFetcher.loadImage(sb.toString(), iv);
		
		if(loadingImageIndex == 0)
			imageFetcher.loadImage(sb.toString(), iv);
		else
			imageFetcher.loadImageByIndex(sb.toString(), iv, loadingImageIndex);
	}
	
	/**
	 * 加载100x100大小的图片，通过url拼接，主要用于加载曲库同步过来的歌曲、专辑等图片。
	 * 
	 * @param imageFetcher
	 * @param url
	 * @param iv
	 */
	public static void loadIn100(ImageFetcher imageFetcher, String url, ImageView iv) {
		loadIn100(imageFetcher, url, iv, 0);
	}
	
	/**
	 * 加载100x100大小的图片，通过url拼接，主要用于加载曲库同步过来的歌曲、专辑等图片。
	 * 
	 * @param imageFetcher
	 * @param url
	 * @param iv
	 */
	public static void loadIn100(ImageFetcher imageFetcher, String url, ImageView iv, int loadingImageIndex) {
		if(!isValidUrl(url))
			return ;
        
		String urls[] = url.split("\\/");
		int length = urls.length;
		int last = length - 1;
		StringBuffer sb = new StringBuffer(length + 8);
		for(int i = 0; i < length; i++) {
			if(i < last)
				sb.append(urls[i]).append("/");
			else 
				sb.append("100/").append(urls[i]);
		}
		if(loadingImageIndex == 0)
			imageFetcher.loadImage(sb.toString(), iv);
		else
			imageFetcher.loadImageByIndex(sb.toString(), iv, loadingImageIndex);
	}
	
	/**
	 * 加载门户管理的图片。
	 * 
	 * @param imageFetcher
	 * @param url
	 * @param iv
	 */
	public static void load(ImageFetcher imageFetcher, String url, ImageView iv) {
		if(!isValidUrl(url))
			return ;
		imageFetcher.loadImage(url, iv);
	}
	
	/**
	 * 加载门户管理的图片。
	 * 
	 * @param imageFetcher
	 * @param url
	 * @param iv
	 */
	public static void load(ImageFetcher imageFetcher, String url, ImageView iv, int loadingImageIndex) {
		if(!isValidUrl(url))
			return ;
		imageFetcher.loadImageByIndex(url, iv, loadingImageIndex);
	}
	
	/**
	 * 图片URL是否有效
	 * 
	 * @param url
	 * @return
	 */
	private static boolean isValidUrl(String url) {
		if (url == null ) {
            return false;
        }
        if("".equals(url) || !url.startsWith("http"))
        	return false;
        return true;
	}
}
