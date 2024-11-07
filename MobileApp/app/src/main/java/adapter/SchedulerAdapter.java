package adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

import do_an.tkll.an_iot_app.R;
import do_an.tkll.an_iot_app.Scheduler;

public class SchedulerAdapter extends RecyclerView.Adapter<SchedulerAdapter.SchedulerViewHolder> {
    private ArrayList<Scheduler> schedulerList;
    private Context context;
    public SchedulerAdapter(Context context, ArrayList<Scheduler> schedulerList) {
        this.context = context;
        this.schedulerList = schedulerList;
    }
    @NonNull
    @Override
    public SchedulerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_scheduler, parent, false);
        return new SchedulerViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull SchedulerViewHolder holder, int position) {
        Scheduler scheduler = schedulerList.get(position);
        holder.textDescription.setText(scheduler.getDescription());

        // Sự kiện chỉnh sửa
        holder.itemView.setOnClickListener(v -> {
            // Thực hiện chỉnh sửa hẹn giờ
        });

        // Sự kiện xóa
        holder.iconDelete.setOnClickListener(v -> {
            schedulerList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, schedulerList.size());
        });
    }
    @Override
    public int getItemCount() {
        return schedulerList.size();
    }

    public static class SchedulerViewHolder extends RecyclerView.ViewHolder {
        TextView textDescription;
        ImageView iconDelete;

        public SchedulerViewHolder(@NonNull View itemView) {
            super(itemView);
            textDescription = itemView.findViewById(R.id.textDescription);
            iconDelete = itemView.findViewById(R.id.iconDelete);
        }
    }
}
