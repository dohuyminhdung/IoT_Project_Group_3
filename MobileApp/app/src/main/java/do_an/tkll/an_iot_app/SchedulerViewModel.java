package do_an.tkll.an_iot_app;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class SchedulerViewModel extends ViewModel {
    private final ArrayList<Scheduler> schedulerTasks = new ArrayList<>();
//    private final MutableLiveData<ArrayList<Scheduler>> schedulerTasks = new MutableLiveData<>(new ArrayList<>());

    public ArrayList<Scheduler> getSchedulerTasks() {
        return schedulerTasks;
    }
//    public LiveData<ArrayList<Scheduler>> getSchedulerTasks() {
//        return schedulerTasks;
//    }

    public void addSchedulerTask(Scheduler task) {
        if (task != null) {
            schedulerTasks.add(task);
        }
    }
//    public void addSchedulerTask(Scheduler task) {
//        if (task != null) {
//            ArrayList<Scheduler> currentTasks = schedulerTasks.getValue();
//            currentTasks.add(task);
//            schedulerTasks.setValue(currentTasks);  // Thông báo cập nhật
//        }
//    }

    public void removeSchedulerTask(Scheduler task) {
        if (task != null) {
            schedulerTasks.remove(task);
        }
    }
//    public void removeSchedulerTask(Scheduler task) {
//        if (task != null) {
//            ArrayList<Scheduler> currentTasks = schedulerTasks.getValue();
//            currentTasks.remove(task);
//            schedulerTasks.setValue(currentTasks);  // Thông báo cập nhật
//        }
//    }

    public void removeTaskAt(int position) {
        if (position >= 0 && position < schedulerTasks.size()) {
            schedulerTasks.remove(position);
        }
    }
}

