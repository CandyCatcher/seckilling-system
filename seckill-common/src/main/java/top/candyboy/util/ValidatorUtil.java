package top.candyboy.util;

import tk.mybatis.mapper.util.StringUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//判断输入的格式是否正确
public class ValidatorUtil {
    private static final Pattern mobile_pattern = Pattern.compile("1\\d{10}");

    public static Boolean isMobile(String string) {
        if (StringUtil.isEmpty(string)) {
            return false;
        }
        Matcher matcher = mobile_pattern.matcher(string);
        return matcher.matches();
    }
}
