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
import do_an.tkll.an_iot_app.Task;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private ArrayList<Task> taskList;
    private Context context;

    public TaskAdapter(Context context, ArrayList<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout cho từng item trong RecyclerView
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        // Lấy đối tượng Task tại vị trí hiện tại
        Task task = taskList.get(position);

        // Thiết lập mô tả công việc
        holder.taskDescription.setText(task.getDescription());

        // Xử lý sự kiện nhấn vào biểu tượng xóa
        holder.deleteIcon.setOnClickListener(v -> {
            taskList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, taskList.size());
        });
    }

    @Override
    public int getItemCount() {
        // Trả về số lượng công việc trong danh sách
        return taskList.size();
    }

    // Lớp ViewHolder cho từng item trong RecyclerView
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskDescription;
        ImageView deleteIcon;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskDescription = itemView.findViewById(R.id.taskDescription);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
        }
    }
}
