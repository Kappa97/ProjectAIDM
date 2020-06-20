package com.example.projectaidm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class RssActivity extends AppCompatActivity {
    ListView lvRss;
    ArrayList<String> titles;
    ArrayList<String> links;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss);
        lvRss = (ListView) findViewById(R.id.lvRss);
        titles = new ArrayList<String>();
        links = new ArrayList<String>();

//aplicatia este formata dintr-o lista cu titluri, fiecare titlu are o pozitie in lista, la apasarea pe un element al listei(titluri)
//se salveaza pozitia apoi se deschide in browser linkul cu acea pozitie
        lvRss.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri uri = Uri.parse(links.get(position));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        new ProcessInBackground().execute();
    }
    //incearca sa faca sa faca o conexiune cu URL-ul, in caz ca este gresit nu va bloc aplicatia
    public InputStream getInputStream(URL url1) {
        try {
            return url1.openConnection().getInputStream();
        } catch (IOException e) {
            return null;
        }
    }

    public class ProcessInBackground extends AsyncTask<Integer, Void, Exception> {

        ProgressDialog progressDialog = new ProgressDialog(RssActivity.this);
        Exception exception = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Please wait ...");
            progressDialog.show();
        }

        @Override
        protected Exception doInBackground(Integer... params) {
            try {
                URL url = new URL("http://feeds.24.com/articles/sport/featured/topstories/rss");

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

                factory.setNamespaceAware(false);

                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(getInputStream(url), "UTF-8");

                boolean insideItem = false;

                int eventType = xpp.getEventType();

//parcuge fisierul xml si colecteza informatii, mai intai cauta tag-ul item apoi in interiorul lui cauta
//tag-urile titile si link, daca le gasete le aduga in 2 liste(una pentru tiltu si alta pentru linkuri)

                while (eventType != XmlPullParser.END_DOCUMENT) {

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equalsIgnoreCase("item")) {
                            insideItem = true;
                        } else if (xpp.getName().equalsIgnoreCase("title")) {
                            if (insideItem) {
                                titles.add(xpp.nextText());
                            }
                        } else if (xpp.getName().equalsIgnoreCase("link")) {
                            if (insideItem) {
                                links.add(xpp.nextText());
                            }
                        }
                    } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                        insideItem = false;
                    }
                    eventType = xpp.next();
                }

            } catch (MalformedURLException e) {
                exception = e;
            } catch (XmlPullParserException e) {
                exception = e;
            } catch (IOException e) {
                exception = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Exception s) {
            super.onPostExecute(s);
//formeaza in aplicatie o lista  cu titlurile care le-a gasit
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(RssActivity.this, android.R.layout.simple_list_item_1, titles);
            lvRss.setAdapter(adapter);

            progressDialog.dismiss();
        }
    }
}
