package io.github.kelvao.cloudflarescrape;

import android.util.Log;

import com.eclipsesource.v8.V8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class CloudflareScrapeCore {

    private String UA;
    private final String url;

    CloudflareScrapeCore(String url) {
        this.url = url;
    }

    public List<HttpCookie> cookies() {
        CookieManager cm = new CookieManager();
        cm.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cm);

        try {
            URL ConnUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) ConnUrl.openConnection();
            conn.setRequestProperty("User-Agent", getUA());
            conn.connect();
            if (conn.getResponseCode() == 503) {
                InputStream is = conn.getErrorStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String str;
                while ((str = br.readLine()) != null) {
                    sb.append(str);
                }
                is.close();
                br.close();
                CookieStore ck = cm.getCookieStore();
                conn.disconnect();
                str = sb.toString();
                System.out.println(str);
                String jschl_vc = Objects.requireNonNull(regex(str, "name=\"jschl_vc\" value=\"(.+?)\"")).get(0);
                String pass = Objects.requireNonNull(regex(str, "name=\"pass\" value=\"(.+?)\"")).get(0);
                int jschl_answer = get_answer(str);
                System.out.println(jschl_answer);

                Thread.sleep(4000);

                String req = String.valueOf("https://" + ConnUrl.getHost()) + "/cdn-cgi/l/chk_jschl?"
                        + "jschl_vc=" + jschl_vc + "&pass=" + pass + "&jschl_answer=" + jschl_answer;
                System.out.println(req);
                HttpURLConnection.setFollowRedirects(false);
                HttpURLConnection reqconn = (HttpURLConnection) new URL(req).openConnection();

                reqconn.setRequestProperty("Referer", req);
                reqconn.setRequestProperty("User-Agent", getUA());
                reqconn.setRequestProperty("Cookie", ck.getCookies().toString());
                reqconn.connect();
                if (reqconn.getResponseCode() == 302) {
                    CookieStore ck1 = cm.getCookieStore();
                    reqconn.disconnect();
                    System.out.println(reqconn.getHeaderFields());

                    HttpURLConnection conn302 = (HttpURLConnection) new URL(req).openConnection();
                    conn302.setRequestProperty("Referer", ConnUrl.getHost());
                    conn302.setRequestProperty("User-Agent", getUA());
                    conn302.setRequestProperty("Cookie", ck1.getCookies().toString());
                    conn302.connect();
                    if (conn302.getResponseCode() == 302) {
                        System.out.println("conn302:302");
                        Log.i("conn302", conn302.getHeaderFields().toString());
                        CookieStore ck2 = cm.getCookieStore();
                        Log.i("conn302", ck2.getCookies().toString());
                        conn302.disconnect();
                        return ck2.getCookies();
                    }
                }
            } else {
                CookieStore cookieStore = cm.getCookieStore();
                return cookieStore.getCookies();

            }
        } catch (NullPointerException e) {
            Log.e("Err", "Must set UA");
        } catch (IndexOutOfBoundsException e) {
            CookieStore cookieStore = cm.getCookieStore();
            return cookieStore.getCookies();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int get_answer(String str) {
        int a = 0;

        try {
            List<String> s = regex(str, "var s,t,o,p,b,r,e,a,k,i,n,g,f, (.+?)=\\{\"(.+?)\"");
            System.out.println(s);
            String varA = Objects.requireNonNull(s).get(0);
            String varB = s.get(1);
            StringBuilder sb = new StringBuilder();
            sb.append("a=");
            sb.append(Objects.requireNonNull(regex(str, varA + "=\\{\"" + varB + "\":(.+?)\\}")).get(0));
            sb.append(";");
            List<String> b = regex(str, varA + "\\." + varB + "(.+?)\\;");
            for (int i = 0; i < Objects.requireNonNull(b).size() - 1; i++) {
                sb.append("a");
                sb.append(b.get(i));
                sb.append(";");
            }
            Log.i("add", sb.toString());
            V8 v8 = V8.createV8Runtime();
            a = v8.executeIntegerScript(sb.toString());

            a += new URL(url).getHost().length();
        } catch (IndexOutOfBoundsException e) {
            Log.e("answerErr", "get answer error");
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return a;
    }

    private String getUA() {
        return UA == null ? DefaultUserAgents.getDefaultUserAgents() : UA;
    }

    public void setUA(String UA) {
        this.UA = UA;
    }

    private List<String> regex(String text, String pattern) {    //正则
        try {
            Pattern pt = Pattern.compile(pattern);
            Matcher mt = pt.matcher(text);
            List<String> group = new ArrayList<>();

            while (mt.find()) {
                if (mt.groupCount() >= 1) {
                    if (mt.groupCount() > 1) {
                        group.add(mt.group(1));
                        group.add(mt.group(2));
                    } else {
                        group.add(mt.group(1));
                    }
                }
            }
            return group;
        } catch (NullPointerException e) {
            Log.i("MATCH", "null");
        }
        return null;
    }

    public HashMap<String, String> Cookies2HashMap(List<HttpCookie> list) {
        HashMap<String, String> map = new HashMap<>();
        try {
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    String[] listStr = list.get(i).toString().split("=");
                    map.put(listStr[0], listStr[1]);
                }
                Log.i("CloudflareScrapeCore", map.toString());
            } else return map;

        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return map;
    }

}





