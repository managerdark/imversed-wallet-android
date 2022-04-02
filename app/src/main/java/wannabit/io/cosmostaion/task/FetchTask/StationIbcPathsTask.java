package wannabit.io.cosmostaion.task.FetchTask;

import static wannabit.io.cosmostaion.base.BaseConstant.ERROR_CODE_NETWORK;
import static wannabit.io.cosmostaion.base.BaseConstant.TASK_FETCH_IBC_PATHS;

import retrofit2.Response;
import wannabit.io.cosmostaion.base.BaseApplication;
import wannabit.io.cosmostaion.base.BaseChain;
import wannabit.io.cosmostaion.network.ApiClient;
import wannabit.io.cosmostaion.network.res.ResIbcPaths;
import wannabit.io.cosmostaion.task.CommonTask;
import wannabit.io.cosmostaion.task.TaskListener;
import wannabit.io.cosmostaion.task.TaskResult;
import wannabit.io.cosmostaion.utils.WLog;

public class StationIbcPathsTask extends CommonTask {

    private BaseChain mBaseChain;
    private String mChainId;

    public StationIbcPathsTask(BaseApplication app, TaskListener listener, BaseChain baseChain, String chainId) {
        super(app, listener);
        this.mBaseChain = baseChain;
        this.mChainId = chainId;
        this.result.taskType = TASK_FETCH_IBC_PATHS;
    }

    @Override
    protected TaskResult doInBackground(String... strings) {
        try {
            Response<ResIbcPaths> response;
            if (mBaseChain.isTestNet()) {
                response = ApiClient.getStationTest(context).getIbcPaths(mChainId).execute();
            } else {
                response = ApiClient.getStation(context).getIbcPaths(mChainId).execute();
            }
            if (!response.isSuccessful()) {
                result.isSuccess = false;
                result.errorCode = ERROR_CODE_NETWORK;
                return result;
            }

            if (response.body() != null && response.body().sendable != null) {
                context.getBaseDao().mIbcPaths = response.body().sendable;
            }
        } catch (Exception e) {
            WLog.w("StationIbcPathsTask Error " + e.getMessage());
        }
        return result;
    }
}
