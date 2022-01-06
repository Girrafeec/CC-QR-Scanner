package com.girrafeecstud.ccqrscanner;

import android.net.Uri;

import java.net.URI;
import java.util.List;
import java.util.regex.Pattern;

public class QuickResponseCodeURL {

    // Pattern for recognizing a URL, based off RFC 3986
    private static final Pattern urlPattern = Pattern.compile(
            "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                    + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                    + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};' ]*)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    // Pattern for valid url path
    // example: /covid-cert/verify/****************, where ***************** - certificate id
    // example: /covid-cert/status/************************************, where ************************************ - hash sum
    // example: /vaccine/cert/verify/************************************, where ************************************ - hash sum
    private static final Pattern urlPathPattern = Pattern.compile(
            "^/[\b(covid\\-cert)|(vaccine)\b/]+/[\b(verify|status|cert/verify)\b/]+/[^/]+[a-zA-Z0-9]$"
    );

    // Pattern for valid url domain
    private static final Pattern validUrlDomain = Pattern.compile(
            "^www.gosuslugi.ru$"
    );

    // function to check if qr contains url
    public boolean isURL(String str){

        if (urlPattern.matcher(str).matches())
            return true;
        else
            return false;

    }

    // function check if url is valid (has valid domain and valid path)
    public boolean isValidURL(String str){
        Uri quickResponseCodeURI = Uri.parse(str);

        String domainName = quickResponseCodeURI.getHost();
        String path = quickResponseCodeURI.getPath();

        if (validUrlDomain.matcher(domainName).matches()
                && urlPathPattern.matcher(path).matches())
            return true;

        return false;

    }

    // function replaces potential spaces in url
    public String replaceSpaces(String url){
        String str  = url.replaceAll(" ", "0%20");
        return str;
    }
}
