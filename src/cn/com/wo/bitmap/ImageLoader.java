package cn.com.wo.bitmap;

import android.widget.ImageView;

/**
 * ͼƬ������
 * 
 * @author xiaoxh
 */
public class ImageLoader {

	/**
	 * ����50x50��С��ͼƬ��ͨ��urlƴ�ӣ���Ҫ���ڼ�������ͬ�������ĸ�����ר����ͼƬ��
	 * 
	 * @param imageFetcher
	 * @param url
	 * @param iv
	 */
	public static void loadIn50(ImageFetcher imageFetcher, String url, ImageView iv) {
		loadIn50(imageFetcher, url, iv, 0);
	}
	
	/**
	 * ����50x50��С��ͼƬ��ͨ��urlƴ�ӣ���Ҫ���ڼ�������ͬ�������ĸ�����ר����ͼƬ��
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
	 * ����100x100��С��ͼƬ��ͨ��urlƴ�ӣ���Ҫ���ڼ�������ͬ�������ĸ�����ר����ͼƬ��
	 * 
	 * @param imageFetcher
	 * @param url
	 * @param iv
	 */
	public static void loadIn100(ImageFetcher imageFetcher, String url, ImageView iv) {
		loadIn100(imageFetcher, url, iv, 0);
	}
	
	/**
	 * ����100x100��С��ͼƬ��ͨ��urlƴ�ӣ���Ҫ���ڼ�������ͬ�������ĸ�����ר����ͼƬ��
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
	 * �����Ż������ͼƬ��
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
	 * �����Ż������ͼƬ��
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
	 * ͼƬURL�Ƿ���Ч
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
