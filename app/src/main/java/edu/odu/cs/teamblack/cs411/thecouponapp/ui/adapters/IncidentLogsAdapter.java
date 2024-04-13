package edu.odu.cs.teamblack.cs411.thecouponapp.ui.adapters;

import androidx.core.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Locale;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

import edu.odu.cs.teamblack.cs411.thecouponapp.R;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity.IncidentLog;

public class IncidentLogsAdapter extends RecyclerView.Adapter<IncidentLogsAdapter.ViewHolder> {

    private List<IncidentLog> incidentLogs = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(IncidentLog incidentLog);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.incident_logs_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        IncidentLog incidentLog = incidentLogs.get(position);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        holder.timestampTextView.setText(sdf.format(incidentLog.getIncidentDate()));
        holder.timestampTextView.setText(incidentLog.getFormattedTimestamp());
        holder.durationTextView.setText(incidentLog.getFormattedDuration());

        if (incidentLog.isCreatedByUser())
            holder.userChip.setText("User");
        else
            holder.userChip.setText("System");
        holder.severityChip.setText(incidentLog.getSeverity());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(incidentLog);
            }
        });

        switch (incidentLog.getSeverity()) {
            case "Low":
                holder.severityChip.setText("Low");
                holder.severityChip.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.severity_low_text));
                holder.severityChip.setChipBackgroundColor(ContextCompat.getColorStateList(holder.itemView.getContext(), R.color.severity_low_background));
                holder.severityChip.setChipStrokeColor(ContextCompat.getColorStateList(holder.itemView.getContext(), R.color.severity_low_outline));
                break;
            case "Medium":
                holder.severityChip.setText("Medium");
                holder.severityChip.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.severity_medium_text));
                holder.severityChip.setChipBackgroundColor(ContextCompat.getColorStateList(holder.itemView.getContext(), R.color.severity_medium_background));
                holder.severityChip.setChipStrokeColor(ContextCompat.getColorStateList(holder.itemView.getContext(), R.color.severity_medium_outline));
                break;
            case "High":
                holder.severityChip.setText("High");
                holder.severityChip.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.severity_high_text));
                holder.severityChip.setChipBackgroundColor(ContextCompat.getColorStateList(holder.itemView.getContext(), R.color.severity_high_background));
                holder.severityChip.setChipStrokeColor(ContextCompat.getColorStateList(holder.itemView.getContext(), R.color.severity_high_outline));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return incidentLogs.size();
    }

    public void setIncidentLogs(List<IncidentLog> incidentLogs) {
        this.incidentLogs = incidentLogs;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView timestampTextView;
        TextView durationTextView;
        Chip userChip;
        Chip severityChip;

        ViewHolder(View itemView) {
            super(itemView);
            timestampTextView = itemView.findViewById(R.id.incident_log_timestamp);
            durationTextView = itemView.findViewById(R.id.incident_log_duration);
            userChip = itemView.findViewById(R.id.chip_user);
            severityChip = itemView.findViewById(R.id.chip_severity);
        }
    }
}