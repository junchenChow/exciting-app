package me.vociegif.android.helper.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import me.vociegif.android.App;
import me.vociegif.android.helper.ConstantsPath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@SuppressLint("CommitPrefEdits")
public class FileUtils_zm {

    /**
     * 缓冲区
     */
    public static final int BUFF_SIZE = 1024;
    public static final String IMG = ".jpg";
    private static String BASE_PATH;
    private static String STICKER_BASE_PATH;
    private static FileUtils_zm mInstance;

    private FileUtils_zm() {
        String sdcardState = Environment.getExternalStorageState();
        // 如果没SD卡则放缓存
        if (Environment.MEDIA_MOUNTED.equals(sdcardState)) {
            BASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/moho/";
        } else {
            BASE_PATH = App.getDefault().getCacheDirPath();
        }

        STICKER_BASE_PATH = BASE_PATH + "/stickers/";
    }

    public static FileUtils_zm getInst() {
        if (mInstance == null) {
            synchronized (FileUtils_zm.class) {
                if (mInstance == null) {
                    mInstance = new FileUtils_zm();
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取sd卡路径
     *
     * @return
     */
    public static String getSdPath() {
        String file_dir = null;
        // SD卡是否存在
        boolean isSDCardExist = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        boolean isRootDirExist = Environment.getExternalStorageDirectory().exists();
        if (isSDCardExist && isRootDirExist) {
            file_dir = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return file_dir;
    }

    /**
     * 检查指定用户是否收藏了指定贴纸系列
     *
     * @param favorite
     * @param id
     * @return
     */
    public static boolean checkUserLike(List<Integer> favorite, int id) {
        if (favorite == null)
            return false;

        for (int item : favorite) {
            if (item == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * 更新贴纸ID序列
     */
    public static void updateStickerIdList() {

    }

//    public static void CheckStickerExist(Context context, StickerContentFull param,
//                                         final StickerDownCallbackListener listener) {
//        String serverpath = Constants.setAliyunImageUrl(param.getResource_url());
//        String filename = Utils_zm.urlToName(param.getResource_url());
//        String localpath = ConstantsPath.getMhCachePath() + String.valueOf(filename.hashCode());
//        // Logger.d("file path is:" + serverpath + " " + localpath);
//        File file = new File(localpath);
//        if (file.exists()) {
//            // 文件已存在
//            listener.fileHasExist(param, localpath);
//            return;
//        }
//        HttpUtils http = new HttpUtils();
//        http.download(serverpath, localpath + "temp", false// 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
//                , false// 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
//                , new RequestCallBack<File>() {
//
//                    @Override
//                    public void onFailure(HttpException arg0, String arg1) {
//                        listener.onFailure(arg0, arg1);
//                    }
//
//                    @Override
//                    public void onSuccess(ResponseInfo<File> arg0) {
//                        listener.onSuccess(arg0);
//                    }
//
//                    @Override
//                    public void onLoading(long total, long current, boolean isUploading) {
//                        super.onLoading(total, current, isUploading);
//                        listener.onLoading(total, current, isUploading);
//                    }
//
//                    @Override
//                    public void onStart() {
//                        super.onStart();
//                        listener.onStart();
//                    }
//
//                    @Override
//                    public void onCancelled() {
//                        super.onCancelled();
//                        listener.onCancelled();
//                    }
//
//                });
//    }

    @SuppressLint("SimpleDateFormat")
    public static String getRandomName() {
        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHms");
        return format.format(c1.getTime());
    }

    public static String getFilePath(String file) {
        return ConstantsPath.getCachePath() + '/' + String.valueOf(file.hashCode());
    }

//    /**
//     * 下载Mh文件
//     *
//     * @param context
//     * @param entity
//     * @param listener
//     */
//    public static void LoadMhFile(Context context, MhEntity entity, final MhDownCallbackListener listener) {
//        FileUtils_zm.entity = entity;
//        String serverpath = Constants.setAliyunImageUrl(entity.getFilename_url());
//
//        String filename = Utils_zm.urlToName(entity.getFilename_url());
//
//        String localpath = ConstantsPath.getMhCachePath() + String.valueOf(filename.hashCode());
//        File file = new File(localpath);
//        if (file.exists()) {
//            // 文件已存在
//            listener.fileHasExist(entity, localpath);
//            return;
//        }
//
//        // 创建文件夾
//        File directoryFile = new File(ConstantsPath.getMhCachePath());
//        if (!directoryFile.exists()) {
//            directoryFile.mkdirs();
//        }
//
//        HttpUtils http = new HttpUtils();
//        http.download(serverpath, localpath + "temp", true, true, new RequestCallBack<File>() {
//
//            @Override
//            public void onFailure(HttpException arg0, String arg1) {
//                listener.onFailure(arg0, arg1);
//            }
//
//            @Override
//            public void onSuccess(ResponseInfo<File> arg0) {
//                listener.onSuccess(arg0);
//            }
//
//            @Override
//            public void onLoading(long total, long current, boolean isUploading) {
//                super.onLoading(total, current, isUploading);
//                listener.onLoading(total, current, isUploading);
//            }
//
//            @Override
//            public void onStart() {
//                super.onStart();
//                listener.onStart();
//            }
//
//            @Override
//            public void onCancelled() {
//                super.onCancelled();
//                listener.onCancelled();
//            }
//
//        });
//    }

    /**
     * 获取mh文件信息
     *
     * @param jsonpath
     * @return
     */
//    public static StickerContent getMhFileInfor(String jsonpath) {
//        StickerContent content = null;
//        FileInputStream inputStream = null;
//        if (new File(jsonpath).exists()) {
//            try {
//                inputStream = new FileInputStream(jsonpath);
//                StringBuilder builder = new StringBuilder();
//                byte[] buffer = new byte[1024 * 1024];
//                int length = -1;
//                while ((length = inputStream.read(buffer, 0, buffer.length)) != -1) {
//                    builder.append(new String(buffer, 0, length));
//                }
//                Gson gson = new Gson();
//                content = gson.fromJson(builder.toString(), StickerContent.class);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } finally {
//                if (inputStream != null)
//                    try {
//                        inputStream.close();
//                        inputStream = null;
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//            }
//
//        }
//        return content;
//    }

    /**
     * 回收位图
     *
     * @param footerBitmap
     */
    public static void recyleBitmap(Bitmap footerBitmap, boolean bGC) {
        if (footerBitmap == null)
            return;
        footerBitmap.recycle();
        footerBitmap = null;
        if (bGC)
            System.gc();
    }
    /*
	public String getPhotoSavedPath() {
		return BASE_PATH + "moho";
	}
	
	public String getBasePath() {
		return BASE_PATH;
	}

	
	 public String getPhotoTempPath() {
		return BASE_PATH + "mohocache";
	}
	 * */

    /**
     * 获取文字贴纸id
     *
     * @return
     */
//    public static int getTextSeriesId() {
//        SharedPreferences stickerData = MHApplication.getInstance().getSharedPreferences(Constants.STICKERPREFER, Context.MODE_PRIVATE);
//        return stickerData.getInt("ndtext", -1);
//    }

    /**
     * 将文字贴纸Id设置进去
     *
     * @param id
     */
//    public static void setTextSeriesId(int id) {
//        SharedPreferences.Editor stickerDataEditor = MHApplication.getInstance().getSharedPreferences(Constants.STICKERPREFER, Context.MODE_PRIVATE).edit();
//        stickerDataEditor.putInt("ndtext", id).commit();
//    }

    /**
     * 将本地文件路径格式化为Uri
     *
     * @param imgsrc
     * @return
     */
    public static Uri pathToUri(String imgsrc) {
        return imgsrc.startsWith("file:") ? Uri.parse(imgsrc) : Uri.parse("file://" + imgsrc);
    }

    public File getExtFile(String path) {
        return new File(BASE_PATH + path);
    }

    /**
     * 获取文件夹大小
     *
     * @param file File实例
     * @return long 单位为K
     * @throws Exception
     */
    public long getFolderSize(File file) {
        try {
            long size = 0;
            if (!file.exists()) {
                return size;
            } else if (!file.isDirectory()) {
                return file.length() / 1024;
            }
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
            return size / 1024;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 远程服务器的图片
     *
     * @param
     * @return
     */
//    public Bitmap getAddonImage(final int packageId, final String stickerUrl) {
//        File f = new File(getImageFilePath(packageId, stickerUrl));
//        if (!f.exists()) {
//            MHApplication.getInstance().runThreadInPool(
//                    new RunnableEx() {
//                        @Override
//                        public void run() {
//                            saveBmpToFolder(packageId, stickerUrl);
//                        }
//                    });
//            return null;
//        } else {
//            Bitmap result = BitmapFactory.decodeFile(getImageFilePath(packageId, stickerUrl));
//            return result;
//        }
//    }

    public String getBasePath(int packageId) {
        return STICKER_BASE_PATH + packageId + "/";
    }

//    private String getImageFilePath(int packageId, String imageUrl) {
//        String md5Str = MD5Util.getMD5(imageUrl).replace("-", "mm");
//        return getBasePath(packageId) + md5Str;
//    }

    // 读取assets文件
//    public String readFromAsset(String fileName) {
//        InputStream is = null;
//        BufferedReader br = null;
//        try {
//            is = MHApplication.getInstance().getAssets().open(fileName);
//            br = new BufferedReader(new InputStreamReader(is));
//            String addonStr = "";
//            String line = br.readLine();
//            while (line != null) {
//                addonStr = addonStr + line;
//                line = br.readLine();
//            }
//            return addonStr;
//        } catch (Exception e) {
//            return null;
//        } finally {
//            IOUtil.closeStream(br);
//            IOUtil.closeStream(is);
//        }
//    }

    public void removeAddonFolder(int packageId) {
        String filename = getBasePath(packageId);
        File file = new File(filename);
        if (file.exists()) {// FIXME 是否需要判断是否为图片并作清除处理
            delete(file);
        }
    }

    public void delete(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                delete(childFiles[i]);
            }
            file.delete();
        }
    }

//    public boolean saveBmpToFolder(int packageId, String stickerUrl) {
//        String filename = getImageFilePath(packageId, stickerUrl);
//        File file = new File(filename);
//        if (file.exists()) {
//            return true;
//        }
//        InputStream inputStream = null;
//        OutputStream outStream = null;
//        try {
//            if (file.exists()) {// FIXME 是否需要判断是否为图片并作清除处理
//                delete(file);
//            }
//
//            if (stickerUrl.startsWith("http://") || stickerUrl.startsWith("https://")) {
//                inputStream = new URL(stickerUrl).openStream();
//            } else {
//                inputStream = MHApplication.getInstance().getAssets().open("preinstall/" + stickerUrl);
//            }
//            createFile(file);
//            outStream = new FileOutputStream(file);
//            byte[] buffer = new byte[2048];
//            int len = -1;
//            while ((len = inputStream.read(buffer)) != -1) {
//                outStream.write(buffer, 0, len);
//            }
//            outStream.flush();
//            return true;
//        } catch (Exception e) {
//            return false;
//        } finally {
//            IOUtil.closeStream(inputStream);
//            IOUtil.closeStream(outStream);
//        }
//    }

    public String getSystemPhotoPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera";
    }

    public boolean createFile(File file) {
        try {
            if (!file.getParentFile().exists()) {
                mkdir(file.getParentFile());
            }
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean mkdir(File file) {
        while (!file.getParentFile().exists()) {
            mkdir(file.getParentFile());
        }
        return file.mkdir();
    }

    public boolean writeSimpleString(File file, String string) {
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
            fOut.write(string.getBytes());
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        } finally {
            IOUtil.closeStream(fOut);
        }
    }

//    public String readSimpleString(File file) {
//        StringBuffer sb = new StringBuffer();
//        BufferedReader br = null;
//        try {
//            br = new BufferedReader(new FileReader(file));
//
//            String line = br.readLine();
//            if (StringUtils.isNotEmpty(line)) {
//                sb.append(line.trim());
//                line = br.readLine();
//            }
//        } catch (Throwable e) {
//            e.printStackTrace();
//            return "";
//        } finally {
//            IOUtil.closeStream(br);
//        }
//        return sb.toString();
//    }

    // 都是相对路径，一一对应
    public boolean copyAssetDirToFiles(Context context, String dirname) {
        try {
            AssetManager assetManager = context.getAssets();
            String[] children = assetManager.list(dirname);
            for (String child : children) {
                child = dirname + '/' + child;
                String[] grandChildren = assetManager.list(child);
                if (0 == grandChildren.length)
                    copyAssetFileToFiles(context, child);
                else
                    copyAssetDirToFiles(context, child);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 都是相对路径，一一对应
    public boolean copyAssetFileToFiles(Context context, String filename) {
        return copyAssetFileToFiles(context, filename, getExtFile("/" + filename));
    }

    private boolean copyAssetFileToFiles(Context context, String filename, File of) {
        InputStream is = null;
        FileOutputStream os = null;
        try {
            is = context.getAssets().open(filename);
            createFile(of);
            os = new FileOutputStream(of);

            int readedBytes;
            byte[] buf = new byte[1024];
            while ((readedBytes = is.read(buf)) > 0) {
                os.write(buf, 0, readedBytes);
            }
            os.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            IOUtil.closeStream(is);
            IOUtil.closeStream(os);
        }
    }

    public boolean renameDir(String oldDir, String newDir) {
        File of = new File(oldDir);
        File nf = new File(newDir);
        return of.exists() && !nf.exists() && of.renameTo(nf);
    }

    /**
     * 复制单个文件
     */
    public void copyFile(String oldPath, String newPath) {
        InputStream inStream = null;
        FileOutputStream fs = null;
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { // 文件存在时
                inStream = new FileInputStream(oldPath); // 读入原文件
                fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; // 字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        } finally {
            IOUtil.closeStream(inStream);
            IOUtil.closeStream(fs);
        }

    }

    public File getCacheDir() {
        return App.getDefault().getCacheDir();
    }

//    // 获取path路径下的图片
//    public ArrayList<PhotoItem> findPicsInDir(String path) {
//        ArrayList<PhotoItem> photos = new ArrayList<PhotoItem>();
//        File dir = new File(path);
//        if (dir.exists() && dir.isDirectory()) {
//            for (File file : dir.listFiles(new FileFilter() {
//
//                @Override
//                public boolean accept(File pathname) {
//                    String filePath = pathname.getAbsolutePath();
//                    return (filePath.endsWith(".png") || filePath.endsWith(".jpg") || filePath.endsWith(".jepg"));
//                }
//            })) {
//                photos.add(new PhotoItem(file.getAbsolutePath(), file.lastModified()));
//            }
//        }
//        Collections.sort(photos);
//        return photos;
//    }

}
