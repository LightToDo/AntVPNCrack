package com.guangping.invite;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guangping.*;
import com.guangping.pojo.Data;
import com.guangping.pojo.InternetAddress;
import com.guangping.pojo.Package;
import com.guangping.pojo.RequestParameter;
import com.guangping.post.AntRequestPackage;
import com.guangping.post.AntResponsePackage;
import com.guangping.post.DataHandler;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.isNull;
import static org.jsoup.Connection.Method.GET;

/**
 * @author GuangPing Lin
 * @date 2021/2/25 0:08
 */
public class InviteTask implements Runnable {

    private static final String AFF_CODE = "aff_code";
    private static final String INVITED_NUM = "invited_num";
    private static final String HOME = "home";
    public static final int COUNT = 36500;

    private static final File EXCEPTION_FILE = new File("src/main/resources/exception.txt");
    private static final File OUT_FILE = new File("src/main/resources/result.txt");
    private static final File REQUEST_FILE = new File("src/main/resources/request.json");

    private static final String EXCHANGE_AFF = "exchangeAFF";
    public static final String MSG = "msg";
    private static final String USER = "user";

    public static final String SUCCESS = "成功";
    public static final String EXPIRE_DESCRIPTION = "expire_description";

    private static final String ZONE = "Asia/Shanghai";
    private static final String INFO = "距离获取永久还剩";
    private static final String SEPARATOR = ", ";

    private static final String IP_URL = "http://api.xiequ.cn/VAD/GetIp.aspx?act=getturn61&num=1&time=6&plat=0&re=0&type=7&so=1&group=51&ow=1&spl=1&db=1";
    private static final String FLAG = "-------------------------------------------------------------";

    public static Package p;

    private static final String TEST_URL = "http://ip.3322.net/";

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static final Map<String, Map<String, List<String>>> UA_LIST = new HashMap<>(50);
    private static final List<String> UA_TYPE_LIST = new ArrayList<>(3);

    private static final Random UA_RANDOM = new Random();

    static {
        UA_TYPE_LIST.add("PC");
        UA_TYPE_LIST.add("IOS");
        UA_TYPE_LIST.add("ANDROID");

        Map<String, List<String>> pcUa = new HashMap<>();

        List<String> opera = new ArrayList<>();
        opera.add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36 OPR/26.0.1656.60");
        opera.add("Opera/8.0 (Windows NT 5.1; U; en)");
        opera.add("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; en) Opera 9.50");
        opera.add("Mozilla/5.0 (Windows NT 5.1; U; en; rv:1.8.1) Gecko/20061208 Firefox/2.0.0 Opera 9.50");
        pcUa.put("Opera", opera);

        List<String> firefox = new ArrayList<>();
        firefox.add("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:34.0) Gecko/20100101 Firefox/34.0");
        firefox.add("Mozilla/5.0 (X11; U; Linux x86_64; zh-CN; rv:1.9.2.10) Gecko/20100922 Ubuntu/10.10 (maverick) Firefox/3.6.10");
        pcUa.put("Firefox", firefox);

        List<String> safari = new ArrayList<>();
        safari.add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.57.2 (KHTML, like Gecko) Version/5.1.7 Safari/534.57.2");
        pcUa.put("Safari", safari);

        List<String> chrome = new ArrayList<>();
        chrome.add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36");
        chrome.add("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11");
        chrome.add("Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US) AppleWebKit/534.16 (KHTML, like Gecko) Chrome/10.0.648.133 Safari/534.16");
        pcUa.put("Chrome", chrome);

        List<String> threeZero = new ArrayList<>();
        threeZero.add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.101 Safari/537.36");
        threeZero.add("Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko");
        pcUa.put("360", threeZero);

        List<String> taoBao = new ArrayList<>();
        taoBao.add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.11 (KHTML, like Gecko) Chrome/20.0.1132.11 TaoBrowser/2.0 Safari/536.11");
        pcUa.put("TaoBao", taoBao);

        List<String> lieBao = new ArrayList<>();
        lieBao.add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.71 Safari/537.1 LBBROWSER");
        lieBao.add("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; LBBROWSER)");
        lieBao.add("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 732; .NET4.0C; .NET4.0E; LBBROWSER)");
        pcUa.put("LieBao", lieBao);

        List<String> qq = new ArrayList<>();
        qq.add("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; QQBrowser/7.0.3698.400)");
        qq.add("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 732; .NET4.0C; .NET4.0E)");
        pcUa.put("QQ", qq);

        List<String> soGou = new ArrayList<>();
        soGou.add("Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.84 Safari/535.11 SE 2.X MetaSr 1.0");
        soGou.add("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; SV1; QQDownload 732; .NET4.0C; .NET4.0E; SE 2.X MetaSr 1.0)");
        pcUa.put("SoGou", soGou);

        List<String> maxThon = new ArrayList<>();
        maxThon.add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Maxthon/4.4.3.4000 Chrome/30.0.1599.101 Safari/537.36");
        pcUa.put("MaxThon", maxThon);

        List<String> uc = new ArrayList<>();
        uc.add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.122 UBrowser/4.0.3214.0 Safari/537.36");
        pcUa.put("UC", uc);

        List<String> pcUniversal = new ArrayList<>();
        pcUniversal.add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/22.0.1207.1 Safari/537.1");
        pcUniversal.add("Mozilla/5.0 (X11; CrOS i686 2268.111.0) AppleWebKit/536.11 (KHTML, like Gecko) Chrome/20.0.1132.57 Safari/536.11");
        pcUniversal.add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.6 (KHTML, like Gecko) Chrome/20.0.1092.0 Safari/536.6");
        pcUniversal.add("Mozilla/5.0 (Windows NT 6.2) AppleWebKit/536.6 (KHTML, like Gecko) Chrome/20.0.1090.0 Safari/536.6");
        pcUniversal.add("Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/19.77.34.5 Safari/537.1");
        pcUniversal.add("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/536.5 (KHTML, like Gecko) Chrome/19.0.1084.9 Safari/536.5");
        pcUniversal.add("Mozilla/5.0 (Windows NT 6.0) AppleWebKit/536.5 (KHTML, like Gecko) Chrome/19.0.1084.36 Safari/536.5");
        pcUniversal.add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1063.0 Safari/536.3");
        pcUniversal.add("Mozilla/5.0 (Windows NT 5.1) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1063.0 Safari/536.3");
        pcUniversal.add("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_0) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1063.0 Safari/536.3");
        pcUniversal.add("Mozilla/5.0 (Windows NT 6.2) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1062.0 Safari/536.3");
        pcUniversal.add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1062.0 Safari/536.3");
        pcUniversal.add("Mozilla/5.0 (Windows NT 6.2) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1061.1 Safari/536.3");
        pcUniversal.add("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1061.1 Safari/536.3");
        pcUniversal.add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1061.1 Safari/536.3");
        pcUniversal.add("Mozilla/5.0 (Windows NT 6.2) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1061.0 Safari/536.3");
        pcUniversal.add("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.24 (KHTML, like Gecko) Chrome/19.0.1055.1 Safari/535.24");
        pcUniversal.add("Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/535.24 (KHTML, like Gecko) Chrome/19.0.1055.1 Safari/535.24");
        pcUa.put("Universal", pcUniversal);

        UA_LIST.put("PC", pcUa);

        Map<String, List<String>> iosUa = new HashMap<>();

        List<String> iPhone = new ArrayList<>();
        iPhone.add("Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_3_3 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8J2 Safari/6533.18.5");
        iosUa.put("IPhone", iPhone);

        List<String> iPod = new ArrayList<>();
        iPod.add("Mozilla/5.0 (iPod; U; CPU iPhone OS 4_3_3 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8J2 Safari/6533.18.5");
        iosUa.put("IPod", iPod);

        List<String> iPad = new ArrayList<>();
        iPad.add("Mozilla/5.0 (iPad; U; CPU OS 4_2_1 like Mac OS X; zh-cn) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8C148 Safari/6533.18.5");
        iPad.add("Mozilla/5.0 (iPad; U; CPU OS 4_3_3 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8J2 Safari/6533.18.5");
        iosUa.put("IPad", iPad);

        UA_LIST.put("IOS", iosUa);

        Map<String, List<String>> androidUa = new HashMap<>();

        List<String> android = new ArrayList<>();
        android.add("Mozilla/5.0 (Linux; U; Android 2.2.1; zh-cn; HTC_Wildfire_A3333 Build/FRG83D) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
        android.add("Mozilla/5.0 (Linux; U; Android 2.3.7; en-us; Nexus One Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
        android.add("Mozilla/5.0 (Linux; U; Android 7.0; zh-cn; M5 Note Build/NRD90M) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Mobile Safari/537.36");
        androidUa.put("Universal", android);

        List<String> qqAndroid = new ArrayList<>();
        qqAndroid.add("MQQBrowser/26 Mozilla/5.0 (Linux; U; Android 2.3.7; zh-cn; MB200 Build/GRJ22; CyanogenMod-7) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
        androidUa.put("QQ", qqAndroid);

        List<String> operaAndroid = new ArrayList<>();
        operaAndroid.add("Opera/9.80 (Android 2.3.4; Linux; Opera Mobi/build-1107180945; U; en-GB) Presto/2.8.149 Version/11.10");
        androidUa.put("Opera", operaAndroid);

        List<String> blackBerry = new ArrayList<>();
        blackBerry.add("Mozilla/5.0 (BlackBerry; U; BlackBerry 9800; en) AppleWebKit/534.1+ (KHTML, like Gecko) Version/6.0.0.337 Mobile Safari/534.1+");
        androidUa.put("BlackBerry", blackBerry);

        List<String> noKia = new ArrayList<>();
        noKia.add("Mozilla/5.0 (SymbianOS/9.4; Series60/5.0 NokiaN97-1/20.0.019; Profile/MIDP-2.1 Configuration/CLDC-1.1) AppleWebKit/525 (KHTML, like Gecko) BrowserNG/7.1.18124");
        androidUa.put("Nokia", noKia);

        List<String> moTo = new ArrayList<>();
        moTo.add("Mozilla/5.0 (Linux; U; Android 3.0; en-us; Xoom Build/HRI39) AppleWebKit/534.13 (KHTML, like Gecko) Version/4.0 Safari/534.13");
        androidUa.put("Moto", moTo);

        List<String> hpTouchPad = new ArrayList<>();
        hpTouchPad.add("Mozilla/5.0 (hp-tablet; Linux; hpwOS/3.0.0; U; en-US) AppleWebKit/534.6 (KHTML, like Gecko) wOSBrowser/233.70 Safari/534.6 TouchPad/1.0");
        androidUa.put("HpTouchPad", hpTouchPad);

        List<String> ucAndroid = new ArrayList<>();
        ucAndroid.add("UCWEB7.0.2.37/28/999");
        ucAndroid.add("NOKIA5700/ UCWEB7.0.2.37/28/999");
        ucAndroid.add("Openwave/ UCWEB7.0.2.37/28/999");
        ucAndroid.add("Mozilla/4.0 (compatible; MSIE 6.0; ) Opera/UCWEB7.0.2.37/28/999");
        androidUa.put("UC", ucAndroid);

        List<String> windowsPhone = new ArrayList<>();
        windowsPhone.add("Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0; HTC; Titan)");
        androidUa.put("WindowsPhone", windowsPhone);

        UA_LIST.put("ANDROID", androidUa);
    }

    private static final ThreadLocal<Connection> TIMEOUT = new ThreadLocal<>();
    private static final ThreadLocal<Connection> GET_IP_CONNECT = new ThreadLocal<>();
    private static final ThreadLocal<Connection> CONNECTION = new ThreadLocal<>();

    static {
        try {
            p = OBJECT_MAPPER.readValue(REQUEST_FILE, Package.class);
        } catch (IOException e) {
            exception("读取请求包文件异常", e);
        }
    }

    private String oauthId;

    public InviteTask(String oauthId) {
        this.oauthId = oauthId;
    }

    @Override
    public void run() {

        boolean fail;
        do {
            try {
                invite();
                fail = false;
            } catch (Exception e) {
                fail = true;
                exception(oauthId + ", 邀请异常", e);
            }
        } while (fail);
    }

    public static void exception(String message, Exception e) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(EXCEPTION_FILE, true))))) {
            synchronized (EXCEPTION_FILE) {
                out.println(message);
                if (!isNull(e)) {
                    out.print(Instant.now() + ", ");
                    out.println(FLAG);
                    e.printStackTrace(out);
                    out.println(FLAG);
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public static void setConnection() {
        if (isNull(CONNECTION.get())) {
            Connection connect = Jsoup.connect(p.getUrlString()).postDataCharset("UTF-8");
            connect.header("Connection", "keep-alive").timeout(60000).ignoreContentType(true).followRedirects(true);
            connect.headers(p.getHeader());
            connect.method(Connection.Method.valueOf(p.getMethod()));

            CONNECTION.set(connect);
        }

        if (isNull(TIMEOUT.get())) {
            TIMEOUT.set(Jsoup.connect(TEST_URL).timeout(10000).method(GET).ignoreContentType(true).followRedirects(true));
        }

        if (isNull(GET_IP_CONNECT.get())) {
            GET_IP_CONNECT.set(Jsoup.connect(IP_URL).timeout(10000).method(GET).ignoreContentType(true).followRedirects(true));
        }
    }

    public void invite() throws IOException {
        List<String> accountInfo = getAccountInfo(oauthId, AFF_CODE, INVITED_NUM);

        if (!isNull(accountInfo)) {
            String affCode = accountInfo.get(0);

            exception(Thread.currentThread().getName() + ": " + oauthId + "-" + affCode, null);

            int invitedSum = Integer.parseInt(accountInfo.get(1));

            int remain = COUNT - invitedSum;

            String dataInfo;

            try {
                for (int i = 1; i <= remain; ) {
                    String newOauthId = Main.generateOauthId();

                    AntResponsePackage exchange = exchange(affCode, newOauthId);

                    dataInfo = affCode.concat(SEPARATOR).concat(INFO).concat(String.valueOf(remain - i)).concat("次").concat(SEPARATOR);
                    if (!isNull(exchange) && println(dataInfo, exchange)) {
                        i++;
                    }
                }

                try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(OUT_FILE, true), StandardCharsets.UTF_8))) {
                    synchronized (OUT_FILE) {
                        OBJECT_MAPPER.writeValue(bufferedWriter, oauthId);
                    }
                } finally {
                    CONNECTION.remove();
                    TIMEOUT.remove();
                    GET_IP_CONNECT.remove();
                }
            } catch (Exception e) {
                exception(affCode + ", 邀请异常", e);
            }
        }
    }

    public static List<String> getAccountInfo(String oauthId, String... keys) throws IOException {
        Data data = p.getData();

        String dataCopy = OBJECT_MAPPER.writeValueAsString(data);
        data = OBJECT_MAPPER.readValue(dataCopy, Data.class);

        data.setMod(USER);
        data.setCode(HOME);

        data.setOauthId(oauthId);

        try {
            AntResponsePackage response = response(request(data));

            return getData(response, keys);
        } catch (Exception e) {
            exception("getAccountInfo" + "异常", e);
        }

        return null;
    }

    public static boolean println(String dataInfo, AntResponsePackage antResponsePackage) throws IOException {
        Instant instant = Instant.ofEpochSecond(antResponsePackage.getTimestamp());
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of(ZONE));
        LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();

        List<String> lines = getData(antResponsePackage, MSG);
        String data = isNull(lines) ? null : lines.get(0);

        dataInfo = dataInfo.concat(String.valueOf(localDateTime)).concat(SEPARATOR).concat(String.valueOf(data));
        exception(dataInfo, null);

        return !isNull(data) && data.contains(SUCCESS);
    }

    public static AntResponsePackage exchange(String affCode, String newOauthId) throws IOException {
        Data data = p.getData();

        String dataCopy = OBJECT_MAPPER.writeValueAsString(data);
        ExchangeData exchangeData = OBJECT_MAPPER.readValue(dataCopy, ExchangeData.class);

        exchangeData.setOauthId(newOauthId);
        exchangeData.setAff(affCode);
        exchangeData.setMod(USER);
        exchangeData.setCode(EXCHANGE_AFF);

        return response(request(exchangeData));
    }

    public static List<String> getData(AntResponsePackage antResponsePackage, String... keys) throws IOException {
        if (!isNull(antResponsePackage)) {
            List<String> lines = new ArrayList<>();

            String dataCopy = antResponsePackage.getData();
            JsonNode jsonNode = OBJECT_MAPPER.readTree(dataCopy);
            if (!isNull(keys) && keys.length > 0) {
                for (String key : keys) {
                    lines.add(jsonNode.findValue(key).asText());
                }
            } else {
                Iterator<JsonNode> elements = jsonNode.elements();
                JsonNode next;
                while (elements.hasNext()) {
                    next = elements.next();
                    lines.add(next.toString());
                }
            }

            return lines;
        }

        return null;
    }

    public static String request(Data data) {
        RequestParameter requestParameter = getRequestParameter(data);

        String body = null;
        if (!isNull(requestParameter)) {
            setConnection();
            CONNECTION.get().request().data().clear();
            CONNECTION.get().data(requestParameter.getMap());
            CONNECTION.get().userAgent(uaRandom("ANDROID"));

            boolean timeout;

            InternetAddress internetAddress;
            do {
                internetAddress = getIp();

                try {
                    if (!(timeout = isNull(internetAddress)) && !(timeout = !testIp(internetAddress))) {
                        CONNECTION.get().proxy(internetAddress.getIp(), internetAddress.getPort());
                        try {
                            TimeUnit.SECONDS.sleep(7);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        body = CONNECTION.get().execute().body();
                    }
                } catch (Exception e) {
                    timeout = true;
                }
                //exception("request" + CONNECTION.get().request().data() + ", 请求异常", e);

            } while (timeout);
        }

        return body;
    }

    public static String uaRandom() {
        return uaRandom(null, null);
    }

    public static String uaRandom(String type) {
        return uaRandom(type, null);
    }

    public static String uaRandom(String type, String uaPlatform) {
        if (isNull(type) || type.isEmpty()) {
            type = UA_TYPE_LIST.get(UA_RANDOM.nextInt(4));
        }

        Map<String, List<String>> ua = UA_LIST.get(type);
        List<String> uaList;
        if (isNull(uaPlatform) || uaPlatform.isEmpty()) {
            Set<String> uaKeySet = ua.keySet();
            String[] uaKeyString = uaKeySet.toArray(new String[0]);
            uaList = ua.get(uaKeyString[UA_RANDOM.nextInt(uaKeySet.size())]);
        } else {
            uaList = ua.get(uaPlatform);
        }

        return uaList.get(UA_RANDOM.nextInt(uaList.size()));
    }

    private static boolean testIp(InternetAddress internetAddress) {
        TIMEOUT.get().proxy(internetAddress.getIp(), internetAddress.getPort());

        try {
            String body = TIMEOUT.get().execute().body();
            if (!isNull(body)) {
                return body.contains(internetAddress.getIp()) || !body.isEmpty();
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }

    public static InternetAddress getIp() {

        do {
            try {
                String body = GET_IP_CONNECT.get().execute().body();

                JsonNode jsonNode = OBJECT_MAPPER.readTree(body);
                JsonNode data = jsonNode.findValue("data");
                JsonNode objectNode = data.get(0);

                if (!isNull(objectNode)) {
                    return OBJECT_MAPPER.treeToValue(objectNode, InternetAddress.class);
                }
            } catch (Exception e) {
                exception("getIP异常", e);
            }
        } while (true);
    }

    public static AntResponsePackage response(String body) {
        try {
            AntResponsePackage antResponsePackage = OBJECT_MAPPER.readValue(body, AntResponsePackage.class);

            String decryptData = AntRequestPackage.decryptData(antResponsePackage.getData());
            antResponsePackage.setData(decryptData);

            return antResponsePackage;
        } catch (Exception e) {
            exception("response异常", e);
        }

        return null;
    }

    public static RequestParameter getRequestParameter(Data data) {
        RequestParameter requestParameter = p.getRequestParameter();

        String copy;
        try {
            copy = OBJECT_MAPPER.writeValueAsString(requestParameter);

            requestParameter = OBJECT_MAPPER.readValue(copy, RequestParameter.class);

            String valueAsString = OBJECT_MAPPER.writeValueAsString(data);
            String encryptData = AntRequestPackage.encryptData(valueAsString);
            requestParameter.setData(encryptData);

            Instant now = Instant.now();
            String timestamp = String.valueOf(now.getEpochSecond());
            requestParameter.setTimestamp(timestamp);

            String sign = DataHandler.sign(requestParameter);
            requestParameter.setSign(sign);

            return requestParameter;
        } catch (Exception e) {
            exception("getRequestParameter异常", e);
        }

        return null;
    }
}
