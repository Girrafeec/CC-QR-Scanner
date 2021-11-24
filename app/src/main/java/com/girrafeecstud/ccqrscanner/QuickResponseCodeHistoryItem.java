package com.girrafeecstud.ccqrscanner;

public class QuickResponseCodeHistoryItem {

    private int qrCodeType = 0; // 1 - not url; 2 - not valid url; 3 - certificate url
    private boolean certificateReuse = false;
    private String content = "";
    private String imgStatus = "";
    private String url = "";

    private String type = "";
    private String title = "";
    private String status = "";
    private String certificateId = "";
    private String expiredAt = "";
    private String fio = "";
    private String enFio = "";
    private String recoveryDate = "";
    private String passport = "";
    private String enPassport = "";
    private String birthDate = "";

    QuickResponseCodeURL quickResponseCodeURL = new QuickResponseCodeURL();

    // Constructor for qr code with content without url or with invalid url
    public QuickResponseCodeHistoryItem(String content, int qrCodeType){
        if (!quickResponseCodeURL.isURL(content))
            this.content = content;
        else
            this.url = content;
        imgStatus = "RED";
        this.qrCodeType = qrCodeType;
    }

    // Constructor for qr code with valid certificate url
    public QuickResponseCodeHistoryItem(int qrCodeType, boolean certificateReuse,String type, String title, String status,
                                        String certificateId, String expiredAt, String fio, String enFio, String recoveryDate,
                                        String passport, String enPassport, String birthDate) {

        if (certificateReuse)
            imgStatus = "YELLOW";
        else
            imgStatus = "GREEN";

        this.qrCodeType = qrCodeType;
        this.certificateReuse = certificateReuse;
        this.type = type;
        this.title = title;
        this.status = status;
        this.certificateId = certificateId;
        this.expiredAt = expiredAt;
        this.fio = fio;
        this.enFio = enFio;
        this.recoveryDate = recoveryDate;
        this.passport = passport;
        this.enPassport = enPassport;
        this.birthDate = birthDate;
    }


}
