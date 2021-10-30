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

    // function to check if qr contains url
    public boolean isURL(String str){

        if (urlPattern.matcher(str).matches())
            return true;
        else
            return false;

    }

    public String isValidURL(String str){
        Uri quickResponseCodeURI = Uri.parse(str);

        String domainName = quickResponseCodeURI.getHost();

        return domainName;

    }

}
