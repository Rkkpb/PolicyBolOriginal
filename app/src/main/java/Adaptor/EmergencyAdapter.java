package Adaptor;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bol.policy.com.policyboloriginal.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class EmergencyAdapter extends RecyclerView.Adapter<EmergencyAdapter.ViewHolder> {
    private ArrayList<HashMap<String, String>> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView textViewName, textViewRelation, textViewNumber;
        ImageView edit_btn, del_btn;

        public ViewHolder(View v) {
            super(v);
            textViewName = (TextView) v.findViewById(R.id.em_name);
            textViewRelation = (TextView) v.findViewById(R.id.em_relation);
            textViewNumber = (TextView) v.findViewById(R.id.em_number);
            edit_btn = (ImageView) v.findViewById(R.id.edit);
            edit_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    
                }
            });
            del_btn=(ImageView)v.findViewById(R.id.delete);
            edit_btn=(ImageView)v.findViewById(R.id.edit);
            edit_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(edit_btn.getContext(), "You press edit button", Toast.LENGTH_SHORT).show();
                }
            });
            del_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(edit_btn.getContext(), "You press delete button", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public EmergencyAdapter(ArrayList<HashMap<String, String>> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public EmergencyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cards_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.textViewName.setText("" + mDataset.get(i).get("contactperson"));
        viewHolder.textViewNumber.setText("" + mDataset.get(i).get("contactmob"));
        viewHolder.textViewRelation.setText("" + mDataset.get(i).get("relation"));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}