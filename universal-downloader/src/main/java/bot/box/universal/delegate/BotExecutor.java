package bot.box.universal.delegate;

import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bot.box.universal.callback.BotCallBack;

/**
 * Created by Barry Allen .
 * boxforbot@gmail.com
 */

public class BotExecutor extends AsyncTask<String, String, String> {
    private static final String INSTA_PREFIX = "https://www.instagram.com/p/.";
    private static final String YOUTUBE_PREFIX = "https://youtu.be/";

    private boolean type;

    private String storagePath, fileNamePrefix;
    private BotCallBack mCallback;

    public BotExecutor(String storagePath, String fileNamePrefix, BotCallBack l) {
        this.storagePath = storagePath;
        this.fileNamePrefix = fileNamePrefix;

        if (l != null)
            this.mCallback = l;
        else
            throw new NullPointerException("must implement BotCallBack interface in your view");
    }

    private boolean isYoutubeLink(String url) {
        Pattern r = Pattern.compile(YOUTUBE_PREFIX);

        Matcher m = r.matcher(url);
        if (m.find())
            return true;
        else
            return false;
    }

    private boolean isInstaLink(String url) {
        Pattern r = Pattern.compile(INSTA_PREFIX);

        Matcher m = r.matcher(url);
        if (m.find())
            return true;
        else
            return false;
    }

    @Override
    protected String doInBackground(String... strings) {
        String media = strings[0];
        if (isInstaLink(media)) {
            int count;
            type = false;
            try {
                String strCaption = null;

                Document document = Jsoup.connect(media).get();
                URL url = null;
                String html = document.toString();
                String urlVid = null;

                //for video
                int indexVid = html.indexOf("\"video_url\"");
                indexVid += 11;
                int startVid = html.indexOf("\"", indexVid);
                startVid += 1;
                int endVid = html.indexOf("\"", startVid);

                urlVid = html.substring(startVid, endVid);

                if (urlVid.equalsIgnoreCase("en")) {
                    int index = html.indexOf("display_url");
                    index += 13;
                    int start = html.indexOf("\"", index);
                    start += 1;
                    int end = html.indexOf("\"", start);
                    String urlImage = html.substring(start, end);
                    type = false;
                    url = new URL(urlImage);

                } else {

                    url = new URL(urlVid);
                    type = true;
                }

                //for caption
                int indexcaption = html.indexOf("edge_media_to_caption");
                indexcaption += 53;

                int startCaption = html.indexOf("\"", indexcaption);
                startCaption += 1;
                int endCaption = html.indexOf("\"", startCaption);

                strCaption = html.substring(startCaption, endCaption);


                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                //generate a unique name

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd-hh-mm-ss");
                //File myFile = null;
                // Output stream to write file
                File direct;

                if (storagePath.isEmpty()) {
                    direct = new File(Environment.getExternalStorageDirectory() + "/Insta-Bot");

                    if (!direct.exists()) {
                        direct = new File(Environment.getExternalStorageDirectory() + "/Insta-Bot");
                        direct.mkdirs();
                    }
                } else {
                    direct = new File(storagePath);
                    if (!direct.exists()) {
                        direct = new File(storagePath);
                        direct.mkdirs();
                    }
                }

                String fileName = null;

                if (fileNamePrefix.isEmpty()) {
                    if (!type) {
                        fileName = "Insta-Bot_"
                                + simpleDateFormat.format(new Date())
                                + ".jpg";
                    } else {
                        fileName = "Insta-Bot_"
                                + simpleDateFormat.format(new Date())
                                + ".mp4";
                    }
                } else {
                    if (!type) {
                        fileName = fileNamePrefix+"_"
                                + simpleDateFormat.format(new Date())
                                + ".jpg";
                    } else {
                        fileName = fileNamePrefix+"_"
                                + simpleDateFormat.format(new Date())
                                + ".mp4";
                    }
                }

                File file = new File(direct, fileName);
                if (file.exists()) {
                    file.delete();
                }

                OutputStream output = new FileOutputStream(file);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                input.close();

                // add image into the database

                return file.getAbsolutePath();
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                Log.e("Error Trace", "" + e);
                new Handler(Looper.getMainLooper()).post(() -> {
                    mCallback.onMediaResult(false, null);
                });
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mCallback.onMediaResult(true, s);
    }

}

