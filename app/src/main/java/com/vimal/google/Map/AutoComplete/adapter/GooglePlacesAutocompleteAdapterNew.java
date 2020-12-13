package com.vimal.google.Map.AutoComplete.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.vimal.google.Map.AutoComplete.model.placeModel;

import com.vimal.google.Map.R;

import java.util.List;

public class GooglePlacesAutocompleteAdapterNew extends BaseAdapter {


    private final Context applicationContext;
    private final List<placeModel> placeModels;

    public GooglePlacesAutocompleteAdapterNew(Context applicationContext, List<placeModel> placeModels) {
        this.applicationContext = applicationContext;
        this.placeModels = placeModels;
    }

    @Override
    public int getCount() {
        return placeModels.size();
    }

    @Override
    public placeModel getItem(int i) {
        return placeModels.get(i);
    }

    @NonNull
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        PlaceViewHolder viewholder;


        try {
            if (view == null) {
                view = LayoutInflater.from(applicationContext).inflate(R.layout.adapter_google_places_autocomplete, parent, false);
                viewholder = new PlaceViewHolder(view);
                view.setTag(viewholder);
            }
            viewholder = (PlaceViewHolder) view.getTag();

            try {
                if (placeModels.size() > position) {
                    viewholder.txtArea.setText(placeModels.get(position).getSecondaryTitle());
                    viewholder.txtTitle.setText(placeModels.get(position).getMainTitle());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        viewholder.txtArea.setText(placeModels.get(position).getSecondaryTitle());
        return view;
    }


    @Override
    public long getItemId(int i) {
        return i;
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
