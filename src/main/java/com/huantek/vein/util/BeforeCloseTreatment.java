package com.huantek.vein.util;

import com.huantek.vein.socket.SocketServer;

import java.io.IOException;

public class BeforeCloseTreatment{

    public void recycling()  {
        SocketServer.OFF_ON = false;
    }

    public static void dosClose() throws IOException {
        String cmd = "taskkill /f /im MotionHub-Server.exe";
        Runtime.getRuntime().exec(cmd);
    }

}
