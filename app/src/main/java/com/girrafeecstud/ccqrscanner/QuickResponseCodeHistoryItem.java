package com.girrafeecstud.ccqrscanner;

import java.time.LocalDateTime;
import java.util.Date;

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
    private LocalDateTime time;

    QuickResponseCodeURL quickResponseCodeURL = new QuickResponseCodeURL();

    // Constructor for qr code with content without url or with invalid url
    public QuickResponseCodeHistoryItem(int qrCodeType, String content, LocalDateTime time){
        if (!quickResponseCodeURL.isURL(content))
            this.content = content;
        else
            this.url = content;
        imgStatus = "RED";
        this.qrCodeType = qrCodeType;
        this.time = time;
         //TODO
    }

    // Constructor for qr code with valid certificate url
    public QuickResponseCodeHistoryItem(int qrCodeType, boolean certificateReuse, String type, String title, String status,
                                        String certificateId, String expiredAt, String fio, String enFio, String recoveryDate,
                                        String passport, String enPassport, String birthDate, LocalDateTime time) {

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
        this.time = time;
    }

    public int getQrCodeType() {
        return qrCodeType;
    }

    public boolean isCertificateReuse() {
        return certificateReuse;
    }

    public String getContent() {
        return content;
    }

    public String getImgStatus() {
        return imgStatus;
    }

    public String getUrl() {
        return url;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getStatus() {
        return status;
    }

    public String getCertificateId() {
        return certificateId;
    }

    public String getExpiredAt() {
        return expiredAt;
    }

    public String getFio() {
        return fio;
    }

    public String getEnFio() {
        return enFio;
    }

    public String getRecoveryDate() {
        return recoveryDate;
    }

    public String getPassport() {
        return passport;
    }

    public String getEnPassport() {
        return enPassport;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public LocalDateTime getTime(){
        return time;
    }
}
