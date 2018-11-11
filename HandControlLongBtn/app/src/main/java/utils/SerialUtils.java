package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ Create by dadac on 2018/10/12.
 * @Function: 各种工具类
 * @Return:
 */
public class SerialUtils {

    /**
     * @param str 为要去除空格回车的字符串
     * @Function: 去除字符串中的 \s \t \r \n 格式
     * @Return: dest 为去除后的字符串
     */
    public static String replaceTRNBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /**
     * @param str 指定的字符串 要去除指定的字符
     *            def  是要去除的字符  比如说  格式 String def    String "#"
     * @Function: 去除字符串中的指定的字符
     * @Return:
     * @attention: 注意转义字符 前面得添加  \\
     */
    public static String replaceStringUseDefineWord(String str, String def) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile(def);
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }

        return dest;
    }

    /*
     * 按字节长度截取字符串
     * @param str 将要截取的字符串参数
     * @param toCount 截取的字节长度
     * @param more 字符串末尾补上的字符串
     * @return 返回截取后的字符串
     */
    public static String getSubString(String str, int toCount, String more) {
        int reInt = 0;
        String reStr = "";
        if (str == null)
            return "";
        char[] tempChar = str.toCharArray();
        for (int kk = 0; (kk < tempChar.length && toCount > reInt); kk++) {
            String s1 = str.valueOf(tempChar[kk]);
            byte[] b = s1.getBytes();
            reInt += b.length;
            reStr += tempChar[kk];
        }
        if (toCount == reInt || (toCount == reInt - 1))
            reStr += more;
        return reStr;
    }

    /**
     * @param def 为分割的标志
     * @Function: 分割字符串
     * @Return: 无法使用
     */
    public static List<String> splitString(String str, String def) {
        List<String> savevalue = new ArrayList<>();
        StringTokenizer stringTokenizer = new StringTokenizer(str, "!");
        String Angles = null;
        while (stringTokenizer.hasMoreElements()) {
            savevalue.add(stringTokenizer.nextToken());
        }
        return savevalue;
    }

    /**
     * @Function: 例如将 String类型的 "[21,3]"  分割成 21  和  3  两个单独的字符串
     * @Return:
     * @attention: 注意转义字符   前面得添加  \\
     */
    public static List<String> spliteStringPower(String data) {
        String a = replaceStringUseDefineWord(replaceStringUseDefineWord(data, "\\["), "\\]");
        String[] b = a.split("[,]");
        List<String> c = new ArrayList<String>();
        c.add(b[0]);
        c.add(b[1]);
        return c;
    }

    /**
     * @Function: 判断一个字符串是否都为数字
     * @Return: true or false
     */
    public static boolean isAllDigit(String strNum) {
        Pattern pattern = Pattern.compile("[0-9]{1,}");
        Matcher matcher = pattern.matcher((CharSequence) strNum);
        return matcher.matches();
    }

    // 判断一个字符串是否都为数字
    public static boolean isDigitRegular(String strNum) {
        return strNum.matches("[0-9]{1,}");
    }

    // 判断一个字符串是否含有数字
    public boolean HasDigit(String content) {
        boolean flag = false;
        Pattern p = Pattern.compile(".*\\d+.*");
        Matcher m = p.matcher(content);
        if (m.matches()) {
            flag = true;
        }
        return flag;
    }

    /**
     * @param size 为 String 保留小数点后几位
     * @Function: number 为输入的值  string类型   保留 double类型的小数点几位
     * @Return: float 类型的 小数点后 2位     要是返回的数据超过 7位 则得改用Float
     */
    public static float savePointEndNumberFloat(String number, String size) {
        String result = String.format("%." + size + "f", Double.parseDouble(number));
        return Float.parseFloat(result);
    }

    //保留小数点后几位
    public static double savePointEndNumberDouble(String number, String size) {
        String result = String.format("%." + size + "f", Double.parseDouble(number));
        return Double.parseDouble(result);
    }

    /**
     * @param angle 语音唤醒的 angle
     * @Function: 将语音的唤醒值0-360  转变成 惯导的类型
     * 语音唤醒为0--360
     * 惯导 机器人左边为  0 ---   -180    机器人 左转 +  x=0 y=0 z>0 不要太大 多少弧度每秒
     * 右边为  0 ---   +180    机器人 右转 -          z<0
     * @Return: 惯导的  angle
     * 但是用不着
     */
    public static int WakeUpToINSAngle(String angle) {
        int WakeToINS = Integer.parseInt(angle);
        if (WakeToINS <= 180) {
            if (WakeToINS == 180)
                WakeToINS = 179;
            WakeToINS = -WakeToINS;
        }
        if (WakeToINS > 180) {
            WakeToINS = 360 - WakeToINS;
            if(WakeToINS==360)
                WakeToINS=0;
        }
        if (WakeToINS == 180) WakeToINS = 179;
        return WakeToINS;
    }

}

































