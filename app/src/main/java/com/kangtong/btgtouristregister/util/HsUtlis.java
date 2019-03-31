package com.kangtong.btgtouristregister.util;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Created by LUA on 2016/9/8.
 */
public class HsUtlis {

    private static HS550 hs550;
    private static Device device;

//    static {
//        hs550 = new HS550();
//        device = new Device();
//    }

    public static void IDCardPonwer1() throws IOException {
        writeFile("-wdout5 1");
        writeFile("-wdout13 1");
    }

    public static void IDCardPonoff1() throws IOException {
        writeFile("-wdout5 0");
        writeFile("-wdout13 0");
    }

    public static int IDCardPonwer2() {
        device = new Device();
        return device.IDCardPower(1);
    }

    public static int IDCardPonoff2() {
        return device.IDCardPower(0);
    }

    public static int UsbPonwer1() {
        hs550 = new HS550();
        return hs550.PowerOnUSB();
    }

    public static int UsbPonoff1() {
        if (hs550 == null) {
            hs550 = new HS550();
        }
        return hs550.PowerOffUSB();
    }

    public static int UsbPonwer2() {
        device = new Device();
        return device.FingerprintPower(1);
    }

    public static int UsbPonoff2() {
        device = new Device();
        return device.FingerprintPower(0);
    }


    private static void writeFile(String sData2Write) throws IOException {

        if (sData2Write == null) {
//            Log.e(TAG, "no data write");
            return;
        }
        String sFile = "/sys/class/misc/mtgpio/pin";
        String sdata = sData2Write;
        File afile = new File(sFile);

        FileOutputStream out = new FileOutputStream(afile, false);
        try {
            out.write(sdata.getBytes());
            Thread.sleep(100);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
