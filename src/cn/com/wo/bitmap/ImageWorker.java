/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.com.wo.bitmap;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.ImageView;

/**
 * This class wraps up completing some arbitrary long running work when loading a bitmap to an
 * ImageView. It handles things like using a memory and disk cache, running the work in a background
 * thread and setting a placeholder image.
 */
public abstract class ImageWorker {
//    private static final String TAG = "ImageWorker";
    private static final int FADE_IN_TIME = 200;

    private ImageCache mImageCache;
    private ImageCache.ImageCacheParams mImageCacheParams;
    private Bitmap mLoadingBitmap[];
    private boolean mFadeInBitmap = true;
    private boolean mExitTasksEarly = false;
    protected boolean mPauseWork = false;
    private final Object mPauseWorkLock = new Object();

    protected Resources mResources;
    
    private static final int MESSAGE_CLEAR = 0;
    private static final int MESSAGE_INIT_DISK_CACHE = 1;
    private static final int MESSAGE_FLUSH = 2;
    private static final int MESSAGE_CLOSE = 3;

    protected ImageWorker(Context context) {
        mResources = context.getResources();
    }

    /**
     * Load an image specified by the data parameter into an ImageView (override
     * {@link ImageWorker#processBitmap(Object)} to define the processing logic). A memory and
     * disk cache will be used if an {@link ImageCache} has been added using
     * {@link ImageWorker#addImageCache(FragmentManager, ImageCache.ImageCacheParams)}. If the
     * image is found in the memory cache, it is set immediately, otherwise an {@link AsyncTask}
     * will be created to asynchronously load the bitmap.
     *
     * @param data The URL of the image to download.
     * @param imageView The ImageView to bind the downloaded image to.
     */
    public void loadImage(String url, ImageView imageView) {
    	if(url == null || url.trim().length() == 0 || !url.startsWith("http"))
    		return ;
    	loadImage(url, imageView, false);
    }
    
    public void loadImage(String url, ImageView imageView, boolean isBackground) {
//      url = Images.getUrl();
    	if(url == null || url.trim().length() == 0 || !url.startsWith("http"))
    		return ;
      if(ImageFetcher.isDebug)
      	Log.i(ImageFetcher.TAG, "loadImage " + url + "; isBg=" + isBackground);
//    url = "http://58.254.132.169/picture/90755000/copyright/singer/2012032806/23280.jpg";

      BitmapDrawable value = null;

      if (mImageCache != null) {
          value = mImageCache.getBitmapFromMemCache(url);
      }

      if (value != null) {
          // Bitmap found in memory cache
    	  if(isBackground)
    		  imageView.setBackgroundDrawable(value);
    	  else
    		  imageView.setImageDrawable(value);
      } else if (cancelPotentialWork(url, imageView, isBackground)) {
      	imageView.setTag(0);
          final BitmapWorkerTask task = new BitmapWorkerTask(imageView, 0, isBackground);
          final AsyncDrawable asyncDrawable =
                  new AsyncDrawable(mResources, mLoadingBitmap[0], task);
//          imageView.setImageDrawable(asyncDrawable);
          
          if(isBackground)
    		  imageView.setBackgroundDrawable(asyncDrawable);
    	  else
    		  imageView.setImageDrawable(asyncDrawable);

          // NOTE: This uses a custom version of AsyncTask that has been pulled from the
          // framework and slightly modified. Refer to the docs at the top of the class
          // for more info on what was changed.
          task.executeOnExecutor(AsyncTask.DUAL_THREAD_EXECUTOR, url);
      }
  }
    
    /**
     * Load an image specified by the data parameter into an ImageView (override
     * {@link ImageWorker#processBitmap(Object)} to define the processing logic). A memory and
     * disk cache will be used if an {@link ImageCache} has been added using
     * {@link ImageWorker#addImageCache(FragmentManager, ImageCache.ImageCacheParams)}. If the
     * image is found in the memory cache, it is set immediately, otherwise an {@link AsyncTask}
     * will be created to asynchronously load the bitmap.
     *
     * @param data The URL of the image to download.
     * @param imageView The ImageView to bind the downloaded image to.
     */
    public void loadImageByIndex(String url, ImageView imageView, int loadingIndex) {
    	if(url == null || url.trim().length() == 0 || !url.startsWith("http"))
    		return ;
    	loadImageByIndex(url, imageView, loadingIndex, false);
    }
    
    public void loadImageByIndex(String url, ImageView imageView, 
    		int loadingIndex, boolean isBackground) {
//      url = Images.getUrl();
    	if(url == null || url.trim().length() == 0 || !url.startsWith("http"))
    		return ;
    	if(ImageFetcher.isDebug)
          	Log.i(ImageFetcher.TAG, "loadImage " + url + "; isBg=" + isBackground + "; " + (imageView == null));
//    url = "http://58.254.132.169/picture/90755000/copyright/singer/2012032806/23280.jpg";

      BitmapDrawable value = null;

      if (mImageCache != null) {
          value = mImageCache.getBitmapFromMemCache(url);
      }

      if (value != null) {
          // Bitmap found in memory cache
    	  if(ImageFetcher.isDebug)
            	Log.i(ImageFetcher.TAG, "value is null ?  " + (value == null));
    	  if(isBackground)
    	  {
    		  imageView.setBackgroundDrawable(null);
    		  imageView.setBackgroundDrawable(value);
    	  }
    	  else
    		  imageView.setImageDrawable(value);
      } else if (cancelPotentialWork(url, imageView, isBackground)) {
    	  if(ImageFetcher.isDebug)
          	Log.i(ImageFetcher.TAG, "value is null ?  " + (value == null));
      	  imageView.setTag(loadingIndex);
          final BitmapWorkerTask task = new BitmapWorkerTask(imageView, 0, isBackground);
          final AsyncDrawable asyncDrawable =
                  new AsyncDrawable(mResources, mLoadingBitmap[loadingIndex], task);
//          imageView.setImageDrawable(asyncDrawable);
          if(isBackground)
          {
        	  imageView.setBackgroundDrawable(null);
        	  imageView.setBackgroundDrawable(asyncDrawable);
          }
    	  else
    		  imageView.setImageDrawable(asyncDrawable);

          // NOTE: This uses a custom version of AsyncTask that has been pulled from the
          // framework and slightly modified. Refer to the docs at the top of the class
          // for more info on what was changed.
          task.executeOnExecutor(AsyncTask.DUAL_THREAD_EXECUTOR, url);
      }
  }
    
    /**
     * Load an image specified by the data parameter into an ImageView (override
     * {@link ImageWorker#processBitmap(Object)} to define the processing logic). A memory and
     * disk cache will be used if an {@link ImageCache} has been added using
     * {@link ImageWorker#addImageCache(FragmentManager, ImageCache.ImageCacheParams)}. If the
     * image is found in the memory cache, it is set immediately, otherwise an {@link AsyncTask}
     * will be created to asynchronously load the bitmap.
     *
     * @param data The URL of the image to download.
     * @param imageView The ImageView to bind the downloaded image to.
     */
    public void loadImage(String url, ImageView imageView, int radian) {
    	if(url == null || url.trim().length() == 0 || !url.startsWith("http"))
    		return ;
    	loadImage(url, imageView, radian, false);
    }
    
    public void loadImage(String url, ImageView imageView, 
    		int radian, boolean isBackground) {
    	if(url == null || url.trim().length() == 0 || !url.startsWith("http"))
    		return ;
    	BitmapDrawable value = null;

        if(ImageFetcher.isDebug)
          	Log.i(ImageFetcher.TAG, "loadImage " + url + "; isBg=" + isBackground);
        
        if (mImageCache != null) {
            value = mImageCache.getBitmapFromMemCache(String.valueOf(url));
        }

        if (value != null) {
            // Bitmap found in memory cache
//            imageView.setImageDrawable(value);
        	if(isBackground)
        	{
        		imageView.setBackgroundDrawable(null);
        		imageView.setBackgroundDrawable(value);
        	}
      	  else
      		  imageView.setImageDrawable(value);
        } else if (cancelPotentialWork(url, imageView, isBackground)) {
        	imageView.setTag(0);
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView, radian, isBackground);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(mResources, mLoadingBitmap[0], task);
//            imageView.setImageDrawable(asyncDrawable);
            if(isBackground)
            {
            	imageView.setBackgroundDrawable(null);
            	imageView.setBackgroundDrawable(asyncDrawable);
            }
      	  	else
      		  imageView.setImageDrawable(asyncDrawable);

            // NOTE: This uses a custom version of AsyncTask that has been pulled from the
            // framework and slightly modified. Refer to the docs at the top of the class
            // for more info on what was changed.
            task.executeOnExecutor(AsyncTask.DUAL_THREAD_EXECUTOR, url);
        }
    }

    /**
     * Set placeholder bitmap that shows when the the background thread is running.
     *
     * @param bitmap
     */
    public void setLoadingImage(Bitmap... bitmap) {
        mLoadingBitmap = bitmap;
    }

    /**
     * Set placeholder bitmap that shows when the the background thread is running.
     *
     * @param resId
     */
//    public void setLoadingImage(int resId) {
//        mLoadingBitmap = BitmapFactory.decodeResource(mResources, resId);
//    }

    /**
     * Adds an {@link ImageCache} to this {@link ImageWorker} to handle disk and memory bitmap
     * caching.
     * @param fragmentManager
     * @param cacheParams The cache parameters to use for the image cache.
     */
    public void addImageCache(FragmentManager fragmentManager,
            ImageCache.ImageCacheParams cacheParams) {
        mImageCacheParams = cacheParams;
        mImageCache = ImageCache.getInstance(fragmentManager, mImageCacheParams);
        new CacheAsyncTask().execute(MESSAGE_INIT_DISK_CACHE);
    }
    
    /**
     * Adds an {@link ImageCache} to this {@link ImageWorker} to handle disk and memory bitmap
     * caching.
     * @param fragmentManager
     * @param cacheParams The cache parameters to use for the image cache.
     */
    public void addImageCache(ImageCache.ImageCacheParams cacheParams) {
        mImageCacheParams = cacheParams;
        mImageCache = ImageCache.getInstance(mImageCacheParams);
        new CacheAsyncTask().execute(MESSAGE_INIT_DISK_CACHE);
    }

    /**
     * Adds an {@link ImageCache} to this {@link ImageWorker} to handle disk and memory bitmap
     * caching.
     * @param activity
     * @param diskCacheDirectoryName See
     * {@link ImageCache.ImageCacheParams#ImageCacheParams(Context, String)}.
     */
    public void addImageCache(FragmentActivity activity, String diskCacheDirectoryName) {
        mImageCacheParams = new ImageCache.ImageCacheParams(activity, diskCacheDirectoryName);
        mImageCache = ImageCache.getInstance(activity.getSupportFragmentManager(), mImageCacheParams);
        new CacheAsyncTask().execute(MESSAGE_INIT_DISK_CACHE);
    }

    /**
     * If set to true, the image will fade-in once it has been loaded by the background thread.
     */
    public void setImageFadeIn(boolean fadeIn) {
        mFadeInBitmap = fadeIn;
    }

    public void setExitTasksEarly(boolean exitTasksEarly) {
        mExitTasksEarly = exitTasksEarly;
        setPauseWork(false);
    }

    /**
     * Subclasses should override this to define any processing or work that must happen to produce
     * the final bitmap. This will be executed in a background thread and be long running. For
     * example, you could resize a large bitmap here, or pull down an image from the network.
     *
     * @param data The data to identify which image to process, as provided by
     *            {@link ImageWorker#loadImage(Object, ImageView)}
     * @return The processed bitmap
     */
    protected abstract Bitmap processBitmap(Object data);

    /**
     * @return The {@link ImageCache} object currently being used by this ImageWorker.
     */
    public ImageCache getImageCache() {
        return mImageCache;
    }

    /**
     * Cancels any pending work attached to the provided ImageView.
     * @param imageView
     */
    public static void cancelWork(ImageView imageView, boolean isBackground) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView, isBackground);
        if (bitmapWorkerTask != null) {
            bitmapWorkerTask.cancel(true);
            if(ImageFetcher.isDebug) {
                final Object bitmapData = bitmapWorkerTask.data;
                Log.d(ImageFetcher.TAG, "ImageWorker cancelWork - cancelled work for " + bitmapData);
            }
        }
    }

    /**
     * Returns true if the current work has been canceled or if there was no work in
     * progress on this image view.
     * Returns false if the work in progress deals with the same data. The work is not
     * stopped in that case.
     */
    public static boolean cancelPotentialWork(Object data, ImageView imageView, boolean isBackground) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView, isBackground);

        if (bitmapWorkerTask != null) {
            final Object bitmapData = bitmapWorkerTask.data;
            if (bitmapData == null || !bitmapData.equals(data)) {
                bitmapWorkerTask.cancel(true);
                if(ImageFetcher.isDebug)
                	Log.d(ImageFetcher.TAG, "ImageWorker cancelPotentialWork - cancelled work for " + data);
            } else {
                // The same work is already in progress.
                return false;
            }
        }
        return true;
    }

    /**
     * @param imageView Any imageView
     * @return Retrieve the currently active work task (if any) associated with this imageView.
     * null if there is no such task.
     */
    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView, boolean isBackground) {
        if (imageView != null) {
            Drawable drawable = null;
            if(isBackground)
            	drawable = imageView.getBackground();
            else
            	drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    /**
     * The actual AsyncTask that will asynchronously process the image.
     */
    private class BitmapWorkerTask extends AsyncTask<Object, Void, BitmapDrawable> {
        private Object data;
        private int radian;
        private boolean isBackground;
        private final WeakReference<ImageView> imageViewReference;

        public BitmapWorkerTask(ImageView imageView, int radian, boolean isBackground) {
            imageViewReference = new WeakReference<ImageView>(imageView);
            this.radian = radian;
            this.isBackground = isBackground;
        }

        /**
         * Background processing.
         */
        @Override
        protected BitmapDrawable doInBackground(Object... params) {
        	if(ImageFetcher.isDebug)
            	Log.d(ImageFetcher.TAG, "ImageWorker doInBackground - starting work");

            data = params[0];
            final String dataString = String.valueOf(data);
            Bitmap bitmap = null;
            BitmapDrawable drawable = null;

            // Wait here if work is paused and the task is not cancelled
            synchronized (mPauseWorkLock) {
                while (mPauseWork && !isCancelled()) {
                    try {
                        mPauseWorkLock.wait();
                    } catch (InterruptedException e) {}
                }
            }

            // If the image cache is available and this task has not been cancelled by another
            // thread and the ImageView that was originally bound to this task is still bound back
            // to this task and our "exit early" flag is not set then try and fetch the bitmap from
            // the cache
            if (mImageCache != null && !isCancelled() && getAttachedImageView(isBackground) != null
                    && !mExitTasksEarly) {
                bitmap = mImageCache.getBitmapFromDiskCache(dataString);
            }

            // If the bitmap was not found in the cache and this task has not been cancelled by
            // another thread and the ImageView that was originally bound to this task is still
            // bound back to this task and our "exit early" flag is not set, then call the main
            // process method (as implemented by a subclass)
            if (bitmap == null && !isCancelled() && getAttachedImageView(isBackground) != null
                    && !mExitTasksEarly) {
                bitmap = processBitmap(params[0]);
            }

            // If the bitmap was processed and the image cache is available, then add the processed
            // bitmap to the cache for future use. Note we don't check if the task was cancelled
            // here, if it was, and the thread is still running, we may as well add the processed
            // bitmap to our cache as it might be used again in the future
            if (bitmap != null) {
            	if(radian > 0) {
            		bitmap = ImageFetcher.getStretchRoundBitmap(bitmap, radian);
            	}
                if (Utils.hasHoneycomb()) {
                    // Running on Honeycomb or newer, so wrap in a standard BitmapDrawable
                    drawable = new BitmapDrawable(mResources, bitmap);
                } else {
                    // Running on Gingerbread or older, so wrap in a RecyclingBitmapDrawable
                    // which will recycle automagically
                    drawable = new RecyclingBitmapDrawable(mResources, bitmap);
                }

                if (mImageCache != null) {
                	try {
                		mImageCache.addBitmapToCache(dataString, drawable);
                	}
                	catch(Exception e) {
                		if(ImageFetcher.isDebug)
                			Log.e(ImageFetcher.TAG, e.getMessage());
                		mImageCache.flush();
                		mImageCache.clearMemoryCache();
                	}
                }
            }

            if(ImageFetcher.isDebug)
            	Log.d(ImageFetcher.TAG, "ImageWorker doInBackground - finished work");

            return drawable;
        }

        /**
         * Once the image is processed, associates it to the imageView
         */
        @Override
        protected void onPostExecute(BitmapDrawable value) {
            // if cancel was called on this task or the "exit early" flag is set then we're done
            if (isCancelled() || mExitTasksEarly) {
            	if(ImageFetcher.isDebug)
                	Log.d(ImageFetcher.TAG, "onPostExecute 1 " + data);
                value = null;
            }

            final ImageView imageView = getAttachedImageView(isBackground);
            if (value != null && imageView != null) {
            	if(ImageFetcher.isDebug)
                	Log.d(ImageFetcher.TAG, "ImageWorker onPostExecute - setting bitmap" + data);
                setImageDrawable(imageView, value, isBackground);
            }
            else {
            	if(ImageFetcher.isDebug)
                	Log.d(ImageFetcher.TAG, "onPostExecute 2 " + (value==null) + "|" + (imageView == null) + "; " + data);
            }
        }

        @Override
        protected void onCancelled(BitmapDrawable value) {
            super.onCancelled(value);
            synchronized (mPauseWorkLock) {
                mPauseWorkLock.notifyAll();
            }
        }

        /**
         * Returns the ImageView associated with this task as long as the ImageView's task still
         * points to this task as well. Returns null otherwise.
         */
        private ImageView getAttachedImageView(boolean isBackground) {
            final ImageView imageView = imageViewReference.get();
            final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView, isBackground);

            if (this == bitmapWorkerTask) {
                return imageView;
            }

            return null;
        }
    }

    /**
     * A custom Drawable that will be attached to the imageView while the work is in progress.
     * Contains a reference to the actual worker task, so that it can be stopped if a new binding is
     * required, and makes sure that only the last started worker process can bind its result,
     * independently of the finish order.
     */
    private static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    /**
     * Called when the processing is complete and the final drawable should be 
     * set on the ImageView.
     *
     * @param imageView
     * @param drawable
     */
    private void setImageDrawable(ImageView imageView, Drawable drawable, 
    		boolean isBackground) {
        if (mFadeInBitmap && !isBackground) {
            // Transition drawable with a transparent drawable and the final drawable
            final TransitionDrawable td =
                    new TransitionDrawable(new Drawable[] {
                            new ColorDrawable(android.R.color.transparent),
                            drawable
                    });
            // Set background to loading bitmap
            int index = 0;
            if(imageView.getTag() != null)
            	index = (Integer) imageView.getTag();
            imageView.setBackgroundDrawable(
                    new BitmapDrawable(mResources, mLoadingBitmap[index]));
            imageView.setImageDrawable(td);
            td.startTransition(FADE_IN_TIME);
            if(ImageFetcher.isDebug)
    			Log.i(ImageFetcher.TAG, "setImageDrawable src");
        } else {
        	if(isBackground)
        	{
        		if(ImageFetcher.isDebug)
        			Log.i(ImageFetcher.TAG, "setImageDrawable isBackground");
        		imageView.setBackgroundDrawable(null);
        		imageView.setBackgroundDrawable(drawable);
        	}
        	else
        	{
        		if(ImageFetcher.isDebug)
        			Log.i(ImageFetcher.TAG, "setImageDrawable src");
        		imageView.setImageDrawable(drawable);
        	}
        }
    }

    /**
     * Pause any ongoing background work. This can be used as a temporary
     * measure to improve performance. For example background work could
     * be paused when a ListView or GridView is being scrolled using a
     * {@link android.widget.AbsListView.OnScrollListener} to keep
     * scrolling smooth.
     * <p>
     * If work is paused, be sure setPauseWork(false) is called again
     * before your fragment or activity is destroyed (for example during
     * {@link android.app.Activity#onPause()}), or there is a risk the
     * background thread will never finish.
     */
    public void setPauseWork(boolean pauseWork) {
        synchronized (mPauseWorkLock) {
            mPauseWork = pauseWork;
            if (!mPauseWork) {
                mPauseWorkLock.notifyAll();
            }
        }
    }

    protected class CacheAsyncTask extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... params) {
            switch ((Integer)params[0]) {
                case MESSAGE_CLEAR:
                    clearCacheInternal();
                    break;
                case MESSAGE_INIT_DISK_CACHE:
                    initDiskCacheInternal();
                    break;
                case MESSAGE_FLUSH:
                    flushCacheInternal();
                    break;
                case MESSAGE_CLOSE:
                    closeCacheInternal();
                    break;
            }
            return null;
        }
    }

    protected void initDiskCacheInternal() {
        if (mImageCache != null) {
            mImageCache.initDiskCache();
        }
    }

    protected void clearCacheInternal() {
        if (mImageCache != null) {
            mImageCache.clearCache();
        }
    }

    protected void flushCacheInternal() {
        if (mImageCache != null) {
            mImageCache.flush();
        }
    }

    protected void closeCacheInternal() {
        if (mImageCache != null) {
            mImageCache.close();
            mImageCache = null;
        }
    }

    public void clearCache() {
        new CacheAsyncTask().execute(MESSAGE_CLEAR);
    }

    public void flushCache() {
        new CacheAsyncTask().execute(MESSAGE_FLUSH);
    }

    public void closeCache() {
        new CacheAsyncTask().execute(MESSAGE_CLOSE);
    }
}
