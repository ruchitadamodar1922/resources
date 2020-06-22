package com.example.resource;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

public class recycleradapter extends RecyclerView.Adapter<recycleradapter.myViewHolder>{

    public recycleradapter(ArrayList<String> filenames) {
        this.filenames = filenames;
    }

    private ArrayList<String> filenames;

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.listview,parent,false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        String title=filenames.get(position);
        holder.uploadedfilename.setText(title);

    }

    @Override
    public int getItemCount() {
        return filenames.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder{

        ImageView down,delete;
        TextView uploadedfilename;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            down=(ImageView)itemView.findViewById(R.id.downloadimg);
            delete=(ImageView)itemView.findViewById(R.id.deleteimg);
            uploadedfilename=(TextView)itemView.findViewById(R.id.uploadedfilename);

        }
    }
}
