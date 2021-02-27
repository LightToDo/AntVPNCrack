package com.guangping.bind;

import com.guangping.post.AntResponsePackage;
import com.guangping.pojo.Data;
import com.guangping.invite.InviteTask;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import static com.guangping.invite.InviteTask.*;
import static java.util.Objects.isNull;

public class Bind {

    private static SmsBindPhoneData smsBindPhoneData;

    private static final String GET_CODE = "getCode";
    private static final String BIND_PHONE = "validateCode";
    private static final String SYSTEM_VERSION = "M5 Note Android 7.0";
    private static final String QUIT = "q";

    public static String sendSms(String oauthId, String countyCode, String mobile) throws IOException {
        Data data = InviteTask.p.getData();

        String dataCopy = OBJECT_MAPPER.writeValueAsString(data);
        smsBindPhoneData = OBJECT_MAPPER.readValue(dataCopy, SmsBindPhoneData.class);

        smsBindPhoneData.setType(10);
        smsBindPhoneData.setMod(MSG);
        smsBindPhoneData.setCode(GET_CODE);

        smsBindPhoneData.setOauthId(oauthId);
        smsBindPhoneData.setCountryCode(countyCode);
        smsBindPhoneData.setMobile(mobile);

        AntResponsePackage response = response(request(smsBindPhoneData));
        List<String> lines = getData(response, MSG);

        return isNull(lines) ? null : lines.get(0);
    }

    public static String bindPhone(String code, SmsBindPhoneData smsBindPhoneData) throws IOException {
        Data data = p.getData();

        String dataCopy = OBJECT_MAPPER.writeValueAsString(data);
        BindPhoneData bindPhoneData = OBJECT_MAPPER.readValue(dataCopy, BindPhoneData.class);

        bindPhoneData.setCode(BIND_PHONE);
        bindPhoneData.setMsgSystemVer(SYSTEM_VERSION);
        bindPhoneData.setMod(MSG);
        bindPhoneData.setOauthId(smsBindPhoneData.getOauthId());
        bindPhoneData.setVCode(code);
        bindPhoneData.setType(10);
        bindPhoneData.setCountryCode(smsBindPhoneData.getCountryCode());

        AntResponsePackage response = response(request(bindPhoneData));
        List<String> lines = getData(response, MSG);

        return isNull(lines) ? null : lines.get(0);
    }

    public static boolean input(String oauthId) {
        boolean quit = false;

        try (Scanner scanner = new Scanner(System.in)) {
            String result;
            String code;

            do {
                System.out.print("请输入国家代码[取消输入q/Q]: ");
                code = scanner.nextLine();

                System.out.print("请输入手机号[取消输入q/Q]: ");
                String mobile = scanner.nextLine();

                quit = QUIT.equalsIgnoreCase(code) || QUIT.equalsIgnoreCase(mobile);
                if (quit) {
                    break;
                }

                result = sendSms(oauthId, code, mobile);
                System.out.println("验证码发送结果: " + result);
            } while (isNull(result) || !result.contains(SUCCESS));

            do {
                System.out.print("请输入验证码[取消输入q/Q]: ");
                code = scanner.nextLine();

                quit = QUIT.equalsIgnoreCase(code);
                if (quit) {
                    break;
                }

                result = bindPhone(code, smsBindPhoneData);
                System.out.println("绑定手机结果: " + result);
                quit = !isNull(result) && result.contains(SUCCESS);
            } while (!quit);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return quit;
    }
}
