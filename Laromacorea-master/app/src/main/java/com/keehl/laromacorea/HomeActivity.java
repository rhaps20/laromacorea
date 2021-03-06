package com.keehl.laromacorea;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
;
import com.navdrawer.SimpleSideDrawer;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class HomeActivity extends Activity implements View.OnClickListener {
    private SimpleSideDrawer slideMenu;
    private SimpleSideDrawer slideMatch;

    private Button menuButton;
    private Button matchButton;

    private Button homeButton_menu;
    private Button noticeButton_menu;
    private Button squadButton_menu;
    private Button matchButton_menu;
    private Button calcioButton_menu;
    private Button freeButton_menu;
    private Button specialButton_menu;
    private Button mediaButton_menu;

    private Button leagueTableButton;
    private Button personalTableButton;
    private Button nextMatchButton;

    private HomeListViewAdapter listViewAdapter;
    private TextView noticeTitle;
    private ListView noticeListView;
    private HomeListViewAdapter noticeListViewAdapter;

    private TextView matchTitle;
    private ListView matchListView;
    private HomeListViewAdapter matchListViewAdapter;

    private TextView calcioTitle;
    private ListView calcioListView;
    private HomeListViewAdapter calcioListViewAdapter;

    private TextView freeTitle;
    private ListView freeListView;
    private HomeListViewAdapter freeListViewAdapter;

    private TextView specialTitle;
    private ListView specialListView;
    private HomeListViewAdapter specialListViewAdapter;

    private TextView mediaTitle;
    private ListView mediaListView;
    private HomeListViewAdapter mediaListViewAdapter;
    private String text = null;
    private String sessionId = null;
    private TextView tv;
    private static MAsyncTask readData = null;

    private ImageView homeLogo;
    private ImageView awayLogo;
    private WebView matchInfo;
    private WebView mainWebView;

    private String userInfoStr;
    private String htmlUserInfo;
    private String htmlPageUrl = "http://www.laromacorea.com/index1.html";
    private String htmlTemp;
    private ProgressDialog pDialog;
    private ArrayList<ContentsData> noticeList;
    private ArrayList<ContentsData> clubList;
    private ArrayList<ContentsData> squadList;
    private ArrayList<ContentsData> matchList;
    private ArrayList<ContentsData> calcioList;
    private ArrayList<ContentsData> freeList;
    private ArrayList<ContentsData> specialList;
    private ArrayList<ContentsData> mediaList;


    private GetUserIdTask getUserIdTask;
    private GetLeagueTable getLeagueTable;
    private GetPersonalTable getPersonalTable;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extra = getIntent().getExtras();

        if (savedInstanceState == null) {
            if (extra == null) {
                sessionId = null;
            } else {
                sessionId = extra.getString("sessionId");
                DataContainer.cookies = sessionId;
                //      title = extra.getString("text");
            }
        } else {
            sessionId = savedInstanceState.getString("sessionId");
            DataContainer.cookies = sessionId;
        }

        DataContainer.matchInfoHtml = htmlTemp;

        new Runnable() {
            @Override
            public void run() {
                init();
            }
        }.run();


    }
    public void init() {
        pDialog = new ProgressDialog(getApplicationContext());
        slideMenu = new SimpleSideDrawer(this);
        slideMenu.setLeftBehindContentView(R.layout.slide_menu);

        slideMatch = new SimpleSideDrawer(this);
        slideMatch.setRightBehindContentView(R.layout.slide_game);

    //    mainWebView = (WebView)findViewById(R.id.mainWebView);
        matchInfo = (WebView)findViewById(R.id.matchInfo);

        homeButton_menu = (Button)findViewById(R.id.homeButton_menu);
        homeButton_menu.setOnClickListener(this);
        noticeButton_menu = (Button)findViewById(R.id.noticeButton_menu);
        noticeButton_menu.setOnClickListener(this);
        squadButton_menu= (Button)findViewById(R.id.squadButton_menu);
        squadButton_menu.setOnClickListener(this);
        matchButton_menu = (Button)findViewById(R.id.matchButton_menu);
        matchButton_menu.setOnClickListener(this);
        calcioButton_menu = (Button)findViewById(R.id.calcioButton_menu);
        calcioButton_menu.setOnClickListener(this);
        freeButton_menu =(Button)findViewById(R.id.freeButton_menu);
        freeButton_menu.setOnClickListener(this);
        specialButton_menu = (Button)findViewById(R.id.specialButton_menu);
        specialButton_menu.setOnClickListener(this);
        mediaButton_menu = (Button)findViewById(R.id.mediaButton_menu);
        mediaButton_menu.setOnClickListener(this);

        leagueTableButton = findViewById(R.id.leagueTable);
        leagueTableButton.setOnClickListener(this);
        personalTableButton = findViewById(R.id.personalTable);
        personalTableButton.setOnClickListener(this);
        nextMatchButton = findViewById(R.id.nextMatch);
        nextMatchButton.setOnClickListener(this);

        noticeList = new ArrayList<>();
        clubList = new ArrayList<>();
        squadList = new ArrayList<>();
        matchList = new ArrayList<>();
        calcioList = new ArrayList<>();
        freeList = new ArrayList<>();
        specialList = new ArrayList<>();
        mediaList = new ArrayList<>();

        menuButton = (Button)findViewById(R.id.menuButton_main);
        menuButton.setOnClickListener(this);

        matchButton = (Button)findViewById(R.id.matchButton_main);
        matchButton.setOnClickListener(this);


        noticeListViewAdapter = new HomeListViewAdapter();
        noticeListViewAdapter.setMode(HomeListViewAdapter.HOME_MODE);
        noticeListView = (ListView)findViewById(R.id.listview1);
        noticeTitle = (TextView)findViewById(R.id.boardTitle1);
        noticeTitle.setBackgroundColor(Color.LTGRAY);
        noticeTitle.setOnClickListener(this);
        noticeListView.setAdapter(noticeListViewAdapter);
        ItemClickListener noticeListener = new ItemClickListener();
        noticeListener.setBoardId("notice");
        noticeListView.setOnItemClickListener(noticeListener);

        matchListViewAdapter = new HomeListViewAdapter();
        matchListViewAdapter.setMode(HomeListViewAdapter.HOME_MODE);
        matchListView = (ListView)findViewById(R.id.listview4);
        matchTitle = (TextView)findViewById(R.id.boardTitle4);
        matchTitle.setOnClickListener(this);
        matchTitle.setBackgroundColor(Color.LTGRAY);
        matchListView.setAdapter(matchListViewAdapter);

        ItemClickListener matchListener = new ItemClickListener();
        matchListener.setBoardId("match");
        matchListView.setOnItemClickListener(matchListener);


        calcioListViewAdapter = new HomeListViewAdapter();
        calcioListViewAdapter.setMode(HomeListViewAdapter.HOME_MODE);
        calcioListView = (ListView)findViewById(R.id.listview5);
        calcioTitle = (TextView)findViewById(R.id.boardTitle5);
        calcioTitle.setBackgroundColor(Color.LTGRAY);
        calcioTitle.setOnClickListener(this);
        calcioListView.setAdapter(calcioListViewAdapter);

        ItemClickListener calcioListener = new ItemClickListener();
        calcioListener.setBoardId("calcio");
        calcioListView.setOnItemClickListener(calcioListener);

        freeListViewAdapter = new HomeListViewAdapter();
        freeListViewAdapter.setMode(HomeListViewAdapter.HOME_MODE);
        freeListView = (ListView)findViewById(R.id.listview6);
        freeTitle = (TextView)findViewById(R.id.boardTitle6);
        freeTitle.setOnClickListener(this);
        freeTitle.setBackgroundColor(Color.LTGRAY);
        freeListView.setAdapter(freeListViewAdapter);

        ItemClickListener freeListener = new ItemClickListener();
        freeListener.setBoardId("free");
        freeListView.setOnItemClickListener(freeListener);


        specialListViewAdapter = new HomeListViewAdapter();
        specialListViewAdapter.setMode(HomeListViewAdapter.HOME_MODE);
        specialListView = (ListView)findViewById(R.id.listview7);
        specialTitle = (TextView)findViewById(R.id.boardTitle7);
        specialTitle.setBackgroundColor(Color.LTGRAY);
        specialTitle.setOnClickListener(this);
        specialListView.setAdapter(specialListViewAdapter);

        ItemClickListener specialListener = new ItemClickListener();
        specialListener.setBoardId("sp");
        specialListView.setOnItemClickListener(specialListener);

        mediaListViewAdapter = new HomeListViewAdapter();
        mediaListViewAdapter.setMode(HomeListViewAdapter.HOME_MODE);
        mediaListView = (ListView)findViewById(R.id.listview8);
        mediaTitle = (TextView)findViewById(R.id.boardTitle8);
        mediaTitle.setOnClickListener(this);
        mediaTitle.setBackgroundColor(Color.LTGRAY);
        mediaListView.setAdapter(mediaListViewAdapter);

        ItemClickListener mediaListener = new ItemClickListener();
        mediaListener.setBoardId("media");
        mediaListView.setOnItemClickListener(mediaListener);

        readData = new MAsyncTask();
        readData.execute();

        getUserIdTask = new GetUserIdTask();
        getUserIdTask.execute();

        getLeagueTable = new GetLeagueTable();
        getLeagueTable.execute();

        getPersonalTable = new GetPersonalTable();
        getPersonalTable.execute();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        Intent it;

        switch(id) {
            case R.id.menuButton_main:
                slideMenu.toggleLeftDrawer();
                break;
            case R.id.matchButton_main:
            //    slideMatch.toggleRightDrawer();
                slideMatch.toggleRightDrawer();;
                break;
            case R.id.homeButton_menu:
                it = new Intent(this, HomeActivity.class);

                it.putExtra("url", "http://www.laromacorea.com/index1.html");
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", "Home");
                startActivity(it);
                finish();
                break;
            case R.id.noticeButton_menu:
                it = new Intent(this, BoardActivity.class);
                it.putExtra("url", "http://www.laromacorea.com/bbs/zboard.php?id=notice");
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", "Notice");
                it.putExtra("page", 1);
                startActivity(it);
                finish();
                break;
            case R.id.squadButton_menu:
                it = new Intent(this, BoardActivity.class);
                it.putExtra("url", "http://www.laromacorea.com/bbs/zboard.php?id=squad");
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", "Squad");
                it.putExtra("page", 1);
                startActivity(it);
                finish();
                break;
            case R.id.matchButton_menu:
                it = new Intent(this, BoardActivity.class);
                it.putExtra("url", "http://www.laromacorea.com/bbs/zboard.php?id=1213match");
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", "Match");
                it.putExtra("page", 1);
                startActivity(it);
                finish();
                break;
            case R.id.calcioButton_menu:
                it = new Intent(this, BoardActivity.class);
                it.putExtra("url", "http://www.laromacorea.com/bbs/zboard.php?id=calcio");
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", "Calcio");
                it.putExtra("page", 1);
                startActivity(it);
                finish();
                break;
            case R.id.freeButton_menu:
                it = new Intent(this, BoardActivity.class);

                it.putExtra("url", "http://www.laromacorea.com/bbs/zboard.php?id=free2");
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", "Free");
                it.putExtra("page", 1);
                startActivity(it);
                finish();
                // finish();
                break;
            case R.id.specialButton_menu:
                it = new Intent(this, BoardActivity.class);
                it.putExtra("url", "http://www.laromacorea.com/bbs/zboard.php?id=sp");
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", "Special");
                it.putExtra("page", 1);
                startActivity(it);
                finish();
                break;
            case R.id.mediaButton_menu:
                it = new Intent(this, BoardActivity.class);
                it.putExtra("url", "http://www.laromacorea.com/bbs/zboard.php?id=media");
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", "Media");
                it.putExtra("page", 1);
                startActivity(it);
                finish();
                break;

            case R.id.leagueTable:
                new MatchDialog(this, DataContainer.leagueTableHtml).show();
                break;
            case R.id.personalTable:
                new PersonalTableDialog(this, DataContainer.playerInfoHtml).show();
                break;
            case R.id.nextMatch:
                new NextMatchDialog(this, DataContainer.matchInfoHtml).show();
                break;
            case R.id.boardTitle1:
                it = new Intent(this, BoardActivity.class);
                it.putExtra("url", "http://www.laromacorea.com/bbs/zboard.php?id=notice");
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", "Notice");
                it.putExtra("page", 1);
                startActivity(it);
                finish();
                break;
            case R.id.boardTitle4:
                it = new Intent(this, BoardActivity.class);
                it.putExtra("url", "http://www.laromacorea.com/bbs/zboard.php?id=1213match");
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", "Match");
                it.putExtra("page", 1);
                startActivity(it);
                finish();
                break;
            case R.id.boardTitle5:
                it = new Intent(this, BoardActivity.class);
                it.putExtra("url", "http://www.laromacorea.com/bbs/zboard.php?id=calcio");
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", "Calcio");
                it.putExtra("page", 1);
                startActivity(it);
                finish();
                break;
            case R.id.boardTitle6:
                it = new Intent(this, BoardActivity.class);
                it.putExtra("url", "http://www.laromacorea.com/bbs/zboard.php?id=free2");
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", "Free");
                it.putExtra("page", 1);
                startActivity(it);
                finish();
                break;
            case R.id.boardTitle7:
                it = new Intent(this, BoardActivity.class);
                it.putExtra("url", "http://www.laromacorea.com/bbs/zboard.php?id=sp");
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", "Special");
                it.putExtra("page", 1);
                startActivity(it);
                finish();
                break;
            case R.id.boardTitle8:
                it = new Intent(this, BoardActivity.class);
                it.putExtra("url", "http://www.laromacorea.com/bbs/zboard.php?id=media");
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", "Media");
                it.putExtra("page", 1);
                startActivity(it);
                finish();
                break;
        }
    }
    public class GetUserIdTask extends AsyncTask<Void, Void, Void>{
        private ProgressDialog pDialog;
        private int i = 0;
        private String str;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(HomeActivity.this);

            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... integers) {
            try {
                Document doc = Jsoup.connect(htmlPageUrl).cookie("PHPSESSID", sessionId).get();
/********************************************************************/

                Elements userInfo = doc.getElementsByTag("b");
                UserInfo.userId = userInfo.get(0).html();

                UserInfo.userId = UserInfo.userId.split("<b>")[1].split("</b>")[0];
            } catch (Exception ex) {

            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... params) {

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pDialog.cancel();
        }
    }

    public class MAsyncTask extends AsyncTask<Void, Void, Void> {
        private String boardId;
        private ProgressDialog pDialog;
        private int i = 0;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(HomeActivity.this);

            pDialog.show();
        }
        public void setBoardId(String boardId) {
            this.boardId = boardId;
        }

        @Override
        protected Void doInBackground(Void... integers) {
            try {
            //    pDialog.show();

                //     pDialog.getProgressBar().setProgress(i);
                Connection.Request request = Jsoup.connect("http://laromacorea.com/index1.html")
                        .cookie("PHPSESSID", "sessionId").request();
                Document matchDoc = Jsoup.connect(htmlPageUrl).get();
                Elements matchInfo = matchDoc.select("td[width=204]");

                for (Element ele : matchInfo ) {
                    htmlTemp += ele.html();
                }

                String matchHtml = "";

                for (int i = 4; i < htmlTemp.length() - 1; i++) {
                    matchHtml += htmlTemp.charAt(i);
                }
                DataContainer.matchInfoHtml = "";
                DataContainer.matchInfoHtml += matchHtml;

                Document doc = Jsoup.connect(htmlPageUrl).get();

                Elements links = doc.select("a[href]");
                Elements com = links.next();

                int i = 0;
                for (Element link : links) {
                    String temp = new String((link.attr("abs:href")
                            + "("+link.text().trim() + ")\n"));

                    Element ele = link.nextElementSibling();

                    ContentsData contents = new ContentsData();

                    contents.str = temp;
                    contents.url = temp.split("\n")[0];

                    if (ele != null) contents.comments = ele.text();
                    else contents.comments = "";

                    if (temp.contains("notice")) {
                        noticeList.add(contents);
                    } else if (temp.contains("club")) {
                        clubList.add(contents);
                    } else if (temp.contains("squad")) {
                        squadList.add(contents);
                    } else if (temp.contains("match")) {
                        matchList.add(contents);
                    } else if (temp.contains("calcio")) {
                        calcioList.add(contents);
                    } else if (temp.contains("free")) {
                        freeList.add(contents);
                    } else if (temp.contains("media")) {
                        mediaList.add(contents);
                    } else if (temp.contains("sp")) {
                        specialList.add(contents);
                    }

                }

            }catch (IOException io) {
                Log.d("HOME ACTIVITY", io.getMessage());
            }
            if(noticeList.size() > 0) noticeList.remove(0);
            if(matchList.size() > 0) matchList.remove(0);
            if(calcioList.size() > 0)calcioList.remove(0);
            if(freeList.size() > 0)freeList.remove(0);
            if(specialList.size() > 0)specialList.remove(0);
            if(mediaList.size() > 0)mediaList.remove(0);

            if(noticeList.size() > 0) noticeList.remove(0);
            if(matchList.size() > 0) matchList.remove(0);
            if(calcioList.size() > 0)calcioList.remove(0);
            if(freeList.size() > 0)freeList.remove(0);
            if(specialList.size() > 0)specialList.remove(0);
            if(mediaList.size() > 0)mediaList.remove(0);


            return null;
        }

        @Override
        protected void onProgressUpdate(Void... params) {

            pDialog.getProgressBar().setProgress(i);
            i++;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

       //     mainWebView.loadDataWithBaseURL(null, htmlUserInfo, "text/html", "utf-8", null);

            String tempStr;
            for (int i = 0; i < noticeList.size(); i++) {
                if (i == 0) UserInfo.noticeBoardLastPost = getLastPostNum(noticeList.get(i).url);
                tempStr = getContentsFromUrl(noticeList.get(i).str);
                noticeList.get(i).str = tempStr;
                ContentsData contentsData = new ContentsData();
                contentsData.str = tempStr;
                contentsData.url = noticeList.get(i).url;
                contentsData.comments = noticeList.get(i).comments;
                if (tempStr.equals("") == false && tempStr.equals("Notice") == false) {
                    noticeListViewAdapter.addItem(null, tempStr, contentsData, "id", "11/10", null, null,false);
                    noticeListViewAdapter.notifyDataSetChanged();
                }
            }

            for (int i = 0; i < matchList.size(); i++) {
                if (i == 0) UserInfo.matchBoardLastPost = getLastPostNum(matchList.get(i).url);
                tempStr = getContentsFromUrl(matchList.get(i).str);
                matchList.get(i).str = tempStr;
                if (tempStr.equals("") == false && tempStr.equals("Match") == false) {
                    ContentsData contentsData = new ContentsData();
                    contentsData.str = tempStr;
                    contentsData.url = matchList.get(i).url;
                    contentsData.comments = matchList.get(i).comments;
                    matchListViewAdapter.addItem(null, tempStr, contentsData, "id", "11/10", null, null,false);
                    matchListViewAdapter.notifyDataSetChanged();
                }
            }

            for (int i = 0; i < calcioList.size(); i++) {
                if (i == 0) UserInfo.calcioBoardLastPost = getLastPostNum(calcioList.get(i).url);
                tempStr = getContentsFromUrl(calcioList.get(i).str);
                calcioList.get(i).str = tempStr;
                if (tempStr.equals("") == false && tempStr.equals("Calcio") == false) {

                    ContentsData contentsData = new ContentsData();
                    contentsData.str = tempStr;
                    contentsData.url = calcioList.get(i).url;
                    contentsData.comments = calcioList.get(i).comments;
                    calcioListViewAdapter.addItem(null, tempStr, contentsData, "id", "11/10", null, null,false);
                    calcioListViewAdapter.notifyDataSetChanged();
                }
            }

            for (int i = 0; i < freeList.size(); i++) {
                if (i == 0) UserInfo.freeBoardLastPost = getLastPostNum(freeList.get(i).url);
                tempStr = getContentsFromUrl(freeList.get(i).str);
                freeList.get(i).str = tempStr;
                if (tempStr.equals("") == false && tempStr.equals("Free") == false) {

                    ContentsData contentsData = new ContentsData();
                    contentsData.str = tempStr;
                    contentsData.url = freeList.get(i).url;
                    contentsData.comments = freeList.get(i).comments;
                    freeListViewAdapter.addItem(null, tempStr, contentsData, "id", "11/10", null, null,false);
                    freeListViewAdapter.notifyDataSetChanged();

                }
            }

            for (int i = 0; i < specialList.size(); i++) {
                if (i == 0) UserInfo.specialBoardLastPost = getLastPostNum(specialList.get(i).url);
                tempStr = getContentsFromUrl(specialList.get(i).str);
                specialList.get(i).str = tempStr;
                if (tempStr.equals("") == false && tempStr.equals("Special") == false) {

                    ContentsData contentsData = new ContentsData();
                    contentsData.str = tempStr;
                    contentsData.url = specialList.get(i).url;
                    contentsData.comments = specialList.get(i).comments;
                    specialListViewAdapter.addItem(null, tempStr, contentsData, "id", "11/10", null, null,false);
                    specialListViewAdapter.notifyDataSetChanged();
                }
            }

            for (int i = 0; i < mediaList.size(); i++) {
                if (i == 0) UserInfo.mediaBoardLastPost = getLastPostNum(mediaList.get(i).url);
                tempStr = getContentsFromUrl(mediaList.get(i).str);
                mediaList.get(i).str = tempStr;
                if (tempStr.equals("") == false && tempStr.equals("Media") == false) {

                    ContentsData contentsData = new ContentsData();
                    contentsData.str = tempStr;
                    contentsData.url = mediaList.get(i).url;
                    contentsData.comments = mediaList.get(i).comments;
                    mediaListViewAdapter.addItem(null, tempStr, contentsData, "id", "11/10", null, null,false);
                    mediaListViewAdapter.notifyDataSetChanged();
                }
            }


            setListViewHeightBasedOnChildren(noticeListView);
            setListViewHeightBasedOnChildren(matchListView);
            setListViewHeightBasedOnChildren(calcioListView);
            setListViewHeightBasedOnChildren(freeListView);
            setListViewHeightBasedOnChildren(specialListView);
            setListViewHeightBasedOnChildren(mediaListView);

        //    matchInfo.loadData(htmlTemp, "text/html", "UTF-8");
        //    pDialog.cancel();
            pDialog.cancel();
        }


    }
    /*league information*/

    public class GetLeagueTable extends AsyncTask<Void, Void, Void>{
        private ProgressDialog pDialog;
        private String leaugeTable = "https://serieamania.com/";
        private int i = 0;
        private String str;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(HomeActivity.this);

            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... integers) {
            try {
                Document doc = Jsoup.connect(leaugeTable).get();
/********************************************************************/
                Elements ele = doc.select("table[class=standings_table box_shadow_3]").select("tbody").select("tr");

                DataContainer.leagueTableHtml = ele.html();
            } catch (Exception ex) {

            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... params) {

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pDialog.cancel();
        }
    }


    public class GetPersonalTable extends AsyncTask<Void, Void, Void>{
        private ProgressDialog pDialog;
        private String personalTable = "https://sports.news.naver.com/wfootball/record/index.nhn?category=seria&tab=player";
        private String personalTableHtml = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(HomeActivity.this);

            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... integers) {
            try {
                Document doc = Jsoup.connect(personalTable).get();
/********************************************************************/
                Elements total = doc.select("div[id=wfootballPlayerRecordBody]")
                        .select("table").select("tbody");

                Elements rows = total.select("tr");

                int size = rows.size();


                personalTableHtml = "<h2 style=\"text-align: center;\"><b>Personal Record</b></h2>";
                personalTableHtml += "<br><br><br>";

                for (int i = 0; i < size; i++) {
                    Elements elements = rows.get(i).select("td");
                    for (int j = 0; j < 5; j++) {
                        if (j == 0) personalTableHtml += "<b>";
                        personalTableHtml += elements.get(j).text() + " ";
                        if (j == 0) personalTableHtml += "</b>";
                    }
                    personalTableHtml += "<br><br>";
                }


            } catch (Exception ex) {

            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... params) {

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pDialog.cancel();
            DataContainer.playerInfoHtml = personalTableHtml;
        }
    }
    public String getContentsFromUrl(String url) {

        char[] arrayOfChar = url.toCharArray();

        int i = 0;

        while (i < url.length()) {
            if (arrayOfChar[i] == '(') {
                arrayOfChar[i]='^';
                break;
            }
            i += 1;
        }

        return new String(arrayOfChar).split("\\^")[1].split("\\)\n")[0];
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public class ItemClickListener implements AdapterView.OnItemClickListener {
        private String str;
        private ArrayList<ContentsData> list;

        public void setBoardId(String boardId) {
            str = boardId;

            if (boardId.equals("notice")) {
                list = noticeList;


            } else if (boardId.equals("club")) {
                list = clubList;
            } else if (boardId.equals("match")) {
                list = matchList;
            } else if (boardId.equals("squad")) {
                list = squadList;
            } else if (boardId.equals("calcio")) {
                list = calcioList;
            } else if (boardId.equals("free")) {
                list = freeList;
            } else if (boardId.equals("sp")) {
                list = specialList;
            } else if (boardId.equals("media")) {
                list = mediaList;
            }
        }

        public void onItemClick(AdapterView<?> parentView, View clickedView, int position, long id) {
            Intent it = new Intent(HomeActivity.this, ContentActivity.class);

            String text = list.get(position).str;
            String url = list.get(position).url;

            it.putExtra("text", text);
            it.putExtra("url", url);
            it.putExtra("sessionId", sessionId);
            it.putExtra("boardId", str);
            startActivity(it);
            finish();
        }
    }
    int i = 0;
    @Override
    public void onBackPressed() {
    //    super.onBackPressed();
        i++;
        if (i >= 2) {
            super.onBackPressed();
            android.os.Process.killProcess(android.os.Process.myPid());
            Utils.showToast(this, "어플리케이션을 종료합니다.", Toast.LENGTH_LONG);
        } else {
            Utils.showToast(this, "빠르게 두번 터치하면 종료합니다.", Toast.LENGTH_LONG);
        }

        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(3000);
                    i = 0;
                } catch(Exception ex) {
                    Log.d("Runnable", ex.getMessage());
                }

            }
        }).start();
    }

    public int getLastPostNum(String url) {
        String test = url.split("no=")[1];
        test = test.split("\\(")[0];

        int postNum = Integer.parseInt(test);

        return postNum;
    }
}
