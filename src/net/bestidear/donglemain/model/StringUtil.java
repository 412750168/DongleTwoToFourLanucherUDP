package net.bestidear.donglemain.model;

import android.graphics.Color;
import android.util.Log;

import java.util.Calendar;
import java.util.Locale;

public final class StringUtil {
	public final static boolean hasData(String value) {
		if (value == null)
			return false;
		if (value.length() <= 0)
			return false;
		return true;
	}

	public final static int toInteger(String value) {
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public final static long toLong(String value) {
		try {
			return Long.parseLong(value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public final static float toFloat(String value) {
		try {
			return Float.parseFloat(value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0f;
	}

	public final static double toDouble(String value) {
		try {
			return Double.parseDouble(value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public final static int findOf(String str, String chars, int startIdx,
			int endIdx, int offset, boolean isEqual) {
		if (offset == 0)
			return -1;
		int charCnt = chars.length();
		int idx = startIdx;
		while (true) {
			if (0 < offset) {
				if (endIdx < idx)
					break;
			} else {
				if (idx < endIdx)
					break;
			}
			char strc = str.charAt(idx);
			int noEqualCnt = 0;
			for (int n = 0; n < charCnt; n++) {
				char charc = chars.charAt(n);
				if (isEqual == true) {
					if (strc == charc)
						return idx;
				} else {
					if (strc != charc)
						noEqualCnt++;
					if (noEqualCnt == charCnt)
						return idx;
				}
			}
			idx += offset;
		}
		return -1;
	}

	public final static int findFirstOf(String str, String chars) {
		return findOf(str, chars, 0, (str.length() - 1), 1, true);
	}

	public final static int findFirstNotOf(String str, String chars) {
		return findOf(str, chars, 0, (str.length() - 1), 1, false);
	}

	public final static int findLastOf(String str, String chars) {
		return findOf(str, chars, (str.length() - 1), 0, -1, true);
	}

	public final static int findLastNotOf(String str, String chars) {
		return findOf(str, chars, (str.length() - 1), 0, -1, false);
	}

	public final static String trim(String trimStr, String trimChars) {
		int spIdx = findFirstNotOf(trimStr, trimChars);
		if (spIdx < 0) {
			String buf = trimStr;
			return buf;
		}
		String trimStr2 = trimStr.substring(spIdx, trimStr.length());
		spIdx = findLastNotOf(trimStr2, trimChars);
		if (spIdx < 0) {
			String buf = trimStr2;
			return buf;
		}
		String buf = trimStr2.substring(0, spIdx + 1);
		return buf;
	}
	
	public static boolean isToday(String date){
        if(date.equals("N/A")){
            return false;
        }
        int YY = Integer.parseInt(date.substring(0, date.indexOf("年")));
        int MM = Integer.parseInt(date.substring(date.indexOf("年")+1, date.indexOf("月")));
        int DD = Integer.parseInt(date.substring(date.indexOf("月")+1, date.indexOf("日")));
        
//        Log.d(Tag, "YYMMDD:"+YY+";"+MM+";"+DD);
        
        Calendar ca = Calendar.getInstance(Locale.CHINA);
        int caYY = ca.get(Calendar.YEAR); 
        int caMM = ca.get(Calendar.MONTH)+1; 
        int caDD = ca.get(Calendar.DAY_OF_MONTH); 
//        Log.d(Tag, "caYYMMDD:"+caYY+";"+caMM+";"+caDD);
        
        if(YY == caYY && MM ==caMM && DD == caDD){
            return true;
        }else{
            return false;
        }
    }
	
}
