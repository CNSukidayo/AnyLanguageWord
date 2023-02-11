package com.gitee.cnsukidayo.anylanguageword.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gitee.cnsukidayo.anylanguageword.R;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.listener.RecycleViewItemOnClickListener;

public class CreditAddToPlaneListAdapter extends RecyclerView.Adapter<CreditAddToPlaneListAdapter.RecyclerViewHolder> {

    private Context context;
    private RecycleViewItemOnClickListener itemOnClickListener;

    public CreditAddToPlaneListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.credit_fragment_add_to_plane_item, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.divideTextView.setText(context.getResources().getString(R.string.divide) + position);
        holder.position = position;
    }

    @Override
    public int getItemViewType(int position) {
        // 解决View复用错乱的问题
        return position;
    }

    @Override
    public int getItemCount() {
        return 30;
    }

    public void setItemOnClickListener(RecycleViewItemOnClickListener itemOnClickListener) {
        this.itemOnClickListener = itemOnClickListener;
    }


    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private View itemView;
        private TextView divideTextView;
        private ImageButton addToPlane;
        private RelativeLayout relativeLayout;
        private boolean isSelect = false;
        private int position;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.divideTextView = itemView.findViewById(R.id.credit_fragment_divide_textview);
            this.addToPlane = itemView.findViewById(R.id.add_to_plane_image_button);
            this.relativeLayout = itemView.findViewById(R.id.credit_fragment_add_to_plane_item_layout);
            this.relativeLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            isSelect = !isSelect;
            if (isSelect) {
                this.addToPlane.setImageDrawable(itemView.getResources().getDrawable(R.drawable.add_to_plane, null));
            } else {
                this.addToPlane.setImageDrawable(null);
            }
            itemOnClickListener.recycleViewOnClick(position);
        }
    }


}
