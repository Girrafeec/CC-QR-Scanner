package com.girrafeecstud.ccqrscanner;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class QuickResponseCodeHistoryRecViewAdapter extends RecyclerView.Adapter<QuickResponseCodeHistoryRecViewAdapter.ViewHolder> {

    private ArrayList<QuickResponseCodeHistoryItem> quickResponseCodeHistoryItemArrayList
            = new ArrayList<>();

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

        if (quickResponseCodeHistoryItemArrayList.get(currentPosition).getQrCodeType() == 1)
            holder.scannedStatus.setText("Некорректный код");

        if (quickResponseCodeHistoryItemArrayList.get(currentPosition).getQrCodeType() == 2)
            holder.scannedStatus.setText("Некорректная ссылка");

        if (quickResponseCodeHistoryItemArrayList.get(currentPosition).getQrCodeType() == 3) {
            if (quickResponseCodeHistoryItemArrayList.get(currentPosition).getStatus().equals("Действителен"))
                holder.scannedStatus.setText("Сертификат действителен");
            else
                holder.scannedStatus.setText("Сертификат недействителен");
            if (quickResponseCodeHistoryItemArrayList.get(currentPosition).getImgStatus().equals("YELLOW"))
                holder.scannedStatus.setText("Повторное использование");
        }

        holder.scannedTime.setText(quickResponseCodeHistoryItemArrayList.get(currentPosition).getTime().getHour()
                + ":" + quickResponseCodeHistoryItemArrayList.get(currentPosition).getTime().getMinute());


    }

    @Override
    public int getItemCount() {
        return quickResponseCodeHistoryItemArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView scannedStatus, scannedTime;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            scannedStatus = itemView.findViewById(R.id.scannedStatusTxt);
            scannedTime = itemView.findViewById(R.id.scannedTimeTxt);

            scannedStatus.setSelected(true); // auto-scroll of scanned status message
        }
    }
}
