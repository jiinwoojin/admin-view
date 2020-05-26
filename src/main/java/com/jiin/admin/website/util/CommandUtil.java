package com.jiin.admin.website.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

// CUI 기반 정보 획득을 위한 메소드 모음
public class CommandUtil {
    /**
     * 리눅스 기반 커멘드 내용 메소드
     * @param linuxCommand String
     * @throws IOException
     * @note 윈도우 기반 커멘드 내용 획득 메소드 추가 필요
     */
    public static String fetchShellContextByLinuxCommand(String linuxCommand) throws IOException {
        StringBuffer sb = new StringBuffer();
        Process process =Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", linuxCommand });
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while((line = reader.readLine()) != null){
            sb.append(line);
        }
        return sb.toString();
    }
}
