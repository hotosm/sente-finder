package com.hot.sentefinder.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hot.sentefinder.R;
import com.hot.sentefinder.services.AppManager;
import com.hot.sentefinder.viewmodels.FinancialServiceProviderViewModel;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Jamie on 4/5/2017.
 */

public class FinancialServiceProviderAdapter extends RecyclerView.Adapter<FinancialServiceProviderAdapter.ViewHolder>{
    private Context context;
    private List<FinancialServiceProviderViewModel> FSPList;

    public FinancialServiceProviderAdapter(Context context, List<FinancialServiceProviderViewModel> FSPList) {
        this.context = context;
        this.FSPList = FSPList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fsp_list_row, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FinancialServiceProviderViewModel fspViewModel = FSPList.get(position);

        holder.name.setText(fspViewModel.getName());
        holder.operator.setText(fspViewModel.getOperator());

        double distance = fspViewModel.getDistance();
        holder.distance.setText(AppManager.convertDistanceToString(distance));

        String amenityType = fspViewModel.getAmenity();
        if (amenityType.equals("bank")) {
            Picasso.with(context).load(R.drawable.ic_bank).into(holder.fspImageView);
        }

    }

    @Override
    public int getItemCount() {
        return FSPList.size();
    }

    public FinancialServiceProviderViewModel getFinancialServiceProvider(int position){
        return FSPList.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name, operator, distance;
        private ImageView fspImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            fspImageView = (ImageView) itemView.findViewById(R.id.fsp_icon);
            name = (TextView)itemView.findViewById(R.id.fsp_name);
            operator = (TextView)itemView.findViewById(R.id.fsp_operator);
            distance = (TextView)itemView.findViewById(R.id.fsp_distance_from_user);
        }
    }
}
