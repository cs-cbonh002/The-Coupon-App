package edu.odu.cs.teamblack.cs411.thecouponapp.ui.adapters;

import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import android.widget.ImageView;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;


import edu.odu.cs.teamblack.cs411.thecouponapp.R;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity.IncidentLog;

public class IncidentLogsAdapter extends RecyclerView.Adapter<IncidentLogsAdapter.IncidentLogViewHolder> {

    private List<IncidentLog> incidentLogs; // Initialize with empty list or from constructor


    public void setIncidentLogs(List<IncidentLog> incidentLogs) {
        this.incidentLogs = incidentLogs;
        notifyDataSetChanged(); // This tells the RecyclerView to refresh the list
    }

    @NonNull
    @Override
    public IncidentLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.incident_logs_item, parent, false);
        return new IncidentLogViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull IncidentLogViewHolder holder, int position) {
        IncidentLog currentLog = incidentLogs.get(position);

        holder.textViewTimestamp.setText(currentLog.getFormattedTimestamp());
        holder.textViewDuration.setText(currentLog.getFormattedDuration());
        holder.chipUser.setText(currentLog.isCreatedByUser() ? "User" : "System");
        holder.chipSeverity.setText(currentLog.getSeverity());
        // ... other binding as necessary
    }

    public interface OnItemClickListener {
        void onItemClick(IncidentLog incidentLog);
    }

    private OnItemClickListener listener;

    // Constructor
    public IncidentLogsAdapter(List<IncidentLog> incidentLogs, OnItemClickListener listener) {
        this.incidentLogs = incidentLogs;
        this.listener = listener;
    }


    @Override
    public int getItemCount() {
        return incidentLogs.size();
    }

    static class IncidentLogViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTimestamp;
        TextView textViewDuration;
        Chip chipUser;
        Chip chipSeverity;
        ImageView imageViewChevron;

        public IncidentLogViewHolder(View itemView) {
            super(itemView);
            textViewTimestamp = itemView.findViewById(R.id.incident_log_timestamp);
            textViewDuration = itemView.findViewById(R.id.incident_log_duration);
            chipUser = itemView.findViewById(R.id.chip_user);
            chipSeverity = itemView.findViewById(R.id.chip_severity);
            imageViewChevron = itemView.findViewById(R.id.incident_log_chevron);

            // Optionally, set the chevron as the click target or the whole card
            itemView.setOnClickListener(v -> {
                // TODO: Handle the item click here
            });
        }
    }
}

