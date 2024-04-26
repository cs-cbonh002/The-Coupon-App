package edu.odu.cs.teamblack.cs411.thecouponapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import com.google.android.material.button.MaterialButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.odu.cs.teamblack.cs411.thecouponapp.R;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity.EmergencyContact;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity.IncidentLog;

public class EmergencyContactsAdapter extends RecyclerView.Adapter<EmergencyContactsAdapter.ViewHolder> {

    private List<EmergencyContact> emergencyContacts = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(EmergencyContact emergencyContact);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setEmergencyContacts(List<EmergencyContact> contacts) {
        this.emergencyContacts = contacts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.emergency_contacts_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EmergencyContact emergencyContact = emergencyContacts.get(position);
        String contact = emergencyContact.getFirstName() + " " + emergencyContact.getLastName();

        holder.contactName.setText(contact);
        holder.contactRelationship.setText(emergencyContact.relationship);

        if (emergencyContact.isPrimary) {
            holder.primaryIcon.setVisibility(View.VISIBLE);
        } else {
            holder.primaryIcon.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener!= null) {
                listener.onItemClick(emergencyContact);
            }
        });
    }

    @Override
    public int getItemCount() {
        return emergencyContacts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView primaryIcon;
        TextView contactName;
        TextView contactRelationship;
        MaterialButton moreButton;

        ViewHolder(View itemView) {
            super(itemView);
            primaryIcon = itemView.findViewById(R.id.primary_icon);
            contactName = itemView.findViewById(R.id.contact_name);
            contactRelationship = itemView.findViewById(R.id.contact_relationship);
            moreButton = itemView.findViewById(R.id.more_button);
        }
    }
}
