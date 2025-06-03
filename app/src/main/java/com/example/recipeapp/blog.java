package com.example.recipeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class blog extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BlogAdapter adapter;
    private List<blog_post> blogPostList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        blogPostList = new ArrayList<>();
        new FetchFeedTask().execute(" https://www.bonappetit.com/feed/rss ");

    }

    private class FetchFeedTask extends AsyncTask<String, Void, List<blog_post>> {

        @Override
        protected List<blog_post> doInBackground(String... strings) {
            String urlString = strings[0];
            List<blog_post> posts = new ArrayList<>();
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                posts = parseFeed(inputStream);

                inputStream.close();
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return posts;
        }

        @Override
        protected void onPostExecute(List<blog_post> posts) {
            super.onPostExecute(posts);
            blogPostList.clear();
            blogPostList.addAll(posts);
            adapter = new BlogAdapter(blogPostList);
            recyclerView.setAdapter(adapter);
        }

        private List<blog_post> parseFeed(InputStream inputStream) throws XmlPullParserException, java.io.IOException {
            List<blog_post> items = new ArrayList<>();
            String title = null;
            String description = null;
            String link = null;
            String imageUrl = null;
            boolean isItem = false;

            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStream, null);

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name;
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        name = parser.getName();

                        if (name.equalsIgnoreCase("item")) {
                            isItem = true;
                            imageUrl = null;  // reset image for new item
                        } else if (name.equalsIgnoreCase("title")) {
                            title = parser.nextText();
                        } else if (name.equalsIgnoreCase("description")) {
                            description = parser.nextText();

                            // Extract image from description if not found yet
                            if (description != null && imageUrl == null) {
                                imageUrl = extractImageUrlFromDescription(description);
                            }
                        } else if (name.equalsIgnoreCase("link")) {
                            link = parser.nextText();
                        } else if (name.equalsIgnoreCase("media:thumbnail") || name.equalsIgnoreCase("thumbnail")) {
                            String url = parser.getAttributeValue(null, "url");
                            if (url != null && !url.isEmpty()) {
                                imageUrl = url;
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase("item") && isItem) {
                            blog_post item = new blog_post(title, description, link, imageUrl);
                            items.add(item);

                            title = null;
                            description = null;
                            link = null;
                            imageUrl = null;
                            isItem = false;
                        }
                        break;
                }
                eventType = parser.next();
            }
            return items;
        }

        // Helper method to extract image URL from description HTML string
        private String extractImageUrlFromDescription(String description) {
            // simple regex to get first <img src="...">
            String imageUrl = null;
            try {
                String regex = "<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>";
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
                java.util.regex.Matcher matcher = pattern.matcher(description);
                if (matcher.find()) {
                    imageUrl = matcher.group(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return imageUrl;
        }
    }}