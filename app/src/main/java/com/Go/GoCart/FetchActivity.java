package com.Go.GoCart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class FetchActivity extends AppCompatActivity {

    AppCompatActivity activity;
    int limit1, minimum = 0, maximum = 0, i, limit, results, errorcode = 0, scrapeusing;
    String html, urltext, json1, url = null, url2 = null, url1 = null, url3 = null, category, json, itemlist, s;
    String[] cost, link, name, site, currency, image, cost1, link1, name1, site1, image1, listorder, listorder1, listorder2, itemindex, listorder3, finallist;
    float[] ratings, ratings1;
    JSONObject object;
    Toast toast, noresult, error;
    Elements LinkNameBlock, LinkNameBlock1, MerchantBlock1, imageBlock11, MerchantBlock, NameBlock, CostBlock, Block, Block1, NameBlock1, CostBlock1, imageBlock1, Block2;
    JSONObject obj = null, data = null, product = null;
    MyProgressDialog load;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int theme = Integer.valueOf(sharedPreferences.getString("theme", "0"));
        switch (theme) {
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case 2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        activity = this;
        Intent i = getIntent();
        urltext = i.getStringExtra("urltext");
        url = i.getStringExtra("url");
        url1 = i.getStringExtra("url1");
        url2 = i.getStringExtra("url2");
        url3 = i.getStringExtra("url3");
        results = i.getIntExtra("resultcount", 50);
        scrapeusing=2;
        scrapeusing = i.getIntExtra("scrapeusing", 2);
        maximum = i.getIntExtra("max", (int) Double.POSITIVE_INFINITY);
        minimum = i.getIntExtra("min", 0);
        s = getIntent().getStringExtra("query");
        category = getIntent().getStringExtra("cat");
        load = new MyProgressDialog(activity);
        load.show();
        chooseMethod();
    }

    public void chooseMethod() {
        if (scrapeusing == 1) {
            website();
        } else if (scrapeusing == 2) {
            google();
        } else {
            hybrid();
        }
    }

    public void website() {
        NetworkInfo info =  ((ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null) {
            new Thread(() -> {
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(url);
                try {
                    HttpResponse response = client.execute(request);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    json = out.toString();
                    out.close();
                } catch (IOException e) {
                }
                if ((json == null) || json.contains("Internal Server Error")) {
                    client = new DefaultHttpClient();
                    request = new HttpGet(url2);
                    try {
                        HttpResponse response = client.execute(request);
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        json = out.toString();
                        out.close();
                    } catch (IOException e) {
                    }
                }
                jsonparse();
            }).start();
        } else {
            load.a = false;
            Toast.makeText(activity, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(activity, MainActivity.class);
            activity.startActivity(intent);
        }
    }

    public void google() {
        NetworkInfo info = ((ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null) {
            new Thread(() -> {
                String url = "https://www.google.com/search?hl=en&tbm=shop&q=" + urltext;
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(url);
                try {
                    HttpResponse response = client.execute(request);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    html = out.toString();
                    out.close();
                } catch (IOException e) {
                }

                Document document = Jsoup.parse(html);
                Block = document.select("div[class$=u30d4]");
                Block1 = document.select("div[class$=KZmu8e]");
                //Block2 = document.select("div[class$=]");
                if (Block1.size() > 0 && Block1 != null) {
                    LinkNameBlock1 = Block1.select("a[class$=sh-np__click-target]");
                    NameBlock1 = Block1.select("div[class$=sh-np__product-title]");
                    imageBlock11 = Block1.select("div[class$=SirUVb sh-img__image]");
                    CostBlock1 = Block1.select("span[class$=T14wmb]");
                    MerchantBlock1 = Block1.select("span[class$=E5ocAb]");
                    if (Block1.size() >= 5) {
                        listorder1 = new String[5];
                    } else {
                        listorder1 = new String[Block1.size()];
                    }
                    for (i = 0; i < listorder1.length; i++) {
                        String linkval = LinkNameBlock1.get(i).attr("href").replace("/url?q=", "");
                        String nameval = LinkNameBlock1.get(i).text();
                        String imgval = imageBlock11.get(i).selectFirst("img").attr("src");
                        String costvalue = CostBlock1.get(i).text().split(" ", 0)[0].replace(",", "").replace("₹", "");
                        String merchant = MerchantBlock1.get(i).text().replace(" ", "");
                        String rating = "null";
                        listorder1[i] = "{\"cost\":" + "\"" + costvalue + "\"" + ",\"name\":" + "\"" + nameval.replace("\"", "") + "\"," + "\"image\":" + "\"" + imgval + "\"," + "\"link\":\"" + linkval + "\"," + "\"rating\":\"" + rating + "\"," + "\"site\":\"" + merchant.replace("\"", "") + "\"}";
                    }
                }
                if (Block.size() > 0 && Block != null) {
                    LinkNameBlock = Block.select("div[class$=rgHvZc]");
                    NameBlock = Block.select("div[class$=rgHvZc]");
                    imageBlock1 = Block.select("div[class$=oR27Gd]");
                    CostBlock = Block.select("span[class$=HRLxBb]");
                    MerchantBlock = Block.select("div[class$=dD8iuc]");
                    if (listorder1 != null) {
                        if (LinkNameBlock.size() >= results && listorder1.length == 5) {
                            listorder = new String[results - 5];
                        } else if (LinkNameBlock.size() < results - 5 && listorder1.length == 5) {
                            listorder = new String[LinkNameBlock.size()];
                        } else if (LinkNameBlock.size() < results - 5 && listorder1.length < 5) {
                            listorder = new String[LinkNameBlock.size()];
                        } else if (LinkNameBlock.size() >= results && listorder1.length < 5) {
                            listorder = new String[results - 5];
                        }
                    } else if (LinkNameBlock.size() > 0) {
                        listorder = new String[LinkNameBlock.size()];
                    }
                }

                if (errorcode != 1) {
                    if (listorder != null) {
                        for (i = 0; i < listorder.length; i++) {
                            String linkval = LinkNameBlock.get(i).selectFirst("a").attr("href").replace("/url?q=", "");
                            String nameval = LinkNameBlock.get(i).selectFirst("a").text();
                            String imgval = imageBlock1.get(i).selectFirst("img").attr("src");
                            String costvalue = CostBlock.get(i).text().split(" ", 0)[0].replace(",", "").replace("₹", "");
                            String merchant = MerchantBlock.get(i).text().split("from ", 0)[1].replace("from", "").replace(" ", "");
                            String rating = "null";
                            listorder[i] = "{\"cost\":" + "\"" + costvalue + "\"" + ",\"name\":" + "\"" + nameval.replace("\"", "") + "\"," + "\"image\":" + "\"" + imgval + "\"," + "\"link\":\"" + linkval + "\"," + "\"rating\":\"" + rating + "\"," + "\"site\":\"" + merchant.replace("\"", "") + "\"}";
                        }
                    }

                    try {
                        document = Jsoup.connect("https://www.amazon.in/s?k=" + urltext).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36").get();
                    } catch (IOException e) {

                    }
                    Block = document.select("div[class$=s-include-content-margin s-border-bottom s-latency-cf-section]");
                    if (Block == null) {
                            Block1 = document.select("div[class$=sg-col-4-of-24 sg-col-4-of-12 sg-col-4-of-36 s-result-item s-asin sg-col-4-of-28 sg-col-4-of-16 sg-col sg-col-4-of-20 sg-col-4-of-32]");
                            if (Block1 != null) {
                                if (Block1.size() > 0) {
                                    if (Block1.size() <= 3) {
                                        listorder2 = new String[Block1.size()];
                                    } else {
                                        listorder2 = new String[3];
                                    }
                                    int count1 = 0;

                                    Elements Ratingblock = null;
                                    int switcher = 0;
                                    for (i = 0; i < listorder2.length; i++) {
                                        String nameval = null;
                                        String costvalue = "";
                                        String linkval = null;
                                        String imgval = "null";
                                        String rating = "null";
                                        if (switcher == 0) {
                                            NameBlock = Block.get(i).select("span[class$=a-size-medium a-color-base a-text-normal]");
                                        }
                                        int size = NameBlock.size();
                                        if (size == 0 || switcher == 1) {
                                            switcher = 1;
                                            NameBlock = Block.get(i).select("span[class$=a-size-base-plus a-color-base a-text-normal]");
                                            size = NameBlock.size();
                                        }
                                        if (NameBlock != null) {
                                            if (NameBlock.size() != 0) {
                                                nameval = NameBlock.get(0).text();
                                                if (nameval.equals("Sponsored")) {
                                                    continue;
                                                }
                                            }
                                        }
                                        CostBlock = Block1.get(i).select("span[class$=a-price-whole]");
                                        if (CostBlock != null) {
                                            if (CostBlock.size() != 0) {
                                                costvalue = CostBlock.get(0).text();
                                            }
                                            if (costvalue.contains(" ")) {
                                                costvalue = "";
                                            }
                                        }
                                        LinkNameBlock = Block1.get(i).select("a[class$=a-link-normal a-text-normal]");
                                        if (LinkNameBlock != null) {
                                            if (LinkNameBlock.size() != 0) {
                                                linkval = "https://www.amazon.in" + LinkNameBlock.get(0).attr("href");
                                            }
                                        } else {
                                            continue;
                                        }
                                        imageBlock1 = Block1.get(i).select("img[class$=s-image]");
                                        if (imageBlock1 != null) {
                                            if (imageBlock1.size() != 0) {
                                                imgval = imageBlock1.get(0).attr("src");
                                            }
                                        }
                                        Ratingblock = Block1.get(i).select("span[class$=a-icon-alt]");
                                        if (Ratingblock != null) {
                                            if (Ratingblock.size() != 0) {
                                                rating = Ratingblock.get(0).text().replace(" out of ", "/").replace(" stars", "");
                                            }
                                        }

                                        listorder2[count1] = "{\"cost\":" + "\"" + costvalue + "\"" + ",\"name\":" + "\"" + nameval.replace("\"", "") + "\"," + "\"image\":" + "\"" + imgval + "\"," + "\"link\":\"" + linkval + "\"," + "\"rating\":\"" + rating + "\"," + "\"site\":\"" + "Amazon" + "\"}";
                                        count1++;
                                    }
                                }
                            }
                    } else {
                        try {
                            document = Jsoup.connect("https://www.amazon.in/s?k=" + urltext).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36").get();
                        } catch (IOException e) {

                        }
                        Block = document.select("div[class$=s-include-content-margin s-border-bottom s-latency-cf-section]");
                        if (Block.size() > 0) {
                            if (Block.size() <= 3) {
                                listorder2 = new String[Block.size()];
                            } else {
                                listorder2 = new String[3];
                            }
                            int count1 = 0;

                            Elements Ratingblock = null;
                            int switcher = 0;
                            for (i = 0; i < listorder2.length; i++) {
                                String nameval = null;
                                String costvalue = "rinf";
                                String linkval = null;
                                String imgval = "null";
                                String rating = "null";
                                if (switcher == 0) {
                                    NameBlock = Block.get(i).select("span[class$=a-size-medium a-color-base a-text-normal]");

                                }
                                int size = NameBlock.size();
                                if (size == 0 || switcher == 1) {
                                    switcher = 1;
                                    NameBlock = Block.get(i).select("span[class$=a-size-base-plus a-color-base a-text-normal]");
                                    size = NameBlock.size();
                                }
                                if (NameBlock != null) {
                                    if (size != 0) {
                                        nameval = NameBlock.get(0).text();
                                        if (nameval.equals("Sponsored")) {
                                            continue;
                                        }
                                    }
                                }
                                CostBlock = Block.get(i).select("span[class$=a-price-whole]");
                                if (CostBlock != null) {
                                    if (CostBlock.size() != 0) {
                                        costvalue = CostBlock.get(0).text();
                                    }
                                }
                                LinkNameBlock = Block.get(i).select("a[class$=a-link-normal a-text-normal]");
                                if (LinkNameBlock != null) {
                                    if (LinkNameBlock.size() != 0) {
                                        linkval = "https://www.amazon.in" + LinkNameBlock.get(0).attr("href");
                                    }
                                } else {
                                    continue;
                                }
                                imageBlock1 = Block.get(i).select("img[class$=s-image]");
                                if (imageBlock1 != null) {
                                    if (imageBlock1.size() != 0) {
                                        imgval = imageBlock1.get(0).attr("src");
                                    }
                                }
                                Ratingblock = Block.get(i).select("span[class$=a-icon-alt]");
                                if (Ratingblock != null) {
                                    if (Ratingblock.size() != 0) {
                                        rating = Ratingblock.get(0).text().replace(" out of ", "/").replace(" stars", "");
                                    }
                                }
                                if (costvalue.contains(" ")) {
                                    costvalue = "";
                                }

                                listorder2[count1] = "{\"cost\":" + "\"" + costvalue + "\"" + ",\"name\":" + "\"" + nameval.replace("\"", "") + "\"," + "\"image\":" + "\"" + imgval + "\"," + "\"link\":\"" + linkval + "\"," + "\"rating\":\"" + rating + "\"," + "\"site\":\"" + "Amazon" + "\"}";
                                count1++;
                            }
                        }
                    }
                    try {
                        document = Jsoup.connect("https://www.flipkart.com/search?q=" + urltext).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36").get();
                    } catch (IOException e) {

                    }
                    Block = document.select("div[class$=_4ddWXP]");
                    if (Block == null) {
                        if (Block.size() == 0) {
                            Block1 = document.select("div[class$=_1xHGtK _373qXS]");
                            if (Block1 != null) {
                                if (Block1.size() > 0) {
                                    if (Block1.size() <= 2) {
                                        listorder3 = new String[Block1.size()];
                                    } else {
                                        listorder3 = new String[2];
                                    }
                                    int count1 = 0;

                                    for (i = 0; i < listorder3.length; i++) {
                                        String nameval = null;
                                        String costvalue = "rinf";
                                        String linkval = null;
                                        String imgval = "null";
                                        String rating = "null";

                                        NameBlock = Block1.get(i).select("a[class$=IRpwTa]");
                                        if (NameBlock != null) {
                                            if (NameBlock.size() != 0) {
                                                nameval = NameBlock.get(0).attr("title");
                                            }
                                        }
                                        CostBlock = Block1.get(i).select("div[class$=_30jeq3]");
                                        if (CostBlock != null) {
                                            if (CostBlock.size() != 0) {
                                                costvalue = CostBlock.get(0).text();
                                            }
                                        }
                                        LinkNameBlock = Block1.get(i).select("a[class$=IRpwTa]");
                                        if (LinkNameBlock != null) {
                                            if (LinkNameBlock.size() != 0) {
                                                linkval = "https://www.flipkart.com" + LinkNameBlock.get(0).attr("href");
                                            }
                                        } else {
                                            continue;
                                        }

                                        listorder3[count1] = "{\"cost\":" + "\"" + costvalue + "\"" + ",\"name\":" + "\"" + nameval.replace("\"", "") + "\"," + "\"image\":" + "\"" + imgval + "\"," + "\"link\":\"" + linkval + "\"," + "\"rating\":\"" + rating + "\"," + "\"site\":\"" + "Flipkart" + "\"}";
                                        count1++;
                                    }
                                }
                            }

                        }
                    } else if (Block1 == null && Block == null) {
                        if (Block1.size() == 0 && Block.size() == 0) {
                            Block1 = document.select("div[class$=_2kHMtA]");
                            if (Block1 != null) {
                                if (Block1.size() > 0) {
                                    if (Block1.size() <= 2) {
                                        listorder3 = new String[Block1.size()];
                                    } else {
                                        listorder3 = new String[2];
                                    }
                                    int count1 = 0;


                                    for (i = 0; i < listorder3.length; i++) {
                                        String nameval = null;
                                        String costvalue = "rinf";
                                        String linkval = null;
                                        String imgval = "null";
                                        String rating = "null";
                                        NameBlock = Block1.get(i).select("div[class$=_4rR01T]");
                                        if (NameBlock != null) {
                                            if (NameBlock.size() != 0) {
                                                nameval = NameBlock.get(0).text();
                                            }
                                        }
                                        CostBlock = Block1.get(i).select("div[class$=_30jeq3 _1_WHN1]");
                                        if (CostBlock != null) {
                                            if (CostBlock.size() != 0) {
                                                costvalue = CostBlock.get(0).text();
                                            }
                                        }
                                        LinkNameBlock = Block1.get(i).select("a[class$=_1fQZEK]");
                                        if (LinkNameBlock != null) {
                                            if (LinkNameBlock.size() != 0) {
                                                linkval = "https://www.flipkart.com" + LinkNameBlock.get(0).attr("href");
                                            }
                                        } else {
                                            continue;
                                        }

                                        listorder3[count1] = "{\"cost\":" + "\"" + costvalue + "\"" + ",\"name\":" + "\"" + nameval.replace("\"", "") + "\"," + "\"image\":" + "\"" + imgval + "\"," + "\"link\":\"" + linkval + "\"," + "\"rating\":\"" + rating + "\"," + "\"site\":\"" + "Flipkart" + "\"}";
                                        count1++;
                                    }
                                }
                            }

                        }
                    } else {
                        if (Block.size() > 0) {
                            if (Block.size() <= 2) {
                                listorder3 = new String[Block.size()];
                            } else {
                                listorder3 = new String[2];
                            }
                            int count1 = 0;


                            for (i = 0; i < listorder3.length; i++) {
                                String nameval = null;
                                String costvalue = "rinf";
                                String linkval = null;
                                String imgval = "null";
                                String rating = "null";
                                NameBlock = Block.get(i).select("a[class$=s1Q9rs]");
                                if (NameBlock != null) {
                                    if (NameBlock.size() != 0) {
                                        nameval = NameBlock.get(0).attr("title");
                                    }
                                }
                                CostBlock = Block.get(i).select("div[class$=_30jeq3]");
                                if (CostBlock != null) {
                                    if (CostBlock.size() != 0) {
                                        costvalue = CostBlock.get(0).text();
                                    }
                                }
                                LinkNameBlock = Block.get(i).select("a[class$=s1Q9rs]");
                                if (LinkNameBlock != null) {
                                    if (LinkNameBlock.size() != 0) {
                                        linkval = "https://www.flipkart.com" + LinkNameBlock.get(0).attr("href");
                                    }
                                } else {
                                    continue;
                                }

                                listorder3[count1] = "{\"cost\":" + "\"" + costvalue + "\"" + ",\"name\":" + "\"" + nameval.replace("\"", "") + "\"," + "\"image\":" + "\"" + imgval + "\"," + "\"link\":\"" + linkval + "\"," + "\"rating\":\"" + rating + "\"," + "\"site\":\"" + "Flipkart" + "\"}";
                                count1++;
                            }
                        }
                    }


                    if (listorder2 != null && listorder != null && listorder1 != null && listorder3 != null) {
                        finallist = new String[listorder.length + listorder1.length + listorder2.length + listorder3.length];
                    } else if (listorder != null && listorder1 != null & listorder3 != null) {
                        finallist = new String[listorder.length + listorder1.length + listorder3.length];
                    } else if (listorder != null && listorder1 != null & listorder2 != null) {
                        finallist = new String[listorder.length + listorder1.length + listorder2.length];
                    } else if (listorder != null && listorder2 != null & listorder3 != null) {
                        finallist = new String[listorder.length + listorder2.length + listorder3.length];
                    } else if (listorder2 != null && listorder1 != null & listorder3 != null) {
                        finallist = new String[listorder3.length + listorder1.length + listorder2.length];
                    } else if (listorder != null && listorder2 != null) {
                        finallist = new String[listorder.length + listorder2.length];
                    } else if (listorder2 != null && listorder1 != null) {
                        finallist = new String[listorder2.length + listorder1.length];
                    } else if (listorder3 != null && listorder1 != null) {
                        finallist = new String[listorder1.length + listorder3.length];
                    } else if (listorder != null && listorder1 != null) {
                        finallist = new String[listorder.length + listorder1.length];
                    } else if (listorder != null && listorder3 != null) {
                        finallist = new String[listorder.length + listorder3.length];
                    } else if (listorder2 != null && listorder3 != null) {
                        finallist = new String[listorder2.length + listorder3.length];
                    } else if (listorder1 != null) {
                        finallist = new String[listorder1.length];
                    } else if (listorder != null) {
                        finallist = new String[listorder.length];
                    } else if (listorder2 != null) {
                        finallist = new String[listorder2.length];
                    } else if (listorder3 != null) {
                        finallist = new String[listorder3.length];
                    }
                    if (finallist != null) {
                        int position = 0;
                        if (listorder1 != null) {
                            for (String element : listorder1) {
                                finallist[position] = element;
                                position++;
                            }
                        }

                        if (listorder != null) {
                            for (String element : listorder) {
                                finallist[position] = element;
                                position++;
                            }
                        }
                        if (listorder2 != null) {
                            for (String element : listorder2) {
                                finallist[position] = element;
                                position++;
                            }
                        }
                        if (listorder3 != null) {
                            for (String element : listorder3) {
                                finallist[position] = element;
                                position++;
                            }
                        }
                        int tempcheck = finallist.length;
                        itemindex = new String[tempcheck + 1];
                        itemlist = ("{\"data\":{");
                        for (i = 0; i < tempcheck; i++) {
                            itemindex[i] = ("\"" + (i + 1) + "\":" + finallist[i] + ",");
                            itemlist += itemindex[i];
                        }
                        itemlist = itemlist.substring(0, itemlist.length() - 1);
                        itemlist += ("},\"results\":" + "\"" + String.valueOf(i) + "\"}");
                        json = itemlist;
                    }
                    jsonparse();
                }
            }).start();
        } else {
            Toast.makeText(activity, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(activity, MainActivity.class);
            activity.startActivity(intent);
        }
    }

    public void hybrid() {
        NetworkInfo info = ((ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null) {
            new Thread(() -> {
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(url);
                try {
                    HttpResponse response = client.execute(request);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    json = out.toString();
                    out.close();
                } catch (IOException e) {
                }
                if ((json == null) || json.contains("Internal Server Error")) {
                    client = new DefaultHttpClient();
                    request = new HttpGet(url2);
                    try {
                        HttpResponse response = client.execute(request);
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        json = out.toString();
                        out.close();
                    } catch (IOException e) {
                    }

                }

                String url = "https://www.google.com/search?hl=en&tbm=shop&q=" + urltext;
                client = new DefaultHttpClient();
                request = new HttpGet(url);
                try {
                    HttpResponse response = client.execute(request);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    html = out.toString();
                    out.close();
                } catch (IOException e) {
                }

                Document document = Jsoup.parse(html);
                Block = document.select("div[class$=u30d4]");
                Block1 = document.select("div[class$=KZmu8e]");
                if (Block1.size() > 0 && Block1 != null) {
                    LinkNameBlock1 = Block1.select("a[class$=sh-np__click-target]");
                    NameBlock1 = Block1.select("div[class$=sh-np__product-title]");
                    imageBlock11 = Block1.select("div[class$=SirUVb sh-img__image]");
                    CostBlock1 = Block1.select("span[class$=T14wmb]");
                    MerchantBlock1 = Block1.select("span[class$=E5ocAb]");
                    if (Block1.size() >= 5) {
                        listorder1 = new String[5];
                    } else {
                        listorder1 = new String[Block1.size()];
                    }
                    for (i = 0; i < listorder1.length; i++) {
                        String linkval = LinkNameBlock1.get(i).attr("href").replace("/url?q=", "");
                        String nameval = LinkNameBlock1.get(i).text();
                        String imgval = imageBlock11.get(i).selectFirst("img").attr("src");
                        String costvalue = CostBlock1.get(i).text().split(" ", 0)[0].replace(",", "").replace("₹", "");
                        String merchant = MerchantBlock1.get(i).text().replace(" ", "");
                        String rating = "null";
                        listorder1[i] = "{\"cost\":" + "\"" + costvalue + "\"" + ",\"name\":" + "\"" + nameval.replace("\"", "") + "\"," + "\"image\":" + "\"" + imgval + "\"," + "\"link\":\"" + linkval + "\"," + "\"rating\":\"" + rating + "\"," + "\"site\":\"" + merchant.replace("\"", "") + "\"}";
                    }
                }


                if (Block.size() > 0 && Block != null) {
                    LinkNameBlock = Block.select("div[class$=rgHvZc]");
                    NameBlock = Block.select("div[class$=rgHvZc]");
                    imageBlock1 = Block.select("div[class$=oR27Gd]");
                    CostBlock = Block.select("span[class$=HRLxBb]");
                    MerchantBlock = Block.select("div[class$=dD8iuc]");
                    if (listorder1 != null) {
                        if (LinkNameBlock.size() >= results && listorder1.length == 5) {
                            listorder = new String[results - 5];
                        } else if (LinkNameBlock.size() < results - 5 && listorder1.length == 5) {
                            listorder = new String[LinkNameBlock.size()];
                        } else if (LinkNameBlock.size() < results - 5 && listorder1.length < 5) {
                            listorder = new String[LinkNameBlock.size()];
                        } else if (LinkNameBlock.size() >= results && listorder1.length < 5) {
                            listorder = new String[results - 5];
                        }
                    } else if (LinkNameBlock.size() > 0) {
                        listorder = new String[LinkNameBlock.size()];
                    }
                }

                if (errorcode != 1) {
                    if (listorder != null) {
                        for (i = 0; i < listorder.length; i++) {
                            String linkval = LinkNameBlock.get(i).selectFirst("a").attr("href").replace("/url?q=", "");
                            String nameval = LinkNameBlock.get(i).selectFirst("a").text();
                            String imgval = imageBlock1.get(i).selectFirst("img").attr("src");
                            String costvalue = CostBlock.get(i).text().split(" ", 0)[0].replace(",", "").replace("₹", "");
                            String merchant = MerchantBlock.get(i).text().split("from ", 0)[1].replace("from", "").replace(" ", "");
                            String rating = "null";
                            //String costval = String.valueOf(Math.round(Float.parseFloat(costvalue.split(" ")[0].substring(0, costvalue.length() - 1).replace("\"", ""))));
                            listorder[i] = "{\"cost\":" + "\"" + costvalue + "\"" + ",\"name\":" + "\"" + nameval.replace("\"", "") + "\"," + "\"image\":" + "\"" + imgval + "\"," + "\"link\":\"" + linkval + "\"," + "\"rating\":\"" + rating + "\"," + "\"site\":\"" + merchant.replace("\"", "") + "\"}";
                        }
                    }
                    String[] finallist = new String[0];
                    if (listorder1 != null && listorder != null) {
                        finallist = new String[listorder.length + listorder1.length];
                    } else if (listorder != null) {
                        finallist = new String[listorder.length];
                    } else if (listorder1 != null) {
                        finallist = new String[listorder1.length];
                    }
                    int position = 0;
                    if (finallist != null && finallist.length > 0) {
                        if (listorder1 != null) {
                            for (String element : listorder1) {
                                finallist[position] = element;
                                position++;
                            }
                        }
                        if (listorder != null) {
                            for (String element : listorder) {
                                finallist[position] = element;
                                position++;
                            }
                        }
                        int tempcheck = finallist.length;
                        itemindex = new String[tempcheck + 1];
                        itemlist = ("{\"data\":{");
                        for (i = 0; i < tempcheck; i++) {
                            itemindex[i] = ("\"" + (i + 1) + "\":" + finallist[i] + ",");
                            itemlist += itemindex[i];
                        }
                        itemlist = itemlist.substring(0, itemlist.length() - 1);
                        itemlist += ("},\"results\":" + "\"" + String.valueOf(i) + "\"}");
                        json1 = itemlist;
                    }
                    jsonparse();
                }
            }).start();
        } else {
            load.a = false;
            Toast.makeText(activity, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(activity, MainActivity.class);
            activity.startActivity(intent);
            activity.finish();
        }
    }

    public void jsonparse() {
        if ((json != null) && !(json.contains("Internal Server Error"))) {

            try {
                obj = new JSONObject(json);

            } catch (JSONException e) {

            }
            if (obj != null) {
                try {
                    limit = obj.getInt("results");
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }

                if (json1 != null) {
                    try {
                        object = new JSONObject(json1);

                    } catch (JSONException e) {

                    }
                    if (object != null) {
                        try {
                            limit1 = object.getInt("results");
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                if (json1 != null) {
                    if (limit >= results / 2) {
                        limit = results / 2;
                    }
                    if (limit1 >= results / 2) {
                        limit1 = results / 2;
                    }
                } else {
                    if (limit >= results) {
                        limit = results;
                    }
                }

                cost = new String[limit + limit1];
                link = new String[limit + limit1];
                name = new String[limit + limit1];
                site = new String[limit + limit1];
                currency = new String[limit + limit1];
                image = new String[limit + limit1];
                ratings = new float[limit + limit1];
                try {
                    data = obj.getJSONObject("data");
                } catch (JSONException e) {

                }
                if (limit != 0) {
                    int count = 0;
                    for (i = 1; i <= limit; i++) {
                        try {
                            product = data.getJSONObject(String.valueOf(i));
                        } catch (JSONException e) {

                        }
                        if (product != null) {
                            try {
                                if ((product.getString("cost") != "null") && (product.getString("cost") != null)) {
                                    if (minimum <= maximum && maximum > 0) {
                                        if (!product.getString("cost").equals("rinf")) {
                                            if ((Integer.valueOf((int) Float.parseFloat(product.getString("cost").replace("₹", "").replace(",", "").replace(" + tax", "").split(" ")[0])) >= minimum) && (Integer.valueOf((int) Float.parseFloat(product.getString("cost").replace("₹", "").replace(",", "").replace(" + tax", "").split(" ")[0])) <= maximum)) {
                                                cost[count] = product.getString("cost");
                                                if (cost[count].equals("rinf") || cost[count].equals("") || cost[count].contains(" ") || cost[count].equals("null") || cost[count] == null) {
                                                    cost[count] = "Out of stock";
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            cost[count] = product.getString("cost");
                                            if (cost[count].equals("rinf") || cost[count].equals("") || cost[count].contains(" ") || cost[count].equals("null") || cost[count] == null) {
                                                cost[count] = "Out of stock";
                                            }
                                        }
                                    } else {
                                        cost[count] = product.getString("cost");
                                        if (cost[count].equals("rinf") || cost[count].equals("") || cost[count].contains(" ") || cost[count].equals("null") || cost[count] == null) {
                                            cost[count] = "Out of stock";
                                        }
                                    }

                                }
                                try {
                                    if ((product.getString("image") != "null") && (!product.getString("image").contains("null"))) {
                                        image[count] = product.getString("image");
                                    }
                                } catch (JSONException e) {

                                }
                                try {
                                    if (!product.getString("rating").contains("null")) {
                                        ratings[count] = Float.parseFloat(product.getString("rating").substring(0, product.getString("rating").length() - 2));
                                    } else {
                                        ratings[count] = Float.valueOf("0");
                                    }
                                } catch (JSONException e) {

                                }
                                try {
                                    if ((product.getString("link") != "null") && (product.getString("link") != null)) {
                                        link[count] = product.getString("link");
                                    }
                                } catch (JSONException e) {

                                }
                                try {
                                    if ((product.getString("name") != "null") && (product.getString("name") != null)) {
                                        name[count] = product.getString("name");
                                    }
                                } catch (JSONException e) {

                                }
                                try {
                                    if ((product.getString("currency") != "null") && (product.getString("currency") != null)) {
                                        currency[count] = product.getString("currency");
                                    }
                                } catch (JSONException e) {

                                }
                                try {
                                    if ((product.getString("image") != "null") && (product.getString("image") != null)) {
                                        image[count] = product.getString("image");
                                    }
                                } catch (JSONException e) {

                                }
                                try {
                                    if ((product.getString("site") != "null") && (product.getString("site") != null)) {
                                        try {
                                            site[count] = product.getString("site");
                                        } catch (JSONException e) {

                                        }
                                    }
                                } catch (JSONException e) {

                                }
                            } catch (JSONException e) {

                            }
                            count++;
                        }
                    }
                    if (json1 != null) {
                        try {
                            obj = new JSONObject(json1);

                        } catch (JSONException e) {

                        }

                        if (obj != null) {
                            try {
                                data = obj.getJSONObject("data");
                            } catch (JSONException e) {

                            }
                        }
                        for (i = 1; i <= limit1; i++) {
                            try {
                                product = data.getJSONObject(String.valueOf(i));
                            } catch (JSONException e) {

                            }
                            if (product != null) {
                                try {
                                    if ((product.getString("cost") != "null") && (product.getString("cost") != null)) {
                                        if (minimum <= maximum && maximum > 0) {
                                            if (!product.getString("cost").equals("rinf")) {
                                                if ((Integer.valueOf((int) Float.parseFloat(product.getString("cost").replace("₹", "").replace(",", ""))) >= minimum) && (Integer.valueOf((int) Float.parseFloat(product.getString("cost").replace("₹", "").replace(",", ""))) <= maximum)) {
                                                    cost[count] = product.getString("cost");
                                                    if (cost[count].equals("rinf") || cost[count].equals("") || cost[count].contains(" ") || cost[count].equals("null") || cost[count] == null) {
                                                        cost[count] = "Out of stock";
                                                    }

                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                cost[count] = product.getString("cost");
                                                if (cost[count].equals("rinf") || cost[count].equals("") || cost[count].contains(" ") || cost[count].equals("null") || cost[count] == null) {
                                                    cost[count] = "Out of stock";
                                                }
                                            }
                                        } else {
                                            cost[count] = product.getString("cost");
                                            if (cost[count].equals("rinf") || cost[count].equals("") || cost[count].contains(" ") || cost[count].equals("null") || cost[count] == null) {
                                                cost[count] = "Out of stock";
                                            }
                                        }

                                    }
                                    try {
                                        if ((product.getString("image") != "null") && (!product.getString("image").contains("null"))) {
                                            image[count] = product.getString("image");
                                        }
                                    } catch (JSONException e) {

                                    }
                                    try {
                                        if (!product.getString("rating").contains("null")) {
                                            ratings[count] = Float.parseFloat(product.getString("rating").substring(0, product.getString("rating").length() - 2));
                                        } else {
                                            ratings[count] = Float.valueOf("0");
                                        }
                                    } catch (JSONException e) {

                                    }
                                    try {
                                        if ((product.getString("link") != "null") && (product.getString("link") != null)) {
                                            link[count] = product.getString("link");
                                        }
                                    } catch (JSONException e) {

                                    }
                                    try {
                                        if ((product.getString("name") != "null") && (product.getString("name") != null)) {
                                            name[count] = product.getString("name");
                                        }
                                    } catch (JSONException e) {

                                    }
                                    try {
                                        if ((product.getString("currency") != "null") && (product.getString("currency") != null)) {
                                            currency[count] = product.getString("currency");
                                        }
                                    } catch (JSONException e) {

                                    }
                                    try {
                                        if ((product.getString("image") != "null") && (product.getString("image") != null)) {
                                            image[count] = product.getString("image");
                                        }
                                    } catch (JSONException e) {

                                    }
                                    try {
                                        if ((product.getString("site") != "null") && (product.getString("site") != null)) {
                                            try {
                                                site[count] = product.getString("site");
                                            } catch (JSONException e) {

                                            }
                                        }
                                    } catch (JSONException e) {

                                    }
                                } catch (JSONException e) {

                                }
                                count++;
                            }
                        }
                    }
                    if (cost[0] != null) {
                        cost1 = new String[count];
                        name1 = new String[count];
                        link1 = new String[count];
                        site1 = new String[count];
                        image1 = new String[count];
                        ratings1 = new float[count];
                        for (i = 0; i < count; i++) {
                            cost1[i] = cost[i];
                            name1[i] = name[i];
                            link1[i] = link[i];
                            site1[i] = site[i];
                            image1[i] = image[i];
                            ratings1[i] = ratings[i];
                        }
                    }

                    if (count == 0) {
                        errorcode = 1;
                    }
                    if (errorcode != 1) {
                        HttpClient client = new DefaultHttpClient();
                        for (i = 0; i < count; i++) {
                            if (image1[i].equals("null") || image1[i] == null) {
                                String flipkart = link1[i].substring(25);
                                String[] flipkartsearch = new String[1];
                                flipkartsearch[0] = String.valueOf(flipkart.split("/")[0]);
                                HttpGet request = new HttpGet("https://www.google.com/search?tbm=isch&q=" + flipkartsearch[0]);
                                try {
                                    HttpResponse response = client.execute(request);
                                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                                    response.getEntity().writeTo(out);
                                    html = out.toString();
                                    out.close();
                                } catch (IOException e) {
                                }

                                Document document = Jsoup.parse(html);
                                imageBlock11 = document.select("img[class$=t0fcAb]");
                                if (imageBlock11 != null) {
                                    if (imageBlock11.size() >= 1) {
                                        image1[i] = imageBlock11.get(1).attr("src");
                                    }
                                }
                            }
                        }
                    }
                    load.a = false;
                    load.dismiss();
                    Intent intent = new Intent(activity, DisplayActivity.class);
                    intent.putExtra("query", s);
                    intent.putExtra("cat", category);
                    intent.putExtra("name", name1);
                    intent.putExtra("cost", cost1);
                    intent.putExtra("site", site1);
                    intent.putExtra("link", link1);
                    intent.putExtra("image", image1);
                    intent.putExtra("ratings", ratings1);
                    activity.startActivity(intent);
                    finish();

                } else {
                    load.a = false;
                    load.dismiss();
                    Intent intent = new Intent(activity, DisplayActivity.class);
                    intent.putExtra("query", s);
                    intent.putExtra("cat", category);
                    intent.putExtra("name", name1);
                    intent.putExtra("cost", cost1);
                    intent.putExtra("site", site1);
                    intent.putExtra("link", link1);
                    intent.putExtra("image", image1);
                    intent.putExtra("ratings", ratings1);
                    activity.startActivity(intent);
                    finish();

                }
            }
        } else {
            load.a = false;
            load.dismiss();
            Intent intent = new Intent(activity, DisplayActivity.class);
            intent.putExtra("query", s);
            intent.putExtra("cat", category);
            intent.putExtra("name", name1);
            intent.putExtra("cost", cost1);
            intent.putExtra("site", site1);
            intent.putExtra("link", link1);
            intent.putExtra("image", image1);
            intent.putExtra("ratings", ratings1);
            activity.startActivity(intent);
            finish();
        }
    }


}