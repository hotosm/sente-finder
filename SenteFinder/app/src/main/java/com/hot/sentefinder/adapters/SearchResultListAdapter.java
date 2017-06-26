package com.hot.sentefinder.adapters;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hot.sentefinder.R;
import com.hot.sentefinder.viewmodels.FinancialServiceProviderViewModel;

import java.util.List;

public class SearchResultListAdapter extends ArrayAdapter<FinancialServiceProviderViewModel> {
    private final int layout;
    private final int name;
    private Context context;
    private List<FinancialServiceProviderViewModel> fsps;

    public SearchResultListAdapter(Context context, @LayoutRes int resource,
                                   @IdRes int textViewResourceId, List<FinancialServiceProviderViewModel> objects) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        this.fsps = objects;
        this.layout = resource;
        this.name = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        TextView item;

        //data from your adapter
        FinancialServiceProviderViewModel serviceProvider = getItem(position);
        viewHolder = new ViewHolder(convertView);
        convertView.setTag(viewHolder);
        //
        viewHolder = (ViewHolder) convertView.getTag();
        item = viewHolder.getItem();
        item.setText(serviceProvider != null ? serviceProvider.getName() : "");

        return convertView;
    }

    private class ViewHolder {
        private View mRow;
        private TextView item = null;

        public ViewHolder(View row) {
            mRow = row;
        }

        public TextView getItem() {
            if (null == item) {
                item = (TextView) mRow.findViewById(R.id.item_text);
            }
            return item;
        }
    }
}
