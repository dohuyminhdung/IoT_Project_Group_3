package do_an.tkll.an_iot_app;

import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
public class ConditionRuleViewModel extends ViewModel  {
    private final ArrayList<ConditionRule> ruleList = new ArrayList<>();
    public ArrayList<ConditionRule> getRuleList() {
        return ruleList;
    }

    public void addRule(ConditionRule rule) {
        ruleList.add(rule);
    }

    public void removeRule(ConditionRule rule) {
        ruleList.remove(rule);
    }

    public void removeRuleAt(int position) {
        if (position >= 0 && position < ruleList.size()) {
            ruleList.remove(position);
        }
    }
}
