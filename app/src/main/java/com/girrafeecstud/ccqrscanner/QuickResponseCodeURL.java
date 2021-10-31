package com.girrafeecstud.ccqrscanner;

import android.net.Uri;

import java.net.URI;
import java.util.regex.Pattern;

public class QuickResponseCodeURL {

    // Pattern for recognizing a URL, based off RFC 3986
    private static final Pattern urlPattern = Pattern.compile(
            "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                    + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                    + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    private static final String[] validURL = new String[]{"www.gosuslugi.ru"};

    // function to check if qr contains url
    public boolean isURL(String str){

        if (urlPattern.matcher(str).matches())
            return true;
        else
            return false;

    }

    public boolean isValidURL(String str){
        Uri quickResponseCodeURI = Uri.parse(str);

        String domainName = quickResponseCodeURI.getHost();

        for (int i=0; i < validURL.length; i++){
            if (domainName.equals(validURL[0]))
                return true;
        }

        return false;

    }

}
