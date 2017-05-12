package com.example.gilad.videomanager;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.security.cert.Certificate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.afollestad.materialdialogs.*;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

/**
 * Created by Gilad on 04/04/2017.
 */

class ScrapException extends Exception {

    public ScrapException(String message) {
        super(message);
    }

    public ScrapException(String message, Throwable throwable) {
        super(message, throwable);
    }

}

class Meta {
    public String num;
    public String type;
    public String ext;

    Meta(String num, String ext, String type) {
        this.num = num;
        this.ext = ext;
        this.type = type;
    }
}

// Not just youtube videos
class YouTubeVideo {
    public String ext = "";
    public String type = "";
    public String url = "";

    YouTubeVideo(String ext, String type, String url) {
        this.ext = ext;
        this.type = type;
        this.url = url;
    }
}


public class YouTubePageStreamUriGetter extends AsyncTask<String, String, String> {

    MaterialDialog md;
    Activity activity;
    String errorText = "";
    FoundVideo foundVideo = new FoundVideo();
    String streamingURL = "";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        md = new MaterialDialog.Builder(GlobalVars.getCurActivity())
                .title(R.string.progress_title)
                .content(R.string.please_wait)
                .progress(true, 0)
                .show();
    }

    @Override
    protected String doInBackground(String... params) {

        String url = params[0];
        this.foundVideo.setOriginalUrl(url);

        if (url.toLowerCase().contains("youtube.com")) {
            ArrayList<YouTubeVideo> videos = null;

            try {
                videos = getStreamingUrisFromYouTubePage(url);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ScrapException e) {

                errorText = e.getMessage();
                return null;
            }
            try {
                if (videos != null && !videos.isEmpty()) {
                    String retVidUrl = null;
                    for (YouTubeVideo video : videos) {
                        if (video.ext.toLowerCase().contains("mp4")
                                && video.type.toLowerCase().contains("medium")) {
                            retVidUrl = video.url;
                            break;
                        }
                    }
                    if (retVidUrl == null) {
                        for (YouTubeVideo video : videos) {
                            if (video.ext.toLowerCase().contains("3gp")
                                    && video.type.toLowerCase().contains(
                                    "medium")) {
                                retVidUrl = video.url;
                                break;

                            }
                        }
                    }
                    if (retVidUrl == null) {

                        for (YouTubeVideo video : videos) {
                            if (video.ext.toLowerCase().contains("mp4")
                                    && video.type.toLowerCase().contains("low")) {
                                retVidUrl = video.url;
                                break;

                            }
                        }
                    }
                    if (retVidUrl == null) {
                        for (YouTubeVideo video : videos) {
                            if (video.ext.toLowerCase().contains("3gp")
                                    && video.type.toLowerCase().contains("low")) {
                                retVidUrl = video.url;
                                break;
                            }
                        }
                    }


                    return retVidUrl;
                }
            } catch (Exception e) {
                errorText = "Couldn't get YouTube streaming URL";
            }
            errorText = "Couldn't get stream URI for " + url;
            return null;
        }
        // Non-Youtube video
        else {

            WebActivity webActivity = (WebActivity) GlobalVars.getCurActivity();

            while (webActivity.HTML != "")
            {
                String html = webActivity.HTML;

                html = html.substring(html.indexOf("<video"), html.indexOf("</video>"));
                if (!html.startsWith("<video") ||
                        !html.startsWith("<video") && html.contains("blob") )

                    errorText = "Couldn't find downloadable video in this page";
                else
                {
                    Document doc = Jsoup.parse(html);
                    Element srcElement = doc.select("video").first();

                    foundVideo.setOriginalHTML(html);

                    return srcElement.attr("src");
                }

                break;
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(String streamingURL) {
        super.onPostExecute(streamingURL);

        // Remove progress dialog
        md.dismiss();

        // In case of problem while getting video
        if ( errorText != "")
        {
            md = new MaterialDialog.Builder(GlobalVars.getCurActivity())
                    .title(R.string.error)
                    .content(errorText)
                    .show();
        }
        else
        {
            foundVideo.getTitleFromHTML();
            GlobalVars.foundVideo = new FoundVideo(foundVideo);

            GlobalVars.getCurActivity().finish();

        }
    }

    public static String readStream(BufferedReader reader) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();

            int i = reader.read();
            while (i != -1) {
                bo.write(i);
                i = reader.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return null;
        }
    }

    public ArrayList<YouTubeVideo> getStreamingUrisFromYouTubePage(String ytUrl)
            throws IOException, ScrapException {
        if (ytUrl == null) {
            return null;
        }

        // Sending request
        HttpsURLConnection client = (HttpsURLConnection) new URL(ytUrl).openConnection();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(client.getInputStream(), Charset.forName("UTF-8"))
                );

        String html = readStream(reader);

        html = html.replace("\\u0026", "&");

        if (html.contains("verify-age-thumb")) {
            throw new ScrapException("YouTube is asking for age verification. We can't handle that sorry.");
        }

        if (html.contains("das_captcha")) {
            throw new ScrapException("Captcha found, please try from different IP address.");
        }

        // Getting the stream maps
        Pattern p = Pattern.compile("stream_map\":\"(.*?)?\"");
        Matcher m = p.matcher(html);
        List<String> matches = new ArrayList<String>();

        while (m.find()) {
            matches.add(m.group());
        }

        // No video was found
        if (matches.size() != 1) {
            throw new ScrapException("Found zero or too many stream maps.");
        }

        String urls[] = matches.get(0).split(",");
        HashMap<String, String> foundArray = new HashMap<String, String>();
        for (String ppUrl : urls) {
            String url = URLDecoder.decode(ppUrl, "UTF-8");

            Pattern p1 = Pattern.compile("itag=([0-9]+?)[&]");
            Matcher m1 = p1.matcher(url);
            String itag = null;
            if (m1.find()) {
                itag = m1.group(1);
            }


            Pattern p2 = Pattern.compile("signature=(.*?)[&]");
            Matcher m2 = p2.matcher(url);
            String sig = null;
            if (m2.find()) {
                sig = m2.group(1);
            }

            Pattern p3 = Pattern.compile("url=(.*?)[&]");
            Matcher m3 = p3.matcher(ppUrl);
            String um = null;
            if (m3.find()) {
                um = m3.group(1);
            }

            if (itag != null && sig != null && um != null) {
                foundArray.put(itag, URLDecoder.decode(um, "UTF-8") + "&"
                        + "signature=" + sig);
            }
        }

        if (foundArray.size() == 0) {
            throw new ScrapException("Couldn't find any URLs and corresponding signatures");
        }

        HashMap<String, Meta> typeMap = new HashMap<String, Meta>();
        typeMap.put("13", new Meta("13", "3GP", "Low Quality - 176x144"));
        typeMap.put("17", new Meta("17", "3GP", "Medium Quality - 176x144"));
        typeMap.put("36", new Meta("36", "3GP", "High Quality - 320x240"));
        typeMap.put("5", new Meta("5", "FLV", "Low Quality - 400x226"));
        typeMap.put("6", new Meta("6", "FLV", "Medium Quality - 640x360"));
        typeMap.put("34", new Meta("34", "FLV", "Medium Quality - 640x360"));
        typeMap.put("35", new Meta("35", "FLV", "High Quality - 854x480"));
        typeMap.put("43", new Meta("43", "WEBM", "Low Quality - 640x360"));
        typeMap.put("44", new Meta("44", "WEBM", "Medium Quality - 854x480"));
        typeMap.put("45", new Meta("45", "WEBM", "High Quality - 1280x720"));
        typeMap.put("18", new Meta("18", "MP4", "Medium Quality - 480x360"));
        typeMap.put("22", new Meta("22", "MP4", "High Quality - 1280x720"));
        typeMap.put("37", new Meta("37", "MP4", "High Quality - 1920x1080"));
        typeMap.put("33", new Meta("38", "MP4", "High Quality - 4096x230"));

        ArrayList<YouTubeVideo> videos = new ArrayList<YouTubeVideo>();

        for (String format : typeMap.keySet()) {
            Meta meta = typeMap.get(format);

            if (foundArray.containsKey(format)) {
                YouTubeVideo newVideo = new YouTubeVideo(meta.ext, meta.type,
                        foundArray.get(format));
                videos.add(newVideo);

                Log.d("TAG", "YouTube Video streaming details: ext:" + newVideo.ext
                        + ", type:" + newVideo.type + ", url:" + newVideo.url);
            }
        }

        // For future use
        this.foundVideo.setOriginalHTML(html);

        return videos;
    }
}


