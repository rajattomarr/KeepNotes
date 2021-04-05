package com.example.android.keepnotes;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static com.example.android.keepnotes.MainActivity.getImage;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private ArrayList<DataModel> mArrayList;
    Context context;
    OnItemClickListener onItemClickListener;
    DataAdapterListener listener;




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rvscreen, parent, false));

    }



    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final DataModel dm = mArrayList.get(position);
        holder.name.setText(mArrayList.get(position).getTitlename());
        holder.contact.setText(mArrayList.get(position).getNotecontact());

        Bitmap bitmap = getImage(dm.getImg(),context);
        holder.imageView.setImageBitmap(bitmap);

        // long press menu and then delete
        holder.name.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.e("EXCEPTION","HEY YOU PRESSED");
               Toast.makeText(context, "onlongpressed" , Toast.LENGTH_SHORT).show();
                PopupMenu popupMenu = new PopupMenu(context, holder.name);
                popupMenu.getMenuInflater().inflate(R.menu.popupmenu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(context, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        switch (item.getItemId()) {
                            //for call
                            case R.id.delete:
                                onItemClickListener.onClick(position, dm.getTitlename(), dm.getNotecontact());
                                break;
                        }
                        return true;
                    }
                });
               popupMenu.show();
                return true;
            }
    });
    }


    @Override
    public int getItemCount()
    {
        return mArrayList.size();
    }

    public interface DataAdapterListener{

    }


    public void add(DataModel arrayList) {
        mArrayList.add(arrayList);
        notifyItemInserted(mArrayList.size() - 1);
    }
    public void removeAt(int position) {
        mArrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mArrayList.size());
    }

    public interface OnItemClickListener {
        void onClick(int pos, String titlename, String notenumber);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        ImageView iv2;
        TextView name, contact;
        ImageView imageView;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.titlename);
            contact = itemView.findViewById(R.id.notecontact);
           imageView = itemView.findViewById(R.id.image1);

        }

        public void setOnLongClickListener(View.OnLongClickListener onlongpressed) {
            Log.e(  "EXCEPTION","HEY YOU");

        }
    }
    public DataAdapter(Context context, OnItemClickListener onItemClickListener, DataAdapterListener listener) {
        this.context = context;
        mArrayList = new ArrayList<>();
        this.onItemClickListener = onItemClickListener;
        this.listener = listener ;
    }

    }


