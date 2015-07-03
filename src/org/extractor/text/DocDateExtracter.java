package org.extractor.text;

import java.io.File;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DocDateExtracter {
	public static String getModifiedDate(File f) {
		String result = "";
		Calendar cal = Calendar.getInstance();
		long time = f.lastModified();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		cal.setTimeInMillis(time);
		result = formatter.format(cal.getTime());
		return result;
	}

	public static String extractDocumentDate(String fileContent, File f) {
		//SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
		String result = getModifiedDate(f);// format.format(new Date());

		//支持 YYYY-MM-DD,YYYY/MM/DD,YYYY年MM月DD日,月DD日 四种格式
		Pattern pattern = Pattern
				.compile("(\\d{4}-\\d{1,2}-\\d{1,2})|"+
		                 "(\\d{4}\\/\\d{1,2}\\/\\d{1,2})|"+
						 "(\\d{4}年\\d{1,2}月\\d{1,2}日)|"+
		                 "(\\d{1,2}月\\d{1,2}日)");
		Matcher matcher = pattern.matcher(fileContent);
		if (matcher.find()) {
			result = matcher.group();
			result = result.replaceAll("\\/", "-");
			
			if (result.indexOf("日")>0 && result.indexOf("月")>0 && result.indexOf("年")<0) {
				result = formatter.format(new Date()) + "年"+result;
			}
			
			result = result.replaceAll("年", "-");
			result = result.replaceAll("月", "-");
			result = result.replaceAll("日", "");
		}

		String[] s = result.split("-");
		if (s[1].length() == 1)
			s[1] = "0" + s[1];
		if (s[2].length() == 1)
			s[2] = "0" + s[2];
		return s[0] + "-" + s[1] + "-" + s[2];
	}
}
