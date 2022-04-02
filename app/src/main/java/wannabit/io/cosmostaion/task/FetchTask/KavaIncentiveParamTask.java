package wannabit.io.cosmostaion.task.FetchTask;

import retrofit2.Response;
import wannabit.io.cosmostaion.base.BaseApplication;
import wannabit.io.cosmostaion.base.BaseChain;
import wannabit.io.cosmostaion.base.BaseConstant;
import wannabit.io.cosmostaion.network.ApiClient;
import wannabit.io.cosmostaion.network.res.ResKavaIncentiveParam;
import wannabit.io.cosmostaion.task.CommonTask;
import wannabit.io.cosmostaion.task.TaskListener;
import wannabit.io.cosmostaion.task.TaskResult;
import wannabit.io.cosmostaion.utils.WLog;

public class KavaIncentiveParamTask extends CommonTask {

    private BaseChain mChain;

    public KavaIncentiveParamTask(BaseApplication app, TaskListener listener, BaseChain chain) {
        super(app, listener);
        this.result.taskType = BaseConstant.TASK_FETCH_KAVA_INCENTIVE_PARAM;
        this.mChain = chain;
    }

    @Override
    protected TaskResult doInBackground(String... strings) {
        try {
            if (mChain.equals(BaseChain.KAVA_MAIN)) {
                Response<ResKavaIncentiveParam> response = ApiClient.getKavaChain(context).getIncentiveParam5().execute();
                if (response.isSuccessful() && response.body() != null && response.body().result != null) {
                    result.resultData = response.body().result;
                    result.isSuccess = true;

                } else {
                    WLog.w("KavaIncentiveParamTask : NOk");
                }

            }

        } catch (Exception e) {
            WLog.w("KavaIncentiveParamTask Error " + e.getMessage());
        }
        return result;
    }
}
