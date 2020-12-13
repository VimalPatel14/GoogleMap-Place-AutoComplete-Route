package com.vimal.google.Map.AutoComplete.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vimal.google.Map.AutoComplete.model.FavouritePlaceItem;

import com.vimal.google.Map.R;

import java.util.ArrayList;

public class SavedPlacesAdapter extends BaseAdapter {

    Context context;
    ArrayList<FavouritePlaceItem> favouritePlaceItems;

    public SavedPlacesAdapter(Context context, ArrayList<FavouritePlaceItem> favouritePlaceItems) {
        this.context = context;
        this.favouritePlaceItems = favouritePlaceItems;
    }

    @Override
    public int getCount() {
        return favouritePlaceItems.size();
    }

    @Override
    public FavouritePlaceItem getItem(int position) {
        return favouritePlaceItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        PlaceViewHolder viewholder;

        try {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.adapter_google_places_autocomplete, parent, false);
                viewholder = new PlaceViewHolder(convertView);
                convertView.setTag(viewholder);
            }
            viewholder = (PlaceViewHolder) convertView.getTag();
        /*viewholder.txtArea.setText(placeModels.get(position).getSecondaryTitle());
        viewholder.txtTitle.setText(placeModels.get(position).getMainTitle());*/

            try {
                viewholder.txtArea.setText(favouritePlaceItems.get(position).getMainAddress());
                viewholder.txtTitle.setText(favouritePlaceItems.get(position).getPlaceDisplayName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        viewholder.txtArea.setText(placeModels.get(position).getSecondaryTitle());
        return convertView;
    }

    private class PlaceViewHolder {
        TextView txtTitle;
        TextView txtArea;

        public PlaceViewHolder(View view) {
            txtArea = view.findViewById(R.id.txtarea);
            txtTitle = view.findViewById(R.id.txtAddress);
        }
    }
}
