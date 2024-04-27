package edu.odu.cs.teamblack.cs411.thecouponapp.ui.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.odu.cs.teamblack.cs411.thecouponapp.R;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity.EmergencyContact;

public class EmergencyContactsAdapter extends RecyclerView.Adapter<EmergencyContactsAdapter.ViewHolder> {

    private List<EmergencyContact> emergencyContacts = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(EmergencyContact emergencyContact);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
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
        holder.contactRelationship.setText(emergencyContact.getRelationship());

        // Access theme colors
        int primaryColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.md_theme_primaryContainer);
        int onPrimaryColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.md_theme_onPrimaryContainer);

        // Set text and background based on primary status
        if (emergencyContact.isPrimary()) {
            holder.primaryIcon.setVisibility(View.VISIBLE);
            holder.cardView.setCardBackgroundColor(primaryColor); // Use the cardView reference here
            holder.contactName.setTextColor(onPrimaryColor); // Use onPrimary color for text
        } else {
            holder.primaryIcon.setVisibility(View.GONE);
            holder.cardView.setCardBackgroundColor(Color.TRANSPARENT); // Use the cardView reference here
            holder.contactName.setTextColor(Color.BLACK); // Default text color
        }

        // If you need to handle the contact click, do so here
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(emergencyContact);
            }
        });
    }

    @Override
    public int getItemCount() {
        return emergencyContacts.size();
    }

    public void setEmergencyContacts(List<EmergencyContact> contacts) {
        Collections.sort(contacts, (contact1, contact2) -> {
            if (contact1.isPrimary() && !contact2.isPrimary()) {
                return -1;
            } else if (!contact1.isPrimary() && contact2.isPrimary()) {
                return 1;
            }
            return Long.compare(contact1.getId(), contact2.getId());
        });
        this.emergencyContacts = contacts;
        notifyDataSetChanged();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView primaryIcon;
        TextView contactName;
        TextView contactRelationship;
        MaterialButton moreButton;
        MaterialCardView cardView;

        ViewHolder(View itemView) {
            super(itemView);
            primaryIcon = itemView.findViewById(R.id.primary_icon);
            contactName = itemView.findViewById(R.id.contact_name);
            contactRelationship = itemView.findViewById(R.id.contact_relationship);
            moreButton = itemView.findViewById(R.id.more_button); // Initialize it if you have it
            cardView = (MaterialCardView) itemView; // Initialize the cardView here
        }
    }
}
