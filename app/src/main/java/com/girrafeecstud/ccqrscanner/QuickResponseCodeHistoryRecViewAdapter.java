package com.girrafeecstud.ccqrscanner;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class QuickResponseCodeHistoryRecViewAdapter extends RecyclerView.Adapter<QuickResponseCodeHistoryRecViewAdapter.ViewHolder> {

    private ArrayList<QuickResponseCodeHistoryItem> quickResponseCodeHistoryItemArrayList
            = new ArrayList<>();

    //private OnQrHistoryItemDropListener onQrHistoryItemDropListener;
    private Context context;

    public QuickResponseCodeHistoryRecViewAdapter(Context context/*, OnQrHistoryItemDropListener onQrHistoryItemDropListener*/){
        this.context = context;
        //this.onQrHistoryItemDropListener = onQrHistoryItemDropListener;
    }

    public void setQuickResponseCodeHistoryItemArrayList(ArrayList<QuickResponseCodeHistoryItem> quickResponseCodeHistoryItemArrayList) {
        this.quickResponseCodeHistoryItemArrayList = quickResponseCodeHistoryItemArrayList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quick_response_code_history_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        int currentPosition = holder.getAdapterPosition();


        // part to expand the qr history item info
        boolean isVisible = quickResponseCodeHistoryItemArrayList.get(currentPosition).getItemInfoVisibility();

        if (!isVisible) {
            holder.qrHistoryItem.setVisibility(View.GONE);
            holder.expandItem.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24);
        }
        else{
            holder.qrHistoryItem.setVisibility(View.VISIBLE);
            holder.expandItem.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24);
        }


        if (quickResponseCodeHistoryItemArrayList.get(currentPosition).getQrCodeType() == 1)
            setInvalidContentData(holder, currentPosition);

        if (quickResponseCodeHistoryItemArrayList.get(currentPosition).getQrCodeType() == 2)
            setInvalidUrlData(holder, currentPosition);

        if (quickResponseCodeHistoryItemArrayList.get(currentPosition).getQrCodeType() == 3)
            setValidData(holder, currentPosition);

        // set time
        if (String.valueOf(quickResponseCodeHistoryItemArrayList.get(currentPosition).getTime().getMinute()).length() == 1)
            holder.scannedTime.setText(quickResponseCodeHistoryItemArrayList.get(currentPosition).getTime().getHour()
                    + ":" + "0" + quickResponseCodeHistoryItemArrayList.get(currentPosition).getTime().getMinute());
        else
            holder.scannedTime.setText(quickResponseCodeHistoryItemArrayList.get(currentPosition).getTime().getHour()
                    + ":" + quickResponseCodeHistoryItemArrayList.get(currentPosition).getTime().getMinute());
    }

    // procedure to set expanded info of qr history item for invalid qr
    public void setInvalidContentData(ViewHolder holder, int currentPosition){
        holder.scannedStatus.setText("Некорректный код");
        holder.statusImg.setImageResource(R.drawable.ic__891023_cancel_cercle_close_delete_dismiss_icon);
        holder.itemStatus.setText("Статус: " + "Некорректный код");
        holder.itemContent.setText("Содержимое: " + quickResponseCodeHistoryItemArrayList.get(currentPosition).getContent());

        holder.itemStatus.setVisibility(View.VISIBLE);
        holder.itemContent.setVisibility(View.VISIBLE);
        holder.itemUrl.setVisibility(View.GONE);
        holder.itemCertType.setVisibility(View.GONE);
        holder.itemCertStatus.setVisibility(View.GONE);
        holder.itemCertId.setVisibility(View.GONE);
        holder.itemCertExpiredAt.setVisibility(View.GONE);
        holder.itemCertFio.setVisibility(View.GONE);
        holder.itemCertEnFio.setVisibility(View.GONE);
        holder.itemCertPassport.setVisibility(View.GONE);
        holder.itemCertEnPassport.setVisibility(View.GONE);
        holder.itemCertRecoveryDate.setVisibility(View.GONE);
        holder.itemCertBirthDate.setVisibility(View.GONE);
        holder.itemCertValidTime.setVisibility(View.GONE);
    }

    // procedure to set expanded info of qr history item for invalid URL
    public void setInvalidUrlData(ViewHolder holder, int currentPosition){
        holder.scannedStatus.setText("Некорректная ссылка");
        holder.statusImg.setImageResource(R.drawable.ic__891023_cancel_cercle_close_delete_dismiss_icon);
        holder.itemStatus.setText("Статус: " + "Некорректная ссылка");
        holder.itemUrl.setText("Ссылка: " + quickResponseCodeHistoryItemArrayList.get(currentPosition).getUrl());

        holder.itemStatus.setVisibility(View.VISIBLE);
        holder.itemUrl.setVisibility(View.VISIBLE);
        holder.itemContent.setVisibility(View.GONE);
        holder.itemCertType.setVisibility(View.GONE);
        holder.itemCertStatus.setVisibility(View.GONE);
        holder.itemCertId.setVisibility(View.GONE);
        holder.itemCertExpiredAt.setVisibility(View.GONE);
        holder.itemCertFio.setVisibility(View.GONE);
        holder.itemCertEnFio.setVisibility(View.GONE);
        holder.itemCertPassport.setVisibility(View.GONE);
        holder.itemCertEnPassport.setVisibility(View.GONE);
        holder.itemCertRecoveryDate.setVisibility(View.GONE);
        holder.itemCertBirthDate.setVisibility(View.GONE);
        holder.itemCertValidTime.setVisibility(View.GONE);
    }

    // procedure to set expanded info of qr history item for valid certificate
    private void setValidData(ViewHolder holder, int currentPosition){

        if (quickResponseCodeHistoryItemArrayList.get(currentPosition).getStatus().equals("Действителен")) {
            holder.scannedStatus.setText("Сертификат подтверждён");
            holder.itemStatus.setText("Статус: " + "Сертификат подтверждён");
            holder.statusImg.setImageResource(R.drawable.ic__930264_check_complete_done_green_success_icon);
        }
        else {
            holder.scannedStatus.setText("Сертификат устарел/не действителен");
            holder.itemStatus.setText("Статус " + "Сертификат устарел/не действителен");
            holder.statusImg.setImageResource(R.drawable.ic__891023_cancel_cercle_close_delete_dismiss_icon);
        }
        if (quickResponseCodeHistoryItemArrayList.get(currentPosition).isCertificateReuse() == true) {
            holder.scannedStatus.setText("Повторное использование сертификата");
            holder.itemStatus.setText("");
            holder.itemStatus.setText("Статус: " + "Повторное использование сертификата");
            holder.statusImg.setImageResource(R.drawable.ic__26633_error_icon);
        }
        holder.itemCertType.setText("Тип сертификата: " + quickResponseCodeHistoryItemArrayList.get(currentPosition).getType());
        holder.itemCertStatus.setText("Статус сертификата: " + quickResponseCodeHistoryItemArrayList.get(currentPosition).getStatus());
        holder.itemCertId.setText("Номер сертификата: " + quickResponseCodeHistoryItemArrayList.get(currentPosition).getCertificateId());
        holder.itemCertExpiredAt.setText("Действует до: " + quickResponseCodeHistoryItemArrayList.get(currentPosition).getExpiredAt());
        holder.itemCertFio.setText("ФИО: " + quickResponseCodeHistoryItemArrayList.get(currentPosition).getFio());
        holder.itemCertEnFio.setText("ФИО (English): " + quickResponseCodeHistoryItemArrayList.get(currentPosition).getEnFio());
        holder.itemCertPassport.setText("Паспорт: " + quickResponseCodeHistoryItemArrayList.get(currentPosition).getPassport());
        holder.itemCertBirthDate.setText("Дата рождени: " + quickResponseCodeHistoryItemArrayList.get(currentPosition).getBirthDate());

        holder.itemStatus.setVisibility(View.VISIBLE);
        holder.itemContent.setVisibility(View.GONE);
        holder.itemUrl.setVisibility(View.GONE);
        holder.itemCertType.setVisibility(View.VISIBLE);
        holder.itemCertStatus.setVisibility(View.VISIBLE);
        holder.itemCertId.setVisibility(View.VISIBLE);
        holder.itemCertExpiredAt.setVisibility(View.VISIBLE);
        holder.itemCertFio.setVisibility(View.VISIBLE);
        holder.itemCertEnFio.setVisibility(View.VISIBLE);
        holder.itemCertPassport.setVisibility(View.VISIBLE);
        holder.itemCertEnPassport.setVisibility(View.VISIBLE);
        holder.itemCertRecoveryDate.setVisibility(View.VISIBLE);
        holder.itemCertBirthDate.setVisibility(View.VISIBLE);
        holder.itemCertValidTime.setVisibility(View.VISIBLE);

        if (quickResponseCodeHistoryItemArrayList.get(currentPosition).getValidFrom().equals("0"))
            holder.itemCertValidTime.setVisibility(View.GONE);
        else
            holder.itemCertValidTime.setText("Действует с: " + quickResponseCodeHistoryItemArrayList.get(currentPosition).getValidFrom());

        if (quickResponseCodeHistoryItemArrayList.get(currentPosition).getRecoveryDate().equals("0"))
            holder.itemCertRecoveryDate.setVisibility(View.GONE);
        else
            holder.itemCertRecoveryDate.setText("Дата выздоровления: " + quickResponseCodeHistoryItemArrayList.get(currentPosition).getRecoveryDate());

        if (quickResponseCodeHistoryItemArrayList.get(currentPosition).getEnPassport().equals("0"))
            holder.itemCertEnPassport.setVisibility(View.GONE);
        else
            holder.itemCertEnPassport.setText("Загранпаспорт: " + quickResponseCodeHistoryItemArrayList.get(currentPosition).getEnPassport());
    }

    @Override
    public int getItemCount() {
        return quickResponseCodeHistoryItemArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageButton expandItem;
        private ImageView statusImg;
        private TextView scannedStatus, scannedTime;
        private TextView itemStatus, itemUrl, itemContent, itemCertType, itemCertStatus, itemCertId,
                itemCertExpiredAt, itemCertFio, itemCertEnFio, itemCertPassport, itemCertEnPassport,
                itemCertRecoveryDate, itemCertBirthDate, itemCertValidTime;

        private ConstraintLayout qrHistoryItem;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            expandItem = itemView.findViewById(R.id.dropScannedHistoryBtn);
            scannedStatus = itemView.findViewById(R.id.scannedStatusTxt);
            scannedTime = itemView.findViewById(R.id.scannedTimeTxt);
            statusImg = itemView.findViewById(R.id.scannedStatusImg);

            qrHistoryItem = itemView.findViewById(R.id.qrHistoryItemInfoLinLay);

            itemStatus = itemView.findViewById(R.id.historyItemStatusTxt);
            itemUrl = itemView.findViewById(R.id.historyItemUrlTxt);
            itemContent = itemView.findViewById(R.id.historyItemContentTxt);
            itemCertType = itemView.findViewById(R.id.historyItemCertificateTypeTxt);
            itemCertStatus = itemView.findViewById(R.id.historyItemCertificateStatusTxt);
            itemCertId = itemView.findViewById(R.id.historyItemCertificateIdTxt);
            itemCertExpiredAt = itemView.findViewById(R.id.historyItemExpiredAtTxt);
            itemCertFio = itemView.findViewById(R.id.historyItemFioTxt);
            itemCertEnFio = itemView.findViewById(R.id.historyItemEnFioTxt);
            itemCertPassport = itemView.findViewById(R.id.historyItemPassportTxt);
            itemCertEnPassport = itemView.findViewById(R.id.historyItemEnPassportTxt);
            itemCertRecoveryDate = itemView.findViewById(R.id.historyItemRecoveryDateTxt);
            itemCertBirthDate = itemView.findViewById(R.id.historyItemBirthDateTxt);
            itemCertValidTime = itemView.findViewById(R.id.historyItemValidTimeTxt);

            scannedStatus.setSelected(true); // auto-scroll of scanned status message

            //TODO исправить нажатие по-правильному
            expandItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!quickResponseCodeHistoryItemArrayList.get(getAdapterPosition()).getItemInfoVisibility())
                        quickResponseCodeHistoryItemArrayList.get(getAdapterPosition()).setItemInfoVisibility(true);
                    else
                        quickResponseCodeHistoryItemArrayList.get(getAdapterPosition()).setItemInfoVisibility(false);
                    notifyItemChanged(getAdapterPosition());
                }
            });

           /* itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onQrHistoryItemDropListener.onQrHistoryItemDropClick(getAdapterPosition());
                }
            });

            */
        }
    }

    /*
    public interface OnQrHistoryItemDropListener{
        public void onQrHistoryItemDropClick(int position);
    }

     */

}
