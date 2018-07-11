package Adaptor;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import bol.policy.com.policyboloriginal.PolicyViewer;
import bol.policy.com.policyboloriginal.R;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<HashMap<String, String>> mDataset;
    Context mContext;

    public void setFilter(ArrayList<HashMap<String, String>> filter) {
        //we have used hashmap,  find how to use it here
        mDataset = new ArrayList<HashMap<String, String>>();
        mDataset.addAll(filter);
        notifyDataSetChanged();
    }

   /* public void filter(String charText, ArrayList<HashMap<String, String>> myDataSet) {
        charText = charText.toLowerCase(Locale.getDefault());
        System.out.println("===searchview===" + charText + "===" + myDataSet.size());
        arraylist = new ArrayList<>();
        arraylist.addAll(myDataSet);
        mDataset.clear();
        if (charText.length() == 0 || TextUtils.isEmpty(charText)) {
            mDataset.addAll(myDataSet);
            System.out.println("===searchview if===" + mDataset.size());
        } else {
            System.out.println("===searchview else===" + arraylist.size());
            for (HashMap<String, String> wp : arraylist) {
                System.out.println("===searchview hashmap===" + wp);
                if (wp.get("contactperson").toLowerCase(Locale.getDefault()).contains(charText)) {
                    mDataset.add(wp);
                }
                if (wp.get("contactmob").toLowerCase(Locale.getDefault()).contains(charText)) {
                    mDataset.add(wp);
                }
                if (wp.get("relation").toLowerCase(Locale.getDefault()).contains(charText)) {
                    mDataset.add(wp);
                }
                if (wp.get("em_id").toLowerCase(Locale.getDefault()).contains(charText)) {
                    mDataset.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }*/

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView pol_reg_no, pol_v_make, pol_hol_name;
        public ImageView pdfview;

        public ViewHolder(View v) {
            super(v);
            mContext = v.getContext();
            pol_reg_no = (TextView) v.findViewById(R.id.pol_reg_no);
            pol_v_make = (TextView) v.findViewById(R.id.pol_v_make);
            pol_hol_name = (TextView) v.findViewById(R.id.pol_hol_name);
            pdfview = (ImageView) v.findViewById(R.id.pdfview);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(ArrayList<HashMap<String, String>> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        viewHolder.pol_hol_name.setText("" + mDataset.get(i).get("contactperson"));
        viewHolder.pol_reg_no.setText("" + mDataset.get(i).get("contactmob"));
        viewHolder.pol_v_make.setText("" + mDataset.get(i).get("relation"));
        viewHolder.pdfview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "" + mDataset.get(i).get("contactperson"), Toast.LENGTH_SHORT).show();
                System.out.println("===pdfviewer===" + mDataset.get(i).get("contactperson"));
                Intent intent = new Intent(mContext, PolicyViewer.class);
                mContext.startActivity(intent);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}