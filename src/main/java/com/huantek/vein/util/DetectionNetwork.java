package com.huantek.vein.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DetectionNetwork {

    /**
     * 检测网络是否畅通
     * 返回：false 不畅通 true 畅通
     * 创建人：wyx
     * @return
     */
    public static boolean isConnect(){
        String ip = "www.baidu.com";
        boolean connect = false;
        Runtime runtime = Runtime.getRuntime();
        Process process;
        try {
            process = runtime.exec("ping " + ip);
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(isr);
            String line = null;
            StringBuffer sb = new StringBuffer();
            while ((line = reader.readLine()) != null){
                sb.append(line);
            }

            is.close();
            isr.close();
            reader.close();

            if (null != sb && !"".equals(sb.toString())){
                if (sb.toString().indexOf("TTL")>0){
                    //网络畅通
                    connect = true;
                }else {
                    //网络不畅通
                    connect = false;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return connect;
    }
}
