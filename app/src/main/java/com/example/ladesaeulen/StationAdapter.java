package com.example.ladesaeulen;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.ViewHolder> implements Filterable {
     ArrayList<ChargingStation> stations;
    ArrayList<ChargingStation> stationsFiltered;
    Context context;
    Dialog myDialog;
    static ArrayList<ChargingStation>x = new ArrayList<>();


    public StationAdapter(ArrayList<ChargingStation> stations,SearchActivity searchActivity) {
        this.stations=stations;
        this.context=searchActivity;
        this.stations=stations;
        this.stationsFiltered=stations;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.stations_item_list,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        myDialog = new Dialog(context);
        myDialog.setContentView(R.layout.station_dialog);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final ChargingStation chargindStationList = stationsFiltered.get(position);
        holder.textaddress.setText(chargindStationList.getLocation());
        holder.textstationName.setText(chargindStationList.getOperator()+" ("+chargindStationList.getDistance()+" Km)");
        holder.textpostCity.setText(chargindStationList.getPostal_code()+" "+chargindStationList.getArea());
        holder.textModeType.setText(chargindStationList.getModule_type());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView textaddressd;
                TextView textpostCityd;
                TextView textModeTyped;
                TextView textstationNamed;
                TextView connectionNumber;
                TextView availability;
                Button favorite;
                Button report;

                availability = myDialog.findViewById(R.id.availabilityd);
                connectionNumber = myDialog.findViewById(R.id.connectiond);
                textaddressd = myDialog.findViewById(R.id.strassed);
                textpostCityd = myDialog.findViewById(R.id.locationd);
                textModeTyped = myDialog.findViewById(R.id.typed);
                favorite = myDialog.findViewById(R.id.favorited);
                textstationNamed = myDialog.findViewById(R.id.stationNamed);
                report=myDialog.findViewById(R.id.reportd);

                textaddressd.setText(chargindStationList.getLocation());
                textstationNamed.setText(chargindStationList.getOperator()+" ("+chargindStationList.getDistance()+" Km)");
                textpostCityd.setText(chargindStationList.getPostal_code()+" "+chargindStationList.getArea());
                textModeTyped.setText(chargindStationList.getModule_type());
                connectionNumber.setText("Number of Connections: "+chargindStationList.getNumber());
                if (chargindStationList.getisDefekt())
                {
                    Toast.makeText(context,"This station is Defekt",Toast.LENGTH_SHORT).show();
                    report.setText("resolved");
                    availability.setText("Not availabel");
                    myDialog.show();

                }
                else
                {
                    availability.setText("availabel");
                    myDialog.show();
                }

                favorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        x.add(chargindStationList);
                        StationManager.setFavorit_station_storage(x);
                        Toast.makeText(context,"added to favorite",Toast.LENGTH_SHORT).show();
                    }
                });

                report.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (report.getText()=="resolved"){

                            chargindStationList.setisDefekt(false);
                            report.setText("Report");
                            Toast.makeText(context,"The problem has been resolved",Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            chargindStationList.setisDefekt(true);
                            report.setText("resolved");
                            Toast.makeText(context,"Report has been made",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return stationsFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String Key = constraint.toString();
                if (Key.isEmpty())
                {
                    stationsFiltered=stations;
                }
                else{
                    ArrayList<ChargingStation> IstFiltered = new ArrayList<ChargingStation>();
                    for (ChargingStation row:stations)
                    {
                        if (row.getOperator().toLowerCase().contains(Key.toLowerCase())||row.getPostal_code().contains(Key))
                        {
                            IstFiltered.add(row);
                        }

                    }
                    stationsFiltered=IstFiltered;

                }
                FilterResults filterResults = new FilterResults();
                filterResults.values= stationsFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                stationsFiltered = (ArrayList<ChargingStation>) results.values;
                notifyDataSetChanged();

            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView textstationName;
        TextView textaddress;
        TextView textpostCity;
        TextView textModeType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textstationName = itemView.findViewById(R.id.stationName);
            textaddress = itemView.findViewById(R.id.address);
            textpostCity = itemView.findViewById(R.id.postCity);
            textModeType = itemView.findViewById(R.id.ModeType);


        }



    }
}
